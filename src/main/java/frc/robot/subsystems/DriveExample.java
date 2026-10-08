package frc.robot.subsystems;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.BooleanTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.IDCMotor;
import frc.robot.IEncoder;
import frc.robot.IServo;
import frc.robot.components.DCGMN20Motor;
import frc.robot.components.GenericServo;
import frc.robot.components.JGB37Motor;
import frc.robot.components.QuadratureEncoder;

public class DriveExample extends SubsystemBase {
    private final JGB37Motor _driveMotorLeft = new JGB37Motor(0, true);
    private final JGB37Motor _driveMotorRight = new JGB37Motor(1);
    // private final DCGMN20Motor _manipulatorMotor = new DCGMN20Motor(2);
    // private final DCGMN20Motor _intakeMotor = new DCGMN20Motor(3);

    private final JGB37Motor _manipulatorMotor = new JGB37Motor(2);
    private final JGB37Motor _intakeMotor = new JGB37Motor(3);
    private final JGB37Motor[] _motors = new JGB37Motor[] {
        _driveMotorLeft, _driveMotorRight, _manipulatorMotor, _intakeMotor
    };

    private final QuadratureEncoder _driveEncoderLeft = new QuadratureEncoder(0);
    private final QuadratureEncoder _driveEncoderRight = new QuadratureEncoder(1);
    private final QuadratureEncoder _manipulatorEncoder = new QuadratureEncoder(2);
    private final QuadratureEncoder _intakeEncoder = new QuadratureEncoder(3);

    private final GenericServo _armLigment1 = new GenericServo(0);
    private final GenericServo _armLigment2 = new GenericServo(1);
    private final GenericServo _armLigment3 = new GenericServo(2);
    private final GenericServo _armLigment4 = new GenericServo(3);
    private final GenericServo _armLigment5 = new GenericServo(4);
    private final GenericServo _armLigment6 = new GenericServo(5);
    private final GenericServo _armLigment7 = new GenericServo(6);
    private final GenericServo _armLigment8 = new GenericServo(7);

    private final GenericServo[] _servos = new GenericServo[] {
        _armLigment1,
        _armLigment2,
        _armLigment3,
        _armLigment4,
        _armLigment5,
        _armLigment6,
        _armLigment7,
        _armLigment8
    };

    private final NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    private final NetworkTable table = ntinst.getTable("Testing");
    private final BooleanTopic topic = ntinst.getBooleanTopic("Testing/MotorTest");
    private final BooleanSubscriber testsub = topic.subscribe(false);
    private boolean motor_test_running = false;

    public DriveExample() {
        // CommandScheduler.getInstance().registerSubsystem(this);
        _driveMotorLeft.attachEncoder(_driveEncoderLeft);
    }

    // public Command driveForward(double dutyCycle, double seconds) {
    //     return new DriveCommand(this, dutyCycle, seconds);
    // }


    @Override
    public void periodic() {
        // boolean motortest = testsub.get();
        // if(motortest && !motor_test_running) {
        //     _driveMotorLeft.setDutyCycle(0.5);
        // } else if (!motortest && motor_test_running) {
        //     _driveMotorLeft.setDutyCycle(0);
        // }
    }

    private Command runMotor(IDCMotor motor, double dutyCycle, double seconds) {
        return Commands.runOnce(
            () -> {motor.setDutyCycle(dutyCycle);}
        ).andThen(Commands.waitSeconds(seconds)
        ).andThen(Commands.runOnce(
            () -> {motor.setDutyCycle(0);}
        ));
    }

    public Command runMotor(int motorId, double dutyCycle) {
        return Commands.runOnce(
            () -> {_motors[motorId].setDutyCycle(dutyCycle);}
            , this);
    }

    private Command printEncoderTick(String prefix, IEncoder encoder) {
        // Use runOnce with a lambda so getDistanceTicks() is called live when the command executes
        return Commands.runOnce(() -> System.out.println(prefix + encoder.getDistanceTicks()));
    }

