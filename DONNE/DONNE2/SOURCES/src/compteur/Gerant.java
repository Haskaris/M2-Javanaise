package compteur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import irc.Irc;
import irc.Sentence;
import jvn.JvnObject;
import jvn.JvnServerImpl;

public class Gerant implements Runnable{
	
	String name;

	public static void main(String[] args) {
		Random r = new Random();
		try {
			Thread.sleep(r.nextInt(100));
			
			// initialize JVN
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			
			// look up the IRC object in the JVN server
			// if not found, create it, and register it in the JVN server
			JvnObject jo = js.jvnLookOrCreate("Compteur", new Compteur());
			
			jo.setName(args[0]);
			
			
			for(int i = 0 ; i < 10 ; i++) {
				jo.jvnLockWrite();
				int tmp = ((Compteur) jo.jvnGetSharedObject()).get();
				Thread.sleep(r.nextInt(150));
				System.out.println(args[0] + " : " + tmp + ", i :" +  i);
				((Compteur) jo.jvnGetSharedObject()).change(tmp+1);
				jo.jvnUnLock();
				Thread.sleep(1);
			}
			jo.jvnLockRead();
			System.out.println(args[0] + " " + ((Compteur) jo.jvnGetSharedObject()).get());
			jo.jvnUnLock();
			
			
		   
		   } catch (Exception e) {
			   System.out.println("IRC problem : [" + args[0] + "] " + e.getMessage());
			   e.printStackTrace();
		   }

	}
	
	

	@Override
	public void run() {
		String l[] = new String[1];
		l[0] = name;
		main(l);
		
	}

}
