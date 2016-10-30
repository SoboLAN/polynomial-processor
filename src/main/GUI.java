package main;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import polynomial.Polynomial;
import polynomial.PolynomialOperations;

/** This is the class that contains all GUI elements of the program (labels, buttons etc.). */
public final class GUI
{
	private static GUI _instance;

	//specify the width and height of the window
	public static final int GUIHEIGHT = 580;
	public static final int GUIWIDTH = 915;

	//the main JFrame
	private JFrame mainframe;

	//the 2 polynomials used as user input.
	private Polynomial x, y;
	
	//the operations object (contains methods for performing operations on the polynomials)
	private PolynomialOperations polynoper;

	//textfields...
	private JTextField magninput,
						coeffinput,
						topowertext;

	//the 4 panels that make up this GUI
	private JPanel inputpanel,
					controlspanel,
					polynpanel,
					resultspanel;

	private JTextArea txta, pol1text, pol2text;

	private JScrollPane jsp1, jsp2, jspresults;

	private JRadioButton polone, poltwo;

	//the same (custom) font will be used for all GUI elements
	private Font guifont;

	public static GUI createGUI ()
	{
		if (_instance == null)
		{
			_instance = new GUI ();
		}
		
		return _instance;
	}
	
	/** Constructor.
	*/
	private GUI ()
	{
		//create the window with the specified title
		this.mainframe = new JFrame ("Polynomial Processor");

		//tell the JFrame to exit the program when the user closes it.
		//(without this call, the default behaviour is to do nothing).
		this.mainframe.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);

		//set the size of the JFrame to the one's specified
		this.mainframe.setSize (GUIWIDTH, GUIHEIGHT);

		//the window will not be resizable by the user. It helps maintain the layout of the GUI components.
		this.mainframe.setResizable (false);

		//will use absolute positioning, so no layout
		this.mainframe.setLayout (null);

		//set the icon of the window
		this.mainframe.setIconImage (Toolkit.getDefaultToolkit ().getImage (getClass ().getResource ("images/letterp.png")));

		//create the polynomials (max size of 1000 seems reasonable)
		this.x = new Polynomial (1000);
		this.y = new Polynomial (1000);

		//create and retrieve the PolynomialOperations object.
		this.polynoper = PolynomialOperations.createOperationsObject (x, y);
		
		//font to be used accross the whole GUI
		this.guifont = new Font ("Arial", Font.BOLD, 12);
		
		//sets the menu bar
		this.mainframe.setJMenuBar (createMenuBar ());

		//create, position and add the inputpanel
		this.inputpanel = createInputPanel ();
		this.inputpanel.setBounds (10, 10, 300, 220);
		this.mainframe.add (inputpanel);

		//create, position and add the polynomials panel
		this.polynpanel = createPolynomialsPanel ();
		this.polynpanel.setBounds (320, 10, 585, 220);
		this.mainframe.add (polynpanel);

		//create, position and add the results panel
		this.resultspanel = createResultsPanel ();
		this.resultspanel.setBounds (320, 235, 585, 280);
		this.mainframe.add (resultspanel);

