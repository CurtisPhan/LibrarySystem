package librarysystem;

import java.awt.*;
import java.applet.*;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 *	@author 		Curtis Phan
 *	@course			ICS-4U0-A
 *	@instructor 	Mr. A. Sayed 
 *	@date 			October 26, 2015
 *
 *	Purpose: This program simulates a library system. The system keeps track of all the books and users in the system and can display 
 *					all of them on screen with a GUI. It is able to create, delete and search for users and books. The system can also check out 
 *					and return books. It can also look up all the books a user has checked out and compare  two books so librarian can offer 
 *					advice on which book is better.  
 *
 *	Known Bugs: - user/book fields that are String type (e.g. title, author name, user name) can break out of their position in the tables
 *							  display list of users/books if they are too long
 *									- could be fixed by putting a character limit on these fields or programming in word wrapping
 *							- the varying length of user/book fields that are String type throws off the centering of text on the result screens
 *									- could be fixed by programming in a centering algorithm for the result screen
 *							- comments are displayed differently depending on computer/settings so formatting will likely be off
 */
public class LibraryGUI extends Applet implements ActionListener {

	private final int MAX_OBJECTS_ONSCREEN=13;			//the number of users/books able to be displayed on 1 page of a list

	int numOfPages;												//the number of pages the current list takes up
	int pageNum;														//the page number of the list the program is presently displaying
	int booksSelected=0;										//the number of books already selected for comparison in compare function

	double totalFine;												//the total overdue fines a user gets added to their balance when returning a book

	private final String LIBRARY_PICTURE_PATH = "library.jpg";
	private final String WOOD_PICTURE_PATH = "wood.jpg";

	Image libraryImage;
	Image woodImage;

	ArrayList <User> currentListOfUsers;          	//the current list of users in the system displayed on the screen
	ArrayList <Book> currentListOfBooks;	   		//the current list of books in the system displayed on the screen

	Font normalFont;				//font for regular text
	Font boldFont;					//font for bold text/headers
	Font startFont;					//font for start screen
	Font titleFontBig;				//font for big titles/screen labels
	Font titleFontSmall;			//font for small titles/screen labels
	Font starFont;					//font for stars (asterisks) drawn to represent star rating

	Color mainBGC;					//the background color for most tables and boxes in the program
	Color headBGC;					//the background color for the headings of the table of Users/Books in the library system
	Color starColor;				//the color of the stars used to represent the star ratings of books

	Book tempBook;					//a temporary Book object for checkout/return purposes
	User tempUser;					//a temporary User object for checkout/return purposes

	//booleans that determine which screen the program is on so the appropriate components and text can be placed on-screen
	boolean startScreen=true;				//the start screen
	boolean mainScreen=false;				//the main menu
	boolean checkOutScreen=false;			//screen for checking out books
	boolean returnScreen=false;				//screen for return books
	boolean createSelectionScreen=false;	//screen for selecting whether to go to create user screen or create book screen
	boolean createUserScreen=false;			//screen for adding new users to the system
	boolean createBookScreen=false;			//screen for adding new books to the system
	boolean deleteSelectionScreen=false;	//screen for selecting whether to go to delete user screen or delete book screen
	boolean deleteUserScreen=false;			//screen for deleting users from the system
	boolean deleteBookScreen=false;			//screen for deleting books from the system
	boolean browseSelectionScreen=false;	//screen for selecting whether to go to browse user screen or browse book screen
	boolean browseUserScreen=false;			//screen for looking at all the users in the system
	boolean browseBookScreen=false;			//screen for looking at all the books in the system
	boolean compareBookScreen=false;		//screen for comparing the star rating of two books in the system
	//the following can be accessed while other screens are still true
	boolean fineCalcScreen=false;			//screen for calculating overdue fines after returning a book
	boolean finishScreen=false;				//screen that summarizes checkout/return results after they have been completed
	boolean checkedOutListScreen=false;		//screen for looking at all the books a user has checked out from the system

	//keeps track of whether content being displayed on screen is user or book related so the program can execute the appropriate code
	boolean userRelated=false;
	boolean bookRelated=false;

	//error checkers that will display error messages on screen
	boolean emptyTextFields=false;				//displays error message when the TextFields on the current screen are empty
	boolean duplicate=false;					//displays error message when the user/book to be created is already in the system
	boolean noResults=false;					//displays error message if search returns no results
	boolean actionUnnecessary=false;			//displays error message if user has no fines to pay off during fine repayment or book has not been lost during replacement
	boolean alreadySelected=false;				//displays error message when the same book is selected twice during comparison
	boolean successful=false;					//checks if the user/book creation was successful
	boolean[] stringToNumError = new boolean[3];	//displays error message when text in the TextFields cannot be converted to a number (int, long, or double)
	boolean[] invalidValues = new boolean [3];	 	//displays error message when numbers entered into the TextFields are invalid (incorrect digits,range,etc)
	boolean[] checkReturnError = new boolean [2];	//displays error messages related to check out or return processes

	//Screen Buttons:  buttons that changes the screen the program is currently on
	Button startButton;					//goes from start screen to main menu

	Button toCheckOutButton;			//goes to check out book screen
	Button toReturnBookButton;			//goes to return book screen
	Button toCreateSelectionButton;		//goes to select create user or book screen
	Button toDeleteSelectionButton;		//goes to select delete user or book screen
	Button toBrowseSelectionButton;		//goes to select browse user or book screen
	Button toCompareBookButton;			//goes to compare book screen
	Button returnToMainButton;			//goes back to main menu
	Button returnToPreviousButton;		//goes back to previous screen

	Button toCreateUserButton;			//goes to create user screen
	Button toCreateBookButton;			//goes to create book screen
	Button toDeleteUserButton;			//goes to delete user screen
	Button toDeleteBookButton;			//goes to delete book screen
	Button toBrowseUserButton;			//goes to browse user screen
	Button toBrowseBookButton;			//goes to browse book screen

	//Important Buttons: buttons with major functions
	Button calcFineButton;				//calculate overdue fines during return process
	Button createButton;				//creates User or Book and adds it to the library system
	Button searchButton;				//searches for a user/book using information entered into the program
	Button nextPageButton;				//switches to next page of current list
	Button previousPageButton;			//switches to previous page of current list

	//List Buttons: arrays of button that correspond to a certain user/book when a list of users/books is displayed on screen
	// first element of array corresponds first user/book on page, second to second, etc
	Button[] checkOutButton = new Button[MAX_OBJECTS_ONSCREEN]; 			//selects book to be checked out/user thats checking it out
	Button[] returnButton = new Button[MAX_OBJECTS_ONSCREEN]; 				//selects user returning book/book to be returned
	Button[] checkedOutListButton = new Button[MAX_OBJECTS_ONSCREEN]; 		//displays corresponding user's list of checked out books 
	Button[] deleteButton = new Button[MAX_OBJECTS_ONSCREEN];				//deletes the user/book from the system
	Button[] payFinesButton = new Button[MAX_OBJECTS_ONSCREEN];				//clears a user's balance
	Button[] replaceBookButton = new Button[MAX_OBJECTS_ONSCREEN];				//replaces a lost book
	Button[] compareButton = new Button[MAX_OBJECTS_ONSCREEN];				//compares the star rating of 2 books to determine which is better

	TextField daysCheckedOutField;				//where the number of days a book has been checked out for is entered
	TextField firstNameField;					//where the first name of the user or first name of author of the book being created is entered
	TextField lastNameField;					//where the last name of the user or last name of author of the book being created is entered
	TextField studentNumberField;				//where the student number of the user being created is entered
	TextField titleField;						//where the title of the book being created is entered
	TextField ISBNField;						//where the ISBN of book being created is entered
	TextField starRatingField;					//where the star rating of book object being created is entered
	TextField costField;						//where the cost of book object being created is entered
	TextField searchField;						//where the search parameters for search function is entered

	CheckboxGroup lostRadioGroup;	//radio group that determines if a book a user has checked out has been lost
	Checkbox lostYes;					//the book has been lost
	Checkbox lostNo;					//the book has not been lost

	Choice categorySelector;		//Drop-down list where user selects category of Book object when creating a book
	Choice userSearchBySelector;	//Drop-down list where user selects what to search for when searching for User (first name, last name, etc)
	Choice bookSearchBySelector;	//Drop-down list where user selects what to search for when searching for Book (title, category, etc)


