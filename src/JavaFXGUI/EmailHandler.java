package JavaFXGUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

import javax.mail.*;  
import javax.mail.internet.*;  
import javax.activation.*;  

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.util.MailConnectException;

public class EmailHandler {
	private String username;
	private String password;
	private String error = "none";
	ArrayList<String> destination = new ArrayList<String>();

	public EmailHandler( String u, String p, ArrayList<String> d){
		username = u;
		password = p;
		readEmailList();
		destination = d;

	}
	public ArrayList<String> getEmailList(){
		return destination;
	}
	
	public static ArrayList<String> readEmailList(){
		Scanner configScanner = null;
		File configFile = new File("src/data/email.config");
		ArrayList<String> d = new ArrayList<String>();
		try {
			configScanner = new Scanner(configFile);
			int num = (int)Integer.parseInt(configScanner.nextLine());
			for (int i = 0; i < num; i++){
				d.add(configScanner.nextLine());
			}

			configScanner.close();
			
		} catch (FileNotFoundException e1) {
			d = new ArrayList<String>();
		}
		return d;
	}
	
	public static void writeEmailList(ArrayList<String> d){

		File configFile = new File("src/data/email.config");
		try {
			PrintWriter printWriter = new PrintWriter (configFile);
			printWriter.println(d.size());

			for (int i = 0; i < d.size(); i++){
				printWriter.println(d.get(i));
			}
			printWriter.close();
			
		} catch (FileNotFoundException e1) {
			d = new ArrayList<String>();
		}

	}
	public static boolean isValidEmailAddress(String email) {
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
		}

	public void send(){
		LocalDate todayDate = LocalDate.now();
		String date = todayDate.toString();

		Properties properties = System.getProperties();  
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");  
		properties.put("mail.smtp.auth", "true");  
		properties.put("mail.smtp.ssl.enable", true);





		Session session = Session.getDefaultInstance(properties, 
				new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						username, password);
			}
		});
		Transport transport;


		MimeMessage generateMailMessage = new MimeMessage(session);

		try {
			transport = session.getTransport("smtp");

			transport.connect("smtp.gmail.com", username, password);
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(destination.get(0)));
			for (int i = 1; i < destination.size(); i++){
				generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(destination.get(i)));
			}

			generateMailMessage.setSubject(date + " Attendance Report");
			String emailBody = "Here is today's attendance report attached below:"
					+ "<br><br>"
					+ "Thanks,"
					+ "<br>"
					+ "Synergy Inc."; 

			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setContent(emailBody,	"text/html");
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();		

			String filename = "src/backup/" + date+"-IN.csv";
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName( date+"-IN.csv");
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			filename = "src/backup/" + date+"-OUT.csv";
			source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(date+"-OUT.csv");
			multipart.addBodyPart(messageBodyPart);
			generateMailMessage.setContent(multipart);
			Transport.send(generateMailMessage);

			error = "none";
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MailConnectException e) {
			// TODO Auto-generated catch block
			error = "This program cannot access the gmail server to send the email."
					+ "\nPlease check your firewall or internet connection.";
			
		}
		catch(SendFailedException e){
			error = "An email is not in email format.";
		}
		catch(MessagingException e){
			error = "Unkown Error";
			
		}

	}

	public String getError(){
		return error;
	}

}

