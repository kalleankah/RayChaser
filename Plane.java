import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;

public class Plane extends Object3D{
    Triangle T1;
    Triangle T2;
    Plane(Vector3d v0, Vector3d v1, Vector3d v2, Material m){
        super(m);
        T1 = new Triangle(v0, v1, v2, m);
        T2 = new Triangle(v0, v2, Utilities.vecAdd(v0, Utilities.vecSub(v2,v1)), m);
    
    }
    @Override
    double rayIntersection(Ray r){
        double t1 = T1.rayIntersection(r);
        double t2 = T2.rayIntersection(r);
        if(t1 < t2){
            return t1;
        }
        return t2;
    }
    @Override
    Vector3d CalculateNormal(Vector3d P){
        return T1.normal;
    }
    @Override
    Vector3d CalculateNormal(){
        return T1.normal;
    }
    @Override
    Vector<Vector3d> getSampleLight(int SAMPLES){
        Vector<Vector3d> v = new Vector<>();
        v.addAll(T1.getSampleLight(SAMPLES));
        v.addAll(T2.getSampleLight(SAMPLES));
        return v;
    }
}