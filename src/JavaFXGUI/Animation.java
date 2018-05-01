package JavaFXGUI;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Animation class that takes a sequence of images and plays it over a duration.
 * Call getView() to get the ImageView that is to be included in the parent application,
 * and call play() to play the animation.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class Animation extends Transition {
    private ImageView imageView;
    private int count;
    private int lastIndex;
    private Image[] sequence;
    
    /**
     * Empty constructor
     */
    public Animation() {
    	
    }
    /**
     * Constructor taking a sequence of images, and a duration to be played.
     * @param sequence A sequence of images to be played
     * @param durationMs The duration of the animation
     */
    public Animation( Image[] sequence, double durationMs) {
        init( sequence, durationMs);
    }

    /**
     * Initializes the animation. 
     * 
     * @param sequence An Array of images.
     * @param durationMs The duration of the animation.
     */
    public void init(Image[] sequence, double durationMs) {
        this.imageView = new ImageView(sequence[0]);
        this.imageView.setFitHeight(40);
        this.imageView.setPreserveRatio(true);
        this.sequence = sequence;
        this.count = sequence.length;

        setCycleCount(1);
        setCycleDuration(Duration.millis(durationMs));
        setInterpolator(Interpolator.LINEAR);
    }
    /**
     * The interpolate class that is called every frame (a required method of Transition)
     * @param k The fraction of the animation that is played through.
     */
    protected void interpolate(double k) {

        final int index = Math.min((int) Math.floor(k * count), count - 1);
        if (index != lastIndex) {
            imageView.setImage(sequence[index]);
            lastIndex = index;
        }

    }
    /**
     * An method returning a ImageView to be used in the main application.
     * @return imageView The imageView that is returned.
     */
    public ImageView getView() {
        return imageView;
    }

}