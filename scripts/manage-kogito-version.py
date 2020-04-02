#!/usr/bin/python3
# This script will be responsible to help to manage kogito images and modules version, it will update all needed files
# Example of usage:
#   # move the current version to the next one or rcX
#   python manage-kogito-version --bump-to 0.99.0
#
#  The point of truth for vesion is the image.yaml.
#
# Dependencies:
#  ruamel.yaml

import argparse
import os
import re

from ruamel.yaml import YAML

# all kogito-image modules that points to the kogito version.
MODULES = {"kogito-data-index/module.yaml", "kogito-image-dependencies/module.yaml",
           "kogito-infinispan-properties/module.yaml", "kogito-jobs-service/module.yaml",
           "kogito-jq/module.yaml", "kogito-kubernetes-client/module.yaml",
           "kogito-launch-scripts/module.yaml", "kogito-logging/module.yaml",
           "kogito-management-console/module.yaml", "kogito-persistence/module.yaml",
           "kogito-quarkus/module.yaml", "kogito-quarkus-jvm/module.yaml",
           "kogito-quarkus-s2i/module.yaml", "kogito-s2i-core/module.yaml",
           "kogito-springboot/module.yaml", "kogito-springboot-s2i/module.yaml",
           "kogito-system-user/module.yaml"}

# imagestream file that contains all images, this file aldo needs to be updated.
IMAGE_STREAM = "kogito-imagestream.yaml"

# image.yaml file definition that needs to be updated
IMAGE = "image.yaml"

# declared envs on modules.yaml that also needs to have its version updated
ENVS = {"KOGITO_VERSION"}

# behave tests that needs to be update
BEHAVE_TESTS = {"kogito-quarkus-ubi8-s2i.feature", "kogito-springboot-ubi8-s2i.feature",
                "kogito-quarkus-jvm-ubi8.feature", "kogito-springboot-ubi8.feature"}


def yaml_loader():
    '''
    default yaml Loader
    :return: yaml object
    '''
    yaml = YAML()
    yaml.preserve_quotes = True
    yaml.width = 1024
    yaml.indent(mapping=2, sequence=4, offset=2)
    return yaml


def update_image_version(target_version):
    '''
    Update image.yaml version tag.
    :param target_version: version used to update the image.yaml file
    '''
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
    '''
    Update the imagestream file, it will update the tag name, version and image tag.
    :param target_version: version used to update the imagestream file;
    '''
    print("Updating ImageStream images version from file {0} to version {1}".format(IMAGE_STREAM, target_version))
    try:
        with open(IMAGE_STREAM) as imagestream:
            data = yaml_loader().load(imagestream)
            for item_index, item in enumerate(data['items'], start=0):
                for tag_index, tag in enumerate(item['spec']['tags'], start=0):
                    # print(tag)
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
    '''
    Update every module.yaml file listed on MODULES as well the envs listed on ENVS.
    :param target_version:  version used to update all needed module.yaml files
    '''
    modules_dir = "modules"
    try:

        for module in MODULES:
            with open(os.path.join(modules_dir, module)) as m:
                data = yaml_loader().load(m)
                print(
                    "Updating module {0} version from {1} to {2}".format(data['name'], data['version'], target_version))
                data['version'] = target_version
                if 'envs' in data:
                    for index, env in enumerate(data['envs'], start=0):
                        for target_env in ENVS:
                            if target_env == env['name']:
                                data['envs'][index]['value'] = target_version

            with open(os.path.join(modules_dir, module), 'w') as m:
                yaml_loader().dump(data, m)

    except TypeError:
        raise


def update_behave_tests(target_branch):
    '''
    will update the behave tests accordingly.
    If master, the app 8.0.0-SNAPSHOT otherwise use the same value for branch and version
    :param target_branch:
    '''
    base_dir = 'tests/features'
    artifact_version = target_branch
    if 'master' in target_branch:
        artifact_version = '8.0.0-SNAPSHOT'

    # this pattern will look for any occurrencies of using master or using x.x.x
    pattern_branch = re.compile(r'(using master)|(using \s*([\d.]+))')
    quarkus_native_pattern_app_version = re.compile(r'(8.0.0-SNAPSHOT-runner\s)|(\s*([\d.]+)-runner\s)')
    quarkus_pattern_app_version = re.compile(r'(8.0.0-SNAPSHOT-runner.jar)|(\s*([\d.]+)-runner.jar)')
    spring_pattern_app_version = re.compile(r'(8.0.0-SNAPSHOT.jar)|(\s*([\d.]+).jar)')
    for feature in BEHAVE_TESTS:
        print("Updating feature {0}".format(feature))
        with open(os.path.join(base_dir, feature)) as fe:
            updated_value = pattern_branch.sub('using ' + target_branch, fe.read())
            updated_value = quarkus_native_pattern_app_version.sub(artifact_version + '-runner ', updated_value)
            updated_value = quarkus_pattern_app_version.sub(artifact_version + '-runner.jar', updated_value)
            updated_value = spring_pattern_app_version.sub(artifact_version + '.jar', updated_value)

        with open(os.path.join(base_dir, feature), 'w') as fe:
            fe.write(updated_value)


def update_test_apps_clone_repo(target_branch):
    file = 'tests/test-apps/clone-repo.sh'
    if target_branch == 'master':
        # os.system('sed -i \'s/^git fetch origin --tags/#git fetch origin --tags/\' ' + file)
        os.system('sed -i \'s/^git checkout.*/git checkout master/\' ' + file)
    else:
        # os.system('sed -i \'s/^#git fetch origin --tags/git fetch origin --tags/\' ' + file)
        os.system('sed -i \'s/^git checkout.*/git checkout -b '+target_branch+' '+target_branch+'/\' ' + file)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Kogito Version Manager')
    parser.add_argument('--bump-to', dest='bump_to', help='bump everything to the next version')

    args = parser.parse_args()

    if args.bump_to:
        # validate if the provided version is valid.
        # e.g. 1.10.0 or 1.0.0-rc1
        pattern = '\d.\d{1,2}.(\d$|\d-rc\d+$)'
        regex = re.compile(r'\d.\d{1,2}.(\d$|\d-rc\d+$)')
        valid = regex.match(args.bump_to)
        if valid:
            print("Version will be updated to {0}".format(args.bump_to))
            update_image_version(args.bump_to)
            update_image_stream(args.bump_to)
            update_kogito_modules(args.bump_to)
            tests_branch = args.bump_to
            if 'rc' in args.bump_to:
                # update the tests to use kogito examples from master branch and app versions to 8.0.0-SNAPSHOT,
                tests_branch = 'master'
            update_behave_tests(tests_branch)
            update_test_apps_clone_repo(tests_branch)
        else:
            print("Provided version {0} does not match the expected regex - {1}".format(args.bump_to, pattern))
    else:
        print(parser.print_usage())
