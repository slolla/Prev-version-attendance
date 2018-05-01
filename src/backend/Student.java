package backend;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/** 
 * Data class that holds information on each student. Holds name, studentID and grade.
 * @author	Kevin
 */
public class Student implements Comparable<Student>{
	
	private String name;
	private int grade;
	private String studentID;
	private String reason;
	//private String note;
	private String date;
	private String time;
	private String excused = "";
	private String arrTime = "None";
	

	/**
	 * Constructor. Initializes Date and Time at moment of creation
	 * 
	 * @param n		Name of student.
	 * @param g		Grade of student.
	 * @param sID	ID of student.
	 */
	
	public Student(String n, int g, String sID){
		name = n;
		grade = (Integer) g;
		studentID = sID;
		LocalDate todayDate = LocalDate.now();
		date = todayDate.toString();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
		LocalTime todayTime = LocalTime.now();
		time = formatter.format(todayTime);
		
	}
	


	/**
	 * Copy Constructor. Applies curent date and time, NOT date and time of different student.
	 * 
	 * @param other	Student to be copied.
	 */
	public Student(Student other) {
		name = other.name;
		grade = other.grade;
		studentID = other.studentID;
		LocalDate todayDate = LocalDate.now();
		date = todayDate.toString();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
		LocalTime todayTime = LocalTime.now();
		time = formatter.format(todayTime);

	}

	

	public String getName() {
		return name;
	}

	public int getGrade() {
		return grade;
	}

	public String getStudentID() {
		return studentID;
	}


	public String toString(){
		return  "  " + String.format("%-37s", name) +grade;
	}



	public String getReason() {
		return reason;
	}



	public void setReason(String reason) {
		this.reason = reason;
	}



	/*public String getNote() {
		return note;
	}



	public void setNote(String note) {
		this.note = note;
	}
	*/
	public String getTime(){
		return time;
	}
	
	public void setTime(String t){
		time = t;
	}
	
	public String getDate(){
		return date;
	}
	public void setDate(String d){
		date = d;
	}
	public String getExcused() {
		return excused;
	}
	public void setExcused(String excused) {
		this.excused = excused;
	}
	public void setArrTime(String arrTime) {
		this.arrTime = arrTime;
	}
	public String getArrTime() {
		return arrTime;
	}

	public int compareTo(Student o) {
		return toString().compareTo(o.toString());
	}
	
	public boolean equals(Student other){
		return this.toString().equals(other.toString());
	}

}
