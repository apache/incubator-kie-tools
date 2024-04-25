#!/usr/bin/env python3
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

#Script responsible to update the tests with
#Should be run from root directory of the repository
#Sample usage:  python3 scripts/retrieve_version.py

import sys
import argparse
import common
import re

sys.dont_write_bytecode = True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Kie Tools - SWF Image Version Manager')
    parser.add_argument('--bump-to', dest='bump_to', help='Bump all images and yamls to the next version')
    parser.add_argument('--source-folder', dest='source_folder')
    parser.add_argument('--quarkus-version', dest='quarkus_version', help='Sets the image Quarkus Version')
    parser.add_argument('--kogito-version', dest='kogito_version', help='Sets the image Kogito Version')

    args = parser.parse_args()

    if args.bump_to is None and args.kogito_version is None and args.quarkus_version is None:
        print(parser.print_usage())
    else:
        if args.bump_to is not None:
            common.update_image_and_modules_version(args.bump_to, args.source_folder)
        if args.kogito_version is not None:
            common.update_kogito_platform_version(args.kogito_version)
        if args.quarkus_version is not None:
            common.update_quarkus_platform_version(args.quarkus_version)