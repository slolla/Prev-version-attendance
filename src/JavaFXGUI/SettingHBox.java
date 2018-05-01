package JavaFXGUI;
import java.io.File;
import java.util.concurrent.atomic.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.concurrent.WorkerStateEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.print.*;
import javafx.util.*;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.cell.*;
import javafx.collections.*;
import javafx.event.EventHandler;
import backend.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.web.*;

/**
 * A Panel that contains all the information on settings. 
 * Inside are a print button, a loading file button, a save button,
 * a changePassword button, and a TabPane with Two tables that displays the data so far.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class SettingHBox extends HBox{
	private ObservableList<StudentProperty> goingIn = FXCollections.observableArrayList();
	private ObservableList<StudentProperty> goingOutIn =FXCollections.observableArrayList();
	private EmailHandler sendEmail;
	private StartApplication parent;
	private TableView tableSignOut;
	private TableView tableSignIn;
	private String passwordHash;
	private String salt;
	private HashMap<String, StudentList>data;
	private AtomicBoolean busMode;
	private String[] headers = {"Date", "Student ID", "Student Name", "Grade", "Time In", 
			"Reason for Late Arrival"};
	private String[] headersOut = {"Date", "Student ID", "Student Name", "Grade", "Reason for leaving", 
			"Excused By", "Time of Departure", "Time of Return"};
	@SuppressWarnings({ "rawtypes", "unchecked" })

	/**
	 * Initiliazes the elements of this class. 
	 * @param p The parent class.
	 * @param width The width of this class.
	 * @param d The data of this class.
	 * @param pHash The password hash.
	 * @param s The salt of the password hash.
	 */
	public SettingHBox(StartApplication p, double width, 
			HashMap<String, StudentList>d, String pHash, String s, AtomicBoolean bmode){
		busMode = bmode;
		passwordHash = pHash;
		salt = s;
		data = d;
		parent = p;
		for (Student st : data.get("in").getStudentList())	{
			goingIn.add(new StudentProperty(st));
		}

		for (Student st : data.get("outin").getStudentList())	{
			goingOutIn.add(new StudentProperty(st));
		}

		HBox contentHBox = new HBox();
		VBox buttonVBox = new VBox();
		AnchorPane buttonAnchorPane = new AnchorPane();
		buttonAnchorPane.getStyleClass().add("buttonAnchorPane");

		HBox viewButtonHBox = new HBox();
		viewButtonHBox.setPadding(new Insets(15, 12, 15, 12));
		viewButtonHBox.setSpacing(10);

		Button printButton = new Button("Print");
		printButton.setPrefSize(150, 40);
		printButton.setMinSize(150, 40);
		printButton.setOnAction(e -> print());

		Button loadButton = new Button("Load");
		loadButton.setPrefSize(150, 40);
		loadButton.setMinSize(150, 40);
		loadButton.getStyleClass().add("loadButton");
		loadButton.setOnAction(e -> openFile());

		Button saveButton = new Button("Save");
		saveButton.setPrefSize(150, 40);
		saveButton.setMinSize(150, 40);
		saveButton.getStyleClass().add("saveButton");
		saveButton.setOnAction(e -> saveFile());

		Button changePasswordButton = new Button("Change Password");
		changePasswordButton.setPrefSize(150, 40);
		changePasswordButton.setMinSize(150, 40);
		changePasswordButton.getStyleClass().add("changePasswordButton");
		changePasswordButton.setOnAction(e -> changePassword());

		Button sendEmailButton = new Button("Send Report");
		sendEmailButton.setPrefSize(150, 40);
		sendEmailButton.setMinSize(150, 40);
		sendEmailButton.setOnAction(e -> sendEmail());
		CheckBox checkBox = new CheckBox("Late Bus Mode");
		checkBox.setSelected(busMode.get());
		checkBox.selectedProperty().addListener((ov, old_val, new_val) ->
		{
			busMode.set(new_val);
		}
		);
		
		Button closeButton = new Button("Close");
		closeButton.setPrefSize(100, 40);
		closeButton.setMinSize(100, 40);
		closeButton.setOnAction(e -> parent.hideOptionsPage());
		closeButton.getStyleClass().add("closeButton");

		viewButtonHBox.getChildren().add(closeButton);
		buttonVBox.getChildren().addAll(printButton, loadButton, saveButton, 
				changePasswordButton, sendEmailButton, checkBox);

		buttonVBox.setPadding(new Insets(15, 15, 15, 15));
		buttonVBox.setSpacing(20);

		AnchorPane.setTopAnchor(buttonVBox, 0.0);
		AnchorPane.setLeftAnchor(buttonVBox, 0.0);

		AnchorPane.setBottomAnchor(closeButton, 15.0);
		AnchorPane.setLeftAnchor(closeButton, 15.0);

		buttonAnchorPane.getChildren().addAll(buttonVBox, closeButton);
		contentHBox.getChildren().addAll(buttonAnchorPane);

		getChildren().add(contentHBox);

		TabPane tableTabPane = new TabPane();
		tableTabPane.setSide(Side.LEFT);
		tableTabPane.getStyleClass().add("tabTable");

		tableSignIn = createTableIn();

		tableSignOut = createTableInOut();


		tableSignOut.setPrefWidth(3000);
		tableSignIn.setPrefWidth(3000);
		Tab signInTableTab = new Tab("Sign In Table");
		signInTableTab.setContent(tableSignIn);

		Tab signOutTableTab = new Tab("Sign Out Table");
		signOutTableTab.setContent(tableSignOut);
		tableTabPane.getTabs().addAll(signInTableTab, signOutTableTab);
		tableTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		getChildren().add(tableTabPane);
	}
	/**
	 * Utility fuction for creating a TableColumn
	 * @param mes The title of the TableColumn
	 * @return The TableColumn
	 */
	@SuppressWarnings("rawtypes")
	private TableColumn createTableColumn(String mes){
		return new TableColumn(mes);
	}


	/**
	 * Prints the two tables, with a page break in between. The data of the program is 
	 * converted to HTML format, which is then printed. 
	 */
	@SuppressWarnings("unchecked")
	private void print(){
		String htmlIn = "<!DOCTYPE html> <html> <head>"
				+ "</head>"
				+ "<body>"
				+"<h1> Sign In Data</h1>"
				+ "<table border = '1' style='border-collapse:collapse;'> <thead>"
				+ "<tr>";
		for (String t : headers){
			htmlIn = htmlIn + "<th>"+t+"</th>";
		}
		htmlIn = htmlIn + "</tr></thead>";
		htmlIn = htmlIn + "<tbody>";
		for (Student st: data.get("in").getStudentList()){
			htmlIn = htmlIn + "<tr>";
			htmlIn = htmlIn + "<td>" + st.getDate() + "</td>";
			htmlIn = htmlIn + "<td>" + st.getStudentID() + "</td>";
			htmlIn = htmlIn + "<td>" + st.getName() + "</td>";
			htmlIn = htmlIn + "<td>" + st.getGrade() + "</td>";
			htmlIn = htmlIn + "<td>" + st.getTime() + "</td>";
			htmlIn = htmlIn + "<td>" + st.getReason() + "</td>";
			//htmlIn = htmlIn + "<td>" + st.getNote() + "</td>";
		}

		String htmlOut = "<!DOCTYPE html> <html> <head>"
				+ "</head>"
				+ "<body>"
				+ "<h1> Sign out Data</h1>"
				+ "<table border = '1' style='border-collapse:collapse;'> <thead>"
				+ "<tr>";
		htmlOut = htmlOut + "<table border = '1' style='border-collapse:collapse'> <thead>"
				+ "<tr>";
		for (String t : headersOut){
			htmlOut = htmlOut + "<th>"+t+"</th>";
		}
		htmlOut = htmlOut + "</tr></thead>";
		htmlOut = htmlOut + "<tbody>";
		for (Student st: data.get("outin").getStudentList()){
			htmlOut = htmlOut + "<tr>";
			htmlOut = htmlOut + "<td>" + st.getDate() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getStudentID() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getName() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getGrade() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getReason() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getExcused() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getTime() + "</td>";
			htmlOut = htmlOut + "<td>" + st.getArrTime() + "</td>";
			//htmlOut = htmlOut + "<td>" + st.getNote() + "</td>";
		}
		htmlOut = htmlOut + "</tbody></table></body></html>";



		WebView browserIn = new WebView();

		browserIn.getEngine().loadContent(htmlIn);
		WebView browserOut = new WebView();

		browserOut.getEngine().loadContent(htmlOut);

		PrinterJob job = PrinterJob.createPrinterJob();
		boolean print = job.showPrintDialog(null);
		if (job != null && print) {
			browserIn.getEngine().print(job);
			browserOut.getEngine().print(job);
			job.endJob();
		}

	}

	/**
	 * Opens a database mer file and updates the database stored by the program. 
	 * If the file is in an incorrect format.
	 */
	private void openFile(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Database File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Database Files", "*.mer"));
		File selectedFile = fileChooser.showOpenDialog(parent.stage);
		if (selectedFile != null){
			StudentList studentData =  parent.readStudentDatabase(selectedFile.getPath(), "d");
			if (studentData != null){
				data.put("database", studentData);
				String text = "";
				try {
					text = new String(Files.readAllBytes(Paths.get(selectedFile.getPath())), StandardCharsets.UTF_8);
				} catch (IOException e) {


					e.printStackTrace();
				}
				try {
					PrintWriter writer = new PrintWriter(new FileOutputStream("src/data/students2018.mer", false));
					writer.print(text);
					writer.close();
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
			}

		}


	}
	/**
	 * Saves the files into a .csv for later viewing, preferably with Excel. 
	 */
	private void saveFile(){
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Save Log File");
		File selectedFile = fileChooser.showDialog((parent.stage));
		PrintWriter writer = null;
		LocalDate todayDate = LocalDate.now();
		String date = todayDate.toString();

		if (selectedFile!=null){
			try {
				writer = new PrintWriter(selectedFile.getPath() + "/" + date + "-Sign-In.csv");
			} catch (FileNotFoundException e) {
				System.out.println("ERROR");
				e.printStackTrace();
			}


			for (String i : headers){
				writer.print( i+ ", ");
			}
			writer.println();
			ArrayList<Student> temp =  data.get("in").getStudentList();
			for (int i = 0; i < temp.size(); i++){
				writer.print (temp.get(i).getDate() + ", ");
				writer.print( temp.get(i).getName() + ", ");
				writer.print( temp.get(i).getStudentID() + ", ");
				writer.print( temp.get(i).getGrade() + ", ");
				writer.print( temp.get(i).getTime() + ", ");
				writer.print( temp.get(i).getReason() + ", ");
				//writer.print( temp.get(i).getNote() + ", ");
			}


			writer.close();

			PrintWriter writerOutIn = null;
			try {
				writerOutIn = new PrintWriter(selectedFile.getPath() + "/" + date+"-Sign-Out.csv");
			} catch (FileNotFoundException e) {
				System.out.println("EROROR");
				e.printStackTrace();
			}

			for (String i : headersOut){
				writerOutIn.print("  \" "  + i+ " \", ");
			}
			writerOutIn.println();	
			temp =  data.get("outIn").getStudentList();
			for (int i = 0; i < temp.size(); i++){
				writerOutIn.print(temp.get(i).getDate() + ", ");
				writerOutIn.print(temp.get(i).getName() + " , ");
				writerOutIn.print(temp.get(i).getStudentID() + ", ");
				writerOutIn.print (temp.get(i).getGrade() + ", ");
				writerOutIn.print(temp.get(i).getReason() + ", ");
				writerOutIn.print( temp.get(i).getExcused() + ", ");
				writerOutIn.print( temp.get(i).getTime() + ", ");
				writerOutIn.print(temp.get(i).getArrTime() + ", ");
				//writerOutIn.print(temp.get(i).getNote() + ", ");
			}
			writerOutIn.close();
		}
	}
	/**
	 * Creates a dialogue that asks to change the password. 
	 * Inside are three fields, one can only change the password if the old password
	 * is correct and both new passwords are equal.
	 */
	public void changePassword(){

		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Change Password");
		dialog.setHeaderText("Please enter your old, then new password below");
		ButtonType loginButtonType = new ButtonType("Change", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		PasswordField oldPasswordTextField = new PasswordField();
		oldPasswordTextField.setPromptText("Old Password");
		oldPasswordTextField.setPrefWidth(250);
		oldPasswordTextField.getStyleClass().add("passwordTextField");

		PasswordField newPasswordTextField = new PasswordField();
		newPasswordTextField.setPromptText("New Password");
		newPasswordTextField.setPrefWidth(250);
		newPasswordTextField.getStyleClass().add("passwordTextField");


		PasswordField newRepeatPasswordTextField = new PasswordField();
		newRepeatPasswordTextField.setPromptText("Confirm New Password");
		newRepeatPasswordTextField.setPrefWidth(250);
		newRepeatPasswordTextField.getStyleClass().add("passwordTextField");

		grid.add(new Label("Enter Old Password:"), 0, 0);
		grid.add(oldPasswordTextField, 1, 0);
		grid.add(new Label("Enter New Password:"), 0, 1);
		grid.add(newPasswordTextField, 1, 1);
		grid.add(new Label("Confirm New Password:"), 0, 2);
		grid.add(newRepeatPasswordTextField, 1, 2);

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(oldPasswordTextField.getText(), newPasswordTextField.getText());
			}
			return null;
		});

		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);
		oldPasswordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newPasswordTextField.getText().isEmpty() || !(passwordHash.equals( SettingConfig.generateHash(newValue, salt)))
					|| !(newPasswordTextField.getText().equals(newRepeatPasswordTextField.getText())));
			if(passwordHash.equals( SettingConfig.generateHash(newValue, salt))){
				if (oldPasswordTextField.getStyleClass().size() == 4){
					oldPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else{
					oldPasswordTextField.getStyleClass().remove(4);
					oldPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
			}
			else{
				if (oldPasswordTextField.getStyleClass().size() == 4){
					oldPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else{
					oldPasswordTextField.getStyleClass().remove(4);
					oldPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
			}
		});



		newPasswordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(!(passwordHash.equals(SettingConfig.generateHash(oldPasswordTextField.getText(), salt)))
					|| !(newPasswordTextField.getText().equals(newRepeatPasswordTextField.getText())));

			if(newPasswordTextField.getText().equals(newRepeatPasswordTextField.getText())){
				if (newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else if(newPasswordTextField.getStyleClass().size() != 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else if(newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() != 4){
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else{
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
			}
			else{
				if (newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else if(newPasswordTextField.getStyleClass().size() != 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else if(newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() != 4){
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else{
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
			}

		});


		newRepeatPasswordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(!(passwordHash.equals( SettingConfig.generateHash(oldPasswordTextField.getText(), salt)))
					|| !(newPasswordTextField.getText().equals(newRepeatPasswordTextField.getText())));
			if(newPasswordTextField.getText().equals(newRepeatPasswordTextField.getText())){
				if (newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else if(newPasswordTextField.getStyleClass().size() != 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else if(newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() != 4){
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
				else{
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-success");
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-success");
				}
			}
			else{
				if (newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else if(newPasswordTextField.getStyleClass().size() != 4 && 
						newRepeatPasswordTextField.getStyleClass().size() == 4){
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else if(newPasswordTextField.getStyleClass().size() == 4 && 
						newRepeatPasswordTextField.getStyleClass().size() != 4){
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
				else{
					newPasswordTextField.getStyleClass().remove(4);
					newPasswordTextField.getStyleClass().add("passwordTextField-error");
					newRepeatPasswordTextField.getStyleClass().remove(4);
					newRepeatPasswordTextField.getStyleClass().add("passwordTextField-error");
				}
			}
		});
		dialog.getDialogPane().getStylesheets().add("css/application.css");
		ButtonBar buttonBar = (ButtonBar)dialog.getDialogPane().lookup(".button-bar");
		buttonBar.getButtons().get(0).getStyleClass().add("saveButton");
		buttonBar.getButtons().get(1).getStyleClass().add("closeButton");


		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(newOldPassword -> {
			SettingConfig.writeSettingsFile("src/data/settings.config", newOldPassword.getValue());
		});

	}

	@SuppressWarnings("unchecked")
	/**
	 * Creates a TableView with the sign in data format.
	 * @return A TableView Sign In.
	 */
	public TableView createTableIn(){
		TableView tableSignIn = new TableView();


		ArrayList<TableColumn> columnList = new ArrayList<TableColumn>();


		double[] widths = {0.1, 0.15, 0.15, 0.07, 0.1, 0.25, 0.18}; 

		for(int i = 0; i < headers.length; i++){
			columnList.add(createTableColumn(headers[i]));
			columnList.get(i).setResizable(false);
			columnList.get(i).prefWidthProperty().bind(tableSignIn.widthProperty().multiply(widths[i]));
			if (i != 3){
				columnList.get(i).setCellFactory(column -> {
					return new TableCell<StudentProperty, String>() {
						@Override
						protected void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							Text text = new Text(item);
							text.wrappingWidthProperty().bind(getTableColumn().widthProperty()); // Setting the wrapping width to the Text
							setGraphic(text);	
						}
					};
				});
				columnList.get(i).setSortable(false);
			}
			tableSignIn.getColumns().add(columnList.get(i));

		}

		columnList.get(0).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("date"));
		columnList.get(1).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("studentID"));
		columnList.get(2).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("name"));
		columnList.get(3).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				Integer>("grade"));
		columnList.get(4).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("time"));
		columnList.get(5).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("reason"));
		//columnList.get(6).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				//String>("note"));

		tableSignIn.setItems(goingIn);
		return tableSignIn;

	}

	/**
	 * Creates a TableView with a sign out format.
	 * @return A sign out TableView
	 */
	@SuppressWarnings("unchecked")
	public TableView createTableInOut(){
		TableView tableSignOut = new TableView();


		ArrayList<TableColumn> columnListOut = new ArrayList<TableColumn>();


		double[] widthsOut = {0.075, 0.125, 0.115, 0.05, 0.15, 0.1, 0.15, 0.125, 0.11}; 

		for(int i = 0; i < headersOut.length; i++){
			columnListOut.add(createTableColumn(headersOut[i]));
			columnListOut.get(i).setResizable(false);
			columnListOut.get(i).prefWidthProperty().bind(tableSignOut.widthProperty().multiply(widthsOut[i]));
			if (i != 3){
				columnListOut.get(i).setCellFactory(column -> {
					return new TableCell<StudentProperty, String>() {
						@Override
						protected void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							Text text = new Text(item);
							text.wrappingWidthProperty().bind(getTableColumn().widthProperty()); // Setting the wrapping width to the Text
							setGraphic(text);	
						}
					};
				});
				columnListOut.get(i).setSortable(false);
			}
			tableSignOut.getColumns().add(columnListOut.get(i));
		}

		columnListOut.get(0).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("date"));
		columnListOut.get(1).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("studentID"));
		columnListOut.get(2).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("name"));
		columnListOut.get(3).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				Integer>("grade"));
		columnListOut.get(4).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("reason"));
		columnListOut.get(5).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("excused"));
		columnListOut.get(6).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("time"));
		columnListOut.get(7).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				String>("arrTime"));
		//columnListOut.get(8).setCellValueFactory(new PropertyValueFactory<StudentProperty, 
				//String>("note"));

		tableSignOut.setItems(goingOutIn);
		return tableSignOut;
	}

	public void sendEmail(){
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Send Report");
		dialog.setHeaderText("Please enter emails, seperated by commas");
		ButtonType loginButtonType = new ButtonType("Send", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		TextArea emailTextArea = new TextArea();
		emailTextArea.setWrapText(true);
		ArrayList<String> emailList = EmailHandler.readEmailList();





		dialog.getDialogPane().setContent(emailTextArea);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(emailTextArea.getText(), "");
			}
			return null;
		});

		Node sendEmailButton = dialog.getDialogPane().lookupButton(loginButtonType);
		sendEmailButton.setDisable(true);
		if (emailList.size() != 0){
			String temp = "";
			for (int i = 0; i < emailList.size(); i++){
				if (emailList.size() != 1 && i != emailList.size()-1){
					temp = temp + emailList.get(i) + ", ";
				}
				else{
					temp = temp + emailList.get(i);
				}
			}
			emailTextArea.setText(temp);
			String[] d = temp.split(",");
			boolean valid = true;
			for (int i =0; i < d.length; i++){
				d[i] = d[i].trim();
				if (!EmailHandler.isValidEmailAddress(d[i])){
					valid = false;
				}
			}
			if (valid){
				sendEmailButton.setDisable(false);
			}
			else{
				sendEmailButton.setDisable(true);
			}
		}

		emailTextArea.textProperty().addListener((observable, oldValue, newValue) -> {

			String[] d = newValue.split(",");
			boolean valid = true;
			for (int i =0; i < d.length; i++){
				d[i] = d[i].trim();
				if (!EmailHandler.isValidEmailAddress(d[i])){
					valid = false;
				}
			}
			if (valid){
				sendEmailButton.setDisable(false);
				if (emailTextArea.getStyleClass().size() == 4){
					emailTextArea.getStyleClass().remove(3);

				}

			}
			else{
				sendEmailButton.setDisable(true);
				if (emailTextArea.getStyleClass().size() == 4){
					emailTextArea.getStyleClass().remove(3);
					emailTextArea.getStyleClass().add("passwordTextField-error");
				}
				else if  (emailTextArea.getStyleClass().size() == 3){
					emailTextArea.getStyleClass().add("passwordTextField-error");
				}
			}
		});
		dialog.getDialogPane().getStylesheets().add("css/application.css");
		ButtonBar buttonBar = (ButtonBar)dialog.getDialogPane().lookup(".button-bar");
		buttonBar.getButtons().get(0).getStyleClass().add("saveButton");
		buttonBar.getButtons().get(1).getStyleClass().add("closeButton");
		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(email -> {

			String[] d = email.getKey().split(",");
			ArrayList<String> destination = new ArrayList<String>();
			for (String i : d){
				destination.add(i.trim());
			}
			sendEmail = new EmailHandler("synergy.inc.smcs.2018@gmail.com", "synergy.inc", destination);
			sendEmail.send();

			EmailHandler.writeEmailList(destination);




		});
	}


}
