package com.frc564.wpielib;

import edu.wpi.first.wpilibj2.command.Command;

public class DriveCommand extends Command {
    private final SubsystemTest _DRIVETRAIN;
    private final double _DUTYCYCLE;
    private final double _SECONDS;

    private double _endTime;

    public DriveCommand(SubsystemTest drivetrain, double dutyCycle, double seconds) {
        _DRIVETRAIN = drivetrain;
        _DUTYCYCLE = dutyCycle;
        _SECONDS = seconds;
        addRequirements(_DRIVETRAIN);
    }

    @Override
    public void initialize() {
        _endTime = System.currentTimeMillis() + (_SECONDS * 1000.0);
        _DRIVETRAIN.setDriveDutyCycle(_DUTYCYCLE, _DUTYCYCLE);
    }

    @Override
    public void execute() {

    }

    @Override
    public void end(boolean interrupt) {
        _DRIVETRAIN.setDriveDutyCycle(0, 0);
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() >= _endTime;
    }
}
