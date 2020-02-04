import javax.vecmath.*;
import java.lang.Math;
import java.util.*;
import java.io.*;
import javafx.scene.image.Image;

// The class Scene contains lists of objects such as walls and boxes, as well as
// light sources. Textures are loaded, materials created and applied to objects.
// Directions are: x = forward, y = left, z = up

public class Scene{
  //List containing all objects (Including emissive objects)
  Vector<Object3D> object3DList = new Vector<Object3D>();
  //List containing only emissive objects (light sources)
  Vector<Object3D> lightList = new Vector<Object3D>();
  Vector<Image> textureList;

  //Construct Scene object (in-argument is a list of textures)
  Scene(Vector<Image> t_List, double brightness){
    textureList = t_List;
    Image block_img = textureList.get(0);
    Image wood_img = textureList.get(1);
    Image gradient_img = textureList.get(2);
    Image wallpaper_img = textureList.get(3);
    Image test_img = textureList.get(4);

    //Textured materials
    Material wood = new Material(wood_img);
    Material block = new Material(block_img);
    Material gradient = new Material(gradient_img);
    Material wallpaper = new Material(wallpaper_img);
    Material test = new Material(test_img);
    //Diffuse materials
    Material white = new Material(new ColorDbl(0.85,0.85,0.85));
    Material red = new Material(new ColorDbl(0.85,0.3,0.3));
    Material green = new Material(new ColorDbl(0.3,0.85,0.3));
    Material blue = new Material(new ColorDbl(0.3,0.3,0.85));
    Material yellow = new Material(new ColorDbl(0.85,0.85,0.3));
    Material cyan = new Material(new ColorDbl(0.3,0.85,0.85));
    Material magenta = new Material(new ColorDbl(0.85,0.3,0.85));
    //Emmissive materials
    Material EMISSION = new Emissive(new ColorDbl(1.0,1.0,1.0), brightness);
    //Reflective materials
    Material Reflective = new Reflective(new ColorDbl(0.99,0.99,0.99));
    Material Mirror = new Reflective(new ColorDbl(0.95,0.99,0.95));
    //Glossy materials
    Material GlossyWhite = new Glossy(new ColorDbl(0.95,0.95,0.95), 0.05);
    Material GlossyBlue = new Glossy(new ColorDbl(0.4,0.4,0.95), 0.1);
    Material GlossyYellow = new Glossy(new ColorDbl(0.95,0.95,0.3), 0.1);
    Material GlossyRed = new Glossy(new ColorDbl(0.98,0.4,0.4), 0.05);

    //Vertex points, corners of the room
    double depth = 12.0;
    double width = 12.0;
    double height = 10.1;
    Vector3d CeilingLeftNear = new Vector3d(0.0,width/2.0,height/2.0);
    Vector3d CeilingRightNear = new Vector3d(0.0,-width/2.0,height/2.0);
    Vector3d CeilingLeftFar = new Vector3d(depth,width/2.0,height/2.0);
    Vector3d CeilingRightFar = new Vector3d(depth,-width/2.0,height/2.0);
    Vector3d FloorLeftNear = new Vector3d(0.0,width/2.0,-height/2.0);
    Vector3d FloorRightNear = new Vector3d(0.0,-width/2.0,-height/2.0);
    Vector3d FloorLeftFar = new Vector3d(depth,width/2.0,-height/2.0);
    Vector3d FloorRightFar = new Vector3d(depth,-width/2.0,-height/2.0);

    //Vertex points ceiling light
    Vector3d CLNR = new Vector3d(4.0,-2.0, height/2.0);
    Vector3d CLFR = new Vector3d(8.0,-2.0, height/2.0);
    Vector3d CLFL = new Vector3d(8.0, 2.0, height/2.0);
    Vector3d CLNL = new Vector3d(4.0, 2.0, height/2.0);

    // Lamp
    addObject(new Plane(CLNL, CLFL, CLFR, CLNR, EMISSION));
    // Entire ceiling Lamp
    // addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, EMISSION));
    // Ceiling
    addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, white));
    // Floor
    addObject(new Plane(FloorLeftFar,FloorLeftNear,FloorRightNear,FloorRightFar, wood));
    // Left wall
    addObject(new Plane(CeilingLeftNear, FloorLeftNear, FloorLeftFar, CeilingLeftFar, green));
    // Right wall
    addObject(new Plane(CeilingRightFar, FloorRightFar, FloorRightNear, CeilingRightNear, red));
    // Far wall
    addObject(new Plane(CeilingLeftFar,FloorLeftFar,FloorRightFar,CeilingRightFar, white));
    // Near wall
    addObject(new Plane(CeilingRightNear,FloorRightNear,FloorLeftNear,CeilingLeftNear, white));


    // Additional items
    addBox(new Vector3d(11, -2.15, -3), 4, 4, 3, yellow);
    addObject(new Sphere(new Vector3d(6.5, -1.1, -4), 1.0, Reflective));
    addObject(new Sphere(new Vector3d(7.5, 1.1, -4), 1.0, GlossyBlue));
    addObject(new Sphere(new Vector3d(6.1, -3, -4.5), 0.5, white));
    addObject(new Sphere(new Vector3d(8.0, -4.1, -4.5), 0.5, Reflective));
    addObject(new Sphere(new Vector3d(6.7, 3.6, -4.5), 0.5, GlossyRed));
    addObject(new Sphere(new Vector3d(9.4, 3.1, -4.5), 0.5, white));
    addObject(new Sphere(new Vector3d(8.3, 4.7, -4.5), 0.5, Reflective));
    addObject(new Triangle(new Vector3d(9,5,3),new Vector3d(9.7,5.9,-5),new Vector3d(11.9,0.25,-5), Mirror));
  }
  //Add object to the lists
  void addObject(Object3D o){
    //Add all objects to "object3DList"
    object3DList.add(o);
    //Additionally, if the object is a light source (emissive), add it to "lightList".
    if(o.mat instanceof Emissive){
      lightList.add(o);
    }
  }
  //Add box to the scene
  void addBox(Vector3d origin, double width, double height, double depth, Material m){
    Vector3d FBL = util.add(origin,new Vector3d(-depth/2,width/2,-height/2));
    Vector3d FBR = util.add(origin,new Vector3d(-depth/2,-width/2,-height/2));
    Vector3d FTL = util.add(origin,new Vector3d(-depth/2,width/2,height/2));
    Vector3d FTR = util.add(origin,new Vector3d(-depth/2,-width/2,height/2));
    Vector3d BBL = util.add(origin,new Vector3d(depth/2,width/2,-height/2));
    Vector3d BBR = util.add(origin,new Vector3d(depth/2,-width/2,-height/2));
    Vector3d BTL = util.add(origin,new Vector3d(depth/2,width/2,height/2));
    Vector3d BTR = util.add(origin,new Vector3d(depth/2,-width/2,height/2));

    addObject(new Plane(FTL,FBL,FBR,FTR,m)); //Front
    addObject(new Plane(BTR,BBR,BBL,BTL,m)); //Back
    addObject(new Plane(BTL,FTL,FTR,BTR,m)); //Top
    addObject(new Plane(FBL,BBL,BBR,FBR,m)); //Bottom
    addObject(new Plane(BTL,BBL,FBL,FTL,m)); //Left
    addObject(new Plane(FTR,FBR,BBR,BTR,m)); //Right
  }

}//Scene ends
