/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package klient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Message;


public class Klient {
	static boolean close_the_socket=false;
	static BufferedReader input;
	static PrintWriter out;
	static public String operacja;
	static public String status;
	static public String id;
	static public String wiadomosc;
	
	//metoda ustawiająca wewnętrzne pola tekstowe
	public static void messageToStrings(String a){
		StringBuilder str1 = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		StringBuilder str3 = new StringBuilder();
		StringBuilder str4 = new StringBuilder();

		//"Operacja-)"+operacja+"(|Status-)"+status+"(|"+"Identyfikator-)"+identyfikator+"(|"+"Wiadomosc-)"+wiadomosc+"(|+ft.format(dNow)
		int i=10;
		while(a.charAt(i)!='('){
			str1.append(a.charAt(i));
			i++;
		}
		// System.out.print(str1.toString());
		
		i=i+10;
		while(a.charAt(i)!='('){
			str2.append(a.charAt(i));
			i++;
		}
		// System.out.print(str2.toString());
		
		i+=17;
		while(a.charAt(i)!='('){
			str3.append(a.charAt(i));
			i++;
		}
		// System.out.print(str3.toString());
		
		i+=13;
		while(a.charAt(i)!='('){
			str4.append(a.charAt(i));
			i++;
		}
		// System.out.print(str4.toString());
		operacja=str1.toString();
		status=str2.toString();
		id=str3.toString();
		wiadomosc=str4.toString();
	}

	//wątek odbierający wiadomości
	public static class GetMessageThread extends Klient implements Runnable  {
		
        public GetMessageThread(BufferedReader input,PrintWriter out){
            this.input=input;
            this.out=out;
        }

        @Override
        public void run() {
			while(true){
				try {
					//oczekiwanie na wiadomość
					char[] bufferread = new char[255];
					while(true){if(input.read(bufferread,0,255)>0)break;}
					
					//odczyt wiadomości
					String s=new String(bufferread);
					messageToStrings(s);
					
					//odpowiednie akcje w zależności od treści wiadomości
					
					//otrzymanie zaproszenia
					if(operacja.equals("chatinv")&&status.equals("inviting")){
						System.out.print("Dostales zaproszenie jezeli chcesz zaakceptowac wcisnij 1(i enter) jezeli nie wcisnij inny klawisz (i enter)");
						
						//odczyt napisanego znaku
						Scanner odczyt = new Scanner(System.in);
						String client_decision = odczyt.nextLine();
						
						//wysłanie komunikatu akceptującego lub odrzucającego zaproszenie
						if(client_decision.equals("1")){
							out.print(new Message("chatinv","accept",id).getMessage());
							out.flush();
						}
						else{
							out.print(new Message("chatinv","refuse",id).getMessage());
							out.flush();
						};
					}
				   
					//akceptacja zaproszenia
					if(operacja.equals("chatinv")&&status.equals("accept")){
						System.out.println("Zaproszenie zaakceptowane");
						System.out.println("Wpisz wiadomosc od wyslania a nastepnie wcisnij enter:");
						
						//odczyt wiadomości
						Scanner odczyt = new Scanner(System.in);
						String client_message = odczyt.nextLine();
						
						//przesłanie komunikatu z wiadomością
						out.print(new Message("wiadomosc","sending",id,client_message).getMessage());
						out.flush();
						
						//zamknięcie socketa oraz wątku
						close_the_socket =true;
						break;
					}
					
					//otrzymanie wiadomości
					if(operacja.equals("wiadomosc")&&status.equals("receiving")){
						System.out.println("Otrzymano wiadomosc:");
						System.out.print(wiadomosc);
						
						//zamknięcie socketa oraz wątku
						close_the_socket=true;
						break;
					}
				} 
				catch (IOException ex) {
					Logger.getLogger(Klient.class.getName()).log(Level.SEVERE, null, ex);
				} 
			}
        }
    }

    public static void main(String[] args) throws IOException {
		//utworzenie instancji klas wymaganych do komunikacji
		Socket socket = new Socket("localhost", 9090);
		out = new PrintWriter(socket.getOutputStream(), true); 
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//wysłanie wiadomości żądania ID
		out.print(new Message("idrequest","requesting","00").getMessage());
		out.flush();
      
		//oczekiwanie na wiadomość
		char[] bufferread = new char[255];
        while(true){if(input.read(bufferread,0,255)>0)break;}
		
		//odczytanie wiadomości
        String s=new String(bufferread);
		messageToStrings(s);
		
		//pętla while przydaje się tylko przy próbie połączenia z klientem offline
		while(true){
			System.out.println("Jezeli chcesz wyslac zaproszenie do rozmowy wcisnij 1 i nacisnij enter");
			System.out.println("Jezeli chcesz czekac na zaproszenie wcisnij dowolny inny klawiasz i nacisnij enter");
			
			//odczytanie odpowiedzi
			Scanner odczyt = new Scanner(System.in);
			String client_decision = odczyt.nextLine();
        
			if(client_decision.equals("1")){   
				//wysłanie wiadomości do serwera
				out.print(new Message("chatinv","invability",id).getMessage());
				out.flush();
				
				//oczekiwanie na odpowiedź
				char[] bufferread2 = new char[255];
                while(true){if(input.read(bufferread2,0,255)>0)break;}
				
				//odczytanie wiadomości
				String s1=new String(bufferread2);
                messageToStrings(s1);
				
				//oczekwianie na przyjęcie zaproszenia (wyjście z pętli while), bądź informacja o niedotępności drugiego klienta (powtórka pętli while)
				if(operacja.equals("chatinv")&&status.equals("reachable")){
					System.out.println("Czekam na przyjecie zaproszenia"); 
					break;
				}
				else{System.out.println("Klient, do ktorego chcesz napisac nie jest online");}                    
			}
			else{
				//przesłanie wiadomości o oczekiwaniu
				out.print(new Message("chatinv","waiting",id).getMessage());
				out.flush();
				
				System.out.println("Czekaj na zaproszenie");
				break;
			}
		}
		
		//uruchomienie wątku nasłuchującego
		GetMessageThread getter=new GetMessageThread(input,out);
		Thread get = new Thread(getter);
		get.start();
     
        while(true){
			//oczekiwanie na zakonczenie programu
			if(get.isAlive()==false){
				break;
			}
		}
		
		socket.close();
    }
}
