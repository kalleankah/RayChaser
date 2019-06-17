import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
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
    Vector<Vector3d> getSampleLight(){
        return null;
    }
}
