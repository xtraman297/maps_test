package noam.socialbridge_alfa;

/**
 * Created by noam2_000 on 06/03/2015.
 */
public class MapObject implements Runnable {
    private Thread thrThread;

    public MapObject(){

    }

    @Override
    public void run() {
        System.out.println("test");
    }

    public void startObjectThread (){
        if (this.thrThread == null){
            this.thrThread = new Thread(this, "test");
        }

        this.thrThread.start();
    }
}
