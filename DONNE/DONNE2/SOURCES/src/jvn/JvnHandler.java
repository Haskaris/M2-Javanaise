package jvn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import annotations.*;

public class JvnHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(proxy.getClass().isAnnotationPresent(JvnRead.class)) {  
			
		} else if(proxy.getClass().isAnnotationPresent(JvnWrite.class)) {  
			
		} else { 
			System.out.println("Elle n'est pas en ligne"); 
		}
		return null;
	}

}
