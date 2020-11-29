package raychaser;

//The purpose of Emissive class is to categorize it as emissive and inherit
//the member functions from Material.java

public class Reflective extends Material{
  private ColorDbl color;

  Reflective(ColorDbl c){
    color = c;
  }
  
  @Override
  ColorDbl getColor() {
    return color;
  }
}
