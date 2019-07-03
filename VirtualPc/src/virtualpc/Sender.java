/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import sharedPackage.FailedNode;
import sharedPackage.Packet;
import sharedPackage.PacketFactory;
import sharedPackage.RoutingTableKey;

/**
 *
 * @author AliFakih
 */
public class Sender extends Thread {

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    private int myport;
    private String myhostname;
    private Object recievedObject;
    Socket portcnX;
    String msg;
    InetAddress destip;
    String desthostname;

    public Sender(String msg, int myport, String myhostname, InetAddress destip, String desthostname, ObjectInputStream ois, ObjectOutputStream oos, Socket portcnX) {
        this.msg = msg;
        System.out.println("*Sender initialized------------------------------------");
        this.myport = myport;
        this.ois = ois;
        this.oos = oos;
        this.portcnX = portcnX;
        this.myhostname = myhostname;
        this.destip = destip;
        this.desthostname = desthostname;
    }

    @Override
    public void run() {

        PacketFactory packetFactory = new PacketFactory(700);

        ArrayList<Packet> ar;
        try {
            ar = packetFactory.getPacket(msg, InetAddress.getLocalHost(), myhostname, destip, desthostname);
            System.out.println("***" + ar.size());
            System.out.println("***" + ar.get(0).header.getMoreFragment());
            for (int i = 0; i < ar.size(); i++) {
                System.out.println(ar.get(i).Message);
                System.out.println(ar.get(i).header.toString());
                oos.writeObject(ar.get(i));
            }
            System.out.println("end");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
