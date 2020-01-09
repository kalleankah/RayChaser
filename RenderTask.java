import javafx.concurrent.Task;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/* The purpose of RenderTask is to render a small portion (a box) of the scene.
A rendering loop is created that covers (startX, startY) to (endX, endY).
In JFX.java a list of RenderTasks that together make up the entire scene, is
created. RenderTask contains the actual ray tracing code, ray-triangle intersection,
reflection of rays, casting shadow rays, integrating light etc.
*/

public class RenderTask extends Task<Void> {
  Scene scene;
  Camera camera;
  int startX;
  int startY;
  int range;
  AtomicInteger progress;

  RenderTask(Scene s, Camera c, int X, int Y, int r, AtomicInteger p){
    scene = s;
    camera = c;
    startX = X;
    startY = Y;
    range = r;
    progress = p;
  }

  //Start rendering the box from (startX, startY) to (endX, endY)
  @Override
  protected Void call(){
    double PixelSize = 2.0/camera.Width;
    double subPixelSize = PixelSize/camera.subpixels;
    double subPixelFactor = 1.0/(camera.subpixels*camera.subpixels);
    double cameraPlaneXaxis = camera.eye.x+camera.fov;
    ColorDbl temp = new ColorDbl();
    int endX = Math.min(startX+range, camera.Width);
    int endY = Math.min(startY+range, camera.Height);

    //Render the box [startX->endX, startY->endY]
    // The xy-loop is a loop over all pixels x*y
    for(int y = startY; y < endY; ++y){
      for(int x = startX; x < endX; ++x){
        temp.R = temp.G = temp.B = 0.0;
        // The loop ij is a loop over all subpixels (samples) i*j on each pixel
        for(int i = 0; i<camera.subpixels; ++i){
          for(int j = 0; j<camera.subpixels; ++j){
            temp.sumColor(CastRay(new Ray(camera.eye, new Vector3d(cameraPlaneXaxis, -x*PixelSize - 0.5*subPixelSize-i*subPixelSize + 1 + camera.eye.y, -y*PixelSize - 0.5*subPixelSize-j*subPixelSize + 1 + camera.eye.z), true),0,0));
          }
        }
        temp.multiply(subPixelFactor);
        temp.clamp();
        //Write color to the current pixel in the image
        camera.image.getPixelWriter().setArgb(x,y,temp.ARGBForImage());
      }
      //Increment the atomic integer to report progress from this thread
      progress.incrementAndGet();
    }
    return null;
  }

