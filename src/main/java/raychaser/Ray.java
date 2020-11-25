package raychaser;

import javax.vecmath.Vector3d;

//The ray contains a start- and end point, it has a direction and a length. The
//Raylength is useful because in ray-object intersection calculations, a value t
//is returned, where t represents a distance in "RayLength"-units.

public class Ray{
  Vector3d start;
  Vector3d end;
  Vector3d direction; //Normalized direction of ray
  Vector3d surfacePoint;
  Vector3d surfaceNormal;
  Ray(Vector3d s, Vector3d e){
    start = s;
    end = e;
    direction = util.sub(e, s);
    direction.normalize();
  }
  //Calculate intersection point by traversing the intersection distance t
  void calculateSurfacePoint(double t){
    //TODO the efficiency could be improved
    surfacePoint = util.sub(end,start);
    surfacePoint.scale(t);
    surfacePoint = util.add(surfacePoint, start);
  }
  //Get the length of the ray
  double length(){
    return Math.sqrt((end.x-start.x)*(end.x-start.x)+(end.y-start.y)*(end.y-start.y)+(end.z-start.z)*(end.z-start.z));
  }
  //Print information about the ray
  void print(){
    System.out.println("Start vector: ( " +start.x+", "+start.y+", "+start.z+" ) \nEnd vector: ( "+end.x+", "+end.y+", "+end.z+" )");
  }
}
