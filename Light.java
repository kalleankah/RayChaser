import javax.vecmath.*;
public class Light{
  Vector3d position;
  ColorDbl color;
  double Brightness;
  Light(Vector3d pos, ColorDbl col,double b){

    position = new Vector3d(pos);
    color = col;
    Brightness = b;
  }
}
