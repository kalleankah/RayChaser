import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Scene{
    Vector<Object3D> object3DList= new Vector<Object3D>();
    Vector<Light> lightList = new Vector<Light>();
    Scene(){
        ColorDbl white = new ColorDbl(0.95,0.95,0.95);
        ColorDbl red = new ColorDbl(0.95,0.2,0.2);
        ColorDbl green = new ColorDbl(0.2,0.95,0.2);
        ColorDbl blue = new ColorDbl(0.2,0.2,0.95);
        ColorDbl yellow = new ColorDbl(0.95,0.95,0.2);
        ColorDbl cyan = new ColorDbl(0.2,0.95,0.95);
        ColorDbl magenta = new ColorDbl(0.95,0.2,0.95);

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
        object3DList.add(new Triangle(CeilingRD,CeilingRU,CeilingRM,yellow));
        object3DList.add(new Triangle(CeilingLD,CeilingLU,CeilingRU,yellow));
        object3DList.add(new Triangle(CeilingRD,CeilingLD,CeilingRU,yellow));
        object3DList.add(new Triangle(CeilingLD,CeilingLM,CeilingLU,yellow));

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
    void addLight(Light l){
        lightList.add(l);
    }

    void triangleIntersect(Ray r, int RayBounces){
        Boolean is_specular = false;
        double t = 0.0;
        double temp = Double.POSITIVE_INFINITY;

        double NearClip;
        //Ignore intersections near camera
        if(r.MotherNode){
            NearClip = 1.0;
        }
        //Don't ignore any intersections for secondary bounces
        else{
            NearClip = 0.0;
        }

        /* ------------------------- IMPORTANT ---------------------------------
            Child-rays (secondary bounces) sometimes originate slightly (10e-15 units) from behind
            the surface its parent ray. For this reason, we have to prevent them from intersecting
            with the surface they originate from.

            Solution: Remove all objects in the object3DList facing away from the ray.
        */
        Vector<Object3D> newObject3DList = new Vector<>();
        for (Object3D obj : object3DList){
            Vector3d objNormal = obj.CalculateNormal();
            if(Utilities.vecDot(objNormal, r.direction) < 0.0){
                newObject3DList.add(obj);
            }
        }
        //Check for intersection with all Objects
        for (Object3D obj : newObject3DList){
            t = obj.rayIntersection(r); //Distance t to intersection with an object3D
            //Conditions must be met for intersection to count
            if(t > NearClip && t < Double.POSITIVE_INFINITY && t < temp){
                //Give the ray the color of the object hit
                r.rayColor.setColor(obj.color);
                temp = t;
                //Calculate the point of intersection
                r.calculatePhit(temp);
                //Calculate the normal of the point of intersection
                r.P_Normal = obj.CalculateNormal(r.P_hit);
                if(obj instanceof Sphere){
                  is_specular = true;
                }else{
                  is_specular = false;
                }
            }
        }
        //System.out.println("Distance to intersection = " + temp);
        //Create a shadow ray
        r.calculateShadowRay(lightList.get(0));
        if(r.ShadowRay == null){
            System.out.println("ShadowRay == null");
        }
        double tshadowray = 0.0;
        //Check for intersection with all Objects (ShadowRay)
        for (Object3D obj : object3DList){
            if(r.ShadowRay == null){
                System.out.println("2 ShadowRay == null");
            }
            //Calculate distance to intersection (ShadowRay)
            tshadowray = obj.rayIntersection(r.ShadowRay);
            if(tshadowray > 0.000001 && tshadowray < 1.0){
                r.isInShadow();
                break;
            }
        }
        //Call the function recursively for the bounce-depth
        if(RayBounces > 0){
            r.raybounce(is_specular);
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
