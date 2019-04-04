
package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
	KeyMap gamepad;

	SendableChooser<String> autoChooser;
	SendableChooser<String> positionChooser;

	// /* Auto Stuff */
	String autoSelected;
	AutoBaseClass mAutoProgram;
	// Auto options
	final String cargoTracking = "Cargo Tracking";
	final String autoRotateTest = "Rotate Test";
	final String autoCalibrateDrive = "Auto Calibrate Drive";
	final String autoDrivePIDTune = "Drive PID Tune";
	final String autoTestEncoders = "Test Encoders";
	final String autoTeleop = "TELEOP";
	final String autoDriveOffPlatform = "Auto Platform";
	final String autoHexDrive = "Hex Drive";

	final String testProgram = "Test Program";
	final String targetTracking = "Target Tracking";

	private ShuffleboardTab visionTab = Shuffleboard.getTab("Vision");
	private NetworkTableEntry SB_vision_STR_P = visionTab.add("Vision STR P", Calibration.VISION_STR_P).getEntry();
	private NetworkTableEntry SB_vision_FWD_DIST = visionTab.add("Vision FWD DIST", 48).getEntry();

	// End setup stuff

	@Override
	public void robotInit() {

		gamepad = new KeyMap();

		RobotGyro.getInstance();
		DriveTrain.getInstance();
		DriveAuto.getInstance();
		TargetInfo.getInstance();
		Lift.getInstance();
		Climber.getInstance();
		Manipulator.getInstance();

		mAutoProgram = new AutoDoNothing();

		Calibration.loadSwerveCalibration();

		setupAutoChoices();

		RobotGyro.reset();

		DriveTrain.allowTurnEncoderReset();
		DriveTrain.resetTurnEncoders(); // sets encoders based on absolute encoder positions

		SmartDashboard.putBoolean("Show Encoders", false);
	}

	@Override
	public void teleopInit() {
		mAutoProgram.stop();
		DriveTrain.stopDriveAndTurnMotors();
		Climber.stop();
		Lift.stop();
		Manipulator.stopLinkage();
		Manipulator.stopIntake();

		DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position

		Vision.setDriverMode();
	}

	/*
	 * 
	 * TELEOP PERIODIC
	 * 
	 */

	@Override
	public void teleopPeriodic() {

		// --------------------------------------------------
		// RESST - allow manual reset of systems by pressing Start
		// --------------------------------------------------
		if (gamepad.getZeroGyro()) {
			RobotGyro.reset();
			Lift.resetLift();
			Manipulator.resetLinkage();
			DriveTrain.allowTurnEncoderReset();
			DriveTrain.resetTurnEncoders(); // sets encoders based on absolute encoder positions
			DriveTrain.setAllTurnOrientation(0, false);
		}

		// --------------------------------------------------
		// GAME PIECES
		// --------------------------------------------------
		if (gamepad.activateCargoIntake()) {
			Manipulator.intakeCargo();
		}

		if (gamepad.activateCargoFeedIntake()) {
			Manipulator.intakeCargoFeeder();
		}

		if (gamepad.turnIntakeOff()) {
			Manipulator.stopIntake();
		}

		if (gamepad.activateHatchIntake()) {
			Manipulator.intakeHatch();
		}
		if (gamepad.ejectGamePiece()) {
			Manipulator.ejectGamePiece();
		}
		if (gamepad.gamePieceOverride()) {
			Manipulator.holdGamePieceOverride();
		}

		if (gamepad.linkageUp()) {
			Manipulator.linkageUp();
			Lift.goToStart();
		}
		// --------------------------------------------------
		// LIFT
		// --------------------------------------------------

		if (Math.abs(gamepad.getManualLift()) > .1) {
			Lift.moveSetpoint(0.75 * powerOf2PreserveSign(-gamepad.getManualLift()));
		}
		if (gamepad.goToTravelPosition()) {
			Lift.goToStart();
			Manipulator.fingerUp();
		}
		if (gamepad.goToLvl1()) {
			Manipulator.setLinkageForPlacement();
			if (Manipulator.isHoldingCargo())
				Lift.goCargoLvl1();
			else
				Lift.goHatchLvl1();
		}
		if (gamepad.goToLvl2()) {
			Manipulator.setLinkageForPlacement();
			if (Manipulator.isHoldingCargo())
				Lift.goCargoLvl2();
			else
				Lift.goHatchLvl2();
		}
		if (gamepad.goToLvl3()) {
			Manipulator.setLinkageForPlacement();
			if (Manipulator.isHoldingCargo())
				Lift.goCargoLvl3();
			else
				Lift.goHatchLvl3();
		}

		if (gamepad.getCargoShipPlacement()) {
			Manipulator.setLinkageForPlacement();
			Lift.goCargoShipCargo();
		}

		if (gamepad.getFingerUp()) {
			Manipulator.moveFingerUp();
		}

		if (gamepad.getHatchOverride()) {
			Manipulator.intakeHatchOverride();
		}

		// --------------------------------------------------
		// AUTO SUB ROUTINES
		// --------------------------------------------------

		// AUTO GET HATCH
		// if (gamepad.activateHatchIntakeAuto() && !mAutoProgram.isRunning()) {
		// 	mAutoProgram = new AutoGrabHatchFromFeeder();
		// 	mAutoProgram.start();
		// }

		// AUTO CLIMB
		if (gamepad.getClimb()) {
			mAutoProgram.stop();
			mAutoProgram = new AutoClimb();
			mAutoProgram.start();
		}

		if (gamepad.getUndoClimb()) {
			mAutoProgram.stop();
			Climber.climberRetractFull();
		}

		// SLIDE LEFT
		if (gamepad.shipMoveLeft() && !mAutoProgram.isRunning()) {
			mAutoProgram = new AutoSlideOver();
			mAutoProgram.start(AutoBaseClass.Direction.LEFT);
		}

		// SLIDE RIGHT
		if (gamepad.shipMoveRight() && !mAutoProgram.isRunning()) {
			mAutoProgram = new AutoSlideOver();
			mAutoProgram.start(AutoBaseClass.Direction.RIGHT);
		}

		// FIND HATCH TARGET
		if (gamepad.findRocketTarget() && !mAutoProgram.isRunning()) {
			mAutoProgram = new AutoFindHatch();
			((AutoFindHatch) mAutoProgram).setDrivingAllowed(false);
			mAutoProgram.start(TargetInfo.TargetType.ROCKET_TARGET);
		}
		if (gamepad.findFeedStation() && !mAutoProgram.isRunning()) {
			mAutoProgram = new AutoFindHatch();
			((AutoFindHatch) mAutoProgram).setDrivingAllowed(false);
			mAutoProgram.start(TargetInfo.TargetType.FEEDER_TARGET);
		}
		if (gamepad.findShipTarget() && !mAutoProgram.isRunning()) {
			mAutoProgram = new AutoFindHatch();
			((AutoFindHatch) mAutoProgram).setDrivingAllowed(false);
			mAutoProgram.start(TargetInfo.TargetType.SHIP_TARGET);
		}
		if (mAutoProgram.isRunning() && (Math.abs(gamepad.getSwerveYAxis()) > .1
				|| Math.abs(gamepad.getSwerveXAxis()) > .1 || Math.abs(gamepad.getSwerveRotAxis()) > .1)) {
			mAutoProgram.stop();
		}

		SmartDashboard.putBoolean("Auto Mode", mAutoProgram.isRunning());

		// DRIVE
		if (mAutoProgram.isRunning()) {
			mAutoProgram.tick();
		} else {
			//
			// DRIVER CONTROL MODE
			// Issue the drive command using the parameters from
			// above that have been tweaked as needed
			double driveFWDAmount = gamepad.getSwerveYAxis();
			double driveStrafeAmount = -gamepad.getSwerveXAxis();
			boolean normalDrive = Lift.liftIsDown() && !gamepad.getDriveModifier();
			if (Math.abs(driveFWDAmount) <= .2 || !normalDrive) // strafe adjust if not driving forward
				driveStrafeAmount = strafeAdjust(driveStrafeAmount, normalDrive);
			else
				driveStrafeAmount = driveStrafeAmount * .75;

			double driveRotAmount = rotationalAdjust(gamepad.getSwerveRotAxis());

			driveFWDAmount = forwardAdjust(driveFWDAmount, normalDrive);

			if (gamepad.getRobotCentricModifier())
				DriveTrain.humanDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
			else
				DriveTrain.fieldCentricDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);

			if (isTippingOver()) {
				System.out.print("ANTI-TIP CODE ACTIVATED - NO ACTION TAKEN THOUGH");
				// Lift.goToStart(); // if we start tipping, bring the lift down
			}
		}

		// --------------------------------------------------
		// TICK
		// --------------------------------------------------

		Lift.tick();
		Climber.tick();
		Manipulator.tick();
		Vision.tick();

		showDashboardInfo();

	}

	@Override
	public void autonomousInit() {

		mAutoProgram.stop();
		Climber.stop();
		Lift.stop();
		Manipulator.stopIntake();
		Vision.setDriverMode();
		DriveTrain.stopDriveAndTurnMotors();
		DriveTrain.setAllTurnOrientation(0, false);

		Lift.resetLift();
		Manipulator.resetLinkage();

		String selectedPos = positionChooser.getSelected();
		SmartDashboard.putString("Position Chooser Selected", selectedPos);
		char robotPosition = selectedPos.toCharArray()[0];

		System.out.println("Robot position: " + robotPosition);

		autoSelected = (String) autoChooser.getSelected();
		SmartDashboard.putString("Auto Selected: ", autoSelected);

		mAutoProgram = new AutoDoNothing();

		switch (autoSelected) {
		case autoTeleop:
			// don't do anything
			break;
		case autoDriveOffPlatform:
			mAutoProgram = new AutoDriveOffPlatform();
			break;
		case autoDrivePIDTune:
			SmartDashboard.putNumber("Drive To Setpoint", 0);
			SmartDashboard.putNumber("Drive Strafe Angle", 0);
			mAutoProgram = new AutoDrivePIDTune();
			break;
		case autoRotateTest:
			mAutoProgram = new AutoRotateTest();
			break;
		case autoTestEncoders:
			mAutoProgram = new AutoTestEncoders();
			break;
		case autoHexDrive:
			mAutoProgram = new AutoDriveHexagon();
			break;
		}

		DriveAuto.reset();

		if (autoSelected == autoTeleop) {
			System.out.println("AUTO TELEOP SELECTED");
		} else if (mAutoProgram != null) {
			System.out.print("auto program started successfully " + autoSelected);
			mAutoProgram.start();
		} else
			System.out.println("No auto program started in switch statement");
	}

	@Override
	public void autonomousPeriodic() {

		if (mAutoProgram != null) {
			mAutoProgram.tick();
		}

		if (autoSelected == autoTeleop) {
			teleopPeriodic();
		} else {
			DriveAuto.tick();

			DriveAuto.showEncoderValues();
			showDashboardInfo();
		}
	}

	private void showDashboardInfo() {
		// SmartDashboard.putNumber("Distance", Vision.getDistanceFromTarget());
		// visionTab.add("Has Target", Vision.targetInfoIsValid());
		// SmartDashboard.putNumber("Vision offset", Vision.offsetFromTarget());
		// SmartDashboard.putNumber("Vision Dist", Vision.getDistanceFromTarget());
		// SmartDashboard.putNumber("Vision Skew", Vision.getTargetSkew());
		// SmartDashboard.putNumber("line sensor", line.getAverageValue());

		// SmartDashboard.putNumber("Match Time",
		// DriverStation.getInstance().getMatchTime());

		SmartDashboard.putNumber("Gyro Relative", round2(RobotGyro.getRelativeAngle()));
		SmartDashboard.putNumber("Gyro Raw", round2(RobotGyro.getAngle()));

		if (SmartDashboard.getBoolean("Show Encoders", false)) {
			DriveTrain.showTurnEncodersOnDash();
			DriveTrain.showDriveEncodersOnDash();
		}

	}

	private double rotationalAdjust(double rotateAmt) {
		// put some rotational power restrictions in place to make it
		// more controlled
		double adjustedAmt = 0;

		if (Math.abs(rotateAmt) < .1) {
			adjustedAmt = 0;
		} else {
			if (Math.abs(rotateAmt) < .4) {
				adjustedAmt = .15 * Math.signum(rotateAmt);
			} else {
				if (Math.abs(rotateAmt) < .6) {
					adjustedAmt = .25 * Math.signum(rotateAmt);
				} else {
					if (Math.abs(rotateAmt) < .95) {
						adjustedAmt = .6 * Math.signum(rotateAmt);
					} else {
						adjustedAmt = rotateAmt;
					}
				}
			}
		}
		return adjustedAmt;
	}

	private double forwardAdjust(double fwd, boolean normalDrive) {
		if (normalDrive) {
			return fwd;
		} else {
			return fwd * .40;
		}
	}

	private double strafeAdjust(double strafeAmt, boolean normalDrive) {
		// put some power restrictions in place to make it
		// more controlled
		double adjustedAmt = 0;

		if (Math.abs(strafeAmt) < .1) {
			adjustedAmt = 0;
		} else {
			if (normalDrive) { // do normal adjustments
				if (Math.abs(strafeAmt) < .7) {
					adjustedAmt = .5 * strafeAmt; // .2 * Math.signum(strafeAmt);
				} else {
					if (Math.abs(strafeAmt) < .98) {
						adjustedAmt = .75 * strafeAmt; // .4 * Math.signum(strafeAmt);
					} else {
						adjustedAmt = strafeAmt;
					}
				}
			} else { // lift is up, so do more drastic adjustments
				adjustedAmt = strafeAmt * .40;
			}
		}
		return adjustedAmt;
	}

	@Override
	public void testInit() {
	}

	@Override
	public void testPeriodic() {
	}

	public void disabledInit() {
		DriveTrain.allowTurnEncoderReset(); // allows the turn encoders to be
											// reset once during disabled
											// periodic
		DriveTrain.resetDriveEncoders();

		DriveTrain.disablePID();
	}

	public void disabledPeriodic() {
		DriveTrain.resetTurnEncoders(); // happens only once because a flag
										// prevents multiple calls
		showDashboardInfo();
	}

	private void setupAutoChoices() {
		// Position Chooser
		positionChooser = new SendableChooser<String>();
		positionChooser.addOption("Left", "L");
		positionChooser.setDefaultOption("Center", "C");
		positionChooser.addOption("Right", "R");
		SmartDashboard.putData("Position", positionChooser);

		/* Auto Chooser */
		autoChooser = new SendableChooser<>();
		autoChooser.addOption(targetTracking, targetTracking);
		autoChooser.addOption(autoRotateTest, autoRotateTest);
		autoChooser.addOption(autoCalibrateDrive, autoCalibrateDrive);
		autoChooser.addOption(autoDrivePIDTune, autoDrivePIDTune);
		autoChooser.addOption(autoTestEncoders, autoTestEncoders);
		autoChooser.addOption(autoDriveOffPlatform, autoDriveOffPlatform);
		autoChooser.setDefaultOption(autoTeleop, autoTeleop);
		autoChooser.addOption(autoHexDrive, autoHexDrive);

		// Put options to smart dashboard
		SmartDashboard.putData("Auto choices", autoChooser);
	}

	private boolean isTippingOver() {
		return Math.abs(RobotGyro.getGyro().getPitch()) > 15 || Math.abs(RobotGyro.getGyro().getRoll()) > 15;
	}

	private double powerOf2PreserveSign(double v) {
		return (v > 0) ? Math.pow(v, 2) : -Math.pow(v, 2);
	}

	private static Double round2(Double val) {
		// added this back in on 1/15/18
		return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private static Double round0(Double val) {
		// added this back in on 1/15/18
		return new BigDecimal(val.toString()).setScale(0, RoundingMode.HALF_UP).doubleValue();
	}

}
