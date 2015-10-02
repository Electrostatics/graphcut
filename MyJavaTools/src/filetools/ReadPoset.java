package filetools;

import hashtools.Key;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import mathtools.Order_old;
import mathtools.Order;
import mathtools.Poset_old2;

public class ReadPoset {
	
	private String filename;
	private Order<String> P;
	
	public ReadPoset(String fname){
		filename = fname;
		P = new Order<String>();
	}
	
	public Order<String> getPoset(){
		return P;
	}
	
	public void readPoset() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		String line = "";
		while ((line = in.readLine()) != null) {
			line = line.trim();
			String[] relation = line.split(", ");
			P.addRelation(new Key<String, String>(relation[0], relation[1]));
		}
		P.makeElements(P.getTrans());
		P.makeTrans(P.getTrans());
	}

}
