

public class Settings{
    public int MAX_DEPTH;
    public int CHILDREN;
    public double DEPTH_DECAY;
    public int SHADOW_RAYS;
    Settings(){
        MAX_DEPTH = 1;
        CHILDREN = 32;
        DEPTH_DECAY = 0.3;
        SHADOW_RAYS = 16;
        
    }
    Settings(int MD, int C, double DD, int SR){
        MAX_DEPTH = MD;
        CHILDREN = C;
        DEPTH_DECAY = DD;
        SHADOW_RAYS = SR;
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
}