
/**
 * The main class which will run a game of Poker
 *
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie>
 * @date Monday 21st November 2005, 7:22pm
 *
 * Last Edit: March 29th 2007 (Open Day)
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Dialog.*;
import java.text.*;


public class PokerMain implements Runnable
{

//----------CONSTANTS----------------//

//-------CLASS VARIABLES-------------//

//------INSTANCE VARIABLES-----------//


/**
 * A HandGenerator Object, acts as Dealer
 */
private HandGenerator handGen;


/**
 * The amount of chips the game user and computer
 * player has left - initially 1000
 */
public int userCash;
public int computerCash;


/**
 * The current amount bet
 */
public int amountBet = 0;


/**
 * The current amount raised
 */
public int amountRaised = 0;


/**
 * The current pot
 */
public int currentPot = 0;

/**
 * User fold
 */
public boolean userFold = false;

/**
 * Is the user all In?
 */
public boolean userAllIn = false;

/**
 * Users chips if All In & wheter the computer has called your all in bet
 */
public int allInAmount = 0;
public boolean computerCallAllIn = false;


/**
 * True if game user has checked
 */
public boolean userCheck = false;

/**
 * True if the user has called the computers last bet
 */
public boolean userCall = false;

/**
 * Has the game user bet?
 */
public boolean userBet = false;

/**
 * Has the user raised?
 */
public boolean userRaise = false;


/**
 * True if computer has checked
 */
public boolean computerCheck = false;

/**
 * Has the computer folded?
 */
private boolean computerFold = false;


/**
 * Holds the game users cards
 */
public Hand userCards;
public String userCard1;
public String userCard2;
public Card userHoleCard1;
public Card userHoleCard2;


/**
 * Holds the computer players cards
 */
public Hand computerCards;
public String compCard1;
public String compCard2;
public Card computerHoleCard1;
public Card computerHoleCard2;

/**
 * The three cards dealt on the flop
 */
private String flop1;
private String flop2;
private String flop3;
private Card flopCard1;
private Card flopCard2;
private Card flopCard3;


/**
 * Holds every card dealt so far
 * - Users hole cards
 * - Computer hole cards
 * - Flop
 * - Turn
 * - River
 */
public String[] allCardsDealt;
private int allCardsIndex;

/**
 * PokerGUI Object
 */
private PokerGUI gui;

/**
 * Information interface Object
 */
private InformationInterface igui;

/**
 * Hand Evaluator
 */
private HandEvaluator handEval = new HandEvaluator();

/**
 * What stage of dealing are we at?
 * Used to determine next sequence of cards to deal
 * - 1: Pre-Flop
 * - 2: On the flop
 * - 3: On the turn
 * - 4: On the River
 */
private int dealStage = 1;

/**
 * Is the game user involved in a hand?
 */
private boolean running = false;

/**
 * The game thread
 */
private Thread gameThread;

/**
 * An array of premium starting poker hands
 */
private String[] premiumHands;

/**
 * Controller
 */
private Controller controller;

/**
 * Has the user done something?
 */
public boolean userAction = false;

/**
 * Has the player exceed his chip amount
 */
public boolean playerOverLimit = false;


//---------CONSTRUCTORS--------------//

/**
 * Constructor
 */
public PokerMain(Controller c)
{
	//Debug
	//debug("NEW POKER MAIN OBJECT CREATED");
	controller = c;

	//Initialise the hand generator
	handGen = new HandGenerator();

	//Initialise the arrays containing the user's and computer's cards
	userCards = new Hand("");
	computerCards = new Hand("");

	//Initialise the array containing all the cards currently dealt from the deck
	allCardsDealt = new String[9];
	allCardsIndex = 0;

	//Premium hands array
	premiumHands = new String[60];
	makePremiumHands();


}

//--------CLASS METHODS--------------//

//---------SET METHODS---------------//

public void setGUI(PokerGUI p)
{
	gui = p;
}

/**
 * Initialise a game of poker
 *  - Called when the 'DEAL' button is clicked in the GUI
 */
public void initGame()
{

	//Deal two hole cards to the user and display them in the GUI,

		// -- Deal the users first hole card
		userCard1 = handGen.generate1Card();

		//TEST
		//JOptionPane.showMessageDialog(null, "User Hole Card 1: "+userCard1, "initGame() Test", JOptionPane.INFORMATION_MESSAGE);
		//debug("User hole card 1: "+userCard1);

		//Check this card is not already dealt -- This is the first card dealt
		// -- Add card to users hand
		userHoleCard1 = new Card(userCard1);
		userCards.addCard(userHoleCard1);
		// -- Add card to allCardsDealt array
		allCardsDealt[allCardsIndex] = userCard1;
		allCardsIndex++;


		// -- Deal the users second hole card
		userCard2 = handGen.generate1Card();

		//TEST
		//JOptionPane.showMessageDialog(null, "User Hole Card 2: "+userCard2, "initGame() Test", JOptionPane.INFORMATION_MESSAGE);
		//debug("User hole card 2: "+userCard2);

		//Check this card not already dealt
		if(checkCardNotDealt(userCard2) == false)
		{
			//Repeat last three steps
			// -- Add card to users hand
			userHoleCard2 = new Card(userCard2);
			userCards.addCard(userHoleCard2);
			// -- Add cards to allCardsDealt array
			allCardsDealt[allCardsIndex] = userCard2;
			allCardsIndex++;

		}
		else
		{
			//Deal another card
			userCard2 = handGen.generate1Card();

			//True if card has not been dealt before
			boolean success = false;

			while(success == false)
			{

				//Deal another card
				userCard2 = handGen.generate1Card();
				//Check it hasn't been dealt
				if(checkCardNotDealt(userCard2) == false)
				{
					// -- If not add to hand and allCardsDealt array
					userHoleCard2 = new Card(userCard2);
					userCards.addCard(userHoleCard2);
					// -- Add to allCardsDealt
					allCardsDealt[allCardsIndex] = userCard2;
					allCardsIndex++;
					// -- Success
					success = true;
				}

			}


		}


		//Update the images of the users cards in the GUI
		//debug("allCardsDealt[0]: "+allCardsDealt[0]);
		String card1 = parseCardImage(allCardsDealt[0]);
		//System.out.println("Card 1 Value: "+card1);

		//debug("allCardsDealt[1]: "+allCardsDealt[1]);
		String card2 = parseCardImage(allCardsDealt[1]);
		//System.out.println("Card 2 Value: "+card2);


		//Update the user hand images array
		gui.userHand[0] = GUICreationHelper.loadImage(card1);
		gui.userHand[1] = GUICreationHelper.loadImage(card2);

		//Update the screen message
		//gui.msgIndex++;
		gui.increment();
		//Repaint the screen
		//gui.repaint(220, 320, 300, 300);
		//gui.validate();
		//gui.show();
		//gui.invalidate();
		gui.repaint();




	//Deal two hole cards to the computer player, don't display in GUI

	//Generate a card using HandGenerator
	compCard1 = handGen.generate1Card();

	//Check this card is not already dealt
	if(checkCardNotDealt(compCard1) == false)
	{
		//Repeat last three steps
		// -- Add card to users hand
		computerHoleCard1 = new Card(compCard1);
		computerCards.addCard(computerHoleCard1);
		// -- Add cards to allCardsDealt array
		allCardsDealt[allCardsIndex] = compCard1;
		allCardsIndex++;

		//TEST
		//JOptionPane.showMessageDialog(null, "Comp Hole Card 2: "+compCard1, "initGame() Test", JOptionPane.INFORMATION_MESSAGE);
		//debug("Computer hole card 1: "+compCard1);
	}
	else
	{
		//Deal another card
		compCard1 = handGen.generate1Card();

		//True if card has not been dealt before
		boolean success = false;

		while(success == false)
		{

			//Deal another card
			compCard1 = handGen.generate1Card();
			//Check it hasn't been dealt
			if(checkCardNotDealt(compCard1) == false)
			{

				// -- If not add to hand and allCardsDealt array
				computerHoleCard1 = new Card(compCard1);
				computerCards.addCard(computerHoleCard1);
				// -- Add to allCardsDealt
				allCardsDealt[allCardsIndex] = compCard1;
				allCardsIndex++;
				// -- Success
				success = true;
			}

		}
		//TEST
		//JOptionPane.showMessageDialog(null, "Comp Hole Card 1: "+compCard1, "initGame() Test", JOptionPane.INFORMATION_MESSAGE);
		//debug("Computer hole card 1: "+compCard1);


	}


		//Deal a second hole card to the computer

		//Generate a card using HandGenerator
		compCard2 = handGen.generate1Card();

		//Check this card is not already dealt
		if(checkCardNotDealt(compCard2) == false)
		{
			//Repeat last three steps
			// -- Add card to users hand
			computerHoleCard2 = new Card(compCard2);
			computerCards.addCard(computerHoleCard2);
			// -- Add cards to allCardsDealt array
			allCardsDealt[allCardsIndex] = compCard2;
			allCardsIndex++;

			//TEST
			//JOptionPane.showMessageDialog(null, "Comp Hole Card 2: "+compCard2, "initGame() Test", JOptionPane.INFORMATION_MESSAGE);
			//debug("Computer hole card 2: "+compCard2);

		}
		else
		{
			//Deal another card
			compCard2 = handGen.generate1Card();

			//True if card has not been dealt before
			boolean success = false;

			while(success == false)
			{

				//Deal another card
				compCard2 = handGen.generate1Card();
				//Check it hasn't been dealt
				if(checkCardNotDealt(compCard2) == false)
				{
					// -- If not add to hand and allCardsDealt array
					computerHoleCard2 = new Card(compCard2);
					computerCards.addCard(computerHoleCard2);
					// -- Add to allCardsDealt
					allCardsDealt[allCardsIndex] = compCard2;
					allCardsIndex++;
					// -- Success
					success = true;
				}

			}
			//TEST
			//JOptionPane.showMessageDialog(null, "Comp Hole Card 2: "+compCard2, "initGame() Test", JOptionPane.INFORMATION_MESSAGE);
			//debug("Computer hole card 2: "+compCard2 + "\n\n");

		}


	//Initiate round of betting
	//playHand();
	//start thread here
	running = true;
	start();

	//Test -: these will be called from within playHand();
	//dealFlop();
	//dealTurn();
	//dealRiver();
}


