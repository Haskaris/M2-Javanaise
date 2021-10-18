package compteur;

import java.io.Serializable;

import jvn.JvnObject;
import jvn.JvnServerImpl;

public class Manager {

	public static void main(String[] args) {
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		
		// look up the IRC object in the JVN server
		// if not found, create it, and register it in the JVN server
		try {
		JvnObject jo = js.jvnLookupObject("Compteur");
		   
		if (jo == null) {
			jo = js.jvnCreateObject((Serializable) new Compteur());
			// after creation, I have a write lock on the object
			jo.jvnUnLock();
			js.jvnRegisterObject("Compteur", jo);
		}
		
		
		Thread t[] = new Thread[10];
		for(int i = 0 ; i < 10 ; i++) {
			Gerant myclass=new Gerant ();
			myclass.name = "Gerant " + i;
			Thread th=new Thread((Runnable) myclass);
			t[i] = th;
			th.start();
		}
		for(int i = 0 ; i < 10 ; i++) {
			while(t[i].isAlive());
		}
		jo.jvnLockRead();
		System.out.println("attendu : 100 , obtenu : " + ((Compteur)(jo.jvnGetSharedObject())).get());
		jo.jvnUnLock();
		}
		catch(Exception e) {
			e.printStackTrace();			
		}
		
	}

}
