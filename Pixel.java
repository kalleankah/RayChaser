import java.util.*;
import javax.vecmath.Vector3d;

public class Pixel{
    Vector<Ray> rayList= new Vector<Ray>();
    ColorDbl color = new ColorDbl();
    Pixel(){}
    Pixel(ColorDbl Cd){
        color = Cd;
    }
    void setColor(ColorDbl Cd){
        color = Cd;
    }
    void addRay(Ray r){
        rayList.add(r);
    }
    void setColorDoubleFromRayList(){
        double _r = 0.0;
        double _g = 0.0;
        double _b = 0.0;
        double Brightness = 1.0;


        for(Ray ray : rayList){
            if(ray.inShadow){
                Brightness = 0.5;
            }
            else{
                Brightness = 1.0;
            }
            _r += ray.rayColor.R*Brightness;
            _g += ray.rayColor.G*Brightness;
            _b += ray.rayColor.B*Brightness;
        }
        color.setColor( _r/rayList.size(), _g/rayList.size(), _b/rayList.size() );
    }
    public static void main(String[] args) {
        Pixel p1 = new Pixel();
        ColorDbl C1 = new ColorDbl(1.0,0.5,0.1);
        ColorDbl C2 = new ColorDbl(0.3,0.2,0.1);
        Pixel p2 = new Pixel(C1);
        p1.color.print();
        p2.color.print();
        p2.setColor(C2);
        p2.color.print();

        /*Ray r1 = new Ray(v1,v2);
        r1.rayColor.print();
        r1.rayColor.setColor(C2);  // this method it throws a NullPointerException on line 22
        p1.addRay(r1);
        p1.setColorDoubleFromRayList();
        p1.color.print();*/

    }
}
