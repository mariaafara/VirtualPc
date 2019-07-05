package virtualpc;

import sharedPackage.Neighbor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import static virtualpc.VirtualPc.buffer;

/**
 *
 * @author maria afara
 */
public class PortConnectionEstablish extends Thread {

    private Port p;
    private Socket socket;
    private int neighborport;
    private InetAddress neighborip;
    private String neighname;

    private int myport;
    private String neighborhostname;
    private String myhostname;

    public PortConnectionEstablish(int myport, String myhostname, InetAddress neighborip, String neighborhostname, int neighborport, Port p) {

        this.neighborport = neighborport;
        this.myport = myport;
        this.p = p;
        this.neighborip = neighborip;
        this.neighborhostname = neighborhostname;
        this.myhostname = myhostname;
    }

    @Override
    public void run() {
        boolean bool;
        if (!p.isconnectionEstablished()) {

            try {

                System.out.println("*establishing connection with ip=" + neighborip + " port=" + neighborport);
                socket = new Socket(neighborip, neighborport);
                System.out.println("*socket : myport " + socket.getLocalPort() + " destport " + socket.getPort());

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(new Neighbor(PC.ipAddress, myhostname, myport));
                p.active = false;
                System.out.println("*sending my self as a neighbor to ip=" + PC.ipAddress + " port=" + myport);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                bool = objectInputStream.readBoolean();

                System.out.println("*" + bool + " was recieved");

                if (bool) {
                    //  rt.activateEntry(neighborport);
                    p.setSocket(socket);

                    p.setStreams(objectInputStream, objectOutputStream);

                    p.setconnectionEstablished(true);
                    Platform.runLater(() -> {
                        buffer.appendText("Connection is established at port " + myport + " with neighb = " + neighname + " , " + neighborport + "\n");
                    });

                    System.out.println("connection is established at port " + myport + " with neighb = " + neighname + " , " + neighborport);

                    ///sar jehez yst2bel 
                    Platform.runLater(() -> {
                        buffer.appendText("Start Receiving\n");
                    });

                    System.out.println("Start Receiving");
                    new Reciever(myport, p.getOis(), p.getOos()).start();
//                    new Sender(myport,myhostname, p.getOis(), p.getOos(),p.socket).start();

                } else {
                    Platform.runLater(() -> {
                        buffer.appendText("*waiting a connection from " + neighborport + "\n");
                    });
                    System.out.println("*waiting a connection from " + neighborport);
                    socket.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(PortConnectionEstablish.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            //already exist a conx at this port
            //and i have to implement a methode to delete the old conx
            System.out.println("*you have to delete the old connection");
        }
    }
}
