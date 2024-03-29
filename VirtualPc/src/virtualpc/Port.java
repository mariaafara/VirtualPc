/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author maria afara
 */
public class Port extends Thread {

    boolean connectionEstablished;
    Socket socket;

    int myport;
    PortConnectionWait portConnectionWait;
    PortConnectionEstablish portConnectionEstablish;

    final Object lockconnectionEstablished = new Object();
    final Object lockSocket = new Object();
    final Object lockOos = new Object();
    final Object lockOis = new Object();
    final Object lockactivated = new Object();
    ObjectInputStream ois;
    ObjectOutputStream oos;

    String myhostname;

    boolean active = true;
    PC pc;

    public Port(int myport, String myhostname, PC pc) {
        this.pc = pc;
        System.out.println("*Port " + myport + " initialized");
        this.myhostname = myhostname;
        this.connectionEstablished = false;
        this.myport = myport;
        this.socket = null;

        portConnectionWait = new PortConnectionWait(myport, myhostname, this, pc);
        this.ois = null;
        this.oos = null;
    }

    public boolean isconnectionEstablished() {
        synchronized (lockconnectionEstablished) {
            return connectionEstablished;
        }
    }

    public void setconnectionEstablished(boolean connectionEstablished) {
        synchronized (lockconnectionEstablished) {
            this.connectionEstablished = connectionEstablished;
        }
    }

    public Socket getSocket() {

        synchronized (lockSocket) {

            return socket;
        }
    }

    public void setSocket(Socket socket) throws IOException {
        System.out.println("in setScoket method2 for port " + myport + " before snchronized");

        synchronized (lockSocket) {
            this.socket = socket;

            System.out.println("*2*in setScoket method2 for port " + myport + " after ");
        }
    }

    public void setStreams(ObjectInputStream ois, ObjectOutputStream oos) {
        setOis(ois);
        setOos(oos);
    }

    public ObjectInputStream getOis() {
        synchronized (lockOos) {
            return ois;
        }
    }

    public void setOis(ObjectInputStream ois) {
        synchronized (lockOis) {
            this.ois = ois;
        }
    }

    public ObjectOutputStream getOos() {
        synchronized (lockOos) {
            return oos;
        }
    }

    public void setOos(ObjectOutputStream oos) {
        synchronized (lockOis) {
            this.oos = oos;
        }
    }

    @Override
    public void run() {

        super.run();
        portConnectionWait.start();

    }

    public void connect(PC p, InetAddress neighborAddress, String neighborhostname, int neighborport) {

        PortConnectionEstablish pce = new PortConnectionEstablish(myport, myhostname, neighborAddress, neighborhostname, neighborport, this, p);
        pce.start();
    }

}
