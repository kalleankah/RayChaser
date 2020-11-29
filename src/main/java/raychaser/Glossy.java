package raychaser;

//The purpose of the class Glossy is to categorize the material type and enable
//the getRoughness() function.

public class Glossy extends Material {
  double roughness, diffuseFac;
  ColorDbl color;

  Glossy(ColorDbl c, double rough, double diffuse){
    color = c;
    roughness = rough;
    diffuseFac = diffuse;
  }
  
  @Override
  ColorDbl getColor() {
    return color;
  }

  //getRoughness() is specific for Glossy material
  @Override
  double getRoughness(){
    return roughness;
  }

  //getDiffuseFac is specific for Glossy material
  @Override
  double getDiffuseFac(){
    return diffuseFac;
  }
}
