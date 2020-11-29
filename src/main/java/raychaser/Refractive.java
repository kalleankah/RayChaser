package raychaser;


//The purpose of the class Glossy is to categorize the material type and enable
//the getRoughness() function.

public class Refractive extends Material {
  private double refractionIndex;
  private ColorDbl color;

  Refractive(ColorDbl c, double refractionIndex){
    color = c;
    this.refractionIndex = refractionIndex;
  }
  //getRoughness() is specific for Glossy material
  @Override
  double getRefractionIndex(){
    return refractionIndex;
  }

  @Override
  ColorDbl getColor() {
    return color;
  }
}
