package frc.robot.libs.HID; 

/**
 *
 * @author Austin
 */
public class LegacyLogitechF310 extends HID {

    public static final Axis STICK_LEFT_X = new Axis(1, 0.02);
    public static final Axis STICK_LEFT_Y = new Axis(2, 0.02);
    public static final Axis TRIGGERS = new Axis(3);
    public static final Axis STICK_RIGHT_X = new Axis(4, 0.02);
    public static final Axis STICK_RIGHT_Y = new Axis(5, 0.02);
    public static final Axis DPAD_X = new Axis(6, 0.02); //the mode button is why
    public static final Axis DPAD_Y = new Axis(7, 0.02); //we need a deadzone
    public static final Button STICK_LEFT_LEFT = new AxisButton(STICK_LEFT_X, true);
    public static final Button STICK_LEFT_RIGHT = new AxisButton(STICK_LEFT_X, false);
    public static final Button STICK_LEFT_UP = new AxisButton(STICK_LEFT_Y, true);
    public static final Button STICK_LEFT_DOWN = new AxisButton(STICK_LEFT_Y, false);
    public static final Button TRIGGER_LEFT = new AxisButton(TRIGGERS, true);
    public static final Button TRIGGER_RIGHT = new AxisButton(TRIGGERS, false);
    public static final Button STICK_RIGHT_LEFT = new AxisButton(STICK_RIGHT_X, true);
    public static final Button STICK_RIGHT_RIGHT = new AxisButton(STICK_RIGHT_X, false);
    public static final Button STICK_RIGHT_UP = new AxisButton(STICK_RIGHT_Y, true);
    public static final Button STICK_RIGHT_DOWN = new AxisButton(STICK_RIGHT_Y, false);
    public static final Button DPAD_LEFT = new AxisButton(DPAD_X, true);
    public static final Button DPAD_RIGHT = new AxisButton(DPAD_X, false);
    public static final Button DPAD_UP = new AxisButton(DPAD_Y, true);
    public static final Button DPAD_DOWN = new AxisButton(DPAD_Y, false);
    public static final Button A = new Button(1);
    public static final Button B = new Button(2);
    public static final Button X = new Button(3);
    public static final Button Y = new Button(4);
    public static final Button BUMPER_LEFT = new Button(5);
    public static final Button BUMPER_RIGHT = new Button(6);
    public static final Button BACK = new Button(7);
    public static final Button START = new Button(8);
    public static final Button STICK_LEFT = new Button(9);
    public static final Button STICK_RIGHT = new Button(10);

    /**
     * The <code>LogitechF310</code> represents a model of gamepad, and stores
     * constants that corresponds to inputs (such as an axis or button) to be
     * passed into the HID object. This object should <b>not</b> be initialized
     * if you are using a <code>KeyMap</code> object along with the
     * <code>HID</code> object.
     *
     * @param port The port (1-4) that the controller is connected to in the
     * Driver Station.
     */
    public LegacyLogitechF310(int port) {
        super(port);
    }
}
