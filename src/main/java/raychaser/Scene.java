package raychaser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.vecmath.Vector3d;

import javafx.scene.image.Image;

// The class Scene contains lists of objects such as walls and boxes, as well as
// light sources. Textures are loaded, materials created and applied to objects.
// Directions are: x = forward, y = left, z = up

public class Scene{
  
  // IT IS OF GRAVE IMPORTANCE TO USE A CONTAINER WITH ASYNCHRONOUS ACCESS
  // SUCH AS ARRAYLIST<>. USING A SYNCHRONIZED CONTAINER LIKE VECTOR<>
  // CAUSES DEVASTATING THREAD BLOCKING!!!
  
  //All objects (Including emissive)
  ArrayList<Object3D> object3DList = new ArrayList<Object3D>();
  //Only emissive objects (light sources)
  ArrayList<Object3D> lightList = new ArrayList<Object3D>();

  //Construct Scene object (in-argument is a list of textures)
  Scene(double brightness){
    ArrayList<Image> textures = loadTextures();
      
    Image block_img = textures.get(0);
    Image wood_img = textures.get(1);
    Image gradient_img = textures.get(2);
    Image wallpaper_img = textures.get(3);
    Image test_img = textures.get(4);
    Image tile_img = textures.get(5);

    //Textured materials
    Textured wood = new Textured(wood_img);
    Textured block = new Textured(block_img);
    Textured gradient = new Textured(gradient_img);
    Textured wallpaper = new Textured(wallpaper_img);
    Textured test = new Textured(test_img);
    Textured tile = new Textured(tile_img);

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
    Diffuse diffuseWhite = new Diffuse(white);
    Diffuse diffuseRed = new Diffuse(red);
    Diffuse diffuseGreen = new Diffuse(green);
    Diffuse diffuseBlue = new Diffuse(blue);
    Diffuse diffuseYellow = new Diffuse(yellow);
    Diffuse diffuseCyan = new Diffuse(cyan);
    Diffuse diffuseMagenta = new Diffuse(magenta);
    
    //Reflective materials
    Reflective reflectiveBright = new Reflective(bright);
    Reflective reflectiveMirror = new Reflective(mirror);

    //Glossy materials
    Glossy glossyWhite = new Glossy(white, 0.05, 0.1);
    Glossy glossyBlue = new Glossy(blue, 0.05, 0.0);
    Glossy glossyBlue2 = new Glossy(blue, 0.05, 0.25);
    Glossy glossyBlue3 = new Glossy(blue, 0.05, 0.5);
    Glossy glossyYellow = new Glossy(yellow, 0.1, 0.4);
    Glossy glossyRed = new Glossy(red, 0.05, 0.5);

    //Refractive materials
    Refractive refractiveGlassCyan = new Refractive(cyan, 1.5);
    Refractive refractiveGlass = new Refractive(bright, 1.5);
    Refractive refractiveWater = new Refractive(bright, 1.3);
    
    //Emmissive materials
    Emissive emissive = new Emissive(new ColorDbl(1.0, 1.0, 1.0), brightness);

    //Vertex points, corners of the room
    double depth = 12.0;
    double width = 12.0;
    double height = 10.01;
    Vector3d CeilingLeftNear = new Vector3d(0.0,width/2.0,height/2.0);
    Vector3d CeilingRightNear = new Vector3d(0.0,-width/2.0,height/2.0);
    Vector3d CeilingLeftFar = new Vector3d(depth,width/2.0,height/2.0);
    Vector3d CeilingRightFar = new Vector3d(depth,-width/2.0,height/2.0);
    Vector3d FloorLeftNear = new Vector3d(0.0,width/2.0,-height/2.0);
    Vector3d FloorRightNear = new Vector3d(0.0,-width/2.0,-height/2.0);
    Vector3d FloorLeftFar = new Vector3d(depth,width/2.0,-height/2.0);
    Vector3d FloorRightFar = new Vector3d(depth,-width/2.0,-height/2.0);

    Vector3d FloorLeftNear2 = new Vector3d(0.0,width/2.0,-height/2.0 + 3.5);
    Vector3d FloorRightNear2 = new Vector3d(0.0,-width/2.0,-height/2.0 + 3.5);
    Vector3d FloorLeftFar2 = new Vector3d(depth,width/2.0,-height/2.0 + 3.5);
    Vector3d FloorRightFar2 = new Vector3d(depth,-width/2.0,-height/2.0 + 3.5);

    //Vertex points ceiling light
    double mediumLampSize = width/4;
    Vector3d medium_CLNR = new Vector3d(depth/2.0 - mediumLampSize, -mediumLampSize, height/2.0);
    Vector3d medium_CLFR = new Vector3d(depth/2.0 + mediumLampSize, -mediumLampSize, height/2.0);
    Vector3d medium_CLFL = new Vector3d(depth/2.0 + mediumLampSize,  mediumLampSize, height/2.0);
    Vector3d medium_CLNL = new Vector3d(depth/2.0 - mediumLampSize,  mediumLampSize, height/2.0);
    double smallLampSize = width/6;
    Vector3d small_CLNR = new Vector3d(depth/2.0 - smallLampSize, -smallLampSize, height/2.0);
    Vector3d small_CLFR = new Vector3d(depth/2.0 + smallLampSize, -smallLampSize, height/2.0);
    Vector3d small_CLFL = new Vector3d(depth/2.0 + smallLampSize,  smallLampSize, height/2.0);
    Vector3d small_CLNL = new Vector3d(depth/2.0 - smallLampSize,  smallLampSize, height/2.0);

    // --- Lamps ---
    // Large
    // addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, emissive));
    // Medium
    addObject(new Plane(medium_CLNL, medium_CLFL, medium_CLFR, medium_CLNR, emissive));
    // Small
    // addObject(new Plane(small_CLNL, small_CLFL, small_CLFR, small_CLNR, emissive));
    // Spherical
    // addObject(new Sphere(new Vector3d(8.0, 0.0, 3.0), 0.5, EMISSION));

    // --- Room ---
    // Ceiling
    addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, diffuseWhite));
    // Floor
    addObject(new Plane(FloorLeftFar,FloorLeftNear,FloorRightNear,FloorRightFar, wood));
    // addObject(new Plane(FloorLeftFar2,FloorLeftNear2,FloorRightNear2,FloorRightFar2, refractiveWater));
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
    addObject(new Sphere(new Vector3d(5.5, -1.1, -4.0), 1.0, refractiveGlass));
    addObject(new Sphere(new Vector3d(7.5, 1.1, -4), 1.0, glossyYellow));
    addObject(new Sphere(new Vector3d(6.1, -3, -4.5), 0.5, glossyRed));
    addObject(new Sphere(new Vector3d(8.0, -4.1, -4.5), 0.5, glossyBlue));
    addObject(new Sphere(new Vector3d(6.7, 3.6, -4.5), 0.5, glossyBlue2));
    addObject(new Sphere(new Vector3d(9.4, 3.1, -4.5), 0.5, glossyBlue3));
    addObject(new Sphere(new Vector3d(8.3, 4.7, -4.5), 0.5, glossyWhite));
    addObject(new Triangle(new Vector3d(9,5,3),new Vector3d(9.7,5.9,-5),new Vector3d(11.9,0.25,-5), reflectiveMirror));

    // Scene for testing caustics
    // double halfside = 0.25;
    // addObject(new Plane(
    //   new Vector3d(7.5-halfside, -0.75, -3+halfside),
    //   new Vector3d(7.5+halfside, -0.75, -3.0+halfside),
    //   new Vector3d(7.5+halfside, -1.0, -3.0-halfside),
    //   emissive));
    // addObject(new Sphere(new Vector3d(6.5, 1.1, -4.0), 1.0, refractiveGlass));
  }
  
  // Add object to the lists
  private void addObject(Object3D o){
    //Add all objects to "object3DList"
    object3DList.add(o);
    //Additionally, if the object is a light source (emissive), add it to "lightList".
    if(o.mat instanceof Emissive){
      lightList.add(o);
    }
  }
  //Add box to the scene
  private void addBox(Vector3d origin, double width, double height, double depth, Material m){
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

  private ArrayList<Image> loadTextures() {
    ArrayList<Image> textures = new ArrayList<>();
    try {
      textures.add(new Image(new FileInputStream("texture/block.png")));
      textures.add(new Image(new FileInputStream("texture/wood.jpg")));
      textures.add(new Image(new FileInputStream("texture/gradient.png")));
      textures.add(new Image(new FileInputStream("texture/wallpaper3k.png")));
      textures.add(new Image(new FileInputStream("texture/test.png")));
      textures.add(new Image(new FileInputStream("texture/tile.jpg")));
    }
    catch(FileNotFoundException e){
      System.out.println("Textures not found in folder \"texture/\"");
      e.printStackTrace();
    }
    return textures;
  }
  
}//Scene ends
