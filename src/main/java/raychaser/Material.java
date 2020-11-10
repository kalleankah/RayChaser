package raychaser;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import java.lang.Math;

//The class Material is parent class to all types of materials. If it doesn't
//have a subclass, it's a diffuse material. Some of its member functions are
//dummy functions and are overridden in subclasses.

class Material{
  ColorDbl color;
  Image texture;
  int width;
  int height;
  PixelReader pixelreader;

  //Default constructor, black diffuse material
  Material(){
    color = new ColorDbl();
  }
  //Construct from color
  Material(ColorDbl C){
    color = new ColorDbl(C);
  }
  //Construct with texture
  Material(Image t){
    texture = t;
    pixelreader = texture.getPixelReader();
    width = (int)texture.getWidth();
    height = (int)texture.getHeight();
  }
  //Copy constructor
  Material(Material m){
    color = m.color;
    texture = m.texture;
    width = m.width;
    height = m.height;
    pixelreader = m.pixelreader;
  }
  //Calculate brightness
  double getBrightness(){
    return Math.sqrt(color.R*color.R + color.G*color.G + color.B*color.B);
  }
  //Roughness can only be used with glossy material (overridden in Glossy.java)
  double getRoughness(){
    //Dummy return
    return -1.0;
  }
  //Get color from texture
  ColorDbl getColor(double u, double v){
    int x = (int) (u*(width-1.0));
    int y = (int) (v*(height-1.0));
    return ColorDbl.argbToColorDbl(pixelreader.getArgb(x, y));
  }
  //Get color from textureless material
  ColorDbl getColor(){
    return new ColorDbl(color);
  }
  //Overridden in Glossy.java
  double getDiffuseFac(){
    //Dummy return
    return -1.0;
  }
}
