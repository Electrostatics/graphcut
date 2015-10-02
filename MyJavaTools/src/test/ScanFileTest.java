package test;

import java.io.FileNotFoundException;

import filetools.ScanFile;

public class ScanFileTest {

	public static void main(String[] args){
		ScanFile scnf = new ScanFile(
				"C:\\Users\\hoga886\\Documents\\_Projects\\Cliff\\AAA\\aqim\\TAI-ontology.2.3.1.small.owl",
				"C:\\Users\\hoga886\\Documents\\_Projects\\Cliff\\AAA\\aqim\\pairs.out", "[ ]");
		try{
			scnf.processLineByLine();
		}catch(FileNotFoundException fnfe){
			System.out.println("exception!");
			System.exit(0);
		}
	}
}
