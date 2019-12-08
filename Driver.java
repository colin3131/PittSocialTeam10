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
    public static String passwordDBMS = "admin";
    public static void main(String[] args) throws Exception
    {
        // Startup
        String startupinput= "postgres\nadmin\n";

        // Create a user
        String createnew = "2\n";
        String username = "TestUser\n";
        String password = "test123\n";
        String email = "test@email.com\n";
        String bday = "1997-05-06\n";
        String leave = "5\n";
        String input = startupinput+createnew+username+password+email+bday+leave;
        setInput(input);
        PittSocial.main(args);
        printTable("profile");
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
}