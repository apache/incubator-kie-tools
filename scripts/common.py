#!/usr/bin/python3
# This script defines some common function that are used by manage-kogito-version.py and push-staging.py script


import os
import re

from ruamel.yaml import YAML

MODULE_FILENAME = "module.yaml"
MODULES_DIR = "modules"

COMMUNITY_PREFIX = 'kogito-'
PRODUCT_PREFIX = 'logic-'

# imagestream file that contains all images, this file aldo needs to be updated.
IMAGE_STREAM_FILENAME = "kogito-imagestream.yaml"
PROD_IMAGE_STREAM_FILENAME = "logic-imagestream.yaml"
# image.yaml file definition that needs to be updated
IMAGE_FILENAME = "image.yaml"
ARTIFACTS_VERSION_ENV_KEY = "KOGITO_VERSION"

QUARKUS_VERSION_ENV_KEY = "QUARKUS_VERSION"
QUARKUS_VERSION_LABEL_NAME = "org.quarkus.version"

# behave tests that needs to be updated
BEHAVE_BASE_DIR = 'tests/features'

CLONE_REPO_SCRIPT = 'tests/test-apps/clone-repo.sh'
SETUP_MAVEN_SCRIPT = 'scripts/setup-maven.sh'

SUPPORTING_SERVICES_IMAGES = {"kogito-data-index-ephemeral", "kogito-data-index-infinispan",
                              "kogito-data-index-mongodb", "kogito-data-index-oracle",
                              "kogito-data-index-postgresql", "kogito-explainability",
                              "kogito-jit-runner", "kogito-jobs-service-ephemeral",
                              "kogito-jobs-service-infinispan", "kogito-jobs-service-mongodb",
                              "kogito-jobs-service-postgresql", "kogito-jobs-service-allinone",
                              "kogito-management-console", "kogito-task-console",
                              "kogito-trusty-infinispan", "kogito-trusty-postgresql",
                              "kogito-trusty-redis", "kogito-trusty-ui"}

