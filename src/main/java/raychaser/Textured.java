package raychaser;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class Textured extends Material {
  private Image texture;
  private int width;
  private int height;
  private PixelReader pixelreader;

  //Construct with texture
  Textured(Image t){
    texture = t;
    pixelreader = texture.getPixelReader();
    width = (int)texture.getWidth();
    height = (int)texture.getHeight();
  }

  //Get color from texture
  @Override
  ColorDbl getColor(double u, double v){
    int x = (int) Math.round(u * (width-1));
    int y = (int) Math.round(v * (height-1));
    return ColorDbl.argbToColorDbl(pixelreader.getArgb(x, y));
  }
}
