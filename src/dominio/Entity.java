package dominio;

public class Entity {
    private double area;
    private double aspectRatio;
    private double aspectRatioReal;
    private String classification;
    private String image;
    private String objectType;

    
    public Entity(double area, double aspectRatio, double aspectRatioReal, String image, String classification, 
    		String objectType) {
        this.area = area;
        this.aspectRatio = aspectRatio;
        this.aspectRatioReal = aspectRatioReal;
        this.image = image;
        this.classification = classification;
        this.objectType = objectType;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }
    
    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    
    public double getAspectRatioReal() {
        return aspectRatioReal;
    }

    public void setAspectRatioReal(double aspectRatioReal) {
        this.aspectRatioReal = aspectRatioReal;
    }
 
    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public String getObjectType() {
        return objectType;
    }
    
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
