package com.frc564.wpielib;

import java.util.function.Supplier;

import com.pi4j.context.Context;

/**
 *
 * PIRobot
 */
public class PIRobot {
    private static PIRobot s_robot;

    private Context _pi4j;
    private IDriverBoard _driverBoard;

    public static void setup(Context pi4j, Supplier<IDriverBoard> driverBoard) {
        s_robot = new PIRobot();
        s_robot._pi4j = pi4j;
        s_robot._driverBoard = driverBoard.get();
    }

    public static IDriverBoard getDriverBoard() {
        return s_robot._driverBoard;

    }

    public static Context getPi4JContext() {
        return s_robot._pi4j;
    }
}
