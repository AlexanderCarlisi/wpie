package frc.robot.components;

import frc.robot.PIRobot;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

/**
 * PCA9685 LED Bus Controller
 * <p>
 * The PCA9685 is a 16 Channel, 12 bit PWM, FM+ Frequency, I2C Controlled LED Bus.
 * </p>
 * <p>
 * Although this is the case, many boards utilize this chip for other purposes.
 * For example Motor Driver Boards may utilize this chip for controlling Motors and Servos.
 * </p>
 *
 * @author Alexander Carlisi
 * @see <a href="https://www.nxp.com/docs/en/data-sheet/PCA9685.pdf">PCA9685 Data Sheet</a>
 * @see <a href="https://github.com/DFRobotdl/RaspberryPi-MotorDriveBoard">Emakefun Motor Drive Board Reference</a>
 */
public class PCA9685 {

    private static final int _CHANNEL_SIZE = 16;
    private static final int _PWM_RES = 4096; // 2^12

    // Registers
    private static final int _MODE1 = 0x00;
    private static final int _MODE2 = 0x01;
    private static final int _PRESCALE = 0xFE;
    private static final int _LED0_ON_L = 0x06;
    private static final int _ALL_LED_ON_L = 0xFA;

    // Bitmasks
    private static final int _SLEEP = 0x10;
    private static final int _ALLCALL = 0x01;
    private static final int _AI = 0x20;      // Auto-Increment
    private static final int _OUTDRV = 0x04;  // Totem Pole Output
    private static final int _RESTART = 0x80;

    private final I2C _I2C;

    /**
     * Initializes the PCA9685 chip at a given I2C address (default 0x60).
     */
    public PCA9685(int bus, int address) {
        I2CConfig config = I2C.newConfigBuilder(PIRobot.getPi4JContext())
                .id("PCA9685-" + Integer.toHexString(address))
                .bus(bus)
                .device(address)
                .build();

        _I2C = PIRobot.getPi4JContext().create(config);

        // Zero out all 16 channels simultaneously using broadcast registers
        setAllPWM(0, 0);

        // Set _MODE2 to Totem Pole (Push-Pull) output drive for H-Bridge compatibility
        _I2C.writeRegister(_MODE2, (byte) _OUTDRV);

        // Respond to All-Call I2C addresses and clear sleep bit to wake up
        _I2C.writeRegister(_MODE1, (byte) _ALLCALL);
        delay(5);

        // Enable Auto-Increment (_AI) so multi-byte writes work sequentially
        int mode1 = _I2C.readRegister(_MODE1);
        mode1 = (mode1 & ~_SLEEP) | _AI; // Wake up + Auto-Increment
        _I2C.writeRegister(_MODE1, (byte) mode1);
        delay(5);
    }

    /**
     * Sets PWM frequency in Hertz.
     *
     * @param freqHz
     */
    public void setPWMFreq(double freqHz) {
        double prescaleVal = (25000000.0 / ((double) _PWM_RES * freqHz)) - 1.0;
        byte prescale = (byte) Math.round(prescaleVal);

        int oldMode = _I2C.readRegister(_MODE1);
        int sleepMode = (oldMode & ~_RESTART) | _SLEEP; // Clear RESTART, set SLEEP

        _I2C.writeRegister(_MODE1, (byte) sleepMode);
        _I2C.writeRegister(_PRESCALE, prescale);

        // Restore mode without sleep bit
        _I2C.writeRegister(_MODE1, (byte) (oldMode & ~_SLEEP));
        delay(5);

        // Wake up with Auto-Increment AND Restart enabled
        _I2C.writeRegister(_MODE1, (byte) (oldMode | _AI | _RESTART));
    }

    /**
     * Sets raw 12-bit PWM ON/OFF values (0 to 4095) for a specific channel.
     *
     * @param channel   PWM Channel
     * @param on        When to start High Signal
     * @param off       When to start Low Signal
     *
     * @implNote Example code from Emakefun explicitly commented out setting the registers
     *  all at the same time, so this code implementation may not work.
     * @implNote The PCA9685 has a unique functionality for PWM of 4096, so its an allowed value.
     */
    // public void setPWM(int channel, int on, int off) {
    //     // Write 4 registers sequentially: ON_L, ON_H, OFF_L, OFF_H
    //     byte[] buffer = new byte[] {
    //         (byte) (on & 0xFF),
    //         (byte) ((on >> 8) & 0xFF),
    //         (byte) (off & 0xFF),
    //         (byte) ((off >> 8) & 0xFF)
    //     };

    //     int regAddr = getRegisterAddr(channel);
    //     _I2C.writeRegister(regAddr, buffer);
    // }
    public void setPWM(int channel, int on, int off) {
        int regAddr = getRegisterAddr(channel);

        // Write each of the 4 consecutive registers individually to avoid
        // any ambiguity or misinterpretation by the Pi4J I2C driver array handler.
        _I2C.writeRegister(regAddr, (byte) (on & 0xFF));
        _I2C.writeRegister(regAddr + 1, (byte) ((on >> 8) & 0xFF));
        _I2C.writeRegister(regAddr + 2, (byte) (off & 0xFF));
        _I2C.writeRegister(regAddr + 3, (byte) ((off >> 8) & 0xFF));
    }

    // /**
    //  * Set PWM as a duty cycle
    //  *
    //  * @param channel PWM Channel
    //  * @param dutyCycle
    //  */
    // public void setDutyCycle(int channel, double dutyCycle) {
    //     dutyCycle = Math.max(0.0, Math.min(1.0, dutyCycle)); // Clamp 0-1
    //     int off = (int) Math.round(dutyCycle * (double) _PWM_RES);

    //     if (off == 0) {
    //         setPWM(channel, 0, 0);
    //     } else if (off >= _PWM_RES-1) {
    //         setPWM(channel, _PWM_RES, 0); // Full ON
    //     } else {
    //         setPWM(channel, 0, off);
    //     }
    // }

    /**
     * Set PWM for ALL 16 channels at once using the broadcast registers.
     */
    public void setAllPWM(int on, int off) {
        byte[] buffer = new byte[]{
            (byte) (on & 0xFF),
            (byte) ((on >> 8) & 0xFF),
            (byte) (off & 0xFF),
            (byte) ((off >> 8) & 0xFF)
        };
        _I2C.writeRegister(_ALL_LED_ON_L, buffer);
    }

    /**
     * Get Register Address for given Channel.
     *
     * @param channel PWM channel
     * @return register address
     *
     * @implNote Every Channel has 4 Registers, LED0 starts Reg 6
     */
    private int getRegisterAddr(int channel) throws IllegalArgumentException {
        if (channel > _CHANNEL_SIZE-1 || channel < 0)
            throw new IllegalArgumentException("Channel arg must be in [0,15]");
        return _LED0_ON_L + (4 * channel);
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}
