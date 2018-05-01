package backend;
import javafx.beans.property.*;

/**
 * Wrapper Class of student for implementation in a TableView.
 * @author Kevin
 */
@SuppressWarnings("restriction")
public class StudentProperty {
	private SimpleStringProperty name;
	private SimpleIntegerProperty grade;
	private SimpleStringProperty studentID;
	private SimpleStringProperty reason;
	//private SimpleStringProperty note;
	private SimpleStringProperty date;
	private SimpleStringProperty time;
	private SimpleStringProperty excused;
	private SimpleStringProperty arrTime;
	
	/**
	 * Constructs StudentProperty from a Student and copies all the data.
	 * @param st the Student to be copied
	 */
	public StudentProperty(Student st){
		name = new SimpleStringProperty(st.getName());
		grade = new SimpleIntegerProperty(st.getGrade());
		studentID = new SimpleStringProperty(st.getStudentID());
		reason = new SimpleStringProperty(st.getReason());
		//note = new SimpleStringProperty(st.getNote());
		date = new SimpleStringProperty(st.getDate());
		time = new SimpleStringProperty(st.getTime());
		excused = new SimpleStringProperty(st.getExcused());
		arrTime = new SimpleStringProperty(st.getArrTime());
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public Integer getGrade() {
		return grade.get();
	}

	public void setGrade(int grade) {
		this.grade.set(grade);
	}

	public String getStudentID() {
		return studentID.get();
	}

	public void setStudentID(String studentID) {
		this.studentID.set(studentID);
	}

	public String getReason() {
		return reason.get();
	}

	public void setReason(String reason) {
		this.reason.set(reason);
	}

	/*public String getNote() {
		return note.get();
	}

	public void setNote(String note) {
		this.note.set(note);
	}*/
	
	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}
	
	public String getExcused() {
		return excused.get();
	}

	public void setExcused(String excused) {
		this.excused.set(excused);
	}
	
	public String getArrTime() {
		return arrTime.get();
	}

	public void setArrTime(String arrTime) {
		this.arrTime.set(arrTime);
	}
	public String getTime() {
		return time.get();
	}

	public void setTime(String time) {
		this.time.set(time);
	}
	
	
}
