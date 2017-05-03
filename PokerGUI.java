
/**
 * The main Poker GUI
 *
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie>
 * @version 1.2
 * @date January 29th 2007
 */



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class PokerGUI extends JPanel //implements ActionListener
{

//----------------INSTANCE VARIABLES----------------//

//The entire 52 card deck
//Hearts
public static String ACE_HEARTS = "Images/Deck/ah.gif";
public static String TWO_HEARTS = "Images/Deck/2h.gif";
public static String THREE_HEARTS = "Images/Deck/3h.gif";
public static String FOUR_HEARTS = "Images/Deck/4h.gif";
public static String FIVE_HEARTS = "Images/Deck/5h.gif";
public static String SIX_HEARTS = "Images/Deck/6h.gif";
public static String SEVEN_HEARTS = "Images/Deck/7h.gif";
public static String EIGHT_HEARTS = "Images/Deck/8h.gif";
public static String NINE_HEARTS = "Images/Deck/9h.gif";
public static String TEN_HEARTS = "Images/Deck/th.gif";
public static String JACK_HEARTS = "Images/Deck/jh.gif";
public static String QUEEN_HEARTS = "Images/Deck/qh.gif";
public static String KING_HEARTS = "Images/Deck/kh.gif";
//Clubs
public static String ACE_CLUBS = "Images/Deck/ac.gif";
public static String TWO_CLUBS = "Images/Deck/2c.gif";
public static String THREE_CLUBS = "Images/Deck/3c.gif";
public static String FOUR_CLUBS = "Images/Deck/4c.gif";
public static String FIVE_CLUBS = "Images/Deck/5c.gif";
public static String SIX_CLUBS = "Images/Deck/6c.gif";
public static String SEVEN_CLUBS = "Images/Deck/7c.gif";
public static String EIGHT_CLUBS = "Images/Deck/8c.gif";
public static String NINE_CLUBS = "Images/Deck/9c.gif";
public static String TEN_CLUBS = "Images/Deck/tc.gif";
public static String JACK_CLUBS = "Images/Deck/jc.gif";
public static String QUEEN_CLUBS = "Images/Deck/qc.gif";
public static String KING_CLUBS = "Images/Deck/kc.gif";
//Diamonds
public static String ACE_DIAMONDS = "Images/Deck/ad.gif";
public static String TWO_DIAMONDS = "Images/Deck/2d.gif";
public static String THREE_DIAMONDS = "Images/Deck/3d.gif";
public static String FOUR_DIAMONDS = "Images/Deck/4d.gif";
public static String FIVE_DIAMONDS = "Images/Deck/5d.gif";
public static String SIX_DIAMONDS = "Images/Deck/6d.gif";
public static String SEVEN_DIAMONDS = "Images/Deck/7d.gif";
public static String EIGHT_DIAMONDS = "Images/Deck/8d.gif";
public static String NINE_DIAMONDS = "Images/Deck/9d.gif";
public static String TEN_DIAMONDS = "Images/Deck/td.gif";
public static String JACK_DIAMONDS = "Images/Deck/jd.gif";
public static String QUEEN_DIAMONDS = "Images/Deck/qd.gif";
public static String KING_DIAMONDS = "Images/Deck/kd.gif";
//Spades
public static String ACE_SPADES = "Images/Deck/as.gif";
public static String TWO_SPADES = "Images/Deck/2s.gif";
public static String THREE_SPADES = "Images/Deck/3s.gif";
public static String FOUR_SPADES = "Images/Deck/4s.gif";
public static String FIVE_SPADES = "Images/Deck/5s.gif";
public static String SIX_SPADES = "Images/Deck/6s.gif";
public static String SEVEN_SPADES = "Images/Deck/7s.gif";
public static String EIGHT_SPADES = "Images/Deck/8s.gif";
public static String NINE_SPADES = "Images/Deck/9s.gif";
public static String TEN_SPADES = "Images/Deck/ts.gif";
public static String JACK_SPADES = "Images/Deck/js.gif";
public static String QUEEN_SPADES = "Images/Deck/qs.gif";
public static String KING_SPADES = "Images/Deck/Ks.gif";

/**
 * Application state
 */
public static int appState;

/**
 * The image while a card is face down
 */
public String faceDownCard = "Images/faceUp/faceDownCard2.gif";


/**
 * Color used for backgrounds
 */
private Color bgColour = new Color(488214);


/**
 * Used for on screen messages and an index to it
 */
public String[] screenMessage = new String[10];
public int msgIndex = 0;

/**
 * Font for the screen message
 */
public Font font;


/**
 * The images of the cards on the table
 */
//public static Image[] cards = new Image[9];
//public static int[] cardsToDisplay = new int[9];

/**
 * The computer players hand, community cards and users cards
 */
public static Image[] computerHand = new Image[2];
public static Image[] community = new Image[5];
public static Image[] userHand = new Image[2];

/**
 * The co-ordinates of the computers cards, the community cards
 * and the game users hand
 */
public static int[][] compCoords = {{220,50}, {300,50}};
public static int[][] communityCoords = {{100,180}, {180,180}, {260,180}, {340,180}, {420,180}};
public static int[][] userCoords = {{220,320}, {300,320}};

/**
 * Indexes for the above
 */
public static int[] compIndex = new int[2];
public static int[] communityIndex = new int[5];
public static int[] userIndex = new int[2];


/**
 * The entire 52 card deck images
 */
public static Image[] deck = new Image[52];


//----------------CONSTRUCTORS----------------//

/**
 * No argument Constructor
 */
public PokerGUI()
{
	super();

	//Set the application state
	appState = 0;

	//Set the size
	setPreferredSize(new Dimension(600,500));
	setOpaque(false);

	//Create the array of card images
	createDeckImages();
	createInitialCards();

	//Initialise the array of screen messages
	createScreenMessages();

	//Set the font
	font = new Font("ms comic sans", Font.BOLD, 12);
	setFont(font);


	//Computer Index
	for(int i = 0; i < computerHand.length; i++)
	{
		compIndex[i] = i;
	}
	//Community Index
	for(int c = 0; c < community.length; c++)
	{
		communityIndex[c] = c;
	}
	//User Hand Index
	for(int u = 0; u < userHand.length; u++)
	{
		userIndex[u] = u;
	}

}



/**
 * Paint Method
 * Draws UI Components
 * @param g A graphics Object
 */
public void paint(Graphics g)
{
	super.paint(g);

	//Fill the panel with the background colour
	g.setColor(bgColour);
	g.fillRect(0,0,this.getWidth(),this.getHeight());


	//Draw the computers cards
	for(int c = 0; c < computerHand.length; c++)
	{
		g.drawImage(computerHand[compIndex[c]], compCoords[c][0], compCoords[c][1], null);
	}
	//Draw the community cards
	for(int p = 0; p < community.length; p++)
	{
		g.drawImage(community[communityIndex[p]], communityCoords[p][0], communityCoords[p][1], null);
	}
	//Draw the players hand
	for(int p = 0; p < userHand.length; p++)
	{
		g.drawImage(userHand[userIndex[p]], userCoords[p][0], userCoords[p][1], null);
	}
	//Draw the on screen message JLabel
	g.setColor(Color.BLACK);

	g.drawString(screenMessage[msgIndex], 220, 450);
	//System.out.println("MSG "+msgIndex);
}

	public void increment()
	{
		//System.out.println("BEFORE Drawing Msg Index " +msgIndex);
		msgIndex++;
		//System.out.println("AFTER Drawing Msg Index " +msgIndex);
	}



/**
 * Test dealing some cards
 *
public void updateCards()
{

	String card1 = main.parseCardImage(main.allCardsDealt[0]);
	System.out.println("Card 1 Value: "+card1);


	String card2 = main.parseCardImage(main.allCardsDealt[1]);
	System.out.println("Card 2 Value: "+card2);


	//Update the user hand images array
	userHand[0] = GUICreationHelper.loadImage(card1);
	userHand[1] = GUICreationHelper.loadImage(card2);


	repaint();

}*/


/**
 * Create the Initial 9 face down cards
 */
 private void createInitialCards()
 {
	 //Create the initial 9 face down cards
	 //Two for the dealer, five community cards, and two for the player
	 //for(int i = 0; i < cards.length; i++)
	 //{
	//	 cards[i] = GUICreationHelper.loadImage(faceDownCard);
	 //}

	 //USED FOR LAYOUT PURPOSES - TO BE REMOVED
	 //cards[0] = GUICreationHelper.loadImage(ACE_HEARTS);
	 //cards[1] = GUICreationHelper.loadImage(TWO_HEARTS);
	 //cards[2] = GUICreationHelper.loadImage(THREE_HEARTS);
	 //cards[3] = GUICreationHelper.loadImage(FOUR_HEARTS);
	 //cards[4] = GUICreationHelper.loadImage(FIVE_HEARTS);
	 //cards[5] = GUICreationHelper.loadImage(SIX_HEARTS);
	 //cards[6] = GUICreationHelper.loadImage(SEVEN_HEARTS);
	 //cards[7] = GUICreationHelper.loadImage(EIGHT_HEARTS);
	 //cards[8] = GUICreationHelper.loadImage(NINE_HEARTS);

	 //Initial cards, Computer Hand
	 for(int i = 0; i < computerHand.length; i++)
	 {
		 computerHand[i] = GUICreationHelper.loadImage(faceDownCard);
	 }
	 //Communtiy cards
	 for(int j = 0; j < community.length; j++)
	 {
		 community[j] = GUICreationHelper.loadImage(faceDownCard);
	 }
	 //Users Hand
	 for(int n = 0; n < userHand.length; n++)
	 {
	     userHand[n] = GUICreationHelper.loadImage(faceDownCard);
	 }


 }


/**
 * Create the deck of card images
 */
private void createDeckImages()
{
	//Set the initial value of the cards
	//Hearts (0 - 12)
	deck[0] = GUICreationHelper.loadImage(ACE_HEARTS);
	deck[1] = GUICreationHelper.loadImage(TWO_HEARTS);
	deck[2] = GUICreationHelper.loadImage(THREE_HEARTS);
	deck[3] = GUICreationHelper.loadImage(FOUR_HEARTS);
	deck[4] = GUICreationHelper.loadImage(FIVE_HEARTS);
	deck[5] = GUICreationHelper.loadImage(SIX_HEARTS);
	deck[6] = GUICreationHelper.loadImage(SEVEN_HEARTS);
	deck[7] = GUICreationHelper.loadImage(EIGHT_HEARTS);
	deck[8] = GUICreationHelper.loadImage(NINE_HEARTS);
	deck[9] = GUICreationHelper.loadImage(TEN_HEARTS);
	deck[10] = GUICreationHelper.loadImage(JACK_HEARTS);
	deck[11] = GUICreationHelper.loadImage(QUEEN_HEARTS);
	deck[12] = GUICreationHelper.loadImage(KING_HEARTS);
	//Diamonds (13 - 25)
	deck[13] = GUICreationHelper.loadImage(ACE_DIAMONDS);
	deck[14] = GUICreationHelper.loadImage(TWO_DIAMONDS);
	deck[15] = GUICreationHelper.loadImage(THREE_DIAMONDS);
	deck[16] = GUICreationHelper.loadImage(FOUR_DIAMONDS);
	deck[17] = GUICreationHelper.loadImage(FIVE_DIAMONDS);
	deck[18] = GUICreationHelper.loadImage(SIX_DIAMONDS);
	deck[19] = GUICreationHelper.loadImage(SEVEN_DIAMONDS);
	deck[20] = GUICreationHelper.loadImage(EIGHT_DIAMONDS);
	deck[21] = GUICreationHelper.loadImage(NINE_DIAMONDS);
	deck[22] = GUICreationHelper.loadImage(TEN_DIAMONDS);
	deck[23] = GUICreationHelper.loadImage(JACK_DIAMONDS);
	deck[24] = GUICreationHelper.loadImage(QUEEN_DIAMONDS);
	deck[25] = GUICreationHelper.loadImage(KING_DIAMONDS);
	//Clubs (26 - 38)
	deck[26] = GUICreationHelper.loadImage(ACE_CLUBS);
	deck[27] = GUICreationHelper.loadImage(TWO_CLUBS);
	deck[28] = GUICreationHelper.loadImage(THREE_CLUBS);
	deck[29] = GUICreationHelper.loadImage(FOUR_CLUBS);
	deck[30] = GUICreationHelper.loadImage(FIVE_CLUBS);
	deck[31] = GUICreationHelper.loadImage(SIX_CLUBS);
	deck[32] = GUICreationHelper.loadImage(SEVEN_CLUBS);
	deck[33] = GUICreationHelper.loadImage(EIGHT_CLUBS);
	deck[34] = GUICreationHelper.loadImage(NINE_CLUBS);
	deck[35] = GUICreationHelper.loadImage(TEN_CLUBS);
	deck[36] = GUICreationHelper.loadImage(JACK_CLUBS);
	deck[37] = GUICreationHelper.loadImage(QUEEN_CLUBS);
	deck[38] = GUICreationHelper.loadImage(KING_CLUBS);
	//Spades (39 - 51)
	deck[39] = GUICreationHelper.loadImage(ACE_SPADES);
	deck[40] = GUICreationHelper.loadImage(TWO_SPADES);
	deck[41] = GUICreationHelper.loadImage(THREE_SPADES);
	deck[42] = GUICreationHelper.loadImage(FOUR_SPADES);
	deck[43] = GUICreationHelper.loadImage(FIVE_SPADES);
	deck[44] = GUICreationHelper.loadImage(SIX_SPADES);
	deck[45] = GUICreationHelper.loadImage(SEVEN_SPADES);
	deck[46] = GUICreationHelper.loadImage(EIGHT_SPADES);
	deck[47] = GUICreationHelper.loadImage(NINE_SPADES);
	deck[48] = GUICreationHelper.loadImage(TEN_SPADES);
	deck[49] = GUICreationHelper.loadImage(JACK_SPADES);
	deck[50] = GUICreationHelper.loadImage(QUEEN_SPADES);
	deck[51] = GUICreationHelper.loadImage(KING_SPADES);

}


/**
 * Initialise the screen messages array
 */
private void createScreenMessages()
{
	screenMessage[0] = "Welcome to PokerShark";
	screenMessage[1] = "                 Game On......";
	screenMessage[2] = "Make your decision";
	screenMessage[3] = "Waiting for Computer......";
	screenMessage[4] = "Computer Checks";
	screenMessage[5] = "";
	screenMessage[6] = "";
	screenMessage[7] = "";
	screenMessage[8] = "";
	screenMessage[9] = "";
}

}  //End of PokerGUI







