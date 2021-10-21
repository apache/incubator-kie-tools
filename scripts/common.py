#!/usr/bin/python3
# This script defines some common function that are used by manage-kogito-version.py and push-staging.py script


import os
import re

from ruamel.yaml import YAML

MODULE_FILENAME = "module.yaml"
MODULES_DIR = "modules"

COMMUNITY_PREFIX = 'kogito-'
PRODUCT_PREFIX = 'rhpam-'

# imagestream file that contains all images, this file aldo needs to be updated.
IMAGE_STREAM_FILENAME = "kogito-imagestream.yaml"
PROD_IMAGE_STREAM_FILENAME = "rhpam-kogito-imagestream.yaml"
# image.yaml file definition that needs to be updated
IMAGE_FILENAME = "image.yaml"
ARTIFACTS_VERSION_ENV_KEY = "KOGITO_VERSION"

# behave tests that needs to be updated
BEHAVE_BASE_DIR = 'tests/features'

CLONE_REPO_SCRIPT = 'tests/test-apps/clone-repo.sh'


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


def update_community_image_version(target_version):
    """
    Update image.yaml version tag.
    :param target_version: version used to update the image.yaml file
    """
    update_image_version_tag_in_yaml_file(target_version, IMAGE_FILENAME)

def update_prod_image_version(target_version):
    """
    Update rhpam-*-overrides.yaml files version tag.
    :param target_version: version used to update the files
    """
    for img in sorted(get_prod_images()):
        file = "{}-overrides.yaml".format(img)
        update_image_version_tag_in_yaml_file(target_version, file)

def update_image_version_tag_in_yaml_file(target_version, yaml_file):
    """
    Update root version tag in yaml file.
    :param target_version: version to set
    :param yaml_file: yaml file to update
    """
    print("Updating Image main file version from file {0} to version {1}".format(yaml_file, target_version))
    try:
        with open(yaml_file) as image:
            data = yaml_loader().load(image)
            if 'version' in data:
                data['version'] = target_version
            else:
                print("Field version not found, returning...")
                return

        with open(yaml_file, 'w') as image:
            yaml_loader().dump(data, image)
    except TypeError as err:
        print("Unexpected error:", err)

def update_image_stream(target_version, prod=False):
    """
    Update the imagestream file, it will update the tag name, version and image tag.
    :param target_version: version used to update the imagestream file;
    """
    imageStreamFilename = IMAGE_STREAM_FILENAME
    if prod:
        imageStreamFilename = PROD_IMAGE_STREAM_FILENAME
    print("Updating ImageStream images version from file {0} to version {1}".format(imageStreamFilename,
                                                                                    target_version))
    try:
        with open(imageStreamFilename) as imagestream:
            data = yaml_loader().load(imagestream)
            for item_index, item in enumerate(data['items'], start=0):
                for tag_index, tag in enumerate(item['spec']['tags'], start=0):
                    data['items'][item_index]['spec']['tags'][tag_index]['name'] = target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['annotations']['version'] = target_version
                    image_dict = str.split(data['items'][item_index]['spec']['tags'][tag_index]['from']['name'], ':')
                    # image name + new version
                    updated_image_name = image_dict[0] + ':' + target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['from']['name'] = updated_image_name

        with open(imageStreamFilename, 'w') as imagestream:
            yaml_loader().dump(data, imagestream)

    except TypeError:
        raise


def get_all_module_dirs(prefix):
    """
    Retrieve the module directories
    """
    modules = []

    # r=>root, d=>directories, f=>files
    for r, d, f in os.walk(MODULES_DIR):
        for item in f:
            if MODULE_FILENAME == item:
                path = os.path.dirname(os.path.join(r, item))
                if os.path.basename(path).startswith(prefix):
                    modules.append(path)

    return modules


def get_community_module_dirs():
    """
    Retrieve the Kogito module directories
    """
    return get_all_module_dirs(COMMUNITY_PREFIX)

def get_prod_module_dirs():
    """
    Retrieve the RHPAM module directories
    """
    return get_all_module_dirs(PRODUCT_PREFIX)

