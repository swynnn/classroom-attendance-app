package monash.fyp.attendanceapplication;

import android.annotation.SuppressLint;

import java.util.ArrayList;

/**
 * Class containing all the methods for unit testing, located in test folder
 */
public class Calculation {

    /***
     * Calculate user's coordinate using Trilateration and check if user is within specified area
     * @param dis_A distance to first beacon
     * @param dis_B distance to second beacon
     * @param dis_C distance to third beacon
     * @return true/false
     */
    @SuppressLint("DefaultLocale")
    public boolean valid_xy(double width, double height, double x1, double x2, double x3, double y1, double y2, double y3,  double dis_A, double dis_B, double dis_C){

        // trilateration formula
        double value1 = ( Math.pow(dis_A, 2) - Math.pow(dis_B, 2) ) - ( Math.pow(x1, 2) - Math.pow(x2, 2) ) - ( Math.pow(y1, 2) - Math.pow(y2, 2) );
        double value2 = ( Math.pow(dis_A, 2) - Math.pow(dis_C, 2) ) - ( Math.pow(x1, 2) - Math.pow(x3, 2) ) - ( Math.pow(y1, 2) - Math.pow(y3, 2) );
        double denominator = ( 4 * (x2 - x1) * (y3 - y1) ) - ( 4 * (x3 - x1) * (y2 - y1) );

        // user's x and y coordinate estimated
        double x = ( (value1 * 2 * (y3-y1)) - (value2 * 2 * (y2-y1)) ) / denominator;
        double y = ( (value2 * 2 * (x2-x1)) - (value1 * 2 * (x3-x1)) ) / denominator;

        // check if user is within specified area
        if (x >= 0 && x <= width && y >= 0 && y <= height ){
            return true;
        }
        return false;
    }

    /***
     * A method to estimate the distance to beacon using rssi value using rssi model
     * @param rssi rssi value
     * @return distance to beacon
     */
    public double getDistance(Integer rssi){
        int A = -88;
        double n = 2.3;
        return Math.pow(10, ((rssi - A) / (-10 * n)) );
    }

    /***
     * A method to get the average rssi value
     * @param rssi_list an array list containing all the rssi value
     * @return average value
     */
    public Integer getAverage(ArrayList<Integer> rssi_list){
        Integer sum = 0;
        for (Integer rssi: rssi_list){
            sum += rssi;
        }
        return sum/rssi_list.size();
    }

    /***
     * A method to get the variance of  rssi value
     * @param rssi_list an array list containing all the rssi value
     * @return variance value
     */
    public double getVariance(ArrayList<Integer> rssi_list){
        double sumDiffSquared = 0.0;
        if (rssi_list.size() > 0) {
            double avg = getAverage(rssi_list);
            for (Integer rssi : rssi_list) {
                double diff = rssi - avg;
                diff *= diff;
                sumDiffSquared += diff;
            }
            return sumDiffSquared / (rssi_list.size());
        }
        else{
            return 0.0;
        }
    }

    /***
     * A method modified from the original within range method for testing purposes
     * @param rssi_A_list distance to first beacon
     * @param rssi_B_list distance to second beacon
     * @param rssi_C_list distance to third beacon
     * @return true/false
     */
    public boolean withinRange(ArrayList<Integer> rssi_A_list, ArrayList<Integer> rssi_B_list, ArrayList<Integer> rssi_C_list){
        double dis_A = 0.0, dis_B = 0.0, dis_C = 0.0;
        // calculate the estimated distance from user to each beacons
        if (rssi_A_list.size() > 0){
            Integer average_A = getAverage(rssi_A_list);
            dis_A = getDistance(average_A);

        }
        if (rssi_B_list.size() > 0){
            Integer average_B = getAverage(rssi_B_list);
            dis_B = getDistance(average_B);

        }
        if (rssi_C_list.size() > 0){
            Integer average_C = getAverage(rssi_C_list);
            dis_C = getDistance(average_C);

        }

        // check if user is within the specified area
        if (dis_A > 0 && dis_B > 0 && dis_C > 0 ) {
            return valid_xy(5.0, 3.0, 0.0, 0.0, 4.0, 3.0,0.0, 2.0, dis_A, dis_B, dis_C);
        }
        // return false if any one of the beacon is out of range
        else {
            return false;
        }
    }
}