/**
 * Provides the information interface
 *
 * @author Jimmy Collins
 * @date 29 January 2006
 */
class InformationInterface extends JPanel
{

/**
 * The labels used in the information panel at the top of the UI
 */
private JLabel pot;
private JLabel youBet;
private JLabel yourCash;
private JLabel compCash;


/**
 * The Text fields used to display amounts in the information panel
 */
public JTextField potTF;
public JTextField youBetTF;
public JTextField yourCashTF;
public JTextField compCashTF;


/**
 * The images used in the information panel
 */
private ImageIcon heart = new ImageIcon("Images/smallHeart.gif");
private ImageIcon club = new ImageIcon("Images/smallClub.gif");
private ImageIcon spade = new ImageIcon("Images/smallSpade.gif");
private ImageIcon diamond = new ImageIcon("Images/smallDiamond.gif");


/**
 * No argument Constructor
 */
public InformationInterface()
{
	super();

	//Debug
	//System.out.println("NEW INFORMATION INTERFACE OBJECT CREATED");

	Color bgColour = new Color(488214);
	setBackground(Color.white/*bgColour*/);

	//GridBagLayout gbl = new GridBagLayout();
	//GridBagConstraints gbc = new GridBagConstraints();
	FlowLayout fl1 = new FlowLayout();
	setLayout(fl1);

	//Initialise the labels and the textfields
	pot = new JLabel("Pot: ");
	youBet = new JLabel("Bet: ");
	yourCash = new JLabel("Your Chips: ");
	compCash = new JLabel("Opponent Chips: ");
	potTF = new JTextField(5);
	youBetTF = new JTextField(5);
	yourCashTF = new JTextField(5);
	compCashTF = new JTextField(5);

	//Create & initialise the labels for the images used here
	JLabel heartImage = new JLabel(heart);
	JLabel clubImage = new JLabel(club);
	JLabel spadeImage = new JLabel(spade);
	JLabel diamondImage = new JLabel(diamond);

	//Layout the items
	//add(heartImage);
	add(pot);
	add(potTF);
	add(heartImage);
	add(youBet);
	add(youBetTF);
	add(spadeImage);
	add(yourCash);
	add(yourCashTF);
	add(clubImage);
	add(compCash);
	add(compCashTF);
	add(diamondImage);

	//Set Initial values to the Text fields
	potTF.setText("0");
	youBetTF.setText("0");
	yourCashTF.setText("1000");
	compCashTF.setText("1000");


	//Set the text fields as disabled
	//potTF.setEnabled(false);
	//youBetTF.setEnabled(false);
	//yourCashTF.setEnabled(false);
	//compCashTF.setEnabled(false);
	potTF.setEditable(false);
	youBetTF.setEditable(false);
	yourCashTF.setEditable(false);
	compCashTF.setEditable(false);

}

} //End of Information Interface









