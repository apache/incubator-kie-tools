#!/usr/bin/python3
# This script will be responsible to help to update the quarkus version in kogito images/modules
# Example of usage:
#   # move the current version to the next one or rcX
#   python scripts/update-quarkus-version.py --bump-to 2.13.0.Final
#
# Dependencies:
#  ruamel.yaml

import sys
import argparse
import common
import re

sys.dont_write_bytecode = True

def update_quarkus_version_in_behave_tests_repository_paths(quarkus_version):
    """
    Update quarkus version into behave tests
    :param quarkus_version: quarkus version to set
    """
    print("Set quarkus_version {} in behave tests as repository path".format(quarkus_version))
    # pattern to change the KOGITO_VERSION
    pattern = re.compile(
        'io/quarkus/platform/quarkus-bom/([\d.]+.Final)/quarkus-bom-([\d.]+.Final).pom')
    replacement = 'io/quarkus/platform/quarkus-bom/{}/quarkus-bom-{}.pom'.format(quarkus_version, quarkus_version)
    common.update_in_behave_tests(pattern, replacement)



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Quarkus version updater')
    parser.add_argument('--bump-to', dest='bump_to', help='bump everything to the given version')
    parser.add_argument('--prod', default=False, action='store_true', help='Update product modules/images')

    args = parser.parse_args()

    if args.bump_to:
        common.update_quarkus_version_env_in_image(args.bump_to)
        common.update_images_env_value(common.QUARKUS_VERSION_ENV_KEY, args.bump_to, args.prod)
        common.update_modules_env_value(common.QUARKUS_VERSION_ENV_KEY, args.bump_to, args.prod)

        common.update_images_label_value(common.QUARKUS_VERSION_LABEL_NAME, args.bump_to, args.prod)
        common.update_modules_label_value(common.QUARKUS_VERSION_LABEL_NAME, args.bump_to, args.prod)

        update_quarkus_version_in_behave_tests_repository_paths(args.bump_to)
    else:
        print(parser.print_usage())