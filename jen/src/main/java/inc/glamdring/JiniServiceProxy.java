package inc.glamdring;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * A smart proxy which wraps the remote Jini service calls.
 */
public class JiniServiceProxy implements Serializable, IJiniService {
	
	private static final long serialVersionUID = 1L;

	private IRemoteJiniService remoteService = null;
	
	public JiniServiceProxy(Remote remote) {
		this.remoteService = (IRemoteJiniService) remote;
	}

}
