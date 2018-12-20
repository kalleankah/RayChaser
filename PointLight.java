import javax.vecmath.*;

public class PointLight extends Light{
    PointLight(Vector3d pos, ColorDbl col, double lum, double b){
        super(pos,col,lum ,b);
    }
}
