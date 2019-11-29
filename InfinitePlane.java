import javax.vecmath.Vector3d;

public class InfinitePlane extends Object3D {
   Vector3d normal, position;
   InfinitePlane(Vector3d pos, Vector3d N, Material m){
      super(m);
      position = pos;
      normal = N;
   }
   @Override
   double rayIntersection(Ray r){
      Vector3d dR = Utilities.vecSub(r.end, r.start);
      Vector3d dP = Utilities.vecSub(position, r.start);
      return Utilities.vecDot(dP, normal)/Utilities.vecDot(dR, normal);
   }
   @Override
   Vector3d CalculateNormal(Vector3d P){
      return normal;
   }
   @Override
   Vector3d CalculateNormal(){
      return normal;
   }
}
