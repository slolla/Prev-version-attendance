package JavaFXGUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import backend.Student;
import backend.StudentList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.*;

/**
 * The Starting tab of the program. 
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class StartTab extends Tab {
	private ObservableList<String> subentries = FXCollections.observableArrayList();
	private MenuTabPane parent;
	private HashMap<String, StudentList> data;
	private AnimatedAlertBox alert;
	private BorderPane content;
	private AtomicBoolean busMode;
	private ListView list;
	private ObservableList<String> nameEntries; 
	private TextField searchTextField;
	/**
	 * Initializes two buttons in the middle and a third View Records button in the bottom
	 * left
	 * @param p The Parent of this node.
	 * @param title The title of this tab.
	 * @param data The data of the program.
	 */
	public StartTab(MenuTabPane p, String title, HashMap<String, StudentList> data, AtomicBoolean bMode){
		busMode = bMode;

		this.data = data;
		parent = p;
		setText(title);
		content = new BorderPane();
		HBox viewButtonHBox = new HBox();
		viewButtonHBox.setPadding(new Insets(15, 12, 15, 12));
		viewButtonHBox.setSpacing(10);

		Button viewButton = new Button("View Records");
		viewButton.setPrefSize(150, 20);
		viewButton.setOnAction(e -> parent.parent.showOptionsPage());
		//viewButton.getStyleClass().add("startScreenButtons");
		viewButtonHBox.getChildren().add(viewButton);

		alert = new AnimatedAlertBox("Submission Sucessful!", false);

		content.setTop(alert);


		content.setBottom(viewButtonHBox);
		setContent(content);
		switchMode();

	}
	/**
	 * Goes to the next page by creating a EnterStudentTab.
	 * @param signIn
	 */
	private void moveOn(boolean signIn){
		EnterStudentTab tab2 = null;
		if (signIn){
			tab2 = new EnterStudentTab(parent, this, "Sign In",  data, signIn);
		}
		else{
			tab2 = new EnterStudentTab(parent, this, "Sign Out",  data, signIn);
		}
		setDisable(true);
		parent.getTabs().add(tab2);
		parent.getSelectionModel().select(tab2);

	}

	public void switchMode(){

		if (busMode.get()){
			list = new ListView<String>();


			Tooltip toolList = new Tooltip("Double click to select student, \n"
					+ "or select student and then click submit.");
			list.setTooltip(toolList);





			VBox imageHBox = new VBox();
			imageHBox.setAlignment(Pos.CENTER);
			imageHBox.setPadding(new Insets(15, 12, 15, 12));
			imageHBox.setSpacing(10);





			Image photoID  = new Image("img/image.png");
			ImageView photoIDView = new ImageView();
			photoIDView.setImage(photoID);
			photoIDView.setFitWidth(300);
			photoIDView.setPreserveRatio(true);
			photoIDView.setSmooth(true);
			photoIDView.setCache(true);

			HBox labelHBox = new HBox();
			imageHBox.setPadding(new Insets(15, 12, 15, 12));
			imageHBox.setSpacing(10);

			Label studentIDLabel = new Label("Enter student name or six-digit student ID for late bus: ");
			studentIDLabel.getStyleClass().add("studentIDLabel");
			Button submitButton = new Button("Submit");
			submitButton.setDefaultButton(true);
			submitButton.setPrefSize(100, 20);
			submitButton.setOnAction(e -> submitButton());
			labelHBox.getChildren().addAll(studentIDLabel);
			labelHBox.setAlignment(Pos.CENTER);





			nameEntries = FXCollections.observableArrayList(data.get("database").getInfoList());
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
						String currentItemSelected = (String) list.getSelectionModel().getSelectedItem();
						list.getSelectionModel().select(-1);
						submit(currentItemSelected);
					}
				}
			});



			imageHBox.getChildren().addAll(photoIDView, labelHBox, searchVBox, submitButton);

			content.setCenter(imageHBox);

		}
		else{
			HBox contentHBox = new HBox();
			contentHBox.setAlignment(Pos.CENTER);
			contentHBox.setPadding(new Insets(15, 12, 15, 12));
			contentHBox.setSpacing(10);




			Button buttonSignIn = new Button("SIGN IN");
			buttonSignIn.setPrefSize(300, 60);
			buttonSignIn.setOnAction(e -> moveOn(true));
			buttonSignIn.getStyleClass().add("startScreenButtons");

			Button buttonSignOut = new Button("SIGN OUT");
			buttonSignOut.setPrefSize(300, 60);
			buttonSignOut.setOnAction(e -> moveOn(false));
			buttonSignOut.getStyleClass().add("startScreenButtons");


			contentHBox.getChildren().addAll(buttonSignIn, buttonSignOut);
			content.setCenter(contentHBox);

		}

	}
	/**
	 * Displays a Success message when an entry is successfully submitted
	 */
	public void displaySuccess(){
		alert.setWarning(false);
		alert.play();
	}


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

	public void submitButton(){
		
		searchTextField.requestFocus();
		String selected = (String) list.getSelectionModel().getSelectedItem();
		String submittedText = "";
		if (selected == null){
			if (subentries.size() !=0){
				if (subentries.size() ==1){
					submittedText = subentries.get(0);
					ArrayList<String> toStringList = data.get("database").getInfoList();
					if (toStringList.contains(submittedText)){
						submit(submittedText);

					}
				}
				else{
					alert.setWarning(true);
					alert.play("Please continue entering information until there is only one possible student.");
				}


			}
			else{
				if (searchTextField.getText().isEmpty()){
					alert.setWarning(true);
	
					alert.play("Please submit your name or submit your ID.");
				}
				else{
					alert.setWarning(true);
					alert.play("The student \"" + searchTextField.getText() + "\" was not found.");
				}
			}




		}

		else{
			submit(selected);
		}
	}

	public void submit(String txt){
		searchTextField.clear();
		LocalDate todayDate = LocalDate.now();
		String date = todayDate.toString();
		
		
		
		Student newStudent = data.get("database").getStudentByToString(txt);
		//newStudent.setNote("No need - Late Bus");
		newStudent.setReason("Late Bus");
		data.get("in").add(newStudent);
		
		File f = new File("src/backup/" + date+"-IN.csv");
		try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			PrintWriter printWriter = new PrintWriter (f);
			printWriter.println("DATE,ID,NAME,GR,TIME,REASON,NOTE");
			for(Student st : data.get("in").getStudentList()){
				printWriter.print("\"" + st.getDate() + "\",");
				printWriter.print("\"" + st.getStudentID() + "\",");
				printWriter.print("\"" + st.getName() + "\",");
				printWriter.print("\"" + st.getGrade() + "\",");
				printWriter.print("\"" + st.getTime() + "\",");
				printWriter.print("\"" + st.getReason() + "\",");
				//printWriter.print("\"" + st.getNote() + "\"");
				printWriter.println();
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		alert.setWarning(false);
		alert.play("Student " + newStudent.getName() + " successfully recorded.");
		
	}
}
