#!/usr/bin/env python
#Script responsible to update the tests with 
#Should be run from root directory of the repository
#Sample usage:  python scripts/retrieve_version.py

import sys
sys.dont_write_bytecode = True

import common

import argparse

if __name__ == "__main__":
    print(common.retrieve_version())
