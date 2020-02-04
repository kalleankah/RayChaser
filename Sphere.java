import javax.vecmath.Vector3d;
import java.util.Vector;

//The Sphere is an analytic sphere, it is not made from primitive objects.

public class Sphere extends Object3D {
  Vector3d center;
  double radius;

  //Constructor from center, radius, material
  Sphere(Vector3d c, double r, Material m){
    super(m);
    center = c;
    radius = r;
  }
  //Copy constructor
  Sphere(Sphere s){
    super(s.mat);
    center = new Vector3d(s.center);
    radius = s.radius;
  }

  //Checks for intersection between a ray and a sphere
  @Override
  double rayIntersection(Ray r){
    //Create a vector from sphere center to ray origin
    Vector3d co = util.sub(r.start, center);
    double b = 2.0 * r.direction.dot(co);
    double c = co.dot(co) - (radius*radius);
    double rootarg = 0.25*b*b - c; //Pre-calculate argument of square root

    //Non-tangental intersection with sphere
    if (rootarg > 0.0){
      return (-0.5 * b - Math.sqrt(rootarg))/r.length();
    }
    //If no intersection return dummy
    return -1.0;
  }
  //Unlike planes and triangles, the normal to the sphere depends on the
  //position on the sphere.
  @Override
  Vector3d CalculateNormal(Vector3d P){
    return new Vector3d((P.x-center.x)/radius, (P.y-center.y)/radius, (P.z-center.z)/radius);
  }
  //Calling CalculateNormal() without argument is wrong, this override is made
  //specifically to warn about it and prevent invisible errors.
  @Override
  Vector3d CalculateNormal(){
    System.out.println("Called CalculateNormal() without arguments and returned null. Correct arguments are: CalculateNormal(Vector3d point)");
    return null;
  }
  //Sample sphere emitter surface uniformly
  @Override
  Vector3d SampleEmitter(Vector3d rayOrigin){
    Vector3d V = new Vector3d();
    //center - normalize(center-rayOrigin)*radius
    // Vector3d sphereDirection = util.normalize(util.sub(center,rayOrigin));
    // Vector3d toSphereSurface = util.scale(sphereDirection, radius);
    // Vector3d point = util.add(rayOrigin, toSphereSurface);
    // V.add(point);
    // util.print(point);

    //Not implemented yet, send warning.
    System.out.println("Sphere.java: SampleEmitter(Vector3d rayOrigin) not implemented!");
    return V;
  }
}
