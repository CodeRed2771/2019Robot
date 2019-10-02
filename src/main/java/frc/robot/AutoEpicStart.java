package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoEpicStart extends AutoBaseClass {
    public AutoEpicStart() {
		super();
	}
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
                        } else {
                            driveInches(10, 0, 1);
                        }
                        setTimerAndAdvanceStep(2000);
                        break;

                    case 1:
                        if (driveCompleted()){
                            advanceStep();
                        }
                        break;
                    case 2:
                        mSubAutoProg = new AutoDoEverything();
                        mSubAutoProg.start();
                        advanceStep();
                        break;
                    case 3:
                        mSubAutoProg.tick();
                        break;
                    
                    }

                
            }


        }


    
}



