import javax.vecmath.Vector3d;
import java.lang.Math;
import java.util.*;
public class Tetrahedron extends Object3D{
    Vector<Object3D> TriList = new Vector<Object3D>();
    Tetrahedron(Vector3d origin, double size,ColorDbl c){
        super(c);
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
        TriList.add( new Triangle( v1, v2, v3, c) );// bottom side
        TriList.add( new Triangle( v1, v4, v2, c) );// left side
        TriList.add( new Triangle( v1, v3, v4, c) );// Right side
        TriList.add( new Triangle( v3, v2, v4, c) );// Back side
    }
    @Override
    double rayIntersection(Ray r){
        double t = Double.POSITIVE_INFINITY;
        double temp = Double.POSITIVE_INFINITY;
        for(Object3D tri : TriList){
            temp = tri.rayIntersection(r);
            if(temp < t){
                t = temp;
            }
        }
        return temp;
    }
}
