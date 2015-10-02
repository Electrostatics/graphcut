package test;

import java.util.Arrays;

import mathtools.VectorsMax;

public class VectorsMaxTest {

	public static void main(String[] args){
		int[] testArray = {1,3,2};
		VectorsMax vm = new VectorsMax(testArray);
		
		while (vm.hasNext()){
			System.out.println(Arrays.toString(vm.next()));
		}
	}
	
}
