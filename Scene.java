import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Scene{
    Vector<Triangle> triangleList= new Vector<Triangle>();
    Vector<Sphere> sphereList = new Vector<Sphere>();
    Scene(){
        ColorDbl white = new ColorDbl(1.0,1.0,1.0);
        ColorDbl red = new ColorDbl(1.0,0.0,0.0);
        ColorDbl green = new ColorDbl(0.0,1.0,0.0);
        ColorDbl blue = new ColorDbl(0.0,0.0,1.0);
        ColorDbl yellow = new ColorDbl(1.0,1.0,0.0);
        ColorDbl cyan = new ColorDbl(0.0,1.0,1.0);
        ColorDbl magenta = new ColorDbl(1.0,0.0,1.0);

        Vector3d CeilingLM = new Vector3d(-3.0,0.0,5.0);
        Vector3d CeilingLU = new Vector3d(0.0,6.0,5.0);
        Vector3d CeilingLD = new Vector3d(0.0,-6.0,5.0);
        Vector3d CeilingRU = new Vector3d(10.0,6.0,5.0);
        Vector3d CeilingRD = new Vector3d(10.0,-6.0,5.0);
        Vector3d CeilingRM = new Vector3d(13.0,0.0,5.0);

        Vector3d FloorLM = new Vector3d(-3.0,0.0,-5.0);
        Vector3d FloorLU = new Vector3d(0.0,6.0,-5.0);
        Vector3d FloorLD = new Vector3d(0.0,-6.0,-5.0);
        Vector3d FloorRU = new Vector3d(10.0,6.0,-5.0);
        Vector3d FloorRD = new Vector3d(10.0,-6.0,-5.0);
        Vector3d FloorRM = new Vector3d(13.0,0.0,-5.0);

        // Floor
        triangleList.add(new Triangle(FloorRD,FloorRM,FloorRU,white));
        triangleList.add(new Triangle(FloorLD,FloorRD,FloorRU,white));
        triangleList.add(new Triangle(FloorLD,FloorRU,FloorLU,white));
        triangleList.add(new Triangle(FloorLD,FloorLU,FloorLM,white));

        //Ceiling
        triangleList.add(new Triangle(CeilingRD,CeilingRU,CeilingRM,white));
        triangleList.add(new Triangle(CeilingLD,CeilingLU,CeilingRU,white));
        triangleList.add(new Triangle(CeilingRD,CeilingLD,CeilingRU,white));
        triangleList.add(new Triangle(CeilingLD,CeilingLM,CeilingLU,white));

        //Left upper wall
        triangleList.add(new Triangle(FloorLM,FloorLU,CeilingLU,yellow));
        triangleList.add(new Triangle(FloorLM,CeilingLU,CeilingLM,yellow));

        //Left bottom wall
        triangleList.add(new Triangle(FloorLD,FloorLM,CeilingLM,cyan));
        triangleList.add(new Triangle(FloorLD,CeilingLM,CeilingLD,cyan));

        //Mid top wall
        triangleList.add(new Triangle(FloorLU,FloorRU,CeilingRU,red));
        triangleList.add(new Triangle(FloorLU,CeilingRU,CeilingLU,red));

        //Mid bottom wall
        triangleList.add(new Triangle(FloorRD,FloorLD,CeilingLD,green));
        triangleList.add(new Triangle(FloorRD,CeilingLD,CeilingRD,green));

        //Right upper wall
        triangleList.add(new Triangle(FloorRU,FloorRM,CeilingRM,blue));
        triangleList.add(new Triangle(FloorRU,CeilingRM,CeilingRU,blue));

        //Right bottom wall
        triangleList.add(new Triangle(FloorRM,FloorRD,CeilingRD,magenta));
        triangleList.add(new Triangle(FloorRM,CeilingRD,CeilingRM,magenta));
    }
    void addTriangle(Triangle t){
        triangleList.add(t);
    }
    void addSphere(Sphere s){
        sphereList.add(s);
    }
    void addTetrahedron(Vector3d origin, double size, ColorDbl shapeColor){
      //Calculate vertex distances
      double d1 = size * 0.942809041582063365867792482806465385713114583584632048; //sqrt(8/9)
      double d2 = size * 0.47140452079103168293389624140323269285655729179231602;  //sqrt(2/9)
      double d3 = size * 0.816496580927726032732428024901963797321982493552223376; //sqrt(2/3)
      double d4 = size * 0.33333333333333333333333333333333333333333333333333333;  //1/3
      //Create vertices, positioned with origin in the geometric center
      Vector3d v1 = new Vector3d(d1, 0.0, -d4);
      Vector3d v2 = new Vector3d(-d2, d3, -d4);
      Vector3d v3 = new Vector3d(-d2, -d3, -d4);
      Vector3d v4 = new Vector3d(0.0, 0.0, size);
      //Move object to selected origin
      v1.add(origin);
      v2.add(origin);
      v3.add(origin);
      v4.add(origin);
      //Create the triangles (positions defined as viewed from the camera, left is positive Y direction)
      triangleList.add( new Triangle( v1, v2, v3, shapeColor) );// bottom side
      triangleList.add( new Triangle( v1, v4, v2, shapeColor) );// left side
      triangleList.add( new Triangle( v1, v3, v4, shapeColor) );// Right side
      triangleList.add( new Triangle( v3, v2, v4, shapeColor) );// Back side
    }
    void addSphere(){

    }
    void triangleIntersect(Ray r){
        double t = 0.0;
        double d = 0.0;
        //Check for intersection with all triangles
        for (Triangle Tri : triangleList){
            t = Tri.rayIntersection(r); //Distance to triangle intersection
            //Check for intersection with all spheres
            for(Sphere Sph : sphereList){
                d = Sph.sphereIntersect(r); //Distance to sphere intersection

            // The color of the ray is the closest of t and d,
            //so long as they are larger than 1
            if(t > 1.0 && t < Double.POSITIVE_INFINITY){
                if(d > 1.0 && d < Double.POSITIVE_INFINITY && d < t){
                    r.rayColor = Sph.color; //Triangle hit but sphere is closer
                }
                else{
                    r.rayColor = Tri.color; //Sphere hit but triangle is closer
                }
            }
            else if(d > 1.0 && d < Double.POSITIVE_INFINITY){
                // -- CANNOT HAPPEN IN CLOSED ROOM
                r.rayColor = Sph.color; //Only sphere hit, no triangle hit
            }
          }
          }
    }
    public static void main(String[] args) {
        Vector3d v1 = new Vector3d(-1.0,0.0,0.0);
        Vector3d v2 = new Vector3d(0.0,0.21,0.1);
        Ray r1 = new Ray(v1,v2);
        Scene s = new Scene();
        r1.rayColor.print();
        s.triangleIntersect(r1);
        r1.rayColor.print();
    }
}
