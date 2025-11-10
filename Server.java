import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
	
	public static void main(String[] args) throws Exception {
		ServerSocket listener = new ServerSocket(7777);
		System.out.println("The capitalization server is running...");
		ExecutorService pool = Executors.newFixedThreadPool (20);
		
		while (true) {
		Socket sock = listener.accept();
		pool.execute(new Capitalizer(sock));
		}
	}
	
	private static class Capitalizer implements Runnable {
		private Socket socket;
		
		Capitalizer(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			System.out.println("Connected: " + socket);
			try (
				Scanner in = new Scanner(socket.getInputStream());
				PrintWriter out = new PrintWriter(socket.getOutputStream (), true);		
			){
				out.println("Enter plus * * / minus * * / times * * / division * *");
				out.println("Enter 'break' to end");
				
				while (in.hasNextLine()) {
					
					String line = in.nextLine().trim();
					
					if(line.equalsIgnoreCase("break")) {
						Response resp = new Response("EXIT", 0, "End system.");
						out.println(resp.serialize());
						break;
					}
					
					String[] list = line.split("\\s+");
					
					if(list.length != 3) {
						Response resp = new Response("ERROR", 0, "Invalid form.");
						out.println(resp.serialize());
						continue;
					}
					
					String oper = list[0];
					double num1, num2;
					
					try {
						num1 = Double.parseDouble(list[1]);
						num2 = Double.parseDouble(list[2]);
					} catch(NumberFormatException e) {
						Response resp = new Response("ERROR", 0, "Numbers must be valid.");
						out.println(resp.serialize());
						continue;
					}
					
					double result;
					
					switch(oper.toLowerCase()) {
						case "plus":
							result = num1 + num2;
							break;
						case "minus": 
							result = num1 - num2;
							break;
						case "times":
							result = num1 * num2;
							break;
						case "division":
							if(num2 == 0) {
								Response resp = new Response("ERROR", 0, "Division by zero.");
								out.println(resp.serialize());
								continue;
							}
							result = num1 / num2;
							break;
						default: 
							Response resp = new Response("ERROR", 0, "Unknown Operation.");
							out.println(resp.serialize());
							continue;
					}
					Response resp = new Response("RESULT", result, "Success");
				}
			} catch (Exception e) {
				System.out.println("Error:" + socket);
			} finally {
				try { socket.close(); } catch (IOException e) {}
				System.out.println("Closed: " + socket);
			}
		}
	}
}
