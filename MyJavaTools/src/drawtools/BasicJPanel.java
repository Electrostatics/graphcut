package drawtools;

import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BasicJPanel extends JPanel{

	public BasicJPanel(){
		// All we do is call JFrame's constructor.
		// We don't need anything special for this
		// program.
		super();
	}

	/* Create a paint() method to override the one in JFrame.
    This is where the drawing happens. 
    We don't have to call it in our program, it gets called
    automatically whenever the frame needs to be redrawn,
    like when it it made visible or moved or whatever.*/
	public void paintComponent(Graphics g){
		g.drawLine(10,10,150,150); // Draw a line from (10,10) to (150,150)
	}

	public static void main(String arg[]){
		JFrame frame = new JFrame("BasicJPanel");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(200,200);
	    
	    // Create a new identifier for a BasicJPanel called "panel",
	    // then create a new BasicJPanel object for it to refer to.
	    BasicJPanel panel = new BasicJPanel();

	    // Make the panel object the content pane of the JFrame.
	    // This puts it into the drawable area of frame, and now
	    // we do all our drawing to panel, using paintComponent(), above.
	    frame.setContentPane(panel);
	    frame.setVisible(true);
	}
	
}
