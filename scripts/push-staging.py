#!/usr/bin/env python
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

# This script will be responsible to fetch the latest rc tags from each image and define the next
# rc tag to avoid images get overridden.
#
# Requires QUAY_TOKEN env to be set.
# export QUAY_TOKEN=XXXXX
# this token can be retrieved from https://quay.io/repository/kiegroup
#
import sys

sys.dont_write_bytecode = True

import docker
import os
import requests
import yaml
import common
import argparse

IMAGES_NEXT_RC_TAG = []
QUAY_KOGITO_ORG_PLACE_HOLDER = "docker.io/apache/incubator-kie-{}:{}"
QUAY_KOGITO_ORG_PLACE_HOLDER_NO_TAG = "docker.io/apache/incubator-kie-{}"


def find_next_tag(override_tags):
    """
    Populate the IMAGES_NEXT_RC_TAGS with the next rc tag for each image.
    """
    global IMAGES_NEXT_RC_TAG
    for image in common.get_community_images():
        tag = fetch_tag(image, override_tags)
        print("Next tag for image %s is %s" % (image, tag))
        IMAGES_NEXT_RC_TAG.append('{}:{}'.format(image, tag))


def fetch_tag(image, override_tags):
    """
    fetch the rcX tag for the given image, keep increasing until no rc tag is found
    then return the next tag to be used.
    :param image: image to be verified
    :param override_tags: if true, does not increase the rc-X tag
    :return: the next rc tag if override_tags is false.
    """
    version = find_current_rc_version()
    while True:
        url = 'https://quay.io/api/v1/repository/kiegroup/{}/tag/{}/images'.format(image, version)
        print("Defining latest rc tag for image %s with url %s" % (image, url))
        authorization = 'Bearer %s'.format(os.environ['QUAY_TOKEN'])
        headers = {'content-type': 'application/json', 'Authorization': authorization}
        response = requests.get(url, headers=headers)
        if response.status_code == 404:
            return version
        else:
            if override_tags:
                # increase number
                current_number = version[-1]
                print("Image found, current rc tag number is %s, increasing..." % current_number)
            version = get_next_rc_version(version, override_tags)


def tag_and_push_images():
    """
    tag and push the images to quay.io
    """
    cli = docker.client.from_env()
    current_version = common.retrieve_version()
    print("New rc tags %s" % IMAGES_NEXT_RC_TAG)
    if '-rc' not in current_version:
        for next_tag in IMAGES_NEXT_RC_TAG:
            iname = str.split(next_tag, ':')[0]
            iversion_next_tag = str.split(next_tag, ':')[1]
            iname_tag = QUAY_KOGITO_ORG_PLACE_HOLDER.format(iname, current_version)
            try:
                print("Tagging image %s as %s" % (iname_tag, iversion_next_tag))
                cr_tag = QUAY_KOGITO_ORG_PLACE_HOLDER_NO_TAG.format(iname)
                cli.images.get(iname_tag).tag(cr_tag, iversion_next_tag)
                print("Trying to push %s:%s" % (cr_tag, iversion_next_tag))
                cli.images.push(cr_tag, iversion_next_tag)
                print("Pushed")
            except:
                raise

    else:
        # if rc is already on the image version, just tag if needed and push it
        for next_tag in IMAGES_NEXT_RC_TAG:
            iname = str.split(next_tag, ':')[0]
            iversion_next_tag = str.split(next_tag, ':')[1]
            iname_tag = QUAY_KOGITO_ORG_PLACE_HOLDER.format(iname, current_version)
            cr_tag = QUAY_KOGITO_ORG_PLACE_HOLDER_NO_TAG.format(iname)
            try:
                if iversion_next_tag != common.retrieve_version():
                    print("Tagging image %s as %s" % (iname_tag, iversion_next_tag))
                    cli.images.get(iname_tag).tag(cr_tag, iversion_next_tag)

                print("Trying to push %s:%s" % (cr_tag, iversion_next_tag))
                cli.images.push(cr_tag, iversion_next_tag)
            except:
                raise

def find_current_rc_version():
    """
    If the current version already includes the rc tag, keep it, otherwise add it -rc1 tag.
    :return: the current image tag version
    """
    version = common.retrieve_version()
    if '-rc' in version:
        current_image_version = version
    else:
        current_image_version = version + '-rc1'
    return current_image_version


def get_next_rc_version(current_rc_version, override_tags):
    """
    After finding the current rc tag of the image, adds one to it
    e.g: 0.10.0-rc1 will returned as 0.10.0-rc2
    :param current_rc_version: takes the current rc version of the image as input
    :param override_tags: override image tags
    :return: returns the next rc version of the image
    """
    return current_rc_version if override_tags else (
                current_rc_version.split("rc")[0] + "rc" + str(int(current_rc_version.split("rc")[1]) + 1))


if __name__ == "__main__":
    if 'QUAY_TOKEN' not in os.environ:
        print("Env QUAY_TOKEN not found, aborting...")
        os._exit(1)

    parser = argparse.ArgumentParser(description='Push staging images to Quay.io registry.')
    parser.add_argument('-o', action='store_true', dest='override_tags',
                        help='If true, instead increase the tag version, it will use the latest tag retrieved from Quay.')
    args = parser.parse_args()

    version = get_next_rc_version(find_current_rc_version(), args.override_tags)
    common.update_community_images_version(version)
    common.update_image_stream(version)
    common.update_kogito_modules_version(version)
    common.update_artifacts_version_env_in_modules(version)

    find_next_tag(args.override_tags)
    tag_and_push_images()
