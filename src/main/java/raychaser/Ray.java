package raychaser;

import javax.vecmath.Vector3d;

//The ray contains a start- and end point, it has a direction and a length. The
//Raylength is useful because in ray-object intersection calculations, a value t
//is returned, where t represents a distance in "RayLength"-units.

public class Ray{
  Vector3d start;
  Vector3d direction; //Normalized direction of ray
  Vector3d surfacePoint;
  Vector3d surfaceNormal;

  Ray(Vector3d s, Vector3d d){
    start = s;
    direction = d;
    direction.normalize();
  }

  //Calculate intersection point by traversing the intersection distance t
  void calculateSurfacePoint(double t){
    surfacePoint = util.add(util.scale(direction, t), start);
  }

  //Print information about the ray
  void print(){
    System.out.println("Start vector: ( " +start.x+", "+start.y+", "+start.z+" ) \nDir vector: ( "+direction.x+", "+direction.y+", "+direction.z+" )");
  }
}