def get_images(prefix):
    """
    Retrieve the Kogito images' files
    """
    images = []

    # r=>root, d=>directories, f=>files
    for r, d, f in os.walk("."):
        for item in f:
            if re.compile(r'.*-overrides.yaml').match(item):
                if item.startswith(prefix):
                    images.append(item.replace("-overrides.yaml", ''))

    return images

def get_community_images():
    """
    Retrieve the Community images' names
    """
    return get_images(COMMUNITY_PREFIX)

def get_prod_images():
    """
    Retrieve the Prod images' names
    """
    return get_images(PRODUCT_PREFIX)

def update_modules_version(target_version, prod=False):
    """
    Update every Kogito module.yaml to the given version.
    :param target_version: version used to update all Kogito module.yaml files
    """
    modules = []
    if prod:
        modules = get_prod_module_dirs()
    else:
        get_community_module_dirs()
    
    for module_dir in modules:
        update_module_version(module_dir, target_version)


def update_module_version(module_dir, target_version):
    """
    Set Kogito module.yaml to given version.
    :param module_dir: directory where cekit modules are hold
    :param target_version: version to set into the module
    """
    try:
        module_file = os.path.join(module_dir, "module.yaml")
        with open(module_file) as module:
            data = yaml_loader().load(module)
            print(
                "Updating module {0} version from {1} to {2}".format(data['name'], data['version'], target_version))
            data['version'] = target_version

        with open(module_file, 'w') as module:
            yaml_loader().dump(data, module)

    except TypeError:
        raise


def retrieve_artifacts_version():
    """
    Retrieve the artifacts version from envs in main image.yaml
    """
    try:
        with open(IMAGE_FILENAME) as imageFile:
            data = yaml_loader().load(imageFile)
            for index, env in enumerate(data['envs'], start=0):
                if env['name'] == ARTIFACTS_VERSION_ENV_KEY:
                    return data['envs'][index]['value']

    except TypeError:
        raise


def update_artifacts_version_env_in_image(artifacts_version):
    """
    Update `KOGITO_VERSION` env var in image.yaml.
    :param artifacts_version: kogito version used to update image.yaml which contains the `KOGITO_VERSION` env var
    """
    try:
        with open(IMAGE_FILENAME) as imageFile:
            data = yaml_loader().load(imageFile)
            for index, env in enumerate(data['envs'], start=0):
                if env['name'] == ARTIFACTS_VERSION_ENV_KEY:
                    print("Updating image.yaml env var {0} with value {1}".format(ARTIFACTS_VERSION_ENV_KEY,
                                                                                  artifacts_version))
                    data['envs'][index]['value'] = artifacts_version

        with open(IMAGE_FILENAME, 'w') as imageFile:
            yaml_loader().dump(data, imageFile)

    except TypeError:
        raise


def update_examples_ref_in_behave_tests(examples_ref):
    """
    Update examples git reference into behave tests
    :param examples_ref: kogito-examples reference
    """
    print("Set examples_ref {} in behave tests".format(examples_ref))
    # this pattern will look for any occurrences of using master or using x.x.x
    pattern = re.compile(r'(using nightly-main)|(using nightly-\s*([\d.]+.x))|(using \s*([\d.]+[.x]?))')
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
    pattern = re.compile('\|[\s]*KOGITO_VERSION[\s]*\|[\s]*(([\d.]+.x)|([\d.]+)[\s]*|([\d.]+-SNAPSHOT)|([\d.]+.Final)|([\d.]+\.redhat-[\d]+))[\s]*\|')
    replacement = '| KOGITO_VERSION | {} | '.format(artifacts_version)
    update_in_behave_tests(pattern, replacement)

