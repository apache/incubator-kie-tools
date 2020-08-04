#!/usr/bin/python3
#Script responsible to update the tests with 
#Should be run from root directory of the repository
#Sample usage:  python3 scripts/update-tests.py

import sys
sys.dont_write_bytecode = True

import common

import argparse

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Update Maven information in repo from the given artifact url and version.')
    parser.add_argument('--repo-url', dest='repo_url', help='Defines the url of the repository to setup into the tests')
    parser.add_argument('--replace-jboss-repo', dest='replace_jboss_repo', default=False, action='store_true', help='Enable if repo-url should replace the main JBoss repository')
    parser.add_argument('--examples-uri', dest='examples_uri', help='To update the examples uri for testing')
    parser.add_argument('--examples-ref', dest='examples_ref', help='To update the examples ref for testing')
    parser.add_argument('--artifacts-version', dest='artifacts_version', help='To update the artifacts version for testing')
    args = parser.parse_args()

    if args.repo_url:
        common.update_maven_repo_in_behave_tests(args.repo_url, args.replace_jboss_repo)
        common.update_maven_repo_in_clone_repo(args.repo_url, args.replace_jboss_repo)

    if args.examples_uri:
        common.update_examples_uri_in_behave_tests(args.examples_uri)
        common.update_examples_uri_in_clone_repo(args.examples_uri)

    if args.examples_ref:
        common.update_examples_ref_in_behave_tests(args.examples_ref)
        common.update_examples_ref_in_clone_repo(args.examples_ref)

    if args.artifacts_version:
        common.update_artifacts_version_in_behave_tests(args.artifacts_version)