/**
 * Play a hand against the computer
 * (assumes cards have been dealt)
 */
public void run()  //used to be playHand()
{
	//while() -- must be an iterative method!!!
	// -- possibly while the deal button is inactive

	if(running == true)
	{
		//debug("Thread is running");

		//Add each players ante (E50) to the pot
		//Get the game users chip amount
		userCash = Integer.parseInt(igui.yourCashTF.getText());

		//Check we have enough to continue
		if(userCash < 50)
		{
			JOptionPane.showMessageDialog(null, "Computer Wins this game\nYou do not have enough chips to play", "Message", JOptionPane.INFORMATION_MESSAGE);
			JOptionPane.showMessageDialog(null, "New Game Beginning", "Message", JOptionPane.INFORMATION_MESSAGE);
			userCash = 1000;
			computerCash = 1000;
			igui.yourCashTF.setText(String.valueOf(userCash));
			igui.compCashTF.setText(String.valueOf(computerCash));
		}

		userCash = Integer.parseInt(igui.yourCashTF.getText());
		//Take 50 from it
		userCash = userCash - 50;
		//Display game users new chip amount
		igui.yourCashTF.setText(String.valueOf(userCash));

		//Add the 50 to the current pot
		currentPot = Integer.parseInt(igui.potTF.getText());
		currentPot = currentPot + 50;
		//Display new pot amount
		igui.potTF.setText(String.valueOf(currentPot));


		//Get the computers chip amount
		computerCash = Integer.parseInt(igui.compCashTF.getText());
		//Check computer has enough to continue
		if(computerCash < 50)
		{
			JOptionPane.showMessageDialog(null, "You have won this game \nComputer is Bust", "Message", JOptionPane.INFORMATION_MESSAGE);
			JOptionPane.showMessageDialog(null, "New Game Beginning", "Message", JOptionPane.INFORMATION_MESSAGE);
			userCash = 1000;
			computerCash = 1000;
			igui.yourCashTF.setText(String.valueOf(userCash));
			igui.compCashTF.setText(String.valueOf(computerCash));
		}
		//else
		//{

		computerCash = Integer.parseInt(igui.compCashTF.getText());
		//Take 50 from it
		computerCash = computerCash - 50;
		//Display new chip amount
		igui.compCashTF.setText(String.valueOf(computerCash));

		//Add the computers 50 ante to the current pot
		currentPot = Integer.parseInt(igui.potTF.getText());
		currentPot = currentPot + 50;
		//Display new pot amount
		igui.potTF.setText(String.valueOf(currentPot));

		//Assume first round of betting is always into the game user
		//Inform user to make his decision after sleeping for a while
		//gui.msgIndex = 2;
		//gui.repaint();


		//Process whatever decision the user makes (check, bet, fold or exit)
		while(userAction != true)
		{try
		{ gameThread.sleep(3500);}
		catch(InterruptedException e)
		{ debug("Interrupt Exception");}
		//debug("Waiting for user action pre-flop");
		}
		//gui.msgIndex = 3;
		//gui.repaint();
		//Make the computers decision
		if(playerOverLimit == true)
		{
			//userAction = false;
			//userBet = false;
			//userCheck = false;

			//Give each player back there share of the pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			int halfPot = currentPot / 2;

			//Give half to each player
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			computerCash = Integer.parseInt(igui.compCashTF.getText());
			userCash = userCash + halfPot;
			computerCash = computerCash + halfPot;
			currentPot = 0;

			//Update chip count for both players
			igui.compCashTF.setText(String.valueOf(computerCash));
			igui.yourCashTF.setText(String.valueOf(userCash));
			igui.potTF.setText(String.valueOf(currentPot));
			playerOverLimit = false;

			//System.out.println("IN TRUE: "+playerOverLimit);
			controller.resetAfterComputerFold();
			resetGame();



		}
		else
		{
			computerMakeDecision();

		}




		//After flop has been dealt
		dealStage = 2;
		userAction = false;
		while(userAction != true)
		{try
		{ gameThread.sleep(3500);}
		catch(InterruptedException e)
		{ debug("Interrupt Exception");}
		//debug("Waiting for user action on flop");
		}
		//gui.msgIndex = 3;
		//gui.repaint();
		//Make the computers decision
		if(playerOverLimit == true)
		{
			//Give each player back there share of the pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			int halfPot = currentPot / 2;

			//Give half to each player
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			computerCash = Integer.parseInt(igui.compCashTF.getText());
			userCash = userCash + halfPot;
			computerCash = computerCash + halfPot;
			currentPot = 0;

			//Update chip count for both players
			igui.compCashTF.setText(String.valueOf(computerCash));
			igui.yourCashTF.setText(String.valueOf(userCash));
			igui.potTF.setText(String.valueOf(currentPot));
			playerOverLimit = false;

			//System.out.println("IN TRUE: "+playerOverLimit);
			controller.resetAfterComputerFold();
			resetGame();
		}
		else
		{
			computerMakeDecision();
		}
		//gameThread.dumpStack();


		//After the turn card has been dealt
		dealStage = 3;
		userAction = false;
		while(userAction != true)
		{try
		{ gameThread.sleep(3500);}
		catch(InterruptedException e)
		{ debug("Interrupt Exception");}
		//debug("Waiting for user action on the turn");
		}
		//gui.msgIndex = 3;
		//gui.repaint();
		//Make the computers decision
		if(playerOverLimit == true)
		{
			//Give each player back there share of the pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			int halfPot = currentPot / 2;

			//Give half to each player
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			computerCash = Integer.parseInt(igui.compCashTF.getText());
			userCash = userCash + halfPot;
			computerCash = computerCash + halfPot;
			currentPot = 0;

			//Update chip count for both players
			igui.compCashTF.setText(String.valueOf(computerCash));
			igui.yourCashTF.setText(String.valueOf(userCash));
			igui.potTF.setText(String.valueOf(currentPot));
			playerOverLimit = false;
			//System.out.println("IN TRUE: "+playerOverLimit);
			controller.resetAfterComputerFold();
			resetGame();
		}
		else
		{
			computerMakeDecision();
		}



		//After the river card has been dealt
		dealStage = 4;
		userAction = false;
		while(userAction != true)
		{try
		{ gameThread.sleep(3500);}
		catch(InterruptedException e)
		{ debug("Interrupt Exception");}
		//debug("Waiting for user action on the river");
		}
		//gui.msgIndex = 3;
		//gui.repaint();
		//Make the computers decision
		if(playerOverLimit == true)
		{
			//Give each player back there share of the pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			int halfPot = currentPot / 2;

			//Give half to each player
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			computerCash = Integer.parseInt(igui.compCashTF.getText());
			userCash = userCash + halfPot;
			computerCash = computerCash + halfPot;
			currentPot = 0;

			//Update chip count for both players
			igui.compCashTF.setText(String.valueOf(computerCash));
			igui.yourCashTF.setText(String.valueOf(userCash));
			igui.potTF.setText(String.valueOf(currentPot));
			playerOverLimit = false;
			//System.out.println("IN TRUE: "+playerOverLimit);
			controller.resetAfterComputerFold();
			resetGame();
		}
		else
		{
			computerMakeDecision();
		}


		//If gone this far, error has occured, computer should fold
		JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

		//Add chips in the current pot to the game users pot
		currentPot = Integer.parseInt(igui.potTF.getText());
		userCash = Integer.parseInt(igui.yourCashTF.getText());
		userCash = userCash + currentPot;
		currentPot = 0;
		igui.potTF.setText(String.valueOf(currentPot));
		igui.yourCashTF.setText(String.valueOf(userCash));

		//Set computerFold variable
		//debug("FOLDING SECOND");
		//computerFold = true;

		controller.resetAfterComputerFold();
		//Reset the game
		resetGame();
		debug("GAME OVER");


	}

}


/**
 * Start method for thread
 */
private void start()
{
	gameThread = new Thread(this);

	if(running == true)
	{
		gameThread.start();
	}
}


/**
 * Processes the somputers decision
 */
