
from __future__ import print_function
import random
from pprint import pprint

def test_normalize(protein):
    pc = protein.protein_complex


    for _ in xrange(100):
        seen = {}
        labeling = {}

        pH = round(random.random() * 20.0, 1)
        #pH = 10.0
        pc.normalize(pH)

        for key, variable in pc.residue_variables.iteritems():
            _, chain, location = key
            location = (chain, location)
            other = seen.get(location)
            if other is not None and not other.protonated:
                instance_name = "PROTONATED"
            else:
                instance_name = "DEPROTONATED" if bool(random.randint(0,1)) else "PROTONATED"

            instance = variable.instances[instance_name]
            seen[location] = instance
            labeling[variable] = instance

        energy = pc.evaluate_energy(labeling, False, pH=pH)
        norm_energy = pc.evaluate_energy(labeling, True)


        pprint(labeling)
        print("Energy:", energy)
        print("Normalized:", norm_energy)

        print("Diff", abs(energy-norm_energy))

        assert abs(energy-norm_energy) < 0.00001



def test_stuff(protein):
    pc = protein.protein_complex
    pH = 4.0
    labeling = {}
    pc.normalize(pH)

    deprotonated = set([("GLU","A","20"),
                       ("GLU","A","21"),
                       ("GLU","A","45"),
                       ("ASP","A","47"),
                       ("HId","A","12")])

    for key, variable in pc.residue_variables.iteritems():
        residue, chain, location = key
        if key in deprotonated:
            instance_name = "DEPROTONATED"
        else:
            instance_name = "PROTONATED"

        instance = variable.instances[instance_name]
        labeling[variable] = instance

    energy = pc.evaluate_energy(labeling, False, pH=pH)
    norm_energy = pc.evaluate_energy(labeling, True)


    pprint(labeling)
    print("Energy:", energy)
    print("Normalized:", norm_energy)

    print("Diff", abs(energy-norm_energy))

    assert abs(energy-norm_energy) < 0.00001


def test_other_stuff(protein):
    pc = protein.protein_complex
    pc.energy_at_pH(10.0)
    keys = pc.interaction_energies_for_ph.keys()
    keys.sort()
    for pair in keys:
        print (pair, pc.interaction_energies_for_ph[pair])

    keys = pc.residue_variables.keys()
    keys.sort()

    for key in keys:
        residue = pc.residue_variables[key]
        prot = residue.instances["DEPROTONATED"]
        deprot = residue.instances["PROTONATED"]
        print (prot, prot.energy_with_ph)
        print (deprot, deprot.energy_with_ph)