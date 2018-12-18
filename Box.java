import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Box extends Object3D{
    Vector<Triangle> Triangles = new Vector<Triangle>();
    Box(Vector3d origin, double w, double h, double d ,ColorDbl c){
        super(c);
        Vector3d FBL = new Vector3d(origin);
        FBL.add(new Vector3d(-d/2,-w/2,-h/2));
        Vector3d FBR = new Vector3d(origin);
        FBR.add(new Vector3d(-d/2,w/2,-h/2));
        Vector3d FTL = new Vector3d(origin);
        FTL.add(new Vector3d(-d/2,-w/2,h/2));
        Vector3d FTR = new Vector3d(origin);
        FTR.add(new Vector3d(-d/2,w/2,h/2));

        Vector3d BBL = new Vector3d(origin);
        BBL.add(new Vector3d(d/2,-w/2,-h/2));
        Vector3d BBR = new Vector3d(origin);
        BBR.add(new Vector3d(d/2,w/2,-h/2));
        Vector3d BTL = new Vector3d(origin);
        BTL.add(new Vector3d(d/2,-w/2,h/2));
        Vector3d BTR = new Vector3d(origin);
        BTR.add(new Vector3d(d/2,w/2,h/2));

        //Front
        Triangles.add(new Triangle(FBL,FBR,FTR,c));
        Triangles.add(new Triangle(FBL,FTR,FTL,c));

        //Right
        Triangles.add(new Triangle(FBR,BBR,BTR,c));
        Triangles.add(new Triangle(FBR,BTR,FTR,c));

        //left
        Triangles.add(new Triangle(FBL,FTL,BTL,c));
        Triangles.add(new Triangle(FBL,BTL,BBL,c));

        //Back
        Triangles.add(new Triangle(BBR,BBL,BTL,c));
        Triangles.add(new Triangle(BBR,BTL,BTR,c));

        //Top
        Triangles.add(new Triangle(FTL,FTR,BTR,c));
        Triangles.add(new Triangle(FTL,BTR,BTL,c));

        //Bottom
        Triangles.add(new Triangle(FBR,FBL,BBL,c));
        Triangles.add(new Triangle(FBR,BBL,BBR,c));

    }
    @Override
    double rayIntersection(Ray r){
        double t = Double.POSITIVE_INFINITY;
        double temp = Double.POSITIVE_INFINITY;
        for(Triangle tri : Triangles){
            temp = tri.rayIntersection(r);
            if(temp < t){
                t = temp;
            }
        }
        return t;
    }
}
