package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import annotations.*;
import irc.ISentence;
import irc.Sentence;

public class JvnHandler implements InvocationHandler {
	private JvnObject jo;
	
	JvnHandler(Object o,String name) throws JvnException{
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		JvnObject jo = js.jvnLookupObject(name);
		   
		if (jo == null) {
			jo = js.jvnCreateObject((Serializable)o);
			jo.jvnUnLock();
			js.jvnRegisterObject(name, jo);
		}
		
		this.jo = jo;
		
	}
	
	public static Object newInstance(Object obj,String name) throws JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(
		obj.getClass().getClassLoader(),
		obj.getClass().getInterfaces(),
		new JvnHandler(obj,name));
		 } 
	
	public static Object newInstance(Serializable obj) throws JvnException {
		return newInstance(obj,"");
		 } 

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object toReturn = null;
		if(method.isAnnotationPresent(JvnRead.class)) {  
			jo.jvnLockRead();
			Serializable obj = (Serializable)(jo.jvnGetSharedObject());
			// proxy = proxy.getClass().cast(obj);
			toReturn = method.invoke(obj, args);
			jo.jvnUnLock();
		} else if(method.isAnnotationPresent(JvnWrite.class)) {  
			jo.jvnLockWrite();
			Serializable obj = (Serializable)(jo.jvnGetSharedObject());
			// proxy = proxy.getClass().cast(obj);
			toReturn = method.invoke(obj, args);
			jo.jvnUnLock();
		} else { 
			System.out.println("Elle n'est pas en ligne");
			throw new JvnException("aucune annotation sur la m�thode appel�e");
		}
		return toReturn;
	}

}
