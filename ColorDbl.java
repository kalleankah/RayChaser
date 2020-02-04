import java.lang.*;

// ColorDbl is a custom vec3-like class representing color, with useful member
// functions such as: shorthand constructors, printing, clamping and converting
// between different data formats.

public class ColorDbl{
  double R;
  double G;
  double B;

  // Constructors
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

  //Print this color
  void print(){
    System.out.println("Color: ("+R+", "+G+", "+B+" )");
  }
  //Clamp color between 0-1
  void clamp(){
    R = Math.max(0.0, Math.min(R, 1.0));
    G = Math.max(0.0, Math.min(G, 1.0));
    B = Math.max(0.0, Math.min(B, 1.0));
  }
  //Operators on ColorDbls
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
  // Multiply two ColorDbls element-wise
  static ColorDbl multiply(ColorDbl c, ColorDbl d){
    return new ColorDbl(c.R*d.R, c.G*d.G, c.B*d.B);
  }
  // Sum two ColorDbls
  static ColorDbl sumColors(ColorDbl c, ColorDbl d){
    return new ColorDbl(c.R+d.R, c.G+d.G, c.B+d.B);
  }
  // Calculate average of two colors
  static ColorDbl avgCol(ColorDbl c1, ColorDbl c2){
    return new ColorDbl(
    (c1.R+c2.R)/2.0,
    (c1.G+c2.G)/2.0,
    (c1.B+c2.B)/2.0);
  }
  // Calculate average of two colors
  static ColorDbl mixColors(ColorDbl c1, ColorDbl c2, double factor){
    return new ColorDbl(
    c1.R*factor+c2.R*(1.0-factor),
    c1.G*factor+c2.G*(1.0-factor),
    c1.B*factor+c2.B*(1.0-factor));
  }
  //Format the data to write ppm image (not currently in use)
  // String printForImage(){
  //    int r = (int)(R*255);
  //    int g =(int)( G*255);
  //    int b = (int)(B*255);
  //    return r + " " + g + " " + b;
  // }
  //Format color data to RGB (not currently in use)
  // int RGBForImage(){
  //    int r = (int)(R*255);
  //    int g =(int)( G*255);
  //    int b = (int)(B*255);
  //    return (((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff));
  // }

  //Format color data to RGB
  int ARGBForImage(){
    int r = (int)(R*255);
    int g =(int)( G*255);
    int b = (int)(B*255);
    int a = 255;
    return (((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff));
  }
  //For converting image texture color data to doubles
  public static ColorDbl argbToColorDbl(int argb){
    return new ColorDbl( (double)((argb>>16)&0x0ff)/255, (double)((argb>>8)&0x0ff)/255, (double)((argb)&0x0ff)/255 );
  }
  //Assignment operator ColorDbl
  void setColor(ColorDbl c){
    R = c.R;
    G = c.G;
    B = c.B;
  }
  //Assignment operator double
  void setColor(double d){
    R = d;
    G = d;
    B = d;
  }
  //Assignment operator doubles
  void setColor(double a, double b, double c){
    R = a;
    G = b;
    B = c;
  }
  //Assignment operator doubles
  void addColor(double a){
    R = a;
    G = a;
    B = a;
  }
}
