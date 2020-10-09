#!/usr/bin/python3
#Script responsible to update the tests with 
#Should be run from root directory of the repository
#Sample usage:  python3 scripts/update-tests.py

import sys
sys.dont_write_bytecode = True

import common

if __name__ == "__main__":
    images = common.get_all_images()

    for img in images:
        print(img)
