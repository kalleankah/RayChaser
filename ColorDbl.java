import java.lang.*;

public class ColorDbl{
   double R;
   double G;
   double B;
   ColorDbl(){
      R=0.0;
      G=0.0;
      B=0.0;
   }
   ColorDbl(double a, double b, double c){
      R = a;
      G = b;
      B = c;
   }
   ColorDbl(double b){
      R = G = B = b;
   }
   ColorDbl(ColorDbl c){
      R = c.R;
      G = c.G;
      B = c.B;
   }
   void print(){
      System.out.println("Color: ("+R+", "+G+", "+B+" )");
   }
   void clamp(){
      R = Math.max(0.0, Math.min(R, 1.0));
      G = Math.max(0.0, Math.min(G, 1.0));
      B = Math.max(0.0, Math.min(B, 1.0));
   }
   void sumColor(ColorDbl C){
      R += C.R;
      G += C.G;
      B += C.B;
   }
   void divide(double i){
      R = R/i;
      G = G/i;
      B = B/i;
   }
   void multiply(double d){
      R *= d;
      G *= d;
      B *= d;
   }
   void multiply(ColorDbl d){
      R *= d.R;
      G *= d.G;
      B *= d.B;
   }
   static ColorDbl multiply(ColorDbl c, double s){
     return new ColorDbl(c.R*s, c.G*s, c.B*s);
   }
   String printForImage(){
      int r = (int)(R*255);
      int g =(int)( G*255);
      int b = (int)(B*255);
      return r + " " + g + " " + b;
   }
   int RGBForImage(){
      int r = (int)(R*255);
      int g =(int)( G*255);
      int b = (int)(B*255);
      return (((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff));
   }
   int ARGBForImage(){
      int r = (int)(R*255);
      int g =(int)( G*255);
      int b = (int)(B*255);
      int a = 255;
      return (((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff));
   }
   //For converting image texture color to ColorDbl
   public static ColorDbl argbToColorDbl(int argb){
      return new ColorDbl( (double)((argb>>16)&0x0ff)/255, (double)((argb>>8)&0x0ff)/255, (double)((argb)&0x0ff)/255 );
   }
   void setColor(ColorDbl c){
      R = c.R;
      G = c.G;
      B = c.B;
   }
   void setColor(double a, double b, double c){
      R = a;
      G = b;
      B = c;
   }
}
