package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
	
	private static final long serialVersionUID = 1L;

	public enum STATES {
		NL,
		R,
		W,
		RC,
		WC,
		RWC
	}
	
	
	private Serializable obj;
	private int id;
	private STATES state;
	
	
	public JvnObjectImpl(int id, Serializable o, STATES s) {
		this.obj = o;
		this.id = id;
		this.state = s;
	}
	

	@Override
	public void jvnLockRead() throws JvnException {
		System.out.println("je veux lock read " + state);
		synchronized(this) {
			switch(state) {
				case RC:
					state = STATES.R;
					break;
				case WC:
					state = STATES.RWC;
					break;
				default:
					obj = JvnServerImpl.jvnGetServer().jvnLockRead(jvnGetObjectId());
					state = STATES.R;
			}
		}
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		System.out.println("je veux lock write " + state);
		synchronized(this) {
			if(state != STATES.WC) {
				obj = JvnServerImpl.jvnGetServer().jvnLockWrite(jvnGetObjectId());
			}
			state = STATES.W;
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		synchronized(this) {
			switch(state) {
				case R:
					state = STATES.RC;
					notify();
					break;
				case W:
				case RWC:
					state = STATES.WC;
					notify();
					break;
				default:
					break;
			}
		}
	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		return id;
	}

	// Précondition :
	// On a fait un lock avant
	@Override
	public Serializable jvnGetSharedObject() throws JvnException {
		return obj;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		synchronized(this) {
			System.out.println("on recupere R");
			if (state != STATES.RC && state != STATES.R) {
				throw new JvnException("Bad invalidate lock reader " + state);
			}
			try {
				while(state == STATES.R) {
					System.out.println("je me met en attente pour enlever R");
					wait();
				}
				state = STATES.NL;
			} catch(Exception e) {
				System.out.println(e);
				throw new JvnException();
			}
		}
	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		synchronized(this) {
			System.out.println("on recupere W");
			if (state != STATES.RWC && state != STATES.WC && state != STATES.W) {
				throw new JvnException("Bad invalidate lock writer " + state);
			}
			
			try {
				while(state == STATES.RWC || state == STATES.W) {
					System.out.println("je me met en attente pour enlever W et RWC");
					wait();
				}
				state = STATES.NL;
			} catch(Exception e) {
				System.out.println(e);
				throw new JvnException();
			}
			
			return obj;
		}
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		synchronized(this) {
			System.out.println("on recupere W en laissant R");
			if (state != STATES.RWC && state != STATES.WC && state != STATES.W) {
				throw new JvnException("Bad invalidate lock writer for reader " + state);
			}
			
			try {
				while(state == STATES.W) {
					System.out.println("je me met en attente pour enlever W");
					wait();
				}
			} catch(Exception e) {
				System.out.println(e);
				throw new JvnException();
			}
			
			if (state == STATES.WC) {
				state = STATES.RC;
			} else if (state == STATES.RWC) {			
				state = STATES.R;
			}
			
			return obj;
		}
	}
}
