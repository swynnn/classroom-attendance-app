package monash.fyp.attendanceapplication;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class representing the information of a class
 */
public class ClassInfo implements Serializable {

    private String unit_code;
    private String unit_name;
    private HashMap<String, HashMap<String, Integer>> beacons = new HashMap<>();
    private HashMap<String, HashMap<String, Boolean>> sessions = new HashMap<>();
    private Integer room_width;
    private Integer room_height;

    ClassInfo(){}

    /**
     * Constructor.
     *
     * @param unit_code Unit code
     * @param unit_name Unit name
     * @param sessions List of sessions containing the session and students attending each session
     */
    ClassInfo(String unit_code, String unit_name, HashMap<String, HashMap<String, Integer>> beacons, HashMap<String, HashMap<String, Boolean>> sessions, Integer width, Integer height){
        this.unit_code = unit_code;
        this.unit_name = unit_name;
        this.beacons = beacons;
        this.sessions = sessions;
        this.room_width = width;
        this.room_height = height;
    }

    /**
     * Getter to obtain unit code
     * @return unit code
     */
    public String getUnit_code() {
        return unit_code;
    }

    /**
     * Getter to obtain unit name
     * @return unit name
     */
    public String getUnit_name() {
        return unit_name;
    }

    /**
     * Getter to obtain all the classes for this unit
     * @return class session list
     */
    public HashMap<String, HashMap<String, Boolean>> getSessions() {
        return sessions;
    }

    /**
     * Getter to obtain all the classes for this unit
     * @return class session list
     */
    public HashMap<String, HashMap<String, Integer>> getBeacons() {
        return beacons;
    }

    /**
     * Getter to obtain room width for this classroom
     * @return class room width
     */
    public Integer getRoom_width() {
        return room_width;
    }

    /**
     * Getter to obtain room height for this classroom
     * @return class room height
     */
    public Integer getRoom_height() {
        return room_height;
    }
}
