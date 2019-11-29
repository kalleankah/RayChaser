import javafx.concurrent.Task;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

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

   //Start rendering scene S
   @Override
   protected Void call(){
      double PixelSize = 2.0/camera.Width;
      double subPixelSize = PixelSize/camera.subpixels;
      double halfSubPixel = 0.5*subPixelSize;
      double subPixelFactor = 1.0/(camera.subpixels*camera.subpixels);
      double cameraPlaneXaxis = camera.eye.x+camera.fov;
      ColorDbl temp = new ColorDbl();
      int endX = startX+range;
      int endY = startY+range;

      //Render the box [startX->endX, startY->endY]
      // The xy-loop is a loop over all pixels x*y
      for(int y = startY; y < endY; ++y){
         for(int x = startX; x < endX; ++x){
            temp.R = temp.G = temp.B = 0.0;
            // The loop ij is a loop over all subpixels (samples) i*j on each pixel
            for(int i = 0; i<camera.subpixels; ++i){
               for(int j = 0; j<camera.subpixels; ++j){
                  temp.sumColor(CastRay(new Ray(camera.eye, new Vector3d(cameraPlaneXaxis, -x*PixelSize - halfSubPixel-i*subPixelSize + 1 + camera.eye.y, -y*PixelSize - halfSubPixel-j*subPixelSize + 1 + camera.eye.z), true),0,0));
               }
            }
            temp.multiply(subPixelFactor);
            temp.clamp();
            camera.image.getPixelWriter().setArgb(x,y,temp.ARGBForImage());
         }
         progress.incrementAndGet();
      }

      return null;
   }

   //Cast a ray into the scene
   ColorDbl CastRay(Ray r, int Depth, int ReflectionDepth){
      Object3D HitObject = triangleIntersect(r);
      //If the ray doesn't hit any object
      if(HitObject == null){
         return SkyColor(r);
      }
      //If the ray hits an emitter
      if(HitObject.mat instanceof Emissive){
         if(Utilities.vecDot(HitObject.CalculateNormal(r.P_hit),r.direction) < 0){
            ColorDbl returncolor = new ColorDbl(HitObject.mat.getColor());
            returncolor.multiply(HitObject.mat.Brightness);
            return returncolor;
         }
         return new ColorDbl(); //If backside of light source
      }
      //If the ray hits a reflective object
      if(HitObject.mat instanceof Reflective){
         if(ReflectionDepth >= camera.MAX_REFLECTION_BOUNCES) {return new ColorDbl();}
         Vector3d d = Utilities.vecSub(r.P_hit,r.start);
         Vector3d PerfectReflector = Utilities.vecSub(d, Utilities.vecScale(r.P_Normal, 2*Utilities.vecDot(d,r.P_Normal)));
         ColorDbl c = CastRay(new Ray(r.P_hit,Utilities.vecAdd(r.P_hit,PerfectReflector), false), Depth, ReflectionDepth+1);
         c.multiply(HitObject.mat.getColor());
         return c;
      }
      ColorDbl glossycolor = new ColorDbl();
      ColorDbl glossyscale = new ColorDbl(1,1,1);
      //If the ray a glossy object
      if(HitObject.mat instanceof Glossy){
          Random Rand = new Random();
          Vector3d N = HitObject.CalculateNormal(r.P_hit);
          double roughness = HitObject.mat.getRoughness();
          //Calculate orthogonal vector to N
          Vector3d ortho_vec1 = Utilities.vecAdd(N,new Vector3d(1,0,0));
          double dot = Utilities.vecDot(ortho_vec1,N);
          if(dot<0.000001){
             ortho_vec1 = Utilities.vecAdd(N,new Vector3d(0,1,0));
             dot = Utilities.vecDot(ortho_vec1,N);
          }
          ortho_vec1 = Utilities.vecSub(ortho_vec1, Utilities.vecScale(N,dot));
          ortho_vec1.normalize();
          //Calculate second orthogonal vector
          Vector3d ortho_vec2 = Utilities.vecCross(N,ortho_vec1);
          //Generate two random numbers around 0 to offset normal with
          ortho_vec1 = Utilities.vecScale(ortho_vec1, Rand.nextDouble()*roughness - roughness/2);
          ortho_vec2 = Utilities.vecScale(ortho_vec2, Rand.nextDouble()*roughness - roughness/2);
          Vector3d offset = Utilities.vecAdd(ortho_vec1,ortho_vec2);
          //Offset normal
          Vector3d new_N = Utilities.vecAdd(N,offset);
          new_N.normalize();
          //Reflect the ray
          Vector3d to_phit = Utilities.vecSub(r.P_hit,r.start);
          Vector3d PerfectReflector = Utilities.vecSub(to_phit, Utilities.vecScale(new_N, 2*Utilities.vecDot(to_phit,new_N)));
          Vector3d endPoint = Utilities.vecAdd(r.P_hit,PerfectReflector);
          Vector3d newRayDir = Utilities.vecSub(endPoint,r.P_hit);
          glossycolor = CastRay(new Ray(r.P_hit,endPoint, false), Depth+1, ReflectionDepth);
          //Calculate the color
          glossycolor.multiply(HitObject.mat.color);
          glossycolor.multiply(0.1);
          glossyscale.multiply(0.9);
      }

      ColorDbl objectcolor; //The color of the surface
      //If the ray hits a textured object
      if(HitObject.mat.texture != null){
         Vector3d nxv0 = Utilities.vecSub(r.P_hit,HitObject.getVertex(0));
         double numerator_u = Utilities.vecDot(nxv0,HitObject.getEdge(1));
         double denominator_u = Utilities.vecNorm(nxv0)*Utilities.vecNorm(HitObject.getEdge(1));
         double numerator_v = Utilities.vecDot(nxv0,HitObject.getEdge(2));
         double denominator_v = Utilities.vecNorm(nxv0)*Utilities.vecNorm(HitObject.getEdge(2));

         double sqrt2ratio = Math.sqrt(2)*Utilities.vecNorm(nxv0)/Utilities.vecNorm(Utilities.vecSub(HitObject.getVertex(2),HitObject.getVertex(0)));

         double u = Math.max(0,Math.min(1,sqrt2ratio*numerator_u/denominator_u));
         double v = Math.max(0,Math.min(1,sqrt2ratio*numerator_v/denominator_v));

         objectcolor = HitObject.mat.getColor(u,v);
      }else{
         objectcolor = HitObject.mat.getColor();
      }

      ColorDbl DirectLightcontrib = new ColorDbl();

      // If the ray hits a diffuse object
      // If using shadow rays
      if(camera.SHADOW_RAYS > 0){
         /* --------------------- LOCAL LIGHT MODEL BEGIN ---------------------*/
         double Brightness = 0.0;
         double TotalBrightness = 0.0;

         for(Object3D l : scene.lightList){
            Vector<Vector3d> SampList = l.getSampleLight(camera.SHADOW_RAYS, r.P_hit); //Find samples on light source
            Brightness = 0.0;
            //Loop over all shadow rays
            for(Vector3d pos : SampList){
               Ray ShadowRay = new Ray(r.P_hit, pos, false);
               if(!ObjectHit(ShadowRay)){
                  //Lambertian model
                  Brightness +=  Math.max(0.0, l.mat.Brightness * Utilities.vecDot(ShadowRay.direction, r.P_Normal) * -Utilities.vecDot(ShadowRay.direction, l.CalculateNormal())/Math.max(1.0,ShadowRay.RayLength));
               }
            }
            Brightness /= SampList.size(); //Average brightness over all samples
            TotalBrightness += Brightness;
         }
         TotalBrightness /= scene.lightList.size(); //Average brightness over all lights
         DirectLightcontrib = ColorDbl.multiply(objectcolor, TotalBrightness); //Multiply incoming light with surface

         //If max depth reached, return without casting more rays
         if(Depth >= camera.MAX_DEPTH) {
            return DirectLightcontrib;
         }
         /* -------------------- LOCAL LIGHT MODEL END --------------------- */
      }
      // If not using shadow rays
      else{
         if(Depth>=camera.MAX_DEPTH){return new ColorDbl();}
      }

      // Calculate World2Local and Local2World Matrices
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
      try{local2world = Utilities.invertMat(world2local);}
      catch(Exception e){
         System.out.println("The matrix could not be inverted.");
         local2world = world2local;
      }

      //Sample hemisphere uniformly
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

   Object3D triangleIntersect(Ray r){
      double t = 0.0;
      double temp = Double.POSITIVE_INFINITY;
      double NearClip = 0.0;
      Object3D hitObject = null;
      if(r.MotherNode){
         NearClip = 1.0;
      }
      for (Object3D obj : scene.object3DList){
         //t = distance to intersection with obj in "RayLength-units"
         t = obj.rayIntersection(r)-0.00000001;
         if(t > NearClip  && t < temp){
            hitObject = obj;
            //temp = nearest intersection
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
            if(t > 0.0000000001 && t < 0.9999999){
               return true;
            }
         }
      }
      return false;
   }
   //Skybox lighting
   ColorDbl SkyColor(Ray ray){
      double b_sun = Math.max(0, Utilities.vecDot(new Vector3d(1,-1,1), ray.direction));
      ColorDbl sun = new ColorDbl(1, 0.9, 0.6);
      sun.multiply(b_sun);
      double b_sky = Math.max(0, Utilities.vecDot(new Vector3d(0,0,1), ray.direction));
      ColorDbl sky = new ColorDbl(1-(0.3*b_sky), 0.8, 0.5+(0.3*b_sky));
      return Utilities.avgCol(sun, sky);
   }

}
