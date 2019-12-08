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
    public static String passwordDBMS = "Smoke7889";
    public static void main(String[] args) throws Exception
    {
        // Test Creation
        TestCreate();
        printTable("profile");

        // Display Messages
        TestDisplayMessages();
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
    }

    public static void setInput(String input){
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    public static void printTable(String tablename){
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

    // DB Login String
    public static String DBLogin(){
        return "postgres" + System.getProperty("line.separator") + "Smoke7889" + System.getProperty("line.separator");
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
}