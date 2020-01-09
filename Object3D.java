import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;

//Object3D is parent class for all types of objects (not only 3d objects). Its
//only member is the material. Most member functions are overridden in its
//subclasses: Triangle.java, Plane.java, Sphere.java, InfinitePlane.java

public class Object3D{
  Material mat;
  //Default constructor (default material)
  public Object3D(){
    mat = new Material();
  }
  //Constructor from Material
  public Object3D(Material m){
    mat = m;
  }

  //All following functions are overridden in subclasses; the returns are dummy
  double rayIntersection(Ray r){
    return Double.POSITIVE_INFINITY;
  }
  Vector3d CalculateNormal(Vector3d P){
    return null;
  }
  Vector3d CalculateNormal(){
    return null;
  }
  Vector<Vector3d> getSampleLight(int SAMPLES, Vector3d rayOrigin){
    return null;
  }
  Vector3d getEdge(int i){
    return null;
  }
  Vector3d getVertex(int i){
    return null;
  }
}
