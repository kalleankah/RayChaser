import javax.vecmath.*;
public class Light{
  Vector3d center;
  ColorDbl color;
  double Luminance;
  Light(Vector3d cen, ColorDbl col, double lum){
    center = cen;
    color = col;
    Luminance = lum;
  }
}
