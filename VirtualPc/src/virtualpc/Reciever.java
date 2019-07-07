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
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import sharedPackage.Packet;
import static virtualpc.VirtualPc.buffer;

/**
 *
 * @author maria afara
 */
public class Reciever extends Thread {

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    private int port;

    private Object recievedObject;

    public Reciever(int myport, ObjectInputStream ois, ObjectOutputStream oos) {

        System.out.println("*reciever initialized------------------------------------");
        this.port = myport;
        this.ois = ois;
        this.oos = oos;

        ///router name aw ip ....n2sa 
    }

    @Override
    public void run() {

        //  portConxs.getPortInstance(port).wait();
        while (true) {
            try {
                String messageReceived = "";
                System.out.println("*Ready to receive packets   ");

                Packet recievedPacket = (Packet) ois.readObject();
                messageReceived = recievedPacket.Message;
                System.out.println(messageReceived);
                while (recievedPacket.header.getMoreFragment()) {
                    recievedPacket = (Packet) ois.readObject();
                    messageReceived = messageReceived + recievedPacket.Message;
                }
                if (recievedPacket.header.getHeaderCheksum().equals(recievedPacket.header.getChecksum(recievedPacket.header.cheksumInput()))) {
                    int ttl = recievedPacket.header.getTTL();
                    ttl--;
                    if (ttl > 0) {
                        final String x = messageReceived;
                        final InetAddress y = recievedPacket.header.getSourceAddress();
                        final String t = recievedPacket.header.toString();
                        final String z = recievedPacket.header.getSourceHostname();
                        Platform.runLater(() -> {
                            buffer.appendText("Received '" + x + "' From " + y + "-" + z + "\n");
                            buffer.appendText("Header Message =" + t + "\n");

                        });
                        System.out.println("*Received Message =" + messageReceived);
                        System.out.println("*From             =" + recievedPacket.header.getSourceAddress() + ":" + recievedPacket.header.getSourceHostname());
                        System.out.println("*Header Message =" + recievedPacket.header.toString());
                    } else {
                        final String t = recievedPacket.header.toString();
                        Platform.runLater(() -> {
                            buffer.appendText("Packet TTL exceeded, therefore the message is dropped!" + "\n");
                            buffer.appendText("Header Message =" + t + "\n");
                        });

                        System.out.println("Packet TTL exceeded, therefore the message is dropped!");
                    }
                } else {
                    final String t = recievedPacket.header.getHeaderCheksum();
                    final String o = recievedPacket.header.getChecksum(recievedPacket.header.cheksumInput());
                    Platform.runLater(() -> {

                        buffer.appendText("Cheksum not equal, there's an alteration of the message" + "\n");
                        buffer.appendText("Initial Cheksum =" + t + "\n");
                        buffer.appendText("Current Cheksum =" + o + "\n");
                    });

                    System.out.println("Cheksum not equal, there's an alteration of the message");
                    System.out.println("Initial Cheksum =" + recievedPacket.header.getHeaderCheksum());
                    System.out.println("Current Cheksum =" + recievedPacket.header.getChecksum(recievedPacket.header.cheksumInput()));

                }
            } catch (IOException ex) {
                Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                //  Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassCastException ex) {
                //  Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
