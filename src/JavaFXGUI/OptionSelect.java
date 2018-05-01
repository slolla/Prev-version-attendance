package JavaFXGUI;
import java.util.ArrayList;

import backend.Student;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.event.*;
/**
 * Creates a carousel of options that the student uses to select his/her reasons for signing in/out.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class OptionSelect extends VBox{
	private ArrayList<ArrayList<OptionButton>> buttonList;
	private ArrayList<String> title = new ArrayList<String>();
	private VBox buttonVBox;
	private ArrayList<String> option = new ArrayList<String>();
	private int page;
	private Label titleLabel;
	private int height, width;
	private HBox bottomHBox;
	private Label pageNumberLabel;
	private Button submitButton;
	private EnterInfoTab tabToBeClosed;
	private Student student;
	private Button pageButtonLeft;
	private Button pageButtonRight; 
	private FadeTransition ftIn;
	private FadeTransition ftOut;
	private ArrayList<OptionHBox> optionHBoxArray = new ArrayList<OptionHBox>();
	/**
	 * Constructor. The data of the optionSelect is represented by an ArrayList of Strings that indicates what options where selected.
	 * @param w Width of the optionSelect
	 * @param h Height of the optionSelect
	 * @param close The Tab to be closed when the options are submitted.
	 * @param st The Student selected
	 */
	public OptionSelect(int w, int h, EnterInfoTab close, Student st){
		buttonList = new ArrayList<ArrayList<OptionButton>>();
		height = h;
		width = w;
		student = st;
		tabToBeClosed = close;

		init();


	}
	/**
	 * Initializes contents of the optionSelect.
	 */
	public void init(){

		
	    ftIn = new FadeTransition(Duration.millis(250), buttonVBox);
		ftIn.setFromValue(0);
		ftIn.setToValue(1.0);
		ftIn.setCycleCount(1);

		
		ftOut = new FadeTransition(Duration.millis(250), buttonVBox);
		ftOut.setFromValue(1.0);
		ftOut.setToValue(0);
		ftOut.setCycleCount(1);
		

		setMaxHeight(height);
		setMaxWidth(width);

		submitButton = new Button("Submit");
		submitButton.getStyleClass().add("submitButton");
		submitButton.setOnAction(new submitHandler());




		titleLabel= new Label();
		titleLabel.getStyleClass().add("optionTitle");


		getChildren().add(titleLabel);
		getStyleClass().add("optionSelect");
		HBox contentHBox = new HBox();

		pageButtonLeft = new Button();
		pageButtonLeft.getStyleClass().add("pageButton-left");
		pageButtonLeft.setOnAction(new NextHandler(false));

		pageButtonRight = new Button();
		pageButtonRight.getStyleClass().add("pageButton-right");
		pageButtonRight.setOnAction(new NextHandler(true));

		contentHBox.getChildren().add(pageButtonLeft);
		buttonVBox = new VBox();
		buttonVBox.getStyleClass().add("buttonVBox");


		contentHBox.getChildren().addAll(buttonVBox, pageButtonRight);
		getChildren().add(contentHBox);

		bottomHBox = new HBox();
		pageNumberLabel = new Label();

		bottomHBox.getChildren().add(pageNumberLabel);
		bottomHBox.getStyleClass().add("bottomHBox");
		getChildren().add(bottomHBox);


	}
	/**
	 * Adds a button.
	 * @param page The page of the carousel of the button
	 * @param name The text displayed in the button.
	 * @param mes The value of the Button
	 */
	public void addButton(int page, String name, String mes){
		buttonList.get(page).add(new OptionButton(name, mes, page));
		updateState(0);
	}


	/**
	 * Adds a page in the carousel.
	 * @param t The title of the page (usually the question).
	 */
	public void addPage(String t){
		buttonList.add(new ArrayList<OptionButton>());
		title.add(t);
		option.add("");
		OptionHBox textFieldOtherHBox  = new OptionHBox(width, this, buttonList.size()-1);
		textFieldOtherHBox.getStyleClass().add("optionTextFieldOther");
		optionHBoxArray.add(textFieldOtherHBox);
		updateState(0);
	}

	/**
	 * Updates the state of the carousel. This is called every time the arrow buttons are clicked,
	 * or when a button/page is added. This method scales every child of the OptionSelect
	 * to fit the OptionSelect.
	 * @param pg The page to be displayed
	 */
	private void updateState(int pg){
		if (pg == 0){
			pageButtonLeft.setDisable(true);
			pageButtonRight.setDisable(false);
		}
		else if (pg == buttonList.size()){
			pageButtonLeft.setDisable(false);
			pageButtonRight.setDisable(true);
		}
		else{
			pageButtonLeft.setDisable(false);
			pageButtonRight.setDisable(false);
		}
		titleLabel.setText(title.get(pg));
		page = pg;

		buttonVBox.getChildren().clear();
		double buttonHeight = (double)(height-80)/ (buttonList.get(page).size()+1);
		double buttonWidth = (double)(width-100);

		if (buttonList.get(page).size() >=3){
			buttonList.get(page).get(0).setPosStyle("top");
			buttonList.get(page).get(0).setOnAction(new ButtonHandler(buttonList.get(page).get(0).getValue(), buttonList.get(page).get(0),this));
			buttonList.get(page).get(0).setPrefHeight(buttonHeight);
			buttonList.get(page).get(0).setPrefWidth(buttonWidth);
			buttonVBox.getChildren().add(buttonList.get(page).get(0));
			for (int i = 1; i < buttonList.get(page).size()-1; i++){
				buttonList.get(page).get(i).setPosStyle("mid");
				buttonVBox.getChildren().add(buttonList.get(page).get(i));

				buttonList.get(page).get(i).setPrefHeight(buttonHeight);
				buttonList.get(page).get(i).setPrefWidth(buttonWidth);

				buttonList.get(page).get(i).setOnAction(new ButtonHandler(buttonList.get(page).get(i).getValue(), buttonList.get(page).get(i),this));
			}
			buttonList.get(page).get(buttonList.get(page).size()-1).setPosStyle("mid");
			buttonList.get(page).get(buttonList.get(page).size()-1).setOnAction(new ButtonHandler(buttonList.get(page).get(buttonList.get(page).size()-1).getValue(),buttonList.get(page).get(buttonList.get(page).size()-1), this));
			buttonList.get(page).get(buttonList.get(page).size()-1).setPrefHeight(buttonHeight);
			buttonList.get(page).get(buttonList.get(page).size()-1).setPrefWidth(buttonWidth);
			buttonVBox.getChildren().add(buttonList.get(page).get(buttonList.get(page).size()-1));
		}
		else if (buttonList.get(page).size() == 2){
			buttonList.get(page).get(0).setPosStyle("top");
			buttonList.get(page).get(0).setOnAction(new ButtonHandler(buttonList.get(page).get(0).getValue(), buttonList.get(page).get(0),this));
			buttonList.get(page).get(0).setPrefHeight(buttonHeight);
			buttonList.get(page).get(0).setPrefWidth(buttonWidth);
			buttonVBox.getChildren().add(buttonList.get(page).get(0));

			buttonList.get(page).get(buttonList.get(page).size()-1).setPosStyle("mid");
			buttonList.get(page).get(buttonList.get(page).size()-1).setOnAction(new ButtonHandler(buttonList.get(page).get(buttonList.get(page).size()-1).getValue(), buttonList.get(page).get(buttonList.get(page).size()-1), this));
			buttonList.get(page).get(buttonList.get(page).size()-1).setPrefHeight(buttonHeight);
			buttonList.get(page).get(buttonList.get(page).size()-1).setPrefWidth(buttonWidth);
			buttonVBox.getChildren().add(buttonList.get(page).get(buttonList.get(page).size()-1));

		}
		else if (buttonList.get(page).size() == 1){
			buttonList.get(page).get(0).setPosStyle("mid");
			buttonList.get(page).get(0).setPrefHeight(buttonHeight);
			buttonList.get(page).get(0).setPrefWidth(buttonWidth);
			buttonList.get(page).get(0).setOnAction(new ButtonHandler(buttonList.get(page).get(0).getValue(), buttonList.get(page).get(0),this));
			buttonVBox.getChildren().add(buttonList.get(page).get(0));
		}
		


		optionHBoxArray.get(page).setPrefHeight(buttonHeight);
		optionHBoxArray.get(page).setPrefWidth(buttonWidth);

		pageNumberLabel.setText(page + 1+ " / " + (buttonList.size() + 1));
		buttonVBox.getChildren().add(optionHBoxArray.get(page));
		tabToBeClosed.updateScrollPane(option);
		tabToBeClosed.updateAnimation(pg);


	}

	/**
	 * This is called by the ButtonHandler to add information to the Data.
	 * @param mes The value of the button clicked to be added.
	 */
	public void addInfo(String mes){
		option.set(page, mes);
	}

	private void setPage(boolean next){
		if (next == true && (page + 1 < buttonList.size())){
			transitionPage(page+1);
		}
		else if ((next != true) && (page - 1 >= 0)){
			transitionPage(page-1);
		}
		else if ((next == true) && (page + 1 == buttonList.size())){
			transitionPage(page+1);
		}
	}

	/**
	 * This is called when the arrow buttons are clicked. If the current page is equal to the amount of pages.
	 * the User is brought to a custom Page with a submit button.
	 * @param pg
	 */
	private void transitionPage(int pg){

		if (pg == buttonList.size()){
			tabToBeClosed.updateScrollPane(option);
			tabToBeClosed.updateAnimation(pg);
			ftOut.play();
			buttonVBox.getChildren().clear();
			titleLabel.setText("Please submit your responses: ");
			
			VBox labelVBox = new VBox();

			labelVBox.setMaxWidth(400);
			buttonVBox.getChildren().add(labelVBox);
			buttonVBox.getChildren().add((submitButton));
			
			buttonVBox.setPrefHeight(height -90);
			buttonVBox.setPrefWidth(width - 100);
			pageNumberLabel.setText(pg + 1 +" / " + (buttonList.size() + 1));
			page = page + 1;
			ftIn.play();
			pageButtonRight.setDisable(true);
			pageButtonLeft.setDisable(false);
		}
		else{
			ftOut.play();
			updateState(pg);
			ftIn.play();
		}




	}
	/**
	 * The class that handles going to the next page.
	 * @author Kevin
	 *
	 */
	private class NextHandler implements EventHandler<ActionEvent> {
		private boolean next ;
		public NextHandler(boolean n) {
			this.next = n;
		}
		@Override
		public void handle(ActionEvent event) {
			setPage(next);
		}

	}
	
	/**
	 * The class that handles submitting the Data.
	 * @author Kevin
	 *
	 */

	private class submitHandler implements EventHandler<ActionEvent> {
		@Override
		
		public void handle(ActionEvent event) {
			tabToBeClosed.addData(option);
		}

	}
	
	public ArrayList<String> getOption(){
		ArrayList<String> returnArr = new ArrayList<String>();
		for (String o : option){
			returnArr.add(o);
		}
		return returnArr;
	}
	
	public ArrayList<String> getPageTitles(){
		ArrayList<String> titleArray = new ArrayList<String>();

		for (String t : title){
			titleArray.add(t);
		}
		return titleArray;
	}
	
	public int getPage(){
		return page;
	}
	
}
