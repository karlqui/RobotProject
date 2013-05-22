package robot;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.net.*;
import java.io.*;

public class ServoController{
	final GpioController gpio = GpioFactory.getInstance();
	final GpioPinDigitalOutput servoPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "ervoPin", PinState.LOW);
	
	public void pulse(String d){
		double delay = Double.parseDouble(d);
		servoPin.high();
		final long INTERVAL = (long)(1000000 * delay);
		long start = System.nanoTime();
		long end=0;
		do{
			end = System.nanoTime();
		}while(start + INTERVAL >= end);
		System.out.println(end - start);
		servoPin.low();
		System.out.println(INTERVAL);
	}
}