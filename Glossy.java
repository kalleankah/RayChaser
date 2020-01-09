//The purpose of the class Glossy is to categorize the material type and enable
//the getRoughness() function.

public class Glossy extends Material {
  double roughness;
  Glossy(ColorDbl col, double coeff){
    super(col);
    roughness = coeff;
  }
  //getRoughness() is specific for Glossy material
  @Override
  double getRoughness(){
    return roughness;
  }
}
