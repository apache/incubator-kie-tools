#!/usr/bin/python3
#This script defines some common function that are used by manage-kogito-version.py and push-staging.py script


import os
import re

from ruamel.yaml import YAML

# All kogito-image modules that have the kogito version.
MODULES = {"kogito-data-index", "kogito-trusty", 
           "kogito-explainability", "kogito-image-dependencies",
           "kogito-jobs-service",
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

# behave tests that needs to be update
BEHAVE_BASE_DIR = 'tests/features'
BEHAVE_TESTS = {"kogito-quarkus-ubi8-s2i.feature", "kogito-springboot-ubi8-s2i.feature",
                "kogito-quarkus-jvm-ubi8.feature", "kogito-springboot-ubi8.feature"}

CLONE_REPO_SCRIPT='tests/test-apps/clone-repo.sh'

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
    """
    Retrieve the module directories
    """
    modules = []

    # r=>root, d=>directories, f=>files
    for r, d, f in os.walk(MODULES_DIR):
        for item in f:
            if MODULE_FILENAME == item:
                modules.append(os.path.dirname(os.path.join(r, item)))

    return modules

def get_kogito_module_dirs():
    """
    Retrieve the Kogito module directories
    """
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


def update_artifacts_version_env_in_modules(artifacts_version):
    """
    Update all modules which contains the `KOGITO_VERSION` env var.
    :param target_version: kogito version used to update all modules which contains the `KOGITO_VERSION` env var
    """
    update_env_in_all_modules("KOGITO_VERSION", artifacts_version)

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

def update_artifacts_version_in_python_scripts(artifacts_version):
    """
    Update python scripts with the default artifacts version
    :param artifacts_version: artifacts version to set
    """
    print("Set artifacts_version {} in python scripts".format(artifacts_version))
    pattern = re.compile(r'(ARTIFACTS_VERSION=.*)')
    replacement = 'ARTIFACTS_VERSION="{}"'.format(artifacts_version)
    update_in_file("scripts/update-maven-artifacts.py", pattern, replacement)

def update_examples_ref_in_behave_tests(examples_ref):
    """
    Update examples git reference into behave tests
    :param examples_ref: kogito-examples reference
    """
    print("Set examples_ref {} in behave tests".format(examples_ref))
    # this pattern will look for any occurrences of using master or using x.x.x
    pattern = re.compile(r'(using master)|(using \s*([\d.]+.x))|(using \s*([\d.]+))')
    replacement = 'using {}'.format(examples_ref)
    update_in_behave_tests(pattern, replacement)

def update_examples_uri_in_behave_tests(examples_uri):
    """
    Update examples uri into behave tests
    :param examples_uri: kogito-examples uri
    """
    print("Set examples_uri {} in behave tests".format(examples_uri))
    # pattern to get the default examples uri
    pattern = re.compile(r'(https://github.com/kiegroup/kogito-examples.git)')
    replacement = examples_uri
    update_in_behave_tests(pattern, replacement)

def update_artifacts_version_in_behave_tests(artifacts_version):
    """
    Update artifacts version into behave tests
    :param artifacts_version: artifacts version to set
    """
    print("Set artifacts_version {} in behave tests".format(artifacts_version))
    # pattern to change the KOGITO_VERSION
    pattern = re.compile('\|[\s]*KOGITO_VERSION[\s]*\|[\s]*(([\d.]+.x)|([\d.]+)[\s]*|([\d.]+-SNAPSHOT))[\s]*\|')
    replacement = '| KOGITO_VERSION | {} | '.format(artifacts_version)
    update_in_behave_tests(pattern, replacement)

def update_maven_repo_in_behave_tests(repo_url, replaceJbossRepository):
    """
    Update maven repository into behave tests
    :param repo_url: Maven repository url
    :param replaceJbossRepository: Set to true if default Jboss repository needs to be overriden
    """
    print("Set maven repo {} in behave tests".format(repo_url))
    pattern = re.compile('\|\s*variable[\s]*\|[\s]*value[\s]*\|')
    envVarKey = "MAVEN_REPO_URL"
    if replaceJbossRepository:
        envVarKey = "JBOSS_MAVEN_REPO_URL"
    replacement = "| variable | value |\n      | {} | {} |\n      | MAVEN_DOWNLOAD_OUTPUT | true |".format(envVarKey, repo_url)
    update_in_behave_tests(pattern, replacement)

def update_in_behave_tests(pattern, replacement):
    """
    Update all behave tests files
    :param pattern: Pattern to look for into file
    :param replacement: What to put instead if pattern found
    """
    for feature in BEHAVE_TESTS:
        update_in_file(os.path.join(BEHAVE_BASE_DIR, feature), pattern, replacement)

def update_examples_ref_in_clone_repo(examples_ref):
    """
    Update examples git reference into clone-repo.sh script
    :param examples_ref: kogito-examples reference
    """
    print("Set examples_ref {} in clone-repo script".format(examples_ref))
    pattern = re.compile(r'(git checkout.*)')
    replacement = "git checkout master"
    if examples_ref != 'master':
        replacement = "git checkout -b {} {}".format(examples_ref, examples_ref)
    update_in_file(CLONE_REPO_SCRIPT, pattern, replacement)

def update_examples_uri_in_clone_repo(examples_uri):
    """
    Update examples uri into clone-repo.sh script
    :param examples_uri: kogito-examples uri
    """
    print("Set examples_uri {} in clone-repo script".format(examples_uri))
    pattern = re.compile(r'(git clone.*)')
    replacement = "git clone {}".format(examples_uri)
    update_in_file(CLONE_REPO_SCRIPT, pattern, replacement)

def update_maven_repo_in_clone_repo(repo_url, replaceJbossRepository):
    """
    Update maven repository into clone-repo.sh script
    :param repo_url: Maven repository url
    :param replaceJbossRepository: Set to true if default Jboss repository needs to be overriden
    """
    print("Set maven repo {} in clone-repo script".format(repo_url))
    pattern = ""
    replacement = ""
    if replaceJbossRepository:
        pattern = re.compile(r'(export JBOSS_MAVEN_REPO_URL=.*)')
        replacement = 'export JBOSS_MAVEN_REPO_URL="{}"'.format(repo_url)
    else :
        pattern = re.compile(r'(# export MAVEN_REPO_URL=.*)')
        replacement = 'export MAVEN_REPO_URL="{}"'.format(repo_url)
    update_in_file(CLONE_REPO_SCRIPT, pattern, replacement)

def update_in_file(file, pattern, replacement):
    """
    Update in given file
    :param file: file to update
    :param pattern: Pattern to look for into file
    :param replacement: What to put instead if pattern found
    """
    with open(file) as fe:
        updated_value = pattern.sub(replacement, fe.read())
    with open(file, 'w') as fe:
        fe.write(updated_value)
    
if __name__ == "__main__":
    for m in get_kogito_module_dirs():
        print("module {}".format(m))