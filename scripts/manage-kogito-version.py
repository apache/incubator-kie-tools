#!/usr/bin/python3
# This script will be responsible to help to manage kogito images and modules version, it will update all needed files
# Example of usage:
#   # move the current version to the next one or rcX
#   python scripts/manage-kogito-version.py --bump-to 0.99.0
#
#   # to set a custom kogito-examples branch for the behave tests different than the defaults (sam than the version
#   # or master for rc) use --branch-apps parameters, e.g.:
#   python scripts/manage-kogito-version.py --bump-to 0.99.0 --apps-branch 0.10.x
#
# Dependencies:
#  ruamel.yaml

import sys
sys.dont_write_bytecode = True

import argparse
import common
import re
import os

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Kogito Version Manager')
    parser.add_argument('--bump-to', dest='bump_to', help='bump everything to the next version')
    parser.add_argument('--artifacts-version', dest='artifacts_version', help='update the artifacts version in modules/tests. Default is equal to bump-to')
    parser.add_argument('--examples-ref', dest='examples_ref',
                        help='Update Behave tests to use the desired branch for kogito-examples')
    parser.add_argument('--confirm', default=False, action='store_true', help='To confirm automatically the setup')

    args = parser.parse_args()

    if args.bump_to:
        # validate if the provided version is valid.
        # e.g. 1.10.0 or 1.0.0-rc1
        pattern = '\d+.\d+.(\d+$|\d+-rc\d+$|\d+-snapshot$)'
        regex = re.compile(r'\d+.\d+.(\d+$|\d+-rc\d+|\d+-snapshot$)')
        valid = regex.match(args.bump_to)
        examples_ref = ""
        if valid:
            examples_ref = args.bump_to
            if args.examples_ref is not None:
                examples_ref = args.examples_ref
            if 'rc' in args.bump_to:
                examples_ref = 'master'
            
            artifacts_version = args.bump_to
            if args.artifacts_version:
                artifacts_version = args.artifacts_version

            print("Images version will be updated to {0}".format(args.bump_to))
            print("Artifacts version will be updated to {0}".format(artifacts_version))
            print("Examples ref will be updated to {}".format(examples_ref))

            if not args.confirm:
                input("Is the information correct? If so press any key to continue...")

            # modules
            common.update_image_version(args.bump_to)
            common.update_image_stream(args.bump_to)
            common.update_modules_version(args.bump_to)
            common.update_artifacts_version_env_in_modules(artifacts_version)
            common.update_artifacts_version_in_python_scripts(artifacts_version)

            # tests default values
            common.update_examples_ref_in_behave_tests(examples_ref)
            common.update_examples_ref_in_clone_repo(examples_ref)
            common.update_artifacts_version_in_behave_tests(artifacts_version)
        else:
            print("Provided version {0} does not match the expected regex - {1}".format(args.bump_to, pattern))
    else:
        print(parser.print_usage())
