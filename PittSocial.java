import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/** This is the PittSocial 1555 Application
 * 
 * GROUP #: 10
 * MEMBERS: JAD285, [FILL IN], [FILL IN]
 */
public class PittSocial 
{
	private static String initialize;
	private static boolean login;
	private static String username;
	private static boolean run;
	private static String url;
	private static String userDBMS;
	private static String passwordDBMS;
	private static int userID;

	/** The main class for PittSocial
	 * @param args - Not used in this application
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		startup();
		while(run)
		{
			login_or_create();
			user_application();
		}
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////Below are Methods for Use in Main////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** This methods sets up the PittSocial program for use.
	 * It sets login to false, connects to the DBMS, and prints the welcome message.
	 */
	private static void startup()
	{
		login = false;
		run = true;
		boolean Connection = false;
		while(!Connection)
		{
			Scanner kbd = new Scanner(System.in);
			System.out.print("Enter your DBMS username: ");
			String DBuser = kbd.nextLine();
			System.out.print("Enter your DBMS password: ");
			String DBpass = kbd.nextLine();
			
			// auto close connection
	        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", DBuser, DBpass)) 
	        {
	            if (conn != null) 
	            {
	                System.out.println("Connected to the database!");
	                Connection = true;
	                userDBMS = DBuser;
	                passwordDBMS = DBpass;
	                url = "jdbc:postgresql://localhost:5432/";
	            } else {
	                System.out.println("Failed to make connection!");
	            }
	
	        } catch (SQLException e) {
	            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
	            System.out.println("");
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("");
	        }
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** This method is called first to login or create a user. 
	 * This will log a user in with either their credentials or
	 * with their newly created account. Upon login or creation,
	 * the variable "login" will be set to true
	 * @throws Exception 
	 */
	private static void login_or_create() throws Exception
	{
		System.out.println("---- Welcome to PittSocial! ----");
		System.out.println("");
		Scanner kbd = new Scanner(System.in);
		
		// THE FOLLOWING IS THE MENU FOR LOGIN, CREATE USER, OR EXIT PROGRAM
		boolean cANDs = false;
		while(!cANDs)
		{
			System.out.println("Would you like to:");
			System.out.println("1: Login");
			System.out.println("2: Create New Account");
			System.out.println("3: Exit");
			System.out.println("");
			System.out.print("--> ");
			String answer = kbd.nextLine();
			
			System.out.println("");
			if(answer.equals("1"))
			{
				initialize = answer;
				cANDs = true;
				
			}
			else if (answer.equals("2"))
			{
				initialize = answer;
				cANDs = true;
			}
			else if(answer.equals("3"))
			{
				initialize = answer;
				cANDs = true;
			}
			else
			{
				System.out.println("Invalid Option... Please input a valid number choice");
				System.out.println("");
			}
		}
		
		// THE FOLLOWING HANDLES THEIR ANSWER TO THE MENU PHASE
		// The Login Process
		if(initialize.equals("1"))
		{
			boolean tryLogin = true;
			while(tryLogin)
			{
				Scanner kbd2 = new Scanner(System.in);
				System.out.print("Please enter your username: ");
				String input1 = kbd2.nextLine();
				System.out.print("Please enter your password: ");
				String input2 = kbd2.nextLine();
				
				boolean userExists = loginRequest(input1, input2);
				//userExists = true; // Testing phase so we can get into the server
				if(userExists)
				{
					System.out.println("Login Success!");
					System.out.println("");
					username = input1;
					// Check to see if the user exists
					login = true;
					tryLogin = false;
				}
				else
				{
					System.out.println("Username + Password combination does not exist...");
					System.out.println("Would you like to: ");
					System.out.println("1: Try Again");
					System.out.println("2: Go Back to Main Page");
					System.out.println("");
					System.out.print("--> ");
					
					String input3 = kbd2.nextLine();
					System.out.println("");
					if(input3.equals("1"))
					{
						login = false;
						tryLogin = true;
					}
					else
					{
						login = false;
						tryLogin = false;
					}
				}
				
			}
		}
		// The create user process
		else if(initialize.equals("2"))
		{
			Scanner kbd2 = new Scanner(System.in);
			System.out.print("Please enter a username [first last]: ");
			String usernameInput = kbd2.nextLine();
			System.out.print("Please enter a password: ");
			String password = kbd2.nextLine();
			System.out.print("Please enter an email [something@something.$$$: ");
			String email = kbd2.nextLine();
			System.out.print("Please enter your birthday [YYYY-MM-DD]: ");
			String birthday = kbd2.nextLine();
			
			boolean created = createUser(usernameInput, password, email, birthday);
			if(created)
			{
				username = usernameInput;
				login = true;
				System.out.println("");
			}
			else 
			{
				System.out.println("Creation Failed");
				login = false;
			}
		}
		// Exiting the application
		else
		{
			login = false;
			run = false;
			System.out.println("Exiting program");
		}
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** This is the main application after login. 
	 * This will call other methods as well
	 */
	private static void user_application()
	{
		while(login)
		{
			main_menu();
		}
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////Methods to Call in user_application//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////Menu Navigations///////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void main_menu()
	{
		System.out.println("MAIN MENU:");
		System.out.println("1: Group Options");
		System.out.println("2: User Options");
		System.out.println("3: Message Options");
		System.out.println("4: Account Options");
		System.out.println("5: Exit");
		System.out.println("");
		System.out.print("--> ");
		
		Scanner kbd = new Scanner(System.in);
		String input = kbd.nextLine();
		if(input.equals("1"))
		{
			group_options();
		}
		else if(input.equals("2"))
		{
			user_options();
		}
		else if(input.equals("3"))
		{
			message_options();
		}
		else if(input.equals("4"))
		{
			account_options();
		}
		else if(input.equals("5"))
		{
			exit();
		}
		else
		{
			System.out.println("Please input a valid menu option");
			System.out.println("");
		}
		
	}
	private static void group_options()
	{
		System.out.println("");
		System.out.println("GROUP OPTIONS:");
		System.out.println("1: Create a Group");
		System.out.println("2: Join a Group");
		System.out.println("3: Back to Main Menu");
		System.out.println("");
		System.out.print("--> ");
		
		Scanner kbd = new Scanner(System.in);
		String input = kbd.nextLine();
		if(input.equals("1"))
		{
			createGroup();
		}
		else if(input.equals("2"))
		{
			initiateAddingGroup();
		}
		else
		{
			System.out.println("Backtracking...");
			System.out.println("");
		}
	}
	private static void user_options()
	{
		System.out.println("");
		System.out.println("USER OPTIONS:");
		System.out.println("1: Send Friend Request");
		System.out.println("2: Confirm/Deny Friend and Group Requests");
		System.out.println("3: Display Friends");
		System.out.println("4: Search For User");
		System.out.println("5: Three Degress Command");
		System.out.println("6: Back to Main Menu");
		System.out.println("");
		System.out.print("--> ");
		
		Scanner kbd = new Scanner(System.in);
		String input = kbd.nextLine();
		if(input.equals("1"))
		{
			initiateFriendship();
		}
		else if(input.equals("2"))
		{
			confirmRequests();
		}
		else if(input.equals("3"))
		{
			displayFriends();
		}
		else if(input.equals("4"))
		{
			searchForUser();
		}
		else if(input.equals("5"))
		{
			threeDegress();
		}
		else
		{
			System.out.println("Backtracking...");
			System.out.println("");
		}
	}
	private static void message_options()
	{
		System.out.println("");
		System.out.println("MESSAGE OPTIONS");
		System.out.println("1: Send a Message to a User");
		System.out.println("2: Send a Message to a Group");
		System.out.println("3: Display Messages");
		System.out.println("4: Display New Messages");
		System.out.println("5: Display Top Messages");
		System.out.println("6: Back to Main Menu");
		System.out.println("");
		System.out.print("--> ");
		
		Scanner kbd = new Scanner(System.in);
		String input = kbd.nextLine();
		if(input.equals("1"))
		{
			sendMessageToUser();
		}
		else if(input.equals("2"))
		{
			sendMessageToGroup();
		}
		else if(input.equals("3"))
		{
			displayMessages();
		}
		else if(input.equals("4"))
		{
			displayNewMessages();
		}
		else if(input.equals("5"))
		{
			topMessages();
		}
		else
		{
			System.out.println("Backtracking...");
			System.out.println("");
		}
	}
	private static void account_options()
	{
		System.out.println("");
		System.out.println("ACCOUNT OPTIONS:");
		System.out.println("1: Logout");
		System.out.println("2: Delete Account");
		System.out.println("3: Back to Main Menu");
		System.out.println("");
		System.out.print("--> ");
		
		Scanner kbd = new Scanner(System.in);
		String input = kbd.nextLine();
		if(input.equals("1"))
		{
			logout();
		}
		else if(input.equals("2"))
		{
			dropUser();
		}
		else
		{
			System.out.println("Backtracking...");
			System.out.println("");
		}
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////Methods to Call in user_application//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////MAIN METHODS/////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** This method will login the user given the input of the username and password
	 * This will return a boolean of true if the user + password combo is correct
	 * 
	 * @param username - the first and last name of the user
	 * @param password - the password of the user
	 * @return boolean - true if logged in false otherwise
	 */
	private static boolean loginRequest(String username, String password)
	{
		boolean exists = false;
		String SQL = "SELECT userid, name, password FROM profile";
		
		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) {
            // look for combo
            exists = lookForUser(rs, username, password);
            return exists;
		}
		catch(Exception e)
		{
			System.out.println("there was a prob");
			return exists;
		}
	}
	
	/** This method is a companion to the loginRequest method
	 * this does the actual search for the user/password
	 * 
	 * @param rs - the result set 
	 * @param username - the first and last name of the user
	 * @param password - the password of the user
	 * @return boolean - true if found, false otherwise
	 * @throws SQLException
	 */
	private static boolean lookForUser(ResultSet rs, String username, String password) throws SQLException
	{
		boolean exists = false;
		while(rs.next())
		{
			int tempID = rs.getInt("userid");
			String tempName = rs.getString("name");
			String tempPass = rs.getString("password");
			if(username.equals(tempName) && password.equals(tempPass))
			{
				userID = tempID;
				exists = true;
				return exists;
			}
		}
		return exists;
	}
	
	/** This method creates a new user and inputs them into the DBMS, returning a true 
	 * if everything completes correctly
	 * 
	 * @param username - the username of the new user (first last)
	 * @param password - the password of the new user
	 * @param email - the email of the new user
	 * @param birthday - the birthday (NOTE OF FORM - "2015-01-01")
	 * @return
	 * @throws ParseException 
	 */
	private static boolean createUser(String username, String password, String email, String birthday) throws Exception
	{
		boolean created = false;
		String SQL = "INSERT INTO profile(userid, name, email, password, date_of_birth, lastlogin) " + "VALUES(?, ?, ?, ?, ?, ?)";
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		try
		{
			Date bday = Date.valueOf(birthday);
			int UID = getNextID();
			try (Connection conn = connect();
	                PreparedStatement pstmt = conn.prepareStatement(SQL)) 
			{
				pstmt.setInt(1, UID);
	            pstmt.setString(2, username);
	            pstmt.setString(3, email);
	            pstmt.setString(4, password);
	            pstmt.setDate(5, bday);
	            pstmt.setTimestamp(6, ts);
	 
				pstmt.executeUpdate();
				
				userID = UID;
	            
	            created = true;
	            return created;
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				return created;
			}
		}
		catch(Exception g)
		{
			System.out.println("Incorrect Date Format");
			return false;
		}
	}
	
	/** This method gets the next userID for the primary key of creating a user
	 * 
	 * @return int - the next userID to use
	 */
	private static int getNextID() throws Exception
	{
		int id = 0;
		String SQL = "SELECT userid FROM profile ORDER BY userid DESC";
		
		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				id = rs.getInt("userid");
				id++;
				return id;
			}
			return id;
		}
	}
	
	/** This method will send a friend request from the current user 
	 * to a user of their choice
	 */
	private static void initiateFriendship()
	{
		Scanner sc = new Scanner(System.in);

		// notValid: Keep the loop running while the input is invalid
		// requestNotSent: Keep the loop running while no request was sent
		boolean requestNotSent = true;
		while(requestNotSent){
			System.out.print("Please enter the User ID to request: ");
			if(sc.hasNextInt()){
				int toUserID = sc.nextInt();
				sc.nextLine(); //Gotta consume the rest of this line
				System.out.print("\nPlease enter a message to send with the request\n>");
				String message = sc.nextLine();
				System.out.println();

				// Pull all of the user's info
				try{
					Map user = getUserInfo(toUserID);
					System.out.print("Are you sure you'd like to add "+user.get("name")+"?\n(y/n): ");
					if(sc.nextLine().equalsIgnoreCase("y")){

						//We have all the vars, construct our insert
						try{
							String SQL = "INSERT INTO pendingFriend VALUES(?, ?, ?)";
							Connection conn = connect();
							PreparedStatement pstmt = conn.prepareStatement(SQL);
							pstmt.setInt(1, userID);
							pstmt.setInt(2, toUserID);
							pstmt.setString(3, message);
							pstmt.executeUpdate();

							// If we get here, request was sent.
							requestNotSent = false;
						}
						catch(Exception e){
							System.out.println(e.getMessage());
							break;
						}
					}
					else{
						break;
					}
				} catch(Exception e){
					System.out.println(e.getMessage());
					System.out.println("\nPlease enter a valid User ID.\n");
				}
			}
			else{ // If the input isn't an integer, make 'em retry
				System.out.println("\nPlease enter a valid User ID.\n");
			}
		}

		// Check if the request was actually sent, print success/failure
		if(requestNotSent){
			System.out.println("Friend Request was not sent.\n");
		}
		else{
			System.out.println("Friend Request sent successfully.\n");
		}
		//sc.close();
	}

	/** This method will pull all of the user info given a User ID
	 * @param UID - The user ID of the User to grab
	 * @return HashMap - returns a bunch of key-value pairs of user info
	 */
	private static Map<String, Object> getUserInfo(int UID) throws Exception
	{
		Map user = new HashMap<String, Object>();
		String SQL = "SELECT * FROM profile WHERE userid=" + UID + "";

		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				user.put("userid", rs.getInt("userid"));
				user.put("name", rs.getString("name"));
				user.put("email", rs.getString("email"));
				user.put("date_of_birth", rs.getDate("date_of_birth"));
				user.put("lastlogin", rs.getTimestamp("lastlogin"));
			}
			return user;
		}
	}

