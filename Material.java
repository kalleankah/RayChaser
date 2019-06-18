class Material{
    ColorDbl color;
    double Brightness;
    Material(){
        color = new ColorDbl();
        Brightness = 1.0;
    }
    Material(ColorDbl C){
        color = new ColorDbl();
        color.setColor(C);
        Brightness = 1.0;
    }
    Material(ColorDbl C, double b){
        color = new ColorDbl();
        color.setColor(C);
        Brightness = b;
    }

}
