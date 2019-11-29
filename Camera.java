import javafx.scene.image.WritableImage;
import javax.vecmath.Vector3d;

public class Camera {
   Vector3d eye;
   int Width, Height;
   double fov;
   int subpixels;
   WritableImage image;
   int THREADS;
   int MAX_DEPTH = 5;
   int SHADOW_RAYS = 1;
   int MAX_REFLECTION_BOUNCES = 10;
   double DEPTH_DECAY = 0;

   Camera(Vector3d e, double f, WritableImage img, int[] args){
      Width = args[0];
      Height = args[0];
      subpixels = args[1];
      MAX_DEPTH = args[2];
      MAX_REFLECTION_BOUNCES = args[3];
      SHADOW_RAYS = args[4];
      THREADS = args[5];
      eye = e;
      fov = f;
      image = img;
   }

}
