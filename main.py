#!/usr/local/bin/python2.7
# encoding: utf-8
'''
@author:     Kyle Monson
'''

import sys
import sys
if sys.version_info < (3, 4):
    raise OSError("Python 3.4 or greater is required")
import os

from argparse import ArgumentParser
from argparse import RawDescriptionHelpFormatter

from protein import Protein
# from graph import ProteinGraph
from pprint import pprint
# from uncertainy import resolve_uncertainty
from titration_curve import get_titration_curves
from create_titration_output import create_output
from datetime import datetime

__all__ = []
__version__ = 0.1
__date__ = '2015-05-07'
__updated__ = '2015-05-07'

DEBUG = 1
PROFILE = 0

INTERACTION_BASE_FILENAME = "INTERACTION_MATRIX.DAT"
DESOLVATION_BASE_FILENAME = "desolvation_energies.txt"
BACKGROUND_BASE_FILENAME = "background_interaction_energies.txt"

def main(argv=None): # IGNORE:C0111
    '''Command line options.'''

    if argv is None:
        argv = sys.argv
    else:
        sys.argv.extend(argv)

    program_name = os.path.basename(sys.argv[0])
    program_version = "v%s" % __version__
    program_build_date = str(__updated__)
    program_version_message = '%%(prog)s %s (%s)' % (program_version, program_build_date)
    program_shortdesc = __import__('__main__').__doc__.split("\n")[1]
    program_license = '''%s

  Created by Kyle Monson on %s.
  Copyright 2015 Pacific Northwest National Laboratory. All rights reserved.

  Licensed under the Apache License 2.0
  http://www.apache.org/licenses/LICENSE-2.0

  Distributed on an "AS IS" basis without warranties
  or conditions of any kind, either express or implied.

USAGE
''' % (program_shortdesc, str(__date__))

    try:
        # Setup argument parser
        parser = ArgumentParser(description=program_license, formatter_class=RawDescriptionHelpFormatter)
        parser.add_argument("-v", "--verbose", dest="verbose", action="count", help="set verbosity level [default: %(default)s]")
        parser.add_argument('-V', '--version', action='version', version=program_version_message)
        parser.add_argument(dest="input", help="path to input folder", metavar="input_path")
        parser.add_argument(dest="output", help="paths to output folder", metavar="output_path")
        parser.add_argument("--test", action='store_true', default=False, help="Run basic sanity tests using selected input.")
        parser.add_argument("--dump-state", action='store_true', default=False, help="Dump state to state.txt in output.")

        # Process arguments
        args = parser.parse_args()

        input_path = args.input
        output_path = args.output
        verbose = args.verbose
        dump_state = args.dump_state

        if verbose > 0:
            print("Verbose mode on")

        try:
            if verbose > 0:
                print("Creating output directory: {0}".format( output_path) )
            os.makedirs(output_path)
        except os.error:
            if verbose > 0:
                print("Output directory already exists: {0}". format( output_path ) )

        #
	#  read in INTERACTION_MATRIX.DAT, BACKGR.DAT, DESOLV.DAT 
	#   (created when pdb2pqk is run)
	#
        interaction_filepath = os.path.join(input_path, INTERACTION_BASE_FILENAME)
        background_filepath = os.path.join(input_path, BACKGROUND_BASE_FILENAME)
        desolvation_filepath = os.path.join(input_path, DESOLVATION_BASE_FILENAME)

        with open(interaction_filepath) as interaction_file, \
             open(background_filepath) as background_file, \
             open(desolvation_filepath) as desolvation_file:
            protein = Protein(interaction_file, desolvation_file, background_file)

        state_file = None
        if dump_state:
            state_file = open(os.path.join(output_path, "state.txt"), 'w')

        #
	#  Call graph cut!
	#
        start = datetime.now()
        curves = get_titration_curves(protein.protein_complex, state_file)
        end = datetime.now()

        delta = end - start
        delta_seconds = delta.total_seconds()

        with open(os.path.join(output_path, "timing.txt"), 'a') as timing_file:
            timing_file.write(str(delta_seconds)+'\n')


        if dump_state:
            state_file.close()

        #
	#  Write out titration curves
	#
        create_output(output_path, curves)

        #pprint(dict(curves))

        if args.test:
            import tests
            #tests.test_normalize(protein)
            #tests.test_stuff(protein)
            #tests.test_adding_ph(protein)

        return 0
    except KeyboardInterrupt as e:
        ### handle keyboard interrupt ###
        if DEBUG:
            raise(e)
        return 0

if __name__ == "__main__":
    if DEBUG:
        sys.argv.append("-v")
    if PROFILE:
        import cProfile
        import pstats
        profile_filename = 'main_profile.txt'
        cProfile.run('main()', profile_filename)
        statsfile = open("profile_stats.txt", "wb")
        p = pstats.Stats(profile_filename, stream=statsfile)
        stats = p.strip_dirs().sort_stats('cumulative')
        stats.print_stats()
        statsfile.close()
        sys.exit(0)
    sys.exit(main())
