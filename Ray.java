import javax.vecmath.*;
import java.lang.Math;
import java.util.*;

//The ray contains a start- and end point, it has a direction and a length. The
//Raylength is useful because in ray-object intersection calculations, a value t
//is returned, where t represents a distance in "RayLength"-units. MotherNode
//means that the current ray is originating from the camera, this is important
//because the near clip is always 0.0 if the ray is not originating from camera.

public class Ray{
  Vector3d start;
  Vector3d end;
  double RayLength;
  Vector3d direction; //Normalized direction of ray
  Vector3d P_hit;
  Vector3d P_Normal;
  Boolean MotherNode;
  Ray(Vector3d s, Vector3d e, Boolean MN){
    start = s;
    end = e;
    this.MotherNode = MN;
    direction = Utilities.vecSub(e, s);
    RayLength = Utilities.vecNorm(direction);
    direction.normalize();
  }
  //Calculate intersection point by traversing the intersection distance t
  void calculatePhit(double t){
    P_hit = Utilities.vecSub(end,start);
    P_hit.scale(t);
    P_hit = Utilities.vecAdd(P_hit, start);
  }
  //Print information about the ray
  void print(){
    System.out.println("Start vector: ( " +start.x+", "+start.y+", "+start.z+" ) \nEnd vector: ( "+end.x+", "+end.y+", "+end.z+" )");
  }
}
