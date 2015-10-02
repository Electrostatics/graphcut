package filetools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ScanFile {
	private File f;
	private String delims;
	//private File outFile;
	private WriteFile wr;
	
	public ScanFile(String fileIn, String fileOut, String delims){
		f = new File(fileIn);
		this.delims = delims;
		//outFile = new File(fileOut);
		wr = new WriteFile(fileOut);
		wr.write("AQIM_PO := {");
		
	}
	
	public final void processLineByLine() throws FileNotFoundException{
		
		Scanner scn = new Scanner(new FileReader(f));
		scn.useDelimiter(delims);
		
		try{
			//while there are more lines
			while (scn.hasNextLine()){
				String firstLine = scn.nextLine();
				String flTrimmed = firstLine.trim();
				//if the line line begins with "<owl:Class"
				if (flTrimmed.startsWith("<owl:Class")){
					//thing1 = rest of the line
					String thing1 = pullEnd1(flTrimmed.substring(10));
					String thing2 = "FAIL";
					//look at the next line
					if (scn.hasNextLine()){
						String nextLine = scn.nextLine();
						String nlTrimmed = nextLine.trim();
						//if it is "<rdfs:subClassOf>"
						if (nlTrimmed.equals("<rdfs:subClassOf>")){
							//look at the next line
							String nxtNxtLine = scn.nextLine();
							String nnlTrimmed = nxtNxtLine.trim();
							//if it starts with "<owl:Class"
							if (nnlTrimmed.startsWith("<owl:Class")){
								//thing2 = rest of the line
								thing2 = pullEnd2(nnlTrimmed.substring(10));
								//look at the next line
								String nxtNxtNxtLine = scn.nextLine();
								String nnnlTrimmed = nxtNxtNxtLine.trim();
								//if it is "</rdfs:subClassOf>"
								if (nnnlTrimmed.equals("</rdfs:subClassOf>")){
									//look at the next line
									String nxtNxtNxtNxtLine = scn.nextLine();
									String nnnnlTrimmed = nxtNxtNxtNxtLine.trim();
									if (nnnnlTrimmed.equals("</owl:Class>")){
										//if it is "</owl:Class>"
										//get out of the main loop
										//continue;
									}
								}
							}
							wr.writeln("[\""+thing1+"\", \""+thing2+"\"], ");
						}
						//if it starts with "<rdfs:subClassOf"
						else if (nlTrimmed.startsWith("<rdfs:subClassOf")){
							//thing2 = rest of the line except for "/>"
							thing2 = pullEnd2(nlTrimmed.substring(16));
							wr.writeln("[\""+thing1+"\", \""+thing2+"\"], ");
						}
						else{
							while (scn.hasNextLine()){
								String theNextLine = scn.nextLine();
								String tnlTrimmed = theNextLine.trim();
								if (tnlTrimmed.equals("</owl:Class>")){
									break;
								}
							}
						}
						
					}
				}
			
			}
			wr.write("}:");
			wr.close();
				
		
		}finally {
			scn.close();
		}
		
	}

	private String pullEnd1(String uri){
		
		String uriTrim = uri.trim();
		if (uriTrim.startsWith("rdf:about=\"http://www.battelle.org/TAI/")){
			String uriTrimEnd = uriTrim.substring(39);
			String done = uriTrimEnd.substring(0, uriTrimEnd.length()-2);
			return done;
		}else if(uriTrim.startsWith("rdf:resource=\"http://www.battelle.org/TAI/")){
			String uriTrimEnd = uriTrim.substring(42);
			String done = uriTrimEnd.substring(0, uriTrimEnd.length()-2);
			return done;
		}else{
			return "FAIL";
		}
	}
	
private String pullEnd2(String uri){
		
		String uriTrim = uri.trim();
		if (uriTrim.startsWith("rdf:about=\"http://www.battelle.org/TAI/")){
			String uriTrimEnd = uriTrim.substring(39);
			String done = uriTrimEnd.substring(0, uriTrimEnd.length()-3);
			return done;
		}else if(uriTrim.startsWith("rdf:resource=\"http://www.battelle.org/TAI/")){
			String uriTrimEnd = uriTrim.substring(42);
			String done = uriTrimEnd.substring(0, uriTrimEnd.length()-3);
			return done;
		}else{
			return "FAIL";
		}
	}
	
}