private void computerMakeDecision()
{
	//Stage 1 ------------------------------------------------------------- Deciding to play or not
	if(dealStage == 1)
	{
		//debug("Deal Stage is 1 ~ Pre Flop");
		//See if computer has a premium starting hand
		//Strip the suits from the hand
		CharacterIterator card1 = new StringCharacterIterator(compCard1);
		CharacterIterator card2 = new StringCharacterIterator(compCard2);
		//debug(compCard1);
		//debug(compCard2);
		String computerHand = "";
		//Add the values to the new hand without the suits
		computerHand = computerHand + card1.first();
		computerHand = computerHand + card2.first();
		//debug(computerHand);

		//Check if computer has good starting hand
		boolean match = false;
		int counter = 0;
		while(counter < premiumHands.length)
		{
			if(premiumHands[counter].equals(computerHand))
			{
				//debug("Premium Hand found");
				match = true;
				break;
			}
			else
			{
				//debug("No premium hand found");
				counter++;
			}

		}

		//If premium hand has not been found, computer will fold, unless user has checked
		if(match == false && userCheck == false)
		{
			JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

			//Add chips in the current pot to the game users pot
			currentPot = Integer.parseInt(igui.potTF.getText());
		    userCash = Integer.parseInt(igui.yourCashTF.getText());
		    userCash = userCash + currentPot;
			currentPot = 0;
			igui.potTF.setText(String.valueOf(currentPot));
			igui.yourCashTF.setText(String.valueOf(userCash));


			//Set computerFold variable
			//computerFold = true;
			//debug("FOLDING FIRST");
			controller.resetAfterComputerFold();
			//Reset the game
			resetGame();
		}
		else
		if(userCheck == true)
		{
			//User has checked, computer will check also
			computerCheck = true;
			JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

			//Both players checked - Deal the flop
			dealFlop();
			userAction = false;
			userBet = false;
			userCheck = false;
		}


		//If user has bet and computer has a good starting hand, computer will call
		if(userBet == true && match == true)
		{
			//Call the game users bet
			JOptionPane.showMessageDialog(null, "Computer will Call", "Message", JOptionPane.INFORMATION_MESSAGE);
			//debug("USER HAS BET");
			//Get the current bet amount
			amountBet = Integer.parseInt(igui.youBetTF.getText());
			//Take it from computers chips
			computerCash = Integer.parseInt(igui.compCashTF.getText());
			computerCash = computerCash - amountBet;
			igui.compCashTF.setText(String.valueOf(computerCash));
			//Add it to current pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			currentPot = currentPot + amountBet;
			igui.potTF.setText(String.valueOf(currentPot));
			//Deal the flop
			dealFlop();
			userAction = false;
			userBet = false;
			userCheck = false;

		}
		else
		if(userBet == true && match == false)
		{
			JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

			//Add chips in the current pot to the game users pot
			currentPot = Integer.parseInt(igui.potTF.getText());
		    userCash = Integer.parseInt(igui.yourCashTF.getText());
			userCash = userCash + currentPot;
			currentPot = 0;
			igui.potTF.setText(String.valueOf(currentPot));
			igui.yourCashTF.setText(String.valueOf(userCash));

			//Set computerFold variable
			//debug("FOLDING SECOND");
			//computerFold = true;

			controller.resetAfterComputerFold();
			//Reset the game
			resetGame();
		}

		//If the user is all In, the computer will fold
		if(userAllIn == true)
		{
			JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

			//Add chips in the current pot to the game users pot
			currentPot = Integer.parseInt(igui.potTF.getText());
		    userCash = Integer.parseInt(igui.yourCashTF.getText());
			userCash = userCash + currentPot;
			currentPot = 0;
			igui.potTF.setText(String.valueOf(currentPot));
			igui.yourCashTF.setText(String.valueOf(userCash));

			//Set computerFold variable
			//debug("FOLDING SECOND");
			//computerFold = true;

			controller.resetAfterComputerFold();
			//Reset the game
			resetGame();
		}
	}
	else

	//Stage 2 ------------------------------------------------------- Making a Decision on the flop
	if(dealStage == 2)
	{
		//debug("Deal Stage is 2 ~ After Flop");
		//Put computer hand through hand evaluator to see if anything has been made
		String postFlopHand = handEval.nameHand(computerCards);
		//debug(postFlopHand);

		//Determine what hand has been made, if any
		CharacterIterator compHand = new StringCharacterIterator(postFlopHand);
		String hand = "";
		hand = hand + compHand.first();
		hand = hand + compHand.next();
		hand = hand + compHand.next();
		hand = hand + compHand.next();
		//debug(hand);

		//Determine what to do in each case of having a particular hand
		//High Card
		if(hand.equals("High"))
		{
			//debug("Computer has High Card");
			//Computer has just a high card


			//If user bets higher than 100 fold, otherwise call to see the next card
			if(userBet == true)
			{
				//Call bet if <= 100
				if(amountBet <= 100)
				{
					//Take amount from computers chips
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash - amountBet;
					igui.compCashTF.setText(String.valueOf(computerCash));

					//Add it to current pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					currentPot = currentPot + amountBet;
					igui.potTF.setText(String.valueOf(currentPot));

					JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

					//Deal the turn card
					dealTurn();
					userAction = false;
					userBet = false;
					userCheck = false;
				}
				//Otherwise fold
				else
				{
					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
				    userCash = userCash + currentPot;
				    currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}
			}


			//If user checks, check also as computer has no made hand
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, fold your hand
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));


				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}



		}
		//A Pair
		if(hand.equals("Pair"))
		{
			//debug("Computer has a pair");
			//Computer has some pair made

			//returnVars();

			//If user bets, call that bet and deal the turn card
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}

			//If user checks, bet 150
			if(userCheck == true)
			{
				//Set amount bet
				amountBet = 150;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 150";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 150", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
			    //debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the turn card
					 dealTurn();
					 userAction = false;
					 userBet = false;
	     			 userCheck = false;
	     			 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}


			}


			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}


		}
		//Two Pair
		if(hand.equals("Two "))
		{
			//Two Pair
			//debug("Computer has two pair");

			//If game user bets, call that bet & deal the turn card
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If the user checks, check also, and deal the turn card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK ON FLOP");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If the user is allIn, fold.  //SHOULD PERHAPS BE A CALL, IF TIME ALLOWS
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}

		}
		//Trips
		if(hand.equals("Thre"))
		{
			//debug("Computer has trips");

			//If user bets, call & raise 250
			if(userBet == true)
			{
				//Take the amount the user bet from the computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				//Raise 250
				//Take 250 from computers chips
				amountBet = 250;
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				//Add it to current bet textfield
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				JOptionPane.showMessageDialog(null, "Computer bets 250", "Message", JOptionPane.INFORMATION_MESSAGE);
				//gui.screenMessage[9] = "Computer Bets 250";
				//gui.msgIndex = 9;
				//gui.repaint();

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with trips");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the turn card
					 dealTurn();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}
				//Process user decision
			}


			//If user checks, check also
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK ON FLOP");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, call & determine winner
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}

				//}
				//else




			}

		}
		//Straight
		if(hand.equals("Stra"))
		{
			//debug("Computer has a straight");

			//If game user bets, call and deal the turn
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user checks, check also and deal turn card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK ON FLOP");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all In, call
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}

			}

		}
		//Flush
		if(hand.equals("a Flu"))
		{
			//debug("Computer has a flush");

			//If user bets, call & Raise 300
			if(userBet == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 300";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the turn card
					 dealTurn();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}


			//}
			}


			//If user checks, bet 250
			if(userCheck == true)
			{
				//Set amount bet
				amountBet = 250;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 250";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 250", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the turn card
					 dealTurn();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}

			}


			//If user is all In, call
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}

			}

		}
		//Straight Flush
		if(hand.equals("a "))
		{
			//debug("Computer has a straight flush");

			//If user bets, call
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user checks, check also & deal next card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all In, Call
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
			}


		}
		//Full House
		if(hand.equals("Full"))
		{
			//debug("Computer has a full house");

			//If user bets, call & raise all in, determine winner
			if(userBet == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 300";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the turn card
					 dealTurn();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}

			}


			//If user checks, move all in, determine winner
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, call, and determine winner
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}

			}


		}
		//Quads
		if(hand.equals("Four"))
		{
			//debug("Computer has a poker");

			//If user bets, call and raise 300
			if(userBet == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 300";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the turn card
					 dealTurn();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}
			}


			//If user checks, check also and deal next card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the turn card
				dealTurn();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, call & determine winner
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
			}

		}
		//Royal Flush
		if(hand.equals(""))
		{
			debug("Computer has a royal flush");

			//If user bets, call and move all in, determine winner
			if(userBet == true)
			{

			}


			//If user checks, check also
			if(userCheck == true)
			{

			}


			//If user is all in, call, and determine winner
			if(userAllIn == true)
			{

			}
		}

	}
	else
	//Stage 3 -------------------------------------------------- Making a decision on the turn card
	if(dealStage == 3)
	{
		//debug("Deal Stage is 3 ~ After turn card");

		//Put computer hand through hand evaluator to see if anything has been made
		String atTurnHand = handEval.nameHand(computerCards);
		//debug("Computer has " +atTurnHand);

		//Determine what hand has been made, if any
		CharacterIterator compHand = new StringCharacterIterator(atTurnHand);
		String hand = "";
		hand = hand + compHand.first();
		hand = hand + compHand.next();
		hand = hand + compHand.next();
		hand = hand + compHand.next();
		//debug(hand);

		//Determine what to do in each case of having a particular hand
		//High Card
		if(hand.equals("High"))
		{
			//debug("Computer has High Card on Turn");

			//If user bets higher than 100 fold, otherwise call to see the next card
			if(userBet == true)
			{
				//Call bet if <= 100
				if(amountBet <= 100)
				{
					//Take amount from computers chips
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash - amountBet;
					igui.compCashTF.setText(String.valueOf(computerCash));

					//Add it to current pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					currentPot = currentPot + amountBet;
					igui.potTF.setText(String.valueOf(currentPot));

					JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

					//Deal the river card
					dealRiver();
					userAction = false;
					userBet = false;
					userCheck = false;
				}
				//Otherwise fold
				else
				{
					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}
			}


			//If user checks, check also as computer has no made hand
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, fold your hand
			if(userAllIn == true)
			{
				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}

		}
		else
		//Computer has a pair
		if(hand.equals("Pair"))
		{
			//debug("Computer has a Pair on Turn");

			//If user bets, call that bet and deal the turn card
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}

			//If user checks, bet 150
			if(userCheck == true)
			{
				//Set amount bet
				amountBet = 150;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 150";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 150", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the river card
					 dealRiver();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}


			}


			if(userAllIn == true)
			{
				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}


		}
		else
		//Computer has a two pair
		if(hand.equals("Two "))
		{
			//debug("Computer has two pair on Turn");

			//If game user bets, call that bet & deal the turn card
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If the user checks, check also, and deal the turn card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK ON FLOP");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If the user is allIn, fold.  //SHOULD PERHAPS BE A CALL, IF TIME ALLOWS
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}

		}
		//Trips
		if(hand.equals("Thre"))
		{
			//debug("Computer has trips on Turn");

			//If user bets, call & raise 250
			if(userBet == true)
			{
				//Take the amount the user bet from the computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				//Raise 250
				//Take 250 from computers chips
				amountBet = 250;
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				//Add it to current bet textfield
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 250";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 250", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with trips");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the river card
					 dealRiver();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}
				//Process user decision
			}


			//If user checks, check also
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK ON FLOP");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, call & determine winner
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next card
				//dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
			}

		}
		else
		//Computer has a straight
		if(hand.equals("Stra"))
		{
			//debug("Computer has a straight on Turn");

			//If game user bets, call and deal the turn
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user checks, check also and deal turn card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK ON FLOP");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all In, call
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				//dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}

			}
		}
		else
		//Computer has a flush
		if(hand.equals("a Flu"))
		{
			//debug("Computer has a flush on the Turn");

			//If user bets, call & Raise 300
			if(userBet == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 300";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the river card
					 dealRiver();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}


			//}
			}


			//If user checks, bet 250
			if(userCheck == true)
			{
				//Set amount bet
				amountBet = 250;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 250";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 250", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the river card
					 dealRiver();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				    resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}

			}


			//If user is all In, call
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				//dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}

			}

		}
		else
		//Computer has a straight flush
		if(hand.equals("a *"))
		{
			//debug("Computer has a straight flush on the Turn");

			//If user bets, call
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user checks, check also & deal next card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all In, Call
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				//dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
			}


		}
		else
		//Computer has full house
		if(hand.equals("Full"))
		{
			//debug("Computer has a full house on the Turn");

			//If user bets, call & raise all in, determine winner
			if(userBet == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 300";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the river card
					 dealRiver();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}

			}


			//If user checks, move all in, determine winner
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, call, and determine winner
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				//dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}

			}

		}
		else
		//Computer has a poker
		if(hand.equals("Four"))
		{
			//debug("Computer has a poker");

			//If user bets, call and raise 300
			if(userBet == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 300";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the flop with two-pair");
				}
				 // -- If User calls, deal the turn card
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Deal the river card
					 dealRiver();
					 userAction = false;
					 userBet = false;
					 userCheck = false;
					 userCall = false;

				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}
			}


			//If user checks, check also and deal next card
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - Deal the river card
				dealRiver();
				userAction = false;
				userBet = false;
				userCheck = false;
			}


			//If user is all in, call & determine winner
			if(userAllIn == true)
			{
				computerCallAllIn = true;

				//Screen msg
				JOptionPane.showMessageDialog(null, "Computer calls All In", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Get amount of user & computers chips
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());

				//If the user has more than the computer, the computer is all in also
				//if(userCash > computerCash)
				//{

				//Add both players chips to the current pot
				//Current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + userCash;
				currentPot = currentPot + computerCash;

				//Turn over the next two cards
				//dealTurn();
				dealRiver();

				//Determine winner
				int winner = determineWinner();

				if(winner == 1)
				{
					//Game user wins
					//Add chips in pot to game users chips
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + currentPot;

					if(computerCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "You have won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}


					//Display new chip amounts
					currentPot = 0;
					computerCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));


				}
				else
				if(winner == 2)
				{
					//Computer Wins
					//Add chips in pot to computers cash
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = computerCash + currentPot;

					if(userCash <= 0)
					{
						JOptionPane.showMessageDialog(null, "Computer has won the game", "Message", JOptionPane.INFORMATION_MESSAGE);
					}

					//Display new chip amounts
					currentPot = 0;
					userCash = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
				else
				{
					//Split pot
					//Get user, computer and current pot chip amounts
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());

					//Add half to each players chip amount
					int halfPot = currentPot / 2;

					//Add to users & computers pot amount
					computerCash = computerCash + halfPot;
					userCash = userCash + halfPot;

					//Screen msg
					JOptionPane.showMessageDialog(null, "There is a Split Pot", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Display new pot amount
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));

				}
			}
		}
		else
		//Computer has a royal flush
		if(hand.equals(""))
		{
			//Will be taken into account if time permits
		}



	}
	else
	//Stage 4 ------------------------------------------------- Making a decision on the River card
	if(dealStage == 4)
	{
		//debug("Deal Stage is 4 ~ After the River");

		//Put computer hand through hand evaluator to see if anything has been made
		String atRiverHand = handEval.nameHand(computerCards);
		//debug("Computer has " +atRiverHand);

		//Determine what hand has been made, if any
		CharacterIterator compHand = new StringCharacterIterator(atRiverHand);
		String hand = "";
		hand = hand + compHand.first();
		hand = hand + compHand.next();
		hand = hand + compHand.next();
		hand = hand + compHand.next();
		//debug(hand);

		//Determine what to do in each case of having a particular hand
		//High Card
		if(hand.equals("High"))
		{
			//debug("Computer has High Card on River");

			//If computer bets more than 100, fold, otherwise call
			if(userBet == true)
			{
				//Call bet if <= 100
				if(amountBet <= 100)
				{
					//Take amount from computers chips
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash - amountBet;
					igui.compCashTF.setText(String.valueOf(computerCash));

					//Add it to current pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					currentPot = currentPot + amountBet;
					igui.potTF.setText(String.valueOf(currentPot));

					JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);



					//Turn over computers cards
					gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
					gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
					gui.repaint();
					//Determine the winner
					int winner = determineWinner();
					//See who gets pot
					if(winner == 1)
					{
						//Add pot to users chips
						currentPot = Integer.parseInt(igui.potTF.getText());
						userCash = Integer.parseInt(igui.yourCashTF.getText());
						userCash = userCash + currentPot;
						currentPot = 0;
						igui.yourCashTF.setText(String.valueOf(userCash));
						igui.potTF.setText(String.valueOf(currentPot));
					}
					else
					if(winner == 2)
					{
						//Add chips to computers pot
						currentPot = Integer.parseInt(igui.potTF.getText());
						computerCash = Integer.parseInt(igui.compCashTF.getText());
						computerCash = computerCash + currentPot;
						currentPot = 0;
						igui.compCashTF.setText(String.valueOf(computerCash));
						igui.potTF.setText(String.valueOf(currentPot));
					}
					else
					{
						//Split pot evenly
						currentPot = Integer.parseInt(igui.potTF.getText());
						int halfPot = currentPot / 2;

						//Give half to each player
						userCash = Integer.parseInt(igui.yourCashTF.getText());
						computerCash = Integer.parseInt(igui.compCashTF.getText());
						userCash = userCash + halfPot;
						computerCash = computerCash + halfPot;
						currentPot = 0;

						//Update chip count for both players
						igui.compCashTF.setText(String.valueOf(computerCash));
						igui.yourCashTF.setText(String.valueOf(userCash));
						igui.potTF.setText(String.valueOf(currentPot));

					}

					userAction = false;
					userBet = false;
					userCheck = false;
					controller.resetAfterComputerFold();
					resetGame();



				}
			}
			//Otherwise fold
			else
			{
				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}


			//If player is all in, fold
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}


		}
		else
		//Computer has a pair
		if(hand.equals("Pair"))
		{
			//debug("Computer has a pair on River");

			//If player bets more than 300 fold, otherwise call
			if(userBet == true)
			{
				//Call bet if <= 300
				if(amountBet <= 300)
				{
					//Take amount from computers chips
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash - amountBet;
					igui.compCashTF.setText(String.valueOf(computerCash));

					//Add it to current pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					currentPot = currentPot + amountBet;
					igui.potTF.setText(String.valueOf(currentPot));

					JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);



					//Turn over computers cards
					gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
					gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
					gui.repaint();
					//Determine the winner
					int winner = determineWinner();
					//See who gets pot
					if(winner == 1)
					{
						//Add pot to users chips
						currentPot = Integer.parseInt(igui.potTF.getText());
						userCash = Integer.parseInt(igui.yourCashTF.getText());
						userCash = userCash + currentPot;
						currentPot = 0;
						igui.yourCashTF.setText(String.valueOf(userCash));
						igui.potTF.setText(String.valueOf(currentPot));
					}
					else
					if(winner == 2)
					{
						//Add chips to computers pot
						currentPot = Integer.parseInt(igui.potTF.getText());
						computerCash = Integer.parseInt(igui.compCashTF.getText());
						computerCash = computerCash + currentPot;
						currentPot = 0;
						igui.compCashTF.setText(String.valueOf(computerCash));
						igui.potTF.setText(String.valueOf(currentPot));
					}
					else
					{
						//Split pot evenly
						currentPot = Integer.parseInt(igui.potTF.getText());
						int halfPot = currentPot / 2;

						//Give half to each player
						userCash = Integer.parseInt(igui.yourCashTF.getText());
						computerCash = Integer.parseInt(igui.compCashTF.getText());
						userCash = userCash + halfPot;
						computerCash = computerCash + halfPot;
						currentPot = 0;

						//Update chip count for both players
						igui.compCashTF.setText(String.valueOf(computerCash));
						igui.yourCashTF.setText(String.valueOf(userCash));
						igui.potTF.setText(String.valueOf(currentPot));

					}

					userAction = false;
					userBet = false;
					userCheck = false;
					controller.resetAfterComputerFold();
					resetGame();

				}
			}

			//If user checks, and computer has a low pair, check also
			//as he may be trying to check raise
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - determine the winner
				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();

			}


			//If game user, goes all in, fold
			if(userAllIn == true)
			{
				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}



		}
		else
		//Computer has a two pair
		if(hand.equals("Two "))
		{
			//debug("Computer has a two pair on the River");

			//If user bets, on the river, and computer has a two pair, call
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);



				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If the user checks on the river, check also, as it may be a trap
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - determine the winner
				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If user is all In, fold
			if(userAllIn == true)
			{
				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
				resetGame();
			}



		}
		else
		//Computer has Trips
		if(hand.equals("Thre"))
		{
			//debug("Computer has trips on the River");

			//If user bets on river, and computer has trips, call bet
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If game user checks, bet 300
			if(userCheck == true)
			{
				//Set amount bet
				amountBet = 300;
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));
				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));
				//Add it to current bet text field
				igui.youBetTF.setText(String.valueOf(amountBet));

				//Inform User
				//gui.screenMessage[9] = "Computer Bets 150";
				//gui.msgIndex = 9;
				//gui.repaint();
				JOptionPane.showMessageDialog(null, "Computer bets 300", "Message", JOptionPane.		INFORMATION_MESSAGE);

				//Display apt buttons
				controller.controlP.bet.setEnabled(false);
				controller.controlP.check.setEnabled(false);
				controller.controlP.call.setEnabled(true);
				controller.controlP.raise.setEnabled(true);

				//Wait for user response
				userAction = false;
				while(userAction != true)
				{try
				{ gameThread.sleep(5500);}
				catch(InterruptedException e)
				{ debug("Interrupt Exception");}
				//debug("Waiting for user action - Computer has bet on the river with trips");
				}
				 // -- If User calls, determine winner
				 if(userCall == true)
				 {
					 //debug("Game user calls computers bet");
					 //Take amount from users chips
					 userCash = Integer.parseInt(igui.yourCashTF.getText());
					 userCash = userCash - amountBet;
					 igui.yourCashTF.setText(String.valueOf(userCash));
					 //Add it to pot
					 currentPot = Integer.parseInt(igui.potTF.getText());
					 currentPot = currentPot + amountBet;
					 igui.potTF.setText(String.valueOf(currentPot));
					 //Reset buttons
					 controller.controlP.bet.setEnabled(true);
					 controller.controlP.check.setEnabled(true);
					 controller.controlP.call.setEnabled(false);
					 controller.controlP.raise.setEnabled(false);
					 //Determine winner

					//Turn over computers cards
					gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
					gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
					gui.repaint();
					//Determine the winner
					int winner = determineWinner();
					//See who gets pot
					if(winner == 1)
					{
						//Add pot to users chips
						currentPot = Integer.parseInt(igui.potTF.getText());
						userCash = Integer.parseInt(igui.yourCashTF.getText());
						userCash = userCash + currentPot;
						currentPot = 0;
						igui.yourCashTF.setText(String.valueOf(userCash));
						igui.potTF.setText(String.valueOf(currentPot));
					}
					else
					if(winner == 2)
					{
						//Add chips to computers pot
						currentPot = Integer.parseInt(igui.potTF.getText());
						computerCash = Integer.parseInt(igui.compCashTF.getText());
						computerCash = computerCash + currentPot;
						currentPot = 0;
						igui.compCashTF.setText(String.valueOf(computerCash));
						igui.potTF.setText(String.valueOf(currentPot));
					}
					else
					{
						//Split pot evenly
						currentPot = Integer.parseInt(igui.potTF.getText());
						int halfPot = currentPot / 2;

						//Give half to each player
						userCash = Integer.parseInt(igui.yourCashTF.getText());
						computerCash = Integer.parseInt(igui.compCashTF.getText());
						userCash = userCash + halfPot;
						computerCash = computerCash + halfPot;
						currentPot = 0;

						//Update chip count for both players
						igui.compCashTF.setText(String.valueOf(computerCash));
						igui.yourCashTF.setText(String.valueOf(userCash));
						igui.potTF.setText(String.valueOf(currentPot));

					}

					userAction = false;
					userBet = false;
					userCheck = false;
					controller.resetAfterComputerFold();
				    resetGame();
				 }
				 else
				 // -- If User folds, take the pot
				 // -- If user raises, fold
				 if(userRaise = true)
				 {
					 //Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
				   resetGame();
				 }
				 else
				 // -- If user moves all In, fold
				 if(userAllIn == true)
				 {
					 JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

					//Add chips in the current pot to the game users pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.potTF.setText(String.valueOf(currentPot));
					igui.yourCashTF.setText(String.valueOf(userCash));

					//Set computerFold variable
					//debug("FOLDING SECOND");
					//computerFold = true;

					controller.resetAfterComputerFold();
					//Reset the game
					resetGame();
				}


			}



			//If user is all In, fold
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer will Fold", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Add chips in the current pot to the game users pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				//Set computerFold variable
				//debug("FOLDING SECOND");
				//computerFold = true;

				controller.resetAfterComputerFold();
				//Reset the game
			    resetGame();
			}

		}
		else
		//Computer has a straight
		if(hand.equals("Stra"))
		{
			//debug("Computer has a straight on the River");

			//If user bets and computer has a made straight, call the bet
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If game user checks, check also
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - determine the winner
				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}



			//If user is all in, call
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}




		}
		else
		//Computer has a flush
		if(hand.equals("a Flu"))
		{
			//debug("Computer has a flush on the River");

			//If user bets, call
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If game user checks, check also
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - determine the winner
				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}



			//If user is all in, call
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}




		}
		else
		//Computer has a straight flush
		if(hand.equals("a "))
		{
			debug("Computer has a straight flush on the River");

		}
		else
		//Computer has a full house
		if(hand.equals("Full"))
		{
			//debug("Computer has a full house on the River");
			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If game user checks, check also
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - determine the winner
				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}



			//If user is all in, call
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}

		}
		else
		//Computer has quads
		if(hand.equals("Four"))
		{
			//debug("Computer has a poker on the River");

			if(userBet == true)
			{
				//Take amount from computers chips
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash - amountBet;
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Add it to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + amountBet;
				igui.potTF.setText(String.valueOf(currentPot));

				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}


			//If game user checks, check also
			if(userCheck == true)
			{
				//debug("BOTH PLAYERS CHECK");
				//User has checked, computer will check also
				computerCheck = true;
				JOptionPane.showMessageDialog(null, "Computer will Check", "Message", JOptionPane.INFORMATION_MESSAGE);

				//Both players checked - determine the winner
				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}



			//If user is all in, call
			if(userAllIn == true)
			{
				JOptionPane.showMessageDialog(null, "Computer Calls", "Message", JOptionPane.	INFORMATION_MESSAGE);

				//Turn over computers cards
				gui.computerHand[0] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[2]));
				gui.computerHand[1] = GUICreationHelper.loadImage(parseCardImage(allCardsDealt[3]));
				gui.repaint();
				//Determine the winner
				int winner = determineWinner();
				//See who gets pot
				if(winner == 1)
				{
					//Add pot to users chips
					currentPot = Integer.parseInt(igui.potTF.getText());
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					userCash = userCash + currentPot;
					currentPot = 0;
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				if(winner == 2)
				{
					//Add chips to computers pot
					currentPot = Integer.parseInt(igui.potTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					computerCash = computerCash + currentPot;
					currentPot = 0;
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.potTF.setText(String.valueOf(currentPot));
				}
				else
				{
					//Split pot evenly
					currentPot = Integer.parseInt(igui.potTF.getText());
					int halfPot = currentPot / 2;

					//Give half to each player
					userCash = Integer.parseInt(igui.yourCashTF.getText());
					computerCash = Integer.parseInt(igui.compCashTF.getText());
					userCash = userCash + halfPot;
					computerCash = computerCash + halfPot;
					currentPot = 0;

					//Update chip count for both players
					igui.compCashTF.setText(String.valueOf(computerCash));
					igui.yourCashTF.setText(String.valueOf(userCash));
					igui.potTF.setText(String.valueOf(currentPot));

				}

				userAction = false;
				userBet = false;
				userCheck = false;
				controller.resetAfterComputerFold();
				resetGame();
			}

		}
		//Computer has a royal flush
		if(hand.equals(""))
		{
			debug("Computer has a royal flush on the River");

		}



	}



}

