import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Vector;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javax.vecmath.Vector3d;

public class JFX extends Application {
   Stage stage;
   WritableImage image;
   AnimationTimer updateWindow;
   //Access to executor is necessary to stop the thread on window closing
   ExecutorService executor;
   //The camera contains the camera position and the render settings
   Camera camera;
   //The image is rendered in 50x50 blocks
   int range = 50;
   //An atomic integer to keep track of progress
   AtomicInteger progress = new AtomicInteger(0);
   double progress_double;
   //Render start time
   long startTime;
   //GUI button
   Button button_cancel;

   //Default launch
   public static void main(String[] args){
      launch(args);
   }

   //--START-- Store stage so setScene() can be called to replace scene
   @Override
   public void start(Stage primaryStage){
      stage = primaryStage;
      openConfigUI();
   }

   public void openConfigUI(){
      //Set title for current window
      stage.setTitle("Configure render options");

      //Create Grid
      GridPane grid = new GridPane();
      grid.setVgap(5);
      grid.setHgap(5);
      grid.setAlignment(Pos.CENTER);
      grid.setPadding(new Insets(50, 50, 50, 50));

      //Add items to grid
      Text scenetitle = new Text("Ray Chaser!");
      scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 36));
      grid.setHalignment(scenetitle, HPos.CENTER);
      grid.add(scenetitle, 0, 0, 2, 1);

      Label res = new Label("Resolution:");
      grid.add(res, 0, 1);
      grid.setHalignment(res, HPos.RIGHT);
      TextField resField = new TextField("500");
      grid.add(resField, 1, 1);

      Label samples = new Label("Samples [25]");
      grid.add(samples, 0, 2);
      grid.setHalignment(samples, HPos.RIGHT);
      Slider samplesSlider = new Slider(1,30,5);
      samplesSlider.setLabelFormatter(new StringConverter<Double>() {
         @Override
         public String toString(Double t) {
            return String.valueOf(Math.round(t*t));
         }
         @Override
         public Double fromString(String string) {
            return Double.parseDouble(string);
         }
      });
      samplesSlider.valueProperty().addListener((obs, oldval, newVal) ->
      samplesSlider.setValue(Math.round(newVal.doubleValue())));
      samplesSlider.valueProperty().addListener((obs, oldval, newVal) ->
      samples.textProperty().setValue("Samples [" + Math.round(newVal.doubleValue()*newVal.doubleValue()) + "]"));
      samplesSlider.setMajorTickUnit(2);
      samplesSlider.setShowTickLabels(true);
      grid.add(samplesSlider, 1, 2);

      Label depth = new Label("Max Depth");
      grid.add(depth, 0, 3);
      grid.setHalignment(depth, HPos.RIGHT);
      Slider depthSlider = new Slider(0,10,5);
      depthSlider.setShowTickLabels(true);
      depthSlider.setMajorTickUnit(1);
      depthSlider.valueProperty().addListener((obs, oldval, newVal) ->
      depthSlider.setValue(Math.round(newVal.doubleValue())));
      grid.add(depthSlider, 1, 3);

      Label rb = new Label("Reflection Bounces");
      grid.add(rb, 0, 4);
      grid.setHalignment(rb, HPos.RIGHT);
      Slider rbSlider = new Slider(0,20,10);
      rbSlider.setShowTickLabels(true);
      rbSlider.setMajorTickUnit(5);
      rbSlider.valueProperty().addListener((obs, oldval, newVal) ->
      rbSlider.setValue(Math.round(newVal.doubleValue())));
      grid.add(rbSlider, 1, 4);

      Label sr = new Label("Shadow Rays");
      grid.add(sr, 0, 5);
      grid.setHalignment(sr, HPos.RIGHT);
      Slider srSlider = new Slider(0,10,1);
      srSlider.setShowTickLabels(true);
      srSlider.setMajorTickUnit(1);
      srSlider.valueProperty().addListener((obs, oldval, newVal) ->
      srSlider.setValue(Math.round(newVal.doubleValue())));
      grid.add(srSlider, 1, 5);

      Label threads = new Label("CPU Threads:");
      grid.add(threads, 0, 6);
      grid.setHalignment(threads, HPos.RIGHT);
      Slider threadsSlider = new Slider(1,Runtime.getRuntime().availableProcessors(),Runtime.getRuntime().availableProcessors());
      threadsSlider.setMajorTickUnit(1);
      threadsSlider.setShowTickLabels(true);
      threadsSlider.valueProperty().addListener((obs, oldval, newVal) ->
      threadsSlider.setValue(Math.round(newVal.doubleValue())));
      grid.add(threadsSlider, 1, 6);

      //Define button action
      Button btn = new Button("Render");
      HBox btnbox = new HBox(10);
      btnbox.setAlignment(Pos.BOTTOM_RIGHT);
      btnbox.getChildren().add(btn);
      grid.add(btnbox, 1, 7);
      btn.setOnAction(new EventHandler<ActionEvent>(){
         @Override
         public void handle(ActionEvent event){
            int[] args = new int[6];
            args[0] = Integer.parseInt(resField.getText());
            args[1] = (int)samplesSlider.getValue();
            args[2] = (int)depthSlider.getValue();
            args[3] = (int)rbSlider.getValue();
            args[4] = (int)srSlider.getValue();
            args[5] = (int)threadsSlider.getValue();
            image = new WritableImage(args[0],args[0]);
            openRenderUI();
            startRenderingTasks(args);
         }
      });

      //Bind enter key to button
      grid.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
         if (event.getCode() == KeyCode.ENTER) {
            btn.fire();
            event.consume();
         }
      });
      stage.setScene(new javafx.scene.Scene(grid));
      stage.show();
   }

   //Replace window content with render preview
   void openRenderUI(){
      //Set window title
      stage.setTitle("Rendering...");

      //Create a progress menu_bar and text to put in a menu_bar
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.SOMETIMES);
      ProgressBar progressbar = new ProgressBar(0);
      progressbar.setMaxWidth(Double.MAX_VALUE);
      progressbar.setMaxHeight(Double.MAX_VALUE);
      HBox.setHgrow(progressbar, Priority.SOMETIMES);
      Text progresstext = new Text("0%");
      progresstext.setFont(Font.font(16));
      Text progresstime = new Text("00:00:00");
      progresstime.setFont(Font.font(16));
      int hours = 0;
      int minutes = 0;

      //Create cancel Button
      button_cancel = new Button("Cancel");
      button_cancel.setOnAction(new EventHandler<ActionEvent>(){
         @Override
         public void handle(ActionEvent event){
            executor.shutdownNow();
            updateWindow.stop();
            progress = new AtomicInteger(0);
            openConfigUI();
         }
      });

      //Create HBox and place in BorderPane's top bar
      BorderPane border = new BorderPane();
      HBox menu_bar = new HBox(8, progressbar, progresstext, progresstime, spacer, button_cancel);
      menu_bar.setPadding(new Insets(4, 4, 4, 4));
      menu_bar.setFillHeight(true);
      border.setTop(menu_bar);


      //Create the canvas containing the image
      Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
      border.setCenter(canvas);

      //updateWindow is responsible for refreshing the window
      updateWindow = new AnimationTimer(){
         //lastUpdate contains time stamp of last update to control frame rate
         private long lastUpdate = 0;
         //Pre calculate progress denominator for performance
         double progressFactor = range/((double)image.getWidth()*image.getWidth());

         @Override
         public void handle(long now) {
            //Update window every 100ms
            if(now-lastUpdate>=100_000_000){
               //Draw the current content of "image" to the canvas
               canvas.getGraphicsContext2D().drawImage(image,0,0);
               //Fetch progress and update progress bar and text
               progress_double = progress.get()*progressFactor;
               progressbar.setProgress(progress_double);
               progresstext.setText(""+(int)(100*progress_double)+"%");
               //Calculate elapsed time and update text
               long current_time_ns = now-startTime;
               progresstime.setText(String.format("%02d:%02d:%02d",
                  TimeUnit.NANOSECONDS.toHours(current_time_ns),
                  TimeUnit.NANOSECONDS.toMinutes(current_time_ns) -
                  TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(current_time_ns)),
                  TimeUnit.NANOSECONDS.toSeconds(current_time_ns) -
                  TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(current_time_ns))));
               lastUpdate = now;
               //Call done() when render is completed
               if(progress_double>=1){done();}
            }
         }
      };

      //Begin updating the window
      updateWindow.start();
      //Set the new scene
      stage.setScene(new javafx.scene.Scene(border));
      stage.show();
   }

   public void startRenderingTasks(int[] args){
      //Start rendering tasks
      camera = new Camera(new Vector3d(0.01,0,0), 1, image, args);
      //measure rendering time
      startTime = System.nanoTime();
      //Load texture images into Vector<Image>
      Vector<Image> textures = new Vector<>();
      try {
         textures.add(new Image(new FileInputStream("texture/block.png")));
         textures.add(new Image(new FileInputStream("texture/wood.jpg")));
         textures.add(new Image(new FileInputStream("texture/gradient.png")));
         textures.add(new Image(new FileInputStream("texture/wallpaper3k.png")));
         textures.add(new Image(new FileInputStream("texture/test.png")));
      }
      catch(FileNotFoundException e){
         System.out.println("Textures could not load");
         e.printStackTrace();
      }
      //Create and submit all tasks to Executor
      executor = Executors.newFixedThreadPool(camera.THREADS);
      for(int y=0; y<(camera.Width/range); ++y){
         for(int x=0; x<(camera.Width/range); ++x){
            //Important to create new scene for each task
            final Scene scene = new Scene(textures);
            final int X = x;
            final int Y = y;
            RenderTask task = new RenderTask(scene, camera, X*range, Y*range, range, progress);
            executor.execute(task);
         }
      }
      //Shut executor down when done
      executor.shutdown();
   }

   //Tasks to perform when rendering is completed
   void done(){
      //Display render time measurement in window and console
      long endTime = System.nanoTime();
      String logFinish = "Render Finished in " + (int)((endTime-startTime)/1000000000.0) + "s";
      stage.setTitle(logFinish);
      System.out.println(logFinish);
      //Update cancel button text
      button_cancel.setText("New Render");
      updateWindow.stop();
      //Save render to file
      savePNG();
   }

   //Write image to PNG
   void savePNG(){
      String filename =
      "RES" + camera.Width +
      "-SPP"+ camera.subpixels*camera.subpixels +
      "-MD" + camera.MAX_DEPTH +
      "-RB" + camera.MAX_REFLECTION_BOUNCES +
      "-SR" + camera.SHADOW_RAYS;

      try{
         OutputStream out = new FileOutputStream("images/" + filename + ".png");
         PNGEncoder encoder = new PNGEncoder(out);
         encoder.encode(image);
      }
      catch(IOException e){
         System.out.println("Error: " + e );
      }
      System.out.println("Saved image as " + "\"" + filename + ".png\"");
   }

   //--STOP-- Window close method terminates executor if it's initiated
   @Override
   public void stop(){
      if(executor != null){
         executor.shutdownNow();
      }
      Platform.exit();
   }
}
