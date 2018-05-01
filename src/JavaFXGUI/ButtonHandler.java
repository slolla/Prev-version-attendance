package JavaFXGUI;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * A handler that handles the selection of elements in the OptionSelect Class.  
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class ButtonHandler implements EventHandler<ActionEvent> {
	public static ArrayList<Playable> previous = new ArrayList<Playable> ();;
	private String val ;
	private Playable button;
	private OptionSelect parent;
	private boolean otherText;
	/**
	 * Constructor  
	 * @param opt The value of the button.
	 * @param b A Playable node.
	 * @param p The Parent OptionSelect class
	 */
	public ButtonHandler(String opt, Playable b, OptionSelect p) {
		this.val = opt ;
		button = b;
		parent = p;
		otherText = false;
		if (button.getPage() == previous.size()){
			previous.add(null);
		}
	}

	/**
	 * 
	 * @param opt The value of the button.
	 * @param b A Playable node.
	 * @param p The Parent OptionSelect class
	 * @param oT Whether or not the Playable Node is an OptionHBox
	 */
	public ButtonHandler(String opt, Playable b, OptionSelect p, boolean oT) {
		this.val = opt ;
		button = b;
		parent = p;
		otherText = oT;

		if (button.getPage() == previous.size()){
			previous.add(null);
		}
	}
	public ButtonHandler( Playable b, OptionSelect p, boolean oT) {

		button = b;
		parent = p;
		otherText = oT;

		if (button.getPage() == previous.size()){
			previous.add(null);
		}
	}
	public void setVal(String v){
		val  =v;
	}
	/**
	 * Handles the playing of the animation of buttons. Also calls addInfo() of 
	 * optionSelect to add the information encoded in each button
	 */
	@Override
	public void handle(ActionEvent event) {
		if (previous.get(button.getPage()) == null){
			button.play();
			previous.set(button.getPage(), button); 
		}
		else if (button != previous.get(button.getPage())){
			button.play();
			previous.get(button.getPage()).reverse();
			previous.get(button.getPage()).clear();
			previous.set(button.getPage(), button);
		}
		if (otherText ==false){
			parent.addInfo(val);
		}
		else{
			parent.addInfo(((OptionHBox)(button)).getText());
		}

		
	}
	


}