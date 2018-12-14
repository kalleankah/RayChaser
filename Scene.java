import javax.vecmath.Vector3d;
import java.util.*;
public class Scene{
    Vector<Triangle> triangleList= new Vector<Triangle>();
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
    void triangleIntersect(Ray r){
        double t = 0.0;
        for (Triangle Tri : triangleList){
            t = Tri.rayIntersection(r);
            System.out.println(t);
            Tri.print();
            if(t > 1.0 && t < Double.POSITIVE_INFINITY){
                System.out.println("TRUE");
                r.rayColor = Tri.color;
            }
            System.out.println("\n\n\n\n");
        }
    }
    public static void main(String[] args) {
        Vector3d v1 = new Vector3d(-1.0,0.0,0.0);
        Vector3d v2 = new Vector3d(0.0,1.0,0.0);
        Ray r1 = new Ray(v1,v2);
        Scene s = new Scene();
        r1.rayColor.print();
        s.triangleIntersect(r1);
        r1.rayColor.print();
    }
}
