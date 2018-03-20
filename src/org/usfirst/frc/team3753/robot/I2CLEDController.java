package org.usfirst.frc.team3753.robot;

import org.usfirst.frc.team3753.robot.LEDCOLORS;
import org.usfirst.frc.team3753.robot.LEDFX;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class I2CLEDController {

	private I2C i2cInterface; 
	private boolean successfulComms = false; // will be true as long as controller reports back with the correct bytes :) 
	private boolean debuggingEnabled = false;
	public I2CLEDController(Port port, int devaddr, boolean debug) {
		i2cInterface = new I2C(port, devaddr);
		debuggingEnabled = debug;
	}
	
	public I2CLEDController() {
		this(Port.kOnboard, 0x0B, true); // Assume if no params are supplied, that controller will be addr 0x0B and on 'onboard' I2c port
	}
	
	public boolean isConnected() { // Will return if we have received a good 'ack' from last poll
		return successfulComms;
	}
	
	public boolean setLEDStripLength(short striplength) { // Tells the Controller how long our LED Strip is. Returns T/F if comms appears to be successful
		byte lowByte = (byte)(striplength & 0xFF);
		byte highByte = (byte)((striplength >> 8) & 0xFF);
		byte[] payload = {1, highByte, lowByte, 0}; // Assemble Payload for Controller
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending Target LED Strip length Command..");
		write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
		if(write) return true;
		}
		return false;
	}
	
	public boolean setLEDBrightness(byte stripBrightness) { // Tells the Controller how long our LED Strip is. Returns T/F if comms appears to be successful

		byte[] payload = {2, stripBrightness, 0, 0}; // Assemble Payload for Controller
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending Target LED Brightness Command..");
		write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
		if(write) return true;
		}
		return false;
	}
	
	public boolean setLEDAnimationSpeed(short AnimSpeed) { // Tells the Controller how How fast it should run animations Returns T/F if comms appears to be successful
		byte lowByte = (byte)(AnimSpeed & 0xFF);
		byte highByte = (byte)((AnimSpeed >> 8) & 0xFF);
		byte[] payload = {3, highByte, lowByte, 0}; // Assemble Payload for Controller
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending Target Animation Speed Command..");
		write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
		if(write) return true;
		}
		return false;
	}
	
	public boolean setLEDAnimation(LEDFX stripAnim) { // Tells the Controller What Animation to play. Returns T/F if comms appears to be successful

		byte[] payload = {4, (byte) stripAnim.ordinal(), 0, 0}; // Assemble Payload for Controller
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending Target Animation Command..");
		write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
		if(write) return true;
		}
		return false;
	}
	
	public boolean setLEDAnimationEnabled(boolean stripEnabled) { // Tells the Controller how long our LED Strip is. Returns T/F if comms appears to be successful

		byte[] payload = {5, (byte) (stripEnabled ? 1 : 0), 0, 0}; // Assemble Payload for Controller
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending Animation Enable/Disable Command..");
		write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
		if(write) return true;
		}
		return false;
	}
	
	public boolean setLEDAnimationColors(LEDCOLORS[] colors) { // Tells Controller what kind of Colors we would like to use in animations
		byte[] payload = {6, (byte) colors[0].ordinal(), (byte) colors[1].ordinal(), (byte) colors[2].ordinal()};
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending LED target Animation Colors..");
			write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
			if(write) return true;
		}
		return false;
	}
	
	public boolean preformSoftReset() {
		byte[] payload = {7, 0, 0, 0}; // Assemble Payload for Controller
		boolean write = false;
		debugPrintln("[LED CONTROLLER] Attempting COMMS Check...");
		if(confirmComms()) {
			debugPrintln("[LED CONTROLLER] COMMS Verified, Sending Soft-Reset Command..");
			write = !i2cInterface.transaction(payload, payload.length, null, 0); // Write payload out. Inversed b/c aborted == true
			if(write) return true;
		}
		return false;
	}
	
	private boolean confirmComms() { // used to check if we are connected to the Controller before we try to send commands	
		byte[] ack = {'G', 'G'};
		boolean read = !i2cInterface.readOnly(ack, 2); // Inversed b/c aborted == true
		if(read && (ack == new byte[]{'o', 'k'})) { // On Successful read, And payload is correct
			successfulComms = true;
			return true;
		} else {
			successfulComms = false;
			return false; // Non-successful comms :(
		}
	}
	
	private void debugPrintln(String dtext) {
		if (debuggingEnabled) {
			System.out.println(dtext);
		}
	}
	
}
