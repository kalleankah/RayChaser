import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
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
    //Start rendering scene S
    void render(Scene S){
        double PixelSize = 2.0/Width;
        double subPixelSize = PixelSize/subpixels;
        double halfSubPixel = 0.5*subPixelSize;
        double subPixelFactor = 1.0/(subpixels*subpixels);
        Vector3d endPoint;
        Ray r;
        ColorDbl temp;

        for(int j = 0; j < Width; ++j){
            Utilities.updateProgress( (double) j/Width); //adds 2-3 seconds to all renders
            for(int i = 0; i < Width; ++i){
                temp = new ColorDbl();
                for(int k = 0; k<subpixels; ++k){
                    for(int l = 0; l<subpixels; ++l){
                        endPoint = new Vector3d(eye.x+fov, -i*PixelSize - halfSubPixel-k*subPixelSize + 1 + eye.y, -j*PixelSize - halfSubPixel-l*subPixelSize + 1 + eye.z);
                        r = new Ray(eye, endPoint, true);
                        temp.sumColor(r.CastRay(S,0,0));
                    }
                }
                temp.multiply(subPixelFactor);
                temp.clamp();
                pixelList[i][j] = temp;
            }
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
        setting.setChildren(Integer.parseInt(args[0]));
        setting.setDepthDecay(Double.parseDouble(args[1]));
        setting.setShadowRays(Integer.parseInt(args[2]));
        setting.setMaxReflectionBounces(Integer.parseInt(args[3]));
        setting.setMaxDepth(Integer.parseInt(args[4]));
        Scene s = new Scene(setting);
        Camera c = new Camera(1080, Integer.parseInt(args[5]), new Vector3d(-1.0,0.0,0.0),5.0);

        //Add objects to scene
        s.addObject(new Sphere(new Vector3d(9.0, -1.1, 0.2), 1.0, new Reflective(new ColorDbl(0.8, 0.8, 0.8))));
        s.addObject(new Sphere(new Vector3d(9.0, 1.1, 0.2), 1.0, new Reflective(new ColorDbl(0.8, 0.8, 0.8))));
        s.addObject(new Box(new Vector3d(9.0, 0.0, -2.9999), 4.0, 4.0, 4.0, new Material(new ColorDbl(0.9, 0.9, 0.9))));

        //Start rendering
        c.render(s);
        c.write("C" + args[0] + "-DD"  + args[1] + "-SR"  + args[2] + "-RB"  + args[3] + "-MD"  + args[4] + "-AA"  + args[5]);

        //Program ends here, set progress to 100%
        long endTime = System.nanoTime();
        Utilities.updateProgress( 1.0 );
        System.out.println("\nExecution time: " + (endTime-startTime)/1000000000.0+"s");
    }
}
