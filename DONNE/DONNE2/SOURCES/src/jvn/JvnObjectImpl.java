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
		synchronized(this) {
			System.out.println("Lock read");
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
			System.out.println(state);
		}
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		synchronized(this) {
			System.out.println("Lock write");
			if(state != STATES.WC) {
				obj = JvnServerImpl.jvnGetServer().jvnLockWrite(jvnGetObjectId());
			}
			state = STATES.W;
			System.out.println(state);
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		synchronized(this) {
			System.out.println("Unlock : " + state);
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
			if (state != STATES.RC && state != STATES.R) {
				throw new JvnException("Bad invalidate lock reader");
			}
			try {
				while(state == STATES.R) {
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
			if (state != STATES.RWC && state != STATES.WC && state != STATES.W) {
				throw new JvnException("Bad invalidate lock writer");
			}
			
			try {
				while(state == STATES.RWC || state == STATES.W) {
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
			if (state != STATES.RWC && state != STATES.WC && state != STATES.W) {
				throw new JvnException("Bad invalidate lock writer for reader");
			}
			
			try {
				while(state == STATES.W) {
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
