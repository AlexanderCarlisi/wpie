package com.frc564.wpielib;

import com.pi4j.context.Context;

/**
 * 
 * PIRobot
 */
public class PIRobot {
    private static PIRobot s_robot;

    private Context _pi4j;
    private IDriverBoard _driverBoard;

    public PIRobot(Context pi4j, IDriverBoard driverBoard) {
        _pi4j = pi4j;
        s_robot._driverBoard = driverBoard;
    }

    public static IDriverBoard getDriverBoard() {
        return s_robot._driverBoard;
    }
}