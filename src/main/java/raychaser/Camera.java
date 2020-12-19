package raychaser;

import javafx.scene.image.WritableImage;
import javax.vecmath.Vector3d;

/*The class Camera contains render settings and the WritableImage that's being
rendered to. The Camera object is passed to each RenderTask. */

public class Camera {
  WritableImage image;
  Vector3d eye;
  int Width, Height;
  int samples;
  int THREADS;
  int MAX_DEPTH;
  Boolean SHADOW_RAYS;
  double Brightness;
  double fov;
  double aspectRatio;
  double shift_H;
  double shift_V;
  
  Camera(){}

  public void setSize(int w, int h){
    Width = w;
    Height = h;

    //Center the camera when aspect ratio is other than 1:1
    aspectRatio = ((double) Width)/((double) Height);
    if(aspectRatio>1){
      shift_V = 1.0/aspectRatio-1.0;
      shift_H = 0.0;
    }
    else if(aspectRatio<1){
      shift_H = aspectRatio-1.0;
      shift_V = 0.0;
    }
    else{
      shift_H = 0.0;
      shift_V = 0.0;
    }
  }
  
  public void setBrightness(double b){
    Brightness = b;
  }

  public void setEye(double z){
    eye = new Vector3d(-0.5, 0.0, z);
  }

  public void setSamples(int s){
    samples = s;
  }

  public void setDepth(int d){
    MAX_DEPTH = d;
  }

  public void setShadowRays(Boolean sr){
    SHADOW_RAYS = sr;
  }

  public void setThreads(int t){
    THREADS = t;
  }

  public void setFov(double f){
    fov = f;
  }

  public void setImg(WritableImage img){
    image = img;
  }
}