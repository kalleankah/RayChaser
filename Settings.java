

public class Settings{
    public int MAX_DEPTH;
    public int CHILDREN;
    public double DEPTH_DECAY;
    public int SHADOW_RAYS;
    public int MAX_REFLECTION_BOUNCES;
    Settings(){
        MAX_DEPTH = 1;
        CHILDREN = 32;
        DEPTH_DECAY = 0.3;
        SHADOW_RAYS = 16;
        MAX_REFLECTION_BOUNCES = 4;
        
    }
    Settings(int MD, int C, double DD, int SR, int MRB){
        MAX_DEPTH = MD;
        CHILDREN = C;
        DEPTH_DECAY = DD;
        SHADOW_RAYS = SR;
        MAX_REFLECTION_BOUNCES = MRB;
    }
    public void setMaxDepth(int MD){
        MAX_DEPTH = MD;
    }
    public void setChildren(int C){
        CHILDREN = C;
    }
    public void setDepthDecay(double d) {
        DEPTH_DECAY = d;
    }
    public void setShadowRays(int s){
        SHADOW_RAYS = s;
    }
    public void setMaxReflectionBounces(int n){
        MAX_REFLECTION_BOUNCES = n;
    }
}