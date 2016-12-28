import java.util.Timer;
import java.util.TimerTask;

        

public class test {
    private int x = 0;
    
    public test() {
        Timer timer = new Timer();
        TimerTask task = new Updater3();
        long delay = 0;
        long period = 5 * 1000; // every 5 seconds
        timer.schedule(task, delay, period);
    }
    
    public void display() {
        System.out.println(x);
        x++;
    }
    
    public class Updater3 extends TimerTask {
        
        public void run() {
            
            display();
        }
    }
    
    
    
    
}


