package annotations;
import java.lang.annotation.*; 

//The annotation is available at execution time 
@Retention(RetentionPolicy.CLASS)  

//The annotation is associated with a type (Classe, interface)  
@Target(ElementType.METHOD) 


public @interface JvnRead {

}
