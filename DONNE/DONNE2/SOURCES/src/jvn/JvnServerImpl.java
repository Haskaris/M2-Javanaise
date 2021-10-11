/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import org.apiguardian.api.API;

import java.io.*;



public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{ 
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	private HashMap<Integer, JvnObject> cache;
	
	private JvnRemoteCoord coordinator;

  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnServerImpl() throws Exception {
		super();

		Registry r = LocateRegistry.getRegistry(1099);
		coordinator = (JvnRemoteCoord) r.lookup("coordinator");
		
		cache = new HashMap<>();
	}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
		try {
			coordinator.jvnTerminate(this);
		} catch (RemoteException e) {
			throw new JvnException();
		}
	} 
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException {
		try {
			int id = coordinator.jvnGetObjectId();
			JvnObject jo = new JvnObjectImpl(id, o,JvnObjectImpl.STATES.W);
			coordinator.jvnRegisterObject(jo, js);
			cache.put(id, jo);
			return jo;
		} catch (RemoteException e) {
			throw new JvnException();
		}
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
		try {
			coordinator.jvnRegisterObject(jon, jo, this);
		} catch (RemoteException e) {
			throw new JvnException();
		}
	}
	
	/**
	* Provide the reference of a JVN object beeing given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
		try {
			JvnObject jo = coordinator.jvnLookupObject(jon, this);
			if (jo == null) {
				return null;
			} else {
				int id = jo.jvnGetObjectId();
				cache.put(id, jo);
				return jo;
			}
		} catch (RemoteException e) {
			throw new JvnException();
		}
	}	
	
	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockRead(int joi)
	 throws JvnException {
		try {
			Serializable toReturn = coordinator.jvnLockRead(joi, this);
			return toReturn;
		} catch (RemoteException e) {
			throw new JvnException();
		}

	}	
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
		try {
			Serializable toReturn = coordinator.jvnLockWrite(joi, this);
			return toReturn;
		} catch (RemoteException e) {
			throw new JvnException();
		}
	}	

	
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
		JvnObject jo = cache.get(joi);
		if(jo != null) {
			jo.jvnInvalidateReader();
		}
	};
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException { 
	  	JvnObject jo = cache.get(joi);
		if(jo != null) {
			jo.jvnInvalidateReader();
			return jo.jvnGetSharedObject();
		} else {
			throw new JvnException();
		}
	};
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException { 
	   JvnObject jo = cache.get(joi);
		if(jo != null) {
			jo.jvnInvalidateWriterForReader();
			return jo.jvnGetSharedObject();
		} else {
			throw new JvnException();
		}
	 };

}

 
