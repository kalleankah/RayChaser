package raychaser;

//The class Material is parent class to all types of materials. If it doesn't
//have a subclass, it's a diffuse material. Some of its member functions are
//dummy functions and are overridden in subclasses.

abstract class Material{
  //Calculate brightness
  double getBrightness(){
    return -1.0;
  }
  //Roughness can only be used with glossy material (overridden in Glossy.java)
  double getRoughness(){
    //Dummy return
    return -1.0;
  }
  //Get color from texture
  ColorDbl getColor(double u, double v){
    return new ColorDbl();
  }
  //Get color from textureless material
  ColorDbl getColor(){
    return new ColorDbl();
  }
  //Overridden in Glossy.java
  double getDiffuseFac(){
    //Dummy return
    return -1.0;
  }
  //Overridden in Glossy.java
  double getRefractionIndex(){
    //Dummy return
    return -1.0;
  }
  //Overridden in Glossy.java
  double getReflectivity(){
    //Dummy return
    return -1.0;
  }
}
