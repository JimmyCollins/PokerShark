
/**
 * The main GUI
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


public class MainGUI extends JPanel
{

/**
 * A poker gui Object
 */
private PokerGUI pokerGUI;


/**
 * An Information Interface Object
 */
public InformationInterface infoInterface;


/**
 * A Control Panel Object
 */
private ControlPanel controlPanel;



/**
 * No argument Constructor
 */
public MainGUI()
{
	super();

	//Initialise the Objects
	pokerGUI = new PokerGUI();
	infoInterface = new InformationInterface();
	controlPanel = new ControlPanel();

	controlPanel.setInterface(infoInterface);
	controlPanel.getController().getPokerMain().setGUI(pokerGUI);

	//Set a layout manager
	BorderLayout bl1 = new BorderLayout();
	setLayout(bl1);

	//Add the panels
	add(infoInterface, BorderLayout.NORTH);
	add(pokerGUI, BorderLayout.CENTER);
	add(controlPanel, BorderLayout.SOUTH);

	System.out.println("Application Running\n");

}

}