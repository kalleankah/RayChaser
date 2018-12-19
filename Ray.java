import javax.vecmath.*;
import java.lang.Math;
public class Ray{
    Vector3d start;
    Vector3d end;
    Vector3d direction = new Vector3d(0.0, 0.0, 0.0);
    ColorDbl rayColor = new ColorDbl();
    Ray(Vector3d s, Vector3d e){
        start = s;
        end = e;
        direction.sub(e, s);
        direction.normalize();
    }
    // Takes the normal of the surface bounced on (and assigns it as Z immediately)
    void raybounce(Vector3d Z, Vector3d intersection){
        Vector3d I_o = vecSub(direction, vecScale(Z, vecDot(direction, Z)));
        Vector3d X = vecScale(I_o, 1.0/vecNorm(I_o));
        Vector3d Y = vecCross(vecScale(X,-1.0), Z);

        //Create coordinate system transformation matrices
        Matrix4d rotation_mat = new Matrix4d(
        X.x, X.y, X.z, 0.0,
        Y.x, Y.y, Y.z, 0.0,
        Z.x, Z.y, Z.z, 0.0,
        0.0, 0.0, 0.0, 1.0);
        Matrix4d translation_mat = new Matrix4d(
        0.0, 0.0, 0.0, -intersection.x,
        0.0, 0.0, 0.0, -intersection.y,
        0.0, 0.0, 0.0, -intersection.z,
        0.0, 0.0, 0.0, 1.0);
        Matrix4d world2local = new Matrix4d();
        world2local.mul(rotation_mat, translation_mat);
        Matrix4d local2world = invertMat(world2local);

        //Incoming ray in local coords
        Vector3d direction_local = mulMatVec(world2local, direction);
    }

    //Vector and matrix functions that don't modify the object ***************
    double vecDot(Vector3d a, Vector3d b){
      return (a.x*b.x + a.y*b.y + a.z*b.z);
    }
    Vector3d vecCross(Vector3d vec1, Vector3d vec2){
      Vector3d copy = new Vector3d();
      copy.cross(vec1, vec2);
      return copy;
    }
    Vector3d vecScale(Vector3d vec, double scale){
      Vector3d copy = new Vector3d(vec);
      copy.x *= scale;
      copy.y *= scale;
      copy.z *= scale;
      return copy;
    }
    Vector3d vecAdd(Vector3d vec1, Vector3d vec2){
      Vector3d copy = new Vector3d(vec1);
      copy.x += vec2.x;
      copy.y += vec2.y;
      copy.z += vec2.z;
      return copy;
    }
    Vector3d vecAdd(Vector3d vec, double add){
      Vector3d copy = new Vector3d(vec);
      copy.x += add;
      copy.y += add;
      copy.z += add;
      return copy;
    }
    Vector3d vecSub(Vector3d vec1, Vector3d vec2){
      Vector3d copy = new Vector3d(vec1);
      copy.x -= vec2.x;
      copy.y -= vec2.y;
      copy.z -= vec2.z;
      return copy;
    }
    Vector3d vecSub(Vector3d vec, double sub){
      Vector3d copy = new Vector3d(vec);
      copy.x -= sub;
      copy.y -= sub;
      copy.z -= sub;
      return copy;
    }
    double vecNorm(Vector3d vec){
      return Math.sqrt(vec.x*vec.x + vec.y*vec.y + vec.z*vec.z);
    }
    Matrix4d invertMat(Matrix4d mat){
      Matrix4d copy = new Matrix4d(mat);
      copy.invert();
      return copy;
    }
    Vector3d mulMatVec(Matrix4d mat, Vector3d vec){
      Vector3d result = new Vector3d();
      result.x = mat.m00*vec.x + mat.m01*vec.y + mat.m02*vec.z;
      result.y = mat.m10*vec.x + mat.m11*vec.y + mat.m12*vec.z;
      result.z = mat.m20*vec.x + mat.m21*vec.y + mat.m22*vec.z;
      return result;
    }//End of utility functions **********************************************

    void print(){
        System.out.println("Start vector: ( " +start.x+", "+start.y+", "+start.z+" ) \nEnd vector: ( "+end.x+", "+end.y+", "+end.z+" )");
    }
    public static void main(String[] args) {
        Vector3d v1 = new Vector3d(1.0,2.0,3.0);
        Vector3d v2 = new Vector3d(2.0,1.0,2.0);
        Ray r1 = new Ray(v1,v2);
        r1.print();
    }
}