def update_runtime_image_in_behave_tests(runtime_image_name, image_suffix):
    """
    Update a runtime image into behave tests
    :param runtime_image_name: new full tag name of the runtime image
    :param image_suffix: suffix of the runtime image to update
    """
    print("Set {0} runtime image to {1} in behave tests".format(image_suffix, runtime_image_name))
    # pattern to change the KOGITO_VERSION
    pattern = re.compile(r'(runtime-image quay.io/kiegroup/kogito-runtime-{}:latest)'.format(image_suffix))
    replacement = 'runtime-image {}'.format(runtime_image_name)
    update_in_behave_tests(pattern, replacement)

    pattern = re.compile(r'(runtime-image rhpam-7/rhpam-kogito-runtime-{}-rhel8:latest)'.format(image_suffix))
    replacement = 'runtime-image {}'.format(runtime_image_name)
    update_in_behave_tests(pattern, replacement)


def update_maven_repo_in_behave_tests(repo_url, replaceJbossRepository):
    """
    Update maven repository into behave tests
    :param repo_url: Maven repository url
    :param replaceJbossRepository: Set to true if default Jboss repository needs to be overriden
    """
    print("Set maven repo {} in behave tests".format(repo_url))
    pattern = re.compile('\|\s*variable[\s]*\|[\s]*value[\s]*\|')
    env_var_key = "MAVEN_REPO_URL"
    if replaceJbossRepository:
        env_var_key = "JBOSS_MAVEN_REPO_URL"
    replacement = "| variable | value |\n      | {} | {} |\n      | MAVEN_DOWNLOAD_OUTPUT | true |".format(env_var_key,
                                                                                                           repo_url)
    update_in_behave_tests(pattern, replacement)


def ignore_maven_self_signed_certificate_in_behave_tests():
    """
    Sets the environment variable to ignore the self-signed certificates in maven
    """
    print("Setting MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE env in behave tests")
    pattern = re.compile('\|\s*variable[\s]*\|[\s]*value[\s]*\|')
    replacement = "| variable | value |\n      | MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE | true |"
    update_in_behave_tests(pattern, replacement)


def update_in_behave_tests(pattern, replacement):
    """
    Update all behave tests files
    :param pattern: Pattern to look for into file
    :param replacement: What to put instead if pattern found
    """
    for f in os.listdir(BEHAVE_BASE_DIR):
        if f.endswith('.feature'):
            update_in_file(os.path.join(BEHAVE_BASE_DIR, f), pattern, replacement)


def update_examples_ref_in_clone_repo(examples_ref):
    """
    Update examples git reference into clone-repo.sh script
    :param examples_ref: kogito-examples reference
    """
    print("Set examples_ref {} in clone-repo script".format(examples_ref))
    pattern = re.compile(r'(git checkout.*)')
    replacement = "git checkout main"
    if examples_ref != 'main':
        replacement = "git checkout -b {0} origin/{1}".format(examples_ref, examples_ref)
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


def update_maven_repo_in_clone_repo(repo_url, replace_jboss_repository):
    """
    Update maven repository into clone-repo.sh script
    :param repo_url: Maven repository url
    :param replace_jboss_repository: Set to true if default Jboss repository needs to be overridden
    """
    print("Set maven repo {} in clone-repo script".format(repo_url))
    pattern = ""
    replacement = ""
    if replace_jboss_repository:
        pattern = re.compile(r'(export JBOSS_MAVEN_REPO_URL=.*)')
        replacement = 'export JBOSS_MAVEN_REPO_URL="{}"'.format(repo_url)
    else:
        pattern = re.compile(r'(# export MAVEN_REPO_URL=.*)')
        replacement = 'export MAVEN_REPO_URL="{}"'.format(repo_url)
    update_in_file(CLONE_REPO_SCRIPT, pattern, replacement)

def ignore_maven_self_signed_certificate_in_clone_repo():
    """
    Sets the environment variable to ignore the self-signed certificates in maven
    """
    print("Setting MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE env in clone repo")
    pattern = re.compile(r'(# MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=.*)')
    replacement = "MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=true"
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
    print("Community modules:")
    for m in get_community_module_dirs():
        print("module {}".format(m))
    print("\nProd modules:")
    for m in get_prod_module_dirs():
        print("module {}".format(m))
