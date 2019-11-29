import java.util.Random;
import javax.vecmath.*;

public class Utilities{
   //Vector and matrix functions
   static double vecDot(Vector3d a, Vector3d b){
      return (a.x*b.x + a.y*b.y + a.z*b.z);
   }
   static Vector3d vecCross(Vector3d vec1, Vector3d vec2){
      Vector3d copy = new Vector3d();
      copy.cross(vec1, vec2);
      return copy;
   }
   static Vector3d vecScale(Vector3d vec, double scale){
      Vector3d copy = new Vector3d(vec);
      copy.x *= scale;
      copy.y *= scale;
      copy.z *= scale;
      return copy;
   }
   static Vector3d vecAdd(Vector3d vec1, Vector3d vec2){
      Vector3d copy = new Vector3d(vec1);
      copy.x += vec2.x;
      copy.y += vec2.y;
      copy.z += vec2.z;
      return copy;
   }
   static Vector3d vecAdd(Vector3d vec, double add){
      Vector3d copy = new Vector3d(vec);
      copy.x += add;
      copy.y += add;
      copy.z += add;
      return copy;
   }
   static Vector3d vecSub(Vector3d vec1, Vector3d vec2){
      Vector3d copy = new Vector3d(vec1);
      copy.x -= vec2.x;
      copy.y -= vec2.y;
      copy.z -= vec2.z;
      return copy;
   }
   static Vector3d vecSub(Vector3d vec, double sub){
      Vector3d copy = new Vector3d(vec);
      copy.x -= sub;
      copy.y -= sub;
      copy.z -= sub;
      return copy;
   }
   static double vecNorm(Vector3d vec){
      return Math.sqrt(vec.x*vec.x + vec.y*vec.y + vec.z*vec.z);
   }
   static Vector3d vecNormalize(Vector3d vec){
      Vector3d temp = new Vector3d(vec);
      temp.normalize();
      return temp;
   }
   static Matrix3d invertMat(Matrix3d mat){
      mat.invert();
      return mat;
   }
   static Vector3d mulMatVec(Matrix3d mat, Vector3d vec){
      Vector3d result = new Vector3d(
      mat.m00*vec.x + mat.m01*vec.y + mat.m02*vec.z,
      mat.m10*vec.x + mat.m11*vec.y + mat.m12*vec.z,
      mat.m20*vec.x + mat.m21*vec.y + mat.m22*vec.z );
      return result;
   }
   //Returns true if killed
   static Boolean RussianBullet(double d){ //d = risk of death; 0.0 cannot die, 1.0 will always die
      Random r = new Random();
      return r.nextDouble() < d;
   }
   static Vector3d FindOrthogonalVector(Vector3d V){
      Vector3d temp;
      if(V.x != 0.0){
         temp = new Vector3d((-V.y - V.z) / V.x, 1.0, 1.0);
      }
      else if(V.y != 0.0){
         temp = new Vector3d(1.0, (-V.x - V.z) / V.y, 1.0);
      }
      else if(V.z != 0.0){
         temp = new Vector3d(1.0, 1.0, (-V.x - V.y) /V.z);
      }
      else{
         return null;
      }
      return vecNormalize(temp);
   }
   static Boolean isNumeric(String s){
      try{Integer.parseInt(s);}
      catch(NumberFormatException nfe){return false;}
      return true;
   }
   static ColorDbl avgCol(ColorDbl c1, ColorDbl c2){
      return new ColorDbl((c1.R+c2.R)/2.0, (c1.G+c2.G)/2.0, (c1.B+c2.B)/2.0);
   }
   static void print(Matrix3d m){
      System.out.print("\nMatrix:\n" +
      m.m00 + ", " + m.m01 + ", " + m.m02 + "\n" +
      m.m10 + ", " + m.m11 + ", " + m.m12 + "\n" +
      m.m20 + ", " + m.m21 + ", " + m.m22 + "\n" );
   }
   static void print(Vector3d v){
      System.out.println("("+v.x+","+v.y+","+v.z+")");
   }
}