	/** This method will pull all of the group info given a Group ID
	 * @param UID - The group ID of the Group to grab
	 * @return HashMap - returns a bunch of key-value pairs of group info
	 */
	private static Map<String, Object> getGroupInfo(int GID) throws Exception
	{
		Map group = new HashMap<String, Object>();
		String SQL = "SELECT * FROM groupInfo WHERE gid=" + GID + "";

		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				group.put("gid", rs.getInt("gid"));
				group.put("name", rs.getString("name"));
				group.put("size", rs.getInt("size"));
				group.put("description", rs.getString("description"));
			}
			return group;
		}
	}


	/** This method will create a group, and make the creator the first
	 * member of that group
	 */
	private static void createGroup()
	{
		Scanner CGkbd = new Scanner(System.in);
		System.out.print("Please enter a name for your group: ");
		String groupName = CGkbd.nextLine();
		System.out.print("Please enter a limit for the group [max # of people]: ");
		String limitString = CGkbd.nextLine();
		int limit = 100;
		try
		{
			limit = Integer.parseInt(limitString);
		}
		catch(Exception l)
		{
			System.out.println("Invalid number... Defaulting to size of 100");
			limit = 100;
		}
		System.out.print("Please enter a description of the group: ");
		String description = CGkbd.nextLine();
		
		boolean createdGroup = createGroupSub(groupName, limit, description);
		
		if(createdGroup)
		{
			System.out.println("Group Successfully Created!");
		}
		else
		{
			System.out.println("Group Failed to be Created");
		}
	}
	
	/** This method will be called in createGroup to run the SQL command
	 * to actually insert the group being created.
	 * 
	 * @param groupName - the name of the group
	 * @param limit - the limit of members allowed
	 * @param description - description of the group
	 * @return boolean - true if created, false otherwise
	 */
	private static boolean createGroupSub(String groupName, int limit, String description)
	{
		boolean success = false;
		String SQL = "INSERT INTO groupinfo(gid, name, size, description) " + "VALUES(?, ?, ?, ?)";
		
		try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(SQL)) 
		{
			int GID = getNextGID();
			pstmt.setInt(1, GID);
            pstmt.setString(2, groupName);
            pstmt.setInt(3, limit);
            pstmt.setString(4, description);
 
            pstmt.executeUpdate();
            addGroupCreator(userID, GID);
            
            success = true;
            return success;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return success;
		}
	}
	
	/** This method gets the next GroupID for the primary key of creating a Group
	 * 
	 * @return int - the next userID to use
	 */
	private static int getNextGID() throws Exception
	{
		int id = 0;
		String SQL = "SELECT gid FROM groupinfo ORDER BY gid DESC";
		
		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				id = rs.getInt("gid");
				id++;
				return id;
			}
			return id;
		}
	}
	
	/** This method adds a user to a group
	 * 
	 * @param UID - User ID to add 
	 * @param groupID - Group to add the UID to
	 */
	private static void addGroupCreator(int UID, int groupID)
	{
		String SQL = "INSERT INTO groupmember(gid, userid, role) " + "VALUES(?, ?, ?)";
		
		try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(SQL)) 
		{
			pstmt.setInt(1, groupID);
            pstmt.setInt(2, UID);
            pstmt.setString(3, "manager");
 
            pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/** This method will send a pendingGroupRequest from the current user
	 * to the specified group
	 */
	private static void initiateAddingGroup()
	{
		
	}
	
	/** This method will list all of the requests (friend and group [if group creator])
	 * and the user will be able to selectively accept or deny requests
	 */
	private static void confirmRequests()
	{
		boolean chooseMore = true;
		try{
			while(chooseMore){
				// Find all the requests.
				ArrayList<HashMap> groupReqs = getGroupRequests(userID);
				ArrayList<HashMap> friendReqs = getFriendRequests(userID);
				int groupSize = groupReqs.size();
				int friendSize = friendReqs.size();

				System.out.println("\nOutstanding Friend and Group Requests");
				System.out.println(" 0. Deny all requests and return to main.");
				for(int i = 0; i < (groupSize + friendSize); i++){
					if(i < groupSize){
						// Get the request details to print
						HashMap<String, Object> grouprequest = groupReqs.get(i);
						String reqmessage = grouprequest.get("message").toString();
						Map<String, Object> reqgroup = getGroupInfo((int)grouprequest.get("gid"));
						String reqgn = reqgroup.get("name").toString();
						Map<String, Object> requser = getUserInfo((int)grouprequest.get("userid"));
						String requn = requser.get("name").toString();

						// Print the request
						System.out.println((i+1) + ". " + requn + " is requesting to join " + reqgn + ": " + reqmessage);
					}
					else{
						// Get the request details to print
						HashMap<String, Object> userrequest = friendReqs.get(i - groupSize);
						String reqmessage = userrequest.get("message").toString();
						Map<String, Object> requser = getUserInfo((int)userrequest.get("fromid"));
						String requn = requser.get("name").toString();
						System.out.println((i+1) + ". " + requn + " wants to be your friend: " + reqmessage);
					}
				}
				
				// Allow user to choose an option
				Scanner sc = new Scanner(System.in);
				System.out.print("\nChoose a request to accept: ");
				if(sc.hasNextInt()){
					// Grab the choice
					int choice = sc.nextInt();
					sc.nextLine();

					if(choice == 0){ // Deny all requests
						denyAllRequests(userID);
						chooseMore = false;
					}else if(choice <= groupSize){ // Accept a group join request
						choice--;
						HashMap<String, Object> grouprequest = groupReqs.get(choice);
						int gID = (int)grouprequest.get("gid");
						int userID = (int)grouprequest.get("userid");
						boolean successful = confirmGroupMember(gID, userID);
						if(successful){
							System.out.println("\nSuccessfully added user to group.\n");
						}else{
							System.out.println("\nAccepting group join request failed.\n");
						}
					}else if(choice <= (groupSize + friendSize)){
						choice -= (groupSize + 1);
						HashMap<String, Object> userrequest = friendReqs.get(choice);
						int toID = userID;
						int fromID = (int)userrequest.get("fromid");
						String message = userrequest.get("message").toString();
						boolean successful = confirmFriend(toID, fromID, message);
						if(successful){
							System.out.println("\nSuccessfully added friend.\n");
						}else{
							System.out.println("\nAccepting friend request failed.\n");
						}
					}
				}
			}
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	/** This method will confirm a friend request, adding the two friends and 
	 * deleting the pendingFriend entry
	 * @param toID - The recipient of the friend request
	 * @param fromID - The sender of the friend request
	 * @param message - The message of the friend request
	 * @return boolean - Return success or failure
	 */
	private static boolean confirmFriend(int toID, int fromID, String message)
	{
		String SQL = "INSERT INTO friend(userid1, userid2, jdate, message) " + "VALUES(?, ?, ?, ?)";
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		try{
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, toID);
			pstmt.setInt(2, fromID);
			pstmt.setDate(3, Date.valueOf(LocalDate.now()));
			pstmt.setString(4, message);
			pstmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}

	/** This method will deny a friend request, deleting it from the db
	 * 
	 * @param toID - the toID of the request
	 * @param fromID - the fromID of the request
	 * @return boolean - Return success or failure
	 */
	private static boolean denyFriend(int toID, int fromID)
	{
		String SQL = "DELETE FROM pendingfriend WHERE fromid=? AND toid=?";
		try{
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, fromID);
			pstmt.setInt(2, toID);
			pstmt.executeUpdate();
			return true;
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}

	/** This method will confirm a group member request, adding the member and 
	 * deleting the pendingGroupMember entry
	 * @param gID - The group that the user is joining
	 * @param userID - The user that is joining
	 * @return boolean - Return success or failure
	 */
	private static boolean confirmGroupMember(int gID, int uid)
	{
		String SQL = "INSERT INTO groupmember(gid, userid, role) " + "VALUES(?, ?, ?)";
		try{
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, gID);
			pstmt.setInt(2, uid);
			pstmt.setString(3, "member");
			pstmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}

	/** This method will deny a group member request, removing it from the db 
	 * 
	 * @param gID - The group that the user is trying to join
	 * @param userID - The user that is trying to join
	 * @return boolean - Return success or failure
	 */
	private static boolean denyGroupMember(int gID, int uid)
	{
		String SQL = "DELETE FROM pendinggroupmember WHERE gid=? AND userid=?";
		try{
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, gID);
			pstmt.setInt(2, uid);
			pstmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}

	private static void denyAllRequests(int UID)
	{
		// First, get and delete all friend requests
		try{
			for(HashMap<String, Object> friendreq : getFriendRequests(UID)){
				int fromID = (int)friendreq.get("fromid");
				boolean success = denyFriend(UID, fromID);
				if(success){
					System.out.println("Successfully denied friend request from " + getUserInfo(fromID).get("name").toString());
				}else{
					System.out.println("Failed to deny friend request from " + getUserInfo(fromID).get("name").toString());
				}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

		// Then, get and delete all group join requests
		try{
			for(HashMap<String, Object> groupreq : getGroupRequests(UID)){
				int gid = (int)groupreq.get("gid");
				int uid = (int)groupreq.get("userid");
				boolean success = denyGroupMember(gid, uid);
				if(success){
					System.out.println("Successfully denied group join request from " + getUserInfo(uid).get("name").toString());
				}else{
					System.out.println("Failed to denied group join request from " + getUserInfo(uid).get("name").toString());
				}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param UID - The user who's requests we are querying for
	 * @return ArrayList - A list of group requests (gid, userid, message)
	 */
	private static ArrayList<HashMap> getGroupRequests(int UID) throws Exception
	{
		ArrayList<HashMap> grouprequests = new ArrayList<HashMap>();
		for(int gid : getManagerGroups(UID)){
			String SQL = "SELECT userid, message FROM pendinggroupmember WHERE gid=" + gid + "";
			try(
				Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SQL)
			){
				while(rs.next()){
					HashMap request = new HashMap<String, Object>();
					request.put("gid", gid);
					request.put("userid", rs.getInt("userid"));
					request.put("message", rs.getString("message"));
					grouprequests.add(request);
				}
			}
		}
		return grouprequests;
	}

	/**
	 * 
	 * @param UID - The user who's requests we are querying for
	 * @return ArrayList - A list of group requests (fromid, message)
	 */
	private static ArrayList<HashMap> getFriendRequests(int UID) throws Exception
	{
		ArrayList<HashMap> friendrequests = new ArrayList<HashMap>();
		String SQL = "SELECT fromid, message FROM pendingfriend WHERE toid=" + UID + "";
		try(
			Connection conn = connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL)
		){
			while(rs.next()){
				HashMap request = new HashMap<String, Object>();
				request.put("fromid", rs.getInt("fromid"));
				request.put("message", rs.getString("message"));
				friendrequests.add(request);
			}
		}
		return friendrequests;
	}

	/** This method looks for groups that the given user is the Manager of.
	 * 
	 * @param userID - The user who created the groups
	 * @return ArrayList - A list of Group ID's where userID is the creator
	 */
	private static ArrayList<Integer> getManagerGroups(int UID) throws Exception
	{
		String SQL = "SELECT gid FROM groupmember WHERE userid=" + UID + " AND role='manager'";
		ArrayList<Integer> groups = new ArrayList<Integer>();
		try(
			Connection conn = connect();
        	Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL)
		){
			while(rs.next()){
				groups.add(rs.getInt("gid"));
			}
			return groups;
		}
	}
	
	/** This method will send a message to a user from the current user
	 * 
	 */
	private static void sendMessageToUser()
	{
		boolean valid = true;
		int UIDtosend = 0;
		Scanner SMTUkbd = new Scanner(System.in);
		System.out.print("To whom would you like to send a message [User ID]: ");
		String UIDSend = SMTUkbd.nextLine();
		
		try
		{
			UIDtosend = Integer.parseInt(UIDSend);
			String username = getUserName(UIDtosend);
			if(username.equals("none"))
			{
				System.out.println("Invalid User ID [Does Not Exist]");
				valid = false;
			}
			else
			{
				System.out.print("Sending message to " + username + ": is this correct [Yes/No]? ");
				String sendornaw = SMTUkbd.nextLine();
				if(sendornaw.equals("Yes"))
				{
					valid = true;
				}
				else
				{
					valid = false;
					System.out.println("Message canceled");
				}
			}
		}
		catch(Exception l)
		{
			System.out.println("Invalid User ID [Must be a number]");
			valid = false;
		}
		
		if(valid)
		{
			System.out.print("Please enter the message to send: ");
			String message = SMTUkbd.nextLine();
			boolean continueMessage = true;
			while(continueMessage)
			{
				System.out.println("Continue typing message? [Yes/No]");
				String answer = SMTUkbd.nextLine();
				if(answer.equals("Yes"))
				{
					System.out.println("Please enter the next part of the message: ");
					String input = SMTUkbd.nextLine();
					message = message.concat(" ");
					message = message.concat(input);
				}
				else
				{
					continueMessage = false;
				}
			}
			
			System.out.println("Your message is as follows: " + message);
			System.out.print("Do you want to send this message [Yes/No]?: ");
			String input2 = SMTUkbd.nextLine();
			if(input2.equals("Yes"))
			{
				sendMessageToUserSub(UIDtosend, message);
			}
			else
			{
				System.out.println("You canceled the message.");
			}
		}
		System.out.println("");
	}
	
	/** This is a helper method to sendMessageToUser. This will actually add the message 
	 * to the DBMS
	 * 
	 * @param UIDtosend - user ID to send the message to
	 * @param message - the message to send
	 */
	private static void sendMessageToUserSub(int UIDtosend, String message)
	{
		String SQL = "INSERT INTO messageinfo(msgid, fromid, message, touserid, timesent) " + "VALUES(?, ?, ?, ?, ?)";
		
		try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(SQL)) 
		{
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			int MID = getNextMID();
			pstmt.setInt(1, MID);
            pstmt.setInt(2, userID);
            pstmt.setString(3, message);
            pstmt.setInt(4, UIDtosend);
            pstmt.setTimestamp(5, ts);
 
            pstmt.executeUpdate();
            System.out.println("Message sent!");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error in sending message");
		}
	}
	
	/** This method gets the next Message ID for the primary key of creating a Message
	 * 
	 * @return int - the next MID to use
	 */
	private static int getNextMID() throws Exception
	{
		int id = 0;
		String SQL = "SELECT msgid FROM messageinfo ORDER BY msgid DESC";
		
		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				id = rs.getInt("msgid");
				id++;
				return id;
			}
			return id;
		}
	}
	
	/** This method gets a username linked to an input User ID
	 * 
	 * @param userID - User ID to retrieve the username of
	 * @return String - the username of the user
	 * @throws Exception
	 */
	private static String getUserName(int userID) throws Exception
	{
		String username = "none";
		String SQL = "SELECT name FROM profile WHERE userid=" + userID + "";

		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				username = rs.getString("name");
			}
			
			return username;
		}
	}
	
	/** This method will send a message to a group from the current user
	 * 
	 */
	private static void sendMessageToGroup()
	{
		boolean valid = true;
		int GIDtosend = 0;
		Scanner SMTGkbd = new Scanner(System.in);
		System.out.print("To what group would you like to send a message [Group ID]: ");
		String GIDSend = SMTGkbd.nextLine();
		
		try
		{
			GIDtosend = Integer.parseInt(GIDSend);
			String groupname = getGroupName(GIDtosend);
			
			if(groupname.equals("none"))
			{
				System.out.println("Invalid Group ID [Does Not Exist]");
				valid = false;
			}
			else
			{
				boolean inGroup = userInGroup(GIDtosend, userID);
				
				if(inGroup)
				{
					System.out.print("Sending message to " + groupname + ": is this correct [Yes/No]? ");
					String sendornaw = SMTGkbd.nextLine();
					if(sendornaw.equals("Yes"))
					{
						valid = true;
					}
					else
					{
						valid = false;
						System.out.println("Message canceled");
					}
				}
				else
				{
					System.out.println("Invalid Credentials [Not in Group]");
					valid = false;
				}
			}
		}
		catch(Exception l)
		{
			System.out.println("Invalid Group ID [Must be a number]");
			valid = false;
		}
		
		if(valid)
		{
			System.out.print("Please enter the message to send: ");
			String message = SMTGkbd.nextLine();
			boolean continueMessage = true;
			while(continueMessage)
			{
				System.out.println("Continue typing message? [Yes/No]");
				String answer = SMTGkbd.nextLine();
				if(answer.equals("Yes"))
				{
					System.out.print("Please enter the next part of the message: ");
					String input = SMTGkbd.nextLine();
					message = message.concat(" ");
					message = message.concat(input);
				}
				else
				{
					continueMessage = false;
				}
			}
			
			System.out.println("Your message is as follows: " + message);
			System.out.print("Do you want to send this message [Yes/No]?: ");
			String input2 = SMTGkbd.nextLine();
			if(input2.equals("Yes"))
			{
				sendMessageToGroupSub(GIDtosend, message);
			}
			else
			{
				System.out.println("You canceled the message.");
			}
		}
		System.out.println("");
	}
	
	/** This method checks if a user is actually part of a given group
	 * 
	 * @param GID - group ID to check
	 * @param UID - user ID to check
	 * @return boolean - true if the UID is in the GID, false otherwise
	 * @throws Exception
	 */
	private static boolean userInGroup(int GID, int UID) throws Exception
	{
		boolean inGroup = false;
		String role = "none";
		
		String SQL = "SELECT role FROM groupmember WHERE gid=" + GID + " AND userid=" + UID +"";
		
		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				role = rs.getString("role");
			}
			
			if(role.equals("none"))
			{
				inGroup = false;
				return inGroup;
			}
			else
			{
				inGroup = true;
				return inGroup;
			}
		}
	}
	
	/** This method is a sub method to the sendMessageToGroup method
	 * This will actually input the message into the messageinfo table 
	 * in the corresponding DBMS
	 * 
	 * @param GIDtosend - Group ID to send the message to
	 * @param message - the message to send
	 */
	private static void sendMessageToGroupSub(int GIDtosend, String message)
	{
		String SQL = "INSERT INTO messageinfo(msgid, fromid, message, togroupid, timesent) " + "VALUES(?, ?, ?, ?, ?)";
		
		try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(SQL)) 
		{
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			int MID = getNextMID();
			pstmt.setInt(1, MID);
            pstmt.setInt(2, userID);
            pstmt.setString(3, message);
            pstmt.setInt(4, GIDtosend);
            pstmt.setTimestamp(5, ts);
 
            pstmt.executeUpdate();
            System.out.println("Message sent!");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error in sending message");
		}
	}
	
	/** This method returns a group name given a group ID
	 * 
	 * @param groupID - group ID to get the name of
	 * @return String - name of the group
	 * @throws Exception
	 */
	private static String getGroupName(int groupID)
	{
		String username = "none";
		String SQL = "SELECT name FROM groupinfo WHERE gid=" + groupID + "";

		try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) 
		{
			while(rs.next())
			{
				username = rs.getString("name");
			}
			
			return username;
		}
		catch(Exception l)
		{
			System.out.println("Getting the group name failed");
			return username;
		}
	}
	
	/** This method will display all messages received by the current user
	 * 
	 */
	private static void displayMessages()
	{
		
	}
	
	/** This method will display all NEW messages received by the current user
	 * NOTE: NEW is defined as all messages received after the past login time,
	 * not necessarily all messages that have not been read
	 */
	private static void displayNewMessages()
	{
		
	}
	
	/** This method will list all of the current user's friends
	 * 
	 */
	private static void displayFriends()
	{
		
	}
	
	/** This method will search for a specific user, and return whether that 
	 * user exists
	 */
	private static void searchForUser()
	{
		
	}
	
	/** Given a userID, find a path, if one exists, between the logged-in user and 
	 * that user with at most 3 hop between them 
	 * NOTE: A hop is defined as a friendship between any two users
	 */
	private static void threeDegress()
	{
		
	}
	
	/** Display the top k users with respect to the number of messages sent to the 
	 * logged-in user plus the number of messages received from the logged-in user 
	 * in the past x months
	 */
	private static void topMessages()
	{
		
	}
	
	/** This method will log a user out, sending you back to the 
	 * login screen
	 */
	private static void logout()
	{
		login = false;
	}
	
	/** This method will delete the current user from existence, afterwards 
	 * sending you back to the start-up menu
	 */
	private static void dropUser()
	{
		
	}
	
	/** This method will exit the application and display
	 * an exit message
	 */
	private static void exit()
	{
		login = false;
		run = false;
		System.out.println("Thanks for using PittSocial!");
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////Methods to Call in user_application//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////DBMS METHODS/////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Connection connect() throws SQLException 
	{
        return DriverManager.getConnection(url, userDBMS, passwordDBMS);
    }
}