import javax.vecmath.*;
import java.lang.Math;
import java.util.*;
import java.io.*;
import javafx.scene.image.Image;

public class Scene{
   Vector<Object3D> object3DList = new Vector<Object3D>();
   Vector<Object3D> lightList = new Vector<Object3D>();
   Vector<Image> textureList;
   Scene(Vector<Image> t_List){
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
      Material EMISSION = new Emissive(new ColorDbl(1.0,1.0,1.0), 1);
      Material EMISSION2 = new Emissive(new ColorDbl(1.0,1.0,1.0), 5);
      //Reflective materials
      Material Reflective = new Reflective(new ColorDbl(0.99,0.99,0.99));
      Material Mirror = new Reflective(new ColorDbl(0.95,0.99,0.95));
      //Glossy materials
      Material GlossyWhite = new Glossy(new ColorDbl(0.95,0.95,0.95), 0.04);
      Material GlossyBlue = new Glossy(new ColorDbl(0.3,0.3,0.95), 0.04);
      Material GlossyYellow = new Glossy(new ColorDbl(0.95,0.95,0.3), 0.04);

      //Vertex points, corners of the room
      Vector3d CeilingLeftNear = new Vector3d(0.0,6.0,5.0);
      Vector3d CeilingRightNear = new Vector3d(0.0,-6.0,5.0);
      Vector3d CeilingLeftFar = new Vector3d(12.0,6.0,5.0);
      Vector3d CeilingRightFar = new Vector3d(12.0,-6.0,5.0);
      Vector3d FloorLeftNear = new Vector3d(0.0,6.0,-5.0);
      Vector3d FloorRightNear = new Vector3d(0.0,-6.0,-5.0);
      Vector3d FloorLeftFar = new Vector3d(12.0,6.0,-5.0);
      Vector3d FloorRightFar = new Vector3d(12.0,-6.0,-5.0);

      //Vertex points ceiling light
      Vector3d CLNR = new Vector3d(4.0,-2.0, 4);
      Vector3d CLFR = new Vector3d(8.0,-2.0, 4);
      Vector3d CLFL = new Vector3d(8.0, 2.0, 4);
      Vector3d CLNL = new Vector3d(4.0, 2.0, 4);

      // Lamp
      // addObject(new Plane(CLNL, CLFL, CLFR, CLNR, EMISSION2));
      // Ceiling
      addObject(new Plane(CeilingLeftNear, CeilingLeftFar, CeilingRightFar, CeilingRightNear, EMISSION));
      // Floor
      addObject(new Plane(FloorLeftFar,FloorLeftNear,FloorRightNear,FloorRightFar, wood));
      // Left wall
      addObject(new Plane(CeilingLeftNear, FloorLeftNear, FloorLeftFar, CeilingLeftFar, white));
      // Right wall
      addObject(new Plane(CeilingRightFar, FloorRightFar, FloorRightNear, CeilingRightNear, white));
      // Far wall
      addObject(new Plane(CeilingLeftFar,FloorLeftFar,FloorRightFar,CeilingRightFar, white));
      // Near wall
      addObject(new Plane(CeilingRightNear,FloorRightNear,FloorLeftNear,CeilingLeftNear, white));
      // Mirror
      addObject(new Plane(new Vector3d(9.7,5.9,3),new Vector3d(9.7,5.9,-5),new Vector3d(11.9,0.25,-5), Mirror));

      // Additional items
      addBox(new Vector3d(11, -2.15, -3), 4, 4, 3, yellow);
      addObject(new Sphere(new Vector3d(6.5, -1.1, -4), 1.0, Reflective));
      addObject(new Sphere(new Vector3d(7.5, 1.1, -4), 1.0, GlossyBlue));
      addObject(new Sphere(new Vector3d(6.1, -3, -4.5), 0.5, white));
      addObject(new Sphere(new Vector3d(8.0, -4.1, -4.5), 0.5, Reflective));
      addObject(new Sphere(new Vector3d(6.7, 3.6, -4.5), 0.5, Reflective));
      addObject(new Sphere(new Vector3d(9.4, 3.1, -4.5), 0.5, white));
      addObject(new Sphere(new Vector3d(8.3, 4.7, -4.5), 0.5, Reflective));
   }
   //Add primitive object to the scene
   void addObject(Object3D o){
      object3DList.add(o);
      if(o.mat instanceof Emissive){
         lightList.add(o);
      }
   }
   //Add a box to the scene
   void addBox(Vector3d origin, double width, double height, double depth, Material m){
      Vector3d FBL = Utilities.vecAdd(origin,new Vector3d(-depth/2,width/2,-height/2));
      Vector3d FBR = Utilities.vecAdd(origin,new Vector3d(-depth/2,-width/2,-height/2));
      Vector3d FTL = Utilities.vecAdd(origin,new Vector3d(-depth/2,width/2,height/2));
      Vector3d FTR = Utilities.vecAdd(origin,new Vector3d(-depth/2,-width/2,height/2));
      Vector3d BBL = Utilities.vecAdd(origin,new Vector3d(depth/2,width/2,-height/2));
      Vector3d BBR = Utilities.vecAdd(origin,new Vector3d(depth/2,-width/2,-height/2));
      Vector3d BTL = Utilities.vecAdd(origin,new Vector3d(depth/2,width/2,height/2));
      Vector3d BTR = Utilities.vecAdd(origin,new Vector3d(depth/2,-width/2,height/2));

      addObject(new Plane(FTL,FBL,FBR,FTR,m)); //Front
      addObject(new Plane(BTR,BBR,BBL,BTL,m)); //Back
      addObject(new Plane(BTL,FTL,FTR,BTR,m)); //Top
      addObject(new Plane(FBL,BBL,BBR,FBR,m)); //Bottom
      addObject(new Plane(BTL,BBL,FBL,FTL,m)); //Left
      addObject(new Plane(FTR,FBR,BBR,BTR,m)); //Right
   }

}//Scene ends
