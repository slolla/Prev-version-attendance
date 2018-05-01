package JavaFXGUI;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.event.*;
import javafx.geometry.*;


/**
 * Extension of label class that flashes whenever the play() function is called.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class AnimatedLabel extends Label{
	FadeTransition ftIn;
	FadeTransition ftOut;
	SequentialTransition seqBlink;
	

	public AnimatedLabel(String text){
		super(text);
		ftIn = new FadeTransition(Duration.millis(500), this);
		ftIn.setFromValue(0);
		ftIn.setToValue(1.0);
		ftIn.setCycleCount(1);
		
		
		ftOut = new FadeTransition(Duration.millis(500), this);
		ftOut.setFromValue(1.0);
		ftOut.setToValue(0);
		ftOut.setCycleCount(1);
		
		seqBlink = new SequentialTransition(ftIn, ftOut);
		seqBlink.setCycleCount(Timeline.INDEFINITE);
		
	}
	
	public void play(){
		seqBlink.play();
		getStyleClass().add("summaryLabel-active");
	}
	
	public void stop(){
		seqBlink.stop();
		getStyleClass().remove("summaryLabel-active");
	}
}
