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
        eye1 = new Vector3d(-1,0.0,0.0);
        eye2 = new Vector3d(-2.0,0.0,0.0);
    }
    //Start rendering scene S
    void createPixels(Scene S){
        long endTime;
        double PixelSize = 2.0/Width;
        Vector3d endPoint;
        Ray r;
        Pixel p;
        for(int j = 0; j < Height; ++j){
            for(int i = 0; i < Width; ++i){
                endPoint= new Vector3d(0.0,0.5*PixelSize+i*PixelSize-1, 1-0.5*PixelSize-j*PixelSize );
                r = new Ray(eye1, endPoint, true);
                ColorDbl temp = r.CastRay(S,0);
                temp.clamp();
                pixelList.add(temp);
                p = null;
            }
            updateProgress( (double) j/Height);
        }
    }

    static void updateProgress(double progressPercentage) {
        final int width = 50; // progress bar width in chars

        System.out.print("\r[");
        int i = 0;
        for (; i <= (int)(progressPercentage*width); i++) {
          System.out.print(".");
        }
        for (; i < width; i++) {
          System.out.print(" ");
        }
        System.out.print("]" + (int)(progressPercentage*100) + "%");
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
        Sphere ball1 = new Sphere(new Vector3d(5.0, 0.0, -3.75), 1.0, new Material(new ColorDbl(1.0, 1.0, 1.0),true));
        Sphere ball2 = new Sphere(new Vector3d(5.0, -2.0, 3.75), 1.0, new Material(new ColorDbl(1.0, 1.0, 1.0),true));
        //s.addObject(ball1);
        //s.addObject(ball2);
        Tetrahedron T1 = new Tetrahedron(new Vector3d(9.0, -4.0, 3.0), 2.0, new Material(new ColorDbl(1.0, 0.0, 0.0)));
        //Box T2 = new Box(new Vector3d(9.0, 2.0, -4.0), 10.0, 7.0, 4.0, new Material(new ColorDbl(0.4, 1.0, 0.2)));
        Tetrahedron T3 = new Tetrahedron(new Vector3d(6.0, 2.0, -3.0), 2.0, new Material(new ColorDbl(0.4, 0.7, 1.0)));
        s.addObject(T1);
        //s.addObject(T2);
        s.addObject(T3);
        c.createPixels(s);
        c.render("bild");
        long endTime = System.nanoTime();
        updateProgress( 1.0 ); //Program ends here, set progress to 100%
        System.out.println("\nExecution time: " + (endTime-startTime)/1000000000.0+"s");
    }
}
