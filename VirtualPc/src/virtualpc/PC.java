/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualpc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AliFakih
 */
public class PC {

    String hostname;
int port;
    InetAddress ipAddress;

    final Object lockRouter = new Object();
    PortConxs portConxs;

    public void run() {

        Scanner scn = new Scanner(System.in);
        System.out.println("enter name of pc.............");
        String name = scn.nextLine();
        setHostname(name);

        for (int i = 0; i < 1; i++) {
            System.out.println("enter a port.................");
            initializePort(Integer.parseInt(scn.nextLine()));
        }
        for (int i = 0; i < 1; i++) {

            try {
                System.out.println("enter to establish connection....(myport:neighname:nexthop).......");
                String line = scn.nextLine();
                StringTokenizer st = new StringTokenizer(line, ":");
                int myport = Integer.parseInt(st.nextToken());
                String neighname = st.nextToken();
                int nexthop = Integer.parseInt(st.nextToken());

                initializeConnection(myport, InetAddress.getLocalHost(), neighname, nexthop);
            } catch (UnknownHostException ex) {
                Logger.getLogger(PC.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        while (true) {
        }

    }

    public PC(InetAddress ipAddress, String hostname, int port) {

        portConxs = new PortConxs();
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.port=port;
        initializePort(port);
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void initializeConnection(int port, InetAddress neighboraddress, String neighborhostname, int neighborport) {
        synchronized (this) {
            if (!portConxs.containsPort(port)) {
                System.out.println("*This port does not exists");
                return;
            }
            portConxs.getPortInstance(port).connect(neighboraddress, neighborhostname, neighborport);
        }
    }
    public void send(String msg, InetAddress destip, String desthostname){
         new Sender(msg,port,hostname, destip,desthostname,portConxs.getPortInstance(port).getOis(), portConxs.getPortInstance(port).getOos(),portConxs.getPortInstance(port).socket).start();
    }
    public void initializePort(int port) {
        synchronized (this) {
            if (portConxs.containsPort(port)) {
                System.out.println("*This port exists");
                return;
            }
            Port portclass = new Port(port, hostname);

            portConxs.addPort(port, portclass);//3m syv 3ndee lport

            portclass.start();
        }

    }
///wrong wrong wrong wrong

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

}
