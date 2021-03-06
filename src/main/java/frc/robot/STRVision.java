/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Add your docs here.
 */
public class STRVision implements PIDSource{
	double targetArray[];
	int arrayPos = 0;
	int maxPos = 5;

	public STRVision() {
		targetArray = new double[maxPos];
	}

    public PIDSourceType getPIDSourceType() {
		return PIDSourceType.kDisplacement;
	}

	public void setPIDSourceType(PIDSourceType pType) {

	}

	public double pidGet() {
		double maxRateOfChange = 2;
		double newValue = Vision.offsetFromTarget();
		double currentArrayAverage = arrayAverage();

		if (newValue == 0) {
			targetArray[arrayPos] = 0;
		} else {
			// make sure the new value isn't extremely different than existing values
			if (currentArrayAverage == 0) { // we don't anything yet, so just take it
				targetArray[arrayPos] = newValue;
			} else {
				if (newValue - currentArrayAverage > maxRateOfChange) {
					newValue = currentArrayAverage + maxRateOfChange;
				} else if (currentArrayAverage - newValue > maxRateOfChange) {
					newValue = currentArrayAverage - maxRateOfChange;
				}

				targetArray[arrayPos] = newValue;
			}
		}

		// if we just got a zero, blank out the array
		if (targetArray[arrayPos] == 0) {
			for (int i = 0; i < maxPos; i++) {
				targetArray[i] = 0;
			}
		} else {
			arrayPos++;

			if (arrayPos >= maxPos)
				arrayPos = 0;
		}

		return arrayAverage();
	}

	private double arrayAverage() {
		double targetSum = 0;
		int targetCount = 0;

		for (int i = 0; i < maxPos; i++) {
			if (targetArray[i] != 0) {
				targetSum += targetArray[i];
				targetCount++;
			}
		}

		if (targetCount == 0) {
			return 0;
		} else {
			return targetSum / targetCount;
		}
	}
}
