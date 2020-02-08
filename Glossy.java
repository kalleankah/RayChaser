//The purpose of the class Glossy is to categorize the material type and enable
//the getRoughness() function.

public class Glossy extends Material {
  double roughness, diffuseFac;
  Glossy(ColorDbl col, double coeff, double diffuse){
    super(col);
    roughness = coeff;
    diffuseFac = diffuse;
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
