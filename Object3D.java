import javax.vecmath.Vector3d;
public class Object3D{
    ColorDbl color;
    public Object3D(ColorDbl c){
        color = c;
    }
    double rayIntersection(Ray r){
        return Double.POSITIVE_INFINITY;
    }
    Vector3d CalculateNormal(Vector3d P){
        return null;
    }
}
