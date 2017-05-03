
/**
 * Runs the game
 *
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie>
 * @date 17th October 2006
 *
 * Last Edit: 17th October 2006
 */


import javax.swing.*;
import javax.swing.event.*;
import java.awt.Dialog.*;
import java.awt.*;
import java.awt.event.*;


public class RunPokerShark
{

/**
 * Test Method
 * @param Command line arguments
 * @throws Exception
 */
public static void main(String[] args)
{
	JFrame frm = new JFrame("PokerShark Limit Texas Hold'em Heads Up Version 1.6");

	//GUI Object
	MainGUI panel = new MainGUI();

	Container contentPane = frm.getContentPane();
	frm.setResizable(false);
	frm.setLocation(200, 70);

	contentPane.add(panel);

	//frm.setDefaultLookAndFeelDecorated(true);
	//UIManager.put("activecaption", Color.green);

	frm.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent we) {
			System.exit(0);
			 }
			  });


	frm.pack();

	frm.setVisible(true);
}

}