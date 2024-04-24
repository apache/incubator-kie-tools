import os
import re
import glob

from ruamel.yaml import YAML

MODULE_FILENAME = "module.yaml"
MODULES_DIR = "modules"
PROJECT_VERSIONS_MODULE = "modules/kogito-project-versions/module.yaml"

KOGITO_VERSION_ENV_KEY = "KOGITO_VERSION"
KOGITO_VERSION_LABEL_NAME = "org.kie.kogito.version"

QUARKUS_PLATFORM_VERSION_ENV_KEY = "QUARKUS_PLATFORM_VERSION"
QUARKUS_PLATFORM_VERSION_LABEL_NAME = "io.quarkus.platform.version"


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

def retrieve_version():
    """
    Retrieve the project version from project data file
    """
    return get_project_versions_module_data()['version']

def get_all_images(source_folder = None):
    """
    Retrieve all images in current dir
    """
    root_folder = "." if source_folder is None else source_folder
    return glob.glob("{}/*-image.yaml".format(root_folder))

def get_project_versions_module_data():
    """
    Get a specific field value from project versions module file
    """
    try:
        project_versions_module_file = os.path.join(PROJECT_VERSIONS_MODULE)
        with open(project_versions_module_file) as project_versions_data:
            return yaml_loader().load(project_versions_data)

    except TypeError:
        raise

def get_all_module_dirs(source_folder = None):
    """
    Retrieve the module directories
    :param source_folder: folder where resources are stored
    """

    root_folder = "." if source_folder is None else source_folder
    modules_dir = "{}/{}".format(root_folder, MODULES_DIR)

    modules = []

    # r=>root, d=>directories, f=>files
    for r, d, f in os.walk(modules_dir):
        for item in f:
            if MODULE_FILENAME == item:
                modules.append(os.path.dirname(os.path.join(r, item)))
                print("[kogito-swf-common] Processing modules of '" + os.path.dirname(os.path.join(r, item)) + "'")

    return modules

def update_image_and_modules_version(target_version, source_folder = None):
    """
    Update every Kogito module.yaml to the given version.
    :param target_version: version used to update all Kogito module.yaml files
    :param source_folder: folder where resources are stored
    """
    print("Images and Modules version will be updated to {0}".format(target_version))
    update_images_version(target_version, source_folder)
    update_modules_version(target_version, source_folder)


def update_modules_version(target_version, source_folder = None):
    """
    Update every Kogito module.yaml to the given version.
    :param target_version: version used to update all Kogito module.yaml files
    :param source_folder: folder where resources are stored
    """
    modules = get_all_module_dirs(source_folder)

    for module_dir in modules:
        update_module_version(module_dir, target_version)


def update_module_version(module_dir, target_version):
    """
    Set Kogito module.yaml to given version.
    :param module_dir: directory where cekit modules are hold
    :param target_version: version to set into the module
    """
    try:
        module_file = os.path.join(module_dir, MODULE_FILENAME)
        with open(module_file) as module:
            data = yaml_loader().load(module)
            print("Updating module {0} version from {1} to {2}".format(data['name'], data['version'], target_version))
            data['version'] = target_version

        with open(module_file, 'w') as module:
            yaml_loader().dump(data, module)

    except TypeError:
        raise

def update_images_version(target_version, source_folder = None):
    """
    Update image.yml files version tag.
    :param target_version: version used to update
    :param source_folder: folder where resources are stored
    """
    for image_filename in get_all_images(source_folder):
        update_image_version_tag_in_yaml_file(target_version, image_filename)


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
            update_field_in_dict(data, 'version', target_version)

        with open(yaml_file, 'w') as image:
            yaml_loader().dump(data, image)
    except TypeError as err:
        print("Unexpected error:", err)

def update_kogito_platform_version(kogito_platform_version):
    """
    Update kogito_platform_version version into images/modules
    :param kogito_platform_version: kogito version to set
    """
    print("Setting Kogito Platform version: " + kogito_platform_version)
    update_env_value(KOGITO_VERSION_ENV_KEY, kogito_platform_version)
    update_label_value(KOGITO_VERSION_LABEL_NAME, kogito_platform_version)

