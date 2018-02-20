package org.usfirst.frc.team3753.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class LEDManager {

	private I2C i2cInterface; 
	private boolean successfulComms; // will be true as long as controller reports back with the correct bytes :) 
	
	public LEDManager(Port port, int devaddr) {
		i2cInterface = new I2C(port, devaddr);
	
	}
	
	public LEDManager() {
		this(Port.kOnboard, 8); // Assume if no params are supplied, that controller will be addr 8 and on 'onboard' I2c port
	}
	
	
	
	
}