//---------GET METHODS---------------//

/**
 * Return the values of certain game variables
 * Used for debugging
 */
private void returnVars()
{
	//User bet
	if(userBet == true)
	{
		JOptionPane.showMessageDialog(null, "userBet is true", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}
	else
	{
		JOptionPane.showMessageDialog(null, "userBet is false", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}

	//User check
	if(userCheck == true)
	{
		JOptionPane.showMessageDialog(null, "userCheck is true", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}
	else
	{
		JOptionPane.showMessageDialog(null, "userCheck is false", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}

	//User all in
	if(userAllIn == true)
	{
		JOptionPane.showMessageDialog(null, "userAllIn is true", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}
	else
	{
		JOptionPane.showMessageDialog(null, "userAAllIn is false", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}

	//User calls
	if(userCall == true)
	{
		JOptionPane.showMessageDialog(null, "userCall is true", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
	}
	else
	{
		JOptionPane.showMessageDialog(null, "userCall is false", "Game Variables Values", JOptionPane.WARNING_MESSAGE);
}

}

//-------INSTANCE METHODS------------//


public void setInterface(InformationInterface i)
{
	igui = i;
}

//------GAME USER FUNCTIONS----------//

/**
 * Call the computer players current bet
 */
public void playerCall()
{
	//Check user has enough chips to call
	if(userCash < amountBet || userCash == 0)
	{
		JOptionPane.showMessageDialog(null, "You do not have enough chips \nto call the current bet", "Not Enough Chips", JOptionPane.WARNING_MESSAGE);
	}
	else
	{
		//Get the amount of the current bet
		amountBet = Integer.parseInt(igui.youBetTF.getText());

		//Minus this amount from users chips
		userCash = Integer.parseInt(igui.yourCashTF.getText());
		userCash = userCash - amountBet;
		igui.yourCashTF.setText(String.valueOf(userCash));

		//Add this amount to current pot
		currentPot = Integer.parseInt(igui.potTF.getText());
		currentPot = currentPot + amountBet;
		igui.potTF.setText(String.valueOf(currentPot));
	}
}


/**
 * Bet a specified amount of chips
 */
public void playerBet()
{
	if(userCash == 0 || amountBet > userCash)
	{
		JOptionPane.showMessageDialog(null, "You do not have enough chips to bet", "Not Enough Chips", JOptionPane.WARNING_MESSAGE);
		playerOverLimit = true;

	}
	else
	{
		//Get the amount the user wishes to bet
		String amount = JOptionPane.showInputDialog(null, "How much do you wish to bet?", "Bet", 	JOptionPane.INFORMATION_MESSAGE);
		amountBet = Integer.parseInt(amount);
		if(amountBet > userCash)
		{
			JOptionPane.showMessageDialog(null, "Cannot make that bet \nPlease choose a smaller amount", "Not Enough Chips", JOptionPane.WARNING_MESSAGE);
			playerOverLimit = true;
		}
		else
		{
			//amountBet = Integer.parseInt(amount);

			//Add the bet to the current pot amount
			currentPot = currentPot + amountBet;

			//Display new pot amount
			igui.potTF.setText(String.valueOf(currentPot));

			//Add this amount to the current bet textfield
			igui.youBetTF.setText(String.valueOf(amountBet));

			//Minus this amount from the users chips
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			userCash = userCash - amountBet;
			igui.yourCashTF.setText(String.valueOf(userCash));
		}
	}

}


/**
 * Call the current bet and raise
 * another amount
 */
public void playerRaise()
{
	//Take the amount currently bet from game users current chips
	userCash = Integer.parseInt(igui.yourCashTF.getText());

	if(amountBet > userCash)
	{
		JOptionPane.showMessageDialog(null, "You do not have enough chips \nto call the current bet", "Cannot Raise", JOptionPane.WARNING_MESSAGE);
		playerOverLimit = true;
	}
	else
	{
		userCash = userCash - amountBet;
		igui.yourCashTF.setText(String.valueOf(userCash));

		//Add it to current pot
		currentPot = Integer.parseInt(igui.potTF.getText());
		currentPot = currentPot + amountBet;
		igui.potTF.setText(String.valueOf(currentPot));

		//Display raise dialog
		String amount = JOptionPane.showInputDialog(null, "Current bet has been matched from your chips. \n How much do you wish to raise?", "Raise Opponent", JOptionPane.INFORMATION_MESSAGE);

		amountRaised = Integer.parseInt(amount);
		if(amountRaised > userCash)
		{
			JOptionPane.showMessageDialog(null, "You do not have enough chips \nto raise this amount ", "Cannot Raise", JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			//Take this amount from current user chips
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			userCash = userCash - amountRaised;
			igui.yourCashTF.setText(String.valueOf(userCash));

			//Put this amount it the current bet textfield
			igui.youBetTF.setText(String.valueOf(amountRaised));

			//Add it to current pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			currentPot = currentPot + amountRaised;
			igui.potTF.setText(String.valueOf(currentPot));

			//Return focus to computer player
		}

	}


}


/**
 * Check, i.e. don't bet
 * or raise
 */
public void playerCheck()
{
	//Change boolean variable to true
	//userCheck = true;

	//If computer player has previously checked (it's boolean will be true)
	if(computerCheck == true)
	{
		if(dealStage == 1)
		{
			//Checking Pre-Flop
			//dealFlop();
		}
		else
		if(dealStage == 2)
		{
			//Checking On the flop
			//dealTurn();
		}
		else
		if(dealStage == 3)
		{
 			//Checking On the turn
 			//dealRiver();
		}
		else
		if(dealStage == 4)
		{
			//Checking on the River
			/*int winner = determineWinner();

			// --- Add chips in current pot to who ever has won the hand
			if(winner == 1)
			{
				//Game user wins
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

			}
			else
			if(winner == 2)
			{
				//Computer wins
				currentPot = Integer.parseInt(igui.potTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash + currentPot;
				currentPot = 0;
				igui.potTF.setText(String.valueOf(currentPot));
				igui.compCashTF.setText(String.valueOf(computerCash));

			}
			else
			{
				//Split pot
				//Get the amount of chips in the pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				int amount = currentPot / 2;
				//Give half to each player
				//Give to user
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + amount;
				igui.yourCashTF.setText(String.valueOf(userCash));
				//Give to computer
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash + amount;
				igui.compCashTF.setText(String.valueOf(computerCash));
			}*/


		}
	}
	else
	{
		// -- return focus to computer player
		// -- wait for response
	}
}


/**
 * Call the current bet and
 * raise all remaining chips
 */
public void playerAllIn()
{
	//Display confirmation
	int answer = JOptionPane.showConfirmDialog(null, "Are you Sure you wish to move All In?", "Move All In", JOptionPane.YES_NO_OPTION);
	if(answer == 0)
	{
		//Get amount of players remaining chips
		allInAmount = Integer.parseInt(igui.yourCashTF.getText());
		//If computer user decides to call
		if(computerCallAllIn == true)
		{
			// -- Add remaining chip amount to current pot
			currentPot = Integer.parseInt(igui.potTF.getText());
			currentPot = currentPot + allInAmount;
			igui.potTF.setText(String.valueOf(currentPot));

			// -- Sort out computer players bet ( chip amount needed etc )

			//Take users all in amount from the computers chips & add to current pot
			computerCash = Integer.parseInt(igui.compCashTF.getText());

			if(allInAmount > computerCash)
			{
				//Computer must be all in
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + computerCash;
				igui.potTF.setText(String.valueOf(currentPot));

				//Set computer chips to 0
				computerCash = 0;
				igui.compCashTF.setText(String.valueOf(userCash));
			}
			else
			{
				//Computer can call and have some chips left over
				computerCash = computerCash - allInAmount;
				//allInAmount = allInAmount * 2;

				//Add to current pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				currentPot = currentPot + allInAmount;
				igui.potTF.setText(String.valueOf(currentPot));

				//Add remaining chips to users cash
				igui.compCashTF.setText(String.valueOf(computerCash));

			}

			// -- Set user chips to 0
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			userCash = 0;
			igui.yourCashTF.setText(String.valueOf(userCash));

			//Determine a winner
			int winner = determineWinner();
			//Give the chips in the pot to who ever won
			if(winner == 1)
			{
				//Game user wins the hand
				//Move chips in pot to user chips
				currentPot = Integer.parseInt(igui.potTF.getText());
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + currentPot;
				currentPot = 0;

				//Set the new values of pot and user chips
				igui.potTF.setText(String.valueOf(currentPot));
				igui.yourCashTF.setText(String.valueOf(userCash));

				//Reset all game varaibles
				resetGame();


			}
			else
			if(winner == 2)
			{
				//Computer wins the hand
				//Move chips in pot to computers chips
				currentPot = Integer.parseInt(igui.potTF.getText());
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash + currentPot;
				currentPot = 0;

				//Set the new values of pot and user chips
				igui.potTF.setText(String.valueOf(currentPot));
				igui.compCashTF.setText(String.valueOf(computerCash));

				//Reset all game variables
				resetGame();
			}
			else
			{
				// There is a split pot - half to chips to each player
				//Get the amount of chips in the pot
				currentPot = Integer.parseInt(igui.potTF.getText());
				int amount = currentPot / 2;
				//Give half to each player
				//Give to user
				userCash = Integer.parseInt(igui.yourCashTF.getText());
				userCash = userCash + amount;
				igui.yourCashTF.setText(String.valueOf(userCash));
				//Give to computer
				computerCash = Integer.parseInt(igui.compCashTF.getText());
				computerCash = computerCash + amount;
				igui.compCashTF.setText(String.valueOf(computerCash));
			}


		}
		else
		{
			// -- Game user wins pot
			// -- Add all chips in the pot to the game users chipcount
			currentPot = Integer.parseInt(igui.potTF.getText());
			userCash = Integer.parseInt(igui.yourCashTF.getText());
			userCash = userCash + currentPot;
			igui.yourCashTF.setText(String.valueOf(userCash));

			// -- Set current pot and bet to 0
			currentPot = 0;
			igui.potTF.setText(String.valueOf(currentPot));
			amountBet = Integer.parseInt(igui.youBetTF.getText());
			amountBet = 0;
			igui.youBetTF.setText(String.valueOf(amountBet));

			//Reset the game
			gui.msgIndex = 0;
			resetGame();

		}
	}
}


/**
 * Fold current hand
 */
public void playerFold()
{
	int answer = JOptionPane.showConfirmDialog(null, "Are you Sure you wish to fold?", "Fold current Hand", JOptionPane.YES_NO_OPTION);
	if(answer == 0)
	{
		//Add chips in the current pot to the computer players chips
		currentPot = Integer.parseInt(igui.potTF.getText());
		computerCash = Integer.parseInt(igui.compCashTF.getText());
		computerCash = computerCash + currentPot;
		currentPot = 0;
		igui.potTF.setText(String.valueOf(currentPot));
		igui.compCashTF.setText(String.valueOf(computerCash));

		//Reset the game
		resetGame();

	}
}


//--------OTHER METHODS--------------//


/**
 * Determine the winner of a hand
 * @return 0 if the game users wins, 1 if the computer wins
 */
private int determineWinner()
{
	//Evaluate and return the winner
	int winner = handEval.compareHands(userCards, computerCards);

	if(winner == 1)
	{
		//debug("GAME USER WINS");
		String handName = handEval.nameHand(userCards);
		String comphand = handEval.nameHand(computerCards);
		//gui.screenMessage[9] = null;
		JOptionPane.showMessageDialog(null, "You win with a "+handName +"\nComputer had a "+comphand, "Message", JOptionPane.	INFORMATION_MESSAGE);
		//gui.screenMessage[9] = "You win with "+handName;
		//gui.msgIndex = 9;
		//gui.repaint();
		return 1;
	}
	else
	if(winner == 2)
	{
		//debug("COMPUTER WINS");
		String handName = handEval.nameHand(computerCards);
		String userHand = handEval.nameHand(userCards);
		//gui.screenMessage[9] = null;
		//gui.screenMessage[9] = "Computer wins with "+handName;
		JOptionPane.showMessageDialog(null, "Computer wins with a "+handName +"\nYou had a "+userHand, "Message", JOptionPane.	INFORMATION_MESSAGE);
		//gui.msgIndex = 9;
		//gui.repaint();
		return 2;

	}
	else
	{
		//debug("Spilt pot");
		String name = "Split Pot";
		//gui.screenMessage[9] = null;
		//gui.screenMessage[9] = "There is a Split Pot";
		JOptionPane.showMessageDialog(null, "There is a Split pot", "Message", JOptionPane.	INFORMATION_MESSAGE);
		//gui.msgIndex = 9;
		//gui.repaint();
		return 0;
	}

}


/**
 * Resets the games variables
 */
private void resetGame()
{
	//Reset all game variables
	//User hand
	gui.userHand[0] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.userHand[1] = GUICreationHelper.loadImage(gui.faceDownCard);

	//Computer hand
	gui.computerHand[0] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.computerHand[1] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.msgIndex = 0;

	//Community cards
	gui.community[0] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.community[1] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.community[2] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.community[3] = GUICreationHelper.loadImage(gui.faceDownCard);
	gui.community[4] = GUICreationHelper.loadImage(gui.faceDownCard);

	//Clear the bet text field
	amountBet = 0;
	igui.youBetTF.setText(String.valueOf(amountBet));
	gui.repaint();

	//Community Cards
	//for(int i = 0; i < gui.community.length; i++)
	//{
	//	gui.community[i] = GUICreationHelper.loadImage(gui.faceDownCard);
	//}

	//Clear the users & computers hands
	userCards.makeEmpty();
	computerCards.makeEmpty();

	//Clear the all cards dealt array
	for(int i = 0; i < allCardsDealt.length; i++)
	{
		allCardsDealt[i] = null;
	}

	//Reset the allCardsDealtIndex
	allCardsIndex = 0;

	//Reset the hand generator Index
	handGen.index = 0;
	//Reset the suit generator Index
	handGen.suitGen.suitIndex = 0;

	//Reset game variables
	userCheck = false;
	userAction = false;
	userBet = false;
	userAllIn = false;
	userCall = false;
	userFold = false;
	userRaise = false;
	dealStage = 1;

	//Re-initialise the hand generator
	handGen = new HandGenerator();

	//Destroy the thread
	gameThread.stop();
	//debug("Thread destroyed");
}


/**
 * Check that a card has not been previously
 * dealt to a player or on the flop etc.
 * @param card The card to check
 * @return true iff card has been previously dealt, false otherwise
 */
public boolean checkCardNotDealt(String card)
{
	//Run through allCardsDealtArray and check if this card has been dealt already
	for(int current = 0; current <= allCardsDealt.length; current++)
	{
		if(allCardsDealt[current] == null)
		return false;
		else
		if(allCardsDealt[current].equals(card))
		{
			return true;
		}
	//return false;

	}

	return false;
}

/**
 * Return the URL of the image of this card
 * @param card The card whose image is to be returned
 */
public String parseCardImage(String card)
{
	//Hearts
	if(card.equals("2h")) return PokerGUI.TWO_HEARTS;
	else
	if(card.equals("3h")) return PokerGUI.THREE_HEARTS;
	else
	if(card.equals("4h")) return PokerGUI.FOUR_HEARTS;
	else
	if(card.equals("5h")) return PokerGUI.FIVE_HEARTS;
	else
	if(card.equals("6h")) return PokerGUI.SIX_HEARTS;
	else
	if(card.equals("7h")) return PokerGUI.SEVEN_HEARTS;
	else
	if(card.equals("8h")) return PokerGUI.EIGHT_HEARTS;
	else
	if(card.equals("9h")) return PokerGUI.NINE_HEARTS;
	else
	if(card.equals("10h")) return PokerGUI.TEN_HEARTS;
	else
	if(card.equals("Jh")) return PokerGUI.JACK_HEARTS;
	else
	if(card.equals("Qh")) return PokerGUI.QUEEN_HEARTS;
	else
	if(card.equals("Kh")) return PokerGUI.KING_HEARTS;
	else
	if(card.equals("Ah")) return PokerGUI.ACE_HEARTS;

	//Clubs
	if(card.equals("2c")) return PokerGUI.TWO_CLUBS;
	else
	if(card.equals("3c")) return PokerGUI.THREE_CLUBS;
	else
	if(card.equals("4c")) return PokerGUI.FOUR_CLUBS;
	else
	if(card.equals("5c")) return PokerGUI.FIVE_CLUBS;
	else
	if(card.equals("6c")) return PokerGUI.SIX_CLUBS;
	else
	if(card.equals("7c")) return PokerGUI.SEVEN_CLUBS;
	else
	if(card.equals("8c")) return PokerGUI.EIGHT_CLUBS;
	else
	if(card.equals("9c")) return PokerGUI.NINE_CLUBS;
	else
	if(card.equals("10c")) return PokerGUI.TEN_CLUBS;
	else
	if(card.equals("Jc")) return PokerGUI.JACK_CLUBS;
	else
	if(card.equals("Qc")) return PokerGUI.QUEEN_CLUBS;
	else
	if(card.equals("Kc")) return PokerGUI.KING_CLUBS;
	else
	if(card.equals("Ac")) return PokerGUI.ACE_CLUBS;

	//Diamonds
	if(card.equals("2d")) return PokerGUI.TWO_DIAMONDS;
	else
	if(card.equals("3d")) return PokerGUI.THREE_DIAMONDS;
	else
	if(card.equals("4d")) return PokerGUI.FOUR_DIAMONDS;
	else
	if(card.equals("5d")) return PokerGUI.FIVE_DIAMONDS;
	else
	if(card.equals("6d")) return PokerGUI.SIX_DIAMONDS;
	else
	if(card.equals("7d")) return PokerGUI.SEVEN_DIAMONDS;
	else
	if(card.equals("8d")) return PokerGUI.EIGHT_DIAMONDS;
	else
	if(card.equals("9d")) return PokerGUI.NINE_DIAMONDS;
	else
	if(card.equals("10d")) return PokerGUI.TEN_DIAMONDS;
	else
	if(card.equals("Jd")) return PokerGUI.JACK_DIAMONDS;
	else
	if(card.equals("Qd")) return PokerGUI.QUEEN_DIAMONDS;
	else
	if(card.equals("Kd")) return PokerGUI.KING_DIAMONDS;
	else
	if(card.equals("Ad")) return PokerGUI.ACE_DIAMONDS;

	//Spades
	if(card.equals("2s")) return PokerGUI.TWO_SPADES;
	else
	if(card.equals("3s")) return PokerGUI.THREE_SPADES;
	else
	if(card.equals("4s")) return PokerGUI.FOUR_SPADES;
	else
	if(card.equals("5s")) return PokerGUI.FIVE_SPADES;
	else
	if(card.equals("6s")) return PokerGUI.SIX_SPADES;
	else
	if(card.equals("7s")) return PokerGUI.SEVEN_SPADES;
	else
	if(card.equals("8s")) return PokerGUI.EIGHT_SPADES;
	else
	if(card.equals("9s")) return PokerGUI.NINE_SPADES;
	else
	if(card.equals("10s")) return PokerGUI.TEN_SPADES;
	else
	if(card.equals("Js")) return PokerGUI.JACK_SPADES;
	else
	if(card.equals("Qs")) return PokerGUI.QUEEN_SPADES;
	else
	if(card.equals("Ks")) return PokerGUI.KING_SPADES;
	else
	if(card.equals("As")) return PokerGUI.ACE_SPADES;
	if(card.equals("")) return "EMPTY STRING";

	return "Unknown Card";

}

/**
 * Deal the flop
 */
private void dealFlop()
{
	//Deal 3 cards
	flop1 = handGen.generate1Card();
	flop2 = handGen.generate1Card();
	flop3 = handGen.generate1Card();

	//debug("\nFlop: ");
	//debug(flop1);
	///debug(flop2);
	//debug(flop3);

	//Ensure these cards have not already been dealt
	//Flop 1
	if(checkCardNotDealt(flop1) == false)
	{
		//Add card to allCardsDealt array
		allCardsDealt[allCardsIndex] = flop1;
		allCardsIndex++;
		//debug("Flop 1 has not been dealt before");
	}
	else
	{
		//Deal another card
		flop1 = handGen.generate1Card();
		//debug("FLOP 1 has been dealt before");
		while(checkCardNotDealt(flop1) != false)
		{
			flop1 = handGen.generate1Card();
		}

		//Add to allCardsDealt
		allCardsDealt[allCardsIndex] = flop1;
		allCardsIndex++;

	}

	//Flop 2
	if(checkCardNotDealt(flop2) == false)
	{
		//Add card to allCardsDealt array
		allCardsDealt[allCardsIndex] = flop2;
		allCardsIndex++;
		//debug("Flop 2 has not been dealt before");
	}
	else
	{
		//Deal another card
		flop2 = handGen.generate1Card();
		//debug("FLOP 2 has been dealt before");
		while(checkCardNotDealt(flop2) != false)
		{
			flop2 = handGen.generate1Card();
		}

		//Add to allCardsDealt
		allCardsDealt[allCardsIndex] = flop2;
		allCardsIndex++;
	}

	//Flop 3
	if(checkCardNotDealt(flop3) == false)
	{
		//Add card to allCardsDealt array
		allCardsDealt[allCardsIndex] = flop3;
		allCardsIndex++;
		//debug("Flop 3 has not been dealt before");
	}
	else
	{
		//Deal another card
		flop3 = handGen.generate1Card();
		//debug("FLOP 3 has been dealt before");
		while(checkCardNotDealt(flop3) != false)
		{
			flop3 = handGen.generate1Card();
		}

		//Add to allCardsDealt
		allCardsDealt[allCardsIndex] = flop3;
		allCardsIndex++;
	}


	//Add flop images to the GUI
	String card1 = parseCardImage(allCardsDealt[4]);
	String card2 = parseCardImage(allCardsDealt[5]);
	String card3 = parseCardImage(allCardsDealt[6]);


	//Update the community cards images array
	gui.community[0] = GUICreationHelper.loadImage(card1);
	gui.community[1] = GUICreationHelper.loadImage(card2);
	gui.community[2] = GUICreationHelper.loadImage(card3);


	//Update the screen message
	//gui.msgIndex++;

	//Repaint the screen
	//gui.repaint(220, 320, 300, 300);
	//gui.validate();
	//gui.show();
	//gui.invalidate();
	gui.repaint(1);


	//Add the cards dealt on the flop to the users and computers hands
	//User hand
	flopCard1 = new Card(flop1);
	flopCard2 = new Card(flop2);
	flopCard3 = new Card(flop3);
	userCards.addCard(flopCard1);
	userCards.addCard(flopCard2);
	userCards.addCard(flopCard3);

	//TEST: = OUTPUT THE CONTENTS OF USERS HAND AT THIS POINT
	//String usersHand = userCards.toString();
	//debug("\n");
	//debug("Users Hand: ");
	//debug(usersHand);

	//Computer hand
	computerCards.addCard(flopCard1);
	computerCards.addCard(flopCard2);
	computerCards.addCard(flopCard3);

	//TEST: = OUTPUT THE CONTENTS OF USERS HAND AT THIS POINT
	//String compHand = computerCards.toString();
	//debug("\n");
	//debug("Computer Hand: ");
	//debug(compHand);

}



/**
 * Deal the turn card
 */
private void dealTurn()
{
	//Deal card
	String turn;
	Card turnCard;
	turn = handGen.generate1Card();

	//debug("\nTurn Card: ");
	//debug(turn);

	//Ensure this card has not already been dealt
	if(checkCardNotDealt(turn) == false)
	{
		//Add card to allCardsDealt array
		allCardsDealt[allCardsIndex] = turn;
		allCardsIndex++;
		//debug("Turn has not been dealt before");
	}
	else
	{
		//Deal another card
		turn = handGen.generate1Card();
		//debug("Turn has been dealt before");
		while(checkCardNotDealt(turn) != false)
		{
			turn = handGen.generate1Card();
		}

		//Add to allCardsDealt
		allCardsDealt[allCardsIndex] = turn;
		allCardsIndex++;

	}

	//Add the image of the turn card to the GUI
	String card1 = parseCardImage(allCardsDealt[7]);

	//Update the user hand images array
	gui.community[3] = GUICreationHelper.loadImage(card1);

	//Update the screen message
	//gui.msgIndex++;

	//Repaint the screen
	//gui.repaint(220, 320, 300, 300);
	//gui.validate();
	//gui.show();
	//gui.invalidate();
	gui.repaint(1);

	//Add the turn card to the users and computers hands
	turnCard = new Card(turn);
	userCards.addCard(turnCard);
	computerCards.addCard(turnCard);


}


/**
 * Deal the river
 */
private void dealRiver()
{
	//Deal card
	String river;
	Card riverCard;
	river = handGen.generate1Card();

	//debug("\nRiver Card: ");
	//debug(river);

	//Ensure this card has not already been dealt
	if(checkCardNotDealt(river) == false)
	{
		//Add card to allCardsDealt array
		allCardsDealt[allCardsIndex] = river;
		allCardsIndex++;
		//debug("River has not been dealt before");
	}
	else
	{
		//Deal another card
		river = handGen.generate1Card();
		//debug("River has been dealt before");
		while(checkCardNotDealt(river) != false)
		{
			river = handGen.generate1Card();
		}

		//Add to allCardsDealt
		allCardsDealt[allCardsIndex] = river;
		allCardsIndex++;

	}


	//Debug -: Output contents of allCardDealt array
	//for(int i = 0; i < allCardsDealt.length; i++)
	//{
	//	JOptionPane.showMessageDialog(null, "Card: "+allCardsDealt[i], "allCardsDealt Array Contents", JOptionPane.INFORMATION_MESSAGE);
	//}


	//Add the image of the turn card to the GUI
	String card1 = parseCardImage(allCardsDealt[8]);

	//Update the user hand images array
	gui.community[4] = GUICreationHelper.loadImage(card1);

	//Update the screen message
	//gui.msgIndex++;

	//Repaint the screen
	//gui.repaint(220, 320, 300, 300);
	//gui.validate();
	//gui.show();
	//gui.invalidate();
	gui.repaint(1);

	//Add the river card to each players hand
	riverCard = new Card(river);
	userCards.addCard(riverCard);
	computerCards.addCard(riverCard);

	//TEST: = OUTPUT THE CONTENTS OF USERS & COMPUTERS HAND AT THIS POINT
	//String usersHand = userCards.toString();
	//debug("\n");
	//debug("Users Hand: ");
	//debug(usersHand);
	//String compHand = computerCards.toString();
	//debug("\n");
	//debug("Computers Hand: ");
	//debug(compHand);

}


/**
 * Fills the premium hands array
 */
private void makePremiumHands()
{
	premiumHands[0] = "AA";
	premiumHands[1] = "AK";
	premiumHands[2] = "AQ";
	premiumHands[3] = "AJ";
	premiumHands[4] = "JA";
	premiumHands[5] = "A9";
	premiumHands[6] = "A8";
	premiumHands[7] = "KK";
	premiumHands[8] = "KQ";
	premiumHands[9] = "KJ";
	premiumHands[10] = "K2";
	premiumHands[11] = "JJ";
	premiumHands[12] = "KA";
	premiumHands[13] = "QK";
	premiumHands[14] = "QJ";
	premiumHands[15] = "QQ";
	premiumHands[16] = "QA";
	premiumHands[17] = "JQ";
	premiumHands[18] = "J9";
	premiumHands[19] = "K9";
	premiumHands[20] = "A7";
	premiumHands[21] = "K9";
	premiumHands[22] = "Q9";
	premiumHands[23] = "K5";
	premiumHands[24] = "A3";
	premiumHands[25] = "A4";
	premiumHands[26] = "A5";
	premiumHands[27] = "7K";
	premiumHands[28] = "J4";
	premiumHands[29] = "3A";
	premiumHands[30] = "89";
	premiumHands[31] = "JK";
	premiumHands[32] = "98";
	premiumHands[33] = "5A";
	premiumHands[34] = "3A";
	premiumHands[35] = "4K";
	premiumHands[36] = "8J";
	premiumHands[37] = "9J";
	premiumHands[38] = "3K";
	premiumHands[39] = "9K";
	premiumHands[40] = "99";
	premiumHands[41] = "88";
	premiumHands[42] = "77";
	premiumHands[43] = "66";
	premiumHands[44] = "55";
	premiumHands[45] = "44";
	premiumHands[46] = "33";
	premiumHands[47] = "22";
	premiumHands[48] = "2A";
	premiumHands[49] = "5Q";
	premiumHands[50] = "8K";
	premiumHands[51] = "9A";
	premiumHands[52] = "A6";
	premiumHands[53] = "K6";
	premiumHands[54] = "Q6";
	premiumHands[55] = "J7";
	premiumHands[56] = "6J";
	premiumHands[57] = "45";
	premiumHands[58] = "68";
	premiumHands[59] = "J2";


}

//---------UTILITY FUNCTIONS---------//

/**
 * Used to hasten debugging
 */
private void debug(String msg)
{
	System.out.println(msg);
}

//---------TEST METHODS--------------//

/**
 * Test method
 * @param agrs Command line arguments
 */
public static void main(String[] args)
{

}

}
