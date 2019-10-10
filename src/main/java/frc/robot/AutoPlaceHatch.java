package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Manipulator.ManipulatorState;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.AutoDoEverything.LiftHeight;
import frc.robot.libs.CurrentBreaker;

public class AutoPlaceHatch extends AutoBaseClass {

    private double distanceToTarget = 0;
    private double angleDiff;
    private double distToStayBackOnFirstDrive = 20;
    private double targetAngle = 0;
    private boolean drivingAllowed = true;
    private boolean doNothing = true;

    public enum ActionMode {
        JUST_DRIVE, GET_HATCH, GET_CARGO, PLACE_HATCH, PLACE_CARGO;
    }

    public enum LiftHeight {
        LVL_1, LVL_2, LVL_3;
    }

    private static LiftHeight liftHeight = LiftHeight.LVL_1;
    private ActionMode actionMode = ActionMode.JUST_DRIVE;

    public static void setLiftHeight(LiftHeight liftHeightParameter) {
        liftHeight = liftHeightParameter;
    }  

    public void start() {
        super.start();
    
        Vision.setTargetTrackingMode();
    }

    public void stop() {
        super.stop();
        Vision.setDriverMode();
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }

    public void setDrivingAllowed(boolean isDrivingAllowed) {
        drivingAllowed = isDrivingAllowed;
    }
    
	public AutoPlaceHatch() {
		super();
    }

	public void tick() {
		if (isRunning()) {

			DriveAuto.tick();

			SmartDashboard.putNumber("Auto Step", getCurrentStep());
			switch (getCurrentStep()) {
                case 0:
                // keep scanning for a distance reading
                distanceToTarget = Vision.getDistanceFromTarget();
                if (distanceToTarget > 0) {
                    advanceStep();
                }
                break;
            case 2:
                setActionMode(actionMode.PLACE_HATCH);
                advanceStep();
                // DriveAuto.turnDegrees(Vision.offsetFromTarget(), 1); // We commented this out because we thought it might be
                // a problem so we are testing it once we have a robot.
                // setTimerAndAdvanceStep(500); // changed from 1000 4.15.19
                break;
            /*case 2:
                if (DriveAuto.turnCompleted()) {
                    advanceStep();
                }
                break;
                I commented this out because we don't need it anymore since we haven't started driving yet--
                we commented out case 1. I did not have authorization to do this. ~Code Menace
                */

            case 3:
                Manipulator.setLinkageForPlacement();
                targetAngle = TargetInfo.targetAngle(frc.robot.TargetInfo.TargetType.ROCKET_TARGET);
                angleDiff = RobotGyro.getClosestTurn(targetAngle);
                // angleDiff = targetAngle - RobotGyro.getRelativeAngle();
                DriveAuto.turnDegrees(angleDiff, 1); // Square up with target
                setTimerAndAdvanceStep(1000);
                break;
            case 4:
                if (DriveAuto.turnCompleted()) {
                    advanceStep();
                }
                break;
            case 5:
                double slideDistance = -((Math.sin(Math.toRadians(angleDiff)) * distanceToTarget)) + 2;
                SmartDashboard.putNumber("Slide Dist", slideDistance);
                driveInches(slideDistance, 90, 1, false);

             if (actionMode == ActionMode.PLACE_HATCH) {
                    Manipulator.setLinkageForPlacement();
                    Lift.goHatchLvl1();
                    DriveAuto.resetDriveCurrentBreaker();
                }

                setTimerAndAdvanceStep(3000);
                break;
            case 6:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 7:
                // keep scanning for a distance reading
                distanceToTarget = Vision.getDistanceFromTarget();
                if (distanceToTarget > 0) {
                    advanceStep();
                }
                break;
            case 8:
                advanceStep();
                break;
            case 9:
            //We added this - Who is we? - Me
                driveInches(distanceToTarget + 12, 0, 1, true); 
                setTimerAndAdvanceStep(4000);
                break;
            case 10:
                if (DriveAuto.isAgainstWall() || DriveAuto.hasArrived()) {
                    //System.out.println("CURRENT TRIPPED!!!!!"); ~Code Menace
                    advanceStep();
                }
                Lift.goHatchLvl1();
                break;
            }
        }
    }
}