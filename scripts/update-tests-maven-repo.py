#!/usr/bin/python3
#Script responsible to update the tests with 
#Should be run from root directory of the repository
#Sample usage:  python3 scripts/update-tests-maven-repo.py

import re
import os
import argparse

MAVEN_MODULE="kogito-maven/3.6.x"

# behave tests that needs to be update
S2I_BEHAVE_TESTS = {"kogito-quarkus-ubi8-s2i.feature", "kogito-springboot-ubi8-s2i.feature"}

def update_test_apps_clone_repo(repo_url, replaceJbossRepository):
    '''
    Updates the clone-repo.sh script for adding the given repository URL to the Maven settings.
    :param repo_url: Maven Repository URL to set
    :param replaceJbossRepository: If enabled, replace the default JBoss repository
    '''
    file = 'tests/test-apps/clone-repo.sh'
    print('Updating file {}'.format(file))
    if replaceJbossRepository:
        os.system('sed -i \'s|^export JBOSS_MAVEN_REPO_URL=.*|export JBOSS_MAVEN_REPO_URL=\"{}\"|\' '.format(repo_url) + file)
    else :
        os.system('sed -i \'s|^# export MAVEN_REPO_URL=.*|export MAVEN_REPO_URL=\"{}\"|\' '.format(repo_url) + file)

def update_maven_repo_in_features(repo_url, replaceJbossRepository):
    '''
    Updates S2I feature tests with the given repository URL.
    :param repo_url: Maven Repository URL to set
    :param replaceJbossRepository: If enabled, replace the default JBoss repository
    '''
    base_dir = 'tests/features'
    pattern_branch = re.compile('\|\s*variable[\s]*\|[\s]*value[\s]*\|')

    envVarKey = "MAVEN_REPO_URL"
    if replaceJbossRepository:
        envVarKey = "JBOSS_MAVEN_REPO_URL"


    for feature in S2I_BEHAVE_TESTS:
        print("Updating feature {0}".format(feature))
        with open(os.path.join(base_dir, feature)) as fe:
            updated_value = pattern_branch.sub("| variable | value |\n      | {} | {} |\n      | MAVEN_DOWNLOAD_OUTPUT | true |".format(envVarKey, repo_url), fe.read())

        with open(os.path.join(base_dir, feature), 'w') as fe:
            fe.write(updated_value)

def update_maven_repo_in_tests(repo_url, replaceJbossRepository):
    '''
    Updates images tests with the given repository URL.
    :param repo_url: Maven Repository URL to set
    :param replaceJbossRepository: If enabled, replace the default JBoss repository
    '''
    update_maven_repo_in_features(repo_url, replaceJbossRepository)
    update_test_apps_clone_repo(repo_url, replaceJbossRepository)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Update Maven information in repo from the given artifact url and version.')
    parser.add_argument('--repo-url', dest='repo_url', default='', help='Defines the url of the repository to setup into the tests')
    parser.add_argument('--replace-jboss-repo', dest='replace_jboss_repo', default=False, action='store_true', help='Enable if repo-url should replace the main JBoss repository')
    args = parser.parse_args()

    if args.repo_url != '':
        update_maven_repo_in_tests(args.repo_url, args.replace_jboss_repo)
