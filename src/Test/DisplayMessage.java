package Test;
import java.util.Scanner;
public class DisplayMessage {
	private static Scanner scanner;
	public static void main(String args[]){
		String message = new String("Hello|Wassup|How are you");
		String sender = "Madhur|Vignesh|Harshini";
		String[] msg_split = message.split("\\|");
		String[] sender_split = sender.split("\\|");
		//System.out.println("String is "+ msg_split[2]);
		//int i = 0;
		for(int i = 0; i < msg_split.length;i++){
			//System.out.println("Message " + msg_split[i]);
			//System.out.println("From "+sender_split[i]);
		}
		String r_id = null;
		scanner = new Scanner(System.in);
		System.out.println("Enter the number of recipients ");
		int count = scanner.nextInt();
		scanner.nextLine();
		for(int i = 0; i < count; i++){
			System.out.println("Enter the recipient user_id");
			String recipient_id = scanner.nextLine();
			if(r_id == null) r_id = recipient_id;
			else{
				r_id += "|" + recipient_id;
			}
		}
		System.out.println("The R_list is " + r_id);
			
	}
}
