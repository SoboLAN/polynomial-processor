package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/** Contains the main () thread. */
public class Main
{
	//specifies minimum major version. Examples: 5 (JRE 5), 6 (JRE 6), 7 (JRE 7) etc.
	private static final int MAJOR_VERSION = 6;
	
	//specifies minimum minor version. Examples: 12 (JRE 6u12), 23 (JRE 6u23), 2 (JRE 7u2) etc.
	private static final int MINOR_VERSION = 14;

	//returns true if the user's screen resolution is big enough to display
	//the program's window
	private static boolean isOKScreenResolution ()
	{
		//get the resolution (index 0: width, index 1: height)
		int[] resolution = GUIUtilities.getResolution ();

		//check if the size of the window would fit on the screen
		return (GUI.GUIWIDTH < resolution[0] && GUI.GUIHEIGHT < resolution[1]);
	}

	//checks if the version of the currently running JVM is bigger than
	//the minimum version required to run this program.
	//returns true if it's ok, false otherwise
	private static boolean checkVersion ()
	{
		//get the JVM version
		String version = System.getProperty ("java.version");

		//extract the major version from it
		int sys_major_version = Integer.parseInt (String.valueOf (version.charAt (2)));
		
		//if the major version is too low (unlikely !!), it's not good
		if (sys_major_version < MAJOR_VERSION)
		{
			return false;
		}
		else if (sys_major_version > MAJOR_VERSION)
		{
			return true;
		}
		else
		{
			//find the underline ( "_" ) in the version string
			int underlinepos = version.lastIndexOf ("_");

			Integer mv;

			try
			{
				//everything after the underline is the minor version.
				//extract that
				mv = Integer.parseInt (version.substring (underlinepos + 1));
			}
			//if it's not ok, then the version is probably not good
			catch (NumberFormatException e)
			{
				return false;
			}

			//if the minor version passes, wonderful
			return (mv.intValue () >= MINOR_VERSION);
		}
	}
	
	//displays an error dialog on the screen using a temporary
	//jframe as parent and disposes that jframe when the dialog is
	//closed (will exit application if no other non-daemon threads are running (like another JFrame))
	//parameters: title (title of the dialog window), message (the error message)
	private static void displayErrorDialog (String title, String message)
	{
		//create temporary jframe
		JFrame invisibleparentframe = new JFrame ();
		
		//make it invisible, since we don't need it
		invisibleparentframe.setVisible (false);

		//display the error message
		GUIUtilities.showErrorDialog (invisibleparentframe, message, title);

		//dispose of the parent frame
		invisibleparentframe.dispose ();
	}

	/** The main () thread. */
	public static void main (String[] args)
	{
		//check if the minimum version is ok
		if (! Main.checkVersion ())
		{
			String title = "Polynomial Processor Minimum Version Error";
			String message = "JVM version detected: " + System.getProperty ("java.version") + ". Minimum version required: " + MAJOR_VERSION + " Update " + MINOR_VERSION + ".";
		
			//display an error message
			Main.displayErrorDialog (title, message);
			
			return;
		}
		
		//check if the screen resolution is OK
		if (! Main.isOKScreenResolution ())
		{
			String title = "Polynomial Processor Minimum Resolution Error";
			String message = "Minimum resolution required: Width = " + GUI.GUIWIDTH + ", Height: " + GUI.GUIHEIGHT + ".";

			//display an error message
			Main.displayErrorDialog (title, message);
			
			return;
		}
		
		//tells whether nimbus lnf was set
		boolean lnfset = false;

		//attempt to set the Nimbus LookAndFeel (if available)
		try
		{
			//iterate over all installed lookandfeels
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels ())
			{
				if (info.getName ().equals ("Nimbus"))
				{
					//set Nimbus
					UIManager.setLookAndFeel (info.getClassName ());
					
					//mark that it was set
					lnfset = true;
					
					break;
				}
			}
		}
		//if there is a problem (shouldn't be), exit application
		catch (Exception e2)
		{
			System.exit (11);
		}

		//check if nimbus was set
		if (! lnfset)
		{
			//attempt to set the default LNF for this system
			try
			{
				UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
			}
			//if there is a problem (shouldn't be), exit application
			catch (Exception e3)
			{
				System.exit (11);
			}
		}

		//everything that happens on the GUI should run
		//on swing's event dispatch thread
		javax.swing.SwingUtilities.invokeLater (new Runnable ()
		{
			@Override public void run ()
			{
				JFrame.setDefaultLookAndFeelDecorated (true);
	
				//create the GUI
				GUI f = GUI.createGUI ();

				//get the resolution of the window
				int[] resolution = GUIUtilities.getResolution ();

				//find the required coordinates
				int xlocation = (resolution[0] - GUI.GUIWIDTH) / 2;
				int ylocation = (resolution[1] - GUI.GUIHEIGHT) / 2;
					
				//set the window in the middle of the screen
				f.setLocation (xlocation, ylocation);

				//make it visible
				f.setVisible (true);
			}
		});
	}
}