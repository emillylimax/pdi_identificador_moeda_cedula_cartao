package dominio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import dominio.ImaJ.ImaJ;
import dominio.ImaJ.Properties;
import persistencia.ImageReader;
//import visao.ImageShow;

public class Processor {
    public double diferencaAbsoluta(double a, double b) {
        if (a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    public List<Entity> process(File file) {
//        ImageShow imageShow = new ImageShow();
        
    	//Achamos melhor definir o diretório
        String Diretorio = "D:\\ADS\\2023.2\\PROCESSAMENTO DIGITAL DE IMAGENS\\PDI\\PDI_Carol_Emilly\\Imagens\\processada\\";
        
        ArrayList<Entity> list = new ArrayList<>();
        int[][][] im = ImageReader.imRead(file.getPath());

        //Transformamos a imagem em escala de cinza
        int[][] im_gray = ImaJ.rgb2gray(im);
        ImageReader.imWrite(im_gray, Diretorio + file.getName().split("\\.")[0] + "_gray.png");

        //Separamos os canais
        int[][] im_red = ImaJ.splitChannel(im, 0);
        ImageReader.imWrite(im_red, Diretorio + file.getName().split("\\.")[0] + "_red.png");
        
        int[][] im_green = ImaJ.splitChannel(im, 1);
        ImageReader.imWrite(im_green, Diretorio + file.getName().split("\\.")[0] + "_green.png");
        
        int[][] im_blue = ImaJ.splitChannel(im, 2);
        ImageReader.imWrite(im_blue, Diretorio + file.getName().split("\\.")[0] + "_blue.png");
        
        //Criamos a máscara a partir do canal vermelho
        boolean[][] im_mask = ImaJ.im2bw(im_red);
        ImageReader.imWrite(im_mask, Diretorio + file.getName().split("\\.")[0] + "_mask.png");
        
        //Fizemos a erosão
        boolean[][] im_erodida = ImaJ.bwErode(im_mask, 3);
        ImageReader.imWrite(im_erodida, Diretorio + file.getName().split("\\.")[0] + "_erodida.png");

        //Fizemos a dilatação
        boolean[][] im_dilatada = ImaJ.bwDilate(im_erodida, 7);
        ImageReader.imWrite(im_dilatada, Diretorio + file.getName().split("\\.")[0] + "_dilatada.png");

        //Fizemos o regionprops
        ArrayList<Properties> sementes = ImaJ.regionProps(im_dilatada);
        
        //Medidas conhecidas do post-it
        double alturaReferencia = 3.8;
        double larguraReferencia = 5.1;
      
        //Tratamos o ruido
        double areaMedia = 0;
        for (int i = 0; i < sementes.size(); i++) {
        	areaMedia += sementes.get(i).area;
        }
        areaMedia = (areaMedia / sementes.size()) * 0.10;
                
        for (int i = 0; i < sementes.size(); i++) {
        	if(sementes.get(i).area > areaMedia) {
                int[][][] im2 = ImaJ.imCrop(im, sementes.get(i).boundingBox[0], sementes.get(i).boundingBox[1],
                        sementes.get(i).boundingBox[2], sementes.get(i).boundingBox[3]);

                int altura = sementes.get(i).boundingBox[2] - sementes.get(i).boundingBox[0];
                int largura = sementes.get(i).boundingBox[3] - sementes.get(i).boundingBox[1];
                
                //Calculamos o Aspect Ratio
                double aspectRatio = (double) altura / largura;                    
                
    			//Aplicamos a máscara na imagem original
    			for(int x = 0; x < im2.length; x++) {
    				for(int y = 0; y < im2[0].length; y++) {
    					//Se é pixel de fundo
    					if(!sementes.get(i).image[x][y]) {
    						im2[x][y] = new int[]{0,0,0};
    					}
    				}
    			}

    			//Classificamos por tipo
                double limiar = 0.20;
                String tipoObjeto;

                if (diferencaAbsoluta(aspectRatio, 1.0) <= limiar) {
                    tipoObjeto = "circular";
                } else {
                    tipoObjeto = "retangular";
                }

                ImageReader.imWrite(im2, Diretorio + file.getName().split("\\.")[0] + "_" + i + ".png");

                //Fizemos o aspectRatioReal(diferença), que definia que quanto mais próximo de 0.03, maior a chance de ser post-it
                double aspectRatioReal = aspectRatio - (alturaReferencia / larguraReferencia);
                
                //Classificamos os objetos
                String tipoObjetoNome = "";
                if (aspectRatioReal >= 0.17 && aspectRatioReal <= 0.38 && 
                    sementes.get(i).area >= 1507.0 && sementes.get(i).area <= 2721.0) {
                    tipoObjetoNome = "moeda";
                } else if ((aspectRatioReal >= -0.28 && aspectRatioReal <= -0.174 && 
                        sementes.get(i).area >= 29104.0 && sementes.get(i).area <= 44141.0) ) {
                    tipoObjetoNome = "nota";
                } else if ((aspectRatioReal >= -0.170 && aspectRatioReal <= 0.80 && 
                        sementes.get(i).area >= 18491.0 && sementes.get(i).area <= 20981.0) ) {
                    tipoObjetoNome = "cartao";
                } else {
                    tipoObjetoNome = "outro";
                }
                
                //Filtramos para que aparecesse na tabela apenas o que fosse moeda, nota e cartão. Ou seja, tudo que for outro, não aparecerá na tabela
                if (!tipoObjetoNome.equals("outro")) {
                list.add(new Entity(sementes.get(i).area, aspectRatio, aspectRatioReal,
                		Diretorio + file.getName().split("\\.")[0] + "_" + i + ".png", tipoObjetoNome, tipoObjeto));
  
                }
        	}
        }

        return list;
    }
}