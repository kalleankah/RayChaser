import javax.vecmath.*;
import java.lang.Math;
import java.util.*;
public class Ray{
    Vector3d start;
    Vector3d end;
    public double RayLength;
    Vector3d direction = new Vector3d(0.0, 0.0, 0.0);
    Vector3d P_hit;
    Vector3d P_Normal;
    ColorDbl rayColor = new ColorDbl();
    Ray ShadowRay;
    Boolean inShadow = false;
    Vector<Ray> Children = new Vector<>();
    Ray(Vector3d s, Vector3d e){
        start = s;
        end = e;
        direction.sub(e, s);
        RayLength = Utilities.vecNorm(direction);
        direction.normalize();
    }
    ColorDbl CalculateColor(){
        if(Children.isEmpty() == true){
            double Brightness = Utilities.vecDot(this.ShadowRay.direction, this.P_Normal);

            if(Brightness < 0.0){
                Brightness = 0.0;
            }
            if(inShadow){
                Brightness = 0.0;
            }
            //rayColor.print();
            rayColor.multiply(Brightness);

            return rayColor;
        }
        ColorDbl C = new ColorDbl();
        for(Ray r : Children){
            C.sumColor(r.CalculateColor());
        }
        C.divide(Children.size());

        return C;
    }
    // Takes the normal of the surface bounced on (and assigns it as Z immediately)
    void raybounce(){
        Vector3d Z = P_Normal;
        Vector3d I_o = Utilities.vecSub(direction, Utilities.vecScale(Z, Utilities.vecDot(direction, Z)));
        Vector3d X = Utilities.vecScale(I_o, 1.0/Utilities.vecNorm(I_o));
        Vector3d Y = Utilities.vecCross(Utilities.vecScale(X,-1.0), Z);

        //Create coordinate system transformation matrices
        Matrix4d rotation_mat = new Matrix4d(
        X.x, X.y, X.z, 0.0,
        Y.x, Y.y, Y.z, 0.0,
        Z.x, Z.y, Z.z, 0.0,
        0.0, 0.0, 0.0, 1.0);
        Matrix4d translation_mat = new Matrix4d(
        1.0, 0.0, 0.0, -P_hit.x,
        0.0, 1.0, 0.0, -P_hit.y,
        0.0, 0.0, 1.0, -P_hit.z,
        0.0, 0.0, 0.0, 1.0);
        Matrix4d world2local = new Matrix4d();
        world2local.mul(rotation_mat, translation_mat);
        //System.out.println(world2local);
        //System.out.println(Y);
        //System.out.println(Z);
        Matrix4d local2world = Utilities.invertMat(world2local);

        //Incoming ray in local coords
        Vector3d direction_local = Utilities.mulMatVec(world2local, direction);
        double Azimuth = 0; //phi
        double Altitude = 0; //theta
        double x,y,z = 0;
        Vector3d LocalEndPoint, EndPoint;
        Ray Child;
        Random random = new Random();
        for(int i = 0; i<4; ++i){
            Azimuth = random.nextDouble()*2*Math.PI;
            Altitude = random.nextDouble()*0.5*Math.PI;
            x = Math.sin(Altitude)*Math.sin(Azimuth);
            y = Math.sin(Altitude)*Math.cos(Azimuth);
            z = Math.cos(Altitude); // up

            LocalEndPoint = new Vector3d(x,y,z);
            EndPoint = Utilities.mulMatVec(local2world, LocalEndPoint);
            Child = new Ray(P_hit, EndPoint);
            Children.add(Child);
        }

    }
    void calculateShadowRay(Light L){
        ShadowRay = new Ray(P_hit,L.position );
    }
    void calculatePhit(double t){
        P_hit = new Vector3d();
        P_hit = Utilities.vecSub(end,start);
        P_hit.scale(t);
        P_hit = Utilities.vecAdd(P_hit, start);
    }
    void isInShadow(){
        inShadow = true;
    }

    void print(){
        System.out.println("Start vector: ( " +start.x+", "+start.y+", "+start.z+" ) \nEnd vector: ( "+end.x+", "+end.y+", "+end.z+" )");
    }
    public static void main(String[] args) {
        Vector3d v1 = new Vector3d(1.0,2.0,3.0);
        Vector3d v2 = new Vector3d(2.0,1.0,2.0);
        Ray r1 = new Ray(v1,v2);
        r1.print();
    }
}
