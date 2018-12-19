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
static Matrix4d invertMat(Matrix4d mat){
  Matrix4d copy = new Matrix4d(mat);
  copy.invert();
  return copy;
}
static Vector3d mulMatVec(Matrix4d mat, Vector3d vec){
  Vector3d result = new Vector3d();
  result.x = mat.m00*vec.x + mat.m01*vec.y + mat.m02*vec.z;
  result.y = mat.m10*vec.x + mat.m11*vec.y + mat.m12*vec.z;
  result.z = mat.m20*vec.x + mat.m21*vec.y + mat.m22*vec.z;
  return result;
}//End of utility functions **********************************************
}
