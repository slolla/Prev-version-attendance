package JavaFXGUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import backend.Student;
import backend.StudentList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.util.Collections;

/**
 * A Tab where the Student that is signing In/Out is selected.
 * @author Kevin
 *
 */
@SuppressWarnings("restriction")
public class EnterStudentTab extends Tab {
	private boolean goingIn;
	private MenuTabPane parent;
	private StartTab previous;
	private ObservableList<String> nameEntries;  
	private ListView<String> list;
	private StudentList studentData;
	private HashMap<String, StudentList> data;
	private TextField searchTextField;
	private AnimatedAlertBox alert;
	private ObservableList<String> subentries = FXCollections.observableArrayList();

	/**
	 * Creates a Student Tab. On each element, a tooltip is present for information. 
	 * Also creates an AnimatedAlertBox that plays an alert whenever the selected student
	 * does not exist.
	 * @param par The parent of this node.
	 * @param prev The previous tab
	 * @param title The title of the tab
	 * @param d The data of which students signed in or out.
	 * @param gIn Whether or not the student is signing in or out.
	 */
	public EnterStudentTab(MenuTabPane par, StartTab prev, String title, HashMap<String, 
			StudentList> d, boolean gIn){

		list = new ListView<String>();

		Tooltip toolList = new Tooltip("Double click to select student, \n"
				+ "or select student and then click submit.");
		list.setTooltip(toolList);



		goingIn = gIn;

		parent = par;
		previous = prev;
		studentData = d.get("database");
		data = d;

		setText(title);
		BorderPane content = new BorderPane();

		VBox imageHBox = new VBox();
		imageHBox.setAlignment(Pos.CENTER);
		imageHBox.setPadding(new Insets(15, 12, 15, 12));
		imageHBox.setSpacing(10);
		
		alert = new AnimatedAlertBox("The ID or Student name does not exist.", true);




		Image photoID  = new Image("img/image.png");
		ImageView photoIDView = new ImageView();
		photoIDView.setImage(photoID);
		photoIDView.setFitWidth(300);
		photoIDView.setPreserveRatio(true);
		photoIDView.setSmooth(true);
		photoIDView.setCache(true);

		HBox labelHBox = new HBox();


		Label studentIDLabel = new Label("Enter student name or six-digit student ID: ");
		studentIDLabel.getStyleClass().add("studentIDLabel");
		Button submitButton = new Button("Submit");
		submitButton.setDefaultButton(true);
		submitButton.setPrefSize(100, 20);
		submitButton.setOnAction(e -> submitButton());
		submitButton.getStyleClass().add("submitButton");

		labelHBox.getChildren().addAll(studentIDLabel);
		labelHBox.setAlignment(Pos.CENTER);

		HBox navHBox = new HBox();
		navHBox.setPadding(new Insets(15, 12, 15, 12));
		navHBox.setSpacing(10);

		Button backButton = new Button("Back");
		backButton.setOnAction(e -> goBack(false));
		backButton.setPrefSize(150, 20);

		navHBox.getChildren().add(backButton);



		nameEntries = FXCollections.observableArrayList(studentData.getInfoList());
		FXCollections.sort(nameEntries, new StudentComparator());
		searchTextField = new TextField();
		searchTextField.setPromptText("Search");
		searchTextField.textProperty().addListener(
				new ChangeListener<Object>() {
					public void changed(ObservableValue<?> observable, 
							Object oldVal, Object newVal) {
						searchStudent(nameEntries, (String)oldVal, (String)newVal);
					}
				});
		Platform.runLater(new Runnable() {
			public void run() {
				searchTextField.requestFocus();
			}
		});

		Tooltip toolTextField = new Tooltip(" Enter in keywords "
				+ "\n separated by a space. ");
		searchTextField.setTooltip(toolTextField);

		list.setMaxHeight(400);
		list.setItems(nameEntries);
		list.getStyleClass().add("searchTextField");

		VBox searchVBox = new VBox();
		searchVBox.setPadding(new Insets(15, 12, 15, 12));
		searchVBox.setSpacing(10);
		searchVBox.setMaxSize(500, 300);
		searchVBox.getChildren().addAll(searchTextField, list);
		list.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					String currentItemSelected = list.getSelectionModel().getSelectedItem();
					list.getSelectionModel().select(-1);
					moveOn(goingIn, data.get("database").getStudentByToString(currentItemSelected));
				}
			}
		});



		imageHBox.getChildren().addAll(searchVBox, submitButton);
		content.setCenter(imageHBox);
		content.setBottom(navHBox);
		content.setTop(alert);
		setContent(content);
	}

	/**
	 * Closes the current tab. Displays a displays a Success AlertAnimatedBox if there is a submission
	 * as opposed to just closing the tab (the back button is clicked, for example)
	 * @param sucess Whether or not the submission was a sucess
	 */
	public void goBack(boolean sucess){
		previous.setDisable(false);
		parent.getSelectionModel().select(previous);
		die();
		if (sucess){
			previous.displaySuccess();
		}
	}
	/**
	 * Kills this tab.
	 */
	public void die(){
		parent.getTabs().remove(this);
	}

	/**
	 * Searches for a student within an ObservableList. Every time a key is pressed
	 * in a textField, this method is called. Updates the ListView list accordingly. 
	 * The search functions uses every space-separated word as a keyword, and an 
	 * element that is deemed part of the search must have EVERY keyword.
	 * @param entries The ObservableList of Entries
	 * @param oldVal The previous value of the search
	 * @param newVal The new value of the Search.
	 */
	public void searchStudent(ObservableList<String> entries, String oldVal, String newVal) {

		if ( oldVal != null && (newVal.length() < oldVal.length()) ) {
			list.setItems( entries );
		}

		String[] parts = newVal.toUpperCase().split(" ");

		subentries = FXCollections.observableArrayList();
		for ( Object entry: list.getItems() ) {
			boolean match = true;
			String entryText = (String)entry;
			for ( String part: parts ) {
				if ( ! entryText.toUpperCase().contains(part) ) {
					match = false;
					break;
				}
			}

			if ( match ) {
				subentries.add(entryText);
			}
		}
		list.setItems(subentries);
	}

	/**
	 * Manages logic for the submit Button. Plays an alert if the student was not found,
	 * and calls moveOn otherwise 
	 */
	private void submitButton(){
		searchTextField.requestFocus();
		String selected = list.getSelectionModel().getSelectedItem();
		String submittedText = "";
		if (selected == null){
			if (subentries.size() !=0){
				if (subentries.size() ==1){
					submittedText = subentries.get(0);
					ArrayList<String> toStringList = data.get("database").getInfoList();
					if (toStringList.contains(submittedText)){
						moveOn(goingIn, data.get("database").getStudentByToString(submittedText));
					}
				}
				else{
					alert.play("Please continue entering information until there is only one possible student.");
				}


			}
			else{
				if (searchTextField.getText().isEmpty()){
					alert.play("Please submit your name or submit your ID.");
				}
				else{

					alert.play("The student \"" + searchTextField.getText() + "\" was not found.");
				}
			}




		}

		else{
			moveOn(goingIn, data.get("database").getStudentByToString(selected));
		}
	}


	/**
	 * Manages logic moving on. If the student is signing back into school (they are signing in
	 * and they have signed out, the arrival time of the Student data structure is updated,
	 * the backup file for the program is updated, and the user is transported to the starting Tab.
	 * Otherwise, the appropriate version (sign-in) or (sign-out) of the EnterInfoTab is created.
	 * @param signIn
	 * @param student
	 */
	private void moveOn(boolean signIn, Student student){
		EnterInfoTab tab3;
		boolean outin = false;
		int j = 0;
		for (int i =0; i < data.get("outin").getStudentList().size(); i++){
			if (student.equals(data.get("outin").getStudentList().get(i))){
				j = i;
				outin=true;
			}

		}
		if (outin){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
			LocalTime todayTime = LocalTime.now();
			String time = formatter.format(todayTime);

			data.get("outin").getStudentList().get(j).setArrTime(time);
			LocalDate todayDate = LocalDate.now();
			String date = todayDate.toString();
			File f = new File("src/backup/" + date+"-OUT.csv");
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				PrintWriter printWriter = new PrintWriter (f);
				printWriter.println("DATE,ID,NAME,GR,REASON,EXCUSED,TIME,ARRTIME");
				for(Student st : data.get("outin").getStudentList()){
					printWriter.print("\"" + st.getDate() + "\",");
					printWriter.print("\"" + st.getStudentID() + "\",");
					printWriter.print("\"" + st.getName() + "\",");
					printWriter.print("\"" + st.getGrade() + "\",");
					printWriter.print("\"" + st.getReason() + "\",");
					printWriter.print("\"" + st.getExcused() + "\",");
					printWriter.print("\"" + st.getTime() + "\",");
					printWriter.print("\"" + st.getArrTime() + "\",");
					//printWriter.print("\"" + st.getNote() + "\"");
					printWriter.println();
				}
				printWriter.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			goBack(true);
		}
		else 
		{
			if (signIn){
				tab3= new EnterInfoTab(parent, this, "Enter Information",  data, signIn, student);
			}
			else{
				tab3 = new EnterInfoTab(parent, this, "Enter Information",  data, signIn, student);
			}
			setDisable(true);
			parent.getTabs().add(tab3);
			parent.getSelectionModel().select(tab3);
		}

	}



}
