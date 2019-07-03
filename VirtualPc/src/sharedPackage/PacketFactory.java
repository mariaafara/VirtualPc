/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedPackage;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

/**
 *
 * @author Administrator
 */
public class PacketFactory {

    String Message;
    int MTU;

    public PacketFactory(int MTU) {

        this.MTU = MTU;

    }
//, int portDest, int portSource

    public ArrayList<Packet> getPacket(String Message, InetAddress ipSource, String hostnameSource, InetAddress ipDest, String hostnameDest) throws IOException, NoSuchAlgorithmException, CloneNotSupportedException {
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        ArrayList<Packet> packets = new ArrayList<>();
        this.Message = Message;
        // ttl =15
        Header h = new Header(n, 5, ipSource, ipDest, hostnameSource, hostnameDest);//, portSource, portDest
        h.headerCheksum = h.getChecksum(h);
        h.totalLength = 123;
        h.DHTL = 0;
        h.moreFragment = false;
        h.DHTL = ObjectSizeCalculator.getObjectSize(h);
        System.out.println(ObjectSizeCalculator.getObjectSize(h));
        String temp = new String(Message);
        Packet p = new Packet(h, Message);
        System.out.println("Packet size: " + ObjectSizeCalculator.getObjectSize(p));
        
        if (ObjectSizeCalculator.getObjectSize(p) <= MTU) {
            packets.add(p);

        } else {
            Packet packetTemp;
            String temp1 = "";
            System.out.println(temp.length());
            while (temp.length() != 0) {
                temp1 = "";
                for (int i = 0; i < temp.length(); i++) {
                    temp1 += temp.charAt(i);
                    Header h1 = h.clone();
                    packetTemp = new Packet(h1, temp1);
                    if (ObjectSizeCalculator.getObjectSize(packetTemp) >= MTU) {
                        temp1 = temp1.substring(0, temp1.length() - 1);
                        temp = temp.substring(i);
                        packetTemp = new Packet(h, temp1);
                        packets.add(packetTemp);
                        break;
                    }
                }
                Header h1 = h.clone();
                packetTemp = new Packet(h1, temp);
                if (ObjectSizeCalculator.getObjectSize(packetTemp) <= MTU) {
                    packets.add(packetTemp);
                    break;
                }
            }
        }
        for (int i = 0; i < packets.size() - 1; i++) {
            packets.get(i).header.moreFragment = true;
            packets.get(i).header.totalLength = (int) ObjectSizeCalculator.getObjectSize(packets.get(i));
            packets.get(i).header.headerCheksum = packets.get(i).header.getChecksum(packets.get(i).header.cheksumInput());
            packets.get(i).header.DHTL = ObjectSizeCalculator.getObjectSize(packets.get(i).header);

        }

        packets.get(packets.size() - 1).header.moreFragment = false;
        packets.get(packets.size() - 1).header.totalLength = (int) ObjectSizeCalculator.getObjectSize(packets.size() - 1);
        packets.get(packets.size() - 1).header.headerCheksum = packets.get(packets.size() - 1).header.getChecksum(packets.get(packets.size() - 1).header.cheksumInput());
        packets.get(packets.size() - 1).header.DHTL = ObjectSizeCalculator.getObjectSize(packets.get(packets.size() - 1).header);
        System.out.println("fin");
        return packets;
    }

}
