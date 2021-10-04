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
	
	
	private JvnLocalServer serv;
	private Serializable obj;
	private int id;
	private STATES state;
	
	
	public JvnObjectImpl(int id, JvnLocalServer s, Serializable o) {
		this.serv = s;
		this.obj = o;
		this.id = id;
		this.state = STATES.NL;
	}
	

	@Override
	public void jvnLockRead() throws JvnException {
		synchronized(state) {
			switch(state) {
				case RC:
					state = STATES.R;
					break;
				case WC:
					state = STATES.RWC;
					break;
				default:
					obj = serv.jvnLockRead(jvnGetObjectId());
					state = STATES.R;
			}
		}
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		synchronized(state) {
			if(state != STATES.WC) {
				obj = serv.jvnLockWrite(jvnGetObjectId());
			}
			state = STATES.W;
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
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
				throw new JvnException();
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
		synchronized(state) {
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
		synchronized(state) {
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
		synchronized(state) {
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
