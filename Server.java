/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
	static ServerController control;
	static ServerSocket listener;  
	static Socket socket1;  
	static Socket socket2 = new Socket();
	static BufferedReader input1;
	static BufferedReader input2;
	static PrintWriter out1;
	static PrintWriter out2;
	static String operacja1;
	static String status1;
	static String id1;
	static String wiadomosc1;
	static String operacja;
	static String status;
	static String id;
	static String wiadomosc;
	static String operacja2;
	static String status2;
	static String id2;
	static String wiadomosc2;
	
	//translacja wiadomości na wewnętrzne pola
	public static void messageToStrings(String a){
		StringBuilder str1 = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		StringBuilder str3 = new StringBuilder();
		StringBuilder str4 = new StringBuilder();

		int i=10;
		while(a.charAt(i)!='('){
			str1.append(a.charAt(i));
			i++;
		}
		//System.out.print(str1.toString());
		
		i=i+10;
		while(a.charAt(i)!='('){
			str2.append(a.charAt(i));
			i++;
		}
		//System.out.print(str2.toString());
		
		i+=17;
		while(a.charAt(i)!='('){
			str3.append(a.charAt(i));
			i++;
		}//System.out.print(str3.toString());
		
		i+=13;
		while(a.charAt(i)!='('){
			str4.append(a.charAt(i));
			i++;
		}
	
		operacja=str1.toString();
		status=str2.toString();
		id=str3.toString();
		wiadomosc=str4.toString();

	}

	//wątek obsługujący odbiór wiadomości od klienta 1
	public static class GetMessageThread1 extends Server implements Runnable  {
		public  static void messageToStrings(String a){
			StringBuilder str1 = new StringBuilder();
			StringBuilder str2 = new StringBuilder();
			StringBuilder str3 = new StringBuilder();
			StringBuilder str4 = new StringBuilder();

			int i=10;
			while(a.charAt(i)!='('){
				str1.append(a.charAt(i));
				i++;   
			}
			//System.out.print(str1.toString());
			
			i=i+10;
			while(a.charAt(i)!='('){
				str2.append(a.charAt(i));
				i++;
			}
			//System.out.print(str2.toString());
			
			i+=17;
			while(a.charAt(i)!='('){
				str3.append(a.charAt(i));
				i++;
			}
			//System.out.print(str3.toString());
		
			i+=13;
			while(a.charAt(i)!='('){
				str4.append(a.charAt(i));
				i++;
			}
			
			operacja1=str1.toString();
			status1=str2.toString();
			id1=str3.toString();
			wiadomosc1=str4.toString();
		}
           
		public GetMessageThread1(BufferedReader input1,PrintWriter out1){
			this.input1=input1;
			this.out1=out1;
		}
		
		@Override
		synchronized public void run() {
			try {
				while(true){
					//oczekiwanie na wiadomość
					char[] bufferread = new char[255];
					while(true){if(input1.read(bufferread,0,255)>0)break;}
					
					//odczyt wiadomości
					String s1=new String(bufferread);
					messageToStrings(s1);

					//działanie zależne od odebranej wiadomości
					
					//wysłanie wiadmości, że klient 2 odbiera wiadomość
					if(operacja1.equals("wiadomosc")&&status1.equals("sending")){
						out2.print(new Message("wiadomosc","receiving",control.getId2(),wiadomosc1).getMessage());
						out2.flush();
						break;
					}
					
					//akceptacja zaproszenia, przesłanie klientowi 2
					if(operacja1.equals("chatinv")&&status1.equals("accept")){
						System.out.print("zaraz bedzie czat!");//wyslij drugiemu start czatu
						out2.print(new Message("chatinv","accept",control.getId2()).getMessage());
						out2.flush();
						break;
					}
					
					//odrzucenie zaproszenia, przesłanie klientowi 2
					if(operacja1.equals("chatinv")&&status1.equals("refuse")){
						out2.print(new Message("chatinv","refuse",control.getId2()).getMessage());
						out2.flush();
					}
					
					//oczekiwanie na wiadomość, ustawienie flagi oczeliwania
					if(operacja1.equals("chatinv")&&status1.equals("waiting")){
						control.client1=true;
					}					
					
					//obsługa zaproszenia i dostępności klienta 2
					if(operacja1.equals("chatinv")&&status1.equals("invability")){
						if(socket2.isConnected()&&(control.client2)==true){   
							out1.print(new Message("chatinv","reachable",control.getId1()).getMessage());
							out1.flush();
							out2.print(new Message("chatinv","inviting",control.getId2()).getMessage());
							out2.flush();
						}
						else{
							out1.print(new Message("chatinv","unreachable",control.getId1()).getMessage());
							out1.flush();
						}
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public static class GetMessageThread2 extends Server implements Runnable  {
		public  static void messageToStrings(String a){
			StringBuilder str1 = new StringBuilder();
			StringBuilder str2 = new StringBuilder();
			StringBuilder str3 = new StringBuilder();
			StringBuilder str4 = new StringBuilder();
			
			int i=10;
			while(a.charAt(i)!='('){
				str1.append(a.charAt(i));
				i++;
			}
			//System.out.print(str1.toString());
			
			i=i+10;
			while(a.charAt(i)!='('){
				str2.append(a.charAt(i));
				i++;
			}
			//System.out.print(str2.toString());
			
			i+=17;
			while(a.charAt(i)!='('){
				str3.append(a.charAt(i));
				i++;
			}
			//System.out.print(str3.toString());
			
			i+=13;
			while(a.charAt(i)!='('){
				str4.append(a.charAt(i));
				i++;
			}
			operacja2=str1.toString();
			status2=str2.toString();
			id2=str3.toString();
			wiadomosc2=str4.toString();
		}
        
		public GetMessageThread2(BufferedReader input2,PrintWriter out2){
			this.input2=input2;
			this.out2=out2;
		}
    

		@Override
		synchronized public void run() {
			String s1="";
			try {
				while(true){
					//oczekiwanie na wiadomość
					char[] bufferread = new char[255];
					while(true){if(input2.read(bufferread,0,255)>0)break;}
					
					//odczyt wiadomości
					s1=new String(bufferread);
					messageToStrings(s1);

					//działanie zależne od odebranej wiadomości
					
					//wysłanie wiadmości, że klient 2 odbiera wiadomość
					if(operacja2.equals("wiadomosc")&&status2.equals("sending")){
						out1.print(new Message("wiadomosc","receiving",control.getId1(),wiadomosc2).getMessage());
						out1.flush();
						socket1.close();
						socket2.close();
						break;
					}
					
					//akceptacja zaproszenia, przesłanie klientowi 2
					if(operacja2.equals("chatinv")&&status2.equals("accept")){
						System.out.print("zaraz bedzie czat !");//wyslij drugiemu start czatu
						out1.print(new Message("chatinv","accept",control.getId1()).getMessage());
						out1.flush();
						break;
					}
					
					//odrzucenie zaproszenia, przesłanie klientowi 1
					if(operacja2.equals("chatinv")&&status2.equals("refuse")){
						out1.print(new Message("chatinv","refuse",control.getId1()).getMessage());
						out1.flush();
					}
					
					//oczekiwanie na wiadomość, ustawienie flagi oczeliwania
					if(operacja2.equals("chatinv")&&status2.equals("waiting")){
						control.client2=true;
					}
					
					//obsługa zaproszenia i dostępności klienta 1
					if(operacja2.equals("chatinv")&&status2.equals("invability")){
						if(socket1.isConnected()&&(control.client1)==true){   
							out2.print(new Message("chatinv","reachable",control.getId2()).getMessage());
							out2.flush();
							out1.print(new Message("chatinv","inviting",control.getId1()).getMessage());
							out1.flush();
						}
						else{
							out2.print(new Message("chatinv","unreachable",control.getId2()).getMessage());
							out2.flush();    
						}
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

    public static void main(String[] args) throws IOException { 
		//utworzenie instancji klas wymaganych do komunikacji
		control=new ServerController();
		listener = new ServerSocket(9090);
		socket1 = listener.accept();  
		input1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
		
		//oczekiwanie na wiadomość od klienta
		char[] bufferread = new char[255];
		while(true){if(input1.read(bufferread,0,255)>0)break;}
		
		//translacja wiadomości
		String s1=new String(bufferread);
		messageToStrings(s1);
		
		//wysłanie ID
		out1 = new PrintWriter(socket1.getOutputStream(), true); 
		out1.print(new Message("idrequest","responding",control.getId1()).getMessage());
		out1.flush();
		
		//uruchomienie wątku obsługującego klienta 1
		GetMessageThread1 getter=new GetMessageThread1(input1,out1);
		Thread get = new Thread(getter);
		get.start();
			
		socket2=listener.accept();
		input2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
		
		//oczekiwanie na wiadomość od klienta 2
		char[] bufferread2 = new char[255];
		while(true){if(input2.read(bufferread2,0,255)>0);break;}
		
		//translacja wiadomości
		String s2=new String(bufferread2);
		messageToStrings(s2);
		
		//wysłanie ID
		out2 = new PrintWriter(socket2.getOutputStream(), true); 
		out2.print(new Message("idreqest","responding",control.getId2()).getMessage());
		out2.flush();
		
		//uruchomienie wątku obsługującego klienta 2
		GetMessageThread2 getter2=new GetMessageThread2(input2,out2);
		Thread get2 = new Thread(getter2);
		get2.start();
    }  
}
