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
    void createPixels(Scene S, int TotalBounces){
        double PixelSize = 2.0/Width;
        Vector3d endPoint;
        Ray r;
        Pixel p;
        for(int j = 0; j < Height; ++j){
            for(int i = 0; i < Width; ++i){
                endPoint= new Vector3d(0.0,0.5*PixelSize+i*PixelSize-1, 1-0.5*PixelSize-j*PixelSize );
                r = new Ray(eye1, endPoint, true);
                S.triangleIntersect(r, TotalBounces);
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

    }
}
