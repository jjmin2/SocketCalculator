import java.io.*;
import java.net.*;
import java.util.*;


public class Client {

	public static void main(String[] args) throws Exception {
		
		String serverIP = "localhost";
		int serverPort = 7777;
		
		File file = new File("server_info.dat");
		if(file.exists()) {
			try(Scanner fileSc = new Scanner(file)){
				if(fileSc.hasNext()) {
					serverIP = fileSc.next();
				}
				if(fileSc.hasNextInt()) {
					serverPort = fileSc.nextInt();
				}
				System.out.println("Connected - " + serverIP + " : " + serverPort);
			} catch(Exception e) {
				System.out.println("Error: File Reading Problem.");
			}
		}
		
		try(var socket = new Socket(serverIP, serverPort);){
			
			System.out.println("Enter lines of text");
			
			var sc = new Scanner(System.in);
			var in = new Scanner(socket.getInputStream());
			var out = new PrintWriter(socket.getOutputStream(), true);
			
			for (int i = 0; i < 2; i++) { 
				if (in.hasNextLine()) { 
					System.out.println(in.nextLine()); 
				} 
			}
			
			while (sc.hasNextLine()) {
				out.println(sc.nextLine());
				
				if (!in.hasNextLine()) break; 
				String responseStr =in.nextLine();
				
				Response resp = Response.deserialize(responseStr);
				
				switch (resp.code) {
                case "RESULT":
                    System.out.println("Answer: " + resp.value);
                    break;
                case "ERROR":
                    System.out.println("Error: " + resp.message);
                    break;
                case "EXIT":
                    System.out.println(resp.message);
                    return;
				}
				
			}
		} catch(Exception e) {
			System.out.println("Failed to connect to server");
		}
	}
}
