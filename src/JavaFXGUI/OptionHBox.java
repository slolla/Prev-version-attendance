package JavaFXGUI;

import java.util.Observable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.geometry.*;
/**
 * Creates a playable panel, inside which is a label, a textfield, and a button.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class OptionHBox extends HBox implements Playable{
	private TextField textFieldOther;
	private OptionSelect parent;
	private AnimatedGif gfA1;
	private AnimatedGif gfA2;
	private int page;
	/**
	 * Creates the playable panel. Creates two gifs.
	 * @param width The Width of the panel.
	 * @param p The parent of this node.
	 * @param pa The page number
	 */
	public OptionHBox(int width, OptionSelect p, int pa) { 
		page = pa;
		parent  = p;
		OptionHBox ref = this;
		Label textFieldOtherLabel = new Label("Other: ");
		textFieldOther = new TextField();
		textFieldOther.setPrefWidth(width-300);
		ButtonHandler handler = new ButtonHandler( (Playable)ref, parent, true);
		textFieldOther.textProperty().addListener((observable, oldVal, newVal)
			->{
				if (oldVal != newVal){
					handler.setVal(newVal);
					handler.handle(new ActionEvent());
				}
				
			}
		);
		

		gfA1 = new AnimatedGif("src/img/gifCheckmark.gif", 500);
		gfA1.setCycleCount(1);
		
		
		gfA2 = new AnimatedGif("src/img/gifCheckmarkReverse.gif", 500);
		gfA2.setCycleCount(1);
		
		getChildren().addAll(gfA1.getView(), textFieldOtherLabel, textFieldOther);
		setSpacing(10);
		setAlignment(Pos.CENTER_LEFT);

	}
	/**
	 *  Plays the animated check mark.
	 */
	@Override
	public void play() {
		getChildren().remove(0);
		getChildren().add(0, gfA1.getView());
		gfA1.play();

	}
	/**
	 * Plays the animated check mark in reverse.
	 */
	@Override
	public void reverse() {
		getChildren().remove(0);
		getChildren().add(0, gfA2.getView());
		gfA2.play();

	}
	
	/**
	 * Clears the TextField inside this object
	 */
	public void clear(){
		textFieldOther.clear();
	}
	
	/**
	 * Gets the text of the TextField of this object
	 * @return The text of the textField of this object.
	 */
	public String getText(){
		return textFieldOther.getText();
	}
	
	public int getPage(){
		return page;
	}
}
