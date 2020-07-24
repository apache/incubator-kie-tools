#!/usr/bin/python3
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

# All Kogito images
IMAGES = ["kogito-quarkus-ubi8", "kogito-quarkus-jvm-ubi8", "kogito-quarkus-ubi8-s2i",
          "kogito-springboot-ubi8", "kogito-springboot-ubi8-s2i", "kogito-data-index",
          "kogito-explainability",
          "kogito-jobs-service", "kogito-management-console"]

IMAGES_NEXT_RC_TAG = []
QUAY_KOGITO_ORG_PLACE_HOLDER = "quay.io/kiegroup/{}:{}"
QUAY_KOGITO_ORG_PLACE_HOLDER_NO_TAG = "quay.io/kiegroup/{}"


def find_next_tag():
    '''
    Populate the IMAGES_NEXT_RC_TAGS with the next rc tag for each image.
    '''
    global IMAGES_NEXT_RC_TAG
    for image in IMAGES:
        tag = fetch_tag(image)
        print("Next tag for image %s is %s" % (image, tag))
        IMAGES_NEXT_RC_TAG.append('{}:{}'.format(image, tag))


def fetch_tag(image):
    '''
    fetch the rcX tag for the given image, keep increasing until no rc tag is found
    then return the next tag to be used.
    :param image: image to be verified
    :return: the next rc tag
    '''
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
            # increase number
            current_number = version[-1]
            print("Image found, current rc tag number is %s, increasing..." % current_number)
            version = get_next_rc_version(version)


def tag_and_push_images():
    '''
    tag and push the images to quay.io
    '''
    cli = docker.client.from_env()
    current_version = get_current_version()
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
                if iversion_next_tag != get_current_version():
                    print("Tagging image %s as %s" % (iname_tag, iversion_next_tag))
                    cli.images.get(iname_tag).tag(cr_tag, iversion_next_tag)

                print("Trying to push %s:%s" % (cr_tag, iversion_next_tag))
                cli.images.push(cr_tag, iversion_next_tag)
            except:
                raise


def get_current_version():
    '''
    get the current image version from image.yaml. The version defined there will be considered
    the point of truth, update it carefully.
    :return: current image.yaml defined version
    '''
    with open('image.yaml') as image_yaml:
        data = yaml.load(image_yaml, Loader=yaml.FullLoader)
        return data['version']


def find_current_rc_version():
    '''
    If the current version already includes the rc tag, keep it, otherwise add it -rc1 tag.
    :return: the current image tag version
    '''
    version = get_current_version()
    if '-rc' in version:
        CURRENT_IMAGE_VERSION = version
    else:
        CURRENT_IMAGE_VERSION = version+'-rc1'
    return CURRENT_IMAGE_VERSION

def get_next_rc_version(current_rc_version):
    '''
    After finding the current rc tag of the image, adds one to it
    e.g: 0.10.0-rc1 will returned as 0.10.0-rc2
    :param current_rc_version: takes the current rc version of the image as input
    :return: returns the next rc version of the image
    '''    
    return (current_rc_version.split("rc")[0] + "rc" + str(int(current_rc_version.split("rc")[1]) + 1 ))

if __name__ == "__main__":
    if 'QUAY_TOKEN' not in  os.environ:
        print("Env QUAY_TOKEN not found, aborting...")
        os._exit(1)
    version = get_next_rc_version(find_current_rc_version())
    common.update_image_version(version)
    common.update_image_stream(version)
    common.update_modules_version(version)
    common.update_kogito_version_env_in_modules(version)

    find_next_tag()
    tag_and_push_images()
