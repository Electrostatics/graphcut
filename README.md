# Graph-cut method for determining optimal titration state configuration

This code assumes that you've run PDB2PQR as follows:

> ./pdb2pqr --ff=parse --ph-calc-method=pdb2pka --with-ph=7 2MKB 2MKB.pqr

and now have a subdirectory <code>pdb2pka_output/</code>.

Graph cut can be run by

> python main.py -v <input path> <output path>

The option <code>-v</code> will print out pH value and the number of leftover residues to standard out.

Make sure <code>input path</code> contains the following files for a single protein:

> INTERACTION_MATRIX.DAT
> desolvation_energies.txt
> background_interaction_energies.txt

The directory <code>output path</code> will get the timing file and all titration curves.