    public Command printEncoderTick(int motorId) {
        return Commands.print(">>>"+_motors[motorId].getEncoder().getDistanceTicks()+">"+_motors[motorId].getEncoder().hashCode());
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
        double dutyCycle = 0.8;
        double startDelaySeconds = 3;
        double testDelaySeconds = 3;

        System.out.println("========================");
        System.out.println("       MOTOR TEST       ");
        System.out.println("========================");
        System.out.println("Starting...");
        return Commands.sequence(
            Commands.waitSeconds(startDelaySeconds),
            testMotor("1", _driveMotorLeft, dutyCycle, testDelaySeconds),
            testMotor("2", _driveMotorRight, dutyCycle, testDelaySeconds),
            testMotor("3", _manipulatorMotor, dutyCycle, testDelaySeconds),
            testMotor("4", _intakeMotor, dutyCycle, testDelaySeconds),
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
        double testDelaySeconds = 5;

        System.out.println("========================");
        System.out.println("       Encoder TEST     ");
        System.out.println("========================");
        System.out.println("Starting...");
        return Commands.sequence(
            Commands.waitSeconds(startDelaySeconds),

            testEncoder("1", _driveEncoderLeft, _driveMotorLeft, dutyCycle, testDelaySeconds),
            // testEncoder("2", _driveEncoderRight, _driveMotorRight, dutyCycle, testDelaySeconds),
            // testEncoder("3", _manipulatorEncoder, _manipulatorMotor, dutyCycle, testDelaySeconds),
            // testEncoder("4", _intakeEncoder, _intakeMotor, dutyCycle, testDelaySeconds),

            Commands.print("========================"),
            Commands.print("  Encoder Test Complete  "),
            Commands.print("========================")
        );
    }

    public Command testServo(String id, IServo servo, double angle, double waitSeconds) {
        return Commands.sequence(
            Commands.print(String.format("Servo (%s): ", id)),
            Commands.runOnce(() -> {servo.setAngle(angle);}),
            Commands.waitSeconds(waitSeconds)
        );
    }

    public Command servoTest() {
        double startDelaySeconds = 3;
        double testDelaySeconds = 5;
        double angle = 40;

        System.out.println("========================");
        System.out.println("       Servos TEST      ");
        System.out.println("========================");
        System.out.println("Starting...");
        return Commands.sequence(
            Commands.waitSeconds(startDelaySeconds),
            testServo("0", _armLigment1, angle, testDelaySeconds),
            testServo("1", _armLigment2, angle, testDelaySeconds),
            testServo("2", _armLigment3, angle, testDelaySeconds),
            testServo("3", _armLigment4, angle, testDelaySeconds),
            testServo("4", _armLigment5, angle, testDelaySeconds),
            testServo("5", _armLigment6, angle, testDelaySeconds),
            testServo("6", _armLigment7, angle, testDelaySeconds),
            testServo("7", _armLigment8, angle, testDelaySeconds),

            Commands.print("========================"),
            Commands.print("   Servo Test Complete  "),
            Commands.print("========================")
        );
    }

    public Command servoTest(int servoId) {
        double startDelaySeconds = 1;
        double testDelaySeconds = 3;
        double angle = 40;

        System.out.println("========================");
        System.out.println("       Servos TEST      ");
        System.out.println("========================");
        System.out.println("Starting...");
        return Commands.sequence(
            Commands.waitSeconds(startDelaySeconds),

            Commands.print("To 0"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(0);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("To 90"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(90);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("To 45"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(45);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("To 180"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(180);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("To 340"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(340);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("To 360"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(360);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("To 420"),
            Commands.runOnce(() -> {_servos[servoId].setAngle(420);}),
            Commands.waitSeconds(testDelaySeconds),

            Commands.print("========================"),
            Commands.print("   Servo Test Complete  "),
            Commands.print("========================"));
        }
}
