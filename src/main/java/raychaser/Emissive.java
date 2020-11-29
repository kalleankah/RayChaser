package raychaser;

//The purpose of Emissive class is to categorize it as emissive and inherit
//the member functions from Material.java

public class Emissive extends Material{
  private final double brightness;
  private final ColorDbl color;
  
  Emissive(ColorDbl c, double b){
    color = c;
    brightness = b;
  }

  @Override
  ColorDbl getColor() {
    return color;
  }

  @Override
  double getBrightness() {
    return brightness;
  }
}
