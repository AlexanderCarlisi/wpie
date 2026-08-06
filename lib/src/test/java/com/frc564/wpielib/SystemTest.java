package com.frc564.wpielib;

import com.frc564.wpielib.components.EmakefunMotorHat;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.plugin.mock.platform.MockPlatform;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;
import com.pi4j.plugin.mock.provider.pwm.MockPwmProvider;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SystemTest {

    private Context pi4j;
    private MockI2CProvider mockI2CProvider;

    @BeforeEach
    void setUp() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().enable();

        // 1. Manually construct and attach the mock providers and platform
        // DO NOT call .autoDetect() - that pulls in pigpio/raspberrypi
        pi4j = Pi4J.newContextBuilder()
                .add(
                    MockI2CProvider.newInstance(),
                    MockDigitalOutputProvider.newInstance(),
                    MockDigitalInputProvider.newInstance(),
                    MockPwmProvider.newInstance()
                )
                .add(new MockPlatform())
                .build();

        // 2. Obtain mock provider reference
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
    void subsystemTest() {
        if (pi4j != null) {
            EmakefunMotorHat driverBoard = new EmakefunMotorHat();
            PIRobot.setup(pi4j, driverBoard);
        }
        // PIRobot.setup(pi4j, new EmakefunMotorHat(pi4j));
        SubsystemTest subsystem = new SubsystemTest();

        // CommandScheduler.getInstance().schedule(
        //     subsystem.motorTest().andThen(Commands.waitSeconds(3)).andThen(subsystem.encoderTest())
        // );
        CommandScheduler.getInstance().schedule(
            subsystem.motorTest()
        );
        // CommandScheduler.getInstance().schedule(
            // subsystem.encoderTest()
        // );
    }
}