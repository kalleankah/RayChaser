import javax.vecmath.*;
import java.lang.Math;
import java.util.*;
public class Scene{
    Vector<Object3D> object3DList= new Vector<Object3D>();
    Vector<Object3D> lightList = new Vector<Object3D>();
    Camera camera;
    public Settings settings;
    Scene(Settings s, Camera c){
        settings = s;
        camera = c;
        Material white = new Material(new ColorDbl(0.95,0.95,0.95));
        Material red = new Material(new ColorDbl(0.95,0.2,0.2));
        Material green = new Material(new ColorDbl(0.2,0.95,0.2));
        Material blue = new Material(new ColorDbl(0.2,0.2,0.95));
        Material yellow = new Material(new ColorDbl(0.95,0.95,0.2));
        Material cyan = new Material(new ColorDbl(0.2,0.95,0.95));
        Material magenta = new Material(new ColorDbl(0.95,0.2,0.95));
        Material EMISSION = new Emissive(new ColorDbl(0.0,1.0,0.0), 5.0);
        Material Reflective = new Reflective(new ColorDbl(1.0,1.0,1.0));

        Vector3d CeilingLM = new Vector3d(-3.0,0.0,5.0);
        Vector3d CeilingLU = new Vector3d(0.0,6.0,5.0);
        Vector3d CeilingLD = new Vector3d(0.0,-6.0,5.0);
        Vector3d CeilingRU = new Vector3d(10.0,6.0,5.0);
        Vector3d CeilingRD = new Vector3d(10.0,-6.0,5.0);
        Vector3d CeilingRM = new Vector3d(13.0,0.0,5.0);

        Vector3d CeilingLight1 = new Vector3d(7.0,-2,4.999999999);
        Vector3d CeilingLight2 = new Vector3d(11.0,-2,4.999999999);
        Vector3d CeilingLight3 = new Vector3d(11.0, 2,4.999999999);

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
        object3DList.add(new Triangle(CeilingRD,CeilingLD,CeilingRU,white));
        object3DList.add(new Triangle(CeilingLD,CeilingLU,CeilingRU,white));
        object3DList.add(new Triangle(CeilingLD,CeilingLM,CeilingLU,white));

        //Lamp
        addLight(new Plane(CeilingLight1,CeilingLight2,CeilingLight3,EMISSION));

        //Left upper wall
        object3DList.add(new Triangle(FloorLM,FloorLU,CeilingLU,red));
        object3DList.add(new Triangle(FloorLM,CeilingLU,CeilingLM,red));

        //Left bottom wall
        object3DList.add(new Triangle(FloorLD,FloorLM,CeilingLM,green));
        object3DList.add(new Triangle(FloorLD,CeilingLM,CeilingLD,green));

        //Mid top wall
        object3DList.add(new Triangle(FloorLU,FloorRU,CeilingRU,blue));
        object3DList.add(new Triangle(FloorLU,CeilingRU,CeilingLU,blue));
        //object3DList.add(new Plane(FloorLU, FloorRU, CeilingRU, blue));

        //Mid bottom wall
        object3DList.add(new Triangle(FloorRD,FloorLD,CeilingLD,yellow));
        object3DList.add(new Triangle(FloorRD,CeilingLD,CeilingRD,yellow));

        //Right upper wall
        object3DList.add(new Triangle(FloorRU,FloorRM,CeilingRM,cyan));
        object3DList.add(new Triangle(FloorRU,CeilingRM,CeilingRU,cyan));

        //Right bottom wall
        object3DList.add(new Triangle(FloorRM,FloorRD,CeilingRD,magenta));
        object3DList.add(new Triangle(FloorRM,CeilingRD,CeilingRM,magenta));
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
        return hitObject;
    }
    //Shadow ray occlusion check
    Boolean ObjectHit(Ray r){
        double t = -1.0;
        for(Object3D obj : object3DList){
                t = obj.rayIntersection(r);
                if(t > 0.0000000001 && t < 0.9999999){
                        return true;
                }
        }
        return false;
    }
    ColorDbl CastRay(Ray r, int Depth, int ReflectionDepth){
        Object3D HitObject = triangleIntersect(r);
        if(HitObject == null){
            return new ColorDbl(0.0,0.0,0.0);
        }
        if(HitObject.mat instanceof Emissive){
            return new ColorDbl(1.0,1.0,1.0);
        }
        Vector3d P_hit_corr = Utilities.vecAdd(r.P_hit,Utilities.vecScale(r.P_Normal, 0.00000001));
        if(HitObject.mat instanceof Reflective && ReflectionDepth <= settings.MAX_REFLECTION_BOUNCES){
            Vector3d d = Utilities.vecSub(r.P_hit,r.start);
            Vector3d PerfectReflector = Utilities.vecSub(d, Utilities.vecScale(r.P_Normal, 2*Utilities.vecDot(d,r.P_Normal)));
            Ray Child = new Ray(P_hit_corr,Utilities.vecAdd(r.P_hit,PerfectReflector), false);
            ColorDbl c = CastRay(Child, Depth, ReflectionDepth+1);
            c.multiply(HitObject.mat.color);
            return c;
        }

        ColorDbl DirectLightcontrib = new ColorDbl(0.0,0.0,0.0);
        ColorDbl IndirectLightcontrib = new ColorDbl(0.0,0.0,0.0);
        Double TotalBrightness = 0.0;
        Double Brightness = 0.0;
        for(Object3D l : lightList){
            Vector<Vector3d> SampList = l.getSampleLight(settings.SHADOW_RAYS);
            Brightness = 0.0;
            for(Vector3d pos : SampList){
                Ray ShadowRay = new Ray(P_hit_corr, pos,false);
                if(!ObjectHit(ShadowRay)){
                    Brightness +=  Math.max(0.0, l.mat.Brightness * Utilities.vecDot(ShadowRay.direction, r.P_Normal)* Utilities.vecDot(ShadowRay.direction, l.CalculateNormal())/(ShadowRay.RayLength));
                }
            }
            Brightness /= SampList.size();
            TotalBrightness += Brightness;
        }
        TotalBrightness /= lightList.size();

        DirectLightcontrib.setColor(HitObject.mat.color);
        DirectLightcontrib.multiply(TotalBrightness);
        if(Depth >= settings.MAX_DEPTH) {
            //DirectLightcontrib.divide(Depth+1);
            return DirectLightcontrib;
        }

        // Calculate World2Local and Local2World Matrices
        Vector3d Z = r.P_Normal;
        Vector3d I_o = Utilities.vecSub(r.direction, Utilities.vecScale(Z, Utilities.vecDot(r.direction, Z)));
        Vector3d X = Utilities.vecScale(I_o, 1.0/Utilities.vecNorm(I_o));
        Vector3d Y = Utilities.vecCross(Utilities.vecScale(X,-1.0), Z);

        //Create coordinate system transformation matrices
        Matrix4d rotation_mat = new Matrix4d(
        X.x, X.y, X.z, 0.0,
        Y.x, Y.y, Y.z, 0.0,
        Z.x, Z.y, Z.z, 0.0,
        0.0, 0.0, 0.0, 1.0);
        Matrix4d translation_mat = new Matrix4d(
        1.0, 0.0, 0.0, -r.P_hit.x,
        0.0, 1.0, 0.0, -r.P_hit.y,
        0.0, 0.0, 1.0, -r.P_hit.z,
        0.0, 0.0, 0.0, 1.0);

        Matrix4d world2local = new Matrix4d();
        world2local.mul(rotation_mat, translation_mat);
        Matrix4d local2world = Utilities.invertMat(world2local);

        //Incoming ray in local coords
        Vector3d direction_local = Utilities.mulMatVec(world2local, r.direction);
        double Azimuth = 0; //phi
        double Altitude = 0; //theta
        double x,y,z = 0;
        Vector3d LocalEndPoint, EndPoint;
        Vector3d localnormal = new Vector3d(0.0, 0.0, 1.0);
        int N_CHILDREN = 0;

        LocalEndPoint = new Vector3d();
        Random random = new Random();
        for(int i = 0; i < settings.CHILDREN; ++i){
            if(!Utilities.RussianBullet(settings.DEPTH_DECAY + (1.0-settings.DEPTH_DECAY) * ((double)Depth / (double)settings.MAX_DEPTH))){
                Azimuth = random.nextDouble()*2*Math.PI;
                Altitude = random.nextDouble()*0.5*Math.PI;
                LocalEndPoint.x = Math.sin(Altitude)*Math.sin(Azimuth);
                LocalEndPoint.y = Math.sin(Altitude)*Math.cos(Azimuth);
                LocalEndPoint.z = Math.cos(Altitude); // up

                EndPoint = Utilities.mulMatVec(local2world, LocalEndPoint);
                Ray Child = new Ray(P_hit_corr, EndPoint, false);
                ColorDbl c = CastRay(Child, Depth+1,0);
                IndirectLightcontrib.sumColor(c);
                N_CHILDREN++;
            }
        }
        if(N_CHILDREN != 0){
            IndirectLightcontrib.divide(N_CHILDREN);
        }
        IndirectLightcontrib.multiply(HitObject.mat.color);
        DirectLightcontrib.sumColor(IndirectLightcontrib);
        return DirectLightcontrib;
    }
    //Start rendering scene S
    void render(int start, int end){
        double PixelSize = 2.0/camera.Width;
        double subPixelSize = PixelSize/camera.subpixels;
        double halfSubPixel = 0.5*subPixelSize;
        double subPixelFactor = 1.0/(camera.subpixels*camera.subpixels);
        Vector3d endPoint;
        Ray r;
        ColorDbl temp;

        for(int j = start; j < end; ++j){
            //Utilities.updateProgress( (double) j/camera.Width); //adds 2-3 seconds to all renders
            for(int i = 0; i < camera.Width; ++i){
                temp = new ColorDbl();
                for(int k = 0; k<camera.subpixels; ++k){
                    for(int l = 0; l<camera.subpixels; ++l){
                        endPoint = new Vector3d(camera.eye.x+camera.fov, -i*PixelSize - halfSubPixel-k*subPixelSize + 1 + camera.eye.y, -j*PixelSize - halfSubPixel-l*subPixelSize + 1 + camera.eye.z);
                        r = new Ray(camera.eye, endPoint, true);
                        temp.sumColor(CastRay(r,0,0));
                    }
                }
                temp.multiply(subPixelFactor);
                temp.clamp();
                camera.pixelList[i][j] = temp;
            }
        }
    }
}
