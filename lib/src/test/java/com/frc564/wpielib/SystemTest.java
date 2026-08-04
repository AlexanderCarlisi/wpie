package com.frc564.wpielib;

import com.frc564.wpielib.components.EmakefunMotorHat;
import com.frc564.wpielib.components.PCA9685;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.plugin.mock.platform.MockPlatform;
import com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {

    private Context pi4j;
    private MockI2CProvider mockI2CProvider;

    @BeforeEach
    void setUp() {
        // 1. Reset the WPILib CommandScheduler before each test run
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().enable();

        // 2. Initialize Pi4J strictly with the Mock Platform for desktop execution
        pi4j = Pi4J.newContextBuilder()
                .add(new MockPlatform())
                .build();

        // 3. Grab reference to Pi4J's Mock I2C Provider
        mockI2CProvider = pi4j.provider("mock-i2c");
    }

    @AfterEach
    void tearDown() {
        // Shutdown Pi4J context after test execution
        if (pi4j != null) {
            pi4j.shutdown();
        }
    }

    @Test
    void testFullStackCommandToHardwarePipeline() {
        // // GIVEN: A PCA9685 driver connected to a simulated I2C bus (Bus 1, Addr 0x60)
        // PCA9685 pca9685 = new PCA9685(pi4j, 1, 0x60);
        // TestMotorSubsystem motorSubsystem = new TestMotorSubsystem(pca9685);

        // // WHEN: A WPILib command is scheduled to run against the subsystem
        // CommandScheduler.getInstance().schedule(
        //     new InstantCommand(() -> motorSubsystem.setSpeed(0.75), motorSubsystem)
        // );

        // // Execute 1 tick of the WPILib Command Loop
        // CommandScheduler.getInstance().run();

        // // THEN: Verify the full pipeline executed cleanly
        // // 1. Subsystem state updated
        // assertEquals(0.75, motorSubsystem.getSpeed(), 0.001, "Subsystem failed to update speed!");

        // // 2. Mock I2C provider created the device instance without crashing
        // assertNotNull(mockI2CProvider, "Mock I2C provider failed to initialize!");

        // // 3. Verify hardware driver executed without throwing exceptions
        // assertDoesNotThrow(() -> pca9685.setDutyCycle(0, 0.75));

        PIRobot robot = new PIRobot(pi4j, new EmakefunMotorHat(pi4j));
        SubsystemTest subsystem = new SubsystemTest();

        CommandScheduler.getInstance().schedule(
            subsystem.driveForward(0.5, 0.5)
        );
    }
}