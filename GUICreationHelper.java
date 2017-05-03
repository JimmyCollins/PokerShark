import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.*;

public class GUICreationHelper {

  //private PokerGUI gui = new PokerGUI();
  private static Font titleFont = new Font("Arial",Font.BOLD,12);
  private static LineBorder textFieldBorder = new LineBorder(Color.black,1);
  private static final Component comp = new Component(){};
  private static final MediaTracker mt = new MediaTracker(comp);
  private static int imageID=0;

  public static Image loadImage(String fileName)
  {
      URL imgURL = GUICreationHelper.class.getResource(fileName);
      if (imgURL != null)
      {
          Image loadedImage = Toolkit.getDefaultToolkit().getImage(imgURL);
          waitForImage(loadedImage);
          return loadedImage;
      }
      else
      {
          System.err.println("GUICreationHelper.loadImage() Couldn't find file: " + fileName);
          return null;
      }
  }

  public static Image loadImageFromFile(String fileName){

          Image loadedImage = Toolkit.getDefaultToolkit().getImage(fileName);
          waitForImage(loadedImage);
          return loadedImage;

  }

  public static String getCodeBase(){
      URL codebase = GUICreationHelper.class.getResource("GUICreationHelper.class");
      if (codebase != null){
          String cb = codebase.toString();
          cb = cb.substring(0,cb.indexOf("guiHelper/"));
          int length = cb.length();
          for (int i = 0; i < length; i++) {
            char x = cb.charAt(i);
            if(x == '%'){
                cb = cb.substring(0,i) + " " +cb.substring(i+3,length);
                length = cb.length();
            }
          }
          return cb;
      }else {
          System.err.println("GUICreationHelper.loadImage() Couldn't find codebase ");
          return null;
      }
  }

  public static ImageIcon createImageIcon(String fileName) {
        URL imgURL = GUICreationHelper.class.getResource(fileName);
        if (imgURL != null){
            return new ImageIcon(imgURL);
        } else {
            System.err.println("GUICreationHelper.loadImage() Couldn't find file: " + fileName);
            return null;
        }
    }

  private static boolean waitForImage(Image theImage){
	  //System.out.println("Waiting for image");
      int id; synchronized(comp){id=imageID++;}
      mt.addImage(theImage,id);
      try{mt.waitForID(id);}
      catch(InterruptedException ie){return false;}
      return mt.isErrorID(id);
  }

  public static void createTitledBorder(JComponent theComponent, String title){
      TitledBorder titledBorder = new TitledBorder(new EtchedBorder());
      titledBorder.setTitle(title);
      titledBorder.setTitleFont(titleFont);
      theComponent.setBorder(titledBorder);
  }

  public static void centerWindow(Window theWindow){
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = theWindow.getSize();
      if (frameSize.height > screenSize.height)
          frameSize.height = screenSize.height;
      if (frameSize.width > screenSize.width)
          frameSize.width = screenSize.width;
      theWindow.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
  }

  public static void textFieldToLabel(JTextField theJTextField){
      theJTextField.setDisabledTextColor(Color.blue);
      theJTextField.setEnabled(false);
      theJTextField.setBorder(textFieldBorder);
      theJTextField.setBackground(Color.white);
  }

}
