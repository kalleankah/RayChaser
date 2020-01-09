import java.util.Random;
import javax.vecmath.*;

//The Utilities class holds multiple useful static functions. Many of these
//functions work like operators for vectors and matrices.

public class Utilities{
  //--- Vector and matrix functions ---
  //Dot product
  static double vecDot(Vector3d a, Vector3d b){
    return (a.x*b.x + a.y*b.y + a.z*b.z);
  }
  //Cross product
  static Vector3d vecCross(Vector3d vec1, Vector3d vec2){
    Vector3d copy = new Vector3d();
    copy.cross(vec1, vec2);
    return copy;
  }
  // Scale vector by double
  static Vector3d vecScale(Vector3d vec, double scale){
    Vector3d copy = new Vector3d(vec);
    copy.x *= scale;
    copy.y *= scale;
    copy.z *= scale;
    return copy;
  }
  // Sum two vectors
  static Vector3d vecAdd(Vector3d vec1, Vector3d vec2){
    Vector3d copy = new Vector3d(vec1);
    copy.x += vec2.x;
    copy.y += vec2.y;
    copy.z += vec2.z;
    return copy;
  }
  // Add a double to each component of vector
  // static Vector3d vecAdd(Vector3d vec, double add){
  //   Vector3d copy = new Vector3d(vec);
  //   copy.x += add;
  //   copy.y += add;
  //   copy.z += add;
  //   return copy;
  // }
  //Extend vector by a double
  static Vector3d vecExtend(Vector3d vec, double extend){
    Vector3d copy = new Vector3d(vec);
    Vector3d extension = Utilities.vecScale(Utilities.vecNormalize(vec), extend);
    return Utilities.vecAdd(copy, extension);
  }
  //Subtract a vector from a vector
  static Vector3d vecSub(Vector3d vec1, Vector3d vec2){
    Vector3d copy = new Vector3d(vec1);
    copy.x -= vec2.x;
    copy.y -= vec2.y;
    copy.z -= vec2.z;
    return copy;
  }
  //Subtract a double from each component
  // static Vector3d vecSub(Vector3d vec, double sub){
  //   Vector3d copy = new Vector3d(vec);
  //   copy.x -= sub;
  //   copy.y -= sub;
  //   copy.z -= sub;
  //   return copy;
  // }
  // Calculate norm of a vector
  static double vecNorm(Vector3d vec){
    return Math.sqrt(vec.x*vec.x + vec.y*vec.y + vec.z*vec.z);
  }
  // Normalize vector
  static Vector3d vecNormalize(Vector3d vec){
    Vector3d temp = new Vector3d(vec);
    temp.normalize();
    return temp;
  }
  // Return an inverted matrix
  static Matrix3d invertMat(Matrix3d mat){
    Matrix3d copy = new Matrix3d(mat);
    copy.invert();
    return copy;
  }
  // Multiply vector by matrix
  static Vector3d mulMatVec(Matrix3d mat, Vector3d vec){
    Vector3d result = new Vector3d(
    mat.m00*vec.x + mat.m01*vec.y + mat.m02*vec.z,
    mat.m10*vec.x + mat.m11*vec.y + mat.m12*vec.z,
    mat.m20*vec.x + mat.m21*vec.y + mat.m22*vec.z );
    return result;
  }
  //Russian roulette, used to terminate rays with increasing risk based on the
  //amount of bounces (the depth).
  //d = risk of death; 0.0 cannot die, 1.0 will always die
  static Boolean RussianBullet(double d){
    Random r = new Random();
    // Returns true if ray is terminated
    return r.nextDouble() < d;
  }
  // Algorithm to find orthogonal vector to another vector
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
  // Check if string is numeric
  // static Boolean isNumeric(String s){
  //   try{Integer.parseInt(s);}
  //   catch(NumberFormatException nfe){return false;}
  //   return true;
  // }
  // Calculate average of two colors
  static ColorDbl avgCol(ColorDbl c1, ColorDbl c2){
    return new ColorDbl((c1.R+c2.R)/2.0, (c1.G+c2.G)/2.0, (c1.B+c2.B)/2.0);
  }
  //Print matrix vertices
  static void print(Matrix3d m){
    System.out.print("\nMatrix:\n" +
    m.m00 + ", " + m.m01 + ", " + m.m02 + "\n" +
    m.m10 + ", " + m.m11 + ", " + m.m12 + "\n" +
    m.m20 + ", " + m.m21 + ", " + m.m22 + "\n" );
  }
  //Print vector vertices
  static void print(Vector3d v){
    System.out.println("("+v.x+","+v.y+","+v.z+")");
  }
}
