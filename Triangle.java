import javax.vecmath.Vector3d;
public class Triangle{
    Vector3d vertex0, vertex1,vertex2;
    Vector3d edge1,edge2;
    Vector3d normal;
    ColorDbl color = new ColorDbl();
    Triangle(Vector3d v0,Vector3d v1,Vector3d v2, ColorDbl c){
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
        color = c;
    }
    void print(){
        System.out.println("vertex0: ( " +vertex0.x+", "+vertex0.y+", "+vertex0.z+" )");
        System.out.println("vertex1: ( " +vertex1.x+", "+vertex1.y+", "+vertex1.z+" )");
        System.out.println("vertex2: ( " +vertex2.x+", "+vertex2.y+", "+vertex2.z+" )");
        System.out.println("edge1: ( " +edge1.x+", "+edge1.y+", "+edge1.z+" )");
        System.out.println("edge2: ( " +edge2.x+", "+edge2.y+", "+edge2.z+" )");
        System.out.println("normal: ( " +normal.x+", "+normal.y+", "+normal.z+" )");
        color.print();
    }
    double rayIntersection(Ray r){
        Vector3d T = new Vector3d();
        Vector3d P = new Vector3d();
        Vector3d Q = new Vector3d();
        Vector3d D = new Vector3d();
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
        if(u < 0.0 || v < 0.0 || u+v > 1.0){
            t = Double.POSITIVE_INFINITY;
        }
        //System.out.println("U: " + u + " V: " +v);
        return t;
    }
    public static void main(String[] args) {
        Vector3d v1 = new Vector3d(0.0,0.0,0.0);
        Vector3d v2 = new Vector3d(1.0,1.0,0.0);
        Vector3d v3 = new Vector3d(1.0,0.0,0.0);
        ColorDbl c = new ColorDbl(1.0,0.0,1.0);
        Triangle Tri = new Triangle(v1,v2,v3,c);
        Tri.print();
        Vector3d s = new Vector3d(0.5,0.20,-1.0);
        Vector3d e = new Vector3d(0.5,5.20,-1.0);
        Ray r = new Ray(s,e);
        r.print();
        double t = Tri.rayIntersection(r);
        System.out.println(t);

    }
}
