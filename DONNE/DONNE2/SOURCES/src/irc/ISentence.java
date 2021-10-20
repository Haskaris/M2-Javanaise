package irc;

import annotations.JvnRead;
import annotations.JvnWrite;

public interface ISentence {
	@JvnWrite
	public void write(String text);
	
	@JvnRead
	public String read();

}
