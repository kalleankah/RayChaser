package raychaser;

import javax.vecmath.Vector3d;

//InfinitePlane is simply an infinitely large plane. It has a different
//intersection function and it's not possible to use it with textures.

public class InfinitePlane extends Object3D {
  Vector3d normal, position;
  InfinitePlane(Vector3d pos, Vector3d N, Material m){
    super(m);
    position = pos;
    normal = N;
  }
  @Override
  double rayIntersection(Ray r){
    Vector3d dP = util.sub(position, r.start);
    // return util.dot(dP, normal)/util.dot(r.direction, normal);
    return dP.dot(normal)/r.direction.dot(normal);
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
