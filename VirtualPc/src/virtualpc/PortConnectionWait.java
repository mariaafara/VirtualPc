package virtualpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import sharedPackage.Neighbor;
import static virtualpc.VirtualPc.buffer;

/**
 * This class listens on given port for incoming connection
 *
 * @author maria afara
 */
public class PortConnectionWait extends Thread {

    ServerSocket serversocket;
    private Port p;
    Socket socket;
    String msg;
    int myport;
    String myhostname;
    PC pc;

    public PortConnectionWait(int myport, String myhostname, Port p,    PC pc) {
        
        try {
            //Creating server socket
            Platform.runLater(() -> {
                buffer.appendText("port " + myport + " waiting for a conx\n");
            });
            System.out.println("*port " + myport + " waiting for a conx");
            serversocket = new ServerSocket(myport);
            this.p = p;
            this.myport = myport;
            this.myhostname = myhostname;
            this.pc=pc;
        } catch (IOException ex) {
            Logger.getLogger(PortConnectionWait.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {

        ObjectOutputStream objectOutputStream;
        while (true) {
            try {
           
                System.out.println("*port " + myport + " still waiting for a connection");

                socket = serversocket.accept();

                // rt.printTable("**Checking**");
                System.out.println("*socket :myport " + socket.getLocalPort() + " destport " + socket.getPort());

                System.out.println("*connection accepteed at port " + myport);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Neighbor neighbor = (Neighbor) objectInputStream.readObject();

                //neighbor.neighborPort is the next hop 
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                if (!p.active) {

                    System.out.println("before activateEntry");
                    p.active=true;

                    System.out.println("after activateEntry and before set socket");
                    System.out.println("\n");

                    System.out.println("\n");

                    p.setSocket(socket);
                    p.setStreams(objectInputStream, objectOutputStream);
                    System.out.println("after setSocket");
                    
                    p.setconnectionEstablished(true);
                    synchronized(pc){
                        pc.notifyAll();
                    }
                    Platform.runLater(() -> {
                        buffer.appendText("Connection is established at port " + myport + "\n");
                    });
                    objectOutputStream.writeBoolean(true);
                    objectOutputStream.flush();

                    ///sar jehez yst2bel 
                    System.out.println("ready to receive");
                    Platform.runLater(() -> {
                        buffer.appendText("Start Receiving\n");
                    });
                    new Reciever(myport, p.getOis(), p.getOos()).start();
//                    new Sender( myport,myhostname, p.getOis(), p.getOos(),p.socket).start();

                    System.out.println("*true was sent");

                } else {

                    objectOutputStream.writeBoolean(false);
                    objectOutputStream.flush();

                    System.out.println("*false was sent");
                    System.out.println("*my turn to establish the connection on my side with port " + myport);

                    socket.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(PortConnectionWait.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PortConnectionWait.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
