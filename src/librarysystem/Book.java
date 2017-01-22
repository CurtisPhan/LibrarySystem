package librarysystem;
/**
 * 
 */

/** The Book Class
 *
 *		Creates Book object that stores all of the information necessary for a book registered in a library system to have
 *
 */
public class Book {
	private String title; 					//the title of the book
	private String authorFName; 			//the book's author's first name
	private String authorLName; 			//the book's author's first name
	private String category; 				//the category the book belongs in
	private long ISBN; 						// the International Standard Book Number of the book
	private int ISBNLength;					//the length of the ISBN (10 or 13 digits)
	private int starRating; 				//the star rating of the book
	private double cost; 					//the amount a user 
	private boolean checkedOut=false; 		//if the book is checked out by a user
	private boolean lost=false;				//if the book has been lost by the user


	//constructor method
	public Book (String title, String authorFName, String authorLName, String category, long ISBN, int ISBNLength, int starRating, double cost){
		this.title=title;
		this.authorFName=authorFName;
		this.authorLName=authorLName;
		this.category=category;
		this.ISBN=ISBN;
		this.ISBNLength=ISBNLength;
		this.starRating=starRating;
		this.cost=cost;
	}

	/**check to see if this Book object is the same as another Book object based on ISBN of the Books (same ISBNs = same Books)
	 * @param ISBN, the ISBN of the other Book
	 * @return whether the other Book object is the same as this Book
	 */
	public boolean equals(long ISBN){
		if (this.ISBN==ISBN)
			return true;
		return false;
	}

	/**check to see if this book object's title, author name or category contains a certain string, so the book can be searched for
	 * @param name, the title, author's name, or category searched for 
	 * @param TAC, whether name is the title (TAC=0), author's first name (TAC=1), author's last name (TAC=2) or category (TAC==3)
	 * @return whether this one of this book's attributes contains name 
	 */
	public boolean fieldContains(String name, int TAC){
		String compareName=""; //stores the user's first or last name
		
		//sets compareName to the book's title, author's first or last name, or category depending on what is being compared
		if (TAC==0) 								//title
			compareName=title.toLowerCase();
		else if (TAC==1)							//author's first name
			compareName=authorFName.toLowerCase();
		else if (TAC==2)							//author's last name
			compareName=authorLName.toLowerCase();
		else if (TAC==3)							//category
			compareName=category.toLowerCase();
		
		//check if the compareName starts with the searched name, ignoring case
		//if name is an empty string, returns true as well
		if (compareName.contains(name.toLowerCase())||name=="")
			return true;
		return false;
	}

	
	/**
	 * @param checkedOut the checkedOut to set
	 */
	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	/**
	 * @param lost the lost to set
	 */
	public void setLost(boolean lost) {
		this.lost = lost;
	}

	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the authorFName
	 */
	public String getAuthorFName() {
		return authorFName;
	}

	/**
	 * @return the authorLName
	 */
	public String getAuthorLName() {
		return authorLName;
	}


	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the iSBN
	 */
	public long getISBN() {
		return ISBN;
	}

	/**
	 * @return the iSBNLength
	 */
	public int getISBNLength() {
		return ISBNLength;
	}

	/**
	 * @return the starRating
	 */
	public int getStarRating() {
		return starRating;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @return the checkedOut
	 */
	public boolean isCheckedOut() {
		return checkedOut;
	}

	/**
	 * @return the lost
	 */
	public boolean isLost() {
		return lost;
	}
}
