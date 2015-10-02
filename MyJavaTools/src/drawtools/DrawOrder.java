package drawtools;

import filetools.ReadPoset;
import hashtools.Key;

import java.awt.Graphics;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mathtools.Interval;
import mathtools.MapleTools;
import mathtools.Order;

public class DrawOrder {
	
	private Order<String> P;
	private HashMap<String, Integer> xCoordMap;
	private HashMap<String, Integer> yCoordMap;
	
	public DrawOrder(){
		P = null;
	}
	
	public DrawOrder(Order<String> P){
		this.P = P;
	}
	
	/**
	 * Sets the y coordinates for elements of the Order
	 */
	private void setYCoords(){
		
		yCoordMap = new HashMap<String, Integer>();
		for (String e : P.getElements()){
			Interval Rplus_e = P.stdIntRank(e);
			yCoordMap.put(e, (int) ((int) 50*Rplus_e.midpoint()+10));
		}
		
	}
	
	/**
	 * @return A HashMap which maps levels (y coordinates) to the
	 * elements which all have the same y coordinate.
	 */
	private HashMap<Integer, HashSet<String>> levels(){
		HashMap<Integer, HashSet<String>> L = new HashMap<Integer, HashSet<String>>();
		for (String e : yCoordMap.keySet()){
			Integer yCoord = yCoordMap.get(e);
			HashSet<String> thisLevel;
			if (L.containsKey(yCoord)){
				thisLevel = L.get(yCoord);
			} else {
				thisLevel = new HashSet<String>();
			}
			thisLevel.add(e);
			L.put(yCoord, thisLevel);
		}
		
		return L;
		
	}
	
	/**
	 * Set the x coordinates for the elements of the Order
	 */
	private void setXCoords(){
		xCoordMap = new HashMap<String, Integer>();
		
		HashMap<Integer, HashSet<String>> levels = levels();
		for (Integer l : levels.keySet()){
			HashSet<String> thisLevel = levels.get(l);
			int i=1;
			for (String s : thisLevel){
				xCoordMap.put(s, (int) (40*++i + ((int) 12*Math.random()-6)-20));
			}
		}
		
	}
	
	/**
	 * sets both X and Y coordinates (Y first)
	 */
	private void setCoords() {
		setYCoords();
		setXCoords();
	}
	
	/**
	 *  Creates a new object instance of a JPanel for the poset
	 *  and draws the poset according to the given x and y
	 *  coordinates
	 */
	public void drawPoset(){
		
		class PosetJPanel extends JPanel{
			PosetJPanel(){
				super();
			}
			
			protected void paintComponent(Graphics g){
				
//				g.drawOval(0, 0, 10, 10);
//				g.drawOval(275, 250, 10, 10);
				
				for (String e : P.getElements()){
					int x = xCoordMap.get(e);
					int y = yCoordMap.get(e);
					g.drawOval(x-2, y-2, 5, 5);
					g.drawString(e, x+4, y+4);
				}
				P.makeCovers();
				
				for (Key<String, String> cov : P.getCovers()){
					String bot = cov.getFirst();
					String top = cov.getSecond();
					int xBot = xCoordMap.get(bot); int yBot = yCoordMap.get(bot);
					int xTop = xCoordMap.get(top); int yTop = yCoordMap.get(top);
					g.drawLine(xBot, yBot, xTop, yTop);
				}
				
			}
			
		}
		
		Collection<Integer> xVals = xCoordMap.values();
		Collection<Integer> yVals = yCoordMap.values();
		
		Integer maxXCoord = MapleTools.max(xVals);
		Integer maxYCoord = MapleTools.max(yVals);
		
		JFrame frame = new JFrame("Poset");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(maxXCoord+50, maxYCoord+75);
	    
	    // Create a new identifier for a BasicJPanel called "panel",
	    // then create a new BasicJPanel object for it to refer to.
	    PosetJPanel panel = new PosetJPanel();

	    // Make the panel object the content pane of the JFrame.
	    // This puts it into the drawable area of frame, and now
	    // we do all our drawing to panel, using paintComponent(), above.
	    frame.setContentPane(panel);
	    frame.setVisible(true);
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// FIXME Auto-generated method stub

//		HashSet<Key<String, String>> relations = new HashSet<Key<String, String>>();
//		relations.add(new Key<String, String>("0", "1"));
//		relations.add(new Key<String, String>("0", "2"));
//		relations.add(new Key<String, String>("3", "4"));
//		relations.add(new Key<String, String>("2", "4"));
//		relations.add(new Key<String, String>("1", "5"));
//		relations.add(new Key<String, String>("5", "3"));
//		
//		Order<String> P = new Order<String>(relations);
	
		String filename = "C:\\Users\\hoga886\\Documents\\_Projects\\SINT\\HScode-poset-Java.txt";
		ReadPoset rp = new ReadPoset(filename);
		rp.readPoset();
		Order<String> P = rp.getPoset();
		
		
		DrawOrder test = new DrawOrder(P);
		test.setCoords();
				
		test.drawPoset();
		
	}

	

}
