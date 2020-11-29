package raychaser;

public class Diffuse extends Material {
  ColorDbl color;
  
  Diffuse(ColorDbl c){
    color = c;
  }

  @Override
  ColorDbl getColor() {
    return color;
  }
}
