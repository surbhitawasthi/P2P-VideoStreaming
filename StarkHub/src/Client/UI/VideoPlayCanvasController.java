package Client.UI;

import com.jfoenix.controls.JFXButton;
import com.sun.jna.Memory;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

public class VideoPlayCanvasController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


//    @FXML
//    Canvas videoCanvas;
//    @FXML
//    JFXButton playButton, pauseButton;
//
//    public static String VIDEO_FILE = "/home/aks/Videos/Berklee.mp4";
//    private PixelWriter pixelWriter;
//    protected WritablePixelFormat<ByteBuffer> pixelFormat;
//    protected DirectMediaPlayerComponent mediaPlayerComponent;
//    protected AnimationTimer timer;
//
//
//    boolean flag = true;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        pixelWriter = videoCanvas.getGraphicsContext2D().getPixelWriter();
//        pixelFormat = PixelFormat.getByteBgraInstance();
//
//        timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                renderFrame();
//            }
//        };
//
//        mediaPlayerComponent = new TestMediaPlayerComponent();
//    }
//
//
//    public void startPlayer(){
//        if(flag) {
//            mediaPlayerComponent.getMediaPlayer().playMedia(VIDEO_FILE);
//            timer.start();
//            flag = false;
//        }else {
//            mediaPlayerComponent.getMediaPlayer().play();
//        }
//    }
//
//    public void resumePlayer(){
//        mediaPlayerComponent.getMediaPlayer().play();
//    }
//
//    public void pausePlayer(){
//        timer.stop();
//        mediaPlayerComponent.getMediaPlayer().pause();
//    }
//
//    public void stopPlayer() throws Exception{
//        //timer.stop();
//        //mediaPlayerComponent.getMediaPlayer().stop();
//        mediaPlayerComponent.getMediaPlayer().pause();
//        mediaPlayerComponent.getMediaPlayer().release();
//    }
//
//    private class TestMediaPlayerComponent extends DirectMediaPlayerComponent {
//
//        public TestMediaPlayerComponent() {
//            super(new TestBufferFormatCallback());
//        }
//    }
//
//    private class TestBufferFormatCallback implements BufferFormatCallback {
//
//        @Override
//        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
//            final int width = (int)(videoCanvas.getWidth());
//            final int height = (int)(videoCanvas.getHeight());
////            if (useSourceSize) {
////                width = sourceWidth;
////                height = sourceHeight;
////            }
////            else {
////                width = WIDTH;
////                height = HEIGHT;
////            }
////            Platform.runLater(new Runnable () {
////                @Override
////                public void run() {
////                    videoCanvas.setWidth(width);
////                    videoCanvas.setHeight(height);
////                    videoCanvas.setWidth(width);
////                    videoCanvas.setHeight(height);
////                }
////            });
//            return new RV32BufferFormat(width, height);
//        }
//    }
//
//    protected void renderFrame() {
//        Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
//        if (nativeBuffers != null) {
//            // FIXME there may be more efficient ways to do this...
//            // Since this is now being called by a specific rendering time, independent of the native video callbacks being
//            // invoked, some more defensive conditional checks are needed
//            Memory nativeBuffer = nativeBuffers[0];
//            if (nativeBuffer != null) {
//                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
//                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
//                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
//                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
//                }
//            }
//        }
//        mediaPlayerComponent.getMediaPlayer().unlock();
//    }
}
