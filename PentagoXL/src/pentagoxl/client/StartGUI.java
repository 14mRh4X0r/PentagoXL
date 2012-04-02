package pentagoxl.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class StartGUI extends JFrame implements ActionListener{
	
	private JButton     bConnect;
    private JTextField  tfPort;
    private JTextField 	tfAddress;
    private JTextField	tfName;
    private JComboBox	cbAmount;
    
    private static final String[] playerAmounts = {"Don't care", "2", "3", "4"};
	
	public StartGUI() {
		super("PentagoXL");
		
		buildGUI();
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new StartGUI();
	}

	private void buildGUI() {
		setSize(450,170);
		
		//Panel with textboxes to connect.
		JPanel p1 = new JPanel(new FlowLayout());
        JPanel pp = new JPanel(new GridLayout(4,2));     


        JLabel lbAddress = new JLabel("Address: ");
        tfAddress = new JTextField(12);

        JLabel lbPort = new JLabel("Port:");
        tfPort        = new JTextField(5);
        
        JLabel lbName = new JLabel("Name:");
        tfName        = new JTextField(System.getProperty("user.name"), 12);
        
        JLabel lbAmount = new JLabel("Amount of players:");
        cbAmount 		= new JComboBox(playerAmounts);
        
        pp.add(lbAddress);
        pp.add(tfAddress);
        pp.add(lbPort);
        pp.add(tfPort);
        pp.add(lbName);
        pp.add(tfName);
        pp.add(lbAmount);
        pp.add(cbAmount);
        
        bConnect = new JButton("Connect");
        bConnect.addActionListener(this);

        p1.add(pp, BorderLayout.WEST);
        p1.add(bConnect, BorderLayout.EAST);
        
        Container cc = getContentPane();
        cc.setLayout(new FlowLayout());
        cc.add(p1); 
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
			Object src = ev.getSource();
	        if (src == bConnect) {
	            
	        }
		
	}
}
