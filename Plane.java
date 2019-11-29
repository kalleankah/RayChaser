import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;

public class Plane extends Object3D{
   Vector3d vertex0, vertex1, vertex2, vertex3, edge1, edge2, normal;
   /*      v0----edge2----v3
           |               |
         edge1   Plane     |
           |               |
           v1----edge2----v2    */
   //Constructor with 4 corners
   Plane(Vector3d v0, Vector3d v1, Vector3d v2, Vector3d v3, Material m){
      super(m);
      vertex0 = v0;
      vertex1 = v1;
      vertex2 = v2;
      vertex3 = v3;
      edge1 = Utilities.vecSub(vertex1,vertex0);
      edge2 = Utilities.vecSub(vertex2,vertex1);
      normal = Utilities.vecNormalize(Utilities.vecCross(edge1, edge2));
   }
   //Constructor with 3 corners
   Plane(Vector3d v0, Vector3d v1, Vector3d v2, Material m){
      super(m);
      vertex0 = v0;
      vertex1 = v1;
      vertex2 = v2;
      edge1 = Utilities.vecSub(vertex1,vertex0);
      edge2 = Utilities.vecSub(vertex2,vertex1);
      vertex3 = Utilities.vecAdd(v0, edge2);
      normal = Utilities.vecNormalize(Utilities.vecCross(edge1, edge2));
   }

   @Override
   double rayIntersection(Ray r){
      Vector3d dR = Utilities.vecSub(r.end, r.start);
      Vector3d dP = Utilities.vecSub(vertex0, r.start);
      double t = Utilities.vecDot(dP, normal)/Utilities.vecDot(dR, normal);
      Vector3d M = Utilities.vecAdd(r.start, Utilities.vecScale(dR,t));

      Vector3d dMV0 = Utilities.vecSub(M,vertex0);
      double u = Utilities.vecDot(dMV0, edge1);
      double v = Utilities.vecDot(dMV0, edge2);

      if(u >= 0.0 && u <= Utilities.vecDot(edge1,edge1)
      && v >= 0.0 && v <= Utilities.vecDot(edge2,edge2)){
         return t;
      }
      return Double.POSITIVE_INFINITY;
   }
   @Override
   Vector3d CalculateNormal(Vector3d P){
      return normal;
   }
   @Override
   Vector3d CalculateNormal(){
      return normal;
   }
   @Override
   Vector<Vector3d> getSampleLight(int SAMPLES, Vector3d rayOrigin){
      Vector<Vector3d> V = new Vector<>();
      Random R = new Random();
      Vector3d temp;
      for(int i = 0; i < SAMPLES; i++){
         temp = Utilities.vecAdd(vertex0, Utilities.vecScale(edge1, R.nextDouble()));
         temp = Utilities.vecAdd(temp, Utilities.vecScale(edge2, R.nextDouble()));
         V.add(temp);
      }
      return V;
   }
   @Override
   Vector3d getEdge(int i){
      return (i==1) ? new Vector3d(edge1) : new Vector3d(edge2);
   }
   @Override
   Vector3d getVertex(int i){
      switch(i){
         case 0:
         return new Vector3d(vertex0);
         case 1:
         return new Vector3d(vertex1);
         case 2:
         return new Vector3d(vertex2);
         case 3:
         return new Vector3d(vertex3);
      }
      return null;
   }
}
