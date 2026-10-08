package frc.robot.components;

import frc.robot.IEncoder;
import frc.robot.PIRobot;
import frc.robot.subsystems.ExampleSubsystem;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * QuadratureEncoder
 */
public class QuadratureEncoder implements IEncoder {
    private final AtomicLong pulseCount = new AtomicLong(0);
    private DigitalInput _pinA;
    private DigitalInput _pinB;

    // Track previous pin states (2-bit state: Bit 1 = A, Bit 0 = B)
    private int lastState = 0;

    // Quadrature state-transition lookup table for 4x decoding resolution
    // Indexes: (oldState << 2) | newState
    // private static final int[] QUADRATURE_TABLE = {
    //      0, -1,  1,  0,
    //      1,  0,  0, -1,
    //     -1,  0,  0,  1,
    //      0,  1, -1,  0
    // };
    private static final int[] QUADRATURE_TABLE = {
        0,  1, -1,  0,  // lastState = 0 (00)
        -1,  0,  0,  1,  // lastState = 1 (01)
        1,  0,  0, -1,  // lastState = 2 (10)
        0, -1,  1,  0   // lastState = 3 (11)
    };

    public QuadratureEncoder(int id) {
        init(id);
    }

    @Override
    public void setPins(int pinA, int pinB) {
        Context pi4j = PIRobot.getPi4JContext();

        // Configure GPIO Pin A
        DigitalInputConfig configA = DigitalInput.newConfigBuilder(pi4j)
                .address(pinA)
                .pull(PullResistance.PULL_UP)
                .debounce(0L)
                .build();

        // Configure GPIO Pin B
        DigitalInputConfig configB = DigitalInput.newConfigBuilder(pi4j)
                .address(pinB)
                .pull(PullResistance.PULL_UP)
                .debounce(0L)
                .build();

        _pinA = pi4j.create(configA);
        _pinB = pi4j.create(configB);

        // Read initial state
        lastState = (getState(_pinA) << 1) | getState(_pinB);

        // Attach listeners for both rising and falling edges
        DigitalStateChangeListener listener = event -> updatePosition();
        _pinA.addListener(listener);
        _pinB.addListener(listener);
    }

    private synchronized void updatePosition() {
        // CommandScheduler.getInstance().schedule(Commands.print(">>UPDATING ENCODER<<"))
        int currentState = (getState(_pinA) << 1) | getState(_pinB);
        int index = (lastState << 2) | currentState;

        pulseCount.addAndGet(QUADRATURE_TABLE[index]);

        if (lastState != currentState) {
            CommandScheduler.getInstance().schedule(Commands.print(this.hashCode()+">"+lastState+","+currentState+"<"+pulseCount.get()+"-"+QUADRATURE_TABLE[index]));

        }


        // if (_pinA.isHigh()) {
        //     CommandScheduler.getInstance().schedule(Commands.print("High"));
        // }
        // if (_pinA.isLow()) {
        //     CommandScheduler.getInstance().schedule(Commands.print("Low"));
        // }
        // if (_pinA.isOff()) {
        //     CommandScheduler.getInstance().schedule(Commands.print("Off"));
        // }
        // if (_pinA.isOn()) {
        //     CommandScheduler.getInstance().schedule(Commands.print("On"));
        // }

        lastState = currentState;
    }

    private int getState(DigitalInput pin) {
        return pin.state() == DigitalState.HIGH ? 1 : 0;
    }

    @Override
    public long getDistanceTicks() {
        return pulseCount.get();
    }

    @Override
    public void reset() {
        pulseCount.set(0);
    }



    // @Override
    // public void update() {
    //     // updatePosition();
    // }
}
