package raychaser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.AnimationTimer;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.stage.Screen;
import javafx.stage.Stage;

/* This class handles the GUI - windows, input, buttons, progress bar etc.
When rendering, the current progress is stored in the WritableImage "image"
which is previewed in real time.
There is an ExecutorService "executor" which creates instances of RenderTask.
Each instance of RenderTask renders a small portion of the image, a box.

RenderTask extends Task<Void>, and contains the actual
*/

public class JFX extends Application {
  // JFX globally accessible members
  Stage stage;
  WritableImage image;
  AnimationTimer updateWindow;
  //Access to executor is necessary to stop the thread on window closing
  ExecutorService executor;
  //The camera contains the camera position and the render settings
  Camera camera = new Camera();
  //The image is rendered in blocks with range*range pixels
  int range = 50;
  //An atomic integer to keep track of progress
  AtomicInteger progress = new AtomicInteger(0);
  double progress_double;
  //Render start time
  long startTime;
  //GUI button to cancel rendering
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

  // Opens the window with settings for rendering
  public void openConfigUI(){
    //Name the windows appropriately
    stage.setTitle("Configure render options");

    //Create Grid to contain fields with settings
    GridPane grid = new GridPane();
    grid.setVgap(5);
    grid.setHgap(5);
    grid.setAlignment(Pos.CENTER);
    grid.setPadding(new Insets(50, 50, 50, 50));

    //Add a fun title
    Text scenetitle = new Text("Ray Chaser!");
    scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 36));
    GridPane.setHalignment(scenetitle, HPos.CENTER);
    grid.add(scenetitle, 0, 0, 2, 1);

    //Fields to select resolution (arbitrary aspect ratio)
    Label res = new Label("Resolution:");
    grid.add(res, 0, 1);
    GridPane.setHalignment(res, HPos.RIGHT);
    TextField horizontalResField = new TextField("800");
    grid.add(horizontalResField, 1, 1);
    TextField verticalResField = new TextField("600");
    grid.add(verticalResField, 1, 2);

    //Add slider to adjust camera zoom level
    Label fov = new Label("Zoom");
    grid.add(fov, 0, 3);
    GridPane.setHalignment(fov, HPos.RIGHT);
    Slider fovSlider = new Slider(0.5, 2.0, 0.8);
    fovSlider.setShowTickLabels(true);
    fovSlider.setMajorTickUnit(0.5);
    // fovSlider.setBlockIncrement(0.1);
    fovSlider.valueProperty().addListener((obs, oldval, newVal) ->
      fovSlider.setValue(Math.round(newVal.doubleValue()*10.0)/10.0));
    grid.add(fovSlider, 1, 3);

    //Add slider to change light source brightness
    Label cameraHeight = new Label("Camera Height");
    grid.add(cameraHeight, 0, 4);
    GridPane.setHalignment(cameraHeight, HPos.RIGHT);
    Slider cameraHeightSlider = new Slider(-5,5,0);
    cameraHeightSlider.setMajorTickUnit(1);
    cameraHeightSlider.setShowTickLabels(true);
    cameraHeightSlider.valueProperty().addListener((obs, oldval, newVal) ->
      cameraHeightSlider.setValue(Math.round(newVal.doubleValue())));
    grid.add(cameraHeightSlider, 1, 4);

    //Add slider to change light source brightness
    Label brightnessLabel = new Label("Brightness");
    grid.add(brightnessLabel, 0, 5);
    GridPane.setHalignment(brightnessLabel, HPos.RIGHT);
    Slider brightnessSlider = new Slider(1,10,3);
    brightnessSlider.setMajorTickUnit(1);
    brightnessSlider.setShowTickLabels(true);
    brightnessSlider.valueProperty().addListener((obs, oldval, newVal) ->
      brightnessSlider.setValue(Math.round(newVal.doubleValue())));
    grid.add(brightnessSlider, 1, 5);

    //Slider to select number of samples
    Label samples = new Label("Samples [1]");
    grid.add(samples, 0, 6);
    GridPane.setHalignment(samples, HPos.RIGHT);
    Slider samplesSlider = new Slider(1,100,1);
    //Round the value and update text when sliding
    samplesSlider.valueProperty().addListener((obs, oldval, newVal) ->
      samplesSlider.setValue(Math.round(newVal.doubleValue())));
    samplesSlider.valueProperty().addListener((obs, oldval, newVal) ->
      samples.textProperty().setValue("Samples [" + Math.round(newVal.doubleValue()) + "]"));
    samplesSlider.setMajorTickUnit(100);
    samplesSlider.setShowTickLabels(true);
    grid.add(samplesSlider, 1, 6);

    //Add slider to select max depth (number of bounces)
    Label depth = new Label("Max Depth");
    grid.add(depth, 0, 7);
    GridPane.setHalignment(depth, HPos.RIGHT);
    Slider depthSlider = new Slider(0,100,10);
    depthSlider.setShowTickLabels(true);
    depthSlider.setMajorTickUnit(1);
    depthSlider.valueProperty().addListener((obs, oldval, newVal) ->
      depthSlider.setValue(Math.round(newVal.doubleValue())));
    grid.add(depthSlider, 1, 7);


    //Add slider to select number of CPU threads to Utilize
    Label threads = new Label("CPU Threads:");
    grid.add(threads, 0, 8);
    GridPane.setHalignment(threads, HPos.RIGHT);
    //Default number of threads equal to number of logical processors detected
    Slider threadsSlider = new Slider(1,Runtime.getRuntime().availableProcessors(),Runtime.getRuntime().availableProcessors());
    threadsSlider.setMajorTickUnit(1);
    threadsSlider.setShowTickLabels(true);
    threadsSlider.valueProperty().addListener((obs, oldval, newVal) ->
      threadsSlider.setValue(Math.round(newVal.doubleValue())));
    grid.add(threadsSlider, 1, 8);

    //Add checkbox for using shadow rays
    Label shadowrays = new Label("Use Shadow Rays");
    grid.add(shadowrays, 0, 9);
    GridPane.setHalignment(shadowrays, HPos.RIGHT);
    //Use shadow rays by default
    CheckBox shadowrayBox = new CheckBox();
    shadowrayBox.setSelected(true);
    grid.add(shadowrayBox, 1, 9);

    //Create button to start render and open render preview
    Button btn = new Button("Render");
    HBox btnbox = new HBox(10);
    btnbox.setAlignment(Pos.BOTTOM_RIGHT);
    btnbox.getChildren().add(btn);
    grid.add(btnbox, 1, 11);
    //Define button behavior
    btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent event){
        //Collect settings from fields/sliders into the camera object
        camera.setSize(
          Integer.parseInt(horizontalResField.getText()),
          Integer.parseInt(verticalResField.getText())
          );
        image = new WritableImage(camera.Width, camera.Height);
        camera.setImg(image);
        camera.setSamples((int)samplesSlider.getValue());
        camera.setDepth((int)depthSlider.getValue());
        camera.setShadowRays(shadowrayBox.isSelected() ? 1:0);
        camera.setThreads((int)threadsSlider.getValue());
        camera.setBrightness((int)brightnessSlider.getValue());
        camera.setEye(cameraHeightSlider.getValue());
        camera.setFov(fovSlider.getValue());
        
        //Switch to the render preview window
        openRenderUI();

        //Start the rendering
        startRenderingTasks(camera);
      }
    });

    //Bind enter key to render button in first screen
    grid.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.ENTER) {
        btn.fire();
        event.consume();
      }
    });

    // Set the scene and make it visible
    stage.setScene(new javafx.scene.Scene(grid));
    stage.show();
  }

  //Open render preview
  void openRenderUI(){
    //Name the window appropriately
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

    // Create cancel Button
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

    //Place a bar at the top of the window, containing the progressbar, timer, cancel button etc.
    BorderPane border = new BorderPane();
    HBox menu_bar = new HBox(8, progressbar, progresstext, progresstime, spacer, button_cancel);
    menu_bar.setPadding(new Insets(4, 4, 4, 4));
    menu_bar.setFillHeight(true);
    border.setTop(menu_bar);

    //Create a canvas and place the image (that's being rendered to) in it
      // First, compensate for any scaling since we want the image 1:1
    double scaling = Screen.getPrimary().getOutputScaleX();
    Canvas canvas = new Canvas(image.getWidth()/scaling, image.getHeight()/scaling);
    border.setCenter(canvas);

    //updateWindow is responsible for refreshing the render preview window
    updateWindow = new AnimationTimer(){
      // lastUpdate contains time stamp of last update to control frame rate
      private long lastUpdate = 0;
      // Calculate total number of lines to compare with current rendered lines
      int cols = ((int)image.getWidth())/range + 1;
      int tot_lines = ((int)image.getHeight())*cols;

      @Override
      public void handle(long now) {
        //Update window every 100ms (in nanoseconds)
        if(now-lastUpdate>=100_000_000){
          //Draw the current content of "image" to the canvas
          GraphicsContext gc = canvas.getGraphicsContext2D();
          final Affine xform = gc.getTransform();
          xform.setMxx(1/scaling);
          xform.setMyy(1/scaling);
          gc.setTransform(xform);
          gc.drawImage(image,0,0);
          //Fetch progress and update progress bar and text
          progress_double = ((double)progress.get())/tot_lines;
          progressbar.setProgress(progress_double);
          progresstext.setText(""+(int)(100*progress_double)+"%");
          //Calculate elapsed time
          long current_time_ns = now-startTime;
          //Update elapsed time-text and format to HH:MM:SS
          progresstime.setText(String.format("%02d:%02d:%02d",
          TimeUnit.NANOSECONDS.toHours(current_time_ns),
          TimeUnit.NANOSECONDS.toMinutes(current_time_ns) -
          TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(current_time_ns)),
          TimeUnit.NANOSECONDS.toSeconds(current_time_ns) -
          TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(current_time_ns))));
          lastUpdate = now;
          //Call done() when render is completed (saves the image and cleans up)
          if(progress_double>=1){done();}
        }
      }
    };

    //Begin refreshing the window
    updateWindow.start();

    //Set the new scene
    stage.setScene(new javafx.scene.Scene(border));
    stage.show();
  }

  //Creates rendering tasks and executes them in parallell
  public void startRenderingTasks(Camera camera){
    //Start measuring render time
    startTime = System.nanoTime();

    //Load texture images into Vector<Image>
    Vector<Image> textures = new Vector<>();
    try {
      textures.add(new Image(new FileInputStream("texture/block.png")));
      textures.add(new Image(new FileInputStream("texture/wood.jpg")));
      textures.add(new Image(new FileInputStream("texture/gradient.png")));
      textures.add(new Image(new FileInputStream("texture/wallpaper3k.png")));
      textures.add(new Image(new FileInputStream("texture/test.png")));
      textures.add(new Image(new FileInputStream("texture/tile.jpg")));
    }
    catch(FileNotFoundException e){
      System.out.println("Textures not found in folder \"texture/\"");
      e.printStackTrace();
    }

    //Create and submit all tasks to Executor
    executor = Executors.newFixedThreadPool(camera.THREADS);
    for(int y=0; y<(camera.Height/range + 1); ++y){
      for(int x=0; x<(camera.Width/range + 1); ++x){
        //Important to create new scene for each task, otherwise each RenderTask
        //has to wait for parallell threads to stop accessing that scene.
        //This causes blocking and makes multithreading much slower
        final Scene scene = new Scene(textures, camera.Brightness);
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
    long renderTime = System.nanoTime()-startTime;
    String logFinish = "Render Finished in " + (int)((renderTime)/1_000_000_000.0) + "s";
    stage.setTitle(logFinish);
    System.out.println(logFinish);

    //Update cancel button text (it still takes you to the same screen)
    button_cancel.setText("New Render");

    //Stop refreshing the window
    updateWindow.stop();

    //Save render to file
    savePNG(renderTime);
  }

  //Write image to PNG
  void savePNG(long renderTime){
    String filename =
    "RES" + camera.Width + "x" + camera.Height +
    "-SR" + camera.SHADOW_RAYS +
    "-MD" + camera.MAX_DEPTH +
    "-SPP"+ camera.samples +
    "-" + (int)((renderTime)/1_000_000_000.0) + "sec";

    try{
      OutputStream out = new FileOutputStream("images/" + filename + ".png");
      PNGEncoder encoder = new PNGEncoder(out);
      encoder.encode(image);
    }
    catch(IOException e){
      System.out.println("Error when writing output image: " + e );
    }
    System.out.println("Saved image as " + "\"" + filename + ".png\"");
  }

  //Window close method terminates the ExecutorService if it's initiated
  @Override
  public void stop(){
    if(executor != null){
      executor.shutdownNow();
    }
    Platform.exit();
  }
}
