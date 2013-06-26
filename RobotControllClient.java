// File Name GreetingClient.java

import mjpeg.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RobotControllClient
{
	public static int port = 6066;
	public static String serverName = "localhost";
	public static JFrame frame;
	private static String msg[] = new String[2];
	private static URL url;
	private static MjpegRunner runner;
	
	public static void main(String [] args) throws Exception
	{
		url = new URL("http://karlqui.no-ip.biz:8080/?action=stream");
		runner = new MjpegRunner(url);
		createGUI();
		runner.run();	
		  	
	}
	
	private static JButton b1,b2,b3,b4,b5, b6, b7, b8, b9;
	private static JPanel p1,p2;
	private static JLabel l1,l2;
	private static JTextField tf1, tf2;
	
	public static void createGUI() 
	{
		frame = new JFrame("RPiController");
		p1 = new JPanel();
		p2 = new JPanel();
		l1 = new JLabel("");
		l2 = new JLabel("");
		tf1 = new JTextField("karlqui.no-ip.biz", 20);
		tf2 = new JTextField("1337", 10);
		frame.setVisible(true);
		frame.add(p2);
		frame.setLayout(new GridLayout(1,1));
		GridLayout layout = new GridLayout(5,2);
		layout.setHgap(100);
		layout.setVgap(100);
		p2.setVisible(true);
		p1.setLayout(layout);
		frame.setSize(1000,700);
		b1 = new JButton("Forward");
		b2 = new JButton("Backward");
		b3 = new JButton("Left");
		b4 = new JButton("Right");
		b5 = new JButton("Exit");
		b7 = new JButton("Servo left");
		b8 = new JButton("Servo right");
		b9 = new JButton("Stop");
		b1.setSize(10,10);
		b6 = new JButton("Connect");
		
		p1.add(b1);
		p1.add(b2);
		p1.add(b3);
		p1.add(b4);
		p1.add(b9);
		p1.add(b5);
		p1.add(b7);
		p1.add(b8);
		p1.add(l2);
		
		p2.add(b6);
		p2.add(l1);
		
		p2.add(tf1);
		p2.add(tf2);
		tf1.getDocument().addDocumentListener(textListener);
		tf2.getDocument().addDocumentListener(textListener);
	    b1.addActionListener(buttonListener);
		b2.addActionListener(buttonListener);
		b3.addActionListener(buttonListener);
		b4.addActionListener(buttonListener);
		b5.addActionListener(buttonListener);
		b6.addActionListener(buttonListener);
		b7.addActionListener(buttonListener);
		b8.addActionListener(buttonListener);
		b9.addActionListener(buttonListener);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
   }
	private static ActionListener buttonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		try{
			if(e.getActionCommand().equals("Forward")){
				System.out.println("Sending message");
				dataOut.writeObject("Forward SLOW");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
			}
			else if(e.getActionCommand().equals("Backward")){
				System.out.println("Sending message");
				dataOut.writeObject("Backward SLOW");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
			}
			else if(e.getActionCommand().equals("Left")){
				System.out.println("Sending message");
				dataOut.writeObject("Left SLOW");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
			}
			else if(e.getActionCommand().equals("Right")){
				System.out.println("Sending message");
				dataOut.writeObject("Right SLOW");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
			}
			else if(e.getActionCommand().equals("Stop")){
				System.out.println("Sending message");
				dataOut.writeObject("Stop");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
			}
			
			else if(e.getActionCommand().equals("Exit")){
				System.out.println("Shutting down");
				dataOut.writeObject("Exit");
				dataOut.flush();
				client.close();
				System.exit(0);
			}
			else if(e.getActionCommand().equals("Servo left")){
				System.out.println("Servo turning left");
				dataOut.writeObject("Servo left");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
				
			}
			else if(e.getActionCommand().equals("Servo right")){
				System.out.println("Servo turn right");
				dataOut.writeObject("Servo right");
				dataOut.flush();
				System.out.println("Server says " + (String)dataIn.readObject());
			}
			else if(e.getActionCommand().equals("Connect")){
				try{
					setupClient();
					frame.remove(p2);
					frame.add(p1);
					frame.add(runner);
					p1.setVisible(true);
					
				}
				catch(Exception b)
			    {
					System.out.println("test");
					p2.setVisible(true);
					p1.setVisible(false);
					l1.setText("Failed to connect to: " + serverName + " on port: " + port +  "	 error: " + b.getMessage());
				}
			}
		 }
		 catch(Exception ex){}
		}
    }; 
	
	private static DocumentListener textListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			setTF();
		}
		public void removeUpdate(DocumentEvent e) {
			setTF();
		}
		public void insertUpdate(DocumentEvent e) {
			setTF();
		}
		private void setTF(){
			serverName = tf1.getText();
			try{
				port = Integer.parseInt(tf2.getText());
			}
			catch(NumberFormatException nfe){
				//tf2.setText("Not an int");
			}
			catch(Exception e1){}
		}
	};
	
	private static ObjectOutputStream dataOut;
	private static ObjectInputStream dataIn;
	private static Socket client;
	
	private static void setupClient() throws Exception {
		l1.setText("Connecting to " + serverName + " on port " + port + " . . .");
        client = new Socket(serverName, port);
        l2.setText("Just connected to " + client.getRemoteSocketAddress());
        OutputStream outToServer = client.getOutputStream();
        dataOut = new ObjectOutputStream(outToServer);
		dataOut.flush();
		dataIn = new ObjectInputStream(client.getInputStream());
		System.out.println((String)dataIn.readObject());   
		
	}  
   
}