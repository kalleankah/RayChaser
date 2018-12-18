import javax.vecmath.Vector3d;
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
    Ray raybounce(Vector3d Z){
      Vector3d ortho_z = new Vector3d(0.0, 0.0, 0.0);
      double IZ = Z.dot(direction);
      Z.scale(IZ);
      ortho_z.sub(direction, )
    }
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
