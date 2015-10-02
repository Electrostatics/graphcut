package filetools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

import mathtools.MapleTools;

public class ReadXMTOutput {

	private static String k;
	private static int numFilesPerSet;
	private static WriteFile wr;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String inFileStart = "C:\\Users\\hoga886\\Documents\\_Projects\\GRADIENT\\GRAD_workspace\\graphs\\cougarxmtOutput\\ER100_0.5new\\s\\ER_100v_0.5p10path_k10";
		String aFileStart = "C:\\Users\\hoga886\\Documents\\_Projects\\GRADIENT\\GRAD_workspace\\graphs\\cougarxmtOutput\\ER100_0.5new\\ER_100v_0.5p10path_k10_a_";
		String typeFile = "s";
		String date = "4-22-13";
		String outFile = "C:\\Users\\hoga886\\Documents\\_Projects\\GRADIENT\\GRAD_workspace\\graphs\\cougarxmtOutput\\ER100_0.5new\\time_accuracy_ER100_0.5"+typeFile+".csv";
		k = "10";
		numFilesPerSet = 5;
		
		int numFileSets = 7;
		
		wr = new WriteFile(outFile);
		
		readAndPrintASet(aFileStart+date);
		wr.writeln();
		
		for (int i=0; i< numFileSets; i++){
			int powerOfTwo = (int) Math.pow(2,i);
			String fileStart = inFileStart+"_n"+powerOfTwo+"_"+typeFile+"_"+date;
			readAndPrintOneSet(fileStart,powerOfTwo);
			wr.writeln();
		}
		
