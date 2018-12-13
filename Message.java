/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
   String message;

    public String getMessage() {
        return message;
    }

    public Message(String operacja,String status,String identyfikator){
		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
		String wiadomosc="";
		
        this.message= ("Operacja-)"+operacja+"(|Status-)"+status+"(|"+"Identyfikator-)"+identyfikator+"(|"+"Data-)"+ft.format(dNow)+"(|");  
    }
	
    public Message(String operacja,String status,String identyfikator,String wiadomosc){
        Date dNow = new Date( );
		SimpleDateFormat ft = 
		new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        this.message= ("Operacja-)"+operacja+"(|Status-)"+status+"(|"+"Identyfikator-)"+identyfikator+"(|"+"Wiadomosc-)"+wiadomosc+"(|"+"Data-)"+ft.format(dNow)+"(|");
    }
}
