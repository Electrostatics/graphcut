package main;

import hashtools.Key;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import prot.MakeProtein;
import prot.Protein;
import prot.ProteinToSystm;
import syst.Instance;
import syst.Systm;
import syst.Variable;
import mathtools.MapleTools;
import filetools.WriteFile;

public class TitrationCurvesHIS {

	public static final double RT = 2479.0;
	public static final double kln10 = Math.log(10)*8.3144621;//*Math.log(10); // gas constant * ln(10)
	public static final double T = 300;
	public static final double modPkaHIP = 6.6;
	public static final double modPkaHIE = modPkaHIP;
	public static final double modPkaHID = modPkaHIP;

	public static void main(String[] args) throws FileNotFoundException{
		TitrationCurvesHIS tc = new TitrationCurvesHIS();
		tc.doIt(args, true);
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public HashMap<Variable, Vector<Key<Double, Double>>> doIt(String[] args, boolean write) throws FileNotFoundException {
	//public void doIt(String[] args, boolean write) throws FileNotFoundException {
		// FIXME Auto-generated method stub

//		For each pH and residue, find the minimum energy state
//		(and energy value) at the given pH. Then protonate/deprotonate
//		(change) the residue and measure the change in energy.
//		Take exp(-change/RT)/(1+exp(-change/RT)) and plot that as the y value for x=pH.
//		So it seems that there is a titration curve with pH on
//		the x axis and exp(-change) on the y axis for each residue.

		String in_path = args[0];
		String out_path = args[1];
		String proteinName = args[2];
		String outFile = out_path+"\\"+proteinName+".pka.out";
		WriteFile wr = new WriteFile(outFile);
		WriteFile wrPartOpt = new WriteFile(out_path+"\\"+proteinName+".partOpt.out", true);


		String pdb2pkaFile = in_path+"\\INTERACTION_MATRIX.DAT";
		String desolvFile = in_path+"\\desolvation_energies.txt";
		String backFile = in_path+"\\background_interaction_energies.txt";

		double[] pHs = MapleTools.seq(0.0, 20.0, 0.1);

		//get start state, when pH is at its minimum
		MakeProtein test = new MakeProtein(pdb2pkaFile, desolvFile, backFile, "[ ]+", proteinName, pHs[0]);
		test.readPDB2PKA();

		Protein testProt = test.getProt();
		testProt.simplify();
		ProteinToSystm makeSys = new ProteinToSystm(testProt);
		makeSys.makeSystm();
		Systm sys = makeSys.getSystm();

		Vector<Variable> residues = sys.getVars();
		HashMap<Variable, Vector<Key<Double, Double>>> titrCurves = new HashMap<Variable, Vector<Key<Double, Double>>>();
		HashMap<Variable, Vector<Key<Double, Double>>> titrCurves2 = new HashMap<Variable, Vector<Key<Double, Double>>>();
		HashSet<Variable> hisRes = new HashSet<Variable>();
		for (Variable r : residues) {
			Vector<Key<Double, Double>> init = new Vector<Key<Double, Double>>();
			Vector<Key<Double, Double>> init2 = new Vector<Key<Double, Double>>();
			titrCurves.put(r, init);
			titrCurves2.put(r, init2);
			if (r.isHIS()){
				hisRes.add(r);
			}
		}

		HashMap<Variable, Variable> hisCorr = new HashMap<Variable, Variable>();
		for (Variable h1 : hisRes){
			String hisLoc1 = h1.getName().substring(3);
			for (Variable h2 : hisRes){
				String hisLoc2 = h2.getName().substring(3);
				if (hisLoc1.equals(hisLoc2) && !h1.equals(h2)){
					hisCorr.put(h1, h2);
					hisCorr.put(h2, h1);
				}
			}
		}

		System.out.println(hisCorr.toString());

		BinaryGraphCut bgc;
		HashMap<Variable, Instance> currState;
		HashSet<Variable> seen;
		double aH = 0.0;

		for (int i=0; i<pHs.length; i++){
			wr.writeln("pH="+pHs[i]);

			aH = Math.pow(10, -pHs[i]);

			// reset the pH value
			testProt.setPH(pHs[i]);
			makeSys = new ProteinToSystm(testProt);
			makeSys.makeSystm();
			sys = makeSys.getSystm();

			// Do the graph cut and energy minimization
			bgc = new BinaryGraphCut(sys);
			bgc.makeBiGr(wr);
			currState = bgc.minimizeAns(wr, wrPartOpt);

			seen = new HashSet<Variable>();

			System.out.println("pH="+pHs[i]);
			// For each residue, set the titration value at this pH
			for (Variable r : residues){

				if (r.isHIS()){
					if (seen.contains(r)){
						continue;
					} else {
						Variable r2 = hisCorr.get(r);
						Variable rep;
						Variable HSE;
						Variable HSD;
						if (r.getName().substring(0, 3).equals("HIe")){
							rep = r;
							HSE = r;
							HSD = r2;
						} else {
							rep = r2;
							HSE = r2;
							HSD = r;
						}
						// get states of both r and r2
						Instance HSEcurr = currState.get(HSE);
						Instance HSDcurr = currState.get(HSD);
						@SuppressWarnings("unchecked")
						HashMap<Variable, Instance> newState = (HashMap<Variable, Instance>) currState.clone();
						Double HSPenergy = 0.0;
						Double HSEenergy = 0.0;
						Double HSDenergy = 0.0;
						if (HSEcurr == null || HSDcurr == null){
							HSPenergy = Double.MIN_VALUE;
							HSEenergy = Double.MIN_VALUE;
							HSDenergy = Double.MIN_VALUE;
						} else if (HSEcurr.getLabel().equals("DEPROT") && HSDcurr.getLabel().equals("PROT")){
							// residue is in state HSD
							HSDenergy = sys.evaluateEnergy(currState, true);

							Instance HSEprot = HSE.getInstance("PROT");
							newState.put(HSE, HSEprot);
							HSPenergy = sys.evaluateEnergy(newState, true);

							Instance HSDdeprot = HSD.getInstance("DEPROT");
							newState.put(HSD, HSDdeprot);
							HSEenergy = sys.evaluateEnergy(newState, true);
						} else if (HSEcurr.getLabel().equals("PROT") && HSDcurr.getLabel().equals("DEPROT")){
							// residue is in state HSE
							HSEenergy = sys.evaluateEnergy(currState, true);

							Instance HSDprot = HSD.getInstance("PROT");
							newState.put(HSD, HSDprot);
							HSPenergy = sys.evaluateEnergy(newState, true);

							Instance HSEdeprot = HSE.getInstance("DEPROT");
							newState.put(HSE, HSEdeprot);
							HSDenergy = sys.evaluateEnergy(newState, true);

						} else if (HSEcurr.getLabel().equals("PROT") && HSDcurr.getLabel().equals("PROT")){
							// residue is in state HSP
							HSPenergy = sys.evaluateEnergy(currState, true);

							Instance HSEdeprot = HSE.getInstance("DEPROT");
							Instance HSDdeprot = HSD.getInstance("DEPROT");
							newState.put(HSE, HSEdeprot);
							HSDenergy = sys.evaluateEnergy(newState, true);

							newState.put(HSE, HSEcurr);
							newState.put(HSD, HSDdeprot);
							HSEenergy = sys.evaluateEnergy(newState, true);

						}
						seen.add(r);
						seen.add(r2);

						Double titrValue;
						if (HSPenergy == Double.MIN_VALUE || HSDenergy == Double.MIN_VALUE || HSEenergy == Double.MIN_VALUE){
							titrValue = null;
						} else{
							HSPenergy = HSPenergy*kln10*T;
							HSEenergy = HSEenergy*kln10*T;
							HSDenergy = HSDenergy*kln10*T;
							double dgd = HSPenergy - HSDenergy;
							double dge = HSPenergy - HSEenergy;
							double dpkad = -Math.log10(Math.exp(dgd/(RT*8.3144621*300)));
							double dpkae = -Math.log10(Math.exp(dge/(RT*8.3144621*300)));
							double pkad = modPkaHIP+dpkad;
							double pkae = modPkaHIP+dpkae;
							double Gd = -Math.log(Math.pow(10, pkad));
							double Ge = -Math.log(Math.pow(10, pkae));

							double ThetaPEnerNumer = Math.exp(-Gd)*aH;
							double ThetaPEnerDenom = 1+Math.exp(-(Gd-Ge))+Math.exp(-Gd)*aH;

							titrValue = ThetaPEnerNumer/ThetaPEnerDenom;

							Vector<Key<Double, Double>> theseTitrValues = titrCurves.get(rep);
							Key<Double, Double> thisTitrValue = new Key<Double, Double>(pHs[i], titrValue);
							theseTitrValues.add(thisTitrValue);
							titrCurves.put(rep, theseTitrValues);

							/* Stuff that didn't work */
//							double Gp = HSPenergy - 300*pHs[i]*Math.log(10);
//							double Gd = HSDenergy;// + 300*(-modPkaHID)*Math.log(10);
//							double Ge = HSEenergy;// + 300*(-modPkaHIE)*Math.log(10);
//							double dgd = (HSPenergy - pHs[i]*Math.log(10) - HSDenergy);
//							double dge = (HSPenergy - pHs[i]*Math.log(10) - HSEenergy);
//							double numer = Math.exp(-dgd/RT);
//							double denom = 1+Math.exp(-(dgd-dge)/RT)+Math.exp(-dgd/RT);
//							System.out.println(numer+" "+denom);
//							double numer = Math.exp((HSDenergy-(HSPenergy-pHs[i]*Math.log(10)*300))/RT);
//							double denom = 1 + Math.exp((HSDenergy-(HSPenergy-pHs[i]*Math.log(10)*300))/RT) + Math.exp(((HSDenergy-pHs[i]*Math.log(10)*300) - (HSEenergy-pHs[i]*Math.log(10)))/RT);
//							double numer = Math.exp((-HSPenergy+HSDenergy+modPkaHID*Math.log(10))/RT);
//							double denom = 1+Math.exp((-HSPenergy+HSDenergy+modPkaHID*Math.log(10))/RT)+Math.exp((HSDenergy-HSEenergy+modPkaHID*Math.log(10)-modPkaHIE*Math.log(10))/RT);

//							double GdeltaEmilNew = HSPenergy - HSDenergy - modPkaHID*Math.log(10)*300*8.3144621;
//							double GepsilonEmilNew = HSPenergy - HSEenergy -modPkaHIE*Math.log(10)*300*8.3144621;
//							ThetapEner[pH, GdeltaEmilNew, GepsilonEmilNew]
//							double ThetaPEnerNumer = Math.exp(-(GdeltaEmilNew)/RT)*aH;
//							double ThetaPEnerDenom = 1+Math.exp(-(GdeltaEmilNew - GepsilonEmilNew)/RT)+Math.exp(-GdeltaEmilNew/RT)*aH;
//							titrValue = ThetaPEnerNumer/ThetaPEnerDenom;



						}
					}

				} else {

					Instance rCurr = currState.get(r);
					HashMap<Variable, Instance> newState = (HashMap<Variable, Instance>) currState.clone();
					Double protEnergy = 0.0;
					Double deprotEnergy = 0.0;
					if (rCurr == null){
						protEnergy = Double.MIN_VALUE;
						deprotEnergy = Double.MIN_VALUE;
					} else if (rCurr.getLabel().equals("DEPROT")){
						Instance rPROT = r.getInstance("PROT");
						newState.put(r, rPROT);
						protEnergy = sys.evaluateEnergy(newState, true);
						deprotEnergy = sys.evaluateEnergy(currState, true);
					} else if (rCurr.getLabel().equals("PROT")){
						Instance rDEPROT = r.getInstance("DEPROT");
						newState.put(r, rDEPROT);
						protEnergy = sys.evaluateEnergy(currState, true);
						deprotEnergy = sys.evaluateEnergy(newState, true);
					} else{
						//shouldn't happen
					}

					Double titrValue;
//					Double titrValue2;
					if (protEnergy == Double.MIN_VALUE || deprotEnergy == Double.MIN_VALUE){
						titrValue = null;
//						titrValue2 = null;
					} else{

						protEnergy = protEnergy*kln10*T;
						deprotEnergy = deprotEnergy*kln10*T;
						//variables mult by kln10*T, leave RTs where they are
						double exponent = -(protEnergy - deprotEnergy)/RT;

						titrValue = (Math.exp(exponent)/(1+Math.exp(exponent)));

//						double aH = Math.pow(10.0, -pHs[i]);
//						titrValue2 = (aH*Math.exp(exponent))/(1+aH*Math.exp(exponent));

//						double K0 = Math.exp(exponent);
//						double pka = Math.log10(K0);
//						titrValue2 = Math.pow(10.0, pka-pHs[i])/(1+Math.pow(10.0, pka-pHs[i]));

						Vector<Key<Double, Double>> theseTitrValues = titrCurves.get(r);
						Key<Double, Double> thisTitrValue = new Key<Double, Double>(pHs[i], titrValue);
						theseTitrValues.add(thisTitrValue);
						titrCurves.put(r, theseTitrValues);
//
//						Vector<Key<Double, Double>> theseTitrValues2 = titrCurves2.get(r);
//						Key<Double, Double> thisTitrValue2 = new Key<Double, Double>(pHs[i], titrValue2);
//						theseTitrValues2.add(thisTitrValue2);
//						titrCurves2.put(r, theseTitrValues2);

					}

				}

			}

		}

		System.out.println(titrCurves.toString());

		if (write){
			for (Variable r : residues){
				String outFileR = out_path+"\\"+r.getName()+".csv";
				WriteFile wrR = new WriteFile(outFileR);
				Vector<Key<Double, Double>> values = titrCurves.get(r);
				for (int i=0; i<values.size(); i++){
					wrR.writeln(values.get(i).getFirst()+", "+values.get(i).getSecond());
				}
				wrR.close();

//				String outFileR2 = out_path+"\\"+r.getName()+".titr2";
//				WriteFile wrR2 = new WriteFile(outFileR2);
//				Vector<Key<Double, Double>> values2 = titrCurves2.get(r);
//				for (int i=0; i<values2.size(); i++){
//					wrR2.writeln(values2.get(i).getFirst()+", "+values2.get(i).getSecond());
//				}
//				wrR2.close();
			}
			wrPartOpt.close();
			return null;
		} else{
			return titrCurves;
		}

	}

}
