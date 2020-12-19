package raychaser;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import javafx.concurrent.Task;

public class RenderTask extends Task<Void> {
  Scene scene;
  Camera camera;
  int startX;
  int startY;
  int range;
  AtomicInteger progress;
  ThreadLocalRandom R;

  RenderTask(Scene s, Camera c, int sx, int sy, int r, AtomicInteger p){
    scene = s;
    camera = c;
    startX = sx;
    startY = sy;
    range = r;
    progress = p;
    R = ThreadLocalRandom.current();
  }

  @Override
  protected Void call() throws Exception {
    //PixelSize is the length a pixel has in the grid from -1 to 1
    double PixelSize = camera.Width>camera.Height ? 2.0/camera.Width : 2.0/camera.Height;
    double subPixelFactor = 1.0/(camera.samples);
    double cameraPlaneXaxis = camera.eye.x+camera.fov;
    ColorDbl temp = new ColorDbl();
    int endX = Math.min(startX+range, camera.Width);
    int endY = Math.min(startY+range, camera.Height);
    double eye_y = camera.eye.y + camera.shift_H + 1.0;
    double eye_z = camera.eye.z + camera.shift_V + 1.0;

    // //Render the box [startX->endX, startY->endY]
    // // The xy-loop is a loop over all pixels x*y
    // for(int y = startY; y < endY; ++y){
    //   for(int x = startX; x < endX; ++x){
    //     temp.R = temp.G = temp.B = 0.0;
    //     // Loop over all subpixels (samples) on each pixel
    //     for(int i = 0; i<camera.samples; ++i){
    //       // Positive Y in space is equal to negative X in the image plane
    //       // Positive Z in space is equal to negative Y in the image plane
    //       temp.sumColor(
    //         Trace(
    //           new Ray(camera.eye,
    //             util.sub(new Vector3d(cameraPlaneXaxis, eye_y - (x + R.nextDouble())*PixelSize, eye_z - (y + R.nextDouble())*PixelSize), camera.eye) 
    //       )));
    //     }
    //     temp.multiply(subPixelFactor);
    //     //Correct gamma for display
    //     // temp.gammaCorrection();
    //     temp.clamp();
    //     //Write color to the current pixel in the image
    //     camera.image.getPixelWriter().setArgb(x,y,temp.ARGBForImage());
    //   }
      
    //   //Increment the atomic integer to report progress from this thread
    //   progress.incrementAndGet();
    // }

    // -------------- ORDERED GRID SAMPLING ---------------
    //Render the box [startX->endX, startY->endY]
    // The xy-loop is a loop over all pixels x*y
    double subPixelFactorSquared = subPixelFactor*subPixelFactor;
    double subPixelSize = PixelSize/camera.samples;
    for(int y = startY; y < endY; ++y){
      for(int x = startX; x < endX; ++x){
        temp.R = temp.G = temp.B = 0.0;
        // Loop over all subpixels (samples) on each pixel
        for(int i = 0; i<camera.samples; ++i){
          for(int j = 0; j<camera.samples; ++j){
            temp.sumColor(
              Trace(
                new Ray(camera.eye,
                  util.sub(new Vector3d(cameraPlaneXaxis, eye_y - (x*PixelSize + i*subPixelSize), eye_z - (y*PixelSize + j*subPixelSize)), camera.eye) 
            )));
          }
        }
        temp.multiply(subPixelFactorSquared);
        //Correct gamma for display
        // temp.gammaCorrection();
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
  ColorDbl Trace(Ray r){
    ColorDbl clr = new ColorDbl();
    ColorDbl attenuation = new ColorDbl(1.0, 1.0, 1.0);

    for(int Bounce = 0; Bounce<camera.MAX_DEPTH; ++Bounce){
      //Determine which object is hit
      Object3D HitObject = intersect(r, Bounce);
      if(HitObject == null){
        clr.sumColor(ColorDbl.multiply(attenuation, SkyColor(r)));
        break;
      }

      //If the ray hits a textured object (BRDF)
      if(HitObject.mat instanceof Textured){
        //Determine texture coordinates
        Vector3d diagonal = util.sub(r.surfacePoint,HitObject.getVertex(0));
        //The u-coordinate is the length of the diagonal along edge 2 compared to the length of edge 2
        // double u = util.dot(diagonal,util.normalize(HitObject.getEdge(2)))/util.norm(HitObject.getEdge(2));
        double u = diagonal.dot(util.normalize(HitObject.getEdge(2)))/HitObject.getEdge(2).length();
        //The v-coordinate is the length of the diagonal along edge 1 compared to the length of edge 1
        double v = diagonal.dot(util.normalize(HitObject.getEdge(1)))/HitObject.getEdge(1).length();

        //Clamp uv coordinates
        u = Math.max(0.0,Math.min(1.0,u));
        v = Math.max(0.0,Math.min(1.0,v));

        //Fetch color using calculated texture coordinates (u,v)
        attenuation.multiply(HitObject.mat.getColor(u,v));
      }
      else{
        //Not textured
        attenuation.multiply(HitObject.mat.getColor());
      }

      // Perform russian roulette more on paths likely to contribute less
      double probability = Math.min(1.0, attenuation.getMaxIntensity());
      if(R.nextDouble() < probability){
        // Compensate for the other rays that are terminated
        attenuation.divide(probability);
      }
      else{
        // The ray gets terminated
        break;
      }
      
      //If the ray hits an emitter(BRDF)
      if(HitObject.mat instanceof Emissive){
        //If it hits front side of light source
        if(r.direction.dot(HitObject.CalculateNormal(r.surfacePoint)) < 0){
          clr.sumColor(ColorDbl.multiply(attenuation, HitObject.mat.getBrightness()));
        }
        //Tracing ends at light source
        break;
      }
      
      //If diffuse reflection
      if(HitObject.mat instanceof Diffuse || HitObject.mat instanceof Textured){
        r = diffuseBRDF(r);
      }

      //If the ray hits a refractive object (BRDF)
    if(HitObject.mat instanceof Refractive){
      //Whether the object is intersected from the outside or inside
      // determines the order of the refraction indices.
      double n1, n2;
      double costheta = Math.min(1.0, r.direction.dot(r.surfaceNormal));
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
      if(R.nextDouble() > probabilityOfReflection){
        // The ray is refracted
        // Vector3d newDir = util.add(util.scale(r.direction, n), util.scale(correctedNormal, n*costheta-Math.sqrt(c2)));
        Vector3d newDir = util.scale(r.direction, n);
        newDir.scaleAdd(n*costheta-Math.sqrt(c2), correctedNormal, newDir);
        
        // The ray is slightly pushed behind the surface to prevent self intersection
        // Vector3d correctedRayOrigin = util.add(r.surfacePoint, util.scale(correctedNormal, -0.001));
        Vector3d correctedRayOrigin = new Vector3d();
        correctedRayOrigin.scaleAdd(-0.001, correctedNormal, r.surfacePoint);

        r = new Ray(correctedRayOrigin, newDir);
      }else{
        // The ray is slightly pushed out to prevent self intersection
        // Vector3d correctedRayOrigin = util.add(r.surfacePoint, util.scale(correctedNormal, 0.001));
        Vector3d correctedRayOrigin = new Vector3d();
        correctedRayOrigin.scaleAdd(0.001, correctedNormal, r.surfacePoint);
        
        Vector3d newDir = reflect(r.direction, correctedNormal);
        r = new Ray(correctedRayOrigin, newDir);
      }
    }

      //If the ray hits a reflective object (BRDF)
      if(HitObject.mat instanceof Reflective){
        //Cast new ray in the "perfect reflection-direction"
        Vector3d reflectDir = reflect(r.direction, r.surfaceNormal);
        r = new Ray(r.surfacePoint, reflectDir);
      }

      //If the ray hits a glossy object (BRDF)
      if(HitObject.mat instanceof Glossy && Bounce < camera.MAX_DEPTH){
        if(R.nextDouble() > HitObject.mat.getDiffuseFac()){
          //Calculate reflection, add roughness
          //Some samples could be > 180 deg 
          Vector3d reflection;
          do{
            reflection = reflect(r.direction, r.surfaceNormal);
            reflection.add(util.random_unit_vec(HitObject.mat.getRoughness()));
          }
          while(util.vecFacing(r.surfaceNormal, reflection));

          r = new Ray(r.surfacePoint, reflection);
        }
        else{
          r = diffuseBRDF(r);
        }
      }
    }
    return clr;
  } //CastRay()

  Vector3d reflect(Vector3d direction, Vector3d normal) {
    Vector3d temp = new Vector3d(normal);
    temp.scale(2*direction.dot(normal));
    temp.sub(direction, temp);
    // return util.sub(direction, util.scale(normal, 2*direction.dot(normal)));
    return temp;
  }

  // Calculate where the ray intersects the scene (if it does)
  Object3D intersect(Ray r, int Bounce){
    double t = 0.0;
    double temp = Double.POSITIVE_INFINITY;
    double NearClip = 0.0; // Clip intersections behind camera
    Object3D hitObject = null;
    if(Bounce==0){
      //Clip objects near camera before first bounce
      NearClip = camera.fov;
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
    double b_sun = Math.max(0, ray.direction.dot(new Vector3d(1,-1,1)));
    ColorDbl sun = new ColorDbl(1, 0.9, 0.6);
    sun.multiply(b_sun);
    double b_sky = Math.max(0, ray.direction.dot(new Vector3d(0,0,1)));
    ColorDbl sky = new ColorDbl(1-(0.3*b_sky), 0.8, 0.5+(0.3*b_sky));
    return ColorDbl.avgCol(sun, sky);
  }

  //Diffuse BRDF
  Ray diffuseBRDF(Ray r){
    //Use the diffuse BRDF
    //Choose a new ray direction
    Vector3d Nt, Nb;
    if(Math.abs(r.surfaceNormal.x) > Math.abs(r.surfaceNormal.y)){
      Nt = util.scale(new Vector3d(r.surfaceNormal.z, 0, -r.surfaceNormal.x), 1.0/Math.sqrt(r.surfaceNormal.x*r.surfaceNormal.x+r.surfaceNormal.z*r.surfaceNormal.z));
    }
    else{
      Nt = util.scale(new Vector3d(0, -r.surfaceNormal.z, r.surfaceNormal.y), 1.0/Math.sqrt(r.surfaceNormal.y*r.surfaceNormal.y+r.surfaceNormal.z*r.surfaceNormal.z));
    }
    Nb = util.cross(r.surfaceNormal,Nt);

    //Rotation matrices world <-> local coordinate systems
    Matrix3d local2world = new Matrix3d(
    Nt.x, Nt.y, Nt.z,
    Nb.x, Nb.y, Nb.z,
    r.surfaceNormal.x, r.surfaceNormal.y, r.surfaceNormal.z);
    local2world.invert();

    //Assign new ray direction
    return new Ray(r.surfacePoint, util.mulMatVec(local2world, util.sampleHemisphere()));
  }
}