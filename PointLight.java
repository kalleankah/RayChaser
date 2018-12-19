import javax.vecmath.*;

public class PointLight extends Light{
    PointLight(Vector3d cen, ColorDbl col, double lum){
        super(cen,col,lum);
    }
}
