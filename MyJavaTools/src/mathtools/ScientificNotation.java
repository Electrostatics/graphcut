package mathtools;

import java.util.Scanner;

public class ScientificNotation {
	
	private static final String plus = "+";
	private static final String minus = "-";
	private static final char pl = plus.charAt(0);
	private static final char mi = minus.charAt(0);
	
	public static double parseSci(String sci){
		
		Scanner scn = new Scanner(sci);
		scn.useDelimiter("[e]");
		double first = scn.nextDouble(); 				//System.out.println("first="+first);
		String end = scn.next(); 						//System.out.println("end="+end);
		char sign = end.charAt(0); 						//System.out.println("sign="+sign);
		int exp = Integer.parseInt(end.substring(1)); 	//System.out.println("exp="+exp);
		
		double ret = 0;
		if (sign == pl){
			ret = first*Math.pow(10,exp);
		}else if (sign == mi){
			ret = first*Math.pow(10, -exp);
		}
		
		return ret;
		
	}
	
}
