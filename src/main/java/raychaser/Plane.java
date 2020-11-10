package raychaser;

import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;

public class Plane extends Object3D{
  Vector3d vertex0, vertex1, vertex2, vertex3, edge1, edge2, normal;
  /*  v0----edge2----v3
  |               |
  edge1   Plane     |
  |               |
  v1----edge2----v2   */
  //Constructor from 4 corners
  Plane(Vector3d v0, Vector3d v1, Vector3d v2, Vector3d v3, Material m){
    super(m);
    vertex0 = v0;
    vertex1 = v1;
    vertex2 = v2;
    vertex3 = v3;
    edge1 = util.sub(vertex1,vertex0);
    edge2 = util.sub(vertex2,vertex1);
    normal = util.normalize(util.cross(edge1, edge2));
  }
  //Constructor from 3 corners
  Plane(Vector3d v0, Vector3d v1, Vector3d v2, Material m){
    super(m);
    vertex0 = v0;
    vertex1 = v1;
    vertex2 = v2;
    edge1 = util.sub(vertex1,vertex0);
    edge2 = util.sub(vertex2,vertex1);
    vertex3 = util.add(v0, edge2);
    normal = util.normalize(util.cross(edge1, edge2));
  }

  //Intersection between ray and plane
  @Override
  double rayIntersection(Ray r){
    // Calculate the distance to the intersection point on the infinite plane
    Vector3d dR = util.sub(r.end, r.start);
    Vector3d dP = util.sub(vertex0, r.start);
    double t = util.dot(dP, normal)/util.dot(dR, normal);

    // M = the intersection point on the surface of the infinite plane
    Vector3d M = util.add(r.start, util.scale(dR,t));

    // Check if the point M is within the limited plane
    Vector3d dMV0 = util.sub(M,vertex0);
    double u = util.dot(dMV0, edge1);
    double v = util.dot(dMV0, edge2);

    // Margins 0.00001 prevent rays from going between adjacent planes
    if(u >= -0.001 && u <= util.dot(edge1,edge1) + 0.001
    && v >= -0.001 && v <= util.dot(edge2,edge2) + 0.001){
      return t;
    }
    return Double.POSITIVE_INFINITY;
  }

  //A flat plane has the same normal everywhere, no calculation needed
  @Override
  Vector3d CalculateNormal(Vector3d P){
    return normal;
  }
  @Override
  Vector3d CalculateNormal(){
    return normal;
  }

  //Sample the plane uniformly for shadow rays to be cast
  @Override
  Vector3d SampleEmitter(Vector3d rayOrigin){
    Random R = new Random();
    Vector3d temp;
    //Distribute points equally all over the plane
    temp = util.add(vertex0, util.scale(edge1, R.nextDouble()));
    temp = util.add(temp, util.scale(edge2, R.nextDouble()));
    return temp;
  }
  //Returns edge i of the plane (used for calculating texture coordinates)
  @Override
  Vector3d getEdge(int i){
    return (i==1) ? new Vector3d(edge1) : new Vector3d(edge2);
  }
  //Similar to getEdge(). Returns vertex i
  @Override
  Vector3d getVertex(int i){
    switch(i){
      case 0:
      return new Vector3d(vertex0);
      case 1:
      return new Vector3d(vertex1);
      case 2:
      return new Vector3d(vertex2);
      case 3:
      return new Vector3d(vertex3);
    }
    return null;
  }
}