	//initialize all the things that will be present on the first screen.only runs once.
	public void init () 
	{
		resize (1100, 700);
		setLayout (null); 

		//hard code some users and books into the system
		Library.createUser("Bob", "Jones", 100001);
		Library.createUser("Rob", "Holmes", 100002);
		Library.createUser("Job", "Stevenson", 100003);
		Library.createUser("Nob", "Holmes", 100004);
		Library.createUser("Mob", "Johnson", 100005);
		Library.createUser("Cave", "Johnson", 100006);
		Library.createUser("Kevin", "James", 100007);
		Library.createUser("Alex", "Hughs", 100008);
		Library.createUser("Michelle", "Brown", 100009);
		Library.createUser("Sarah", "Carter", 100010);
		Library.createUser("Paige", "Holmes", 100011);
		Library.createUser("Robert", "Walker", 100012);
		Library.createUser("Pierre", "Cartier", 100013);
		Library.createUser("Tyler", "Swift", 100014);
		Library.createUser("Kanye", "East", 100015);
		Library.createUser("Alicia", "Keith", 100016);
		Library.createUser("Keith", "Churchill", 100017);
		Library.createUser("Winston", "Chu", 100018);
		Library.createUser("Bob", "Holmes", 100019);
		Library.createUser("Yu", "Shu", 100020);
		Library.createUser("Bob", "Holmes", 100021);
		Library.createUser("John", "Johnson", 100022);
		Library.createUser("Max", "Maxwell", 100023);
		Library.createUser("Bob", "Holmes", 100024);
		Library.createUser("Kevin", "Lu", 100025);
		Library.createUser("Bob", "Holmes", 100026);
		Library.createUser("Bob", "Holmes", 100027);
		Library.getListOfUsers().get(1).changeBalance(0.20);
		Library.getListOfUsers().get(2).changeBalance(5.99);
		Library.createBook("SQUID", "Roger", "Morris", "Horror", 1234567890121L, 13, 5, 4.50);
		Library.createBook("Flower", "Ann", "Donnelly", "Romance", 1234567890122L, 13, 3, 4.30);
		Library.createBook("Doing Donuts", "Karen", "Smith", "Action", 1234567890123L, 13, 5, 4.50);
		Library.createBook("The Catcher in the Rye", "J.D.", "Salinger", "Drama", 1234567890124L, 13, 4, 5.50);
		Library.createBook("Lightweight Sandwich Construction", "Kyle", "Morris", "Science Fiction", 1234567890125L, 13, 4, 4.50);
		Library.createBook("Toast", "Nigel", "Slater", "Action", 1234567890126L, 13, 2, 4.50);
		Library.createBook("A Trail of Bread Crumbs", "Christine", "Atha", "Mystery", 1234567890127L, 13, 4, 4.50);
		Library.createBook("Muffin Man", "Michael", "Cena", "Romance", 1234567890128L, 13, 4, 4.50);
		Library.createBook("Loafers", "Chet", "Reznor", "Comedy", 1234567890129L, 13, 4, 4.00);
		Library.createBook("Nickel and Dime", "Jenna", "Black", "Comedy", 1234567890130L, 13, 3, 4.99);
		Library.createBook("Bunion Sores", "Bertha", "Grande", "Horror", 1234567890131L, 13, 1, 4.50);
		Library.createBook("KID", "Roger", "Morris", "Horror", 1234567890132L, 13, 5, 4.50);
		Library.createBook("SQUID", "Roger", "Morris", "Horror", 1254567890133L, 13, 5, 4.50);
		Library.createBook("Bananarama", "Kate", "Potash", "Comedy", 1234567890134L, 13, 5, 4.50);
		Library.checkOutBook(Library.getListOfUsers().get(0), Library.getListOfBooks().get(1));
		Library.checkOutBook(Library.getListOfUsers().get(1), Library.getListOfBooks().get(0));
		Library.checkOutBook(Library.getListOfUsers().get(1), Library.getListOfBooks().get(3));
		Library.checkOutBook(Library.getListOfUsers().get(1), Library.getListOfBooks().get(5));
		Library.getListOfBooks().get(2).setLost(true);

		//initiate the current list of users/books to all users/books in the system
		currentListOfUsers=Library.getListOfUsers();
		currentListOfBooks=Library.getListOfBooks();

		normalFont = new Font(Font.SANS_SERIF,Font.PLAIN,13);
		boldFont = new Font(Font.SANS_SERIF,Font.BOLD,14);
		startFont = new Font("Verdana", Font.BOLD,60);
		titleFontBig = new Font("Verdana", Font.BOLD,45);
		titleFontSmall = new Font("Verdana", Font.BOLD,26);
		starFont = new Font(Font.SANS_SERIF,Font.BOLD,18);

		mainBGC = new Color(250, 250, 250);
		headBGC = new Color (230,230,230);
		starColor = new Color(255, 223, 0);

		/////////////////////////////////////////////////////
		//initiate labels, position, etc of every component//
		/////////////////////////////////////////////////////

		//start screen
		startButton = new Button ("Start");
		startButton.setBounds (300,500,500,100);

		returnToMainButton = new Button ("Main Menu");
		returnToMainButton.setBounds (20,20,100,50);

		returnToPreviousButton =  new Button ("Return");
		returnToPreviousButton.setBounds (150,20,100,50);

		//main screen
		toCheckOutButton = new Button ("Checkout Book");
		toCheckOutButton.setBounds (450,140,200,50);

		toReturnBookButton = new Button ("Return Book");
		toReturnBookButton.setBounds (450,240,200,50);

		toCreateSelectionButton = new Button ("Create User/Book");
		toCreateSelectionButton.setBounds (450,340,200,50);

		toDeleteSelectionButton = new Button ("Delete User/Book");
		toDeleteSelectionButton.setBounds (450,440,200,50);

		toBrowseSelectionButton = new Button ("Browse User/Book List");
		toBrowseSelectionButton.setBounds (450,540,200,50);

		toCompareBookButton = new Button ("Compare Books");
		toCompareBookButton.setBounds (450,640,200,50);

		//create screen
		toCreateUserButton = new Button ("Create User");
		toCreateUserButton.setBounds (300,500,200,50);

		toCreateBookButton = new Button ("Create Book");
		toCreateBookButton.setBounds (700,500,200,50);

		//delete screen
		toDeleteUserButton = new Button ("Delete User");
		toDeleteUserButton.setBounds (300,500,200,50);

		toDeleteBookButton = new Button ("Delete Book");
		toDeleteBookButton.setBounds (700,500,200,50);

		//browse screen
		toBrowseUserButton = new Button ("Browse User List / Pay Fines");
		toBrowseUserButton.setBounds (300,500,200,50);

		toBrowseBookButton = new Button ("Browse Book List / Replace Books");
		toBrowseBookButton.setBounds (700,500,200,50);

		//major buttons
		calcFineButton = new Button ("Return and Calculate Fines");
		calcFineButton.setBounds(450, 420, 200, 50);

		createButton = new Button ("Create");
		createButton.setBounds (450,600,200,50);

		searchButton = new Button ("Search");
		searchButton.setBounds (540,20,100,50);

		nextPageButton = new Button ("Next Page");
		nextPageButton.setBounds(600,650,200,50);

		previousPageButton = new Button ("Previous Page");
		previousPageButton.setBounds(300,650,200,50);

		//radio buttons
		lostRadioGroup = new CheckboxGroup ();
		lostYes = new Checkbox ("Yes", lostRadioGroup, false);
		lostNo = new Checkbox ("No", lostRadioGroup, true);
		lostYes.setBounds (500, 350, 50, 50);
		lostNo.setBounds (570, 350, 50, 50);

		lostYes.setBackground(mainBGC);
		lostNo.setBackground(mainBGC);
		
		//text fields
		daysCheckedOutField = new TextField (30);
		daysCheckedOutField .setBounds (450, 250, 200, 30);

		firstNameField = new TextField (30);
		firstNameField.setBounds (450, 250, 200, 30);

		lastNameField = new TextField (30);
		lastNameField.setBounds (450, 300, 200, 30);

		studentNumberField = new TextField (30);
		studentNumberField.setBounds (450, 350, 200, 30);

		titleField= new TextField (30);
		titleField.setBounds (450, 200, 200, 30);

		ISBNField= new TextField (30);
		ISBNField.setBounds (450, 400, 200, 30);

		starRatingField= new TextField (30);
		starRatingField.setBounds (450, 450, 200, 30);

		costField= new TextField (30);
		costField.setBounds (450, 500, 200, 30);

		searchField= new TextField (30);
		searchField.setBounds (310, 40, 200, 30);

		//choice/selectors
		categorySelector = new Choice();
		categorySelector.setBounds (450,350,200,200);
		categorySelector.add("Drama");
		categorySelector.add("Action");
		categorySelector.add("Romance");
		categorySelector.add("Mystery");
		categorySelector.add("Horror");
		categorySelector.add("Comedy");
		categorySelector.add("Fantasy");
		categorySelector.add("Science Fiction");

		userSearchBySelector = new Choice();
		userSearchBySelector.setBounds (380,15,140,50);
		userSearchBySelector.add("First Name");
		userSearchBySelector.add("Last Name");
		userSearchBySelector.add("Student Number (Full)");

		bookSearchBySelector = new Choice();
		bookSearchBySelector.setBounds (380,15,140,50);
		bookSearchBySelector.add("Title");
		bookSearchBySelector.add("Author's First Name");
		bookSearchBySelector.add("Author's Last Name");
		bookSearchBySelector.add("Category");
		bookSearchBySelector.add("ISBN (Full)");

		add(startButton);

		// Attach actions to the components
		startButton.addActionListener(this);
		returnToMainButton.addActionListener(this);
		returnToPreviousButton.addActionListener(this);
		toCheckOutButton.addActionListener(this);
		toReturnBookButton.addActionListener(this);
		toCreateSelectionButton.addActionListener(this);
		toDeleteSelectionButton.addActionListener(this);
		toBrowseSelectionButton.addActionListener(this);
		toCompareBookButton.addActionListener(this);
		toCreateUserButton.addActionListener(this);
		toCreateBookButton.addActionListener(this);
		toDeleteUserButton.addActionListener(this);
		toDeleteBookButton.addActionListener(this);
		toBrowseUserButton.addActionListener(this);
		toBrowseBookButton.addActionListener(this);
		calcFineButton.addActionListener(this);
		createButton.addActionListener(this);
		searchButton.addActionListener(this);
		nextPageButton.addActionListener(this);
		previousPageButton.addActionListener(this);

		//initiate labels and position, and attach actions to the components in arrays
		for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
		{
			checkOutButton[x] = new Button("Select");
			checkOutButton[x].setBounds(950,130+x*40,90,30);

			returnButton[x] = new Button("Select");
			returnButton[x].setBounds(950,130+x*40,90,30);

			checkedOutListButton[x] = new Button ("See List");
			checkedOutListButton[x].setBounds(750,130+x*40,90,30);

			deleteButton[x] = new Button("Delete");
			deleteButton[x].setBounds(950,130+x*40,90,30);

			payFinesButton[x] = new Button("Pay");
			payFinesButton[x].setBounds(950,130+x*40,90,30);

			replaceBookButton[x] = new Button("Replace");
			replaceBookButton[x].setBounds(950,130+x*40,90,30);

			compareButton[x] = new Button("Select");
			compareButton[x].setBounds(950,130+x*40,90,30);

			checkOutButton[x].addActionListener(this);
			returnButton[x].addActionListener(this);
			checkedOutListButton[x].addActionListener(this);
			deleteButton[x].addActionListener(this);
			payFinesButton[x].addActionListener(this);
			replaceBookButton[x].addActionListener(this);
			compareButton[x].addActionListener(this);
		}

		/*This is what I use if I want to put a picture on the screen*/
		libraryImage = loadImage (LIBRARY_PICTURE_PATH);
		woodImage = loadImage (WOOD_PICTURE_PATH);
		prepareImage (libraryImage, this);
		prepareImage (woodImage, this);

		// Now, it can actually take some time to load the image, and
		// it could fail (image not found, etc).  The following checks for
		// all that.
		MediaTracker tracker = new MediaTracker (this);
		// Add the picture to the list of images to be tracked
		tracker.addImage (libraryImage, 0);
		tracker.addImage (woodImage, 0);
		// Wait until all the images are loaded.  This can throw an
		// InterruptedException although it's not likely, so we ignore
		// it if it occurs.
		try
		{
			tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		// If there were any errors loading the image, then abort the
		// program with a message.
		if (tracker.isErrorAny ())
		{
			showStatus ("Couldn't load ");
			return;
		}
	}

	private static BufferedImage loadImage(String imgPath){
		BufferedInputStream imgStream = new BufferedInputStream(LibraryGUI.class.getResourceAsStream("/"+imgPath));
		if(imgStream != null){
			try {
				return javax.imageio.ImageIO.read(imgStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		return null;
	}

	/**	Remove every component from the screen
	 * 	Components include Buttons, TextFields, Choice selectors, and CheckBoxes
	 * 	Also empties text fields and remove error messages
	 */
	private void removeComponentsAndText()
	{
		//set all TextFields to blank
		daysCheckedOutField.setText("");
		firstNameField.setText("");			
		lastNameField.setText("");					
		studentNumberField.setText("");			
		titleField.setText("");				
		ISBNField.setText("");					
		starRatingField.setText("");					
		costField.setText("");					
		searchField.setText("");						

		//remove every Button
		remove(startButton);
		remove(toCheckOutButton);
		remove(toReturnBookButton);
		remove(toCreateSelectionButton);
		remove(toDeleteSelectionButton);
		remove(toBrowseSelectionButton);
		remove(toCompareBookButton);
		remove(returnToMainButton);
		remove(returnToPreviousButton);
		remove(toCreateUserButton);
		remove(toCreateBookButton);
		remove(toDeleteUserButton);
		remove(toDeleteBookButton);
		remove(toBrowseUserButton);
		remove(toBrowseBookButton);
		remove(calcFineButton);
		remove(createButton);
		remove(searchButton);
		remove(nextPageButton);
		remove(previousPageButton);

		//remove the buttons stored in arrays
		for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
		{
			remove(checkOutButton[x]); 
			remove(returnButton[x]); 
			remove(checkedOutListButton[x]); 
			remove(deleteButton[x]); 
			remove(payFinesButton[x]);
			remove(replaceBookButton[x]);
			remove(compareButton[x]);
		}

		//remove every TextField
		remove(daysCheckedOutField);
		remove(firstNameField);
		remove(lastNameField);
		remove(studentNumberField);
		remove(titleField);
		remove(ISBNField);
		remove(starRatingField);
		remove(costField);
		remove(searchField);

		//remove every Choice object
		remove(categorySelector);
		remove(userSearchBySelector);
		remove(bookSearchBySelector);

		//remove every CheckBox
		remove(lostYes);
		remove(lostNo);

		//set all error checkers to empty to remove every error message from the screen
		emptyTextFields=false;
		duplicate=false;
		noResults=false;
		actionUnnecessary=false;
		alreadySelected=false;
		successful=false;
		//set the error checkers in arrays to false
		for (int x=0;x<stringToNumError.length;x++)
		{
			stringToNumError[x]=false;
			invalidValues[x]=false;
			//prevents for loop from setting a value outside the bounds of checkReturnError
			if (x<checkReturnError.length)
				checkReturnError[x]=false;
		}
	}

	/**determines number of pages of current list of users/books and adds all appropriate list buttons when list is first added onto the screen
	 * i.e. page number of list is 1, next/previous page buttons have not been pressed
	 * 
	 */
	private void addList ()
	{
		//determine number of pages of current list 
		//the number of pages is the size of the current user/book list divided by 13, rounded up
		if (userRelated==true)					//number of pages based on current user list
		{
			numOfPages=currentListOfUsers.size()/MAX_OBJECTS_ONSCREEN;
			if (currentListOfUsers.size()%MAX_OBJECTS_ONSCREEN!=0)
				numOfPages+=1;
		}
		else if (bookRelated==true)				//number of pages based on current book list
		{
			numOfPages=currentListOfBooks.size()/MAX_OBJECTS_ONSCREEN;
			if (currentListOfBooks.size()%MAX_OBJECTS_ONSCREEN!=0)
				numOfPages+=1;
		}

		pageNum=1;				//display first page of list

		//remove all of the list buttons currently on screen
		for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
		{
			remove(checkOutButton[x]); 
			remove(returnButton[x]); 
			remove(checkedOutListButton[x]);
			remove(deleteButton[x]);
			remove(payFinesButton[x]);
			remove(replaceBookButton[x]);
			remove(compareButton[x]);
		}

		remove(nextPageButton);
		remove(previousPageButton);

		//add buttons based on number of pages list takes up and number of objects on page
		//adds next page button and 13 list buttons if there is more than 1 page
		if (numOfPages>1)
		{
			add(nextPageButton);
			for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
			{
				//adds list buttons depending on which screen program is on
				if (checkOutScreen==true)																
					add(checkOutButton[x]); 
				else if (returnScreen==true)															
					add(returnButton[x]); 
				else if (deleteUserScreen==true||deleteBookScreen==true)			
					add(deleteButton[x]);
				else if (browseUserScreen==true)
					add(payFinesButton[x]);
				else if (browseBookScreen==true)
					add(replaceBookButton[x]);
				else if (compareBookScreen==true)
					add(compareButton[x]);
				if (userRelated==true)						//only add checkedOutListButton to user lists
					add(checkedOutListButton[x]);
			}
		}
		//adds list buttons equal to the size of the current list if there is only 1 page
		else if (numOfPages==1)
		{
			//add list buttons that have to with users
			if (userRelated==true) 					
			{
				for (int x=0;x<currentListOfUsers.size();x++)
				{
					add(checkedOutListButton[x]);
					//adds list buttons depending on which screen program is on
					if (checkOutScreen==true)					
						add(checkOutButton[x]); 
					else if (returnScreen==true)				
						add(returnButton[x]); 
					else if (deleteUserScreen==true)			
						add(deleteButton[x]);
					else if (browseUserScreen==true)
						add(payFinesButton[x]);
				}
			}
			//add list buttons that have to with books
			else if (bookRelated==true);		
			{
				for (int x=0;x<currentListOfBooks.size();x++)
				{
					//adds list buttons depending on which screen program is on
					if (checkOutScreen==true && checkedOutListScreen==false)			//when not looking at a user's list of checked out books
						add(checkOutButton[x]); 
					else if (returnScreen==true)																
						add(returnButton[x]); 
					else if (deleteBookScreen==true)														
						add(deleteButton[x]);
					else if (compareBookScreen==true)
						add(compareButton[x]);
					else if (browseBookScreen==true)
						add(replaceBookButton[x]);
				}
			}
		}
	}

	/**	Prints list of users or books in system meant to be displayed on screen
	 * 	Which users/books are displayed is based on search parameters and page number of current list
	 */
	private void displayListOfObjects (Graphics g)
	{
		int objectsOnPage;		//number of users/books on current page
		int listIndex;			//index of user/book to be displayed on screen in current list of users/books
		int yPos;				//the y-coordinate of the user/book to be displayed on screen

		//draw background boxes and tables
		g.setColor(mainBGC);
		g.fillRect(7, 80, 1065, 565);

		g.setColor(headBGC);
		g.fillRect(7, 80, 1065, 40);
		g.fillRect(807, 650, 265, 47);

		g.setColor(Color.black);
		g.drawRect(7, 80, 1065, 565);
		g.drawRect(807, 650, 265, 47);


		//prints list of users if the current screen is user related
		if (userRelated==true)
		{
			g.setFont(boldFont);
			g.drawString("Search By", 300, 30);
			g.drawString("#",10, 100);
			g.drawString("First Name",40, 100);
			g.drawString("Last Name",220, 100);
			g.drawString("Student Number",420, 100);
			g.drawString("Balance",600, 100);
			g.drawString("Books Checked Out",730, 100);
			g.setFont(normalFont);

			//prints the max number of users per page on screen if it is not the last page of the list
			//prints the remaining users in the list not already printed on previous pages on screen if it is the last page
			if (pageNum<numOfPages||currentListOfUsers.size()==MAX_OBJECTS_ONSCREEN)
				objectsOnPage=MAX_OBJECTS_ONSCREEN;
			else
				objectsOnPage=currentListOfUsers.size()-(numOfPages-1)*MAX_OBJECTS_ONSCREEN;

			//prints each User's information row by row in a table, does not print if list is empty
			if (currentListOfUsers.isEmpty()==false)
			{
				for (int x=0;x<objectsOnPage;x++)
				{
					yPos=150 + x*40;
					listIndex = x + (pageNum-1)*MAX_OBJECTS_ONSCREEN;

					g.drawString(Integer.toString(listIndex+1),10,yPos);
					g.drawString(currentListOfUsers.get(listIndex).getFirstName(),40,yPos);
					g.drawString(currentListOfUsers.get(listIndex).getLastName(),220,yPos);

					//pad student number with leading zeros until it is 6 digits long					
					//source: http://stackoverflow.com/questions/275711/add-leading-zeroes-to-number-in-java
					g.drawString(String.format("%06d", currentListOfUsers.get(listIndex).getStudentNumber()),420,yPos);

					//pad cost with trailing zeros until it has 2 decimal places
					//source: http://stackoverflow.com/questions/1734282/how-to-properly-display-a-price-up-to-two-decimals-cents-including-trailing-ze
					g.drawString("$"+ String.format("%.2f",currentListOfUsers.get(listIndex).getBalance()),595,yPos);
				}
			}
		}
		//prints list of books if the current screen is book related
		else if (bookRelated==true)
		{
			g.setFont(boldFont);
			g.drawString("#",10, 100);
			g.drawString("Title",40, 100);
			g.drawString("Author",260, 100);
			g.drawString("Category",390, 100);
			g.drawString("ISBN",490, 100);
			g.drawString("Star Rating",600, 100);
			g.drawString("Cost",710, 100);
			g.drawString("Checked Out?",770, 100);
			g.drawString("Lost?",880, 100);
			//does not put search function on screen if looking at a user's list of checked out books
			if (checkedOutListScreen==false && returnScreen==false)
				g.drawString("Search By", 300, 30);
			g.setFont(normalFont);

			//prints the max number of books per page on screen if it is not the last page of the list
			//prints the books in the list not already printed on previous pages on screen if it is the last page
			if (pageNum<numOfPages)
				objectsOnPage=MAX_OBJECTS_ONSCREEN;
			else
				objectsOnPage=currentListOfBooks.size()-(pageNum-1)*MAX_OBJECTS_ONSCREEN;

			//prints each Book's information row by row in a table, does not print if list is empty
			if (currentListOfBooks.isEmpty()==false)
			{
				for (int x=0;x<objectsOnPage;x++)
				{
					yPos=150 + x*40;
					listIndex = x + (pageNum-1)*MAX_OBJECTS_ONSCREEN;

					g.drawString(Integer.toString(listIndex+1),10,yPos);
					g.drawString(currentListOfBooks.get(listIndex).getTitle(),40,yPos);
					g.drawString(currentListOfBooks.get(listIndex).getAuthorFName() + " " + currentListOfBooks.get(listIndex).getAuthorLName(),260,yPos);
					g.drawString(currentListOfBooks.get(listIndex).getCategory(),390,yPos);

					//pad ISBN with leading zeros until it is 10 or 13 digits long depending on the length of the ISBN for the specified book
					//source: http://stackoverflow.com/questions/275711/add-leading-zeroes-to-number-in-java
					g.drawString(String.format("%0" +  currentListOfBooks.get(listIndex).getISBNLength() + "d", currentListOfBooks.get(listIndex).getISBN()),490,yPos);

					g.setFont(starFont);
					g.setColor(starColor);
					//draw a number of stars on screen equal to the star rating of the book
					for (int y=0;y<currentListOfBooks.get(listIndex).getStarRating();y++)
						g.drawString("*",605 + y*15,yPos+5);

					g.setFont(normalFont);
					g.setColor(Color.black);
					//pad cost with trailing zeros until it has 2 decimal places
					//source: http://stackoverflow.com/questions/1734282/how-to-properly-display-a-price-up-to-two-decimals-cents-including-trailing-ze
					g.drawString("$"+ String.format("%.2f",currentListOfBooks.get(listIndex).getCost()),705,yPos);

					//prints whether the book is checked out or not
					if (currentListOfBooks.get(listIndex).isCheckedOut()==true)
						g.drawString("Yes",806, yPos);
					else
						g.drawString("No",810, yPos);

					//prints whether the book is lost or not
					if (currentListOfBooks.get(listIndex).isLost()==true)
						g.drawString("Yes",886, yPos);
					else
						g.drawString("No",890, yPos);
				}
			}
		}
	}

	// Here we will show the results of our actions
	public void paint (Graphics g)
	{
		g.drawImage(woodImage, 0, 0, 1100, 700, null);

		//default text font and color
		g.setFont(normalFont);
		g.setColor(Color.black);

		if (startScreen==true)																
		{
			
			//draw and scale image
			g.drawImage(libraryImage, 0, 0, 1100, 700, null);
			g.setFont(startFont);
			g.setColor(Color.black);
			g.drawString("SLSS Library System",230,100);
		}
		else if (mainScreen==true)														
		{
			//draw and scale image
			g.drawImage(libraryImage, 0, 0, 1100, 700, null);
			g.setFont(titleFontBig);
			g.drawString("Main Menu",420,100);
		}
		else if (checkOutScreen==true)					//check out process involves 3 screens, numbered in order of appearance
		{
			//displays information needed during process of checking out books
			if (finishScreen==false)									//CHECK OUT BOOK SCREEN #1 & 2
			{
				displayListOfObjects(g);

				g.setFont (titleFontSmall);
				g.drawString("Check Out Screen", 20, 685);

				g.setFont(boldFont);
				//displays text asking which book is being checked out and any related errors
				if (bookRelated==true)									//CHECK OUT BOOK SCREEN #1
				{
					//does not display this when looking at a user's list of checked out books
					if (checkedOutListScreen==false)
					{
						g.drawString("Which Book is Being",925,95);
						g.drawString("Checked Out?",950, 115);
						//displays error messages
						if (checkReturnError[0]==true)				
						{
							g.drawString("Book has been lost and", 810, 665);
							g.drawString("cannot be checked out.", 810, 680);
							g.drawString("Please select another book.", 810, 695);
						}
						else if (checkReturnError[1]==true)		
						{
							g.drawString("Book has already been checked out.", 810, 670);
							g.drawString("Please select another book.", 810, 690);
						}
					}
				}
				//displays text asking which user is checking out the book and any related errors
				else if (userRelated==true)								//CHECK OUT BOOK SCREEN #2
				{
					g.drawString("Which User is ",935,95);
					g.drawString("Checking Out the Book?",900, 115);
					//displays error messages
					if (checkReturnError[0]==true)				
					{
						g.drawString("User already has three ", 810, 665);
						g.drawString("books checked out.", 810, 680);
						g.drawString("Please select another user.", 810, 695);
					}
					else if (checkReturnError[1]==true)		
					{
						g.drawString("User has a balance of over $5.00 ", 810, 665);
						g.drawString("and cannot check out a book. ", 810, 680);
						g.drawString("Please select another user.", 810, 695);
					}
				}
			}
			//summarizes results of check out process
			else if (finishScreen==true)								//CHECK OUT BOOK SCREEN #3
			{
				g.setFont(titleFontBig);
				g.drawString ("Results",450,100);

				g.setFont(boldFont);
				g.drawString(tempBook.getTitle() + " has been successfully checked out to " + tempUser.getFirstName() + " " + tempUser.getLastName() ,350, 400);
				g.drawString("The book must be returned to the library within 2 weeks.", 355, 430);
				g.drawString("For every day it is over due, $0.10 will be charged to the user's account.", 310, 460);
			}
		}
		else if (returnScreen==true)					//return process involves 4 screens, numbered in order of appearance
		{
			//displays information needed during process of returning books
			if (fineCalcScreen==false && finishScreen==false)			//RETURN BOOK SCREEN #1 & 2
			{
				displayListOfObjects(g);

				g.setFont(titleFontSmall);
				g.drawString("Return Screen", 40, 685);

				g.setFont(boldFont);
				//displays text asking which user is returning a book and any related errors
				if (userRelated==true)									//RETURN BOOK SCREEN #1
				{
					g.drawString("Which User is",945, 95);
					g.drawString("Returning a Book?",935, 115);
					//display error messages
					if (checkReturnError[0]==true)				
					{
						g.drawString("User has not checked out any books.", 810, 670);
						g.drawString("Please select another user.", 810, 690);
					}
				}
				//displays text asking which book is being returned
				else if (bookRelated==true)								//RETURN BOOK SCREEN #2
				{
					g.drawString("Which Book is ",950,95);
					g.drawString("Being Returned?",945, 115);
				}
			}
			//displays text asking for information needed to calculate overdue fines 
			else if (fineCalcScreen==true)								//RETURN BOOK SCREEN #3
			{
				//draws background box 
				g.setColor(mainBGC);
				g.fillRect(405, 220, 300, 290);
				g.setColor(Color.black);
				g.drawRect(405, 220, 300, 290);

				g.setFont(titleFontBig);
				g.drawString ("Fine Calculation",350,100);

				g.setFont(normalFont);
				g.drawString("How long has the book been checked out?", 435, 240);
				g.drawString("Did the user lose the book?", 475, 340);
				g.drawString("Days", 660, 270);
				//displays error messages
				if (stringToNumError[0]==true)
				{
					g.setColor(Color.red);
					g.drawString("Enter a whole number",450,295);
					g.setColor(Color.black);
				}
			}
			//summarizes results of return process
			else if (finishScreen==true)								//RETURN BOOK SCREEN #4
			{
				g.setFont(titleFontBig);
				g.drawString ("Results",450,100);

				g.setFont(boldFont);
				g.drawString(tempBook.getTitle() + " has been successfully returned" ,350, 400);
				g.drawString("$"+ String.format("%.2f",totalFine) + " have been added to  " + tempUser.getFirstName() + " " + tempUser.getLastName() +"'s balance", 375, 430);
			}
		}
		else if (createSelectionScreen==true)										
		{
			//draw and scale image
			g.drawImage(libraryImage, 0, 0, 1100, 700, null);
			g.setFont(titleFontBig);
			g.drawString("Create User/Book",360,100);
		}
		else if (createUserScreen==true)												
		{
			g.setFont(titleFontBig);
			g.drawString("Create User",420,100);

			//draws background box 
			g.setColor(mainBGC);
			g.fillRect(330, 220, 490, 210);
			g.setColor(Color.black);
			g.drawRect(330, 220, 490, 210);

			//labels the text fields for entering in the new user's information
			g.setFont(normalFont);
			g.drawString("User's First Name", 340, 268);
			g.drawString("User's Last Name", 340, 318);
			g.drawString("Student Number", 340, 368);
			g.drawString("(6 digits)", 360, 385);

			//displays error messages
			g.setColor(Color.red);

			if (emptyTextFields==true)
				g.drawString("Fill in every field", 450, 400);

			//studentNumberField related errors
			if (stringToNumError[0]==true)
				g.drawString("Enter a whole number", 660, 368);
			else if (invalidValues[0]==true)
				g.drawString("Enter a 6-digit long number", 660, 368);
			else if (duplicate==true)
				g.drawString("User with same student number already exists", 450, 240);
			else if (successful==true)
			{
				g.setColor(Color.black);
				g.drawString("User creation successful!", 450, 400);
			}
		}
		else if (createBookScreen==true)										
		{
			g.setFont(titleFontBig);
			g.drawString("Create Book",420,100);

			//draws background box 
			g.setColor(mainBGC);
			g.fillRect(320, 180, 650, 380);
			g.setColor(Color.black);
			g.drawRect(320, 180, 650, 380);

			//labels the text fields for entering in the new book's information
			g.setFont(normalFont);
			g.drawString("Title of Book", 330, 218);
			g.drawString("Author's First Name", 330, 268);
			g.drawString("Author's Last Name",330, 318);
			g.drawString("Category", 330, 368);
			g.drawString("ISBN", 330, 418);
			g.drawString("(10 or 13 digits)", 330, 435);
			g.drawString("Star Rating	  (1-5)", 330, 468);
			g.drawString("Cost      (ex. 5.99)", 330, 518);

			//displays error messages
			g.setColor(Color.red);

			if (emptyTextFields==true)
				g.drawString("Fill in every field", 450, 550);

			//ISBNField related errors
			if (stringToNumError[0]==true)
				g.drawString("Enter a whole number", 660, 418);
			else if (invalidValues[0]==true)
				g.drawString("Enter a 10 or 13-digit long number", 660, 418);
			else if (duplicate==true)
				g.drawString("Book with the same ISBN already exists", 450, 190);
			else if (successful==true)
			{
				g.setColor(Color.black);
				g.drawString("Book creation successful!", 450, 550);
			}

			//starRatingField related errors
			if (stringToNumError[1]==true)
				g.drawString("Enter a whole number", 660, 468);
			else if (invalidValues[1]==true)
				g.drawString("Enter a number between 1 and 5", 660, 468);

			//costField related errors
			if (stringToNumError[2]==true)
				g.drawString("Enter a number", 660, 518);
			else if (invalidValues[2]==true)
				g.drawString("Enter a number with a maximum of 2 decimal places", 660, 518);
		}
		else if (deleteSelectionScreen==true)								
		{
			//draw and scale image
			g.drawImage(libraryImage, 0, 0, 1100, 700, null);
			g.setFont(titleFontBig);
			g.drawString("Delete User/Book",360,100);
		}
		else if (deleteUserScreen==true)										
		{
			displayListOfObjects(g);

			g.setFont(titleFontSmall);
			g.drawString("Delete User", 50, 685);

			g.setFont(boldFont);
			//prompt for deletion of user; does not prompt if looking at user's list of checked out books
			if(checkedOutListScreen==false)										
				g.drawString("Delete from System",930, 100);
			if (successful==true)
				g.drawString("User has been successfully deleted", 810, 680);
		}
		else if (deleteBookScreen==true)										
		{
			displayListOfObjects(g);

			g.setFont(titleFontSmall);
			g.drawString("Delete Book", 50, 685);

			g.setFont(boldFont);
			//prompt for deletion of book
			g.drawString("Delete from System", 930, 100);
			if (successful==true)
				g.drawString("Book has been successfully deleted", 810, 680);
		}
		else if (browseSelectionScreen==true)								
		{
			//draw and scale image
			g.drawImage(libraryImage, 0, 0, 1100, 700, null);
			g.setFont(titleFontBig);
			g.drawString("Browse User/Book List", 300, 100);
		}
		else if (browseUserScreen==true)										
		{
			displayListOfObjects(g);

			g.setFont(titleFontSmall);
			g.drawString("Browse User", 50, 685);

			g.setFont(boldFont);
			//prompt for paying off a user's fines
			g.drawString("Pay Off All", 960, 95);
			g.drawString("Overdue Fines", 940, 115);

			//displays error message if the user's balance is 0
			if (actionUnnecessary==true)
				g.drawString("User has no fines to pay off", 810, 680);
			else if (successful==true)
			{
				g.drawString(tempUser.getFirstName() + " " + tempUser.getLastName() + "'s", 810, 670);
				g.drawString("balance has been cleared.", 810, 690);
			}
		}
		else if (browseBookScreen==true)										
		{
			displayListOfObjects(g);

			g.setFont(titleFontSmall);
			g.drawString("Browse Book", 50, 685);

			g.setFont(boldFont);
			g.drawString("Replace Lost Book", 935, 100);

			//displays error message if the book does not need to be replaced
			if (actionUnnecessary==true)
				g.drawString("Book has not been lost", 810, 680);
			else if (successful==true)
			{
				g.drawString(tempBook.getTitle(), 810, 670);
				g.drawString("has been replaced", 810, 690);
			}
		}
		else if (compareBookScreen==true)
		{
			displayListOfObjects(g);

			g.setFont(titleFontSmall);
			g.drawString("Compare Books", 40, 685);

			g.setFont(boldFont);
			//prompt for selection of two books to be compared
			g.drawString("Select two books to", 930, 95);
			g.drawString("be compared", 950, 115);

			//displays how many books have been selected for comparison
			//once 2 books have been selected, displays which book is better between the 2 books,
			//or states that they are equal if their star ratings are the same
			if (booksSelected == 0 || booksSelected == 1)
			{
				//displays error message is same book is selected twice
				if (alreadySelected==false)
				{
					g.drawString(booksSelected + " books have been selected for", 810, 670);
					g.drawString("comparison", 810, 690);
				}
				else if (alreadySelected==true)		
				{
					g.drawString("The same book has been selected twice.", 810, 670);
					g.drawString("Please select a different book", 810, 690);
				}
			}
			else if (booksSelected == 2 && tempBook != null)
			{
				g.drawString(tempBook.getTitle(), 810, 670);
				g.drawString("is the better of the two books", 810, 690);
			}
			else if (booksSelected == 2 && tempBook == null)
			{
				g.drawString("The books have equal ratings", 810, 670);
				g.drawString("and are equally as good", 810, 690);
			}
		}

		//displays error message if search turns up no results
		if (noResults==true)
		{
			g.setFont (boldFont);
			g.drawString("Your search for \"" +  searchField.getText() + "\" did not turn up any results", 30, 150);
		}
	}

	// When a button is clicked this method will get automatically called
	// This is where all actions are specified
	public void actionPerformed (ActionEvent evt)
	{
		//////////////////////////////////////////////
		//////////DISPLAY CHANGE BUTTONS//////////////
		//////////////////////////////////////////////


		if (evt.getSource () == startButton)
		{
			//set booleans to display appropriate screen
			startScreen=false;
			mainScreen=true;

			removeComponentsAndText();

			//add components to screen
			add(toCheckOutButton);
			add(toReturnBookButton);
			add(toCreateSelectionButton);
			add(toDeleteSelectionButton);
			add(toBrowseSelectionButton);
			add(toCompareBookButton);

			repaint();
		}
		if (evt.getSource () == toCheckOutButton || evt.getSource() == returnToPreviousButton && checkOutScreen==true)
		{
			//set booleans to display appropriate screen
			checkOutScreen=true;
			mainScreen=false;
			checkedOutListScreen=false;
			userRelated=false;
			bookRelated=true;
			//make current list the full list of books in the system
			currentListOfBooks=Library.getListOfBooks();

			removeComponentsAndText();

			//add components to screen
			add(searchButton);
			add(searchField);
			add(bookSearchBySelector);
			add(returnToMainButton);

			addList();

			repaint();
		}

		if (evt.getSource () == toReturnBookButton || evt.getSource() == returnToPreviousButton && returnScreen==true)
		{
			//set booleans to display appropriate screens
			returnScreen=true;
			mainScreen=false;
			checkedOutListScreen=false;
			userRelated=true;
			bookRelated=false;
			//make current list the full list of users in the system
			currentListOfUsers=Library.getListOfUsers();

			removeComponentsAndText();

			//add components to screen
			add(searchButton);
			add(searchField);
			add(userSearchBySelector);
			add(returnToMainButton);

			addList();

			repaint();
		}

		if (evt.getSource () == toCreateSelectionButton||(evt.getSource () == returnToPreviousButton && (createUserScreen == true||createBookScreen==true)))
		{
			//set booleans to display appropriate screens
			createSelectionScreen=true;
			mainScreen=false;
			createUserScreen=false;
			createBookScreen=false;

			removeComponentsAndText();

			//add components to screen
			add(toCreateUserButton);
			add(toCreateBookButton);
			add(returnToMainButton);

			repaint();
		}
		if (evt.getSource () == toCreateUserButton)
		{
			//set booleans to display appropriate screens
			createUserScreen=true;
			createSelectionScreen=false;
			userRelated=true;
			bookRelated=false;

			removeComponentsAndText();

			//add components to screen
			add(createButton);
			add(firstNameField);
			add(lastNameField);
			add(studentNumberField);
			add(returnToMainButton);
			add(returnToPreviousButton);

			repaint();
		}

		if (evt.getSource () == toCreateBookButton)
		{
			//set booleans to display appropriate screens
			createBookScreen=true;
			createSelectionScreen=false;
			userRelated=false;
			bookRelated=true;

			removeComponentsAndText();

			//add components to screen
			add(createButton);
			add(titleField);
			add(firstNameField);
			add(lastNameField);
			add(categorySelector);
			add(ISBNField);
			add(starRatingField);
			add(costField);
			add(returnToMainButton);
			add(returnToPreviousButton);

			repaint();
		}



		if (evt.getSource () == toDeleteSelectionButton||(evt.getSource () == returnToPreviousButton && (deleteUserScreen == true||deleteBookScreen==true) && checkedOutListScreen==false))
		{
			//set booleans to display appropriate screens
			mainScreen=false;
			deleteSelectionScreen=true;
			deleteUserScreen=false;
			deleteBookScreen=false;

			removeComponentsAndText();

			//add components to screen
			add(toDeleteUserButton);
			add(toDeleteBookButton);
			add(returnToMainButton);

			repaint();
		}

		if (evt.getSource () == toDeleteUserButton||evt.getSource () == returnToPreviousButton && deleteUserScreen == true && checkedOutListScreen==true)
		{
			//set booleans to display appropriate screens
			deleteUserScreen=true;
			deleteSelectionScreen=false;
			checkedOutListScreen=false;
			userRelated=true;
			bookRelated=false;
			//make current list the full list of users in the system
			currentListOfUsers=Library.getListOfUsers();

			removeComponentsAndText();

			//add components to the screen
			add(searchButton);
			add(searchField);
			add(userSearchBySelector);
			add(returnToMainButton);
			add(returnToPreviousButton);

			addList();

			repaint();
		}

		if (evt.getSource () == toDeleteBookButton)
		{
			//set booleans to display appropriate screens
			deleteBookScreen=true;
			deleteSelectionScreen=false;
			userRelated=false;
			bookRelated=true;
			//make current list the full list of books in the system
			currentListOfBooks=Library.getListOfBooks();

			removeComponentsAndText();

			//add components to the screen
			add(searchButton);
			add(searchField);
			add(bookSearchBySelector);
			add(returnToMainButton);
			add(returnToPreviousButton);

			addList();

			repaint();
		}

		if (evt.getSource () == toBrowseSelectionButton||evt.getSource () == returnToPreviousButton && (browseUserScreen == true||browseBookScreen==true) && checkedOutListScreen==false)
		{
			//set booleans to display appropriate screens
			mainScreen=false;
			browseSelectionScreen=true;
			browseUserScreen=false;
			browseBookScreen=false;

			removeComponentsAndText();

			//add components to the screen
			add(toBrowseUserButton);
			add(toBrowseBookButton);
			add(returnToMainButton);

			repaint();
		}

		if (evt.getSource () == toBrowseUserButton||evt.getSource () == returnToPreviousButton && browseUserScreen == true && checkedOutListScreen==true)
		{
			//set booleans to display appropriate screens
			browseUserScreen=true;
			browseSelectionScreen=false;
			checkedOutListScreen=false;
			userRelated=true;
			bookRelated=false;
			//make current list the full list of users in the system
			currentListOfUsers=Library.getListOfUsers();

			removeComponentsAndText();

			//add components to the screen
			add(searchButton);
			add(searchField);
			add(userSearchBySelector);
			add(returnToMainButton);
			add(returnToPreviousButton);

			addList();

			repaint();
		}

		if (evt.getSource () == toBrowseBookButton)
		{
			//set booleans to display appropriate screens
			browseBookScreen=true;
			browseSelectionScreen=false;
			userRelated=false;
			bookRelated=true;
			//make current list the full list of books in the system
			currentListOfBooks=Library.getListOfBooks();

			removeComponentsAndText();

			//add components to the screen
			add(searchButton);
			add(searchField);
			add(bookSearchBySelector);
			add(returnToMainButton);
			add(returnToPreviousButton);

			addList();

			repaint();
		}

		if (evt.getSource () == toCompareBookButton)
		{
			compareBookScreen=true;
			mainScreen=false;
			userRelated=false;
			bookRelated=true;
			//make current list the full list of books in the system
			currentListOfBooks=Library.getListOfBooks();

			removeComponentsAndText();

			//add components to the screen
			add(searchButton);
			add(searchField);
			add(bookSearchBySelector);
			add(returnToMainButton);

			addList();

			repaint();
		}


		////////////////////////////////////////////
		///////////IMPORTANT BUTTONS////////////////
		////////////////////////////////////////////


		//switches to next page of the current list
		if (evt.getSource () == nextPageButton)
		{
			pageNum+=1;
			successful=false;

			/**	When switching to the last page of list, removes next page button and 
			 * 	remove list buttons on screen to match number of users/books on page.
			 * 	(ex. 10 users/books on page = 10 of each list button on screen, so 3 list buttons are removed)
			 */
			if (pageNum>=numOfPages)
			{
				remove(nextPageButton);
				if (userRelated==true)					//remove user related list buttons
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfUsers.size()-(pageNum-1)*MAX_OBJECTS_ONSCREEN;x--)
					{
						remove(checkOutButton[x]);
						remove(returnButton[x]);
						remove(checkedOutListButton[x]);
						remove(deleteButton[x]);
						remove(payFinesButton[x]);
						remove(compareButton[x]);
					}
				}
				else if (bookRelated==true)			//remove book related list button
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfBooks.size()-(pageNum-1)*MAX_OBJECTS_ONSCREEN;x--)
					{
						remove(checkOutButton[x]);
						remove(returnButton[x]);
						remove(deleteButton[x]);
						remove(replaceBookButton[x]);
						remove(compareButton[x]);
					}
				}
			}
			add(previousPageButton);

			repaint();
		}

		//switches to previous page of the current list
		if (evt.getSource () == previousPageButton)
		{
			pageNum-=1;
			successful=false;
			/**	When switching from the last page of the list, add list buttons on screen to match max number of users/books on page 
			 * 	(ex. 10 users/books on last page = 10 list buttons on screen, so 3 list buttons added to make 13)
			 */
			if (pageNum+1==numOfPages)
			{
				if (userRelated==true)				//add user related list buttons
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfUsers.size()%MAX_OBJECTS_ONSCREEN;x--)
					{
						add(checkedOutListButton[x]);
						//adds list buttons depending on which screen program is on
						if (checkOutScreen==true)
							add(checkOutButton[x]);
						else if (returnScreen==true)
							add(returnButton[x]);
						else if (deleteUserScreen==true)
							add(deleteButton[x]);
						else if (browseUserScreen==true)
							add(payFinesButton[x]);
						else if (compareBookScreen==true)
							add(compareButton[x]);
					}
				}
				else if (bookRelated==true)			//add book related list buttons
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfBooks.size()%MAX_OBJECTS_ONSCREEN;x--)
					{
						//adds list buttons depending on which screen program is on
						if (checkOutScreen==true)
							add(checkOutButton[x]);
						else if (returnScreen==true)
							add(returnButton[x]);
						else if (deleteBookScreen==true)
							add(deleteButton[x]);
						else if (browseBookScreen==true)
							add(replaceBookButton[x]);
						else if (compareBookScreen==true)
							add(compareButton[x]);
					}
				}
			}
			//when switching to the first page of the list, remove previous page button 
			if (pageNum<=1)
				remove(previousPageButton);
			add(nextPageButton);

			repaint();
		}

		//return book and calculate any overdue fines and show results of the return process
		if (evt.getSource() == calcFineButton)
		{
			//reset error checker so errors aren't displayed until they come up
			stringToNumError[0]=false;

			//convert days checked out entry into an integer, display error message if not possible
			try
			{
				Integer.parseInt(daysCheckedOutField.getText());
			}
			catch (NumberFormatException e)
			{
				stringToNumError[0]=true;
				daysCheckedOutField.setText("");
			}

			//consider book returned as lost if user lost the book
			if (lostYes.getState()==true)
				tempBook.setLost(true);

			//if there aren't any errors, carry out return
			if (stringToNumError[0]==false)
			{
				totalFine = Library.returnBookAndCalcFines(Integer.parseInt(daysCheckedOutField.getText()), tempUser, tempBook);
				//set booleans to display appropriate screens
				fineCalcScreen=false;
				finishScreen=true;

				removeComponentsAndText();

				//add components to the screen
				add(returnToMainButton);
			}

			repaint();
		}

		//creates a user or book object and adds it to the library system
		if (evt.getSource () == createButton)
		{
			//reset error checkers to false so error messages aren't displayed until an error comes up
			emptyTextFields=false;
			duplicate=false;
			successful=false;
			for (int x=0;x<stringToNumError.length;x++)
			{
				stringToNumError[x]=false;
				invalidValues[x]=false;
			}

			//creates a user and adds it to the system if there aren't any errors
			if (userRelated==true)
			{
				int stuNum=0;

				//displays error message if any of the text fields are not filled in
				if (firstNameField.getText().equals("")||lastNameField.getText().equals("")||studentNumberField.getText().equals(""))
					emptyTextFields=true;

				//convert student number entry into an integer, display error message if not possible
				try
				{
					stuNum = Integer.parseInt(studentNumberField.getText());
				}
				catch (NumberFormatException e)
				{
					stringToNumError[0]=true;
					studentNumberField.setText("");
				}

				//displays error message if student number entry was converted properly, but is invalid
				invalidValues[0]=Library.invalidateUserValues(studentNumberField.getText());

				/**If there aren't any errors, adds a User object to library system using information entered into the Text Fields 
				 * Displays error message if duplicate
				 * Does not check if duplicate if they are no Users in Library's list of users
				 */
				if (emptyTextFields==false&&invalidValues[0]==false)
				{
					//adds a User object to library system using information entered into the Text Fields if the User is not a duplicate
					//displays error message if duplicate
					//does not check if duplicate if they are no Users in Library's list of users
					if (Library.getListOfUsers().isEmpty()==true||Library.isDuplicateUser(stuNum)==false)
					{
						Library.createUser(firstNameField.getText(), lastNameField.getText(), stuNum);
						successful=true;

						//clear text fields
						firstNameField.setText("");
						lastNameField.setText("");
						studentNumberField.setText("");
					}
					else
					{
						duplicate=true;
						studentNumberField.setText("");
					}

				}
			}
			//creates a book and adds it to the system if there aren't any errors
			else if (bookRelated==true)
			{
				String category=categorySelector.getSelectedItem();
				long ISBN=0;
				int starRating=0;
				double cost=0;


				//displays error message if any of the text fields are not filled in
				if (titleField.getText().equals("")||firstNameField.getText().equals("")||lastNameField.getText().equals("")||ISBNField.getText().equals("")
						||starRatingField.getText().equals("")||costField.getText().equals(""))
					emptyTextFields=true;

				//converts ISBN entry into a long, display error message if not possible
				try
				{
					ISBN = Long.parseLong(ISBNField.getText());
				}
				catch (NumberFormatException e)
				{
					stringToNumError[0]=true;
					ISBNField.setText("");
				}

				//converts star rating entry into an integer, display error message if not possible
				try
				{
					starRating=Integer.parseInt(starRatingField.getText());
				}
				catch (NumberFormatException e)
				{
					stringToNumError[1]=true;
					starRatingField.setText("");
				}

				//convert cost entry into a double, display error message if not possible
				try
				{
					cost = Double.parseDouble(costField.getText());
				}
				catch (NumberFormatException e)
				{
					stringToNumError[2]=true;
					costField.setText("");
				}

				//displays error message if ISBN, star rating or cost entries were converted properly, but are invalid values 
				invalidValues=Library.invalidateBookValues(ISBNField.getText(), starRating, cost);

				/**If there aren't any errors, adds a Book object to library system using information entered into the Text Fields 
				 * Displays error message if duplicate
				 * Does not check if duplicate if they are no Books in Library's list of books
				 */
				if (emptyTextFields==false&&invalidValues[0]==false&&invalidValues[1]==false&&invalidValues[2]==false)
				{
					if (Library.getListOfBooks().isEmpty()==true||Library.isDuplicateBook(ISBN)==false)
					{
						Library.createBook(titleField.getText(), firstNameField.getText(), lastNameField.getText(), category,
								ISBN, ISBNField.getText().length(),starRating, cost);
						successful=true;

						//clear text fields
						titleField.setText("");
						firstNameField.setText("");
						lastNameField.setText("");
						ISBNField.setText("");
						starRatingField.setText("");
						costField.setText("");
					}
					else
					{
						duplicate=true;
						ISBNField.setText("");
					}
				}
			}
			repaint();
		}


		//search library for users/books that match inputted search parameters
		if (evt.getSource() == searchButton)
		{
			//reset error checkers
			noResults=false;
			stringToNumError[0]=false;
			invalidValues[0]=false;
			//search users for matching information (first names, last names, or student numbers)
			if (userRelated==true)
			{
				//determines the type of information the program should searches through based on search selector
				if (userSearchBySelector.getSelectedItem()=="First Name")								
					currentListOfUsers=Library.searchUser(searchField.getText(), 0);
				else if (userSearchBySelector.getSelectedItem()=="Last Name")						
					currentListOfUsers=Library.searchUser(searchField.getText(), 1);
				else if (userSearchBySelector.getSelectedItem()=="Student Number (Full)")	
				{
					//converts student number entry into an integer, display error message if not possible
					try
					{
						Integer.parseInt(searchField.getText());
					}
					catch (NumberFormatException e)
					{
						stringToNumError[0]=true;
					}

					//displays error message if student number entry was converted properly, but is invalid
					invalidValues[0]=Library.invalidateUserValues(searchField.getText());

					//execute search if there aren't any errors
					if (stringToNumError[0]==false && invalidValues[0]==false)
						currentListOfUsers=Library.searchUser(Integer.parseInt(searchField.getText()));
					else
						searchField.setText("Enter a 6 digit number");
				}

				//displays error message if search returns no results
				if (currentListOfUsers.isEmpty()==true)
					noResults=true;
			}
			//search books for matching information (title, author, category, or ISBN)
			else if (bookRelated==true)
			{
				//determines the type of information the program should searches through based on search selector
				if (bookSearchBySelector.getSelectedItem()=="Title")
					currentListOfBooks=Library.searchBook(searchField.getText(), 0);
				else if (bookSearchBySelector.getSelectedItem()=="Author's First Name")
					currentListOfBooks=Library.searchBook(searchField.getText(), 1);
				else if (bookSearchBySelector.getSelectedItem()=="Author's Last Name")
					currentListOfBooks=Library.searchBook(searchField.getText(), 2);
				else if (bookSearchBySelector.getSelectedItem()=="Category")
					currentListOfBooks=Library.searchBook(searchField.getText(), 3);
				else if (bookSearchBySelector.getSelectedItem()=="ISBN (Full)")
				{
					//converts ISBN entry into a long, display error message if not possible
					try
					{
						Long.parseLong(searchField.getText());
					}
					catch (NumberFormatException e)
					{
						stringToNumError[0]=true;
					}

					//displays error message if ISBN entry was converted properly, but is invalid
					invalidValues[0]=Library.invalidateBookValues(searchField.getText(),0,0)[0];

					//execute search if there aren't any errors
					if (stringToNumError[0]==false && invalidValues[0]==false)
						currentListOfBooks=Library.searchBook(Long.parseLong(searchField.getText()));
					else
						searchField.setText("Enter a 10 or 13 digit number");
				}
				//displays error message if search returns no results
				if (currentListOfBooks.isEmpty()==true)
					noResults=true;
			}

			addList();

			repaint();

			stringToNumError[0]=false;
		}


		////////////////////////////////////
		/////////BUTTON ARRAYS//////////
		///////////////////////////////////

		int index;		//the index  of the selected user/book in the current list of users/books

		//checks if a button from the various arrays of buttons is pressed
		for (int n=0;n<MAX_OBJECTS_ONSCREEN;n++)
		{
			//determine the index of the user/book corresponding to the button that was pressed
			index = n+MAX_OBJECTS_ONSCREEN*(pageNum-1);


			//checks the selected book out to the selected user
			if (evt.getSource () == checkOutButton[n])
			{
				//selects which book is being checked out (selected book is the book in the same row as this button on screen)
				if (bookRelated==true)
				{
					tempBook = currentListOfBooks.get(index);

					//check for any reason book can't be checked out
					checkReturnError = Library.catchCheckOutErrors(tempBook);

					//if there aren't any errors, switches to screen to selects which user is being checking out the book
					if (checkReturnError[0]==false && checkReturnError [1]==false)
					{
						//set booleans to display appropriate screens
						userRelated=true; 
						bookRelated=false;
						//make current list the full list of users in the system
						currentListOfUsers=Library.getListOfUsers();

						//remove search function
						remove(bookSearchBySelector);

						//add components to the screen
						add(userSearchBySelector);
						add(returnToPreviousButton);

						addList();
					}
				}
				//selects which user is checking the book out (selected user is the user in the same row as this button on screen)
				else if (userRelated==true)
				{
					tempUser = currentListOfUsers.get(index);

					//check for any reason user can't check a book out
					checkReturnError = Library.catchCheckOutErrors(tempUser);

					//if there aren't any error, checks the book out to the user and show results of the check out process
					if (checkReturnError[0]==false && checkReturnError [1]==false)
					{
						Library.checkOutBook(tempUser, tempBook);

						//set booleans to display appropriate screens
						finishScreen=true;

						removeComponentsAndText();

						//add components to the screen
						add(returnToMainButton);
					}
				}
				repaint();
			}


			//returns the selected book checked out by the selected user bakc to the library
			//does not carry out actual return but selects which book and user are involved
			if (evt.getSource () == returnButton[n])
			{
				//selects which user is returning the book (selected user is the user in the same row as this button on screen)
				if (userRelated==true)
				{
					tempUser = currentListOfUsers.get(index);

					//check for any reason user can't return a book (user does not any books checked out
					checkReturnError[0] = Library.catchReturnError(tempUser);

					//if there aren't any errors, switches to screen to selects which user is being checking out the book
					if (checkReturnError[0]==false)
					{
						//set booleans to display appropriate screens
						userRelated=false; 
						bookRelated=true;
						currentListOfBooks=tempUser.getBooksCheckedOut();

						//remove the search related display objects
						removeComponentsAndText();

						//add components to the screen
						add(returnToMainButton);
						add(returnToPreviousButton);

						addList();
					}
				}
				//selects which book is being returned (selected book is the book in the same row as this button on screen)
				//switches to screen to calculate fines to finish return process
				else if (bookRelated==true)
				{
					tempBook=currentListOfBooks.get(index);

					//set booleans to display appropriate screens
					fineCalcScreen=true;
					bookRelated=false;

					removeComponentsAndText();

					//add components to the screen
					add(calcFineButton);
					add(daysCheckedOutField);
					add(lostYes);
					add(lostNo);
					add(returnToMainButton);
				}
				repaint();
			}


			//show all of the books the selected user has checked out (selected user is the user in the same row as this button on screen)
			if (evt.getSource () == checkedOutListButton[n])
			{
				//set booleans to display appropriate screens
				checkedOutListScreen=true;
				userRelated=false;
				bookRelated=true;
				successful=false;
				//make current list the list of books the selected user has checked out
				currentListOfBooks=currentListOfUsers.get(index).getBooksCheckedOut();

				removeComponentsAndText();

				//add components to the screen
				add(returnToMainButton);
				add(returnToPreviousButton);

				addList();

				repaint();
			}


			//delete the selected user/book from the screen
			if (evt.getSource () == deleteButton[n])
			{
				if (userRelated==true)				//delete user
				{
					Library.deleteUser(currentListOfUsers.get(index));
					currentListOfUsers=Library.getListOfUsers();
				}
				else if (bookRelated==true)		//delete book
				{
					Library.deleteBook(currentListOfBooks.get(index));
					currentListOfBooks=Library.getListOfBooks();
				}
				successful=true;

				addList();

				repaint();
			}


			//clear the selected user's balance
			if (evt.getSource() == payFinesButton[n])
			{
				//reset error checkers to false
				actionUnnecessary=false;
				successful=false;

				//clear the selected user's balance if they have fines to pay off, display error message if they don't
				if (currentListOfUsers.get(index).getBalance() > 0)
				{
					tempUser = currentListOfUsers.get(index);
					Library.payFines(tempUser);

					successful=true;
				}
				else
					actionUnnecessary=true;

				repaint();
			}

			//replaces the selected lost book
			if (evt.getSource() == replaceBookButton[n])
			{

				//reset error checkers to false
				actionUnnecessary=false;
				successful=false;

				//replace the selected lost book, display error message if the book does not need to be replaced
				if (currentListOfUsers.get(index).getBalance() > 0)
				{
					tempBook = currentListOfBooks.get(index);
					Library.replaceBook(tempBook);

					successful=true;
				}
				else
					actionUnnecessary=true;

				repaint();
			}


			//select two books to compare to see which is better
			if (evt.getSource()==compareButton[n])
			{
				//stores first book selected in a temporary book object and inform user that the book has been selected
				if (booksSelected == 0 ||booksSelected == 2)
				{
					tempBook = currentListOfBooks.get(index);
					booksSelected=1;

					repaint();
				}
				//compare the second book selected with the first book and prints which is better
				else if (booksSelected==1)
				{
					//reset error checker
					alreadySelected=false;

					//compares books unless the same book is selected twice, in which case display error message
					if (tempBook != currentListOfBooks.get(index))
					{
						tempBook = Library.compareBooks(tempBook, currentListOfBooks.get(index));
						booksSelected=2;
					}
					else
						alreadySelected = true;

					repaint();
				}
			}
		}


		//return to the main menu from any screen
		if (evt.getSource () == returnToMainButton)
		{
			booksSelected=0;

			//set booleans to display appropriate screens
			mainScreen=true;
			checkOutScreen=false;
			returnScreen=false;
			fineCalcScreen=false;
			finishScreen=false;
			createSelectionScreen=false;
			createUserScreen=false;
			createBookScreen=false;
			deleteSelectionScreen=false;
			deleteUserScreen=false;
			deleteBookScreen=false;
			browseSelectionScreen=false;
			browseUserScreen=false;
			browseBookScreen=false;
			compareBookScreen=false;
			checkedOutListScreen=false;

			removeComponentsAndText();

			//add components to the screen
			add(toCheckOutButton);
			add(toReturnBookButton);
			add(toCreateSelectionButton);
			add(toDeleteSelectionButton);
			add(toBrowseSelectionButton);
			add(toCompareBookButton);

			repaint();
		}
	}
}