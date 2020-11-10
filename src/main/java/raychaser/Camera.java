package raychaser;

import javafx.scene.image.WritableImage;
import javax.vecmath.Vector3d;

/*The class Camera contains render settings and the WritableImage that's being
rendered to. The Camera object is passed to each RenderTask. */

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
  double aspectRatio;
  double shift_H = 0.0;
  double shift_V = 0.0;

  Camera(Vector3d e, double f, WritableImage img, int[] args){
    Width = args[0];
    Height = args[1];
    subpixels = args[2];
    MAX_DEPTH = args[3];
    MAX_REFLECTION_BOUNCES = args[4];
    SHADOW_RAYS = args[5];
    THREADS = args[6];
    fov = f;
    eye = e;
    image = img;

    //Center the camera when aspect ratio is other than 1:1
    aspectRatio = ((double) Width)/((double) Height);
    if(aspectRatio>1){
      shift_V = 1.0/aspectRatio-1.0;
    }
    if(aspectRatio<1){
      shift_H = aspectRatio-1.0;
    }
  }

}
