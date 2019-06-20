import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class Camera  {
    int Width;
    Vector3d eye;
    int subpixels;
    double fov;
    ColorDbl[][] pixelList;
    Camera(int w, int s, Vector3d e, double f){
        Width = w;
        subpixels = s;
        eye = e;
        fov = f;
        pixelList = new ColorDbl[w][w];
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

        //Create Camera
        Camera c = new Camera(840, Integer.parseInt(args[5]), new Vector3d(-1.0,0.0,0.0),1.25);
        Settings setting = new Settings();
        setting.setChildren(Integer.parseInt(args[0]));
        setting.setDepthDecay(Double.parseDouble(args[1]));
        setting.setShadowRays(Integer.parseInt(args[2]));
        setting.setMaxReflectionBounces(Integer.parseInt(args[3]));
        setting.setMaxDepth(Integer.parseInt(args[4]));

        //Create Scenes for each thread
        int threads = Runtime.getRuntime().availableProcessors();
        System.out.println("Found " + threads + " CPU cores.");
        CountDownLatch latch = new CountDownLatch(threads);
        Scene[] Scenes = new Scene[threads];
        for(int i=0; i<threads; ++i){
            Scenes[i] = new Scene(new Settings(setting), c);
            Scenes[i].addObject(new Sphere(new Vector3d(9.0, -1.1, 0.2), 1.0, new Reflective(new ColorDbl(0.8, 0.8, 0.8))));
            Scenes[i].addObject(new Sphere(new Vector3d(9.0, 1.1, 0.2), 1.0, new Reflective(new ColorDbl(0.8, 0.8, 0.8))));
            Scenes[i].addObject(new Box(new Vector3d(9.0, 0.0, -2.9999), 4.0, 4.0, 4.0, new Material(new ColorDbl(0.9, 0.9, 0.9))));
            Thread T = new Thread(new Multithread(Scenes[i], c.Width/threads, i, latch));
            T.start();
        }

        //Wait for threads
        try{
            latch.await();
        }
        catch(InterruptedException e){}

        //Write file
        c.write("C" + args[0] + "-DD"  + args[1] + "-SR"  + args[2] + "-RB"  + args[3] + "-MD"  + args[4] + "-AA"  + args[5]);

        //Program ends here, set progress to 100%
        long endTime = System.nanoTime();
        //Utilities.updateProgress( 1.0 );
        System.out.println("\nExecution time: " + (endTime-startTime)/1000000000.0+"s");
    }
}
