package librarysystem;

import java.util.ArrayList;
/**
 * 
 */


/**	The Library Class
 * 
 * 	Stores the lists of all users and books registered in the library system and execute most of the functions required 
 * 	to run the library (e.g. check out, return, user/book creation and deletion, etc)
 * 
 */
public class Library {
	private static ArrayList <User>listOfUsers=new ArrayList<User>(); 	//the list of all the users stored/registered in the system
	private static ArrayList <Book>listOfBooks=new ArrayList<Book>(); 	//the list of all the books stored/registered in the system

	private static final int MAX_BOOKS=3; 			//the maximum number of Books a User can have
	private static final int MAX_CHECKOUT_TIME=14; 	//the maximum number of days a user can have a book checked out until they start getting overdue fines
	private static final double OVERDUE_FINE=0.1;  	//the amount of money the user is charged each day a book is overdue;
	private static final double FINE_LIMIT=5; 		//the maximum value of overdue fines in dollars a user can have until they can't checkout books anymore;

	/**
	 * @return the listOfUsers
	 */
	public static ArrayList<User> getListOfUsers() {
		return listOfUsers;
	}

	/**
	 * @return the listOfBooks
	 */
	public static ArrayList<Book> getListOfBooks() {
		return listOfBooks;
	}


	/**determine if the numbers being used to create new users are invalid (incorrect digits)
	 * @param stuNum, the student number of the user to be created
	 * @return whether the student number is invalid
	 */
	public static boolean invalidateUserValues (String stuNum)
	{
		//determine if received value is invalid or not 
		if (stuNum.length()!=6)						//not 6 digits long
			return true;
		return false;
	}


	/**determine if  the numbers being used to create new books are invalid (incorrect digits, range, etc)
	 * @param ISBN, the ISBN of the book to be created
	 * @param star, the star rating of the book to be created
	 * @param cost, the cost of the book to be created
	 * @return invalidValues, whether the numbers being used are invalid
	 */
	public static boolean[] invalidateBookValues (String ISBN, int star, double cost)
	{
		boolean [] invalidValues = new boolean [3];

		//determine if received values are invalid or not 
		if (ISBN.length()!=10&&ISBN.length()!=13)	//not 10 or 13 digits long
			invalidValues[0]=true;
		if (star<1||star>5)							//not 1-5
			invalidValues[1]=true;
		if (cost*100%1!=0)							//more then 2 decimal places
			invalidValues[2]=true;

		return invalidValues;
	}



	/**create a User object and add it to the system
	 * @param firstName, the first name of the new user
	 * @param lastName, the last name of the new user
	 * @param studentNumber, the student number of the new user
	 */
	public static void createUser (String firstName, String lastName, int studentNumber){
		User u = new User (firstName, lastName, studentNumber);
		listOfUsers.add(u);
	}


	/**	Determine if user to be created is already in the system by checking through list of users in the system 
	 * 	to see if student number of user to be created matches student number of an existing user.
	 * 	If it matches, user to be created is a duplicate user
	 * @param studentNumber, the student number of the user to be created
	 * @return whether the user is a duplicate or not
	 */
	public static boolean isDuplicateUser(int studentNumber){
		for (int x=0; x<listOfUsers.size(); x++)
		{
			if (listOfUsers.get(x).equals(studentNumber))
				return true;
		}
		return false;
	}


	/**create a Book object and add it to the system
	 * @param title, the title of the new book
	 * @param author, the author of the new book
	 * @param category, the category of the new book
	 * @param ISBN, the ISBN of the new book
	 * @param starRating, the star rating of the new book
	 * @param cost, the cost of the new book
	 */
	public static void createBook (String title, String authorFName, String authorLName,String category, long ISBN, int ISBNLength,
			int starRating, double cost){
		Book b = new Book (title, authorFName, authorLName, category, ISBN, ISBNLength, starRating, cost);
		listOfBooks.add(b);
	}


	/**	Determine if book to be created is already in the system by checking through list of books in the system 
	 * 	to see if ISBN of book to be created matches ISBN of an existing book.
	 * 	If it matches, book to be created is a duplicate book
	 * @param ISBN, the ISBN of the book to be created
	 * @return whether the book is a duplicate or not
	 */
	public static boolean isDuplicateBook(long ISBN){
		for (int x=0; x<listOfBooks.size(); x++)
		{
			if (listOfBooks.get(x).equals(ISBN))
				return true;
		}
		return false;
	}



	/**delete the specified User object from the system
	 * @param u, the user to be deleted
	 */
	public static void deleteUser (User u){
		listOfUsers.remove(u);
	}


	/**delete the specified Book object from the system
	 * @param b, the book to be deleted
	 */
	public static void deleteBook (Book b){
		listOfBooks.remove(b);
	}



	/**searches through library system for a certain User
	 * @param studentNumber, the student number of the user
	 * @return, all the users with the same student number (should be only be one)
	 */
	public static ArrayList<User> searchUser (int studentNumber){
		ArrayList<User> matchingStudents = new ArrayList<User>();
		//search through listOfUsers for users with matching student numbers and add them to the matchingStudents list
		for (int x=0;x<listOfUsers.size();x++)
		{
			if (listOfUsers.get(x).equals(studentNumber))
				matchingStudents.add(listOfUsers.get(x));
		}
		return matchingStudents;
	}

