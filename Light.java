import javax.vecmath.*;
public class Light{
  Vector3d position;
  ColorDbl color;
  double Luminance;
  double Brightness;
  Light(Vector3d pos, ColorDbl col, double lum, double b){

    position = new Vector3d(pos);
    color = col;
    Luminance = lum;
    Brightness = b;
  }
}
