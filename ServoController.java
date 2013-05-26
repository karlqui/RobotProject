package robot;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import java.net.*;
import java.io.*;

public class ServoController{
	private final Serial serial = SerialFactory.createInstance();
	public ServoController(){
		serial.open(Serial.DEFAULT_COM_PORT, 9600);
	}
	public void turnServo(String direction){
		try{
			if(direction.equals("left"))
			{
				serial.write('a');
				serial.write('b');
				serial.write('l');
			}
			else if(direction.equals("right"))
			{
				serial.write('a');
				serial.write('b');
				serial.write('r');
			}
		}
		catch(SerialPortException ex) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
	}
}