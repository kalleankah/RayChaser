import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class Main{
  public static void main(String[] args) {

    long startTime = System.nanoTime();
    Camera c = new Camera(500,500);
    Scene s = new Scene();
    Sphere ball = new Sphere(new Vector3d(8.0, -4.0, -2.0), 1.0, new ColorDbl(1.0, 1.0, 1.0));
    Light lamp = new Light(new Vector3d(10.0, 0.0, -3.0), new ColorDbl(1.0, 1.0, 1.0), 1.0,1.0);
    s.addLight(lamp);
    s.addObject(ball);
    /*Tetrahedron T1 = new Tetrahedron(new Vector3d(9.0, -4.0, 3.0), 2.0, new ColorDbl(0.4, 0.7, 0.2));
    Box T2 = new Box(new Vector3d(9.0, 2.0, -4.0), 10.0, 7.0, 4.0, new ColorDbl(1.0, 0.0, 0.3));
    Tetrahedron T3 = new Tetrahedron(new Vector3d(3.0, -0.5, -1.0), 2.0, new ColorDbl(0.7, 0.8, 0.9));
    //s.addObject(T1);
    //s.addObject(T2);
    //s.addObject(T3);*/
    c.createPixels(s, 1); // (Scence, TotalBounces)
    c.render("bild");
    long endTime = System.nanoTime();
    System.out.println("Execution time: " + (endTime-startTime)/1000000000.0+"s");
  }
}
