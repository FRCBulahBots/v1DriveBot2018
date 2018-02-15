package org.usfirst.frc.team3753.robot;
import edu.wpi.first.wpilibj.Spark;

public class BoxManipulator {
	private Spark rightSideMotors;
	private Spark leftSideMotors;
	private double motorSpeed;
	
	public BoxManipulator(int rightsidemotorport, int leftsidemotorport, double speed) {
		rightSideMotors = new Spark(rightsidemotorport);
		leftSideMotors = new Spark(leftsidemotorport);
		motorSpeed = speed;
	}
	
	public void overrideSpeed(double speed) {
		motorSpeed = speed;
	}
	
	public void loopFeed(double joyval) {
		if(joyval > 0.2f) {
			processVal(RobotEnums.BoxManipulator.RECEIVE);
		} else if (joyval < 0.2f && joyval > -0.2f) {
			processVal(RobotEnums.BoxManipulator.HOLD);
		} else {
			processVal(RobotEnums.BoxManipulator.DEPOSIT);
		}
	}
	
	private void driveMotors(int dir) {
		rightSideMotors.set(dir * motorSpeed * -1);
		leftSideMotors.set(dir * motorSpeed);
	}
	
	private void processVal(RobotEnums.BoxManipulator cmd) {	
		switch (cmd){
			case RECEIVE:
				driveMotors(1);
				break;
			case HOLD:
				driveMotors(0);
				break;
			case DEPOSIT:
				driveMotors(-1);
				break;
		}
	}
}
