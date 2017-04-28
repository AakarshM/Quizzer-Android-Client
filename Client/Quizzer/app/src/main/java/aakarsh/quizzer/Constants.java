package aakarsh.quizzer;

/**
 * Created by Aakarsh on 3/3/17.
 */

public class Constants {
    public static final String BASE_URL = "http://da61f299.ngrok.io"; //get
    public static final String SIGNUP_STUDENT = BASE_URL + "/students"; //post
    public static final String LOGIN_STUDENT = BASE_URL + "/students" + "/login"; //post
    public static final String SIGNUP_TEACHER = BASE_URL + "/teachers"; //post
    public static final String LOGIN_TEACHER = BASE_URL + "/teachers" + "/login"; //post
    public static final String STUDENT_INFO = BASE_URL + "/student/info";
    public static final String ADD_CLASS = BASE_URL + "/addclass"; //put
    public static final String QUESTION_ANSWERED = BASE_URL + "/questionanswered"; //put
    public static final String CLASS_LIST = BASE_URL + "/classlist";
    public static final String CLASS_SUMMARY = BASE_URL + "/summaryclass";
    public static final String ATTENDANCE_SUMMARY = BASE_URL + "/student/info/attendance";

    public static String CLASS_FOCUS = "";
    public static String JOINED_ROOM = "";
    public static String HEADER = "";
    public static String CLASS_NAME = ""; //Name of class current session is of.

    public static String TEACHER_EMAIL = "";
}
