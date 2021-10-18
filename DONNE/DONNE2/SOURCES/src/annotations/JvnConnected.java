package annotations;
import java.lang.annotation.*; 

//The annotation is available at execution time 
@Retention(RetentionPolicy.RUNTIME)  

//The annotation is associated with a type (Classe, interface)  
@Target(ElementType.CONSTRUCTOR) 

public @interface JvnConnected {
	String name();
	
	
}
