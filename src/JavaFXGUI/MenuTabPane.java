package JavaFXGUI;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import backend.StudentList;
import javafx.scene.control.*;

/**
 * A Custom styled TabPane that can't close tabs.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class MenuTabPane extends TabPane{

	public StartTab init;
	public StartApplication parent;
	/**	
	 * Initializes a StartTab. Holds the public variable parent so that the StartTab can
	 * call methods in StartApplication
	 * 
	 * @param p The root node.
	 * @param data The data of the program.
	 */
	public MenuTabPane(StartApplication p, HashMap<String, StudentList> data, AtomicBoolean busMode){
		setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		 init = new StartTab(this, "Start", data, busMode);
		getTabs().addAll(init);
		parent = p;
	}
	



}