PROD_SUPPORTING_SERVICES_IMAGES = {"logic-data-index-ephemeral-rhel8"}


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
    Update logic-*-overrides.yaml files version tag.
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
    :param prod: if the imagestream is the prod version
    :param target_version: version used to update the imagestream file;
    """
    image_stream_filename = IMAGE_STREAM_FILENAME
    if prod:
        image_stream_filename = PROD_IMAGE_STREAM_FILENAME
    print("Updating ImageStream images version from file {0} to version {1}".format(image_stream_filename,
                                                                                    target_version))
    try:
        with open(image_stream_filename) as imagestream:
            data = yaml_loader().load(imagestream)
            for item_index, item in enumerate(data['items'], start=0):
                for tag_index, tag in enumerate(item['spec']['tags'], start=0):
                    data['items'][item_index]['spec']['tags'][tag_index]['name'] = target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['annotations']['version'] = target_version
                    image_dict = str.split(data['items'][item_index]['spec']['tags'][tag_index]['from']['name'], ':')
                    # image name + new version
                    updated_image_name = image_dict[0] + ':' + target_version
                    data['items'][item_index]['spec']['tags'][tag_index]['from']['name'] = updated_image_name

        with open(image_stream_filename, 'w') as imagestream:
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
    Retrieve the Logic module directories
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


def get_supporting_services_images(is_prod_image):
    """
    Retrieve the Supporting Services images' names
    """
    if is_prod_image:
        return PROD_SUPPORTING_SERVICES_IMAGES
    return SUPPORTING_SERVICES_IMAGES


def is_supporting_services_image(image_name, prod=False):
    """
    Raise an error if the given image is not a supporting service
    """
    found = False
    if prod:
        if image_name not in PROD_SUPPORTING_SERVICES_IMAGES:
            raise RuntimeError('{} is not a productized supporting service'.format(image_name))
    else:
        if image_name not in SUPPORTING_SERVICES_IMAGES:
            raise RuntimeError('{} is not a supporting service'.format(image_name))


def update_modules_version(target_version, prod=False):
    """
    Update every Kogito module.yaml to the given version.
    :param prod: if the module to be updated is prod version.
    :param target_version: version used to update all Kogito module.yaml files
    """
    modules = []
    if prod:
        modules = get_prod_module_dirs()
    else:
        modules = get_community_module_dirs()

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

def update_images_env_value(env_name, new_value, prod=False):
    """
    Update the given env name for all images with the given new value.
    :param env_name: environment variable name to update
    :param new_value: new value to set
    :param prod: if the module to be updated is prod version.
    """
    images = []
    if prod:
        images = get_prod_images()
    else:
        images = get_community_images()

    for image_name in images:
        image_filename = "{}-overrides.yaml".format(image_name)
        update_env_value(image_filename, env_name, new_value)

def update_images_label_value(label_name, new_value, prod=False):
    """
    Update the given label name for all images with the given new value.
    :param label_name: label name to update
    :param new_value: new value to set
    :param prod: if the module to be updated is prod version.
    """
    images = []
    if prod:
        images = get_prod_images()
    else:
        images = get_community_images()

    for image_name in images:
        image_filename = "{}-overrides.yaml".format(image_name)
        update_label_value(image_filename, label_name, new_value)

def update_modules_env_value(env_name, new_value, prod=False):
    """
    Update the given environment variable name for all Kogito modules with the given new value.
    :param env_name: label name to update
    :param new_value: new value to set
    :param prod: if the module to be updated is prod version.
    """
    modules = []
    if prod:
        modules = get_prod_module_dirs()
    else:
        modules = get_community_module_dirs()

    for module_dir in modules:
        module_file = os.path.join(module_dir, "module.yaml")
        update_env_value(module_file, env_name, new_value)

def update_modules_label_value(label_name, new_value, prod=False):
    """
    Update the given label name for all Kogito modules with the given new value.
    :param label_name: label name to update
    :param new_value: new value to set
    :param prod: if the module to be updated is prod version.
    """
    modules = []
    if prod:
        modules = get_prod_module_dirs()
    else:
        modules = get_community_module_dirs()

    for module_dir in modules:
        module_file = os.path.join(module_dir, "module.yaml")
        update_label_value(module_file, label_name, new_value)

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

def update_quarkus_version_env_in_image(quarkus_version):
    """
    Update `QUARKUS_VERSION` env var in image.yaml.
    :param quarkus_version: quarkus version used to update image.yaml which contains the `QUARKUS_VERSION` env var
    """
    update_env_value(IMAGE_FILENAME, QUARKUS_VERSION_ENV_KEY, quarkus_version)

def update_artifacts_version_env_in_image(artifacts_version):
    """
    Update `KOGITO_VERSION` env var in image.yaml.
    :param artifacts_version: kogito version used to update image.yaml which contains the `KOGITO_VERSION` env var
    """
    update_env_value(IMAGE_FILENAME, ARTIFACTS_VERSION_ENV_KEY, artifacts_version)

def update_examples_ref_in_behave_tests(examples_ref):
    """
    Update examples git reference into behave tests
    :param examples_ref: kogito-examples reference
    """
    print("Set examples_ref {} in behave tests".format(examples_ref))
    # this pattern will look for any occurrences of using nightly-main or using nightly-x.x.x or using x.x.x
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
    pattern = re.compile(
        '\|[\s]*KOGITO_VERSION[\s]*\|[\s]*(([\d.]+.x)|([\d.]+)[\s]*|([\d.]+-SNAPSHOT)|([\d.]+.Final)|([\d.]+\.redhat-[\d]+))[\s]*\|')
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


def update_maven_repo_in_behave_tests(repo_url, replace_jboss_repository):
    """
    Update maven repository into behave tests
    :param repo_url: Maven repository url
    :param replace_jboss_repository: Set to true if default Jboss repository needs to be overriden
    """
    print("Set maven repo {} in behave tests".format(repo_url))
    pattern = re.compile('\|\s*variable[\s]*\|[\s]*value[\s]*\|')
    env_var_key = "MAVEN_REPO_URL"
    if replace_jboss_repository:
        env_var_key = "JBOSS_MAVEN_REPO_URL"
    replacement = "| variable | value |\n      | {} | {} |\n      | MAVEN_DOWNLOAD_OUTPUT | true |".format(env_var_key,
                                                                                                           repo_url)
    update_in_behave_tests(pattern, replacement)


def update_maven_mirror_url_in_quarkus_plugin_behave_tests(repo_url):
    """
    Update maven repository into behave tests
    :param repo_url: Maven repository url
    """
    print("Set maven repo {} in quarkus plugin behave tests".format(repo_url))
    pattern = re.compile(
        '(Kogito Maven archetype.*(?<!springboot)\r?\n\s*Given.*\r?\n\s*)\|\s*variable[\s]*\|[\s]*value[\s]*\|')
    replacement = "\g<1>| variable | value |\n      | {} | {} |\n      | MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE | true |\n      | DEBUG | true |".format(
        "MAVEN_MIRROR_URL", repo_url)
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


def update_maven_repo_in_setup_maven(repo_url, replace_jboss_repository):
    """
    Update maven repository into setup-maven.sh script
    :param repo_url: Maven repository url
    :param replace_jboss_repository: Set to true if default Jboss repository needs to be overridden
    """
    print("Set maven repo {} in setup-maven script".format(repo_url))
    pattern = ""
    replacement = ""
    if replace_jboss_repository:
        pattern = re.compile(r'(export JBOSS_MAVEN_REPO_URL=.*)')
        replacement = 'export JBOSS_MAVEN_REPO_URL="{}"'.format(repo_url)
    else:
        pattern = re.compile(r'(# export MAVEN_REPO_URL=.*)')
        replacement = 'export MAVEN_REPO_URL="{}"'.format(repo_url)
    update_in_file(SETUP_MAVEN_SCRIPT, pattern, replacement)

def update_env_value(filename, env_name, env_value):
    """
    Update environment value into the given yaml module/image file
    :param filename: filename to update
    :param env_name: environment variable name to update
    :param env_value: value to set
    """
    try:
        with open(filename) as yaml_file:
            data = yaml_loader().load(yaml_file)
            if 'envs' in data:
                for index, env in enumerate(data['envs'], start=0):
                    if env['name'] == env_name:
                        print("Updating {0} label {1} with value {2}".format(filename, env_name,
                                                                                    env_value))
                        if 'value' in data['envs'][index]: # Do not update if no value already defined
                            data['envs'][index]['value'] = env_value

        with open(filename, 'w') as yaml_file:
            yaml_loader().dump(data, yaml_file)

    except TypeError:
        raise

def update_label_value(filename, label_name, label_value):
    """
    Update label value into the given yaml module/image file
    :param filename: filename to update
    :param label_name: label name to update
    :param label_value: value to set
    """
    try:
        with open(filename) as yaml_file:
            data = yaml_loader().load(yaml_file)
            if 'labels' in data:
                for index, env in enumerate(data['labels'], start=0):
                    if env['name'] == label_name:
                        print("Updating {0} label {1} with value {2}".format(filename, label_name,
                                                                                    label_value))
                        if 'value' in data['labels'][index]: # Do not update if no value already defined
                            data['labels'][index]['value'] = label_value

        with open(filename, 'w') as yaml_file:
            yaml_loader().dump(data, yaml_file)

    except TypeError:
        raise

def ignore_maven_self_signed_certificate_in_setup_maven():
    """
    Sets the environment variable to ignore the self-signed certificates in maven
    """
    print("Setting MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE env in setup maven")
    pattern = re.compile(r'(# MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=.*)')
    replacement = "MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=true"
    update_in_file(SETUP_MAVEN_SCRIPT, pattern, replacement)


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
