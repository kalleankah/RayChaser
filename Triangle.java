import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Triangle extends Object3D{
   Vector3d vertex0, vertex1,vertex2;
   Vector3d edge1,edge2;
   Vector3d normal;
   Triangle(Vector3d v0,Vector3d v1,Vector3d v2, Material m){
      super(m);
      vertex0 = new Vector3d(v0);
      vertex1 = new Vector3d(v1);
      vertex2 = new Vector3d(v2);
      edge1 = Utilities.vecSub(vertex1,vertex0);
      edge2 = Utilities.vecSub(vertex2,vertex0);
      normal = Utilities.vecCross(edge1, edge2);
      normal.normalize();
   }
   Triangle(Triangle T){
      vertex0 = new Vector3d(T.vertex0);
      vertex1 = new Vector3d(T.vertex1);
      vertex2 = new Vector3d(T.vertex2);
      normal = new Vector3d(T.normal);
   }
   void print(){
      System.out.println("vertex0: ( " +vertex0.x+", "+vertex0.y+", "+vertex0.z+" )");
      System.out.println("vertex1: ( " +vertex1.x+", "+vertex1.y+", "+vertex1.z+" )");
      System.out.println("vertex2: ( " +vertex2.x+", "+vertex2.y+", "+vertex2.z+" )");
      System.out.println("edge1: ( " +edge1.x+", "+edge1.y+", "+edge1.z+" )");
      System.out.println("edge2: ( " +edge2.x+", "+edge2.y+", "+edge2.z+" )");
      System.out.println("normal: ( " +normal.x+", "+normal.y+", "+normal.z+" )");
      mat.color.print();
   }
   @Override
   double rayIntersection(Ray r){
      Vector3d T = Utilities.vecSub(r.start,vertex0);
      Vector3d Q = Utilities.vecCross(T,edge1);
      Vector3d D = Utilities.vecSub(r.end,r.start);
      Vector3d P = Utilities.vecCross(D,edge2);

      double QE1 = Utilities.vecDot(Q, edge2);
      double PE1 = Utilities.vecDot(P, edge1);
      double PT = Utilities.vecDot(P, T);
      double QD = Utilities.vecDot(Q, D);
      double t = QE1/PE1;

      double u = PT/PE1;
      double v = QD/PE1;

      if(u < -0.00000000001 || v < -0.00000000001|| u+v > 1.00000000001){
         t = Double.POSITIVE_INFINITY;
      }
      return t;
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
      Vector<Vector3d> V = new Vector<Vector3d>();
      Random R = new Random();
      for(int i = 0; i < SAMPLES; i++){
         Vector3d temp = new Vector3d(vertex0);
         temp = Utilities.vecAdd(vertex0, Utilities.vecScale(edge1, R.nextDouble()));
         temp = Utilities.vecAdd(temp, Utilities.vecScale(Utilities.vecSub(vertex2, temp), R.nextDouble()));
         V.add(temp);
      }
      return V;
   }
}
