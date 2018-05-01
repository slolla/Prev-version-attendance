package JavaFXGUI;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import backend.GifDecoder;

/**
 * Wrapper class for Animation that reads a .gif file.
 * @author Kevin
 */
public class AnimatedGif extends Animation {
	/**
	 * Wrapper class for Animation that reads a .gif file.
	 * @param filename filepath of gif.
	 * @param durationMs the duration of the gif to be played
	 */
    public AnimatedGif( String filename, double durationMs) {
        GifDecoder d = new GifDecoder();
        d.read( filename);

        Image[] sequence = new Image[ d.getFrameCount()];
        for( int i=0; i < d.getFrameCount(); i++) {

            WritableImage wimg = null;
            BufferedImage bimg = d.getFrame(i);
            sequence[i] = SwingFXUtils.toFXImage( bimg, wimg);

        }

        super.init( sequence, durationMs);

    }

}