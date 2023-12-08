#!/usr/bin/env python
# Script responsible to update the tests with
# Should be run from root directory of the repository


import argparse
import sys

import common

sys.tracebacklimit = 0
sys.dont_write_bytecode = True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='Kogito Version Manager - List Images by Community and Product version')
    parser.add_argument('--prod', default=False, action='store_true', help='List product images')
    parser.add_argument('--swf-builder', default=False, action='store_true', help='List swf builder images')
    parser.add_argument('-s', '--supporting-services', default=False, action='store_true',
                        help='List Supporting Services images')
    parser.add_argument('-is', '--is_supporting_services_or_swf_builder', default=False, type=str,
                        help='Query the given supporting service or swf builder image, if not found raise exception.')

    args = parser.parse_args()

    images = []
    if args.is_supporting_services_or_swf_builder:
        common.is_supporting_services_or_swf_builder(args.is_supporting_services_or_swf_builder, args.prod)
    elif args.supporting_services:
        images = common.get_supporting_services_images(args.prod)
    elif args.prod:
        images = common.get_prod_images()
    elif args.swf_builder:
        images = common.get_swf_builder_images(args.prod)
    else:
        images = common.get_community_images()

    for img in sorted(images):
        print(img)
