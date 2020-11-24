package raychaser;

import java.util.Random;

import javax.vecmath.Vector3d;

//The Sphere is an analytic sphere, it is not made from primitive objects.

public class Sphere extends Object3D {
  Vector3d center;
  double radius;

  //Constructor from center, radius, material
  Sphere(Vector3d c, double r, Material m){
    super(m);
    center = c;
    radius = r;
  }
  //Copy constructor
  Sphere(Sphere s){
    super(s.mat);
    center = new Vector3d(s.center);
    radius = s.radius;
  }

  //Checks for intersection between a ray and a sphere
  @Override
  double rayIntersection(Ray r){
    // //Create a vector from ray origin to sphere center
    // Vector3d L = util.sub(center, r.start);
    // double tCenter = util.dot(r.direction, L);
    // if(tCenter < 0.0){
    //   //Sphere center is in behind the ray origin
    //   return -1.0;
    // }
    // double d2 = L.dot(L) - (tCenter*tCenter);
    // if(d2 > radius*radius){
    //   // Ray doesn't intersect the sphere
    //   return -1.0;
    // }
    // double tCenterToSurface = Math.sqrt(radius*radius - d2);
    // double t0 = tCenter - tCenterToSurface;
    // double t1 = tCenter + tCenterToSurface;

    // if(t0 < t1){
    //   if(t0 > 0.0){
    //     //if t0 is in front of ray origin, both are, but t0 is closer
    //     return t0;
    //   }
    //   if(t1 > 0.0){
    //     //t0 is behind ray origin, but t1 is in front
    //     return t1;
    //   }
    //   //Both t0 and t1 are behind the ray origin
    //   return -1.0;
    // }
    // else{
    //   //t1 < t0
    //   if(t1 > 0.0){
    //     //if t1 is in front of ray origin, both are, but t1 is closer
    //     return t1;
    //   }
    //   if(t0 > 0.0){
    //     //t1 is behind ray origin, but t0 is in front
    //     return t0;
    //   }
    //   //Both t0 and t1 are behind the ray origin
    //   return -1.0;
    // }

    //Create a vector from sphere center to ray origin
    Vector3d L = util.sub(r.start, center);
    double b = 2.0 * r.direction.dot(L);
    double c = L.dot(L) - (radius*radius);

    //Solve quadratic
    double t0, t1;
    double rootarg = b*b - 4*c;
    if(rootarg <= 0.0){
      //Complex root, no intersection
      return -1.0;
    }
    else{
      double q = b>0 ? -0.5 * (b + Math.sqrt(rootarg)) : -0.5 * (b - Math.sqrt(rootarg));
      t0 = q;
      t1 = c/q;
    }
    if(t0 > t1){
      double temp = t0;
      t0 = t1;
      t1 = temp;
    }

    // t0 is smaller than t1, we need to return the smallest positive of them
    if(t0 < 0.0){
      //t0 is behind ray origin, replace it with t1 and check again
      t0 = t1;
      if(t0 < 0.0){
        return -1.0;
      }
    }
    
    //t0 is the closest non-negative intersection
    return t0/r.length();
  }
  //Unlike planes and triangles, the normal to the sphere depends on the
  //position on the sphere.
  @Override
  Vector3d CalculateNormal(Vector3d P){
    return new Vector3d((P.x-center.x)/radius, (P.y-center.y)/radius, (P.z-center.z)/radius);
  }
  //Calling CalculateNormal() without argument is wrong, this override is made
  //specifically to warn about it and prevent invisible errors.
  @Override
  Vector3d CalculateNormal(){
    System.out.println("Called CalculateNormal() without arguments and returned null. Correct arguments are: CalculateNormal(Vector3d point)");
    return null;
  }
  //Sample sphere emitter surface uniformly
  @Override
  Vector3d SampleEmitter(Vector3d rayOrigin){
    
    //Create  vector from ray origin to the center of the light
    Vector3d fromCenter = util.sub(rayOrigin, center);
    fromCenter.normalize();

    //Create normalized vectors spanning a unit square plane orthogonal to "fromCenter"
    Vector3d orthovec1 = util.findOrthogonalVector(fromCenter);
    Vector3d orthovec2 = util.normalize(util.cross(fromCenter, orthovec1));
    // System.out.println("Orthovec1 dot: " + util.dot(fromCenter, orthovec1));
    // System.out.println("Orthovec2 dot: " + util.dot(fromCenter, orthovec2));

    Random rand = new Random();
    double d1,d2,generatedRadius;
    do{
      d1 = (-1 + 2 * rand.nextDouble()) * radius;
      d2 = (-1 + 2 * rand.nextDouble()) * radius;
      generatedRadius = Math.sqrt(d1*d1+d2*d2);
    }
    //Reject samples on the square, outside the disk
    while(generatedRadius >= radius);
    
    //Create a vector from center to the surface of the sphere
    double height = radius * Math.sqrt(Math.sin(Math.PI/2 * (1 - generatedRadius/radius) ));
    Vector3d toSphereSurface = util.add(util.scale(orthovec1, d1), util.scale(orthovec2, d2), util.scale(fromCenter, height));
    //Ensure the point is not inside the sphere
    double epsilon = 0.001;
    toSphereSurface = util.scale(toSphereSurface, (radius+epsilon)/util.norm(toSphereSurface));
    Vector3d pointOnSphere = util.add(center, toSphereSurface);

    // util.print(pointOnSphere);

    return pointOnSphere;
  }
}
