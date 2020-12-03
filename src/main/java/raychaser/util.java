package raychaser;

import java.util.concurrent.ThreadLocalRandom;

import javax.vecmath.*;

//The Utilities class holds multiple useful static functions. Many of these
//functions work like operators for vectors and matrices.

public class util{
  //Cross product
  static Vector3d cross(Vector3d vec1, Vector3d vec2){
    Vector3d copy = new Vector3d();
    copy.cross(vec1, vec2);
    return copy;
  }
  // Scale vector by double
  static Vector3d scale(Vector3d vec, double scale){
    Vector3d copy = new Vector3d(vec);
    copy.x *= scale;
    copy.y *= scale;
    copy.z *= scale;
    return copy;
  }
  // Sum two vectors
  static Vector3d add(Vector3d vec1, Vector3d vec2){
    Vector3d copy = new Vector3d(vec1);
    copy.x += vec2.x;
    copy.y += vec2.y;
    copy.z += vec2.z;
    return copy;
  }
  // Sum three vectors
  static Vector3d add(Vector3d vec1, Vector3d vec2, Vector3d vec3){
    Vector3d copy = new Vector3d(
      vec1.x + vec2.x + vec3.x,
      vec1.y + vec2.y + vec3.y,
      vec1.z + vec2.z + vec3.z);
    return copy;
  }
  //Subtract a vector from a vector
  static Vector3d sub(Vector3d vec1, Vector3d vec2){
    Vector3d copy = new Vector3d(vec1);
    copy.x -= vec2.x;
    copy.y -= vec2.y;
    copy.z -= vec2.z;
    return copy;
  }
  // Normalize vector
  static Vector3d normalize(Vector3d vec){
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

  //random vector inside unit sphere using rejection sampling
  public static Vector3d random_unit_vec(double radius){
    ThreadLocalRandom R = ThreadLocalRandom.current();
    Vector3d v;
    do{
      v = new Vector3d(R.nextDouble()*2.0*radius-radius, R.nextDouble()*2.0*radius-radius, R.nextDouble()*2.0*radius-radius);
    }while(v.x*v.x+v.y*v.y+v.z*v.z >= radius);
    return v;
  }

  //Sample hemisphere uniformly using cosine weighted hemisphere sampling
  public static Vector3d sampleHemisphere(){
    ThreadLocalRandom R = ThreadLocalRandom.current();
    double u = R.nextDouble();
    double sintheta = Math.sqrt(-u*(u-2));
    double phi = R.nextDouble() * 2 * Math.PI;
    double x = sintheta * Math.cos(phi);
    double y = sintheta * Math.sin(phi);
    return new Vector3d(x, y, Math.sqrt(1-x*x-y*y));
  }

  // Algorithm to find orthogonal vector to another vector
  static Vector3d findOrthogonalVector(Vector3d V){
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
      System.out.println("ERROR: Can't find orthogonal vector to a zero vector!");
      return null;
    }

    temp.normalize();
    return temp;
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