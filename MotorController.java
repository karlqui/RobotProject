package robot;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class MotorController {
	final GpioController gpio = GpioFactory.getInstance();
	final GpioPinDigitalOutput motor1pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Motor1Pin1", PinState.LOW);//motor is off as default
	final GpioPinDigitalOutput motor1pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Motor1Pin2", PinState.LOW);
	final GpioPinDigitalOutput motor2pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Motor2Pin1", PinState.LOW);
	final GpioPinDigitalOutput motor2pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "Motor2Pin2", PinState.LOW);
	private static int getSpeed(String speed){
		if(speed.equals("FAST")){
			return 200;
		}
		else if(speed.equals("MEDIUM")){
			return 400; //TODO decide what is medium and slow
		}
		else if(speed.equals("SLOW")){
			return 800;
		}
		else
			return -1; //error
	}
	public void shutDown(){ //to shutdown the controller
		gpio.shutdown();
	}
	
	public void motorForward(String speed){
		motor1pin1.pulse(500);
		motor2pin1.pulse(500);
		//motor1pin1.high();
		//motor2pin1.high();
		motor1pin2.low();
		
	
		motor2pin2.low();
	}
	public void motorLeft(String speed){
		motor1pin1.pulse(500);
		//motor1pin1.high();
		motor1pin2.low();
		motor2pin1.low();
		motor2pin2.pulse(500);
		//motor2pin2.high();
	}
	public void motorRight(String speed){
		motor1pin1.low();
		motor1pin2.pulse(500);
		//motor1pin2.high();
		motor2pin1.pulse(500);
		//motor2pin1.high();
		motor2pin2.low();
	}
	public void motorBackward(String speed){
		motor1pin1.low();
		motor1pin2.pulse(500);
		//motor1pin2.high();
		motor2pin1.low();
		motor2pin2.pulse(500);
		//motor2pin2.high();
	}
	public void motorStop(){
		motor1pin1.low();
		motor1pin2.low();
		motor2pin1.low();
		motor2pin2.low();
	}	
}