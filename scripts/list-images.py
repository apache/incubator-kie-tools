#!/usr/bin/python3
# Script responsible to update the tests with
# Should be run from root directory of the repository
# Sample usage:  python3 scripts/update-tests.py

import argparse
import sys

import common

sys.dont_write_bytecode = True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='Kogito Version Manager - List Images by Community and Product version')
    parser.add_argument('--prod', default=False, action='store_true', help='List product images')

    args = parser.parse_args()

    images = []
    if args.prod:
        images = common.get_prod_images()
    else:
        images = common.get_community_images()

    for img in sorted(images):
        print(img)
