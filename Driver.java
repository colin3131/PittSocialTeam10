import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;

public class Driver
{
    public static String url = "jdbc:postgresql://localhost:5432/";
    public static String userDBMS = "postgres";
    public static String passwordDBMS = "anastasiap05@";
    public static void main(String[] args) throws Exception
    {
        // Test Creation
        TestCreate();
        printTable("profile");

        // Display Messages
        TestDisplayMessages();

        // Display Friends
        TestDisplayFriends();

        // Three Degrees
        TestThreeDegrees();

        // Drop User
        TestDropUser();
        printTable("profile");
        printTable("messageinfo");
        printTable("groupmember");
        printTable("friend");

        // Initiate Friendship
        TestInitiateFriendshipPt1();
        printTable("pendingfriend");

        // Confirm Request
        TestInitiateFriendshipPt2();
        printTable("pendingfriend");
        printTable("friend");
      
        // Create Group
        createGroup();
        printTable("groupinfo");
      
        // Send Message To User
        smtu();
        printTable("messageinfo");
        printTable("messagerecipient");
      
        // Send Message To Group
        smtg();
        printTable("messageinfo");
        printTable("messagerecipient");
        // dispplay new messages
        newMessages();
        // search for user
        searchUser();
        // display top messages
        topMessages();
        // initiate adding group
        initiateGroup();
        printTable("pendinggroupmember");

    }

    public static void setInput(String input){
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    public static void printTable(String tablename){
        System.out.println("\n\n ----------------- Printing Table " + tablename + " -----------------");
        try{
            Connection conn = connect();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from " + tablename);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("---------------------------------------------------\n\n");
    }

    public static Connection connect() throws SQLException 
	{
        return DriverManager.getConnection(url, userDBMS, passwordDBMS);
    }

    public static String Login(String email, String pass){
        String loginnum = "1"+ System.getProperty("line.separator");
        String password = pass+ System.getProperty("line.separator");
        String emailin = email+ System.getProperty("line.separator");
        return loginnum+emailin+password;
    }

    // Profile Creation Test
    public static void TestCreate() throws Exception{
        // Startup
        String startupinput= DBLogin();

        // Create a user
        String createnew = "2"+ System.getProperty("line.separator");
        String username = "TestUser5"+ System.getProperty("line.separator");
        String password = "test123"+ System.getProperty("line.separator");
        String email = "test5@email.com"+ System.getProperty("line.separator");
        String bday = "1997-05-06"+ System.getProperty("line.separator");
        String leave = "5"+ System.getProperty("line.separator");
        String input = startupinput+createnew+username+password+email+bday+leave;
        RunTest(input);
    }

    // Display Messages Test
    public static void TestDisplayMessages() throws Exception{
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        String path = "3\n3\n5\n";
        String input = startup+path;
        RunTest(input);
    }

    public static void TestDisplayFriends() throws Exception{
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        String path = "2\n3\n5\n0\n5\n";
        String input = startup+path;
        RunTest(input);
    }

    public static void TestThreeDegrees() throws Exception{
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        String path = "2\n5\n6\n0\n5\n";
        String input = startup+path;
        RunTest(input);
    }

    public static void TestDropUser() throws Exception{
        String startup = DBLogin() + Login("yaw@pitt.edu", "ypwd");
        String path = "4\n2\n3\n";
        String input = startup+path;
        RunTest(input);
    }

    public static void TestInitiateFriendshipPt1() throws Exception{
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        String path = "2\n1\n3\nInitiate Friendship Test\ny\n5\n";
        String input = startup+path;
        RunTest(input);
    }

    public static void TestInitiateFriendshipPt2() throws Exception{
        String startup = DBLogin() + Login("pdj@pitt.edu", "ppwd");
        String path = "2\n2\n2\n0\n5\n";
        String input = startup+path;
        RunTest(input);
    }

    // DB Login String
    public static String DBLogin(){
        return "postgres" + System.getProperty("line.separator") + "anastasiap05@" + System.getProperty("line.separator");
    }

    // Test Runner
    public static void RunTest(String input) throws Exception{
        setInput(input);
        String[] args = {};
        PittSocial.main(args);
    }
    
    // Create Group Test
    public static void createGroup() throws Exception
    {
    	String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
    	String path = "1\n1\nNEWTESTGROUP\n10\nTHIS IS A TEST GROUP\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }
    
    // Send Message To User
    public static void smtu() throws Exception
    {
    	String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
    	String path = "3\n1\n2\nYes\nTHIS IS A TEST MESSAGE TO USER\nNo\nYes\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }
    
    // Send Message To Group
    public static void smtg() throws Exception
    {
    	String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
    	String path = "3\n2\n1\nYes\nTHIS IS A TEST MESSAGE TO GROUP\nNo\nYes\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }

    //display new messages since login
    public static void newMessages() throws Exception
    {
    	String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
    	String path = "3\n4\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }

    //search database for users with a string for this the string is r, you can change this to whatever you want it to be 
    public static void searchUser() throws Exception
    {
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        String search = "r";
    	String path = "2\n4\n"+search+"\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }

    //get the top messages to and from users with in the specified statement we are viewing top 10 in the past 200 months
    public static void topMessages() throws Exception
    {
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        //10 and 200 are x and k if you want to change them
        String numOfUsers = "10";
        String monthsBack = "200";
    	String path = "3\n5\n"+numOfUsers+"\n"+monthsBack+"\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }

    //adds user to pending group member asking to joing group 1
    public static void initiateGroup() throws Exception
    {
        String startup = DBLogin() + Login("shg@pitt.edu", "shpwd");
        String group = "1";
        String message = "TESTING JOING GROUP ONE WITH DRIVER";
    	String path = "1\n2\n"+group+"\n"+message+"\ny\n5\n";
    	String input = startup+path;
    	RunTest(input);
    }


}