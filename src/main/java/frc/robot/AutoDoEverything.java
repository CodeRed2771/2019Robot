package frc.robot;


// import org.graalvm.compiler.graph.Position;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Manipulator.ManipulatorState;

public class AutoDoEverything extends AutoBaseClass {

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

    int numberOfHatchesUsed = 0;

    public void tick() {

        if (isRunning()) {

            DriveAuto.tick();
            SmartDashboard.putNumber("Hatch Step", getCurrentStep());

            switch (getCurrentStep()) {
            case 0:
                // keep scanning for a distance reading
                distanceToTarget = Vision.getDistanceFromTarget();
                if (distanceToTarget > 0) {
                    setStep(3);
                }
                break;
            case 2:
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

                if (actionMode == ActionMode.JUST_DRIVE) {
                    
                } else if (actionMode == ActionMode.GET_HATCH) {
                    Manipulator.intakeHatch();
                     Manipulator.resetIntakeStallDetector();
                     Lift.getHatchPanel();
                     DriveAuto.resetDriveCurrentBreaker();
                } else if (actionMode == ActionMode.GET_CARGO) {
                    Manipulator.intakeCargoFeeder();
                    Lift.getCargoOffFeeder();
                    DriveAuto.resetDriveCurrentBreaker();
                } else if (actionMode == ActionMode.PLACE_HATCH) {
                    Manipulator.setLinkageForPlacement();
                    Lift.goHeight(liftHeight);
                    DriveAuto.resetDriveCurrentBreaker();
                } else if (actionMode == ActionMode.PLACE_CARGO) {
                    Manipulator.setLinkageForPlacement();
                    Lift.goHeight(liftHeight);
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
                Lift.goHeight(liftHeight);
                break;
            case 11:
                setActionMode(actionMode.JUST_DRIVE);
                setStep(39);
                break;
            case 39:
                if (actionMode == ActionMode.JUST_DRIVE) {
                    setStep(500);
                } else if (actionMode == ActionMode.GET_HATCH) {
                    setStep(110);
                } else if (actionMode == ActionMode.GET_CARGO) {
                    setStep(40);
                } else if (actionMode == ActionMode.PLACE_HATCH) {
                    setStep(60);
                } else if (actionMode == ActionMode.PLACE_CARGO) {
                    setStep(90);
                } else {
                    setStep(500);
                }
                break;

            /////////////////////////////////////////////////////////////////////////
            // GET CARGO
            /////////////////////////////////////////////////////////////////////////
            case 40:
                driveInches(30, 0, .2);
                setTimerAndAdvanceStep(3000);
                break;
            case 41:
                if (Manipulator.intakeStalled()) {
                    advanceStep();
                }
                break;
            case 42:
                DriveAuto.stopDriving();
                driveInches(-2, 0, .3);
                break;
            case 43:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 44:
                Manipulator.intakeCargoFeeder();
                setTimerAndAdvanceStep(5000);
                break;

            case 45:
                if (Manipulator.intakeStalled()) {
                    Manipulator.resetIntakeStallDetector();
                    setStep(46);
                } else {
                    setStep(500);
                }
                break;
            case 46:
                DriveAuto.driveInches(-24, 0, .5);
                setTimerAndAdvanceStep(2000);
                break;
            case 47:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 48:
                DriveAuto.stopDriving();
                setStep(500);
                break;

            /////////////////////////////////////////////////////////////////////////
            // PLACE HATCH
            /////////////////////////////////////////////////////////////////////////
            case 60:
                Manipulator.moveFingerDown();
                setTimer(750);
            case 61:
                driveInches(-12, 0, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 62:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 63:
                turnToHeading(180, 1);
                break;
            case 64:
                if (turnCompleted()) {
                    advanceStep();
                }
                break;
            case 65:
                setStep(0);
                break;

            /////////////////////////////////////////////////////////////////////////
            // PLACE CARGO
            /////////////////////////////////////////////////////////////////////////
            
            //Need to make sure Lift is at the right height
            case 90: 
                Lift.goHeight(liftHeight);
                driveInches(30, 0, .3); 
                setTimerAndAdvanceStep(2000);
                break;
            case 91:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 92:
                Manipulator.ejectGamePiece();
                setTimerAndAdvanceStep(1000);
                break;
            case 93:
                break;
            case 94:
                driveInches(-12, 0, .25);
                setTimerAndAdvanceStep(2000);
                break;
            case 95:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 96:
                DriveAuto.stopDriving();
                setStep(500);
                break;

            /////////////////////////////////////////////////////////////////////////
            // Get HATCH
            /////////////////////////////////////////////////////////////////////////
            case 110:
                DriveAuto.stopDriving();
                Manipulator.holdGamePieceOverride();
                advanceStep();
                break;
            case 111:
                setTimerAndAdvanceStep(200);
                break;
            case 112:
                DriveAuto.driveInches(-30, 0, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 113:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 114:
                DriveAuto.stopDriving();
                setStep(500);
                break;

            /////////////////////////////////////////////////////////////////////////
            // THE END
            /////////////////////////////////////////////////////////////////////////
            case 500:
                stop();
                break;
            } // end of switch statement
        } //end of isRunning

        SmartDashboard.putBoolean("Hatch running", isRunning());
        SmartDashboard.putNumber("tx", Vision.tx());

    } //end of tick statement
} //end of extends statement
