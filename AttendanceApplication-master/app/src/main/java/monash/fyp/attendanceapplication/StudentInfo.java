package monash.fyp.attendanceapplication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing the information of a student
 */
public class StudentInfo implements Serializable {

    private String name;
    private Integer age;
    private String student_id;
    private String identification_number;
    private ArrayList<String> classEnrolled;

    StudentInfo(){}

    /**
     * Constructor.
     *
     * @param name Name of student
     * @param age Age of student
     * @param student_id Student ID
     * @param identification_number Student IC
     * @param classList List of class enrolled
     */
    public StudentInfo(String name, Integer age, String student_id, String identification_number, ArrayList<String> classList){
        this.name = name;
        this.age = age;
        this.student_id = student_id;
        this.identification_number = identification_number;
        this.classEnrolled = classList;
    }

    /**
     * Getter to obtain student's name
     * @return name of student
     */
    public String getName() {return name;}

    /**
     * Setter to change student's name
     * @param new_name new name
     */
    public void setName(String new_name){name = new_name;}

    /**
     * Getter to obtain student age
     * @return age of student
     */
    public Integer getAge() {return age;}

    /**
     * Setter to change student age
     * @param new_age new age
     */
    public void setAge(Integer new_age) {age = new_age;}

    /**
     * Getter to get student ID number
     * @return student ID
     */
    public String getStudent_id() {return student_id;}

    /**
     * Setter to change student ID number
     * @param new_student_id new student ID
     */
    public void setStudent_id(String new_student_id) {
        student_id = new_student_id;}

    /**
     * Getter to get student IC
     * @return student IC number
     */
    public String getIdentification_number() {return identification_number;}

    /**
     * Setter to change student IC
     * @param new_identification_number new IC number
     */
    public void setIdentification_number(String new_identification_number){
        identification_number = new_identification_number;}

    /**
     * Getter to obtain the collection of class enrolled by student
     * @return class list
     */
    public ArrayList<String> getClassList() {return classEnrolled;}

    /**
     * Setter to change the class enrolled
     * @param classList new list containing class enrolled
     */
    public void setClassList(ArrayList<String> classList) {
        this.classEnrolled = classList;
    }
}
