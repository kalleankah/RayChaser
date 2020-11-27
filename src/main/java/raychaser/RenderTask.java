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
    Object3D HitObject = intersect(r, Bounce);

    /* TODO: - REPLACE ALL IF-STATEMENTS WITH CALL TO MATERIAL BRDF */
    //If the ray doesn't hit any object, return skybox color in that direction
    if(HitObject == null){
      // System.out.println("RenderTask.CastRay(): HitObject == null");
      // return SkyColor(r);
      return new ColorDbl();
    }
    //If the ray hits an emitter(BRDF)
    if(HitObject.mat instanceof Emissive){
      //If it hits front side of light source
      if(util.dot(HitObject.CalculateNormal(r.surfacePoint),r.direction) < 0){
        return HitObject.mat.getColor();
      }
      //If backside of light source, return black
      return new ColorDbl();
    }

    //If the ray hits a reflective object (BRDF)
    if(HitObject.mat instanceof Reflective && Bounce < camera.MAX_DEPTH){
      //Cast new ray in the "perfect reflection-direction"
      Vector3d reflectDir = reflect(r.direction, r.surfaceNormal);
      ColorDbl c = CastRay(new Ray(r.surfacePoint,util.add(r.surfacePoint,reflectDir)), Bounce+1);
      c.multiply(HitObject.mat.getColor());
      return c;
    }

    //If the ray hits a refractive object (BRDF)
    if(HitObject.mat instanceof Refractive && Bounce < camera.MAX_DEPTH){
      //Whether the object is intersected from the outside or inside
      // determines the order of the refraction indices.
      double n1, n2;
      double costheta = Math.min(1.0, util.dot(r.direction, r.surfaceNormal));
      Vector3d correctedNormal = r.surfaceNormal;
      if(costheta < 0.0){
        //Hitting the outside of the refractive object
        n1 = 1.0;
        n2 = HitObject.mat.getRefractionIndex();
        costheta *= -1.0;
      }else{
        //Hitting the inside of the refractive object
        n1 = HitObject.mat.getRefractionIndex();
        n2 = 1.0;
        correctedNormal = util.scale(r.surfaceNormal, -1.0);
      }

      // Schlick's approximation of the fresnel equation
      double probabilityOfReflection;
      double r_zero = (n1-n2)/(n1+n2);
      r_zero *= r_zero;
      //Check for total internal reflection
      double n = n1/n2;
      double c2 = 1 - n*n * (1-costheta*costheta);
      if(c2 < 0.0){
        //Total internal reflection
        probabilityOfReflection = 1.0;
      }
      else{
        double x = Math.max(0.0, 1-costheta);
        probabilityOfReflection = Math.min(r_zero + (1-r_zero) * x * x * x * x * x, 1.0);
      }
      
      // The total reflectivity represents a statistical probability that a ray is reflected rather than refracted
      ColorDbl c;
      Random random = new Random();
      if(random.nextDouble() > probabilityOfReflection){
        // The ray is refracted
        Vector3d newDir = util.add(util.scale(r.direction, n), util.scale(correctedNormal, n*costheta-Math.sqrt(c2)));
        // The ray is slightly pushed behind the surface to prevent self intersection
        Vector3d correctedRayOrigin = util.add(r.surfacePoint, util.scale(correctedNormal, -0.001));
        
        c = CastRay(new Ray(correctedRayOrigin,util.add(correctedRayOrigin,newDir)), Bounce+1);
      }else{
        // The ray is reflected (TODO Use a a shared reflect function with Reflective material)
        // The ray is slightly pushed out to prevent self intersection
        Vector3d correctedRayOrigin = util.add(r.surfacePoint, util.scale(correctedNormal, 0.001));
        
        Vector3d newDir = reflect(r.direction, correctedNormal);
        c = CastRay(new Ray(correctedRayOrigin,util.add(correctedRayOrigin,newDir)), Bounce+1);
      }

      // TODO It would make more sense to use a volume color
        // (e.g. using Beer's Law) but in this case a simple surface color is used.
        c.multiply(HitObject.mat.getColor());
        return c;
    }

    ColorDbl glossycolor = new ColorDbl();
    //If the ray hits a glossy object (BRDF)
    if(HitObject.mat instanceof Glossy && Bounce < camera.MAX_DEPTH){
      Random glossy_or_diffuse = new Random();

      if(glossy_or_diffuse.nextDouble() > HitObject.mat.getDiffuseFac()){
        //Calculate reflection, add roughness, reject samples > 180 deg
        Vector3d reflection = reflect(r.direction, r.surfaceNormal);
        Vector3d endPoint = util.add(r.surfacePoint, util.add(reflection, util.random_unit_vec(HitObject.mat.getRoughness())));

        glossycolor = CastRay(new Ray(r.surfacePoint,endPoint), Bounce+1);
        glossycolor.multiply(HitObject.mat.getColor());
        return glossycolor;
      }
    }

    //The color of the surface must be initiated for texture calculations
    ColorDbl objectcolor;
    //If the ray hits a textured object (BRDF)
    if(HitObject.mat.texture != null){
      //Determine texture coordinates
      Vector3d diagonal = util.sub(r.surfacePoint,HitObject.getVertex(0));
      //The u-coordinate is the length of the diagonal along edge 2 compared to the length of edge 2
      double u = util.dot(diagonal,util.normalize(HitObject.getEdge(2)))/util.norm(HitObject.getEdge(2));
      //The v-coordinate is the length of the diagonal along edge 1 compared to the length of edge 1
      double v = util.dot(diagonal,util.normalize(HitObject.getEdge(1)))/util.norm(HitObject.getEdge(1));

      //Clamp uv coordinates
      u = Math.max(0.0,Math.min(1.0,u));
      v = Math.max(0.0,Math.min(1.0,v));

      //Fetch color using calculated texture coordinates (u,v)
      objectcolor = HitObject.mat.getColor(u,v);
    }else{
      //If no texture, get color from material (independent of texture coordinates)
      objectcolor = HitObject.mat.getColor();
    }

    /* --------------------- LOCAL LIGHT MODEL BEGIN ---------------------*/
    ColorDbl DirectLight = new ColorDbl();
    if(camera.SHADOW_RAYS){
      double Brightness = 0.0;
      //Loop through all emitters in scene
      for(Object3D l : scene.lightList){
        //Send shadowray to random position on the surface of the emitter
        Vector3d pointOnLight = l.SampleEmitter(r.surfacePoint);
        Ray ShadowRay = new Ray(r.surfacePoint, pointOnLight);
        if(!Occluded(ShadowRay)){
          /*
          Brightness = b * dot(L, Ns) * -dot(L Nl) * 1/d
          b = emitter brightness
          L = light direction
          Ns = surface normal
          Ne = emitter surface normal
          d = distance to emitter sample point (d is clamped to d >= 1.0)
          */

          Brightness += Math.max(0.0, l.mat.getBrightness() * util.dot(ShadowRay.direction, r.surfaceNormal) * -util.dot(ShadowRay.direction, l.CalculateNormal(pointOnLight)) / Math.max(1.0,ShadowRay.length()));
        }
      }
      //Average brightness over all emitters
      Brightness /= scene.lightList.size();
      DirectLight = ColorDbl.multiply(objectcolor, Brightness); //Multiply incoming light with surface
    }
    // double riskOfTermination = Math.min(1.0, (Bounce)/((double) camera.MAX_DEPTH));
    double riskOfTermination = 1-Math.min(1.0, objectcolor.getMaxIntensity());
    if(util.RussianBullet(riskOfTermination) || Bounce >= camera.MAX_DEPTH) {
      // If the ray gets terminated
      return DirectLight;
    }
    /* -------------------- LOCAL LIGHT MODEL END --------------------- */

    // Calculate World2Local and Local2World transformation matrices
    Vector3d Nt, Nb;
    if(Math.abs(r.surfaceNormal.x) > Math.abs(r.surfaceNormal.y)){
      Nt = util.scale(new Vector3d(r.surfaceNormal.z, 0, -r.surfaceNormal.x), 1.0/Math.sqrt(r.surfaceNormal.x*r.surfaceNormal.x+r.surfaceNormal.z*r.surfaceNormal.z));
    }
    else{
      Nt = util.scale(new Vector3d(0, -r.surfaceNormal.z, r.surfaceNormal.y), 1.0/Math.sqrt(r.surfaceNormal.y*r.surfaceNormal.y+r.surfaceNormal.z*r.surfaceNormal.z));
    }
    Nb = util.cross(r.surfaceNormal,Nt);

    //Rotation matrices world <-> local coordinate systems
    Matrix3d world2local = new Matrix3d(
    Nt.x, Nt.y, Nt.z,
    Nb.x, Nb.y, Nb.z,
    r.surfaceNormal.x, r.surfaceNormal.y, r.surfaceNormal.z);
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
    ColorDbl IndirectLight = CastRay(new Ray(r.surfacePoint, util.add(r.surfacePoint, util.mulMatVec(local2world, util.sampleHemisphere()))), Bounce+1);
    IndirectLight.multiply(objectcolor);
    ColorDbl output = ColorDbl.sumColors(DirectLight, IndirectLight);
    
    //Compensate for the lost energy when rays are randomly terminated. Greater risk
    //of being terminated -> greater magnification of non-terminated rays.
    output.divide((1-riskOfTermination));
    return output;
  }

  private Vector3d reflect(Vector3d direction, Vector3d normal) {
    return util.sub(direction, util.scale(normal, 2*util.dot(direction,normal)));
  }

  // Calculate where the ray intersects the scene (if it does)
  Object3D intersect(Ray r, int Bounce){
    double t = 0.0;
    double temp = Double.POSITIVE_INFINITY;
    double NearClip = 0.0; // Clip intersections behind camera
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
      r.calculateSurfacePoint(temp);
      r.surfaceNormal = hitObject.CalculateNormal(r.surfacePoint);
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
