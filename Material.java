import com.sun.prism.paint.Color;

class Material{
    ColorDbl color;
    Boolean isSpecular;
    Material(){
        color = new ColorDbl();
        isSpecular = false;
    }
    Material(ColorDbl C){
        color = new ColorDbl();
        color.setColor(C);
        isSpecular = false;
    }
    Material(ColorDbl C, Boolean S){
        color = new ColorDbl();
        color.setColor(C);
        isSpecular = S;
    }
}