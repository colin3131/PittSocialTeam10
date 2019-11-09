import java.util.Scanner;

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

	/** The main class for PittSocial
	 * @param args - Not used in this application
	 */
	public static void main(String[] args) 
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
	 * It sets login to false and prints the welcome message.
	 */
	private static void startup()
	{
		login = false;
		run = true;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** This method is called first to login or create a user. 
	 * This will log a user in with either their credentials or
	 * with their newly created account. Upon login or creation,
	 * the variable "login" will be set to true
	 */
	private static void login_or_create()
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
				
				boolean userExists = false;
				//userExists = true; // Testing phase so we can get into the server
				if(userExists)
				{
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
			System.out.print("Please enter a username: ");
			String username = kbd2.nextLine();
			System.out.print("Please enter a password: ");
			String password = kbd2.nextLine();
			System.out.print("Please enter an email: ");
			String email = kbd2.nextLine();
			System.out.print("Please enter your birthday: ");
			String birthday = kbd2.nextLine();
			
			login = true;
			System.out.println("");
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
		System.out.println("GROUP OPTIONS:");
		System.out.println("1: Create a Group");
		System.out.println("2: Join a Group");
		System.out.println("3: Back to Main Menu");
		
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
		System.out.println("USER OPTIONS:");
		System.out.println("1: Send Friend Request");
		System.out.println("2: Confirm/Deny Friend and Group Requests");
		System.out.println("3: Display Friends");
		System.out.println("4: Search For User");
		System.out.println("5: Three Degress Command");
		System.out.println("6: Back to Main Menu");
		
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
		System.out.println("MESSAGE OPTIONS");
		System.out.println("1: Send a Message to a User");
		System.out.println("2: Send a Message to a Group");
		System.out.println("3: Display Messages");
		System.out.println("4: Display New Messages");
		System.out.println("5: Display Top Messages");
		System.out.println("6: Back to Main Menu");
		
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
		System.out.println("ACCOUNT OPTIONS:");
		System.out.println("1: Logout");
		System.out.println("2: Delete Account");
		System.out.println("3: Back to Main Menu");
		
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
	
	/** This method will send a friend request from the current user 
	 * to a user of their choice
	 */
	private static void initiateFriendship()
	{
		
	}
	
	/** This method will create a group, and make the creator the first
	 * member of that group
	 */
	private static void createGroup()
	{
		
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
		
	}
	
	/** This method will send a message to a user from the current user
	 * 
	 */
	private static void sendMessageToUser()
	{
		
	}
	
	/** This method will send a message to a group from the current user
	 * 
	 */
	private static void sendMessageToGroup()
	{
		
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

}
