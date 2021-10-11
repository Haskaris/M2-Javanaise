package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jvn.JvnObjectImpl;
import jvn.JvnServerImpl;

class JvnObjectImplTest {

	@Test
	void test_states() {
		Boolean b = true;
		JvnObjectImpl jvO = new JvnObjectImpl(2,JvnServerImpl.jvnGetServer(),b);
		
		
	}

}
