#!/usr/bin/python3
#This script defines some common function that are used by manage-kogito-version.py and push-staging.py script


import os
import re

from ruamel.yaml import YAML

# All kogito-image modules that have the kogito version.
MODULES = {"kogito-data-index", "kogito-explainability", "kogito-image-dependencies",
           "kogito-infinispan-properties", "kogito-jobs-service",
           "kogito-jq", "kogito-kubernetes-client",
           "kogito-launch-scripts", "kogito-logging",
           "kogito-management-console", "kogito-persistence",
           "kogito-quarkus", "kogito-quarkus-jvm",
           "kogito-quarkus-s2i", "kogito-s2i-core",
           "kogito-springboot", "kogito-springboot-s2i",
           "kogito-system-user"}
MODULE_FILENAME = "module.yaml"
MODULES_DIR = "modules"

# imagestream file that contains all images, this file aldo needs to be updated.
IMAGE_STREAM_FILENAME = "kogito-imagestream.yaml"
# image.yaml file definition that needs to be updated
IMAGE_FILENAME = "image.yaml"

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
    print("Updating Image main file version from file {0} to version {1}".format(IMAGE_FILENAME, target_version))
    try:
        with open(IMAGE_FILENAME) as image:
            data = yaml_loader().load(image)
            if 'version' in data:
                data['version'] = target_version
            else:
                print("Field version not found, returning...")
                return

        with open(IMAGE_FILENAME, 'w') as image:
            yaml_loader().dump(data, image)
    except TypeError as err:
        print("Unexpected error:", err)


def update_image_stream(target_version):
    """
    Update the imagestream file, it will update the tag name, version and image tag.
    :param target_version: version used to update the imagestream file;
    """
    print("Updating ImageStream images version from file {0} to version {1}".format(IMAGE_STREAM_FILENAME, target_version))
    try:
        with open(IMAGE_STREAM_FILENAME) as imagestream:
            data = yaml_loader().load(imagestream)
            for item_index, item in enumerate(data['items'], start=0):
                for tag_index, tag in enumerate(item['spec']['tags'], start=0):
                    data['items'][item_index]['spec']['tags'][tag_index]['name'] = target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['annotations']['version'] = target_version
                    imageDict = str.split(data['items'][item_index]['spec']['tags'][tag_index]['from']['name'], ':')
                    # image name + new version
                    updatedImageName = imageDict[0] + ':' + target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['from']['name'] = updatedImageName

        with open(IMAGE_STREAM_FILENAME, 'w') as imagestream:
            yaml_loader().dump(data, imagestream)

    except TypeError:
        raise

def get_all_module_dirs():
    modules = []

    # r=>root, d=>directories, f=>files
    for r, d, f in os.walk(MODULES_DIR):
        for item in f:
            if MODULE_FILENAME == item:
                modules.append(os.path.dirname(os.path.join(r, item)))

    return modules

def get_kogito_module_dirs():
    modules = []

    for moduleName in MODULES:
        modules.append(os.path.join(MODULES_DIR, moduleName))

    return modules

def update_modules_version(target_version):
    """
    Update every Kogito module.yaml to the given version.
    :param target_version: version used to update all Kogito module.yaml files
    """
    for module_dir in get_kogito_module_dirs():
        update_module_version(module_dir, target_version)

def update_module_version(moduleDir, target_version):
    """
    Set Kogito module.yaml to given version.
    :param target_version: version to set into the module
    """
    try:
        moduleFile = os.path.join(moduleDir, "module.yaml")
        with open(moduleFile) as module:
            data = yaml_loader().load(module)
            print(
                "Updating module {0} version from {1} to {2}".format(data['name'], data['version'], target_version))
            data['version'] = target_version

        with open(moduleFile, 'w') as module:
            yaml_loader().dump(data, module)

    except TypeError:
        raise


def update_kogito_version_env_in_modules(target_version):
    """
    Update all modules which contains the `KOGITO_VERSION` env var.
    :param target_version: kogito version used to update all modules which contains the `KOGITO_VERSION` env var
    """
    update_env_in_all_modules("KOGITO_VERSION", target_version)

def update_env_in_all_modules(envKey, envValue):
    """
    Update all modules which contains the given envKey to the given envValue.
    :param envKey: Environment variable key to update
    :param envValue: Environment variable value to set
    """
    for module_dir in get_all_module_dirs():
        update_env_in_module(module_dir, envKey, envValue)

def update_env_in_module(module_dir, envKey, envValue):
    """
    Update a module if it contains the given envKey to the given envValue.
    :param envKey: Environment variable key to update if exists
    :param envValue: Environment variable value to set if exists
    """
    try:
        moduleFile = os.path.join(module_dir, "module.yaml")
        changed = False
        with open(moduleFile) as module:
            data = yaml_loader().load(module)
            if 'envs' in data:
                for index, env in enumerate(data['envs'], start=0):
                    if envKey == env['name']:
                        print("Updating module {0} env var {1} with value {2}".format(data['name'], envKey, envValue))
                        data['envs'][index]['value'] = envValue
                        changed = True

        if (changed):
            with open(moduleFile, 'w') as module:
                yaml_loader().dump(data, module)

    except TypeError:
        raise

if __name__ == "__main__":
    for m in get_kogito_module_dirs():
        print("module {}".format(m))