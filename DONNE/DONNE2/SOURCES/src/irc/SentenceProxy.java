/***
 * Sentence class : used for keeping the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

import annotations.JvnRead;
import annotations.JvnWrite;

public class SentenceProxy implements java.io.Serializable, ISentence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String 	data;
  
	public SentenceProxy() {
		data = new String("");
	}
	
	@JvnWrite
	public void write(String text) {
		data = text;
	}
	
	@JvnRead
	public String read() {
		return data;	
	}
	
}