		//create, position and add the controls panel
		this.controlspanel = createControlsPanel ();
		this.controlspanel.setBounds (10, 235, 300, 280);
		this.mainframe.add (controlspanel);
	}

	/** Sets this Window visible or invisible
	*
	* @param on specify if the window should be visible or not
	*/
	public void setVisible (boolean on)
	{
		this.mainframe.setVisible (on);
	}
	
	/** Sets the location of the Window on the screen relative to
	* the top-left corner.
	*
	* @param x the number of pixels to move to the right
	*
	* @param y the number of pixels to move to the bottom
	*/
	public void setLocation (int x, int y)
	{
		this.mainframe.setLocation (x, y);
	}

	//creates the menubar for the window, then returns it
	private JMenuBar createMenuBar ()
	{
		//the menu-bar
		JMenuBar mybar = new JMenuBar ();

		//create the menus and add mnemonics to them
		JMenu filemenu = new JMenu ("File");
		filemenu.setMnemonic (KeyEvent.VK_F);
		JMenu commandmenu = new JMenu ("Commands");
		commandmenu.setMnemonic (KeyEvent.VK_C);
		JMenu helpmenu = new JMenu ("Help");
		helpmenu.setMnemonic (KeyEvent.VK_H);

		//create the menu-items
		JMenuItem exitaction = new JMenuItem ("Exit");
		JMenuItem emptyaction = new JMenuItem ("Empty Results Area");
		JMenuItem aboutaction = new JMenuItem ("About");

		//the Exit menu-item causes the main window to be disposed (and therefore exit the application)
		exitaction.addActionListener (new ActionListener ()
		{
			@Override public void actionPerformed (ActionEvent a)
			{
				mainframe.dispose ();
			}
		});

		//the Empty menu-item cleares the results area.
		emptyaction.addActionListener (new ActionListener ()
		{
			@Override public void actionPerformed (ActionEvent a)
			{
				txta.setText ("");
			}
		});
		
		//the About menu-item creates the about box (the panel contained in a dialog)
		//and displays it
		aboutaction.addActionListener (new ActionListener ()
		{
			@Override public void actionPerformed (ActionEvent a)
			{
				new AboutDialog (mainframe).display ();
			}
		});

		//add menu items to the menu
		filemenu.add (exitaction);
		commandmenu.add (emptyaction);
		helpmenu.add (aboutaction);

		//add the menus to the menu-bar
		mybar.add (filemenu);
		mybar.add (commandmenu);
		mybar.add (helpmenu);
		
		//return the menu-bar
		return mybar;
	}

	//creates the input panel
	private JPanel createInputPanel ()
	{
		//selectors for the polynomials.
		//whichever of these buttons is selected, that's the polynomial on which
		//the values are added/removed
		this.polone = new JRadioButton ("1", true);
		this.poltwo = new JRadioButton ("2", false);

		//add the radio-buttons to a button group
		ButtonGroup polgroup = new ButtonGroup ();
		polgroup.add (this.polone);
		polgroup.add (this.poltwo);

		JLabel pol = new JLabel ("Polynomial: ");
		JLabel magn = new JLabel ("Magnitude: ");
		JLabel coeff = new JLabel ("Coefficient: ");

		//text-fields for magnitude and coefficient user input
		this.magninput = new JTextField (4);
		this.coeffinput = new JTextField (5);

		//create the buttons
		JButton addval = new JButton ("Add Value");
		JButton remval = new JButton ("Remove Value");
		JButton resetpol = new JButton ("Reset Polynomial");
		JButton polswap = new JButton ("Swap Polynomials");

		//add the listeners to the buttons
		addval.addActionListener (new PolynomialValueAdder ());
		remval.addActionListener (new PolynomialValueRemover ());
		resetpol.addActionListener (new PolynomialResetter ());
		polswap.addActionListener (new PolynomialSwapper ());

		//create the panel and add its border
		JPanel panel = new JPanel ();
		panel.setLayout (null);
		panel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), "Input", TitledBorder.CENTER, TitledBorder.TOP));

		//next, position and add all the elements
		
		pol.setBounds (10, 30, 70, 20);
		panel.add (pol);

		this.polone.setBounds (85, 20, 40, 20);
		panel.add (this.polone);

		this.poltwo.setBounds (85, 50, 40, 20);
		panel.add (this.poltwo);

		magn.setBounds (10, 90, 70, 20);
		panel.add (magn);

		this.magninput.setBounds (85, 90, 35, 30);
		panel.add (this.magninput);

		coeff.setBounds (10, 130, 70, 20);
		panel.add (coeff);

		this.coeffinput.setBounds (85, 130, 35, 30);
		panel.add (this.coeffinput);

		addval.setBounds (140, 30, 145, 30);
		panel.add (addval);

		remval.setBounds (140, 70, 145, 30);
		panel.add (remval);

		resetpol.setBounds (140, 110, 145, 30);
		panel.add (resetpol);

		polswap.setBounds (140, 150, 145, 30);
		panel.add (polswap);

		//apply the same font for everything in this panel
		GUIUtilities.applyFont (panel);
		
		return panel;
	}

	//create the controls panel (buttons which perform the operations on the polynomials)
	private JPanel createControlsPanel ()
	{
		//input for the toPower method
		this.topowertext = new JTextField (3);
		JLabel powlabel = new JLabel ("Power: ");

		//create the buttons
		JButton badd = new JButton ("Add");
		JButton bsub = new JButton ("Subtract");
		JButton bdiv = new JButton ("Divide");
		JButton bmul = new JButton ("Multiply");
		JButton bder1 = new JButton ("Derivate 1");
		JButton bder2 = new JButton ("Derivate 2");
		JButton bpow1 = new JButton ("Power 1");
		JButton bpow2 = new JButton ("Power 2");
		JButton bgraph1 = new JButton ("Graph 1");
		JButton bgraph2 = new JButton ("Graph 2");

		//add the listeners
		badd.addActionListener (new PolynomialAdder ());
		bsub.addActionListener (new PolynomialSubtracter ());
		bdiv.addActionListener (new PolynomialDivider ());
		bmul.addActionListener (new PolynomialMultiplier ());
		bder1.addActionListener (new PolynomialDerivator (1));
		bder2.addActionListener (new PolynomialDerivator (2));
		bpow1.addActionListener (new PolynomialPowerRaiser (1));
		bpow2.addActionListener (new PolynomialPowerRaiser (2));
		bgraph1.addActionListener (new PolynomialGraphDisplay (1));
		bgraph2.addActionListener (new PolynomialGraphDisplay (2));

		//create the panel and add its border
		JPanel panel = new JPanel ();
		panel.setLayout (null);
		panel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), "Operations", TitledBorder.CENTER, TitledBorder.TOP));

		//next, add all the elements
		
		powlabel.setBounds (10, 30, 60, 20);
		panel.add (powlabel);

		this.topowertext.setBounds (60, 20, 30, 30);
		panel.add (this.topowertext);

		bpow1.setBounds (100, 20, 85, 40);
		panel.add (bpow1);

		bpow2.setBounds (195, 20, 85, 40);
		panel.add (bpow2);

		badd.setBounds (10, 75, 90, 40);
		panel.add (badd);

		bsub.setBounds (10, 125, 90, 40);
		panel.add (bsub);

		bmul.setBounds (10, 175, 90, 40);
		panel.add (bmul);

		bdiv.setBounds (10, 225, 90, 40);
		panel.add (bdiv);

		bder1.setBounds (190, 75, 100, 40);
		panel.add (bder1);

		bder2.setBounds (190, 125, 100, 40);
		panel.add (bder2);
		
		bgraph1.setBounds (190, 175, 100, 40);
		panel.add (bgraph1);
		
		bgraph2.setBounds (190, 225, 100, 40);
		panel.add (bgraph2);

		//apply the same font to all the elements in this panel
		GUIUtilities.applyFont (panel);
		
		return panel;
	}

	//creates the polynomials panel. used to visualize the current polynomials
	private JPanel createPolynomialsPanel ()
	{
		this.pol1text = new JTextArea ();
		this.pol2text = new JTextArea ();

		//text-area for the first polynomial.
		//it's not editable
		this.pol1text.setEditable (false);
		this.pol1text.setLineWrap (true);
		this.pol1text.setWrapStyleWord (true);
		this.pol1text.setText (this.x.toString ());

		//wrap the text-area in a scroll-pane. (big/long polynomials are accepted)
		this.jsp1 = new JScrollPane (this.pol1text);

		//create the second text-area
		//also not editable
		this.pol2text.setEditable (false);
		this.pol2text.setLineWrap (true);
		this.pol2text.setWrapStyleWord (true);
		this.pol2text.setText (this.y.toString ());

		//wrap it in a scroll-pane
		this.jsp2 = new JScrollPane (this.pol2text);

		JLabel pol1name = new JLabel ("Polynomial 1: ");
		JLabel pol2name = new JLabel ("Polynomial 2: ");

		//create the panel and add its border
		JPanel panel = new JPanel ();
		panel.setLayout (null);
		panel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), "Polynomials", TitledBorder.CENTER, TitledBorder.TOP));

		//next, add all the elements
		
		pol1name.setBounds (10, 20, 80, 20);
		panel.add (pol1name);

		this.jsp1.setBounds (100, 20, 475, 90);
		panel.add (this.jsp1);

		pol2name.setBounds (10, 120, 80, 20);
		panel.add (pol2name);

		this.jsp2.setBounds (100, 120, 475, 90);
		panel.add (this.jsp2);

		//make everything in this panel the same font
		GUIUtilities.applyFont (panel);
		
		return panel;
	}

	//creates the results panel. It's just a text-area...
	private JPanel createResultsPanel ()
	{
		//create the text-area.
		//not editable of course...
		this.txta = new JTextArea ();
		this.txta.setEditable (false);
		this.txta.setLineWrap (true);
		this.txta.setWrapStyleWord (true);
		
		//wrap it in a scroll-pane
		this.jspresults = new JScrollPane (this.txta);

		//create the panel and add its border
		JPanel panel = new JPanel ();
		panel.setLayout (null);
		panel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), "Results", TitledBorder.CENTER, TitledBorder.TOP));
		
		//add the text-area
		this.jspresults.setBounds (10, 20, 565, 250);
		panel.add (this.jspresults);

		//make it the same font
		GUIUtilities.applyFont (panel);
		
		return panel;
	}
	
	//listener for the Graph buttons. will display a dialog containing a visual graph representation
	//of the polynomial(s)
	private class PolynomialGraphDisplay implements ActionListener, Runnable
	{
		private int whichpolynomial;

		PolynomialGraphDisplay (int polynomial)
		{
			//validate parameter
			if (polynomial != 1 && polynomial != 2)
			{
				throw new IllegalArgumentException ("invalid polynomial specification");
			}

			this.whichpolynomial = polynomial;
		}
		
		@Override public void actionPerformed (ActionEvent e)
		{
			Thread worker = new Thread (this);
			worker.start ();
		}
		
		@Override public void run ()
		{
			//decide which title to give to the graph
			String charttitle = (this.whichpolynomial == 1) ? "Polynomial 1 Graph" : "Polynomial 2 Graph";

			//create a new series of data (x, y) pairs
			XYSeries series = new XYSeries ("P(X) = Y");

			//add (x, y) pairs to the series depending on which polynomial was selected
			if (this.whichpolynomial == 1)
			{
				for (int i = -100; i <= 100; i++)
				{
					series.add (i, x.getPolynomialValue (i));
				}
			}
			else
			{
				for (int i = -100; i <= 100; i++)
				{
					series.add (i, y.getPolynomialValue (i));
				}
			}

			//create the chart
			JFreeChart chart = ChartFactory.createXYLineChart
											(charttitle,						//chart title
											"X",								//label for X axis
											"Polynomial Value",					//label for Y axis
											new XYSeriesCollection (series),	//dataset (the actual graph)
											PlotOrientation.VERTICAL,			//orientation
											true,								//legend
											true,								//tooltips
											true);								//URLs
			
			//create the panel containing the chart
			final ChartPanel panel = new ChartPanel (chart);
			
			//from this point on, most instructions concern Swing objects.
			//therefore, those calls should happen on the EDT (event dispatch thread)
			SwingUtilities.invokeLater (new Runnable ()
			{
				public void run ()
				{
					//the size of the dialog
					final int DWIDTH = 850;
					final int DHEIGHT = 620;
			
					//create the dialog
					final JDialog graphdiag = new JDialog (mainframe, "Polynomial Graph", false);
					graphdiag.setLayout(null);
					graphdiag.setSize (DWIDTH, DHEIGHT);
					graphdiag.setResizable (false);
					graphdiag.setDefaultCloseOperation (JDialog.DISPOSE_ON_CLOSE);
			
					//add the chart panel to the dialog
					panel.setBounds (0, 0, DWIDTH, DHEIGHT - 110);
					graphdiag.add (panel);

					//close button. create, position and add.
					JButton diagclose = new JButton ("Close");
					diagclose.setBounds ((DWIDTH - 70) / 2, DHEIGHT - 90, 70, 40);
					graphdiag.add (diagclose);

					//when the close button is pressed, the dialog is disposed
					diagclose.addActionListener (new ActionListener ()
					{
						@Override public void actionPerformed (ActionEvent a)
						{
							graphdiag.dispose ();
						}
					});

					//everything in this panel must be the same font as everything else
					GUIUtilities.applyFont (panel);
					GUIUtilities.applyComponentFont (diagclose);

					//resolution of the screen needed
					int[] rez = GUIUtilities.getResolution ();
	
					//calculate coordinates
					int xlocation = (rez[0] - DWIDTH) / 2;
					int ylocation = (rez[1] - DHEIGHT) / 2;

					//position the dialog on the middle of the screen and display it
					graphdiag.setLocation (xlocation, ylocation);
					graphdiag.setVisible (true);
				}
			});
		}
	}
	
	//listener for the swap polynomials button. it will do what it's name says...
	private class PolynomialSwapper implements ActionListener
	{
		@Override public void actionPerformed (ActionEvent a)
		{
			Polynomial aux = x;
			x = y;
			y = aux;

			polynoper.swap ();

			pol1text.setText (x.toString ());
			pol2text.setText (y.toString ());
		}
	}
	
	//listener for the reset polynomial button. will set all coefficients to 0
	private class PolynomialResetter implements ActionListener
	{
		@Override public void actionPerformed (ActionEvent a)
		{
			if (polone.isSelected ())
			{
				x.reset ();
				pol1text.setText (x.toString ());
			}
			else if (poltwo.isSelected ())
			{
				y.reset ();
				pol2text.setText (y.toString ());
			}
		}
	}
	
	//This class is used for adding the 2 polynomials. Should be added as an ActionListener to the
	//corresponding button on the GUI.
	private class PolynomialAdder implements ActionListener, Runnable
	{
		//when the button is pressed, a "worker" thread is created.
		//that thread will perform the actual addition
		@Override public void actionPerformed (ActionEvent a)
		{
			Thread worker = new Thread (this);
			worker.start ();
		}

		//this method will be called in a dedicated thread
		//it will perform the addition
		@Override public void run ()
		{
			//generate result of addition
			final String r = "Sum: " + polynoper.add ().toString () + "\n";

			//append the result, but do it on the EDT
			SwingUtilities.invokeLater (new Runnable ()
			{
				@Override public void run ()
				{
					txta.append (r);
				}
			});
		}
	}
	
	//This class is used for adding the 2 polynomials. Should be added as an ActionListener to the
	//corresponding button on the GUI.
	private class PolynomialSubtracter implements ActionListener, Runnable
	{
		//when the button is pressed, a "worker" thread is created.
		//that thread will perform the actual subtraction
		@Override public void actionPerformed (ActionEvent a)
		{
			Thread worker = new Thread (this);
			worker.start ();
		}

		//this method will be called in a dedicated thread
		//it will perform the subtraction
		@Override public void run ()
		{
			//generate result of addition
			final String r = "Difference: " + polynoper.subtract ().toString () + "\n";

			//append the result, but do it on the EDT
			SwingUtilities.invokeLater (new Runnable ()
			{
				@Override public void run ()
				{
					txta.append (r);
				}
			});
		}
	}
	
	//this class is used for subtracting the 2 polynomials. Should be added as an ActionListener to the
	//corresponding button on the GUI.
	private class PolynomialDerivator implements ActionListener, Runnable
	{
		//specifies which polynomial should be derivated when called (1st or 2nd)
		private int whichpolynomial;

		//Creates a PolynomialSubtracter object.
		//parameter:  polynomial the polynomial which to derivate. Accepted values are 1 or 2.
		PolynomialDerivator (int polynomial)
		{
			//validate parameter
			if (polynomial != 1 && polynomial != 2)
			{
				throw new IllegalArgumentException ("invalid polynomial specification");
			}

			this.whichpolynomial = polynomial;
		}
		
		//when the button is pressed, a "worker" thread is created.
		//that thread will perform the actual derivation
		@Override public void actionPerformed (ActionEvent a)
		{
			Thread worker = new Thread (this);
			worker.start ();
		}

		//this method will be called in a dedicated thread
		//it will perform the derivation
		@Override public void run ()
		{
			//generate result of derivation
			Polynomial rez = polynoper.derivate (this.whichpolynomial);
			
			final String r = "Derivation of polynomial " + this.whichpolynomial + ": " + rez.toString () + "\n";

			//append the result, but do it on the EDT
			SwingUtilities.invokeLater (new Runnable ()
			{
				@Override public void run ()
				{
					txta.append (r);
				}
			});
		}
	}
	
	//this class is used for multiplying the 2 polynomials. Should be added as an ActionListener to the
	//corresponding button on the GUI.
	private class PolynomialMultiplier implements ActionListener, Runnable
	{
		//when the button is pressed, a "worker" thread is created.
		//that thread will perform the actual multiplication
		@Override public void actionPerformed (ActionEvent a)
		{
			Thread worker = new Thread (this);
			worker.start ();
		}

		//this method will be called in a dedicated thread
		//it will perform the multiplication
		@Override public void run ()
		{
			//generate result of multiplication
			final String r = "Product: " + polynoper.multiply ().toString () + "\n";

			//append the result, but do it on the EDT
			SwingUtilities.invokeLater (new Runnable ()
			{
				@Override public void run ()
				{
					txta.append (r);
				}
			});
		}
	}

	//this class is used for dividing the 2 polynomials. Should be added as an ActionListener to the
	//corresponding button on the GUI.
	private class PolynomialDivider implements ActionListener, Runnable
	{
		//when the button is pressed, a "worker" thread is created.
		//that thread will perform the actual division
		@Override public void actionPerformed (ActionEvent a)
		{
			Thread worker = new Thread (this);
			worker.start ();
		}

		//this method will be called in a dedicated thread
		//it will perform the division
		@Override public void run ()
		{
			//generate result of division
			Polynomial[] rez = polynoper.divide ();
			
			final String r = "Division Result: " +
							rez[0].toString () + "\n" +
							"Division Rest: " +
							rez[1].toString () + "\n";

			//append the result, but do it on the EDT
			SwingUtilities.invokeLater (new Runnable ()
			{
				@Override public void run ()
				{
					txta.append (r);
				}
			});
		}
	}
	
	//listener for the value adder button
	private class PolynomialValueAdder implements ActionListener
	{
		@Override public void actionPerformed (ActionEvent a)
		{
			//if no input, nothing to do...
			if (magninput.getText ().isEmpty () || coeffinput.getText ().isEmpty ())
			{
				return;
			}
			
			//get input
			int d, f;
			try
			{
				d = Integer.parseInt (magninput.getText ());
				f = Integer.parseInt (coeffinput.getText ());
			}
			catch (NumberFormatException e)
			{
				GUIUtilities.showErrorDialog (mainframe, "Invalid character typed.", "Input Error");

				return;
			}

			//if not in the required range, boohoooo
			if (d < 0 || d > 999)
			{
				GUIUtilities.showErrorDialog (mainframe, "Magnitude is out of range ([0; 999]).", "Input Dialog");

				return;
			}
	
			//check to see on which polynomial the value should be added/updated
			//and then do it
			if (polone.isSelected ())
			{
				x.setElement (d, (double) f);
				pol1text.setText (x.toString ());
			}
			else if (poltwo.isSelected ())
			{
				y.setElement (d, (double) f);
				pol2text.setText (y.toString ());
			}
		}
	}
	
	//listener for the Power Button(s)
	private class PolynomialPowerRaiser implements ActionListener
	{
		private int whichpolynomial;

		PolynomialPowerRaiser (int polynomial)
		{
			if (polynomial != 1 && polynomial != 2)
			{
				throw new IllegalArgumentException ("invalid polynomial specification");
			}

			this.whichpolynomial = polynomial;
		}

		@Override public void actionPerformed (ActionEvent a)
		{
			if (topowertext.getText ().isEmpty ())
			{
				return;
			}

			int d;
			try
			{
				d = Integer.parseInt (topowertext.getText ());
			}
			catch (NumberFormatException e)
			{
				GUIUtilities.showErrorDialog (mainframe, "Invalid character typed into power field.", "Input Error");

				return;
			}

			if (d <= 0)
			{
				GUIUtilities.showErrorDialog (mainframe, "Negative power is not accepted", "Input Error");

				return;
			}

			if (! polynoper.isPowerAllowed (whichpolynomial, d))
			{
				GUIUtilities.showErrorDialog (mainframe, "Power is too high.", "Input Error");

				return;
			}

			String r = "The polynomial " + this.whichpolynomial + " raised to power: " + Integer.toString (d) + ": ";
			Polynomial rez = polynoper.toPower (this.whichpolynomial, d);
			r += rez.toString ();
			r += "\n";

			txta.append (r);
		}
	}
	
	//listener for the Remove Value button
	private class PolynomialValueRemover implements ActionListener
	{	
		@Override public void actionPerformed (ActionEvent a)
		{
			if (magninput.getText ().isEmpty ())
			{
				return;
			}

			int d;
			try
			{
				d = Integer.parseInt (magninput.getText ());
			}
			catch (NumberFormatException e)
			{
				GUIUtilities.showErrorDialog (mainframe, "Invalid character in magnitude field", "Input Error");

				return;
			}

			if (d < 0 || d > 999)
			{
				GUIUtilities.showErrorDialog (mainframe, "Magnitude is out of range ([0; 999]).", "Input Error");

				return;
			}

			//check to see on which polynomial the value should be added/updated
			//and then do it
			if (polone.isSelected ())
			{
				x.setElement (d, (double) 0);
				pol1text.setText (x.toString ());
			}
			else if (poltwo.isSelected ())
			{
				y.setElement (d, (double) 0);
				pol2text.setText (y.toString ());
			}
		}
	}
}