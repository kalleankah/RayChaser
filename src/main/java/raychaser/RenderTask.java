package raychaser;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import javafx.concurrent.Task;

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
    //PixelSize is the length a pixel has in the grid from -1 to 1
    double PixelSize = camera.Width>camera.Height ? 2.0/camera.Width : 2.0/camera.Height;
    double subPixelFactor = 1.0/(camera.samples);
    double cameraPlaneXaxis = camera.eye.x+camera.fov;
    ColorDbl temp = new ColorDbl();
    int endX = Math.min(startX+range, camera.Width);
    int endY = Math.min(startY+range, camera.Height);
    double eye_y = camera.eye.y + camera.shift_H + 1.0;
    double eye_z = camera.eye.z + camera.shift_V + 1.0;
    Random Rand = new Random();

    //Render the box [startX->endX, startY->endY]
    // The xy-loop is a loop over all pixels x*y
    for(int y = startY; y < endY; ++y){
      for(int x = startX; x < endX; ++x){
        temp.R = temp.G = temp.B = 0.0;
        // Loop over all subpixels (samples) on each pixel
        for(int i = 0; i<camera.samples; ++i){
          // Positive Y in space is equal to negative X in the image plane
          // Positive Z in space is equal to negative Y in the image plane
          temp.sumColor(
            CastRay(
              new Ray(camera.eye,
                new Vector3d(cameraPlaneXaxis, eye_y - (x + Rand.nextDouble())*PixelSize, eye_z - (y + Rand.nextDouble())*PixelSize)),0));
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
  ColorDbl CastRay(Ray r, int Bounce){
    if(Bounce>camera.MAX_DEPTH){
      System.out.println("Depth exceeded!");
    }
    //Determine which object is hit
    Object3D HitObject = triangleIntersect(r, Bounce);

    /* TODO: - REPLACE ALL IF-STATEMENTS WITH CALL TO MATERIAL BRDF */
    //If the ray doesn't hit any object, return skybox color in that direction
    if(HitObject == null){
      System.out.println("RenderTask.CastRay(): HitObject == null");
      // return SkyColor(r);
      return new ColorDbl();
    }
    //If the ray hits an emitter(BRDF)
    if(HitObject.mat instanceof Emissive){
      //If it hits front side of light source
      if(util.dot(HitObject.CalculateNormal(r.P_hit),r.direction) < 0){
        return HitObject.mat.getColor();
      }
      //If backside of light source, return black
      return new ColorDbl();
    }

    //If the ray hits a reflective object (BRDF)
    if(HitObject.mat instanceof Reflective && Bounce < camera.MAX_DEPTH){
      //Cast new ray in the "perfect reflection-direction"
      Vector3d d = util.sub(r.P_hit,r.start);
      Vector3d PerfectReflector = util.sub(d, util.scale(r.P_Normal, 2*util.dot(d,r.P_Normal)));
      ColorDbl c = CastRay(new Ray(r.P_hit,util.add(r.P_hit,PerfectReflector)), Bounce+1);
      c.multiply(HitObject.mat.getColor());
      return c;
    }

    //If the ray hits a refractive object (BRDF)
    if(HitObject.mat instanceof Refractive && Bounce < camera.MAX_DEPTH){
      //Whether the object is intersected from the outside or inside
      // determines the order of the refraction indices.
      double n1, n2;
      double costheta = -util.dot(r.direction, r.P_Normal);
      if(costheta > 0.0){
        //Entering the refractive object
        n1 = 1.0;
        n2 = HitObject.mat.getRefractionIndex();
      }else{
        //Leaving the refractive object
        n1 = HitObject.mat.getRefractionIndex();
        n2 = 1.0;
      }

      // TODO Account for total internal reflection
      // Schlick's approximation of the fresnel equation
      double r_zero = (n1-n2)/(n1+n2);
      r_zero *= r_zero;
      double x = (1-costheta);
      double probabilityOfReflection = Math.min(r_zero + (1-r_zero) * x * x * x * x * x, 1.0);
      
      // The total reflectivity represents a statistical probability that a ray is reflected rather than refracted
      Random random = new Random();
      if(random.nextDouble() > probabilityOfReflection){
        // The ray is refracted
        double n = n1/n2;
        double c2 = Math.sqrt( (1-n*n) * (1-costheta*costheta) );
        Vector3d newDir = util.add(util.scale(r.direction, n), util.scale(r.P_Normal, n*costheta-c2)) ;
        ColorDbl c = CastRay(new Ray(r.P_hit,util.add(r.P_hit,newDir)), Bounce+1);
        // TODO It would make more sense to use a volume color
        // (e.g. using Beer's Law) but in this case a simple surface color is used.
        c.multiply(HitObject.mat.getColor());
        return c;
      }else{
        // The ray is reflected (TODO Use a a shared reflect function with Reflective material)
        Vector3d newDir = util.add(r.direction, util.scale(r.P_Normal, 2*costheta));
        ColorDbl c = CastRay(new Ray(r.P_hit,util.add(r.P_hit,newDir)), Bounce+1);
        c.multiply(HitObject.mat.getColor());
        return c;
      }
    }

    ColorDbl glossycolor = new ColorDbl();
    //If the ray hits a glossy object (BRDF)
    if(HitObject.mat instanceof Glossy && Bounce < camera.MAX_DEPTH){
      Random glossy_or_diffuse = new Random();

      if(glossy_or_diffuse.nextDouble() > HitObject.mat.getDiffuseFac()){
        //Calculate reflection, add roughness, reject samples > 180 deg
        Vector3d reflection = util.sub(r.direction, util.scale(r.P_Normal,util.dot(r.direction,r.P_Normal)*2.0));
        Vector3d endPoint = util.add(r.P_hit, util.add(reflection, util.random_unit_vec(HitObject.mat.getRoughness())));

        glossycolor = CastRay(new Ray(r.P_hit,endPoint), Bounce+1);
        glossycolor.multiply(HitObject.mat.getColor());
        return glossycolor;
      }
    }

    //The color of the surface must be initiated for texture calculations
    ColorDbl objectcolor;
    //If the ray hits a textured object (BRDF)
    if(HitObject.mat.texture != null){
      //Determine texture coordinates
      Vector3d nxv0 = util.sub(r.P_hit,HitObject.getVertex(0));
      double numerator_u = util.dot(nxv0,HitObject.getEdge(1));
      double denominator_u = util.norm(nxv0)*util.norm(HitObject.getEdge(1));
      double numerator_v = util.dot(nxv0,HitObject.getEdge(2));
      double denominator_v = util.norm(nxv0)*util.norm(HitObject.getEdge(2));

      double sqrt2ratio = Math.sqrt(2)*util.norm(nxv0)/util.norm(util.sub(HitObject.getVertex(2),HitObject.getVertex(0)));

      double u = Math.max(0,Math.min(1,sqrt2ratio*numerator_u/denominator_u));
      double v = Math.max(0,Math.min(1,sqrt2ratio*numerator_v/denominator_v));

      //Fetch color using calculated texture coordinates (u,v)
      objectcolor = HitObject.mat.getColor(u,v);
    }else{
      //If no texture, get color from material (independent of texture coordinates)
      objectcolor = HitObject.mat.getColor();
    }

    /* --------------------- LOCAL LIGHT MODEL BEGIN ---------------------*/
    ColorDbl DirectLight = new ColorDbl();
    if(camera.SHADOW_RAYS > 0){
      double Brightness = 0.0;
      //Loop through all emitters in scene
      for(Object3D l : scene.lightList){
        //Send shadowray to random position on the surface of the emitter
        Vector3d pointOnLight = l.SampleEmitter(r.P_hit);
        Ray ShadowRay = new Ray(r.P_hit, pointOnLight);
        if(!Occluded(ShadowRay)){
          /*
          Brightness = b * dot(L, Ns) * -dot(L Nl) * 1/d
          b = emitter brightness
          L = light direction
          Ns = surface normal
          Ne = emitter surface normal
          d = distance to emitter sample point (d is clamped to d >= 1.0)
          */

          Brightness += Math.max(0.0, l.mat.getBrightness() * util.dot(ShadowRay.direction, r.P_Normal) * -util.dot(ShadowRay.direction, l.CalculateNormal(pointOnLight)) / Math.max(1.0,ShadowRay.length()));
        }
      }
      //Average brightness over all emitters
      Brightness /= scene.lightList.size();
      DirectLight = ColorDbl.multiply(objectcolor, Brightness); //Multiply incoming light with surface
    }
    double riskOfTermination = (Bounce-1)/((double) camera.MAX_DEPTH);
    if(util.RussianBullet(riskOfTermination) || Bounce >= camera.MAX_DEPTH) {
      // If the ray gets terminated
      return DirectLight;
    }
    /* -------------------- LOCAL LIGHT MODEL END --------------------- */

    // Calculate World2Local and Local2World transformation matrices
    Vector3d Nt, Nb;
    if(Math.abs(r.P_Normal.x) > Math.abs(r.P_Normal.y)){
      Nt = util.scale(new Vector3d(r.P_Normal.z, 0, -r.P_Normal.x), 1.0/Math.sqrt(r.P_Normal.x*r.P_Normal.x+r.P_Normal.z*r.P_Normal.z));
    }
    else{
      Nt = util.scale(new Vector3d(0, -r.P_Normal.z, r.P_Normal.y), 1.0/Math.sqrt(r.P_Normal.y*r.P_Normal.y+r.P_Normal.z*r.P_Normal.z));
    }
    Nb = util.cross(r.P_Normal,Nt);

    //Rotation matrices world <-> local coordinate systems
    Matrix3d world2local = new Matrix3d(
    Nt.x, Nt.y, Nt.z,
    Nb.x, Nb.y, Nb.z,
    r.P_Normal.x, r.P_Normal.y, r.P_Normal.z);
    Matrix3d local2world;
    //Matrix inversion is prone to errors, wrap in try-catch
    try{local2world = util.invertMat(world2local);}
    catch(Exception e){
      //Print error, the render is to be declared a failiure
      System.out.println("RenderTask.CastRay(): matrix could not be inverted.");
      /* To prevent a crash if a matrix inversion for some reason fails, ignore
      inverting and use world2local. */
      local2world = world2local;
    }

    //Cast the reflected ray
    ColorDbl IndirectLight;
    if(Bounce == 0){
      IndirectLight = ColorDbl.avgCol(
        CastRay(new Ray(r.P_hit, util.add(r.P_hit, util.mulMatVec(local2world, util.sampleHemisphere()))), Bounce+1),
        CastRay(new Ray(r.P_hit, util.add(r.P_hit, util.mulMatVec(local2world, util.sampleHemisphere()))), Bounce+1));
    }else{
      IndirectLight = CastRay(new Ray(r.P_hit, util.add(r.P_hit, util.mulMatVec(local2world, util.sampleHemisphere()))), Bounce+1);
    }
    IndirectLight.multiply(objectcolor);
    ColorDbl output = ColorDbl.sumColors(DirectLight, IndirectLight);
    
    //Compensate for the lost energy when rays are randomly terminated. Greater risk
    //of being terminated -> greater magnification of non-terminated rays.
    output.multiply(1/(1-riskOfTermination));
    return output;
  }

  //Calculate where the ray intersects the scene (if it does)
  Object3D triangleIntersect(Ray r, int Bounce){
    double t = 0.0;
    double temp = Double.POSITIVE_INFINITY;
    double NearClip = 0.0; // Clip extremely near intersections
    Object3D hitObject = null;
    if(Bounce==0){
      //Clip objects near camera, when bounce == 0, don't clip for bounces
      NearClip = 1.0;
    }
    //Loop through all objects in the scene (including emitters)
    for (Object3D obj : scene.object3DList){
      //t is the distance to intersection with this object.
      //A negative value of t means the intersection is behind the ray origin.
      //Multiplication by 0.999 ensures we don't end up behind the surface
      t = obj.rayIntersection(r)*0.999999;
      //Check that the intersection is not behind clip and is the closest one.
      if(t > NearClip && t < temp){
        hitObject = obj;
        //temp = nearest intersection so far
        temp = t;
      }
    }
    if(hitObject != null){
      //Calculate the point of intersection
      r.calculatePhit(temp);
      r.P_Normal = hitObject.CalculateNormal(r.P_hit);
    }

    return hitObject;
  }
  
  //Shadow ray occlusion check
  Boolean Occluded(Ray r){
    double t = -1.0;
    for(Object3D obj : scene.object3DList){
      t = obj.rayIntersection(r);
      //If intersection is between origin and emitter (with margin)...
      if(t > 0.0 && t < 0.99999999){
        // ...the emitter is occluded -> return true.
        return true;
      }
    }
    return false;
  }

  //If no object is hit, there is a procedural light box to light the scene
  ColorDbl SkyColor(Ray ray){
    double b_sun = Math.max(0, util.dot(new Vector3d(1,-1,1), ray.direction));
    ColorDbl sun = new ColorDbl(1, 0.9, 0.6);
    sun.multiply(b_sun);
    double b_sky = Math.max(0, util.dot(new Vector3d(0,0,1), ray.direction));
    ColorDbl sky = new ColorDbl(1-(0.3*b_sky), 0.8, 0.5+(0.3*b_sky));
    return ColorDbl.avgCol(sun, sky);
  }

}
