import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Box extends Object3D{
    Vector<Plane> Planes = new Vector<Plane>();
    Vector3d[][] sides;
    Box(Vector3d origin, double w, double h, double d ,Material m){
        super(m);
        sides = new Vector3d[6][2];
        Vector3d FBL = Utilities.vecAdd(origin,new Vector3d(-d/2,w/2,-h/2));
        Vector3d FBR = Utilities.vecAdd(origin,new Vector3d(-d/2,-w/2,-h/2));
        Vector3d FTL = Utilities.vecAdd(origin,new Vector3d(-d/2,w/2,h/2));
        Vector3d FTR = Utilities.vecAdd(origin,new Vector3d(-d/2,-w/2,h/2));

        Vector3d BBL = Utilities.vecAdd(origin,new Vector3d(d/2,w/2,-h/2));
        Vector3d BBR = Utilities.vecAdd(origin,new Vector3d(d/2,-w/2,-h/2));
        Vector3d BTL = Utilities.vecAdd(origin,new Vector3d(d/2,w/2,h/2));
        Vector3d BTR = Utilities.vecAdd(origin,new Vector3d(d/2,-w/2,h/2));

        //Front
        Planes.add(new Plane(FBL,FBR,FTR,m));
        sides[0][0] = Utilities.vecAdd(FBL,Utilities.vecScale(Utilities.vecSub(FTR, FBL),0.5));
        sides[0][1] = Planes.get(0).CalculateNormal();
        //Right
        Planes.add(new Plane(FBR,BBR,BTR,m));
        sides[1][0] = Utilities.vecAdd(FBR,Utilities.vecScale(Utilities.vecSub(BTR, FBR),0.5));
        sides[1][1] = Planes.get(1).CalculateNormal();

        //left
        Planes.add(new Plane(FBL,FTL,BTL,m));
        sides[2][0] = Utilities.vecAdd(FBL,Utilities.vecScale(Utilities.vecSub(BTL, FBL),0.5));
        sides[2][1] = Planes.get(2).CalculateNormal();
        //Back
        Planes.add(new Plane(BBR,BBL,BTL,m));
        sides[3][0] = Utilities.vecAdd(BBR,Utilities.vecScale(Utilities.vecSub(BTL, BBR),0.5));
        sides[3][1] = Planes.get(3).CalculateNormal();

        //Top
        Planes.add(new Plane(FTL,FTR,BTR,m));
        sides[4][0] = Utilities.vecAdd(FTL,Utilities.vecScale(Utilities.vecSub(BTR, FTL),0.5));
        sides[4][1] = Planes.get(4).CalculateNormal();

        //Bottom
        Planes.add(new Plane(FBR,FBL,BBL,m));
        sides[5][0] = Utilities.vecAdd(FBR,Utilities.vecScale(Utilities.vecSub(BBL, FBR),0.5));
        sides[5][1] = Planes.get(5).CalculateNormal();

    }
    @Override
    double rayIntersection(Ray r){
        double t = Double.POSITIVE_INFINITY;
        double temp = Double.POSITIVE_INFINITY;
        for(Plane tri : Planes){
            temp = tri.rayIntersection(r);
            if(temp < t){
                t = temp;
            }
        }
        return t;
    }
    @Override
    Vector3d CalculateNormal(Vector3d P){
        double temp = Double.POSITIVE_INFINITY;
        Vector3d norm = new Vector3d();
        for(int i = 0; i<6; i++){
            if(Utilities.vecNorm(Utilities.vecSub(sides[i][0], P)) < temp){
                temp = Utilities.vecNorm(Utilities.vecSub(sides[i][0], P));
                norm = sides[i][1];
            }

        }
        return norm;
    }
    @Override
    Vector3d CalculateNormal(){
        return null;
    }
}
