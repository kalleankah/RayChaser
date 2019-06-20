import javax.vecmath.*;
import java.lang.Math;
import java.util.*;

public class Ray{
    Vector3d start;
    Vector3d end;
    public double RayLength;
    Vector3d direction = new Vector3d(0.0, 0.0, 0.0);
    Vector3d P_hit;
    Vector3d P_Normal;
    Boolean MotherNode;
    Ray(Vector3d s, Vector3d e, Boolean MN){
        start = s;
        end = e;
        this.MotherNode = MN;
        direction.sub(e, s);
        RayLength = Utilities.vecNorm(direction);
        direction.normalize();
    }
    void calculatePhit(double t){
        P_hit = new Vector3d();
        P_hit = Utilities.vecSub(end,start);
        P_hit.scale(t);
        P_hit = Utilities.vecAdd(P_hit, start);
    }

    void print(){
        System.out.println("Start vector: ( " +start.x+", "+start.y+", "+start.z+" ) \nEnd vector: ( "+end.x+", "+end.y+", "+end.z+" )");
    }
}
