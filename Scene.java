import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Scene{
    Vector<Object3D> object3DList= new Vector<Object3D>();
    Vector<Light> lightList = new Vector<Light>();
    Scene(){
        ColorDbl white = new ColorDbl(1.0,1.0,1.0);
        ColorDbl red = new ColorDbl(1.0,0.1,0.1);
        ColorDbl green = new ColorDbl(0.1,1.0,0.1);
        ColorDbl blue = new ColorDbl(0.1,0.1,1.0);
        ColorDbl yellow = new ColorDbl(1.0,1.0,0.1);
        ColorDbl cyan = new ColorDbl(0.1,1.0,1.0);
        ColorDbl magenta = new ColorDbl(1.0,0.1,1.0);

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
    void addLight(Light l){
        lightList.add(l);
    }

    void triangleIntersect(Ray r, int RayBounces){
        double t = 0.0;
        double temp = Double.POSITIVE_INFINITY;
        //Object3D tempObj = new Object3D(new ColorDbl(0.0,0.0,0.0));
        //Check for intersection with all Objects
        double BiggerThan;
        if(r.MotherNode){
            BiggerThan = 1.0;
        }
        else{
            BiggerThan = 0.0;
        }
        for (Object3D obj : object3DList){
            t = obj.rayIntersection(r); //Distance to object3D intersection
            if(t > BiggerThan && t < Double.POSITIVE_INFINITY && t < temp){
                r.rayColor.setColor(obj.color);
                temp = t;
                r.calculatePhit(temp);
                r.P_Normal = obj.CalculateNormal(r.P_hit);
            }
        }
        r.calculateShadowRay(lightList.get(0)); // Problem i hörn 
        double tshadowray = 0.0;
        for (Object3D obj : object3DList){
            tshadowray = obj.rayIntersection(r.ShadowRay);
            if(tshadowray > 0.000001 && tshadowray < 1.0){
                r.isInShadow();
                break;
            }
        }
        if(RayBounces > 0){
            r.raybounce();
            for (Ray rChild : r.Children){
                this.triangleIntersect(rChild, RayBounces-1);
            }
        }

    }
    public static void main(String[] args) {
        /*Vector3d v1 = new Vector3d(-1.0,0.0,0.0);
        Vector3d v2 = new Vector3d(0.0,0.21,0.1);
        Ray r1 = new Ray(v1,v2);
        Scene s = new Scene();
        r1.rayColor.print();
        s.triangleIntersect(r1);
        r1.rayColor.print();*/
    }
}