  //Cast a ray into the scene
  ColorDbl CastRay(Ray r, int Depth, int ReflectionDepth){
    //Determine which object is hit
    Object3D HitObject = triangleIntersect(r);
    //If the ray doesn't hit any object, return skybox color in that direction
    if(HitObject == null){
      return SkyColor(r);
    }
    //If the ray hits an emitter
    if(HitObject.mat instanceof Emissive){
      //If it hits front side of light source
      if(Utilities.vecDot(HitObject.CalculateNormal(r.P_hit),r.direction) < 0){
        ColorDbl returncolor = new ColorDbl(HitObject.mat.getColor());
        returncolor.multiply(HitObject.mat.Brightness);
        return returncolor;
      }
      //If backside of light source, return black
      return new ColorDbl();
    }
    //If the ray hits a reflective object
    if(HitObject.mat instanceof Reflective){
      //Check if max consecutive reflections between reflective objects is reached
      if(ReflectionDepth >= camera.MAX_REFLECTION_BOUNCES) {return new ColorDbl();}
      //Otherwise, cast new ray in the "perfect reflection direction"
      Vector3d d = Utilities.vecSub(r.P_hit,r.start);
      Vector3d PerfectReflector = Utilities.vecSub(d, Utilities.vecScale(r.P_Normal, 2*Utilities.vecDot(d,r.P_Normal)));
      ColorDbl c = CastRay(new Ray(r.P_hit,Utilities.vecAdd(r.P_hit,PerfectReflector), false), Depth, ReflectionDepth+1);
      c.multiply(HitObject.mat.getColor());
      return c;
    }

    //glossycolor and glossyscale must be initiated for glossy calculations
    ColorDbl glossycolor = new ColorDbl();
    ColorDbl glossyscale = new ColorDbl(1);
    //If the ray hits a glossy object
    if(HitObject.mat instanceof Glossy){
      /* When glossy objects are hit we reflect a ray somewhere in an area
      around the perfect reflection direction. How big the area is, and thus
      how diffuse the object is, depends on the "roughness". It's achieved by
      shifting the normal of the surface in the point. */

      Random Rand = new Random();
      //Find normal N at point of intersection
      Vector3d N = HitObject.CalculateNormal(r.P_hit);
      //Fetch roughness from material
      double roughness = HitObject.mat.getRoughness();

      // --- Calculate orthogonal vector to N ---
      Vector3d ortho_vec1 = Utilities.vecAdd(N,new Vector3d(1,0,0));
      double dot = Utilities.vecDot(ortho_vec1,N);
      if(dot<0.000001){
        ortho_vec1 = Utilities.vecAdd(N,new Vector3d(0,1,0));
        dot = Utilities.vecDot(ortho_vec1,N);
      }
      ortho_vec1 = Utilities.vecSub(ortho_vec1, Utilities.vecScale(N,dot));
      ortho_vec1.normalize();

      //Calculate second orthogonal vector to N
      Vector3d ortho_vec2 = Utilities.vecCross(N,ortho_vec1);
      //Generate two random numbers around 0 to offset normal with
      ortho_vec1 = Utilities.vecScale(ortho_vec1, Rand.nextDouble()*roughness - roughness/2);
      ortho_vec2 = Utilities.vecScale(ortho_vec2, Rand.nextDouble()*roughness - roughness/2);
      Vector3d offset = Utilities.vecAdd(ortho_vec1,ortho_vec2);
      //Offset normal
      Vector3d new_N = Utilities.vecAdd(N,offset);
      new_N.normalize();
      //Reflect the ray in the new shifted normal's direction
      Vector3d to_phit = Utilities.vecSub(r.P_hit,r.start);
      Vector3d PerfectReflector = Utilities.vecSub(to_phit, Utilities.vecScale(new_N, 2*Utilities.vecDot(to_phit,new_N)));
      Vector3d endPoint = Utilities.vecAdd(r.P_hit,PerfectReflector);
      Vector3d newRayDir = Utilities.vecSub(endPoint,r.P_hit);
      glossycolor = CastRay(new Ray(r.P_hit,endPoint, false), Depth+1, ReflectionDepth);
      //Calculate the color from reflections off the glossy object
      glossycolor.multiply(HitObject.mat.color);
      glossycolor.multiply(0.1);
      glossyscale.multiply(0.9);
    }

    //The color of the surface must be initiated for texture calculations
    ColorDbl objectcolor;
    //If the ray hits a textured object
    if(HitObject.mat.texture != null){
      //Determine texture coordinates
      Vector3d nxv0 = Utilities.vecSub(r.P_hit,HitObject.getVertex(0));
      double numerator_u = Utilities.vecDot(nxv0,HitObject.getEdge(1));
      double denominator_u = Utilities.vecNorm(nxv0)*Utilities.vecNorm(HitObject.getEdge(1));
      double numerator_v = Utilities.vecDot(nxv0,HitObject.getEdge(2));
      double denominator_v = Utilities.vecNorm(nxv0)*Utilities.vecNorm(HitObject.getEdge(2));

      double sqrt2ratio = Math.sqrt(2)*Utilities.vecNorm(nxv0)/Utilities.vecNorm(Utilities.vecSub(HitObject.getVertex(2),HitObject.getVertex(0)));

      double u = Math.max(0,Math.min(1,sqrt2ratio*numerator_u/denominator_u));
      double v = Math.max(0,Math.min(1,sqrt2ratio*numerator_v/denominator_v));

      //Fetch color using calculated texture coordinates (u,v)
      objectcolor = HitObject.mat.getColor(u,v);
    }else{
      //If no texture, get color from material (independent of texture coordinates)
      objectcolor = HitObject.mat.getColor();
    }

    //DirectLightcontrib needs to be initialized before local light model calculations
    ColorDbl DirectLightcontrib = new ColorDbl();

    /* --------------------- LOCAL LIGHT MODEL BEGIN ---------------------*/
    // If the ray hits a diffuse object
    // ... and shadow rays are enabled
    if(camera.SHADOW_RAYS > 0){
      double Brightness = 0.0;
      double TotalBrightness = 0.0;

      //Loop through all emitters in scene
      for(Object3D l : scene.lightList){
        //Get "camera.SHADOW_RAYS"-number of sample positions on the emitter.
        Vector<Vector3d> SampList = l.getSampleLight(camera.SHADOW_RAYS, r.P_hit);
        Brightness = 0.0;
        //Loop over all shadow ray samples
        for(Vector3d pos : SampList){
          Ray ShadowRay = new Ray(r.P_hit, pos, false);
          if(!ObjectHit(ShadowRay)){
            /* Simplified for readability:
            b = emitter brightness
            L = light direction
            Ns = surface normal
            Ne = emitter surface normal
            d = distance to emitter sample point (d is clamped to d >= 1.0)
            Brightness = b * dot(L, Ns) * -dot(L Nl) * 1/d
            */
            Brightness += Math.max(0.0, l.mat.Brightness * Utilities.vecDot(ShadowRay.direction, r.P_Normal) * -Utilities.vecDot(ShadowRay.direction, l.CalculateNormal())/Math.max(1.0,ShadowRay.RayLength));
          }
        }
        //Average brightness over all samples
        Brightness /= SampList.size();
        TotalBrightness += Brightness;
      }
      //Average brightness over all emitters
      // TotalBrightness /= scene.lightList.size();
      DirectLightcontrib = ColorDbl.multiply(objectcolor, TotalBrightness); //Multiply incoming light with surface

      //If max depth reached, return without casting more rays
      if(Depth >= camera.MAX_DEPTH) {
        return DirectLightcontrib;
      }
    }
    /* -------------------- LOCAL LIGHT MODEL END --------------------- */

    // If not using shadow rays, simply kill rays exceeding max bounces
    else{
      if(Depth>=camera.MAX_DEPTH){return new ColorDbl();}
    }

    // Calculate World2Local and Local2World transformation matrices
    Vector3d N = r.P_Normal;
    Vector3d Nt, Nb;
    if(Math.abs(N.x) > Math.abs(N.y)){
      Nt = Utilities.vecScale(new Vector3d(N.z, 0, -N.x), 1.0/Math.sqrt(N.x*N.x+N.z*N.z));
    }
    else{
      Nt = Utilities.vecScale(new Vector3d(0, -N.z, N.y), 1.0/Math.sqrt(N.y*N.y+N.z*N.z));
    }
    Nb = Utilities.vecCross(N,Nt);

    //Rotation matrices world <-> local coordinate systems
    Matrix3d world2local = new Matrix3d(
    Nt.x, Nt.y, Nt.z,
    Nb.x, Nb.y, Nb.z,
    N.x, N.y, N.z);
    Matrix3d local2world;
    //Matrix inversion is prone to errors, wrap in try-catch
    try{local2world = Utilities.invertMat(world2local);}
    catch(Exception e){
      //Print error, the render is to be declared a failiure
      System.out.println("The matrix could not be inverted.");
      /* To prevent a crash if a matrix inversion for some reason fails, ignore
      inverting and use world2local. */
      local2world = world2local;
    }

    //Sample hemisphere uniformly using cosine weighted hemisphere sampling
    Random random = new Random();
    double u = random.nextDouble();
    double sintheta = Math.sqrt(-u*(u-2));
    double phi = random.nextDouble() * 2 * Math.PI;
    double x = sintheta * Math.cos(phi);
    double y = sintheta * Math.sin(phi);

    //Cast the reflected ray
    ColorDbl c = CastRay(new Ray(r.P_hit, Utilities.vecAdd(r.P_hit, Utilities.mulMatVec(local2world, new Vector3d(x, y, Math.sqrt(1-x*x-y*y)))), false), Depth+1, 0);
    c.multiply(objectcolor);
    DirectLightcontrib.sumColor(c);
    DirectLightcontrib.multiply(glossyscale);
    DirectLightcontrib.sumColor(glossycolor);
    return DirectLightcontrib;
  }

