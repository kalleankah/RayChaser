import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;

public class Triangle extends Object3D{
  Vector3d vertex0, vertex1,vertex2;
  Vector3d edge1,edge2;
  Vector3d normal;
  //Constructor from 3 vertices and material
  Triangle(Vector3d v0,Vector3d v1,Vector3d v2, Material m){
    super(m);
    vertex0 = new Vector3d(v0);
    vertex1 = new Vector3d(v1);
    vertex2 = new Vector3d(v2);
    edge1 = util.sub(vertex1,vertex0);
    edge2 = util.sub(vertex2,vertex0);
    normal = util.cross(edge1, edge2);
    normal.normalize();
  }
  //Copy constructor
  Triangle(Triangle T){
    vertex0 = new Vector3d(T.vertex0);
    vertex1 = new Vector3d(T.vertex1);
    vertex2 = new Vector3d(T.vertex2);
    normal = new Vector3d(T.normal);
  }
  //Print vertices, edges and the normal
  void print(){
    System.out.println("vertex0: ( " +vertex0.x+", "+vertex0.y+", "+vertex0.z+" )");
    System.out.println("vertex1: ( " +vertex1.x+", "+vertex1.y+", "+vertex1.z+" )");
    System.out.println("vertex2: ( " +vertex2.x+", "+vertex2.y+", "+vertex2.z+" )");
    System.out.println("edge1: ( " +edge1.x+", "+edge1.y+", "+edge1.z+" )");
    System.out.println("edge2: ( " +edge2.x+", "+edge2.y+", "+edge2.z+" )");
    System.out.println("normal: ( " +normal.x+", "+normal.y+", "+normal.z+" )");
    mat.color.print();
  }
  //Calculate ray-triangle intersection using the Möller-Trumbore algorithm
  @Override
  double rayIntersection(Ray r){
    Vector3d T = util.sub(r.start,vertex0);
    Vector3d Q = util.cross(T,edge1);
    Vector3d D = util.sub(r.end,r.start);
    Vector3d P = util.cross(D,edge2);

    double QE1 = util.dot(Q, edge2);
    double PE1 = util.dot(P, edge1);
    double PT = util.dot(P, T);
    double QD = util.dot(Q, D);
    double t = QE1/PE1;

    double u = PT/PE1;
    double v = QD/PE1;

    //Using margins to prevent rays from going between adjacent triangles
    if(u < -0.001 || v < -0.001|| u+v > 1.001){
      t = Double.POSITIVE_INFINITY;
    }
    //Return distance to intersection, t.
    return t;
  }
  //A flat triangle has the same normal everywhere, no calculation needed
  @Override
  Vector3d CalculateNormal(Vector3d P){
    return normal;
  }
  @Override
  Vector3d CalculateNormal(){
    return normal;
  }
  //Sample the trinagle uniformly for shadow rays to be cast
  @Override
  Vector3d SampleEmitter(Vector3d rayOrigin){
    Random R = new Random();
    Vector3d temp;
    temp = util.add(vertex0, util.scale(edge1, R.nextDouble()));
    temp = util.add(temp, util.scale(util.sub(vertex2, temp), R.nextDouble()));
    return temp;
  }
}
