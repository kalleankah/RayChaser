import java.util.concurrent.CountDownLatch;
class Multithread implements Runnable {
  Scene scene;
  int range;
  int thread_index;
  private CountDownLatch latch;
  Multithread(Scene s, int r, int i, CountDownLatch l){
      scene = s;
      range = r;
      thread_index = i;
      latch = l;
  }
  public void run(){
      try{
          // Do something
          scene.render(thread_index*range, (thread_index+1)*range);
          latch.countDown();
          System.out.println("Thread " + thread_index + " done.");
      }
      catch (Exception e){System.out.println ("Exception is caught");}
  }
}
