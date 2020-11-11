package raychaser;


//The purpose of the class Glossy is to categorize the material type and enable
//the getRoughness() function.

public class Refractive extends Material {
  double refractionIndex, reflectivity;
  Refractive(ColorDbl col, double refract, double reflect){
    super(col);
    refractionIndex = refract;
    reflectivity = reflect;
  }
  //getRoughness() is specific for Glossy material
  @Override
  double getRefractionIndex(){
    return refractionIndex;
  }
  @Override
  double getReflectivity(){
    return reflectivity;
  }
}
