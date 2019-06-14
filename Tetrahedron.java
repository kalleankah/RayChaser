import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Tetrahedron extends Object3D{
    Vector<Triangle> TriList = new Vector<>();
    Tetrahedron(Vector3d origin, double size,Material m){
        super(m);
        //Calculate vertex distances
        double d1 = size * 0.942809041582063365867792482806465385713114583584632048; //sqrt(8/9)
        double d2 = size * 0.47140452079103168293389624140323269285655729179231602;  //sqrt(2/9)
        double d3 = size * 0.816496580927726032732428024901963797321982493552223376; //sqrt(2/3)
        double d4 = size * 0.33333333333333333333333333333333333333333333333333333;  //1/3
        //Create vertices, positioned with origin in the geometric center
        Vector3d v1 = new Vector3d(d1, 0.0, -d4);
        Vector3d v2 = new Vector3d(-d2, d3, -d4);
        Vector3d v3 = new Vector3d(-d2, -d3, -d4);
        Vector3d v4 = new Vector3d(0.0, 0.0, size);
        //Move object to selected origin
        v1.add(origin);
        v2.add(origin);
        v3.add(origin);
        v4.add(origin);
        //Create the triangles (positions defined as viewed from the camera, left is positive Y direction)
        TriList.add( new Triangle( v1, v2, v3, m) );// bottom side
        TriList.add( new Triangle( v1, v4, v2, m) );// left side
        TriList.add( new Triangle( v1, v3, v4, m) );// Right side
        TriList.add( new Triangle( v3, v2, v4, m) );// Back side
    }
    @Override
    double rayIntersection(Ray r){
        double t = Double.POSITIVE_INFINITY;
        double temp = Double.POSITIVE_INFINITY;
        for(Triangle tri : TriList){
            temp = tri.rayIntersection(r);
            if(temp < t){
                t = temp;
            }
        }
        return temp;
    }
    @Override
    Vector3d CalculateNormal(Vector3d P){
        for(Triangle tri : TriList){
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
        System.out.println("CalculateNormal for Tetrahedron returned null");
        return null;
    }
    @Override
    Vector3d CalculateNormal(){
        return null;
    }

}
