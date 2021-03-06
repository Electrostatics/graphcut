from graph import ProteinGraph
from uncertainty import resolve_uncertainty
from collections import defaultdict
import math
from pprint import pprint
import sys

gas_constant = 8.3144621
#ln(10) * gas constant
kln10 = math.log(10) * gas_constant
# Degrees Kelvin
T=300.0
kln10_T = kln10  *T
RT = 2479.0;
RT_gas = RT * gas_constant

modPkaHIP = 6.6
modPkaHIE = modPkaHIP
modPkaHID = modPkaHIP

def print_pc_state(pc, normal_form, out_file):
    """Dump protein_complex state to out_file
       normal_form - Dump the normal form part of the state."""
    rv = pc.residue_variables

    ie = pc.normalized_interaction_energies if normal_form else pc.interaction_energies_for_ph

    for v_residue in rv.itervalues():
        for v_instance in v_residue.instances.itervalues():
            for w_residue in rv.itervalues():
                if v_residue == w_residue:
                    continue
                for w_instance in w_residue.instances.itervalues():
                    out_file.write(str((v_instance, w_instance)) + " " + str(round(ie[v_instance, w_instance],4)) + '\n')

    keys = pc.residue_variables.keys()

    for key in keys:
        residue = pc.residue_variables[key]
        for instance in residue.instances.values():
            if normal_form:
                out_file.write(str(instance) + " " + str(round(instance.energyNF,4)) + "\n")
            else:
                out_file.write(str(instance) + " " + str(round(instance.energy_with_ph,4)) + "\n")

    if normal_form:
        out_file.write("Normalized constant energy: " + str(round(pc.normalized_constant_energy,4)) + "\n")

def print_dg_state(dg, out_file):
    """Dump directed graph state to out_file"""
    out_file.write("Flow network:\nVertices:\n")

    nodes = dg.node.keys()
    nodes.sort()

    for node in nodes:
        out_file.write('_'.join(node)+"\n")

    out_file.write("\nEdges:\n")

    edges = []
    for edge in dg.edges_iter(data="capacity"):
        result = []
        if isinstance(edge[0], tuple):
            result.append('_'.join(edge[0]))
        else:
            result.append(edge[0])
        if isinstance(edge[1], tuple):
            result.append('_'.join(edge[1]))
        else:
            result.append(edge[1])

        result.append(edge[2])

        edges.append(result)

    edges.sort()

    for edge in edges:
        out_file.write("(")
        out_file.write(edge[0])
        out_file.write(", ")
        out_file.write(edge[1])
        out_file.write(")= ")
        out_file.write(str(round(edge[2],4))+"\n")


def get_titration_curves(protein_complex, state_file=None):
    """For each ph value:
           Get the normal form of the protein energies.
           Build a flow graph
           Get the min cut of the graph
           Find which state for each residue from the cut (labeling) and the unknown states (uncertain)
           Use brute force or MC to resolve the uncertain states.
           Calculate the curve value for each residue

        Returns results for all residues for each ph."""
    curves = defaultdict(list)

    pg = ProteinGraph(protein_complex)

    pH = 0.0
    step_size = 0.1
    end_ph = 20.0
    steps = int(end_ph / step_size) + 1

    for step in xrange(steps):
        pH = step * 0.1
        print "pH", pH
        #print "Processing pH:", pH

        if state_file is not None:
            state_file.write ("pH="+ str(pH)+"\n")

            state_file.write("REGULAR ENERGIES\n")
            protein_complex.energy_at_pH(pH)
            print_pc_state(protein_complex, False, state_file)

            state_file.write('\n')
            state_file.write("NORMAL FORM ENERGIES\n")


        protein_complex.normalize(pH)

        if state_file is not None:
            print_pc_state(protein_complex, True, state_file)
            state_file.write('\n')

        pg.update_graph()

        if state_file is not None:
            print_dg_state(pg.DG, state_file)
            state_file.write('\n')

        cv, s_nodes, t_nodes = pg.get_cut()
        labeling, uncertain = pg.get_labeling_from_cut(s_nodes, t_nodes)

        new_labeling = resolve_uncertainty(protein_complex, labeling, uncertain, verbose=True)

        curve_values = get_curve_values(protein_complex, new_labeling, pH)
        for key, value in curve_values.iteritems():
            curves[key].append((pH, value))

    return curves

def get_curve_values(protein_complex, labeling, pH):
    """Using the given selected residue states (labeling) and pH get the
       current curve value for all titratable residues."""
    his_seen = set()
    results = {}

    aH = math.pow(10, -pH)

    for key, residue in protein_complex.residue_variables.iteritems():
        name, chain, location = key

        if name in ("HId", "HIe"):
            #Do HIS stuff
            if (chain, location) in his_seen:
                continue

            his_seen.add((chain, location))

            if name == "HId":
                hid_residue = residue
                hie_residue = protein_complex.residue_variables["HIe", chain, location]
            else:
                hie_residue = residue
                hid_residue = protein_complex.residue_variables["HId", chain, location]

            dge, dgd = protein_complex.evaluate_energy_diff_his(hie_residue, hid_residue, labeling,
                                                                 normal_form=True)

            dge *= kln10_T
            dgd *= kln10_T

            try:
                dpkad = -math.log10(math.exp(dgd/RT_gas))
                dpkae = -math.log10(math.exp(dge/RT_gas))

                pkad = modPkaHIP + dpkad
                pkae = modPkaHIP + dpkae

                Gd = -math.log(math.pow(10, pkad))
                Ge = -math.log(math.pow(10, pkae))

                ThetaPEnerNumer = sys.float_info.min
                ThetaPEnerDenom = sys.float_info.min

                Gdeavg = (Gd+Ge)/2.0

                if not labeling[hie_residue].protonated and labeling[hid_residue].protonated:
                    ThetaPEnerNumer = math.exp(-Ge)*aH
                    ThetaPEnerDenom = 1.0+math.exp(-(Gd-Ge))+math.exp(-Ge)*aH
                elif labeling[hie_residue].protonated and not labeling[hid_residue].protonated:
                    ThetaPEnerNumer = math.exp(-Gd)*aH
                    ThetaPEnerDenom = 1.0+math.exp(-(Gd-Ge))+math.exp(-Gd)*aH
                elif labeling[hie_residue].protonated and labeling[hid_residue].protonated:
                    ThetaPEnerNumer = math.exp(-Gdeavg)*aH
                    ThetaPEnerDenom = 1.0+math.exp(-(Gd-Ge))+math.exp(-Gdeavg)*aH

                titration_value = ThetaPEnerNumer/ThetaPEnerDenom
            except OverflowError:
                titration_value = 1.0

            results["HIS", chain, location] = titration_value

        else:
            #Do not HIS stuff
            #energy_diff = protonated_energy - depotonated_energy
            energy_diff = protein_complex.evaluate_energy_diff(residue, labeling, normal_form=True)

            exp = -(energy_diff*kln10_T)/RT
            #Handle case where there is an unresolved bump.
            try:
                e_exp = math.exp(exp)
                titration_value = e_exp/(1.0+e_exp)
            except OverflowError:
                titration_value = 1.0
            results[key] = titration_value

    return results





