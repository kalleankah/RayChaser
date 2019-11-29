import javax.vecmath.Vector3d;
import java.util.Vector;

public class Sphere extends Object3D {
   Vector3d center;
   double radius;

   Sphere(Vector3d c, double r, Material m){
      super(m);
      center = c;
      radius = r;
   }
   Sphere(Sphere s){
      super(s.mat);
      center = new Vector3d(s.center);
      radius = s.radius;
   }

   //Checks for intersection between a ray and a sphere
   @Override
   double rayIntersection(Ray r){
      //Create a vector with the direction of ray origin to sphere center
      Vector3d oc = Utilities.vecSub(r.start, center);
      double b = 2.0 * r.direction.dot(oc);
      double c = oc.dot(oc) - (radius*radius);
      double rootarg = 0.25*b*b - c; //Pre-calculate argument of square root

      //Non-tangental intersection with sphere
      if (rootarg > 0){
         return (-0.5 * b - Math.sqrt(rootarg))/r.RayLength;
      }
      return -1.0;
   }
   @Override
   Vector3d CalculateNormal(Vector3d P){
      return new Vector3d((P.x-center.x)/radius, (P.y-center.y)/radius, (P.z-center.z)/radius);
   }
   @Override
   Vector3d CalculateNormal(){
      System.out.println("Called CalculateNormal() without arguments and returned null. Correct arguments are: CalculateNormal(Vector3d point)");
      return null;
   }
   @Override
   Vector<Vector3d> getSampleLight(int SAMPLES, Vector3d rayOrigin){
      Vector<Vector3d> V = new Vector<>();
      //center - normalize(center-rayOrigin)*radius
      // Vector3d sphereDirection = Utilities.vecNormalize(Utilities.vecSub(center,rayOrigin));
      // Vector3d toSphereSurface = Utilities.vecScale(sphereDirection, radius);
      // Vector3d point = Utilities.vecAdd(rayOrigin, toSphereSurface);
      // V.add(point);
      // Utilities.print(point);
      System.out.println("Sphere::getSampleLight not implemented!");
      return V;
   }
}
