
/**
 * The main GUI Controller
 *
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie>
 * @version 1.0
 * @date January 30th 2007
 */



import java.awt.*;
import java.awt.event.*;
import java.awt.Dialog.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class Controller implements ActionListener
{

/**
 * PokerMain Objects
 */
private PokerMain main;



/**
 * ControlPanel Object
 */
public ControlPanel controlP;

/**
 * Information interface
 */
//private InformationInterface infoInterface;



/**
 * No argument Constructor
 */
public Controller(ControlPanel p /*InformationInterface i*/)
{
	super();
	main = new PokerMain(this);
	controlP = p;
	//infoInterface = i;

}

public PokerMain getPokerMain()
{
	return main;
}

public void resetAfterComputerFold()
{
	controlP.deal.setEnabled(true);
	controlP.check.setEnabled(false);
	controlP.bet.setEnabled(false);
	controlP.call.setEnabled(false);
	controlP.raise.setEnabled(false);
	controlP.allIn.setEnabled(false);
	controlP.fold.setEnabled(false);
	controlP.exit.setEnabled(true);
}

/**
 * Listens for GUI actions and updates
 * game accordingly
 * @param ae The action being performed
 */
public void actionPerformed(ActionEvent ae)
{
    String str = ae.getActionCommand();
    if(str.equals("Deal"))
    {
		//dealCards();
		controlP.deal.setEnabled(false);
		//Set rest of buttons active
		controlP.check.setEnabled(true);
		controlP.bet.setEnabled(true);
		controlP.call.setEnabled(false);
		controlP.raise.setEnabled(false);
		controlP.allIn.setEnabled(true);
		controlP.fold.setEnabled(true);

		main.initGame();



	}
	else
	if(str.equals("Bet"))
	{
		main.playerBet();
		main.userBet = true;
		main.userAction = true;
		//controlP.bet.setEnabled(false);
	}
	else
	if(str.equals("Check"))
	{
		main.playerCheck();
		main.userCheck = true;
		main.userAction = true;
		//controlP.check.setEnabled(false);
	}
	else
	if(str.equals("Call"))
	{
		main.playerCall();
		main.userAction = true;
		main.userCall = true;
		//controlP.call.setEnabled(false);
	}
	else
	if(str.equals("Raise"))
	{
		main.playerRaise();
		main.userAction = true;
		main.userRaise = true;
		//controlP.raise.setEnabled(false);
	}
	else
	if(str.equals("All In"))
	{
		main.playerAllIn();
		controlP.deal.setEnabled(true);
		controlP.check.setEnabled(false);
		controlP.bet.setEnabled(false);
		controlP.call.setEnabled(false);
		controlP.raise.setEnabled(false);
		controlP.allIn.setEnabled(false);
		controlP.fold.setEnabled(false);
		main.userAction = true;
		main.userAllIn = true;
		//controlP.allIn.setEnabled(false);
	}
	else
	if(str.equals("Fold"))
	{
		main.playerFold();
		main.userFold = true;
		controlP.deal.setEnabled(true);
		controlP.check.setEnabled(false);
		controlP.bet.setEnabled(false);
		controlP.call.setEnabled(false);
		controlP.raise.setEnabled(false);
		controlP.allIn.setEnabled(false);
		controlP.fold.setEnabled(false);
	}
	else
	if(str.equals("Exit"))
	{
		//Exit the System
		int answer = JOptionPane.showConfirmDialog(null, "Do you really want to exit?", "Exit PokerShark", JOptionPane.YES_NO_OPTION);
		if(answer == 0)
		{
			System.exit(1);
		}

	}

}

public void setInterface(InformationInterface i)
{
	 main.setInterface(i);
}


}