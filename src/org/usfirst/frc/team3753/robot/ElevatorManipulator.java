package org.usfirst.frc.team3753.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ElevatorManipulator {
	// Class Vars we need
	private DigitalInput MinLimitSwitch;
    private DigitalInput MaxLimitSwitch;
    private PWMTalonSRX motorLift;
	
	public ElevatorManipulator(int motorPort, int minPort, int maxPort) { // Class Constructor
		motorLift = new PWMTalonSRX(motorPort);
		MinLimitSwitch = new DigitalInput(minPort);
		MaxLimitSwitch = new DigitalInput(maxPort);
	}
	
	public void loopFeed(double dir) {
        if (MaxLimitSwitch.get()) { // If the Max limit switch is pressed, we want to keep the values between -1 and 0
            dir = Math.max(dir, 0);
        }else if(MinLimitSwitch.get()) { // If the Min limit switch is pressed, we want to keep the values between 0 and 1
            dir = Math.min(dir, 0);
        }
		SmartDashboard.putBoolean("Elevator Min Switch: ", MinLimitSwitch.get());
		SmartDashboard.putBoolean("Elevator Max Switch: ", MaxLimitSwitch.get());
        motorLift.set(dir);
	}
	
}
