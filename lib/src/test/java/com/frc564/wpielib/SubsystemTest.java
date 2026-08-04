package com.frc564.wpielib;

import com.frc564.wpielib.components.DCGMN20Motor;
import com.frc564.wpielib.components.JGB37Motor;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SubsystemTest extends SubsystemBase {
    private final JGB37Motor _driveMotorLeft = new JGB37Motor(0, true);
    private final JGB37Motor _driveMotorRight = new JGB37Motor(1);
    private final DCGMN20Motor _manipulatorMotor = new DCGMN20Motor(2);
    private final DCGMN20Motor _intakeMotor = new DCGMN20Motor(3);

    public Command driveForward(double dutyCycle, double seconds) {
        return new DriveCommand(this, dutyCycle, seconds);
    }

    public void setDriveDutyCycle(double left, double right) {
        _driveMotorLeft.setDutyCycle(left);
        _driveMotorRight.setDutyCycle(right);
    }

}
