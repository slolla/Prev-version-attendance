package JavaFXGUI;

import javafx.scene.control.*;
/**
 * An optionSelect block that implements Playable. Is a button and is handled by ButtonHandler
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class OptionButton extends Button implements Playable{
	private String valueText;
	private AnimatedGif gfA1;
	private AnimatedGif gfA2;
	private int page;
	/**
	 * Creates itself without any style. 
	 * @param t The content displayed in the button
	 * @param value The value of the button
	 * @param p The page number
	 */
	public OptionButton(String t, String value, int p){
		setText(t);
		valueText = value;
		init();
		page = p;
	}
	/**
	 * Initializes the button with a specific style.
	 * @param t The content displayed in the button
	 * @param value The value of the button
	 * @param style The Style of the button ("top", "mid", or "bottom");
	 * @param p the Page number
	 */
	public OptionButton(String t, String value, String style, int p){
		setText(t);
		valueText = value;
		setPosStyle(style);
		init();
		page = p;
	}

	/**
	 * Initializes the Button. Creates an AnimatedGif of a checkMark and an AnimatedGif of a checkMark closing.
	 */
	private void init(){
		gfA1 = new AnimatedGif("src/img/gifCheckmark.gif", 500);
		gfA1.setCycleCount(1);
		setGraphic(gfA1.getView());

		gfA2 = new AnimatedGif("src/img/gifCheckmarkReverse.gif", 500);
		gfA2.setCycleCount(1);

	}

	/**
	 * Gets the value of the button
	 * @return The value of the button
	 */
	public String getValue(){
		return valueText;
	}

	/**
	 * Sets the style of the button. "top", "mid", and "bottom" correspond to where the button is in the OptionSelect.
	 * @param style The style of the button
	 */
	public void setPosStyle(String style){
		getStyleClass().clear();
		if (style == "top"){
			getStyleClass().add("optionButton-top");
		}
		else if (style == "mid"){ 
			getStyleClass().add("optionButton-mid");
		}

		else if (style == "bottom"){
			getStyleClass().add("optionButton-bottom");
		}
	}

	/**
	 * Plays the gif in the button
	 */
	public void play(){
		setGraphic(gfA1.getView());
		gfA1.play();
	}
	/**
	 * Plays the gif in the button in reverse
	 */
	public void reverse(){
		setGraphic(gfA2.getView());
		gfA2.play();
	}

	public int getPage(){
		return page;
	}
	
	public boolean equals(OptionButton other){
		return (valueText.equals(other.valueText));
	}
	public void clear(){
		
	}

}
