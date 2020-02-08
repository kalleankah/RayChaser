import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import java.lang.Math;

//The class Material is parent class to all types of materials. If it doesn't
//have a subclass, it's a diffuse material. Some of its member functions are
//dummy functions and are overridden in subclasses.

class Material{
  ColorDbl color;
  double Brightness;
  Image texture;
  int width;
  int height;
  PixelReader pixelreader;

  //Default constructor, black diffuse material
  Material(){
    color = new ColorDbl();
    Brightness = 1.0;
  }
  //Construct from color
  Material(ColorDbl C){
    color = new ColorDbl(C);
    Brightness = 1.0;
  }
  //Construct with texture
  Material(Image t){
    Brightness = 1.0;
    texture = t;
    pixelreader = texture.getPixelReader();
    width = (int)texture.getWidth();
    height = (int)texture.getHeight();
  }
  //Construct with brightness, useful for emitters
  Material(ColorDbl C, double b){
    color = new ColorDbl(C);
    Brightness = b;
  }
  //Copy constructor
  Material(Material m){
    color = m.color;
    Brightness = m.Brightness;
    texture = m.texture;
    width = m.width;
    height = m.height;
    pixelreader = m.pixelreader;
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
  double getDiffuseFac(){
    //Dummy return
    return -1.0;
  }
}
