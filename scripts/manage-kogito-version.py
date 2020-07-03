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

# behave tests that needs to be update
BEHAVE_TESTS = {"kogito-quarkus-ubi8-s2i.feature", "kogito-springboot-ubi8-s2i.feature",
                "kogito-quarkus-jvm-ubi8.feature", "kogito-springboot-ubi8.feature"}



def update_behave_tests(tests_branch):
    """
    will update the behave tests accordingly.
    If master, the app 8.0.0-SNAPSHOT otherwise use the same value for branch and version
    :param tests_branch:
    """
    base_dir = 'tests/features'
    # if 'master' in target_branch:
    #     artifact_version = '8.0.0-SNAPSHOT'

    # this pattern will look for any occurrences of using master or using x.x.x
    pattern_branch = re.compile(r'(using master)|(using \s*([\d.]+.x))|(using \s*([\d.]+))')
    # kogito examples does not have the version on built examples anymore, let's comment it out for now.
    # quarkus_native_pattern_app_version = re.compile(r'(8.0.0-SNAPSHOT-runner\s)|((\s*([\d.]+)-runner\s)|(\s*([\d.]+)-SNAPSHOT-runner\s))')
    # quarkus_pattern_app_version = re.compile(r'(8.0.0-SNAPSHOT-runner.jar)|((\s*([\d.]+)-runner.jar)|(\s*([\d.]+)-SNAPSHOT-runner.jar))')
    # spring_pattern_app_version = re.compile(r'(8.0.0-SNAPSHOT.jar)|((\s*([\d.]+).jar)|(\s*([\d.]+)-SNAPSHOT.jar))')
    for feature in BEHAVE_TESTS:
        print("Updating feature {0}".format(feature))
        with open(os.path.join(base_dir, feature)) as fe:
            updated_value = pattern_branch.sub('using ' + tests_branch, fe.read())
            # kogito examples does not have the version on built examples anymore, let's comment it out for now.
            # updated_value = quarkus_native_pattern_app_version.sub(tests_version + '-runner ', updated_value)
            # updated_value = quarkus_pattern_app_version.sub(tests_version + '-runner.jar', updated_value)
            # updated_value = spring_pattern_app_version.sub(tests_version + '.jar', updated_value)

        with open(os.path.join(base_dir, feature), 'w') as fe:
            fe.write(updated_value)


def update_test_apps_clone_repo(target_branch):
    file = 'tests/test-apps/clone-repo.sh'
    print('Updating file {}'.format(file))
    if target_branch == 'master':
        os.system('sed -i \'s/^git checkout.*/git checkout master/\' ' + file)
    elif 'x' in target_branch:
        os.system('sed -i \'s/^git checkout.*/git checkout -b ' + target_branch + '/\' ' + file)
    else:
        os.system('sed -i \'s/^git checkout.*/git checkout -b ' + target_branch + ' ' + target_branch + '/\' ' + file)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Kogito Version Manager')
    parser.add_argument('--bump-to', dest='bump_to', help='bump everything to the next version')
    parser.add_argument('--apps-branch', dest='apps_branch',
                        help='Update Behave tests to use the desired branch for kogito-examples')
    parser.add_argument('--confirm', default=False, action='store_true', help='To confirm automatically the setup')

    args = parser.parse_args()

    if args.bump_to:
        # validate if the provided version is valid.
        # e.g. 1.10.0 or 1.0.0-rc1
        pattern = '\d.\d{1,2}.(\d$|\d-rc\d+$)'
        regex = re.compile(r'\d.\d{1,2}.(\d$|\d-rc\d+$)')
        valid = regex.match(args.bump_to)
        tests_branch = ""
        if valid:
            tests_branch = args.bump_to
            if args.apps_branch is not None:
                tests_branch = args.apps_branch
            if 'rc' in args.bump_to:
                tests_branch = 'master'

            print("Version will be updated to {0}".format(args.bump_to))
            print("Version on behave tests examples will be updated to version {0} and branch {1}".format(args.bump_to,
                                                                                                          tests_branch))
            if not args.confirm:
                input("Is the information correct? If so press any key to continue...")

            common.update_image_version(args.bump_to)
            common.update_image_stream(args.bump_to)
            common.update_modules_version(args.bump_to)
            common.update_kogito_version_env_in_modules(args.bump_to)
            update_behave_tests(tests_branch)
            update_test_apps_clone_repo(tests_branch)
        else:
            print("Provided version {0} does not match the expected regex - {1}".format(args.bump_to, pattern))
    else:
        print(parser.print_usage())
