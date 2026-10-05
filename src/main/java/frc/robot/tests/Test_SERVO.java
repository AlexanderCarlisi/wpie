package frc.robot.tests;

import com.pi4j.Pi4J;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.context.Context;

public class Test_SERVO {

    // Default I2C address for DFRobot Motor Driver HAT / PCA9685 compatible controllers
    private final int I2C_BUS = 1;
    private final int I2C_ADDRESS = 0x40; // Change to 0x40 if using standard PCA9685 HAT variants

    // PCA9685 Register definitions
    private final int MODE1 = 0x00;
    private final int PRESCALE = 0xFE;
    private final int LED0_ON_L = 0x06;

    private I2C i2cDevice;

    public void test() throws Exception {
        // Initialize Pi4J context
        Context pi4j = Pi4J.newAutoContext();

        System.out.println("Initializing DFRobot Motor Driver HAT / Servo expansion...");

        // Create I2C config & instance
        I2CProvider i2cProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("I2C-" + I2C_BUS)
                .bus(I2C_BUS)
                .device(I2C_ADDRESS)
                .build();

        i2cDevice = i2cProvider.create(i2cConfig);

        // Initialize driver board (Reset and set PWM frequency to ~50Hz for servos)
        initializeServoDriver(50.0);

        // Example: Control Servo connected to channel 0 (Miuzei MG90S)
        int servoChannel = 0;

        for (servoChannel = 0; servoChannel < 8; servoChannel++) {
            System.out.println("Moving servo to 0 degrees (Min Position)...");
            setServoAngle(servoChannel, 0);
            Thread.sleep(1000);

            System.out.println("Moving servo to 90 degrees (Center Position)...");
            setServoAngle(servoChannel, 90);
            Thread.sleep(1000);

            System.out.println("Moving servo to 180 degrees (Max Position)...");
            setServoAngle(servoChannel, 180);
            Thread.sleep(1000);
        }

        // Shutdown Pi4J context
        pi4j.shutdown();
        System.out.println("Done.");
    }

    private void initializeServoDriver(double frequency) throws Exception {
        // Wake up board and set to normal mode
        i2cDevice.writeRegister(MODE1, (byte) 0x00);
        Thread.sleep(10);

        // Set PWM Frequency for Servos (~50Hz)
        // Formula: prescale = round(clock_freq / (4096 * frequency)) - 1, where clock_freq = 25,000,000 Hz
        int preScaleVal = (int) (Math.round(25000000.0 / (4096.0 * frequency)) - 1);

        // Go to sleep to set prescale
        byte oldMode = (byte) i2cDevice.readRegister(MODE1);
        byte newMode = (byte) ((oldMode & 0x7F) | 0x10); // Sleep
        i2cDevice.writeRegister(MODE1, newMode);

        // Write prescale value
        i2cDevice.writeRegister(PRESCALE, (byte) preScaleVal);

        // Wake up and enable auto-increment
        i2cDevice.writeRegister(MODE1, (byte) (oldMode | 0xa1));
        Thread.sleep(10);
    }

    public void setServoAngle(int channel, double angle) throws Exception {
        // Constrain angle between 0 and 180 degrees
        angle = Math.max(0, Math.min(180, angle));

        // Map angle (0-180) to pulse width ticks (typically 150 to 600 for standard MG90S servos)
        // 150 ≈ 1ms pulse (0 deg), 600 ≈ 2ms pulse (180 deg)
        int pulseLength = (int) (150.0 + (angle / 180.0) * (600.0 - 150.0));

        int register = LED0_ON_L + (4 * channel);

        // Send PWM command (ON = 0, OFF = pulseLength)
        i2cDevice.writeRegister(register, (byte) 0);
        i2cDevice.writeRegister(register + 1, (byte) 0);
        i2cDevice.writeRegister(register + 2, (byte) (pulseLength & 0xFF));
        i2cDevice.writeRegister(register + 3, (byte) (pulseLength >> 8));
    }
}
