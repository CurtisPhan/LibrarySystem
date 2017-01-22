package librarysystem;

import java.util.ArrayList;

/**
 * 
 */

/** The User Class
 *
 *		Creates User object that stores all of the information necessary for a user registered in a library system to have
 *
 */
public class User {
	private String firstName;		//the user's first name
	private String lastName;		//the user's last name
	private int studentNumber;		//the user's student number
	private double balance=0;		//the user's balance for fines
	private ArrayList <Book> booksCheckedOut=new ArrayList<Book> ();	//the list of books the user has checked out


	//constructor method
	public User(String firstName, String lastName, int studentNumber){
		this.firstName = firstName;
		this.lastName = lastName;
		this.studentNumber = studentNumber;
	}

	/**check to see if this User object is the same as another User object based on student numbers of the Users (same numbers=same Users)
	 * @param studentNumber, the student number of the other User
	 * @return whether the other User object is the same as this User
	 */
	public boolean equals(int studentNumber){
		if (this.studentNumber==studentNumber)
			return true;
		return false;
	}

	/**check to see if this User's name contains a certain string, so the user can be searched for
	 * @param name, the name to be searched for
	 * @param firstLast, whether the name is the user's first name or last name (0 is first, 1 is last) 
	 * @return whether this one of this user's attributes contains name 
	 */
	public boolean fieldContains(String name, int firstLast){
		String compareName="";		//stores the user's first or last name
		
		//sets compareName to the user's first or last name depending on what is being compared
		if (firstLast==0) 								//first name
			compareName=firstName.toLowerCase();
		else if (firstLast==1)							//last name
			compareName=lastName.toLowerCase();
		
		//check if the compareName contains the searched name, ignoring case
		//if name is an empty string, returns true as well
		if (compareName.startsWith(name.toLowerCase())||name=="")
			return true;
		return false;
	}


	/**add  a book to the user's array of books checked out
	 * @param b, the book to be added
	 */
	public void addBook(Book b){
		booksCheckedOut.add(b);
	}


	/**delete a book from the user's array of books checked out
	 * @param b, the book to be deleted
	 */
	public void deleteBook(Book b){
		booksCheckedOut.remove(b);
	}
	
	
	/**change the user's balance by a certain amount
	 *@param amount, the amount the balance is changed by
	 */
	public void changeBalance(double amount){
		balance+=amount;
	}
	
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the studentNumber
	 */
	public int getStudentNumber() {
		return studentNumber;
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @return the booksCheckedOut
	 */
	public ArrayList<Book> getBooksCheckedOut() {
		return booksCheckedOut;
	}

}
