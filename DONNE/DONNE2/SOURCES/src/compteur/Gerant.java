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
			JvnObject jo = js.jvnLookupObject("Compteur");
			   
			if (jo == null) {
				jo = js.jvnCreateObject((Serializable) new Compteur());
				// after creation, I have a write lock on the object
				jo.jvnUnLock();
				js.jvnRegisterObject("Compteur", jo);
			}
			
			for(int i = 0 ; i < 10 ; i++) {
				jo.jvnLockWrite();
				int tmp = ((Compteur) jo.jvnGetSharedObject()).get();
				System.out.println(args[0] + " : " + tmp);
				Thread.sleep(r.nextInt(15));
				((Compteur) jo.jvnGetSharedObject()).change(tmp+1);
				jo.jvnUnLock();
			}
			
			
		   
		   } catch (Exception e) {
			   System.out.println("IRC problem : " + e.getMessage());
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
