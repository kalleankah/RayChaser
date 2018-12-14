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
        double PixelSize = 2.0/Width;
        Vector3d endPoint;
        Ray r;
        Pixel p;
        for(int j = 0; j < Height; ++j){
            for(int i = 0; i < Width; ++i){
                endPoint= new Vector3d(0.0,1-0.5*PixelSize-i*PixelSize, 1-0.5*PixelSize-j*PixelSize );
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
            writer.println("P3");
            writer.println(Width + " " + Height);
            writer.println("255");
            for(int j = 0; j < Height; ++j){
              for(int i = 0; i < Width; ++i){
                writer.print(pixelList.elementAt(i+j*Width  ).color.printForImage());
                if(i != Width-1){
                  writer.print(" ");
                }
              }
              if(j != Height-1){
              writer.print("\n");
            }
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
        s.addTetrahedron(new Vector3d(9.0, 0.0, -2.0), 2.0, new ColorDbl(0.4, 0.7, 0.2));
        s.addTetrahedron(new Vector3d(9.0, 4.0, -4.0), 4.0, new ColorDbl(0.6, 0.7, 0.3));
        s.addTetrahedron(new Vector3d(3.0, -0.5, -2.0), 2.0, new ColorDbl(0.7, 0.8, 0.9));
        c.createPixels(s);
        c.render("bild.ppm");

    }
}
