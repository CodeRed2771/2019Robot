package frc.robot;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
  Vision
  
  To track a vision target Call setVisionTrackingMode() to entable tracking
  Call "Vision.tick()" in your periodic loop to continually check for a target
  and store the horizontal offset for when you ask for it. This avoids the
  issue where the image happens not to be valid the instant you check it. With
  this code, if it was valid up to a half second earlier, it uses those values.
  Your code should check targetInfoIsValid() before bothering to read the data.
  If the targetInfo is not valid, the data function will return a 0.
  
 */
public class Vision {
	private static Vision instance;
	private static NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
	private static long lastValidReadTime;
	private static double storedOffsetFromTarget = 0;
	private static double storedTargetArea = 0;
	private static double storedTargetSkew = 0;
	private static double storedTargetVertical = 0;
	private static double dis = 0;
	private static int validCount = 0;

	public static Vision getInstance() {
		if (instance == null) {
			try {
				instance = new Vision();
			} catch (Exception ex) {
				System.out.println("Vision module could not be initialized due to the following error: ");
				System.out.println(ex.getMessage());
				System.out.println(ex.getStackTrace());
				return null;
			}
		}
		return instance;
	}

	public Vision() {
		// set the last valid read time to an old time to make it invalid
		lastValidReadTime = System.currentTimeMillis() - 5000; // 5 seconds back in time
	}

	public static void tick() {
		readTargetInfo();

	}

	public static void readTargetInfo() {
		validCount++;
		if (inVisionTrackingMode() && targetCount() > 0 && validCount >= 2) { // why 2?

			lastValidReadTime = System.currentTimeMillis();

			storedOffsetFromTarget = table.getEntry("tx").getDouble(0);
			storedTargetArea = table.getEntry("ta").getDouble(0);
			storedTargetSkew = table.getEntry("ts").getDouble(0);
			storedTargetVertical = table.getEntry("tvert").getDouble(0);
		} else {
			if (targetCount() == 0)
				validCount = 0;
		}
	}

	public static void setLED(boolean turnOn) {
		table.getEntry("ledMode").forceSetNumber(turnOn ? 3 : 1); // 3 - on, 1 = off, 2 - blink
	}
	public static void flashLED(){
		table.getEntry("ledMode").forceSetNumber(2);
	}
	public static boolean inVisionTrackingMode() {
		return (table.getEntry("camMode").getNumber(0).intValue() == 0);
	}

	public static void setDriverMode() {
		setLED(false);
		table.getEntry("camMode").forceSetNumber(1);
	}

	private static void setVisionTrackingMode() {
		table.getEntry("camMode").forceSetNumber(0);
	}

	public static boolean targetInfoIsValid() {
		readTargetInfo();
		return (System.currentTimeMillis() - lastValidReadTime) < 500; // less than 500 ms old
	}

	public static double offsetFromTarget() {
		if (targetInfoIsValid()) {
			return storedOffsetFromTarget;
		} else
			return 0;
	}

	public static double targetVerticalHeight() {
		if (targetInfoIsValid()) {
			return storedTargetVertical;
		} else
			return 0;
	}

	public static double targetArea() {
		if (targetInfoIsValid()) {
			return storedTargetArea;
		} else {
			return 0;
		}
	}

	public static double getDistanceFromTarget() {

		double tvert = targetVerticalHeight();
		SmartDashboard.putNumber("tvert", tvert);

		if (tvert == 0) {
			return 0;
		}
		
		if(tvert >= 53)
			return 24;
		else if(tvert >= 44)
			return 30;
		else if (tvert >=37) 
			return 36;
		else if (tvert >= 36) 
			return 42;
		else if (tvert >= 32) 
			return 48;
		else if (tvert >= 28) 
			return 54;
		else if (tvert >= 25) 
			return 60;
		else if (tvert >= 23)
			return 66;
		else if (tvert >= 22) 
			return 72;
		else if (tvert >= 21)
			return 78;
		else if (tvert >= 20) 
			return 84;
		else if (tvert >= 17)
			return 90;
		else if (tvert >= 16)
			return 94;
		else if (tvert >= 15)
			return 102;
		else if (tvert >= 14) 
			return 108;
		else if (tvert >= 13)
			return 122;
		else if (tvert >= 12)
			return 132;
		else if (tvert >= 11)
			return 138;
		else if (tvert >= 10)
			return 150;
		else
			return 0;
	}

	public static double getTargetSkew() {
		if (targetInfoIsValid()) 
			return storedTargetSkew;
		else
			return 0;
	}

	public static int targetCount() {
		return (int) table.getEntry("tv").getDouble(0);
	}

	public static double tx() {
		if (targetCount() > 0) {
			return (double) table.getEntry("tx").getDouble(0);
		} else {
			return 0;
		}
	}

	public static void setTargetTrackingMode() {
		setLED(true);
		setVisionTrackingMode();
	}

	public static void codeExample() {
		double targetOffsetAngle_Horizontal = table.getEntry("tx").getDouble(0);
		double targetOffsetAngle_Vertical = table.getEntry("ty").getDouble(0);
		double targetArea = table.getEntry("ta").getDouble(0);
		double targetSkew = table.getEntry("ts").getDouble(0);
		
		double targetCount = table.getEntry("tv").getDouble(0);

		SmartDashboard.putNumber("Target Area", targetArea);
		SmartDashboard.putNumber("Horizontal Offset Angle", targetOffsetAngle_Horizontal);
		SmartDashboard.putNumber("Vertical Offset Angle", targetOffsetAngle_Vertical);
		SmartDashboard.putNumber("target Count", targetCount);

		// TURN CODE FOR MAIN PROGRAM
		double turnP = .03;
		double distP = .12;
		double turnSpeed = 0;
		double fwdSpeed = 0;

		fwdSpeed = (5 - targetArea) * distP;
		turnSpeed = targetOffsetAngle_Horizontal * turnP;

		SmartDashboard.putNumber("fwd speed", fwdSpeed);
		SmartDashboard.putNumber("turn speed", turnSpeed);

		if (targetCount > 0) {

			// leftBack.set(fwdSpeed + turnSpeed);
			// leftFront.set(fwdSpeed + turnSpeed);
			// rightBack.set(-fwdSpeed + turnSpeed);
			// rightFront.set(-fwdSpeed + turnSpeed);

		} else {
			// leftBack.set(0);
			// leftFront.set(0);
			// rightBack.set(0);
			// rightFront.set(0);
		}
	}

}
