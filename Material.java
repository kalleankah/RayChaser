import com.sun.prism.paint.Color;

class Material{
    ColorDbl color;
    Boolean isSpecular;
    Boolean Emissive;
    double Brightness;
    Material(){
        color = new ColorDbl();
        isSpecular = false;
        Emissive = false;
    }
    Material(ColorDbl C){
        color = new ColorDbl();
        color.setColor(C);
        isSpecular = false;
        Emissive = false;
    }
    Material(ColorDbl C, Boolean S){
        color = new ColorDbl();
        color.setColor(C);
        isSpecular = S;
        Emissive = false;
    }
    Material(ColorDbl c, Boolean S, Boolean E, double b){
        color = new ColorDbl();
        color.setColor(c);
        isSpecular = S;
        Emissive = E;
        Brightness = b;
    }
    
}