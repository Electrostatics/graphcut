
from __future__ import print_function
import random
from pprint import pprint

def test_normalize(protein):
    pc = protein.protein_complex

    for _ in xrange(10):
        seen = {}
        labeling = {}

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

        energy = pc.evaluate_energy(labeling, False)
        norm_energy = pc.evaluate_energy(labeling, True)

        pprint(labeling)
        print("Energy:", energy)
        print("Normalized:", norm_energy)


