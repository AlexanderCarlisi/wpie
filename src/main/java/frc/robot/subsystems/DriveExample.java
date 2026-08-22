package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.IDCMotor;
import frc.robot.IEncoder;
import frc.robot.components.DCGMN20Motor;
import frc.robot.components.JGB37Motor;
import frc.robot.components.QuadratureEncoder;

public class DriveExample extends SubsystemBase {
    private final JGB37Motor _driveMotorLeft = new JGB37Motor(0, true);
    private final JGB37Motor _driveMotorRight = new JGB37Motor(1);
    private final DCGMN20Motor _manipulatorMotor = new DCGMN20Motor(2);
    private final DCGMN20Motor _intakeMotor = new DCGMN20Motor(3);

    private final QuadratureEncoder _driveEncoderLeft = new QuadratureEncoder(0);
    private final QuadratureEncoder _driveEncoderRight = new QuadratureEncoder(1);
    private final QuadratureEncoder _manipulatorEncoder = new QuadratureEncoder(2);
    private final QuadratureEncoder _intakeEncoder = new QuadratureEncoder(3);

    public DriveExample() {
        // CommandScheduler.getInstance().registerSubsystem(this);
    }

    // public Command driveForward(double dutyCycle, double seconds) {
    //     return new DriveCommand(this, dutyCycle, seconds);
    // }

    private Command runMotor(IDCMotor motor, double dutyCycle, double seconds) {
        return Commands.runOnce(
            () -> {motor.setDutyCycle(dutyCycle);}
        ).andThen(Commands.waitSeconds(seconds)
        ).andThen(Commands.runOnce(
            () -> {motor.setDutyCycle(0);}
        ));
    }

    private Command printEncoderTick(String prefix, IEncoder encoder) {
        return Commands.print(prefix + String.valueOf(encoder.getDistanceTicks()));
    }

    public void setDriveDutyCycle(double left, double right) {
        _driveMotorLeft.setDutyCycle(left);
        _driveMotorRight.setDutyCycle(right);
    }

    private Command testMotor(String id, IDCMotor motor, double dutyCycle, double seconds) {
        return Commands.sequence(
            Commands.print("Running Motor Id: " + id),
            runMotor(motor, dutyCycle, seconds)
        );
    }

    public Command motorTest() {
        double dutyCycle = 0.25;
        double startDelaySeconds = 3;
        double testDelaySeconds = 1;

        System.out.println("========================");
        System.out.println("       MOTOR TEST       ");
        System.out.println("========================");
        System.out.println("Starting...");
        return Commands.sequence(
            Commands.waitSeconds(startDelaySeconds),
            testMotor("1", _driveMotorLeft, dutyCycle, testDelaySeconds),
            testMotor("2", _driveMotorLeft, dutyCycle, testDelaySeconds),
            testMotor("3", _driveMotorLeft, dutyCycle, testDelaySeconds),
            testMotor("4", _driveMotorLeft, dutyCycle, testDelaySeconds),
            Commands.print("========================"),
            Commands.print("   Motor Test Complete  "),
            Commands.print("========================")
        );
    }

    private Command testEncoder(String id, IEncoder encoder, IDCMotor motor, double dutyCycle, double seconds) {
        return Commands.sequence(
            printEncoderTick(String.format("Encoder (%s): ", id), encoder),
            testMotor(id, motor, dutyCycle, seconds),
            printEncoderTick(String.format("Encoder (%s): ", id), encoder),
            Commands.waitSeconds(seconds)
        );
    }

    public Command encoderTest() {
        double dutyCycle = 0.25;
        double startDelaySeconds = 3;
        double testDelaySeconds = 1;

        System.out.println("========================");
        System.out.println("       Encoder TEST     ");
        System.out.println("========================");
        System.out.println("Starting...");
        return Commands.sequence(
            Commands.waitSeconds(startDelaySeconds),

            testEncoder("1", _driveEncoderLeft, _driveMotorLeft, dutyCycle, testDelaySeconds),
            testEncoder("2", _driveEncoderRight, _driveMotorRight, dutyCycle, testDelaySeconds),
            testEncoder("3", _manipulatorEncoder, _manipulatorMotor, dutyCycle, testDelaySeconds),
            testEncoder("4", _intakeEncoder, _intakeMotor, dutyCycle, testDelaySeconds),

            Commands.print("========================"),
            Commands.print("  Encoder Test Complete  "),
            Commands.print("========================")
        );
    }
}
