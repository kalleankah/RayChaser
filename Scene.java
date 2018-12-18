import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Scene{
    Vector<Object3D> object3DList= new Vector<Object3D>();
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
        object3DList.add(new Triangle(FloorRD,FloorRM,FloorRU,white));
        object3DList.add(new Triangle(FloorLD,FloorRD,FloorRU,white));
        object3DList.add(new Triangle(FloorLD,FloorRU,FloorLU,white));
        object3DList.add(new Triangle(FloorLD,FloorLU,FloorLM,white));

        //Ceiling
        object3DList.add(new Triangle(CeilingRD,CeilingRU,CeilingRM,white));
        object3DList.add(new Triangle(CeilingLD,CeilingLU,CeilingRU,white));
        object3DList.add(new Triangle(CeilingRD,CeilingLD,CeilingRU,white));
        object3DList.add(new Triangle(CeilingLD,CeilingLM,CeilingLU,white));

        //Left upper wall
        object3DList.add(new Triangle(FloorLM,FloorLU,CeilingLU,yellow));
        object3DList.add(new Triangle(FloorLM,CeilingLU,CeilingLM,yellow));

        //Left bottom wall
        object3DList.add(new Triangle(FloorLD,FloorLM,CeilingLM,cyan));
        object3DList.add(new Triangle(FloorLD,CeilingLM,CeilingLD,cyan));

        //Mid top wall
        object3DList.add(new Triangle(FloorLU,FloorRU,CeilingRU,red));
        object3DList.add(new Triangle(FloorLU,CeilingRU,CeilingLU,red));

        //Mid bottom wall
        object3DList.add(new Triangle(FloorRD,FloorLD,CeilingLD,green));
        object3DList.add(new Triangle(FloorRD,CeilingLD,CeilingRD,green));

        //Right upper wall
        object3DList.add(new Triangle(FloorRU,FloorRM,CeilingRM,blue));
        object3DList.add(new Triangle(FloorRU,CeilingRM,CeilingRU,blue));

        //Right bottom wall
        object3DList.add(new Triangle(FloorRM,FloorRD,CeilingRD,magenta));
        object3DList.add(new Triangle(FloorRM,CeilingRD,CeilingRM,magenta));
    }
    void addObject(Object3D o){
        object3DList.add(o);
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
      object3DList.add( new Triangle( v1, v2, v3, shapeColor) );// bottom side
      object3DList.add( new Triangle( v1, v4, v2, shapeColor) );// left side
      object3DList.add( new Triangle( v1, v3, v4, shapeColor) );// Right side
      object3DList.add( new Triangle( v3, v2, v4, shapeColor) );// Back side
    }

    void triangleIntersect(Ray r){
        double t = 0.0;
        double temp = Double.POSITIVE_INFINITY;
        //Check for intersection with all Objects
        for (Object3D obj : object3DList){
            t = obj.rayIntersection(r); //Distance to object3D intersection
            if(t > 1.0 && t < Double.POSITIVE_INFINITY && t < temp){
                r.rayColor = obj.color;
                temp = t;
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
