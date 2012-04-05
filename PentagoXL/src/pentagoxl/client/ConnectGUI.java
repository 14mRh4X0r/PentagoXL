package pentagoxl.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

import pentagoxl.spel.Speler;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.ProtocolError;

public class ConnectGUI extends JFrame implements NetHandler.Listener, ActionListener {

    private JButton bConnect;
    private JTextField tfPort;
    private JTextField tfAddress;
    private JTextField tfName;
    private JComboBox cbAmount;
    private JLabel lbMessage;
    private boolean nacked = false;
    private boolean acked = false;
    private ProtocolError nack;
    private static final String[] playerAmounts = {"Don't care", "2", "3", "4"};
    
    public static final String[] SUPPORTEDCOMMANDS = {/*ProtocolEndpoint.CMD_CHAT*/};

    public ConnectGUI() {
        super("PentagoXL > Connect");
        buildGUI();
        this.pack();
        this.setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }

            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void buildGUI() {

        //Connection panel		
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel pl = new JPanel(new BorderLayout());
        JPanel pp = new JPanel(new GridLayout(5, 2));


        JLabel lbAddress = new JLabel("Address: ");
        tfAddress = new JTextField(12);

        JLabel lbPort = new JLabel("Port:");
        tfPort = new JTextField(5);

        JLabel lbName = new JLabel("Name:");
        tfName = new JTextField(System.getProperty("user.name"), 12);

        JLabel lbAmount = new JLabel("Amount of players:");
        cbAmount = new JComboBox(playerAmounts);

        lbMessage = new JLabel();

        pp.add(lbAddress);
        pp.add(tfAddress);
        pp.add(lbPort);
        pp.add(tfPort);
        pp.add(lbName);
        pp.add(tfName);
        pp.add(lbAmount);
        pp.add(cbAmount);

        pl.add(lbMessage, BorderLayout.CENTER);

        bConnect = new JButton("Connect");
        bConnect.addActionListener(this);

        pl.add(pp, BorderLayout.NORTH);
        p1.add(pl, BorderLayout.NORTH);
        p1.add(bConnect, BorderLayout.SOUTH);

        Container cc = getContentPane();
        cc.add(p1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bConnect)
            tryConnect();

    }

    //Tries to connect. If connection succeeds, opens gameGUI and hides this.
    private void tryConnect() {
        String addrString = tfAddress.getText();
        String portString = tfPort.getText();
        String name = tfName.getText();
        try {
            InetAddress host = InetAddress.getByName(addrString);
            int port = Integer.parseInt(portString);
            Socket sock = new Socket(host, port);
            NetHandler nh = new NetHandler(sock);
            nh.addListener(this);
            String[] nameAndCommands = new String[SUPPORTEDCOMMANDS.length + 1];
            nameAndCommands[0] = name;
            for (int i = 0; i < SUPPORTEDCOMMANDS.length; i++) {
            	nameAndCommands[i+1] = SUPPORTEDCOMMANDS[i];
            }
            nh.addMessage(ProtocolEndpoint.CMD_HELLO, nameAndCommands);
            while (!acked && !nacked)
                synchronized (this) {
                    this.wait();
                }
            if (nacked)
                lbMessage.setText(nack.getDescription());
            else { //should be acked, so no need to check
                String amountToPlayWith = (String) cbAmount.getSelectedItem();
                if (amountToPlayWith.equals(playerAmounts[0]))
                    amountToPlayWith = "-1";
                new GUI(new ClientClient(name, nh), this); //create a new GUI instance with a Client
                nh.addMessage(ProtocolEndpoint.CMD_JOIN, amountToPlayWith);
                this.setVisible(false);
            }
        } catch (UnknownHostException e) {
            lbMessage.setText("Address not found");
        } catch (NumberFormatException e) {
            lbMessage.setText("Not a valid port number");
        } catch (IOException e) {
            lbMessage.setText("Something went wrong while connecting");
        } catch (InterruptedException e) {
        }
        acked = false;
        nacked = false;
    }

    @Override
    public synchronized void onReceive(String cmd, String[] args) {
        if (cmd.equals(ProtocolEndpoint.SRV_NACK)) {
            nacked = true;
            if (args.length > 0)
                try {
                    int errCode = Integer.parseInt(args[0]);
                    nack = ProtocolError.lookupCode(errCode);
                } catch (NumberFormatException e) {
                    nack = ProtocolError.UNSPECIFIED;
                }
            notifyAll();
        } else if (cmd.equals(ProtocolEndpoint.SRV_ACK)) {
            acked = true;
            notifyAll();
        }

    }

    public static void main(String[] args) {
        new ConnectGUI();
    }
}
