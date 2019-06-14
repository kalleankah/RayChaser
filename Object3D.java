import javax.vecmath.Vector3d;
public class Object3D{
    Material mat;
    public Object3D(){
        mat = new Material();
    }
    public Object3D(Material m){
        mat = m;
    }
    double rayIntersection(Ray r){
        return Double.POSITIVE_INFINITY;
    }
    Vector3d CalculateNormal(Vector3d P){
        return null;
    }
    Vector3d CalculateNormal(){
        return null;
    }
}
