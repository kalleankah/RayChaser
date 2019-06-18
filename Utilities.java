import java.util.Random;

import javax.vecmath.*;
public class Utilities{
//Vector and matrix functions that don't modify the object ***************
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
static Matrix4d invertMat(Matrix4d mat){
  Matrix4d copy = new Matrix4d(mat);
  copy.invert();
  return copy;
}
static Vector3d mulMatVec(Matrix4d mat, Vector3d vec){
  Vector3d result = new Vector3d();
  result.x = mat.m00*vec.x + mat.m01*vec.y + mat.m02*vec.z + mat.m03;
  result.y = mat.m10*vec.x + mat.m11*vec.y + mat.m12*vec.z + mat.m13;
  result.z = mat.m20*vec.x + mat.m21*vec.y + mat.m22*vec.z + mat.m23;
  return result;
}
static Boolean RussianBullet(double d){
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
static Vector3d FindOrthogonalVector(Vector3d V1, Vector3d V2){
  Vector3d V = vecCross(V1, V2);
  return vecNormalize(V);
}
static void updateProgress(double progressPercentage) {
    final int width = 50; // progress bar width in chars

    System.out.print("\r[");
    int i = 0;
    for (; i <= (int)(progressPercentage*width); i++) {
      System.out.print(".");
    }
    for (; i < width; i++) {
      System.out.print(" ");
    }
    System.out.print("]" + (int)(progressPercentage*100) + "%");
}
//End of utility functions **********************************************
}
