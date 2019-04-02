/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class AutoFindHatch extends AutoBaseClass {

    private double distanceToTarget = 0;
    private double angleDiff;
    private double distToStayBackOnFirstDrive = 20;
    private double targetAngle = 0;
    private boolean drivingAllowed = true;

    public void start() {
        super.start();
        Vision.setTargetTrackingMode();
    }

    public void stop() {
        super.stop();
        Vision.setDriverMode();
    }

    public void setDrivingAllowed(boolean isDrivingAllowed) {
        drivingAllowed = isDrivingAllowed;
    }

    public void tick() {

        if (isRunning()) {

            DriveAuto.tick();
            SmartDashboard.putNumber("Hatch Step", getCurrentStep());

            switch (getCurrentStep()) {
            case 0:
                // keep scanning for a distance reading
                distanceToTarget = Vision.getDistanceFromTarget();
                if (distanceToTarget > 0) {
                    advanceStep();
                }
                break;
            case 1:
                System.out.println("target type " + mTargetType.toString());
                targetAngle = TargetInfo.targetAngle(mTargetType);
                System.out.println("Target Angle " + targetAngle);
                DriveAuto.turnDegrees(Vision.offsetFromTarget(), .35);
                setTimerAndAdvanceStep(1000);
                break;
            case 2:
                if (DriveAuto.turnCompleted()) {
                    advanceStep();
                }
                break;
            case 3:
                angleDiff = RobotGyro.getClosestTurn(targetAngle);
                // angleDiff = targetAngle - RobotGyro.getRelativeAngle();
                System.out.println("anglediff " + angleDiff);
                DriveAuto.turnDegrees(angleDiff, .45); // Square up with target
                setTimerAndAdvanceStep(1000);
                break;
            case 4:
                if (DriveAuto.turnCompleted()) {
                    advanceStep();
                }
                break;
            case 5:
                double slideDistance = -((Math.sin(Math.toRadians(angleDiff)) * distanceToTarget) + 1);
                SmartDashboard.putNumber("Slide Dist", slideDistance);
                driveInches(slideDistance, 90, .5, false);
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
                driveInches(distanceToTarget - distToStayBackOnFirstDrive, 0, .5, true);
                setTimerAndAdvanceStep(3000);
                break;
            case 9:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 10:
                if (drivingAllowed) {
                    driveInches(distToStayBackOnFirstDrive, 0, .2, false);
                    setTimerAndAdvanceStep(1000);
                } else {
                    setTimerAndAdvanceStep(20);
                }
                break;
            case 11:
                if (DriveAuto.hasArrived()) {
                    advanceStep();
                }
                break;
            case 12:
                stop();
                break;
            }
        }

        SmartDashboard.putBoolean("Hatch running", isRunning());
        SmartDashboard.putNumber("tx", Vision.tx());
    }
}
