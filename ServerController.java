/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Random;


public class ServerController {
	boolean client1=false;
	boolean client2=false;
	String id1;
	String id2;

	public String getId1() {
		return id1;
	}

	public String getId2() {
		return id2;
	}

	private void SetIds(){
		Random rand = new Random();
		int randInt = rand.nextInt( 16 );
		if(randInt==0){randInt+=1;}
		
		rand = new Random();
		int randInt2 =rand.nextInt(16);
		if(randInt2==0){randInt2+=1;}
		
		String value=String.valueOf(randInt);
		String value2=String.valueOf(randInt2);
		id1=value;
		id2=value2;
		
		if(id1.equals(id2)){
			randInt2+=1;
			value2=String.valueOf(randInt2);
		}
	}

	ServerController(){
		SetIds();
	};
}