/**
 * Provides the game users control panel
 *
 * @author Jimmy Collins
 * @date 29 January 2006
 */
class ControlPanel extends JPanel
{

/**
 * Controller Object
 */
private Controller controller;


/**
 * The buttons the user will use
 */
public JButton check;
public JButton raise;
public JButton call;
public JButton allIn;
public JButton fold;
public JButton bet;
public JButton exit;
public JButton deal;


/**
 * No argument Constructor
 */
public ControlPanel()
{
	super();

	//System.out.println("NEW CONTROL PANEL CREATED");

	//Initialise the controller
	controller = new Controller(this);

	//gameUserP = new JPanel();
	Color bgColour = new Color(488214);
	this.setBackground(/*Color.white*/bgColour);
	FlowLayout fl1 = new FlowLayout();
	setLayout(fl1);

	//Initialise the game buttons
	//check = new JButton("Check", new ImageIcon("Images/Buttons/Set1/check.gif"));
	deal = new JButton("Deal");
	bet = new JButton("Bet");
	check = new JButton("Check");
	raise = new JButton("Raise");
	call = new JButton("Call");
	allIn = new JButton("All In");
	fold = new JButton("Fold");
	exit = new JButton("Exit");

	//Add the buttons to the users control panel
	add(deal);
	add(bet);
	add(check);
 	add(call);
 	add(raise);
 	//add(allIn);
 	add(fold);
 	add(exit);

 	//Add tool tip text to these buttons
 	deal.setToolTipText("Click here to deal");
 	check.setToolTipText("Click here to Check");
 	raise.setToolTipText("Click here to raise your oppenent");
 	call.setToolTipText("Click here to call your opponents last bet");
 	allIn.setToolTipText("Click here to move all in");
 	bet.setToolTipText("Click here to bet");
 	fold.setToolTipText("Click here to fold your current hand");
	 exit.setToolTipText("Click here to exit - Your game will not be saved!");

 	//Set some buttons inactive until game is begun
	check.setEnabled(false);
 	raise.setEnabled(false);
 	call.setEnabled(false);
 	allIn.setEnabled(false);
 	bet.setEnabled(false);
 	fold.setEnabled(false);

 	//GUI Object
 	//PokerGUI gui = new PokerGUI();

 	//Add listener to these buttons
 	deal.addActionListener(controller);
 	check.addActionListener(controller);
 	raise.addActionListener(controller);
 	call.addActionListener(controller);
 	allIn.addActionListener(controller);
 	bet.addActionListener(controller);
 	fold.addActionListener(controller);
 	exit.addActionListener(controller);
}


public void setInterface(InformationInterface i)
{
	controller.setInterface(i);
}

public Controller getController()
{
	return controller;
}



} //End of Control Panel