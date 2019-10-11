package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoEpicStart extends AutoBaseClass {
    
    public AutoEpicStart() {
		super();
    }
    
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

    AutoBaseClass mSubAutoProg = new AutoDoNothing();

    public void tick (){
        if (isRunning()) {

            DriveAuto.tick();

            switch(getCurrentStep()){

                    case 0:
                        if (robotPosition () == Position.LEFT) {
                            driveInches(90, 300, 1);
                        } else if (robotPosition () == Position.CENTER) {
                            driveInches(40, 0, 1);
                        } else if (robotPosition() == Position.RIGHT) {
                            driveInches(90, 60, 1);
                        }
                        setTimerAndAdvanceStep(4000); //I changed this from 2000 to 4000 --Code Menace
                        break;

                    case 1:
                        if (driveCompleted()){
                            advanceStep();
                        }
                        break;
                    case 2:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            setStep(4); //changed from 3 to 4 because there's nothing in 3 --Code Menace
                        }
                        break;
                    case 3:
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
        
                    case 4:
                        Manipulator.setLinkageForPlacement();
                        targetAngle = TargetInfo.targetAngle(frc.robot.TargetInfo.TargetType.ROCKET_TARGET);
                        angleDiff = RobotGyro.getClosestTurn(targetAngle);
                        // angleDiff = targetAngle - RobotGyro.getRelativeAngle();
                        DriveAuto.turnDegrees(angleDiff, 1); // Square up with target
                        setTimerAndAdvanceStep(1000);
                        break;
                    case 5:
                        if (DriveAuto.turnCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 6:
                        double slideDistance = -((Math.sin(Math.toRadians(angleDiff)) * distanceToTarget)) + 2;
                        SmartDashboard.putNumber("Slide Dist", slideDistance);
                        driveInches(slideDistance, 90, 1, false);
                        setTimerAndAdvanceStep(3000);
                        break;
                    case 7:
                        if (DriveAuto.hasArrived()) {
                            advanceStep();
                        }
                        break;
                    case 8:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            advanceStep();
                        }
                        break;
                    case 9:
                        if (numberOfHatchesUsed == 2) {
                            Lift.goHatchLvl2();
                            advanceStep();
                        } else {
                            advanceStep();
                        }
                        break;
                    case 10:
                        //We added this - Who is we? - Me
                        driveInches(distanceToTarget + 20, 0, 1, true); //changed from 12 to 20 because the robot wasn't making it --CM
                        setTimerAndAdvanceStep(4000);
                        break;
                    case 11:
                        if (DriveAuto.isAgainstWall() || DriveAuto.hasArrived()) {
                            //System.out.println("CURRENT TRIPPED!!!!!"); ~Code Menace
                            setStep(13);
                        }
                        break;
                   /* case 12:
                        setStep();
                        break; */
                    case 13:
                        Manipulator.moveFingerDown();
                        advanceStep();
                        break;
                    case 14:
                        if (robotPosition() == Position.LEFT) {
                            driveInches(12, 135, 1);
                            System.out.println("trying to get out of case 13");
                        } else if (robotPosition() == Position.CENTER) {
                            driveInches(12, 180, 1);
                        } else if (robotPosition() == Position.RIGHT) {
                            driveInches(12, 225, 1);
                        }
                        setTimerAndAdvanceStep(2000);
                        break;
                    case 15:
                        if (driveCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 16:
                        if (robotPosition() == Position.LEFT) {
                            turnDegrees(-150, 1);
                        } else if (robotPosition() == Position.CENTER) {
                            turnDegrees(180, 1);
                        } else if (robotPosition() == Position.RIGHT) {
                            turnDegrees(150, 1);
                        }
                        setTimerAndAdvanceStep(2500);
                        break;
                    case 17:
                        if (turnCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 18:
                        if (robotPosition() == Position.LEFT || robotPosition() == Position.RIGHT) {
                            driveInches(100, 180, 1);
                        } else if (robotPosition() == Position.CENTER) {
                            driveInches(100, 240, 1);
                        } 
                        setTimerAndAdvanceStep(4000);
                        break;
                    case 19: 
                        if (driveCompleted()) {
                            setStep(21);
                        }
                    //case 20:
                       // advanceStep();
                      //  break;
                    case 21:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            setStep(23);
                        }
                        break;
                    case 22:
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
        
                    case 23:
                        Manipulator.setLinkageForPlacement();
                        targetAngle = TargetInfo.targetAngle(frc.robot.TargetInfo.TargetType.FEEDER_TARGET);
                        angleDiff = RobotGyro.getClosestTurn(targetAngle);
                        // angleDiff = targetAngle - RobotGyro.getRelativeAngle();
                        DriveAuto.turnDegrees(angleDiff, 1); // Square up with target
                        setTimerAndAdvanceStep(1000);
                        break;
                    case 24:
                        if (DriveAuto.turnCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 25:
                        slideDistance = -((Math.sin(Math.toRadians(angleDiff)) * distanceToTarget)) + 2;
                        SmartDashboard.putNumber("Slide Dist", slideDistance);
                        driveInches(slideDistance, 90, 1, false);
                        setTimerAndAdvanceStep(3000);
                        break;
                    case 26:
                        if (DriveAuto.hasArrived()) {
                            advanceStep();
                        }
                        break;
                    case 27:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            advanceStep();
                        }
                        break;
                    case 28:
                        if (numberOfHatchesUsed == 2) {
                            Lift.goHatchLvl2();
                            advanceStep();
                        } else {
                            advanceStep();
                        }
                        break;
                    case 29:
                        //We added this - Who is we? - Me
                        driveInches(distanceToTarget + 12, 0, 1, true); 
                        setTimerAndAdvanceStep(4000);
                        break;
                    case 30:
                        if (DriveAuto.isAgainstWall() || DriveAuto.hasArrived()) {
                            //System.out.println("CURRENT TRIPPED!!!!!"); ~Code Menace
                            advanceStep();
                        }
                        break;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    case 31:
                        Manipulator.fingerUp();
                        advanceStep();
                        break;
                    case 32:
                        driveInches(12, 0, 1);
                        setTimerAndAdvanceStep(2000);
                        break;
                    case 33:
                        if (driveCompleted()) {
                            setActionMode(ActionMode.PLACE_HATCH);
                            advanceStep();
                        }
                        break;
                    case 34:
                        turnDegrees(180, 1);
                        setTimerAndAdvanceStep(2000);
                        break;
                    case 35:
                        if (turnCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 36: 
                        driveInches(100, 0, 1);
                        setTimerAndAdvanceStep(4000);
                        break;
                    case 37:
                        if (driveCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 38:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            setStep(40);
                        }
                        break;
                    case 39:
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
        
                    case 40:
                        Manipulator.setLinkageForPlacement();
                        targetAngle = TargetInfo.targetAngle(frc.robot.TargetInfo.TargetType.ROCKET_TARGET);
                        angleDiff = RobotGyro.getClosestTurn(targetAngle);
                        // angleDiff = targetAngle - RobotGyro.getRelativeAngle();
                        DriveAuto.turnDegrees(angleDiff, 1); // Square up with target
                        setTimerAndAdvanceStep(1000);
                        break;
                    case 41:
                        if (DriveAuto.turnCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 42:
                        slideDistance = -((Math.sin(Math.toRadians(angleDiff)) * distanceToTarget)) + 2;
                        SmartDashboard.putNumber("Slide Dist", slideDistance);
                        driveInches(slideDistance, 90, 1, false);
                        setTimerAndAdvanceStep(3000);
                        break;
                    case 43:
                        if (DriveAuto.hasArrived()) {
                            advanceStep();
                        }
                        break;
                    case 44:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            advanceStep();
                        }
                        break;
                    case 45:
                        Lift.goHatchLvl2();
                        advanceStep();
                        break;
                    case 46:
                        //We added this - Who is we? - Me
                        driveInches(distanceToTarget + 12, 0, 1, true); 
                        setTimerAndAdvanceStep(4000);
                        break;
                    case 47:
                        if (DriveAuto.isAgainstWall() || DriveAuto.hasArrived()) {
                            //System.out.println("CURRENT TRIPPED!!!!!"); ~Code Menace
                            advanceStep();
                        }
                        break;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    case 48:
                        Manipulator.moveFingerDown();
                        advanceStep();
                        break;
                    case 49:
                        if (robotPosition() == Position.LEFT || robotPosition() == Position.CENTER) {
                            driveInches(12, 150, 1);
                        } else if (robotPosition() == Position.RIGHT) {
                            driveInches(12, 240, 1);
                        }
                        setTimerAndAdvanceStep(2000);
                        break;
                    case 50:
                        if (driveCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 51:
                        //stop();
                        break;
                    case 52:
                        Lift.goHatchLvl1();
                        break;
                    case 53:
                        DriveAuto.turnDegrees(140, 1);
                        break;
                    case 54:
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0){
                            advanceStep();
                        }
                        else 
                        stop();
                        break;
                    case 55:
                        Manipulator.setLinkageForPlacement();
                        targetAngle = TargetInfo.targetAngle(frc.robot.TargetInfo.TargetType.FEEDER_TARGET);
                        angleDiff = RobotGyro.getClosestTurn(targetAngle);
                        // angleDiff = targetAngle - RobotGyro.getRelativeAngle();
                        DriveAuto.turnDegrees(angleDiff, 1); // Square up with target
                        setTimerAndAdvanceStep(1000);
                        break;
                    case 56:
                        if (DriveAuto.turnCompleted()) {
                            advanceStep();
                        }
                        break;
                    case 57:
                        slideDistance = -((Math.sin(Math.toRadians(angleDiff)) * distanceToTarget)) + 2;
                        SmartDashboard.putNumber("Slide Dist", slideDistance);
                        driveInches(slideDistance, 90, 1, false);
                        setTimerAndAdvanceStep(3000);
                        break;
                    case 58:
                        if (DriveAuto.hasArrived()) {
                            advanceStep();
                        }
                        break;
                    case 59:
                        // keep scanning for a distance reading
                        distanceToTarget = Vision.getDistanceFromTarget();
                        if (distanceToTarget > 0) {
                            advanceStep();
                        }
                        break;
                    case 60:
                        DriveAuto.driveInches(distanceToTarget + 12, 0, 1);
                        setTimerAndAdvanceStep(4000);
                        break;
                    case 61:
                        if (DriveAuto.hasArrived()){
                            advanceStep();
                        }
                        break;
                    case 62:
                        Manipulator.fingerUp();
                        break;
                        

                    
                    } //end of switch statement

                
            } //end of isRunning statement


        } //end of tick statement


    
} //end of extends statement



