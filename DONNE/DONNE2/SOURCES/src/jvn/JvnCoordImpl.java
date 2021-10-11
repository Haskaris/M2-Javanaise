/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;


class Actor{
	public static boolean WRITER = true;
	public static boolean READER = false;
	
	boolean type;
	List<JvnRemoteServer> listActors;
	
	Actor(){
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
	
	public boolean changeType(boolean t) {
		if(listActors.isEmpty()) {
			type = t;
			return true;
		}
		else {
			return false;
		}
	}
	
	
}

public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Serializable> idMap;
	private HashMap<String, Integer> nameMap;
	
	private int globalId;
	
/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		this.idMap = new HashMap<>();
		this.nameMap = new HashMap<>();
		this.globalId = 0;
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {

	  this.idMap.put(globalId, null);
	  
	  globalId += 1;
	  
	  return globalId - 1;
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
	  
	  jvnRegisterObject(jo,js);
	  int tmpId = jo.jvnGetObjectId();
	  
	  this.nameMap.put(jon, tmpId);
  }
  
  /**
   * register an object without a name
   * @param jo  : the JVN object 
   * @param js  : the remote reference of the JVNServer
   * @throws java.rmi.RemoteException,JvnException
   **/
   public void jvnRegisterObject(JvnObject jo, JvnRemoteServer js)
   throws java.rmi.RemoteException,jvn.JvnException{

 	  int tmpId = jo.jvnGetObjectId();
 	  this.idMap.put(tmpId, jo);
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
		  if(idMap.containsKey(nameMap.get(jon))) {
			  return (JvnObject) idMap.get(nameMap.get(jon));
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
    // to be completed
    return null;
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
    // to be completed
    return null;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
	 // to be completed
    }
}

 