	/**searches through library system for a certain User
	 * @param name, the name of the user
	 * @param firstLast, whether the name is the user's first name or last name (0 is first, 1 is last) 
	 * @return matchingStudents, all the users with the same (first/last) name
	 */
	public static ArrayList<User> searchUser (String name, int firstLast){
		ArrayList<User> matchingStudents = new ArrayList<User>();
		//search through listOfUsers for users with matching names and add them to the matchingStudents list
		for (int x=0;x<listOfUsers.size();x++)
		{
			if (listOfUsers.get(x).fieldContains(name, firstLast))
				matchingStudents.add(listOfUsers.get(x));
		}
		return matchingStudents;
	}


	/**searches through library system for a certain Book
	 * @param ISBN, the student number of the user
	 * @return matchingBooks, all the books with the same ISBN (should only be one)
	 */
	public static ArrayList<Book> searchBook (long ISBN){
		ArrayList<Book> matchingBooks = new ArrayList<Book>();
		//search through listOfBooks for books with matching ISBNs and add them to the matchingBooks list
		for (int x=0;x<listOfBooks.size();x++)
		{
			if (listOfBooks.get(x).equals(ISBN))
			{
				System.out.println(listOfBooks.get(x));
				matchingBooks.add(listOfBooks.get(x));
			}
		}
		return matchingBooks;
	}


	/**searches through library system for a list of Books
	 * @param name, the title, author or category of the Book
	 * @param TAC, whether name is the title (TAC=0), author's first name (TAC=1), author's last name (TAC=2) or category (TAC==3)
	 * @return matchingBooks, all the books with the same title/author/category
	 */
	public static ArrayList<Book> searchBook (String name, int TAC){
		ArrayList<Book> matchingBooks = new ArrayList<Book>();
		//search through listOfBooks for books with matching title/author/category and add them to the matchingBooks list
		for (int x=0;x<listOfBooks.size();x++)
		{
			if (listOfBooks.get(x).fieldContains(name,TAC))
				matchingBooks.add(listOfBooks.get(x));
		}
		return matchingBooks;
	}

	/**
	 * compares two books by star rating to see which is better
	 * @param b1 the first book being compared
	 * @param b2 the second book being compared
	 * @return the better book, null if their star ratings are equal 
	 */
	public static Book compareBooks(Book b1, Book b2)
	{
		if (b1.getStarRating() > b2.getStarRating())
			return b1;
		else if (b1.getStarRating() < b2.getStarRating())
			return b2;
		return null;
	}

	/**check for any errors that come up while checking out a book (book related)
	 * @param b, the book selected to be checked out
	 * @return checkOutError, whether any errors came up stored in an array of booleans
	 */
	public static boolean[] catchCheckOutErrors (Book b)
	{
		boolean[] checkOutError = new boolean[2];

		if (b.isLost()==true)							//book has been lost
			checkOutError[0]=true;
		else if (b.isCheckedOut()==true)				//book has already been checked out by one of the users
			checkOutError[1]=true;

		return checkOutError;
	}

	/**check for any errors that come up while checking out a book (user related)
	 * @param u, the user selected to check out the book
	 * @return checkOutError, whether any errors came up stored in an array of booleans
	 */
	public static boolean[] catchCheckOutErrors (User u)
	{
		boolean[] checkOutError = new boolean[2];

		if (u.getBooksCheckedOut().size()>=MAX_BOOKS)	//user already has 3 books checked out
			checkOutError[0]=true;
		else if (u.getBalance()>FINE_LIMIT)				//user has a fine balance of over $5.00
			checkOutError[1]=true;

		return checkOutError;
	}

	/**check for any errors that come up while returning a book (user related)
	 * @param u, the user selected to return a book
	 * @return whether any errors came up
	 */
	public static boolean catchReturnError (User u)
	{

		if (u.getBooksCheckedOut().isEmpty())			//user has no books to return
			return true;
		return false;
	}



	/**	Check out a book to a user and add it to user's list of checked out books
	 * 	Does not check out the book if book is already checked out, the user's overdue fines balance exceeds $5.00
	 * 	or the user already has 3 books checked out
	 * @param u, the user the book is being checked out to
	 * @param b, the book being checked out
	 */
	public static void checkOutBook (User u, Book b){
		if (b.isCheckedOut()==false && u.getBalance()<FINE_LIMIT && u.getBooksCheckedOut().size()<MAX_BOOKS)
		{
			u.addBook(b);
			b.setCheckedOut(true);
		}
	}


	/**return the specified book and calculate any overdue fines
	 * @param daysCheckedOut, the number of days the book has been checked out
	 * @param u, the user returning the book
	 * @param b, the book being returned
	 * @return totalFine, the total value of all the fines added to the user's balance
	 */
	public static double returnBookAndCalcFines (int daysCheckedOut,User u, Book b){
		double totalFine=0;

		//calculate total fines
		if (daysCheckedOut>MAX_CHECKOUT_TIME)			//adds $0.10 for every day the user has the book checked out past 2 weeks
			totalFine+=OVERDUE_FINE*(daysCheckedOut-MAX_CHECKOUT_TIME);
		if (b.isLost()==true)							//adds the cost of the book to the user's balance if they lost it
			totalFine+=b.getCost();
		totalFine = Math.floor(totalFine*100)/100;			//rounds total fines to two decimal places due to some weird calculation problems
		u.changeBalance(totalFine);

		//delete book from user's list of checked out books and return book
		u.deleteBook(b);
		b.setCheckedOut(false);

		return totalFine;
	}
	
	/** Clear the received user's balance when they pay off their overdue fines
	 * @param u, the user that is paying off the overdue fines
	 */
	public static void payFines(User u){
		u.changeBalance(- u.getBalance());
	}
	
	/** Replace a lost book registered in the system
	 * @param b, the lost book being replaced
	 */
	public static void replaceBook(Book b){
		b.setLost(false);
	}
}
