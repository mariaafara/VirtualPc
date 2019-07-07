package virtualpc;

import java.util.HashMap;

/**
 *
 * @author maria afara
 */
public class PortConxs {

    HashMap<Integer, Port> portsConxs;//kel port 3ndo thread port khas fi

    public PortConxs() {
        portsConxs = new HashMap<Integer, Port>();
       
    }

    synchronized public HashMap<Integer, Port> getPortsConxs() {
        return portsConxs;
    }

    synchronized public void addPort(int port, Port instancePort) {
        portsConxs.put(port, instancePort);
    }

    synchronized public Port getPortInstance(int port) {
        return portsConxs.get(port);
    }

    synchronized public boolean containsPort(int key) {
        return portsConxs.containsKey(key);
    }

}
