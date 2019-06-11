import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
public class Camera  {
    int Width;
    int Height;
    Vector3d eye1;
    Vector3d eye2;
    Vector<ColorDbl> pixelList= new Vector<ColorDbl>();
    Camera(int w, int h){
        Width = w;
        Height = h;
        eye1 = new Vector3d(-1.0,0.0,0.0);
        eye2 = new Vector3d(-2.0,0.0,0.0);
    }
    void createPixels(Scene S){
        double PixelSize = 2.0/Width;
        Vector3d endPoint;
        Ray r;
        Pixel p;
        for(int j = 0; j < Height; ++j){
            for(int i = 0; i < Width; ++i){
                endPoint= new Vector3d(0.0,0.5*PixelSize+i*PixelSize-1, 1-0.5*PixelSize-j*PixelSize );
                r = new Ray(eye1, endPoint, true);
                S.triangleIntersect(r, 1);
                p = new Pixel();
                p.addRay(r);
                p.setColorDoubleFromRayList();
                pixelList.add(p.color);
                p = null;
            }
        }
    }
    void render(String filename){
        File file = null;
        BufferedImage img = new BufferedImage(Width,Height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < Height; y++){
            for(int x = 0; x < Width; x++){
                int rgb = pixelList.elementAt(x+y*Width).RGBForImage();
                img.setRGB(x,y,rgb);
            }
        }
        try{
            file = new File(filename + ".png");
            ImageIO.write(img,"png",file);
        }
        catch(IOException e){
            System.out.println("Error: " +e );
        }

    }
    public static void main(String[] args) throws IOException{
        long startTime = System.nanoTime();
        Camera c = new Camera(500,500);
        Scene s = new Scene();
        Sphere ball = new Sphere(new Vector3d(8.0, -4.0, -2.0), 1.0, new ColorDbl(1.0, 1.0, 1.0));
        Light lamp = new Light(new Vector3d(10.0, 0.0, -3.0), new ColorDbl(1.0, 1.0, 1.0), 1.0,1.0);
        s.addLight(lamp);
        s.addObject(ball);
        Tetrahedron T1 = new Tetrahedron(new Vector3d(9.0, -4.0, 3.0), 2.0, new ColorDbl(0.4, 0.7, 0.2));
        Box T2 = new Box(new Vector3d(9.0, 2.0, -4.0), 10.0, 7.0, 4.0, new ColorDbl(1.0, 0.0, 0.3));
        Tetrahedron T3 = new Tetrahedron(new Vector3d(3.0, -0.5, -1.0), 2.0, new ColorDbl(0.7, 0.8, 0.9));
        //s.addObject(T1);
        //s.addObject(T2);
        //s.addObject(T3);
        c.createPixels(s);
        c.render("bild");
        long endTime = System.nanoTime();
        System.out.println("Execution time: " + (endTime-startTime)/1000000000.0+"s");
    }
}
