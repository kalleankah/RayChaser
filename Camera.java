import javax.vecmath.Vector3d;
import java.io.*;
import java.util.*;
public class Camera  {
    int Width;
    int Height;
    Vector3d eye1;
    Vector3d eye2;
    Vector<Pixel> pixelList= new Vector<Pixel>();
    Camera(int w, int h){
        Width = w;
        Height = h;
        eye1 = new Vector3d(-1.0,0.0,0.0);
        eye2 = new Vector3d(-2.0,0.0,0.0);
    }
    void createPixels(Scene S){
        double PixelSize = 2/Width;
        Vector3d endPoint;
        Ray r;
        Pixel p;
        for(int j = 0; j < Height; ++j){
            for(int i = 0; i < Width; ++i){
                endPoint= new Vector3d(0.0,i*PixelSize-1+0.5*PixelSize,j*PixelSize-1+0.5*PixelSize );
                r = new Ray(eye1, endPoint);
                S.triangleIntersect(r);
                p = new Pixel();
                p.addRay(r);
                p.setColorDoubleFromRayList();
                pixelList.add(p);
            }
        }
    }
    void render(String filename){
        File file = new File(filename);
        try{
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("P6");
            writer.println(Width + " " + Height);
            writer.println("255");
            for(int i = 0; i < Width*Height; ++i){
                writer.print(pixelList.elementAt(i).color.printForImage());
            }
            writer.close();
        }
        catch(IOException e){
            System.out.println(e);
        }

    }
    void print(){

    }
    public static void main(String[] args) throws IOException{
        Camera c = new Camera(800,800);
        Scene s = new Scene();
        c.createPixels(s);
        c.render("bild.ppm");

    }
}
