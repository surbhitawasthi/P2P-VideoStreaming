package test;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class AnnoyingBeep {
    Toolkit toolkit;
    Timer timer;

    public AnnoyingBeep() {
        toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
        timer.schedule(new RemindTask(), 0, 2 * 1000);
    }

    public static void main(String[] args) {
        new AnnoyingBeep();
    }

    class RemindTask extends TimerTask {
        int numWarningBeeps = 3;

        public void run() {
            if (numWarningBeeps > 0) {
                toolkit.beep();
                System.out.println("Beep!");
                numWarningBeeps--;
            } else {
                toolkit.beep();
                System.out.println("Time's up!");
                //timer.cancel(); // Not necessary because
                // we call System.exit
                System.exit(0);   // Stops the AWT thread
                // (and everything else)
            }
        }
    }
}