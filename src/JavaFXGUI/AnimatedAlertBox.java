package JavaFXGUI;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.layout.*;



/**
 * An Animated Alert box, either green or red, that fades in and out. Intended to be 
 * attached to the top of a BorderPane.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class AnimatedAlertBox extends HBox{
	private SequentialTransition  seqT; 
	private Label studentIDLabel;
	private boolean warning;
	/**
	 * Constructor
	 * @param defText The default text.
	 * @param warning Whether or not the Alert is a warning, red, or not, green.
	 */
	public AnimatedAlertBox(String defText, boolean w){
		warning = w;
		setOpacity(0);
		studentIDLabel = new Label(defText);
		
		if (warning){
			
			getStyleClass().add("alertMessage");
			studentIDLabel.getStyleClass().add("alertText");
			
		}
		else{
			
			getStyleClass().add("sucessMessage");
			studentIDLabel.getStyleClass().add("sucessText");
			
		}
		
		getChildren().add(studentIDLabel);
		setAlignment(Pos.CENTER);
		setPrefHeight(40);
		
		
		FadeTransition ftIn = new FadeTransition(Duration.millis(500), this);
		ftIn.setFromValue(0);
		ftIn.setToValue(1.0);
		ftIn.setCycleCount(1);
		PauseTransition pt = new PauseTransition(Duration.millis(2000));
		FadeTransition ftOut = new FadeTransition(Duration.millis(500), this);
		ftOut.setFromValue(1.0);
		ftOut.setToValue(0);
		ftOut.setCycleCount(1);
		
		seqT = new SequentialTransition (ftIn, pt, ftOut);
	}
	
	public void setStyle(){
		if (warning){
			getStyleClass().remove(getStyleClass().size()-1);
			getStyleClass().add("alertMessage");
			studentIDLabel.getStyleClass().add("alertText");
			studentIDLabel.getStyleClass().remove(getStyleClass().size()-1);
		}
		else{
			getStyleClass().remove(getStyleClass().size()-1);
			getStyleClass().add("sucessMessage");
			studentIDLabel.getStyleClass().add("sucessText");
			studentIDLabel.getStyleClass().remove(getStyleClass().size()-1);
		}
	}
	/**
	 * Plays the animation (fades in and out) with a custom message.
	 * @param mes The Message of the alert.
	 */
	public void play(String mes){
		studentIDLabel.setText(mes);
		seqT.play();
	}
	/**
	 * Plays the animation (fades in and out) with the default message defined in the constructor.
	 */
	public void play(){
		seqT.play();
	}
	public void setWarning(boolean w){
		warning = w;
		setStyle();
	}
	
}
