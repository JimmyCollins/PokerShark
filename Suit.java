
/**
 * Abstraction of a card suit
 *
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie>
 * @date 17 November 2005
 */

public class Suit 
{
//----------CONSTANTS----------------//
    
/**
 * The four possible suits a card can be
 */
protected static final String CLUBS = "Clubs";
protected static final String SPADES = "Spades";
protected static final String HEARTS = "Hearts";
protected static final String DIAMONDS = "Diamonds";

//-------CLASS VARIABLES-------------//

//------INSTANCE VARIABLES-----------//

/**
 * The specified suit
 */
protected String suit;

//---------CONSTRUCTORS--------------//

/**
 * Allocates a suit Object
 * @param suit The specifed suit
 */
public Suit(String theSuit)
{
    suit = theSuit;
}

/**
 * No arguments constructor
 */
public Suit()
{
}

//--------CLASS METHODS--------------//

//---------SET METHODS---------------//

//---------GET METHODS---------------//

/**
 * Returns the suit
 * @returns the suit
 */
public String getSuit()
{
    return suit; 
}

/**
 * Returns the Colour of this suit
 */
public String getColour(String suit)
{
    if(suit.equals("HEARTS") || suit.equals("DIAMONDS") )
        return "red";
    else
        return "black";
}

//-------INSTANCE METHODS------------//

//--------OTHER METHODS--------------//

//---------TEST METHODS--------------//

/**
 * A test method
 * @param args Command line arguments
 */
public static void main(String[] args)
{
}

    
}
