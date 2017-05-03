
/**
 * Generates random card suits
 *
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie>
 * @date 29th December 2005
 *
 * Last Modified: December 19th 2006
 */

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.*;
import java.io.*;

public class RandomSuitGenerator
{

//----------CONSTANTS----------------//

//-------CLASS VARIABLES-------------//

//------INSTANCE VARIABLES-----------//

/**
 * A random number generator
 */
private RandomNumGenerator random = new RandomNumGenerator(4);


/**
 * The random suits will be stored here
 */
public String[] suits;


/**
 * Indexes the suits array
 */
public int suitIndex;


/**
 * Keeps track of the random number generator
 */
//private int randomIndex = random.nextNumber();

//---------CONSTRUCTORS--------------//

/**
 * Constructor
 */
public RandomSuitGenerator()
{
    suits = new String[20];
    //random = new RandomNumGenerator(3);
    suitIndex = 0;
    generateRandomSuits();
}

//--------CLASS METHODS--------------//

//---------SET METHODS---------------//

/**
 * Generate four random suits
 * and store them in the suits array
 */
public void generateRandomSuits()
{
    suits[0] = new String(parseSuit(random.nextNumber()));
    suits[1] = new String(parseSuit(random.nextNumber()));
    suits[2] = new String(parseSuit(random.nextNumber()));
    suits[3] = new String(parseSuit(random.nextNumber()));
    suits[4] = new String(parseSuit(random.nextNumber()));
    suits[5] = new String(parseSuit(random.nextNumber()));
    suits[6] = new String(parseSuit(random.nextNumber()));
    suits[7] = new String(parseSuit(random.nextNumber()));
    suits[8] = new String(parseSuit(random.nextNumber()));
    suits[9] = new String(parseSuit(random.nextNumber()));
    suits[10] = new String(parseSuit(random.nextNumber()));
    suits[11] = new String(parseSuit(random.nextNumber()));
    suits[12] = new String(parseSuit(random.nextNumber()));
    suits[13] = new String(parseSuit(random.nextNumber()));
    suits[14] = new String(parseSuit(random.nextNumber()));
    suits[15] = new String(parseSuit(random.nextNumber()));
    suits[16] = new String(parseSuit(random.nextNumber()));
    suits[17] = new String(parseSuit(random.nextNumber()));
    suits[18] = new String(parseSuit(random.nextNumber()));
    suits[19] = new String(parseSuit(random.nextNumber()));
}

/**
 * Returns a string representation of
 * the suit, i.e. 1 would be hearts,
 * 2 would be diamonds etc.
 * @param value The value to be parsed
 * @return A string representation of the integer value
 */
private String parseSuit(int value)
{
    switch(value)
    {
        case 0:
            return "s";
        case 1:
            return "h";
        case 2:
            return "d";
        case 3:
            return "s";
        case 4:
            return "c";
        default:
            return "h";
    }
}

//---------GET METHODS---------------//

/**
 * Return a string representation of the suits
 * array after it has been filled
 */
private void checkSuitArray()
{
	int index = 0;

	while(index < suits.length)
	{
		for(int i = 0; i <= suits.length; i++)
		{
			JOptionPane.showMessageDialog(null, ""+suits[index], "Testing the suits array", JOptionPane.INFORMATION_MESSAGE);
			index++;
		}
	}
}

//-------INSTANCE METHODS------------//

//--------OTHER METHODS--------------//

//---------TEST METHODS--------------//

/**
 * Test method
 * @param args Command line arguments
 */
public static void main(String[] args)
{
    RandomSuitGenerator rsg = new RandomSuitGenerator();
    rsg.generateRandomSuits();

    //Extra test - Is the suits array filled
	rsg.checkSuitArray();

    //int index = 0;
    //int counter = 0;

    //while(counter != 4)
    //{
    //    JOptionPane.showMessageDialog(null, ""+rsg.suits[index], "Testing the Random Suit Generator", JOptionPane.INFORMATION_MESSAGE);
    //    index++;
    //    counter++;
    //}
    //System.exit(0);
}
}