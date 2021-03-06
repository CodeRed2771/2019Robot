package frc.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CalibrationCHOOSER {

	static SendableChooser<String> robotChooser = setupRobotChoices();

	private static SendableChooser<String> setupRobotChoices() {
		//Robot Chooser
		SendableChooser<String> robotChooser = new SendableChooser<String>();
		robotChooser.addOption("Test Chassis", "T");
		robotChooser.addOption("Practice Bot", "P");
		robotChooser.setDefaultOption("Competition Bot", "C");
		SmartDashboard.putData("Calibration", robotChooser);
		return robotChooser;
	}
	/*
	 * Drive Train
	 */
	
	 // TEST/OLD BOT

	private final static double DT_A_ABS_ZERO_INITIAL_TEST = .522; //Practice Robot Calibration
	private final static double DT_B_ABS_ZERO_INITIAL_TEST = .904; //Practice Robot Calibration
	private final static double DT_C_ABS_ZERO_INITIAL_TEST = .793; //Practice Robot Calibration
	private final static double DT_D_ABS_ZERO_INITIAL_TEST = .106; //Practice Robot Calibration

	// PRACTICE
	
	private final static double DT_A_ABS_ZERO_INITIAL_PRACTICE = .259; //Practice Robot Calibration
	private final static double DT_B_ABS_ZERO_INITIAL_PRACTICE = .056; //Practice Robot Calibration
	private final static double DT_C_ABS_ZERO_INITIAL_PRACTICE = .658; //Practice Robot Calibration
	private final static double DT_D_ABS_ZERO_INITIAL_PRACTICE = .742; //Practice Robot Calibration
	
	public static final double ARM_ABS_ZERO = 0.454;  // Absolute encoder value in horizontal position
	
	// COMPETIION
	private final static double DT_A_ABS_ZERO_INITIAL_COMP = .8212; // COMPEITION
	private final static double DT_B_ABS_ZERO_INITIAL_COMP = .2661;
	private final static double DT_C_ABS_ZERO_INITIAL_COMP = .8466;
	private final static double DT_D_ABS_ZERO_INITIAL_COMP = .7338;
	// public static final double ARM_ABS_ZERO = 0.18;

	public static double GET_DT_A_ABS_ZERO_INITIAL() {
		if (robotChooser.getSelected().equals("T")) {
			return DT_A_ABS_ZERO_INITIAL_TEST;
		} else if (robotChooser.getSelected().equals("P")) {
			return DT_A_ABS_ZERO_INITIAL_PRACTICE;
		} else {
			return DT_A_ABS_ZERO_INITIAL_COMP;
		}
	}

	public static double GET_DT_B_ABS_ZERO_INITIAL() {
		if (robotChooser.getSelected().equals("T")) {
			return DT_B_ABS_ZERO_INITIAL_TEST;
		} else if (robotChooser.getSelected().equals("P")) {
			return DT_B_ABS_ZERO_INITIAL_PRACTICE;
		} else {
			return DT_B_ABS_ZERO_INITIAL_COMP;
		}
	}

	public static double GET_DT_C_ABS_ZERO_INITIAL() {
		if (robotChooser.getSelected().equals("T")) {
			return DT_C_ABS_ZERO_INITIAL_TEST;
		} else if (robotChooser.getSelected().equals("P")) {
			return DT_C_ABS_ZERO_INITIAL_PRACTICE;
		} else {
			return DT_C_ABS_ZERO_INITIAL_COMP;
		}
	}

	public static double GET_DT_D_ABS_ZERO_INITIAL() {
		if (robotChooser.getSelected().equals("T")) {
			return DT_D_ABS_ZERO_INITIAL_TEST;
		} else if (robotChooser.getSelected().equals("P")) {
			return DT_D_ABS_ZERO_INITIAL_PRACTICE;
		} else {
			return DT_D_ABS_ZERO_INITIAL_COMP;
		}
	}

	public final static double VISION_FWD_P = 0.05;
	public final static double VISION_FWD_I = 0;
	public final static double VISION_FWD_D = 0.15;

	public final static double VISION_STR_P = 0.15;
	public final static double VISION_STR_I = 0;
	public final static double VISION_STR_D = 0.2;
	
	public final static double VISION_ROT_P = 0.03;
	public final static double VISION_ROT_I = 0;
	public final static double VISION_ROT_D = 0;
	
	public final static double TURN_P = 13; //was 10 3.10.19
	public final static double TURN_I = 0.01;
	public final static double TURN_D = 400;
	
	//Physical Module - A
	public final static int DT_A_DRIVE_TALON_ID = 6;
	public final static int DT_A_TURN_TALON_ID = 5;
	private static double DT_A_ABS_ZERO = GET_DT_A_ABS_ZERO_INITIAL();
	public static double GET_DT_A_ABS_ZERO() { return DT_A_ABS_ZERO; }
	
	// Physical Module - B
	public final static int DT_B_DRIVE_TALON_ID = 3;
	public final static int DT_B_TURN_TALON_ID = 4;
	private static double DT_B_ABS_ZERO = GET_DT_B_ABS_ZERO_INITIAL();
	public static double GET_DT_B_ABS_ZERO() { return DT_B_ABS_ZERO; }
	
	// Physical Module - C
	public final static int DT_C_DRIVE_TALON_ID = 7;
	public final static int DT_C_TURN_TALON_ID = 8;
	private static double DT_C_ABS_ZERO = GET_DT_C_ABS_ZERO_INITIAL();
	public static double GET_DT_C_ABS_ZERO() { return DT_C_ABS_ZERO; }
	
	// Physical Module - D
	public final static int DT_D_DRIVE_TALON_ID = 2;
	public final static int DT_D_TURN_TALON_ID = 1;
	private static double DT_D_ABS_ZERO = GET_DT_D_ABS_ZERO_INITIAL();
	public static double GET_DT_D_ABS_ZERO() { return DT_D_ABS_ZERO; }
	
	//Rot PID - this is for turning the robot, not turning a module
	public final static double DT_ROT_PID_P = .007; 
	public final static double DT_ROT_PID_I =.0004;
	public final static double DT_ROT_PID_D= .000;
	public final static double DT_ROT_PID_IZONE = 18;

	public final static int DT_MM_ACCEL = 300;
	public final static int DT_MM_VELOCITY = 350;
	
	public static final double DRIVE_DISTANCE_TICKS_PER_INCH = 32.900; //2624 ticks in 80 inches, goes 2,427.2
	
	public static final double AUTO_ROT_P = 0.08; // increased from .03 on 2/9/2019 by CS
	public static final double AUTO_ROT_I = 0.001;
	public static final double AUTO_ROT_D = 0.1;  // was 067
	public static final double AUTO_ROT_F = 0.0;

	public static final double AUTO_DRIVE_P = 20;  // was .5
	public static final double AUTO_DRIVE_I = 0.1;
	public static final double AUTO_DRIVE_D = 200.0;  // was 0
	public static final int AUTO_DRIVE_IZONE = 50;
	
	public static final double INTAKE_MAX_CURRENT = 17;
	
	public static final double LIFT_P = 0.6;
	public static final double LIFT_I = 0.0;
	public static final double LIFT_D = 0.0;
	public static final double LIFT_F = 1.0;
	public static final int LIFT_ACCEL = 1500;
	public static final int LIFT_VELOCITY = 3000;

	public static final double LINKAGE_P = 3.2;
	public static final double LINKAGE_I = 0.0;
	public static final double LINKAGE_D = 0.0;
	public static final double LINKAGE_F = 1.0;
	public static final int LINKAGE_ACCEL = 500;
	public static final int LINKAGE_VELOCITY = 1000;

	public static void loadSwerveCalibration() {
		File calibrationFile = new File("/home/lvuser/swerve.calibration");
		if (calibrationFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(calibrationFile));
				DT_A_ABS_ZERO = Double.parseDouble(reader.readLine());
				DT_B_ABS_ZERO = Double.parseDouble(reader.readLine());
				DT_C_ABS_ZERO = Double.parseDouble(reader.readLine());
				DT_D_ABS_ZERO = Double.parseDouble(reader.readLine());
				reader.close();
				SmartDashboard.putBoolean("Using File-based Swerve Calibration", true);
				return;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		SmartDashboard.putBoolean("Using File-based Swerve Calibration", false);
	}
	
	public static void saveSwerveCalibration(double dt_a, double dt_b, double dt_c, double dt_d) {
		File calibrationFile = new File("/home/lvuser/swerve.calibration");
		try {
			if (calibrationFile.exists()) {
				calibrationFile.delete();
			}
			calibrationFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(calibrationFile));
			writer.write(String.valueOf(dt_a) + "\n");
			writer.write(String.valueOf(dt_b) + "\n");
			writer.write(String.valueOf(dt_c) + "\n");
			writer.write(String.valueOf(dt_d) + "\n");
			writer.flush();
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		System.out.println(calibrationFile.getAbsolutePath());
		
		DT_A_ABS_ZERO = dt_a;
		DT_B_ABS_ZERO = dt_b;
		DT_C_ABS_ZERO = dt_c;
		DT_D_ABS_ZERO = dt_d;
	}
	
	public static void resetSwerveDriveCalibration() {
		DT_A_ABS_ZERO = GET_DT_A_ABS_ZERO_INITIAL();
		DT_B_ABS_ZERO = GET_DT_B_ABS_ZERO_INITIAL();
		DT_C_ABS_ZERO = GET_DT_C_ABS_ZERO_INITIAL();
		DT_D_ABS_ZERO = GET_DT_D_ABS_ZERO_INITIAL();
		
		File calibrationFile = new File("/home/lvuser/swerve.calibration");
		calibrationFile.delete();
	}
}