		wr.close();		

	}
	
	private static void readAndPrintASet(String filenameStart){
		double[] minorsTimes = new double[numFilesPerSet];
		double[] sumPowersTimes = new double[numFilesPerSet];
		double[] uStarTimes = new double[numFilesPerSet];
		double[] uListSizes = new double[numFilesPerSet];
		double[] uTimes = new double[numFilesPerSet];
		double[] numPathss = new double[numFilesPerSet];
		double[] bfsTimes = new double[numFilesPerSet];
	
		for (int i=1; i<=numFilesPerSet; i++){
			String filename = filenameStart+"_"+i+".out";
//			System.out.println(filename);
			double[] info = scanOneAFile(filename);
			minorsTimes[i-1] = info[0];
			sumPowersTimes[i-1] = info[1];
			uStarTimes[i-1] = info[2];
			uListSizes[i-1] = info[3];
			uTimes[i-1] = info[4];
			numPathss[i-1] = info[5];
			bfsTimes[i-1] = info[6];
		}
		
		double totalAverageTime = 0.0;
		
		// Print header
		wr.write("n=0, ");
		for (int i=1; i<=numFilesPerSet; i++){
			wr.write(i+", ");
		}
		wr.writeln("avg");
		
		// Print U list sizes
		wr.write("U list size, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(uListSizes[i]+", ");
		}
		wr.writeln(MapleTools.avg(uListSizes));
		
		// Print # paths
		wr.write("# paths, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(numPathss[i]+", ");
		}
		wr.writeln(MapleTools.avg(numPathss));
		
		// Print time for minors
		wr.write("minors time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(minorsTimes[i]+", ");
		}
		double avgMinorsTime = MapleTools.avg(minorsTimes);
		totalAverageTime += avgMinorsTime;
		wr.writeln(avgMinorsTime);

		// Print time for sum of powers
		wr.write("sum of powers time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(sumPowersTimes[i]+", ");
		}
		double avgSumPowersTime = MapleTools.avg(sumPowersTimes);
		totalAverageTime += avgSumPowersTime;
		wr.writeln(avgSumPowersTime);

		// Print time for U* list
		wr.write("U* list time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(uStarTimes[i]+", ");
		}
		double avgUStarTime = MapleTools.avg(uStarTimes);
		totalAverageTime += avgUStarTime;
		wr.writeln(avgUStarTime);

		// Print time for U list
		wr.write("U list time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(uTimes[i]+", ");
		}
		double avgUTime = MapleTools.avg(uTimes);
		totalAverageTime += avgUTime;
		wr.writeln(avgUTime);

		// Print time for BFS
		wr.write("BFS time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(bfsTimes[i]+", ");
		}
		double avgBfsTime = MapleTools.avg(bfsTimes);
		totalAverageTime += avgBfsTime;
		wr.writeln(avgBfsTime);

		// Print sum of average times
		wr.writeln(" , , , , , , "+totalAverageTime);
		
	}
	
	private static void readAndPrintOneSet(String filenameStart, int n){
		double[] minorsTimes = new double[numFilesPerSet];
		double[] sumPowersTimes = new double[numFilesPerSet];
		double[] uStarTimes = new double[numFilesPerSet];
		double[] uListSizes = new double[numFilesPerSet];
		double[] uTimes = new double[numFilesPerSet];
		double[] numPathss = new double[numFilesPerSet];
		double[] bfsTimes = new double[numFilesPerSet];
		
		for (int i=1; i<=numFilesPerSet; i++){
			String filename = filenameStart+"_"+i+".out";
//			System.out.println(filename);
			double[] info = scanOneFile(filename);
			minorsTimes[i-1] = info[0];
			sumPowersTimes[i-1] = info[1];
			uStarTimes[i-1] = info[2];
			uListSizes[i-1] = info[3];
			uTimes[i-1] = info[4];
			numPathss[i-1] = info[5];
			bfsTimes[i-1] = info[6];
		}
		
		double totalAverageTime = 0.0;
		
		// Print header
		wr.write("n="+n+", ");
		for (int i=1; i<=numFilesPerSet; i++){
			wr.write(i+", ");
		}
		wr.writeln("avg");
		
		// Print U list sizes
		wr.write("U list size, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(uListSizes[i]+", ");
		}
		wr.writeln(MapleTools.avg(uListSizes));
		
		// Print # paths
		wr.write("# paths, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(numPathss[i]+", ");
		}
		wr.writeln(MapleTools.avg(numPathss));
		
		// Print time for minors
		wr.write("minors time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(minorsTimes[i]+", ");
		}
		double avgMinorsTime = MapleTools.avg(minorsTimes);
		totalAverageTime += avgMinorsTime;
		wr.writeln(avgMinorsTime);

		// Print time for sum of powers
		wr.write("sum of powers time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(sumPowersTimes[i]+", ");
		}
		double avgSumPowersTime = MapleTools.avg(sumPowersTimes);
		totalAverageTime += avgSumPowersTime;
		wr.writeln(avgSumPowersTime);

		// Print time for U* list
		wr.write("U* list time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(uStarTimes[i]+", ");
		}
		double avgUStarTime = MapleTools.avg(uStarTimes);
		totalAverageTime += avgUStarTime;
		wr.writeln(avgUStarTime);

		// Print time for U list
		wr.write("U list time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(uTimes[i]+", ");
		}
		double avgUTime = MapleTools.avg(uTimes);
		totalAverageTime += avgUTime;
		wr.writeln(avgUTime);

		// Print time for BFS
		wr.write("BFS time, ");
		for (int i=0; i<numFilesPerSet; i++){
			wr.write(bfsTimes[i]+", ");
		}
		double avgBfsTime = MapleTools.avg(bfsTimes);
		totalAverageTime += avgBfsTime;
		wr.writeln(avgBfsTime);

		// Print sum of average times
		wr.writeln(" , , , , , , "+totalAverageTime);
		
	}
	
	private static double[] scanOneAFile(String filename){

		File in = new File(filename);
		
		double uListSize = 0.0;
		double sumPowersTime = 0.0;
		double uTime = 0.0;
		double numPaths = 0.0;
		double bfsTime = 0.0;
		
		Scanner scn = null;
		try{
			scn = new Scanner(new FileReader(in));
		} catch(FileNotFoundException fnfe){
			System.err.println("Could not find file "+filename);
			System.exit(0);
		}
		
		while (scn.hasNextLine()){
			String line = scn.nextLine();
			if (line.startsWith("Time to compute sum of the matrix power of order")){
				sumPowersTime = Double.parseDouble(line.substring(52+k.length(),60+k.length()));
//				System.out.println(sumPowersTime);
			} else if (line.startsWith("Time to generate U list")){
				uTime = Double.parseDouble(line.substring(26,34));
//				System.out.println(uTime);
			}  else if (line.startsWith("total number of paths")){
				numPaths = Double.parseDouble(line.substring(24));
//				System.out.println(numPaths);
			} else if (line.startsWith("Time to do the BFS")){
				String timeAnd = line.substring(22);
				bfsTime = Double.parseDouble(timeAnd.substring(0, timeAnd.length()-5));
//				System.out.println(bfsTime);
			} else if (line.startsWith("U list has")){
				String sizeAnd = line.substring(11);
				uListSize = Double.parseDouble(sizeAnd.substring(0,sizeAnd.length()-9));
//				System.out.println(uListSize);
			}
			
		}

		scn.close();
		
		double[] info = {0.0, sumPowersTime, 0.0, uListSize, uTime, numPaths, bfsTime};
		
		return info;
	}

	private static double[] scanOneFile(String filename){
		
		File in = new File(filename);
		
		double minorsTime = 0.0;
		double sumPowersTime = 0.0;
		double uStarTime = 0.0;
		double uListSize = 0.0;
		double uTime = 0.0;
		double numPaths = 0.0;
		double bfsTime = 0.0;
		
		Scanner scn = null;
		try{
			scn = new Scanner(new FileReader(in));
		} catch(FileNotFoundException fnfe){
			System.err.println("Could not find file "+filename);
			System.exit(0);
		}
		
		while (scn.hasNextLine()){
			String line = scn.nextLine();
			if (line.startsWith("!!! Total time to do all the graph minors = ")){
				minorsTime = Double.parseDouble(line.substring(44,52));
//				System.out.println(minorsTime);
			} else if (line.startsWith("Time to compute sum of the matrix power of order")){
				sumPowersTime = Double.parseDouble(line.substring(52+k.length(),60+k.length()));
//				System.out.println(sumPowersTime);
			} else if (line.startsWith("Time to generate U")){
				uStarTime = Double.parseDouble(line.substring(22,30));
//				System.out.println(uStarTime);
			} else if (line.startsWith("the expanded U list has")){
				String sizeAnd = line.substring(24);
				uListSize = Double.parseDouble(sizeAnd.substring(0,sizeAnd.length()-9));
//				System.out.println(uListSize);
			} else if (line.startsWith("Time to expand the U list")){
				uTime = Double.parseDouble(line.substring(28,36));
//				System.out.println(uTime);
			} else if (line.startsWith("total number of paths")){
				numPaths = Double.parseDouble(line.substring(24));
//				System.out.println(numPaths);
			} else if (line.startsWith("Time to do the BFS")){
				String timeAnd = line.substring(22);
				bfsTime = Double.parseDouble(timeAnd.substring(0, timeAnd.length()-5));
//				System.out.println(bfsTime);
			}
		}
		
		double[] info = {minorsTime, sumPowersTime, uStarTime, uListSize, uTime, numPaths, bfsTime};
		
		scn.close();
		return info;
		
	}
	
}
