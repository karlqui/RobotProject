import java.net.*;
import java.io.*;
import robot.*;

public class RobotController extends Thread
{
   private ServerSocket serverSocket;
   private Process p;
   private final MotorController motorController = new MotorController(); //create controller to controll the robot's motor
   
   public RobotController(int port) throws IOException
   {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(600000); //wait 10 min
		try{
			p = Runtime.getRuntime().exec("./start_viktor.sh");
			System.out.println("Mjpeg-Streamer started");
		}
		catch(Exception e){
			System.out.println("Failed to start Mjpeg-Streamer, error: " + e.getMessage());
		}
   }

   public void run()
   {
	  String message[];
	  String inS = "";
      while(true)
      {
         try
         {
			Socket server = serverSocket.accept();
			System.out.println("Client connected");
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
		    ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			out.flush();
			out.writeObject("Success");
			do{
					try{
						inS = (String)in.readObject(); 
						message = inS.split(" ");
						if(message.length==2){
							if(message[0].equals("Forward")){ 
								out.writeObject("Going Forward");
								motorController.motorForward(message[1]); //go forward with speed = message[1]
							}
							else if(message[0].equals("Backward")){ 
								out.writeObject("Going Backward");
								motorController.motorBackward(message[1]);
							}
							else if(message[0].equals("Left")){ 
								out.writeObject("Turning Left");
								motorController.motorLeft(message[1]);
							}
							else if(message[0].equals("Right")){ 
								out.writeObject("Turning Right");
								motorController.motorRight(message[1]);
							}
						}
						out.flush(); // flush buffer
						
					}
					catch(ClassNotFoundException classnot){
						System.err.println("Data received in unsupported format");
					}
					
			}while(!inS.equals("Exit")); //client decided to terminate the connection
			server.close();
			in.close();
			out.close();
			p.destroy();
			Runtime.getRuntime().exec("pkill mjpg_streamer"); //manually close mjpg streamer
			System.out.println("Connection terminated");
			System.exit(0);
         }catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
   }
   public static void main(String [] args)
   {
      try
      {
         Thread t = new RobotController(1337); //create new server thread on port 6066
         t.start(); 
		 System.out.println("Controller started");
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}