package inc.glamdring;

import net.jini.config.*;
import net.jini.core.lease.*;
import net.jini.core.lookup.*;
import net.jini.discovery.*;
import net.jini.export.*;
import net.jini.lease.*;

import java.io.*;
import java.rmi.*;

/**
 * NOTE: Originally taken from code examples in Jan Newmarch's tutorial at 
 * <a href="http://jan.netcomp.monash.edu.au/java/jini/tutorial/SimpleExample.xml">here</a>
 * My Jini Service!
 *
 * @author <a href="csterwa@jini.org">Chris Sterling</a>
 * @author Jan Newmarch
 */
public class JiniServiceServer implements DiscoveryListener, LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    protected ServiceID serviceID = null;
    protected JiniServiceProxy smartProxy = null;
    protected Remote rmiProxy = null;
    protected JiniServiceImpl impl = null;

    public static void main(String argv[]) {
    	try {
    		new JiniServiceServer(ConfigurationProvider.getInstance(argv));
    	} catch (ConfigurationException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
		
        // keep server running forever to 
		// - allow time for locator discovery and
		// - keep re-registering the lease
		Object keepAlive = new Object();
		synchronized(keepAlive) {
		    try {
		    	keepAlive.wait();
		    } catch(java.lang.InterruptedException e) {
		    	// do nothing
		    }
		}
    }

    public JiniServiceServer(Configuration config) {
		// Create the service
		impl = new JiniServiceImpl();
	
		// Try to load the service ID from file.
		// It isn't an error if we can't load it, because
		// maybe this is the first time this service has run
		DataInputStream din = null;
		
		try {
		    din = new DataInputStream(new FileInputStream(this.getClass().getName() + ".id"));
		    serviceID = new ServiceID(din);
		} catch(Exception e) {
		    // ignore
		}

		try {
		    // and use this to construct an exporter
		    Exporter exporter = (Exporter) config.getEntry( "JiniServiceServer", "exporter", Exporter.class); 
		    // export an object of this class
		    this.rmiProxy = (IRemoteJiniService) exporter.export(impl);
		} catch(Exception e) {
		    System.err.println(e.toString());
		    e.printStackTrace();
		    System.exit(1);
		}


		System.setSecurityManager(new RMISecurityManager());

		// proxy primed with impl
		smartProxy = new JiniServiceProxy(rmiProxy);

        LookupDiscovery discover = null;
        
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println("Discovery failed " + e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {
        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

            ServiceItem item = new ServiceItem(serviceID, smartProxy, null);
            ServiceRegistration reg = null;

            try {
				reg = registrar.register(item, Lease.FOREVER);
		    } catch(java.rmi.RemoteException e) {
		    	System.err.println("Register exception: " + e.toString());
		    	continue;
		    }
		    
		    System.out.println("Service registered with id " + reg.getServiceID());
	
		    // set lease renewal in place
		    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);
	
		    // set the serviceID if necessary
		    if (serviceID == null) {
		    	serviceID = reg.getServiceID();
	
				// try to save the service ID in a file
				DataOutputStream dout = null;
				try {
				    dout = new DataOutputStream(new FileOutputStream(this.getClass().getName() + ".id"));
				    serviceID.writeBytes(dout);
				    dout.flush();
				} catch(Exception e) {
				    // ignore
				}
		    }
		}
    }

    public void notify(LeaseRenewalEvent evt) {
    	System.out.println("Lease expired " + evt.toString());
    }   
    
	public void discarded(DiscoveryEvent arg0) {
		// do nothing
	}   

} // JiniServiceServer
