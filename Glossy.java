import javafx.scene.image.Image;

public class Glossy extends Material {
double roughness;
    Glossy(ColorDbl col, double coeff){
        super(col);
        roughness = coeff;
    }
    Glossy(Image i, double coeff){
        super(i);
        roughness = coeff;
    }
    @Override
    double getRoughness(){
        return roughness;
    }
}