  //Calculate where the ray intersects the scene (if it does)
  Object3D triangleIntersect(Ray r){
    double t = 0.0;
    double temp = Double.POSITIVE_INFINITY;
    double NearClip = 0.0; //Used to clip objects behind ray origin
    Object3D hitObject = null;
    if(r.MotherNode){
      //Clip objects near camera (MotherNode means the camera is the origin)
      NearClip = 1.0;
    }
    //Loop through all objects in the scene (including emitters)
    for (Object3D obj : scene.object3DList){
      //t is the distance to intersection with this object. A small number is
      //subtracted from t to prevent ending up behind the intersected object.
      //A negative value of t means the intersection is behind the ray origin.
      t = obj.rayIntersection(r)-0.00000001;
      //Check that the intersection is not behind clip and is the closest one.
      if(t > NearClip  && t < temp){
        hitObject = obj;
        //temp = nearest intersection so far
        temp = t;
        //Calculate the point of intersection
        r.calculatePhit(temp);
        r.P_Normal = obj.CalculateNormal(r.P_hit);
      }
    }
    return hitObject;
  }
  //Shadow ray occlusion check
  Boolean ObjectHit(Ray r){
    double t = -1.0;
    for(Object3D obj : scene.object3DList){
      if(!(obj.mat instanceof Emissive)){
        t = obj.rayIntersection(r);
        //If intersection is between origin and emitter (with margins)...
        if(t > 0.0000000001 && t < 0.9999999){
          // ...the emitter is occluded -> return true.
          return true;
        }
      }
    }
    return false;
  }

  //If no object is hit, there is a procedural light box to light the scene
  ColorDbl SkyColor(Ray ray){
    double b_sun = Math.max(0, Utilities.vecDot(new Vector3d(1,-1,1), ray.direction));
    ColorDbl sun = new ColorDbl(1, 0.9, 0.6);
    sun.multiply(b_sun);
    double b_sky = Math.max(0, Utilities.vecDot(new Vector3d(0,0,1), ray.direction));
    ColorDbl sky = new ColorDbl(1-(0.3*b_sky), 0.8, 0.5+(0.3*b_sky));
    return Utilities.avgCol(sun, sky);
  }

}
