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
    @Override
    Vector3d CalculateNormal(Vector3d P){
        for(Triangle tri : Triangles){
            double PlaneValue = tri.normal.x*(P.x-tri.vertex0.x) + tri.normal.y*(P.y-tri.vertex0.y) + tri.normal.z*(P.z-tri.vertex0.z);
            if(Math.abs(PlaneValue) < 0.0000001)
            {
                double Area = Utilities.vecNorm(Utilities.vecCross(tri.edge1,tri.edge2))/2.0;
                Vector3d PA = Utilities.vecSub(tri.vertex0, P);
                Vector3d PB = Utilities.vecSub(tri.vertex1, P);
                Vector3d PC = Utilities.vecSub(tri.vertex2, P);
                double alpha = Utilities.vecNorm(Utilities.vecCross(PB,PC))/(2.0*Area);
                double beta = Utilities.vecNorm(Utilities.vecCross(PC,PA))/(2.0*Area);
                double gamma = 1 - alpha - beta;
                if(alpha >= 0.0 && alpha <= 1.0 && beta >= 0.0 && beta <= 1.0 && gamma >= 0.0 && gamma <= 1.0){
                    return tri.normal;
                }
            }
        }
        System.out.println("CalculateNormal for box returned null");
        return null;
    }
}
