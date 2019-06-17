import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Scene{
    Vector<Object3D> object3DList= new Vector<Object3D>();
    Vector<Object3D> lightList = new Vector<Object3D>();
    int MAX_DEPTH;
    double DEPTH_DECAY;
    Scene(){
        MAX_DEPTH = 1;
        DEPTH_DECAY = 0.3;
        Material white = new Material(new ColorDbl(0.95,0.95,0.95));
        Material red = new Material(new ColorDbl(0.95,0.2,0.2));
        Material green = new Material(new ColorDbl(0.2,0.95,0.2));
        Material blue = new Material(new ColorDbl(0.2,0.2,0.95));
        Material yellow = new Material(new ColorDbl(0.95,0.95,0.2), false, true, 10.0);
        Material cyan = new Material(new ColorDbl(0.2,0.95,0.95));
        Material magenta = new Material(new ColorDbl(0.95,0.2,0.95));

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
        object3DList.add(new Triangle(FloorRD,FloorRM,FloorRU,cyan));
        object3DList.add(new Triangle(FloorLD,FloorRD,FloorRU,cyan));
        object3DList.add(new Triangle(FloorLD,FloorRU,FloorLU,cyan));
        object3DList.add(new Triangle(FloorLD,FloorLU,FloorLM,cyan));

        //Ceiling
        addLight(new Triangle(CeilingRD,CeilingRU,CeilingRM,yellow));
        addLight(new Triangle(CeilingLD,CeilingLU,CeilingRU,yellow));
        addLight(new Triangle(CeilingRD,CeilingLD,CeilingRU,yellow));
        addLight(new Triangle(CeilingLD,CeilingLM,CeilingLU,yellow));
        

        //Left upper wall
        object3DList.add(new Triangle(FloorLM,FloorLU,CeilingLU,white));
        object3DList.add(new Triangle(FloorLM,CeilingLU,CeilingLM,white));

        //Left bottom wall
        object3DList.add(new Triangle(FloorLD,FloorLM,CeilingLM,white));
        object3DList.add(new Triangle(FloorLD,CeilingLM,CeilingLD,white));

        //Mid top wall
        object3DList.add(new Triangle(FloorLU,FloorRU,CeilingRU,white));
        object3DList.add(new Triangle(FloorLU,CeilingRU,CeilingLU,white));

        //Mid bottom wall
        object3DList.add(new Triangle(FloorRD,FloorLD,CeilingLD,white));
        object3DList.add(new Triangle(FloorRD,CeilingLD,CeilingRD,white));

        //Right upper wall
        object3DList.add(new Triangle(FloorRU,FloorRM,CeilingRM,white));
        object3DList.add(new Triangle(FloorRU,CeilingRM,CeilingRU,white));

        //Right bottom wall
        object3DList.add(new Triangle(FloorRM,FloorRD,CeilingRD,white));
        object3DList.add(new Triangle(FloorRM,CeilingRD,CeilingRM,white));
    }
    void addObject(Object3D o){
        object3DList.add(o);
    }
    void addLight(Object3D o){
        lightList.add(o);
        object3DList.add(o);
    }
    
    Object3D triangleIntersect(Ray r){
        double t = 0.0;
        double temp = Double.POSITIVE_INFINITY;
        double NearClip =0.0;
        Object3D hitObject = null;
        if(r.MotherNode){
            NearClip = 1.0;
        }
        for (Object3D obj : object3DList){
            t = obj.rayIntersection(r);
            if(t > NearClip  && t < temp){
                hitObject = obj;
                temp = t;
                //Calculate the point of intersection
                r.calculatePhit(temp);
                //Calculate the normal of the point of intersectio
                r.P_Normal = obj.CalculateNormal(r.P_hit);
            }
        }
        if(hitObject == null){
            hitObject = new Object3D();
        }
        return hitObject;
    }
    Boolean ObjectHit(Ray r){
        double t = -1.0;
        for(Object3D obj : object3DList){
            //if(!obj.mat.Emissive){
                t = obj.rayIntersection(r);
                if(t > 0.01 && t < 1.0){
                        return true;
                }
            //}
        }
        return false;
    }
    
}
