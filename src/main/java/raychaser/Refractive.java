package raychaser;


//The purpose of the class Glossy is to categorize the material type and enable
//the getRoughness() function.

public class Refractive extends Material {
  double refractionIndex;
  Refractive(ColorDbl col, double refractionIndex){
    super(col);
    this.refractionIndex = refractionIndex;
  }
  //getRoughness() is specific for Glossy material
  @Override
  double getRefractionIndex(){
    return refractionIndex;
  }
}
