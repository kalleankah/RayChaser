import javax.vecmath.Vector3d;
public class Triangle extends Object3D{
    Vector3d vertex0, vertex1,vertex2;
    Vector3d edge1,edge2;
    Vector3d normal;
    Triangle(Vector3d v0,Vector3d v1,Vector3d v2, Material m){
        super(m);
        vertex0 = new Vector3d(v0);
        vertex1 = new Vector3d(v1);
        vertex2 = new Vector3d(v2);
        edge1 = new Vector3d();
        edge1.sub(vertex1,vertex0);
        edge2 = new Vector3d();
        edge2.sub(vertex2,vertex0);
        normal = new Vector3d();
        normal.cross(edge1, edge2);
        normal.normalize();
        if(normal == null){
            System.out.println("Null normal in triangle");
        }
    }
    void print(){
        System.out.println("vertex0: ( " +vertex0.x+", "+vertex0.y+", "+vertex0.z+" )");
        System.out.println("vertex1: ( " +vertex1.x+", "+vertex1.y+", "+vertex1.z+" )");
        System.out.println("vertex2: ( " +vertex2.x+", "+vertex2.y+", "+vertex2.z+" )");
        System.out.println("edge1: ( " +edge1.x+", "+edge1.y+", "+edge1.z+" )");
        System.out.println("edge2: ( " +edge2.x+", "+edge2.y+", "+edge2.z+" )");
        System.out.println("normal: ( " +normal.x+", "+normal.y+", "+normal.z+" )");
        mat.color.print();
    }
    @Override
    double rayIntersection(Ray r){
        Vector3d T = new Vector3d();
        Vector3d P = new Vector3d();
        Vector3d Q = new Vector3d();
        Vector3d D = new Vector3d();
        if(r == null){
            System.out.println("r == null");
        }
        if(r.start == null){
            System.out.println ("r.start == null");
        }
        T.sub(r.start,vertex0);
        Q.cross(T,edge1);
        D.sub(r.end,r.start);
        P.cross(D,edge2);

        double QE1 = Q.dot(edge2);
        double PE1 = P.dot(edge1);
        double PT = P.dot(T);
        double QD = Q.dot(D);
        double t = QE1/PE1;
        double u = PT/PE1;
        double v = QD/PE1;
        if(u < -0.00000000001 || v < -0.00000000001|| u+v > 1.00000000001){
            t = Double.POSITIVE_INFINITY;
        }
        //System.out.println("U: " + u + " V: " +v);
        return t;
    }
    @Override
    Vector3d CalculateNormal(Vector3d P){
        return normal;
    }
    @Override
    Vector3d CalculateNormal(){
        return normal;
    }
}
