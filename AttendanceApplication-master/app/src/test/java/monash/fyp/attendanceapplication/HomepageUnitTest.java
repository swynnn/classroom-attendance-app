package monash.fyp.attendanceapplication;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Class representing the main page for unit testing
 */
public class HomepageUnitTest {

    /***
     * Test method for checking x y coordinate
     */
    @Test
    public void valid_xy_true() {
        Calculation test = new Calculation();
        assertTrue(test.valid_xy(5.0, 3.0, 0.0, 0.0, 4.0, 3.0,0.0, 2.0, 1.5, 1.5, 2.5));
    }

    /***
     * Test method for checking x y coordinate
     */
    @Test
    public void valid_xy_false() {
        Calculation test = new Calculation();
        assertFalse(test.valid_xy(5.0, 3.0, 0.0, 0.0, 4.0, 3.0,0.0, 2.0, 2.0, 2.0, 5.0));
    }

    /***
     * Test method for getting correct distance
     */
    @Test
    public void getDistance_correct() {
        Calculation test = new Calculation();
        assertEquals(test.getDistance(-88), 1.0, 1e-15);
    }

    /***
     * Test method for getting correct average value
     */
    @Test
    public void getAverage_correct() {
        Calculation test = new Calculation();
        ArrayList<Integer> RSSI = new ArrayList<>();
        RSSI.add(-87);
        RSSI.add(-88);
        RSSI.add(-89);
        assertEquals((int)test.getAverage(RSSI), -88);
    }

    /***
     * Test method for getting correct variance value
     */
    @Test
    public void getVariance_correct() {
        Calculation test = new Calculation();
        ArrayList<Integer> RSSI = new ArrayList<>();
        RSSI.add(-86);
        RSSI.add(-87);
        RSSI.add(-88);
        RSSI.add(-89);
        RSSI.add(-90);
        assertEquals((int)test.getVariance(RSSI), 2);
    }

    /***
     * Test method for checking user is within range
     */
    @Test
    public void withinRange_check_within(){
        Calculation test = new Calculation();
        ArrayList<Integer> A = new ArrayList<>();
        ArrayList<Integer> B = new ArrayList<>();
        ArrayList<Integer> C = new ArrayList<>();

        A.add(-92);
        B.add(-92);
        C.add(-97);

        assertTrue(test.withinRange(A, B, C));
    }

    /***
     * Test method for checking user is within range
     */
    @Test
    public void withinRange_check_not_inside(){
        Calculation test = new Calculation();
        ArrayList<Integer> A = new ArrayList<>();
        ArrayList<Integer> B = new ArrayList<>();
        ArrayList<Integer> C = new ArrayList<>();

        A.add(-101);
        B.add(-80);
        C.add(-111);

        assertFalse(test.withinRange(A, B, C));
    }

}