def update_quarkus_platform_version(quarkus_platform_version):
    """
    Update quarkus_platform_version version into images/modules
    :param quarkus_platform_version: quarkus version to set
    """
    print("Setting Quarkus version: " + quarkus_platform_version)
    update_env_value(QUARKUS_PLATFORM_VERSION_ENV_KEY, quarkus_platform_version)
    update_label_value(QUARKUS_PLATFORM_VERSION_LABEL_NAME, quarkus_platform_version)

def update_env_value(env_name, env_value):
    """
    Update environment value into the given yaml module/image file
    :param env_name: environment variable name to update
    :param env_value: value to set
    """

    images = get_all_images()
    modules = get_all_module_dirs()

    for image_filename in images:
        update_env_value_in_file(image_filename, env_name, env_value)

    for module_dir in modules:
        module_file = os.path.join(module_dir, "module.yaml")
        update_env_value_in_file(module_file, env_name, env_value)

def update_env_value_in_file(filename, env_name, env_value):
    """
    Update environment value into the given yaml module/image file
    :param filename: filename to update
    :param env_name: environment variable name to update
    :param env_value: value to set
    """
    print("Updating {0} label {1} with value {2}".format(filename, env_name, env_value))
    try:
        with open(filename) as yaml_file:
            data = yaml_loader().load(yaml_file)
            update_env_value_in_data(data, env_name, env_value)

        with open(filename, 'w') as yaml_file:
            yaml_loader().dump(data, yaml_file)

    except TypeError:
        raise


def update_env_value_in_data(data, env_name, env_value, ignore_empty = False):
    """
    Update environment variable value in data dict if exists
    :param data: dict to update
    :param env_name: environment variable name
    :param env_value: environment variable value to set
    :param ignore_empty: Whether previous value should be present to set the new value
    """
    if isinstance(data, list):
        for data_item in data:
            update_env_value_in_data(data_item, env_name, env_value, ignore_empty)
    else:
        if ignore_empty:
            if 'envs' not in data:
                data['envs'] = []
            data['envs'] += [ dict(name=env_name, value=env_value) ]
        elif 'envs' in data:
            for _, env in enumerate(data['envs'], start=0):
                if env['name'] == env_name:
                    update_field_in_dict(env, 'value', env_value, ignore_empty)

def update_field_in_dict(data, key, new_value, ignore_empty = False):
    """
    Update version field in given data dict
    :param data: dictionary to update
    :param key: key to lookup
    :param new_value: value to set
    :param ignore_empty: Whether previous value should be present to set the new value
    """
    if isinstance(data, list):
        for data_item in data:
            update_field_in_dict(data_item, key, new_value, ignore_empty)
    else:
        if ignore_empty or key in data:
            data[key] = new_value
        else:
            print("Field " + key + " not found, returning...")

def update_label_value(label_name, label_value):
    """
    Update label value in all modules and images
    :param label_name: label name to update
    :param label_value: value to set
    """

    images = get_all_images()
    modules = get_all_module_dirs()

    for image_filename in images:
        print("updating image: " + image_filename)
        update_label_value_in_file(image_filename, label_name, label_value)

    for module_dir in modules:
        module_file = os.path.join(module_dir, "module.yaml")
        update_label_value_in_file(module_file, label_name, label_name)

def update_label_value_in_file(filename, label_name, label_value):
    """
    Update label value into the given yaml module/image file
    :param filename: filename to update
    :param label_name: label name to update
    :param label_value: value to set
    """
    print("Updating {0} label {1} with value {2}".format(filename, label_name, label_value))
    try:
        with open(filename) as yaml_file:
            data = yaml_loader().load(yaml_file)
            update_label_value_in_data(data, label_name, label_value)

        with open(filename, 'w') as yaml_file:
            yaml_loader().dump(data, yaml_file)

    except TypeError:
        raise


def update_label_value_in_data(data, label_name, label_value, ignore_empty = False):
    """
    Update label value in data dict if exists
    :param data: dict to update
    :param label_name: label name
    :param label_value: label value to set
    :param ignore_empty: Whether previous value should be present to set the new value
    """
    if isinstance(data, list):
        for data_item in data:
            update_label_value_in_data(data_item, label_name, label_value, ignore_empty)
    else:
        if ignore_empty:
            if 'labels' not in data:
                data['labels'] = []
            data['labels'] += [ dict(name=label_name, value=label_value) ]
        elif 'labels' in data:
            for _, label in enumerate(data['labels'], start=0):
                if label['name'] == label_name:
                    update_field_in_dict(label, 'value', label_value, ignore_empty)

