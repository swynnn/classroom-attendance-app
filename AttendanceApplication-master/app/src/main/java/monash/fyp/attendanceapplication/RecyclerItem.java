package monash.fyp.attendanceapplication;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for each item displayed using card view
 */
public class RecyclerItem implements Serializable, Comparable<RecyclerItem> {

    private String unit;
    private String unitName;
    private String date;

    /**
     * Constructor.
     *
     * @param code Unit code
     * @param name Unit name
     * @param date Class session
     */
    public RecyclerItem(String code, String name, String date){
        unit = code;
        unitName = name;
        this.date = date;
    }

    /**
     * Getter to obtain unit code
     * @return unit code
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Getter to obtain unit name
     * @return unit name
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Getter to obtain class session
     * @return date and time for that session
     */
    public String getDate() {
        return date;
    }

    /**
     * A method for comparison
     * Override to modify the comparison method to compare the date of item
     *
     * @param secondRecyclerItem item to be compared
     * @return comparison result
     */
    @Override
    public int compareTo(RecyclerItem secondRecyclerItem) {
        if (checkTimings(this.date, secondRecyclerItem.getDate())){
            return 1;
        }
        else{
            return -1;
        }
    }

    /**
     * A method for comparing the dates in two Strings
     *
     * @param firstTiming first string containing the date and time to be compared
     * @param secondTiming second string containing the date and time to be compared
     * @return true if first date and time is before second date and time otherwise false
     */
    private boolean checkTimings(String firstTiming, String secondTiming) {

        String pattern = "dd-MM-yyyy";
        String timePat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        SimpleDateFormat tdf = new SimpleDateFormat(timePat);

        try {
            // extract date for comparison
            Date date1 = sdf.parse(firstTiming.substring(0, 10));
            Date date2 = sdf.parse(secondTiming.substring(0, 10));

            // extract time for comparison
            Date time1 = tdf.parse(firstTiming.substring(12, 17));
            Date time2 = tdf.parse(secondTiming.substring(12, 17));

            if(date1.before(date2) || (date1.equals(date2) && time1.before(time2)) )  {
                return true;
            } else {

                return false;
            }

        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

}
