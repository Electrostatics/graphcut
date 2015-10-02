package filetools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The skeleton for this class was taken from 
 * http://www.javadb.com/write-to-file-using-bufferedwriter
 **/
public class WriteFile {
	
	private File f;
	private BufferedWriter writer;
	
	public WriteFile(File file, boolean append){
		f = file;
		
		try {

			//Construct the BufferedWriter object
			writer = new BufferedWriter(new FileWriter(f,append));

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public WriteFile(String file, boolean append){
		f = new File(file);
		
		try {

			//Construct the BufferedWriter object
			writer = new BufferedWriter(new FileWriter(f,append));

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public WriteFile(String file){
		f = new File(file);
		
		try{
			//Construct the BufferedWriter object
			writer = new BufferedWriter(new FileWriter(f));
		}catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Writes the string to the file without adding a newline after writing s.
	 * @param s A String to write to the file
	 */
	public void write(String s){
		try {
			writer.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Changes the double to a String and calls the write(String) method
	 * @param d A double to write to the file
	 */
	public void write(double d){
		write(Double.toString(d));
	}
	
	/**
	 * Changes the int to a String and calls the write(String) method
	 * @param i An int to write to the file
	 */
	public void write(int i){
		write(Integer.toString(i));
	}
	
	/**
	 * Writes the string "line" to the file, then adds a new line.
	 * @param line A String to write to the file
	 */
	public void writeln(String line){
		try {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Changes the double to a string and calls the writeln(String) method
	 * @param line A double to write to the file
	 */
	public void writeln(double line){
		writeln(Double.toString(line));
	}
	
	/**
	 * Changes the int to a string and calls the writeln(String) method
	 * @param line An int to write to the file
	 */
	public void writeln(int line){
		writeln(Integer.toString(line));
	}
	
	/**
	 * Writes a newline character
	 */
	public void writeln() {
		try {
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Closes the BufferedWriter. You must call close() at the end or the file will not be written.
	 */
	public void close(){
		try {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	

}
