import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
public class Camera  {
    int Width;
    Vector3d eye;
    ColorDbl[][] pixelList;
    Camera(int w, Vector3d e){
        Width = w;
        eye = e;
        pixelList = new ColorDbl[w][w];
    }
    //Start rendering scene S
    void render(Scene S){
        long endTime;
        double PixelSize = 2.0/Width;
        Vector3d endPoint;
        Ray r;
        for(int j = 0; j < Width; ++j){
            for(int i = 0; i < Width; ++i){
                endPoint = new Vector3d(0.0,0.5*PixelSize+i*PixelSize-1, 1-0.5*PixelSize-j*PixelSize );
                r = new Ray(eye, endPoint, true);
                ColorDbl temp = r.CastRay(S,0,0);
                temp.clamp();
                pixelList[i][j] = temp;
            }
            Utilities.updateProgress( (double) j/Width);
        }
    }
    //Write data to a PNG
    void write(String filename){
        File file = null;
        BufferedImage img = new BufferedImage(Width,Width, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < Width; y++){
            for(int x = 0; x < Width; x++){
                int rgb = pixelList[x][y].RGBForImage();
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

        //Create Scene and Camera
        Settings setting = new Settings();
        setting.setMaxDepth(1);
        setting.setChildren(16);
        setting.setDepthDecay(0.3);
        setting.setShadowRays(8);
        setting.setMaxReflectionBounces(8);
        Scene s = new Scene(setting);
        Camera c = new Camera(500, new Vector3d(-1.0,0.0,0.0));

        //Add objects to scene
        Sphere ball1 = new Sphere(new Vector3d(10.0, 3.0, 0.0), 1.0, new Reflective(new ColorDbl(1.0, 0.0, 0.0)));
        Sphere ball2 = new Sphere(new Vector3d(5.0, -2.0, 3.75), 1.0, new Reflective(new ColorDbl(0.25, 0.25, 0.75)));
        s.addObject(ball1);
        s.addObject(ball2);
        Tetrahedron T1 = new Tetrahedron(new Vector3d(9.0, -4.0, 3.0), 2.0, new Material(new ColorDbl(1.0, 0.0, 0.0)));
        //Box T2 = new Box(new Vector3d(9.0, 2.0, -4.0), 10.0, 7.0, 4.0, new Material(new ColorDbl(0.4, 1.0, 0.2)));
        Tetrahedron T3 = new Tetrahedron(new Vector3d(6.0, 2.0, -5.0), 2.0, new Material(new ColorDbl(0.4, 0.7, 1.0)));
        s.addObject(T1);
        //s.addObject(T2);
        s.addObject(T3);

        //Start rendering
        c.render(s);
        c.write("bild");

        //Program ends here, set progress to 100%
        long endTime = System.nanoTime();
        Utilities.updateProgress( 1.0 );
        System.out.println("\nExecution time: " + (endTime-startTime)/1000000000.0+"s");
    }
}
