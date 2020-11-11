package raychaser;

import java.util.Vector;

import javax.vecmath.Vector3d;

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

    //Colors
    ColorDbl white = new ColorDbl(0.95,0.95,0.95);
    ColorDbl red = new ColorDbl(0.85,0.3,0.3);
    ColorDbl green = new ColorDbl(0.3,0.85,0.3);
    ColorDbl blue = new ColorDbl(0.3,0.3,0.85);
    ColorDbl yellow = new ColorDbl(0.85,0.85,0.3);
    ColorDbl cyan = new ColorDbl(0.3,0.85,0.85);
    ColorDbl magenta = new ColorDbl(0.85,0.3,0.85);
    
    ColorDbl bright = new ColorDbl(0.99,0.99,0.99);
    ColorDbl mirror = new ColorDbl(0.95,0.99,0.95);

    //Diffuse materials
    Material diffuseWhite = new Material(white);
    Material diffuseRed = new Material(red);
    Material diffuseGreen = new Material(green);
    Material diffuseBlue = new Material(blue);
    Material diffuseYellow = new Material(yellow);
    Material diffuseCyan = new Material(cyan);
    Material diffuseMagenta = new Material(magenta);
    
    //Reflective materials
    Material reflectiveBright = new Reflective(bright);
    Material reflectiveMirror = new Reflective(mirror);

    //Glossy materials
    Material glossyWhite = new Glossy(white, 0.05, 0.1);
    Material glossyBlue = new Glossy(blue, 0.05, 0.0);
    Material glossyBlue2 = new Glossy(blue, 0.05, 0.25);
    Material glossyBlue3 = new Glossy(blue, 0.05, 0.5);
    Material glossyYellow = new Glossy(yellow, 0.1, 0.4);
    Material glossyRed = new Glossy(red, 0.05, 0.5);

    //Refractive materials
    Material refractiveCyan = new Refractive(cyan, 1.5, 0.1);
    Material refractiveBright = new Refractive(bright, 1.5, 0.1);
    
    //Emmissive materials
    Material emissive = new Emissive(new ColorDbl(brightness, brightness, brightness));

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
    double lampSize = 1.5;
    Vector3d CLNR = new Vector3d(depth/2.0 - lampSize, -lampSize, height/2.0);
    Vector3d CLFR = new Vector3d(depth/2.0 + lampSize, -lampSize, height/2.0);
    Vector3d CLFL = new Vector3d(depth/2.0 + lampSize,  lampSize, height/2.0);
    Vector3d CLNL = new Vector3d(depth/2.0 - lampSize,  lampSize, height/2.0);

    // Entire ceiling Lamp
    // addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, EMISSION));
    // Lamp
    addObject(new Plane(CLNL, CLFL, CLFR, CLNR, emissive));
    // addObject(new Sphere(new Vector3d(8.0, 0.0, 3.0), 0.5, EMISSION));
    // Ceiling
    addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, diffuseWhite));
    // Floor
    addObject(new Plane(FloorLeftFar,FloorLeftNear,FloorRightNear,FloorRightFar, wood));
    // Left wall
    addObject(new Plane(CeilingLeftNear, FloorLeftNear, FloorLeftFar, CeilingLeftFar, diffuseGreen));
    // Right wall
    addObject(new Plane(CeilingRightFar, FloorRightFar, FloorRightNear, CeilingRightNear, diffuseRed));
    // Far wall
    addObject(new Plane(CeilingLeftFar,FloorLeftFar,FloorRightFar,CeilingRightFar, diffuseWhite));
    // Near wall
    addObject(new Plane(CeilingRightNear,FloorRightNear,FloorLeftNear,CeilingLeftNear, diffuseWhite));

    // Default items
    addBox(new Vector3d(11, -2.15, -3), 4, 4, 3, diffuseYellow);
    addObject(new Sphere(new Vector3d(6.5, -1.1, -4), 1.0, refractiveBright));
    addObject(new Sphere(new Vector3d(7.5, 1.1, -4), 1.0, glossyYellow));
    addObject(new Sphere(new Vector3d(6.1, -3, -4.5), 0.5, diffuseWhite));
    addObject(new Sphere(new Vector3d(8.0, -4.1, -4.5), 0.5, reflectiveBright));
    addObject(new Sphere(new Vector3d(6.7, 3.6, -4.5), 0.5, glossyRed));
    addObject(new Sphere(new Vector3d(9.4, 3.1, -4.5), 0.5, diffuseWhite));
    addObject(new Sphere(new Vector3d(8.3, 4.7, -4.5), 0.5, reflectiveBright));
    addObject(new Triangle(new Vector3d(9,5,3),new Vector3d(9.7,5.9,-5),new Vector3d(11.9,0.25,-5), reflectiveMirror));

    // Scene for testing caustics
    // addBox(new Vector3d(11, -2.15, -3), 4, 4, 3, yellow);
    double halfside = 0.25;
    // addObject(new Sphere(new Vector3d(6.5, -1.1, -4), 0.7, EMISSION));
    // addObject(new Plane(
    //   new Vector3d(7.5-halfside, -0.85, -3.5+halfside),
    //   new Vector3d(7.5+halfside, -0.85, -3.5+halfside),
    //   new Vector3d(7.5+halfside, -1.0, -3.5-halfside),
    //   EMISSION));
    // addObject(new Plane(
    //   new Vector3d(7.5+halfside*4, 0.5, -3.5+halfside*4),
    // new Vector3d(7.5-halfside*4, 2.0, -3.5+halfside*4),
    // new Vector3d(7.5-halfside*4, 2.0, -3.5-halfside*4),
    // Reflective));
    // addObject(new Sphere(new Vector3d(7.5, 1.1, -4.0), 1.0, Reflective));
    // addObject(new Sphere(new Vector3d(6.1, -3, -4.5), 0.5, white));
    // addObject(new Sphere(new Vector3d(8.0, -4.1, -4.5), 0.5, Reflective));
    // addObject(new Sphere(new Vector3d(6.7, 3.6, -4.5), 0.5, GlossyRed));
    // addObject(new Sphere(new Vector3d(9.4, 3.1, -4.5), 0.5, white));
    // addObject(new Sphere(new Vector3d(8.3, 4.7, -4.5), 0.5, Reflective));
    // addObject(new Triangle(new Vector3d(9,5,3),new Vector3d(9.7,5.9,-5),new Vector3d(11.9,0.25,-5), Mirror));


    //Scene with only glossy spheres
    // addObject(new Sphere(new Vector3d(depth/2.0, width/4.0, -1.0), 1.25, GlossyBlue));
    // addObject(new Sphere(new Vector3d(depth/2.0, 0.0, -1.0), 1.25, GlossyBlue2));
    // addObject(new Sphere(new Vector3d(depth/2.0, -width/4.0, -1.0), 1.25, GlossyBlue3));

    //Scene with one big sphere
    // addObject(new Sphere(new Vector3d(depth/2.0, 0.0, -0.5), 2.5, GlossyYellow));


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
