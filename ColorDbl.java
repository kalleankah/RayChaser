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
        R = Math.min(a,1.0);
        G = Math.min(b,1.0);
        B = Math.min(c,1.0);
    }
    void print(){
        System.out.println("Color: ("+R+", "+G+", "+B+" );");
    }
    String printForImage(){
        int r = (int)(R*255);
        int g =(int)( G*255);
        int b = (int)(B*255);
        return r + " " + g + " " + b;
    }
    void setColor(ColorDbl Cd){
        R = Cd.R;
        G = Cd.G;
        B = Cd.B;
    }
    void setColor(double a, double b, double c){
        R = Math.min(a,1.0);
        G = Math.min(b,1.0);
        B = Math.min(c,1.0);
    }
    public static void main(String[] args) {
        ColorDbl C1 = new ColorDbl();
        ColorDbl C2 = new ColorDbl(1.0,0.5,0.1);
        ColorDbl C3 = new ColorDbl(2.0,3.0,2.0);
        C3.setColor(C1);
        //C1.print();
        C2.print();
        System.out.println(C2.printForImage());
        //C3.print();

    }
}
