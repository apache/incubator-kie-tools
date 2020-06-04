#!/usr/bin/python3
#This script defines some common function that are used by manage-kogito-version.py and push-staging.py script


import os
import re

from ruamel.yaml import YAML

# all kogito-image modules that points to the kogito version.
MODULES = {"kogito-data-index", "kogito-image-dependencies",
           "kogito-infinispan-properties", "kogito-jobs-service",
           "kogito-jq", "kogito-kubernetes-client",
           "kogito-launch-scripts", "kogito-logging",
           "kogito-management-console", "kogito-persistence",
           "kogito-quarkus", "kogito-quarkus-jvm",
           "kogito-quarkus-s2i", "kogito-s2i-core",
           "kogito-springboot", "kogito-springboot-s2i",
           "kogito-system-user"}

# imagestream file that contains all images, this file aldo needs to be updated.
IMAGE_STREAM = "kogito-imagestream.yaml"

# image.yaml file definition that needs to be updated
IMAGE = "image.yaml"

# declared envs on modules.yaml that also needs to have its version updated
ENVS = {"KOGITO_VERSION"}

def yaml_loader():
    """
    default yaml Loader
    :return: yaml object
    """
    yaml = YAML()
    yaml.preserve_quotes = True
    yaml.width = 1024
    yaml.indent(mapping=2, sequence=4, offset=2)
    return yaml

def update_image_version(target_version):
    """
    Update image.yaml version tag.
    :param target_version: version used to update the image.yaml file
    """
    print("Updating Image main file version from file {0} to version {1}".format(IMAGE, target_version))
    try:
        with open(IMAGE) as image:
            data = yaml_loader().load(image)
            if 'version' in data:
                data['version'] = target_version
            else:
                print("Field version not found, returning...")
                return

        with open(IMAGE, 'w') as image:
            yaml_loader().dump(data, image)
    except TypeError as err:
        print("Unexpected error:", err)


def update_image_stream(target_version):
    """
    Update the imagestream file, it will update the tag name, version and image tag.
    :param target_version: version used to update the imagestream file;
    """
    print("Updating ImageStream images version from file {0} to version {1}".format(IMAGE_STREAM, target_version))
    try:
        with open(IMAGE_STREAM) as imagestream:
            data = yaml_loader().load(imagestream)
            for item_index, item in enumerate(data['items'], start=0):
                for tag_index, tag in enumerate(item['spec']['tags'], start=0):
                    data['items'][item_index]['spec']['tags'][tag_index]['name'] = target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['annotations']['version'] = target_version
                    imageDict = str.split(data['items'][item_index]['spec']['tags'][tag_index]['from']['name'], ':')
                    # image name + new version
                    updatedImageName = imageDict[0] + ':' + target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['from']['name'] = updatedImageName

        with open(IMAGE_STREAM, 'w') as imagestream:
            yaml_loader().dump(data, imagestream)

    except TypeError:
        raise


def update_kogito_modules(target_version):
    """
    Update every module.yaml file listed on MODULES as well the envs listed on ENVS.
    :param target_version:  version used to update all needed module.yaml files
    """
    modules_dir = "modules"
    try:

        for module in MODULES:
            module = module + "/module.yaml"
            with open(os.path.join(modules_dir, module)) as m:
                data = yaml_loader().load(m)
                print(
                    "Updating module {0} version from {1} to {2}".format(data['name'], data['version'], target_version))
                data['version'] = target_version

            with open(os.path.join(modules_dir, module), 'w') as m:
                yaml_loader().dump(data, m)

    except TypeError:
        raise

    update_kogito_version_in_modules(target_version)

def update_kogito_version_in_modules(target_version):
    """
    Update every module.yaml file listed on MODULES as well the envs listed on ENVS.
    :param target_version:  version used to update all needed module.yaml files
    """
    modules_dir = "modules"
    try:

        for module in MODULES:
            module = module + "/module.yaml"
            with open(os.path.join(modules_dir, module)) as m:
                data = yaml_loader().load(m)
                if 'envs' in data:
                    for index, env in enumerate(data['envs'], start=0):
                        for target_env in ENVS:
                            if target_env == env['name']:
                                print("Updating module {0} env var {1} with value {2}".format(data['name'], target_env, target_version))
                                data['envs'][index]['value'] = target_version

            with open(os.path.join(modules_dir, module), 'w') as m:
                yaml_loader().dump(data, m)

    except TypeError:
        raise
