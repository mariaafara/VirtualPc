/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualpc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sharedPackage.Packet;
import sharedPackage.PacketFactory;

/**
 *
 * @author maria afara
 */
public class VirtualPc extends Application {
    
    static String currentHostIpAddress = null;
    public static TextArea buffer;
//192.168.182.1
    public PC pc;
    Stage stage;
    String filename;
    String hostname;
    Registry registry;
    String command;
    String msg;
    String position;
    int port;
    String entermsg = "Enter msg you want to forward \n";
    
    String entertoconnect = "->Enter ip address neighip hostname eighhostname port neighport \n";
    String enterdestination = "->Enter a destination to forward a packet in the form ip hostname \n";
    boolean setdestination = false;
    InetAddress address;
    String nexthostname;
    
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        //  buffer = new ObservaleStringBuffer();
        VBox root = new VBox(3);
        HBox hostnameConnectionbox = new HBox();
        buffer = new TextArea();//for the feedbacks
        buffer.setEditable(false);
        // textArea.textProperty().bind(buffer);
        buffer.setWrapText(true);
        Label lblip = new Label();
        root.setVgrow(buffer, Priority.ALWAYS);
        TextField txtPort = new TextField();
        txtPort.setPrefWidth(120);
        Tooltip tooltipport = new Tooltip("enter the port of the pc");
        txtPort.setTooltip(tooltipport);
        TextField txtHostname = new TextField();
        Tooltip tooltiphostname = new Tooltip("enter the hostname of the pc");
        txtPort.setTooltip(tooltiphostname);
        txtHostname.setPrefWidth(150);
        Button btnConnect = new Button("Connect");
        Button btnExport = new Button("Export Feedbacks");
        TextField txtcommand = new TextField();
        txtcommand.setDisable(false);
        txtcommand.setPrefWidth(425);
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    if (btnConnect.getText().equals("Connect")) {
                        if (InetAddress.getByName(getCurrentEnvironmentNetworkIp()) == null) {
                            System.out.println(InetAddress.getLocalHost());
                            pc = new PC(InetAddress.getByName("127.0.0.1"), txtHostname.getText(), Integer.parseInt(txtPort.getText()));
                            lblip.setText("    " + InetAddress.getByName("127.0.0.1") + "");
                            
                        } else {
                            pc = new PC(InetAddress.getByName(getCurrentEnvironmentNetworkIp()), txtHostname.getText(), Integer.parseInt(txtPort.getText()));
                            System.out.println(InetAddress.getByName(getCurrentEnvironmentNetworkIp()));
                            lblip.setText(getCurrentEnvironmentNetworkIp());
                        }
                        
                        hostname = txtHostname.getText();
                        port = Integer.parseInt(txtPort.getText());
                        Platform.runLater(() -> {
                            buffer.appendText("Pc " + hostname + "is ready to establish and accept conx at port " + txtPort.getText() + "\n");
                        });
                        Platform.runLater(() -> {
                            buffer.appendText("To establish conx \n");
                        });
                        
                        Platform.runLater(() -> {
                            buffer.appendText(entertoconnect);
                        });
                        txtHostname.setDisable(true);
                        txtPort.setDisable(true);
                        // Process.Start("path/to/your/file")
                        primaryStage.setTitle("Pc " + pc.getHostname());
                        btnConnect.setText("Disconnect");
                        btnExport.setDisable(false);
                    } else if (btnConnect.getText().equals("Disconnect")) {
                        pc.disconnect();
                        Platform.runLater(() -> {
                            buffer.appendText("Disconnected\n");
                        });
                        txtcommand.setDisable(true);
                    }
                } catch (UnknownHostException ex) {
                    Logger.getLogger(VirtualPc.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(VirtualPc.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btnExport.setDisable(true);
        btnExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    extractFeedback();
                } catch (IOException ex) {
                    Logger.getLogger(VirtualPc.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        position = "connect";
        txtcommand.setOnAction(e -> {
            command = txtcommand.getText();
            //  lastCommand = lbl.getText();
//            if (command.equals("")) {
//                emptycommad = true;
//            }

            switch (position) {
                case "connect":
                    String[] connect_array = command.split(" ");
                    String first_connect_command = connect_array[0];
                    switch (first_connect_command) {
                        default:
                            Platform.runLater(() -> {
                                buffer.appendText(command + "\n%unknown command or computer name , or unable to find computer address\n");
                            });
                            break;
                        case "ip":
                            if (connect_array.length != 7
                                    || !connect_array[1].equals("address")
                                    || !connect_array[3].equals("hostname")
                                    || !connect_array[5].equals("port")) {
                                System.out.println("syntax error");
                                Platform.runLater(() -> {
                                    buffer.appendText("" + command + "\n");
                                });
                                
                                Platform.runLater(() -> {
                                    buffer.appendText("%unknown command or computer name , or unable to find computer address\n");
                                });
                                
                            } else {
                                try {
                                    int nextport = Integer.parseInt(connect_array[6]);
                                    InetAddress address = InetAddress.getByName(connect_array[2]);
                                    String nexthostname = connect_array[4];

                                    //initializeConx
                                    if (nextport != port && !address.getHostAddress().equals(InetAddress.getByName("127.0.0.1"))) {
                                        pc.initializeConnection(port, address, nexthostname, nextport);
                                        Platform.runLater(() -> {
                                            buffer.appendText("" + command + "\n");
                                        });
                                        Platform.runLater(() -> {
                                            buffer.appendText("trying to initialize conx" + port + " with " + address.getHostAddress() + "-" + nexthostname + "to" + nextport + "\n");
                                        });
                                        Platform.runLater(() -> {
                                            buffer.appendText("port " + port + " is waiting for a connection\n");
                                        });
                                        while (!pc.isconnectionEstablished()) {
                                            synchronized (pc) {
                                                pc.wait();
                                            }
                                            System.out.println("waiting\n");
                                        } //waiting for connection approvalll 
                                        //hon ha n7tej wait notify
                                        ////hon abel lezmm nt222kd ino lconnextion is establlished

                                        position = "destination";
                                        
                                        Platform.runLater(() -> {
                                            buffer.appendText(enterdestination);
                                        });
                                        
                                    }

                                    /////////////////
                                } catch (NumberFormatException efee) {
                                    Platform.runLater(() -> {
                                        buffer.appendText("" + command + "\n");
                                    });
                                    
                                    Platform.runLater(() -> {
                                        buffer.appendText("%unknown command or computer name , or unable to find computer address\n");
                                    });
                                    
                                } catch (UnknownHostException ex) {
                                    Platform.runLater(() -> {
                                        buffer.appendText("" + command + "\n");
                                    });
                                    
                                    Platform.runLater(() -> {
                                        buffer.appendText("%unknown command or computer name , or unable to find computer address\n");
                                    });
                                    
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(VirtualPc.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;
                    }
                case "destination":
                    String[] dest_array = command.split(" ");
                    if (dest_array.length == 2) {
                        try {
                            address = InetAddress.getByName(dest_array[0]);
                            nexthostname = dest_array[1];
                            
                            Platform.runLater(() -> {
                                buffer.appendText("" + command + "\n");
                            });
                            
                            Platform.runLater(() -> {
                                buffer.appendText("destination is entered\n");
                            });
                            
                            position = "forward";
                            
                            Platform.runLater(() -> {
                                buffer.appendText(entermsg);
                            });
                            
                        } catch (UnknownHostException ex) {
                            Platform.runLater(() -> {
                                buffer.appendText("" + command + "\n");
                            });
                            
                            Platform.runLater(() -> {
                                buffer.appendText("%unknown command or computer name , or unable to find computer address\n");
                            });
                            
                        }
                    }
                    break;
                case "forward":
                    Platform.runLater(() -> {
                        buffer.appendText(" " + command);
                        pc.send(command, address, nexthostname);
                    });
                    Platform.runLater(() -> {
                        buffer.appendText("msg sent\n");
                    });
                    position = "destination";
                    Platform.runLater(() -> {
                        buffer.appendText(enterdestination);
                    });
                    break;
                
                default:
                    Platform.runLater(() -> {
                        buffer.appendText(" " + command + "\n");
                    });
                    
                    Platform.runLater(() -> {
                        buffer.appendText("%unknown command or computer name , or unable to find computer address\n");
                    });
                    
                    break;
            }
            
        });
        
        hostnameConnectionbox.getChildren()
                .addAll(txtHostname, txtPort, btnConnect, btnExport, lblip);
        root.getChildren()
                .addAll(hostnameConnectionbox, buffer, txtcommand);
        //buffer.appendText("kakjhas\nsdfdghj\nadsafdsgdhj\nadsafdsgf\n");
        primaryStage.setScene(
                new Scene(root, 650, 400));
        
        primaryStage.setTitle(
                "Pc");
        
        primaryStage.show();
    }
    
    public String getCurrentEnvironmentNetworkIp() {
        
        if (currentHostIpAddress == null) {
            Enumeration<NetworkInterface> netInterfaces = null;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();
                
                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    //System.out.println(ni.getName());
                    if (!ni.getName().contains("wlan")) {
                        continue;
                    }
                    Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        InetAddress addr = address.nextElement();
                        //                      log.debug("Inetaddress:" + addr.getHostAddress() + " loop? " + addr.isLoopbackAddress() + " local? "
                        //                            + addr.isSiteLocalAddress());
                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                                && !(addr.getHostAddress().indexOf(":") > -1)) {
                            //System.out.println(addr);
                            currentHostIpAddress = addr.getHostAddress();
                            return currentHostIpAddress;
                        }
                    }
                }
                if (currentHostIpAddress == null) {
                    currentHostIpAddress = "127.0.0.1";
                }
                
            } catch (SocketException e) {
//                log.error("Somehow we have a socket error acquiring the host IP... Using loopback instead...");
                currentHostIpAddress = "127.0.0.1";
            }
        }
        return currentHostIpAddress;
    }

    /**
     * @param args the command line arguments
     */
    public void extractFeedback() throws FileNotFoundException, IOException {
        if (buffer.getText().equals("")) {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("No FeedBacks");
            alert1.setContentText("Oups, There's Nothing To Save...");
            alert1.showAndWait();
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Specify a file to save the Feedback!");
        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if (selectedDirectory == null) {
            //saveResults.setText("No Directory selected");
        } else {
            String filename = selectedDirectory.getAbsolutePath();
            File f2 = new File(filename + "\\" + hostname + "Feedbacks.txt");
            
            f2.delete();
            File f1 = new File(filename + "\\" + hostname + "Feedbacks.txt");
            
            PrintWriter writer = new PrintWriter(f1);
            writer.println("Feebacks exported at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            String[] contents = buffer.getText().split("\n");
            
            for (int i = 0; i < contents.length; i++) {
                
                writer.println(contents[i] + "\n");
            }
            writer.close();
            
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
