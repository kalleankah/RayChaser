import javax.vecmath.Vector3d;

public class Sphere extends Object3D {
  Vector3d center;
  double radius;

  Sphere(Vector3d c, double r, ColorDbl col){
    super(col);
    center = c;
    radius = r;
  }

//Checks for intersection between a ray and a sphere
@Override
double rayIntersection(Ray r){
    //Create a vector with the direction of ray origin to sphere center
    Vector3d oc = new Vector3d(0.0, 0.0, 0.0);
    oc.sub(r.start, center);
    //System.out.println(oc);
    //Create function vars
    double b = 2.0 * r.direction.dot(oc);
    double c = oc.dot(oc) - (radius*radius);

    double d0 = Double.POSITIVE_INFINITY; //Direction to entry intersection of sphere
    double d1 = Double.POSITIVE_INFINITY; //Direction to exit intersection of sphere
    Vector3d x = new Vector3d(0.0, 0.0, 0.0);
    double rootarg = 0.25*b*b - c; //Pre-calculate argument of square root
    double epsilon = 0.0001;

    //Non-tangental intersection with sphere
    if (rootarg > epsilon){
        d0 = -0.5 * b - Math.sqrt(rootarg);
        d1 = -0.5 * b + Math.sqrt(rootarg);
        //The point of intersection is x
        x.scaleAdd(d0, r.direction, r.start);
    }
    double t = d0/r.RayLength;
    return t;
}
@Override
Vector3d CalculateNormal(Vector3d P){
    return Utilities.vecNormalize(Utilities.vecSub(P,center));
}
@Override
Vector3d CalculateNormal(){
    Vector3d temp = new Vector3d(-0.01, -0.01, -0.01);
    return temp;
}

}
