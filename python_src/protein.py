'''
Created on May 8, 2015

@author: D3Y382
'''

import re
from protein_complex import ProteinComplex

class Protein(object):
    def __init__(self, interaction_file, desolv_file, background_file):
        self.protein_complex = ProteinComplex()

        self.ingest_interaction_file(interaction_file)
        self.ingest_desolv_or_background_file(desolv_file)
        self.ingest_desolv_or_background_file(background_file)

        self.protein_complex.simplify()
        self.protein_complex.normalize(0.0)



    def ingest_interaction_file(self, interaction_file):
        interaction_file.seek(0)
        #Get and ditch header
        interaction_file.readline()
        interaction_file.readline()

        for line in interaction_file:
            self.process_interaction_line(line)

    def process_interaction_line(self, line):
        split_line = line.split()
        group1, group2, group1_state, group2_state, _, _, inter_avg = split_line
        _, group1_chain, group1_loc, _, group1_type = re.split('[_:]', group1)
        _, group2_chain, group2_loc, _, group2_type = re.split('[_:]', group2)
        inter_avg = float(inter_avg)

        pc = self.protein_complex

        pc.add_residue(group1_type, group1_chain, group1_loc)
        pc.add_residue(group2_type, group2_chain, group2_loc)

        #We skip all interactions with self. They should always be 0.
        if (group1_type, group1_chain, group1_loc) != (group2_type, group2_chain, group2_loc):
            instance1 = pc.get_instance(group1_type, group1_chain, group1_loc, group1_state)
            instance2 = pc.get_instance(group2_type, group2_chain, group2_loc, group2_state)

            pc.interaction_energies[instance1, instance2] = inter_avg

            #All interaction files SHOULD be symmetric. PDB2PKA makes them that way.
            #This just makes sure that is true, especially if we tweak stuff by hand.
            flipped_inter_avg = pc.interaction_energies.get((instance2, instance1))
            if flipped_inter_avg is not None:
                diff = abs(inter_avg - flipped_inter_avg)
                if diff > 0.0:
                    print group1_type, group1_chain, group1_loc, group1_state
                    print group2_type, group2_chain, group2_loc, group2_state
                    print "Difference:", diff

            else:
                #This makes it easier to make test files and does not prevent
                # the sanity checking above.
                pc.interaction_energies[instance2, instance1] = inter_avg

        elif inter_avg != 0:
            print "Non-zero interaction energy with self:", inter_avg
            print group1_type, group1_chain, group1_loc, group1_state, group2_state


    def ingest_desolv_or_background_file(self, file_obj):
        file_obj.seek(0)

        for line in file_obj:
            self.process_desolv_or_background_line(line)


    def process_desolv_or_background_line(self, line):
        split_line = line.split()
        type_chain_loc, state_name, energy_str = split_line
        energy = float(energy_str)
        res_type, chain, location = type_chain_loc.split('_')

        instance = self.protein_complex.get_instance(res_type, chain, location, state_name)

        instance.energy += energy
