package compteur;

public class Compteur implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer compteur;
	
	Compteur(){
		compteur = 0;
	}
	
	public void change(int c) {
		compteur = c;
	}
	
	public int get() {
		return compteur;
	}

}
