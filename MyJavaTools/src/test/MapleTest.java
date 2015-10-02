package test;

import java.util.Arrays;

import mathtools.MapleTools;

public class MapleTest {

	
	public static void main(String[] args){
		
//		int[] first = {1,2,3,4,5};
//		int[] second = {1,2,6};
//		System.out.println(Arrays.toString(MapleTools.minus(first, second)));
		
		double start = 0.0;
		double end = 2.0;
		double step = 0.1;
		double[] vec = MapleTools.seq(start, end, step);
		System.out.println(Arrays.toString(vec));
		
	}
	
}
