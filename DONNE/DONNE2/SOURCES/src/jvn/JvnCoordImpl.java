/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import irc.Irc;
import irc.Sentence;
import sun.rmi.registry.RegistryImpl;

import java.io.Serializable;


class Actors{
	public static boolean WRITER = true;
	public static boolean READER = false;
	
	private boolean type;
	public List<JvnRemoteServer> listActors;
	
	Actors(){
		listActors = new ArrayList<>();
	}
	
	public void add(JvnRemoteServer j) {
		listActors.add(j);
	}
	
	public void remove(JvnRemoteServer j) {
		listActors.remove(j);
	}
	
	public boolean getType() {
		return type;
	}
	
	public JvnRemoteServer getFirst() {
		return listActors.get(0);
	}

	public boolean isEmpty() {
		return listActors.isEmpty();
	}
	
	public void changeType(boolean t) {
		this.type = t;
	}
	
	
}

public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Serializable> idMap;
	private HashMap<String, Integer> nameMap;
	private HashMap<Integer, Actors> actorMap;
	
	private int globalId;
	
	public static void main(String argv[]) {
		try {
			new JvnCoordImpl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		this.idMap = new HashMap<>();
		this.nameMap = new HashMap<>();
		this.actorMap = new HashMap<>();
		this.globalId = 0;
		
		Registry r = LocateRegistry.createRegistry(1099);
		r.bind("coordinator", this);
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
	  synchronized(this) {
		  this.idMap.put(globalId, null);
		  
		  globalId += 1;
		  
		  return globalId - 1;
	  }
  }
  
  /**
   * Associate a symbolic name with a JVN object
   * @param jon : the JVN object name
   * @param jo  : the JVN object 
   * @param js  : the remote reference of the JVNServer
   * @throws java.rmi.RemoteException,JvnException
   **/
   public void jvnRegisterObject(JvnObject jo, JvnRemoteServer js)
   throws java.rmi.RemoteException,jvn.JvnException{
 	  
	   int tmpId = jo.jvnGetObjectId();
 	  
  	  this.idMap.put(tmpId, jo.jvnGetSharedObject());
  	  this.actorMap.put(tmpId, new Actors());
  	  actorMap.get(tmpId).changeType(Actors.WRITER);
  	  actorMap.get(tmpId).add(js);
   }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	  
	  int tmpId = jo.jvnGetObjectId();
	  
	  this.nameMap.put(jon, tmpId);
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	  if(nameMap.containsKey(jon)) {
		  int jvnId = nameMap.get(jon);
		  if(idMap.containsKey(jvnId)) {
			  JvnObject toReturn = new JvnObjectImpl(jvnId, idMap.get(jvnId), JvnObjectImpl.STATES.NL);
			  return toReturn;
		  }
		  else {
			  throw new jvn.JvnException();
		  }
	  }
	  return null;
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   if (actorMap.containsKey(joi) && idMap.containsKey(joi)) {
		   
		   Actors a = actorMap.get(joi);
		   if (a.getType() == Actors.READER) {
			   a.add(js);
		   } else {
			   //a.type == writer
			   JvnRemoteServer tmp = null;
			   while (!a.isEmpty()) {
				   tmp = a.getFirst();
				   idMap.put(joi,tmp.jvnInvalidateWriterForReader(joi));
				   //a.remove(tmp);
			   }
			   a.changeType(Actors.READER);
			   a.add(js);
		   }
		   return idMap.get(joi);
	   } else {		   
		   return null;
	   }
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   if (actorMap.containsKey(joi) && idMap.containsKey(joi)) {
		   
		   Actors a = actorMap.get(joi);
		   JvnRemoteServer tmp = null;
		   System.out.println(a.listActors.size());
		   System.out.println(a.getType());
		   while (!a.isEmpty()) {
			   tmp = a.getFirst();
			   if (tmp != js) {
				   if (a.getType() == Actors.READER) {
					   tmp.jvnInvalidateReader(joi);
				   } else {				   
					   idMap.put(joi,tmp.jvnInvalidateWriter(joi));
				   }
			   } else {
				   System.out.println("Au maximum je m'affiche une fois");
			   }
			   a.remove(tmp);
		   }
		   a.changeType(Actors.WRITER);
		   a.add(js);
		   return idMap.get(joi);
	   } else {		   
		   return null;
	   }
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
    	this.actorMap.forEach((key, value) -> value.remove(js));
    }
}

 
