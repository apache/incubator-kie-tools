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

from ruamel.yaml import YAML
import re

# Relative to the project root
CONFIG_FILE = "config/manager/controllers_cfg.yaml"
BUILDER_DOCKERFILE = "config/manager/SonataFlow-Builder.containerfile"

# Load the YAML file
yaml = YAML()
yaml.preserve_quotes = True


def update_imagetags(attribute_mappings):
    """
    Update the YAML file based on provided variable mappings.

    :param attribute_mappings: Dictionary mapping YAML attribute names to their new values.
    """
    # Read the YAML file
    with open(CONFIG_FILE, "r") as file:
        data = yaml.load(file)

    # Apply updates based on var_mappings
    for cfg_key, cfg_value in attribute_mappings.items():
        if cfg_key in data and cfg_value:
            data[cfg_key] = cfg_value

    # Write the updated YAML back to the file
    with open(CONFIG_FILE, "w") as file:
        yaml.dump(data, file)


def update_dockerfile(base_image_tag):
    """
    Update the first FROM clause in the Dockerfile with the specified base image tag.
    Preserves any trailing 'AS <stage>' or other content after the image tag.

    :param base_image_tag: The new base image tag for the Dockerfile.
    """
    with open(BUILDER_DOCKERFILE, "r") as file:
        dockerfile_lines = file.readlines()

    for i, line in enumerate(dockerfile_lines):
        if line.strip().startswith("FROM"):
            match = re.match(r"FROM\s+(\S+)(.*)", line.strip())
            if match:
                trailing_content = match.group(2)  # Everything after the image tag
                updated_line = f"FROM {base_image_tag}{trailing_content}\n"
                print(f"📋 Updating first FROM clause to: {updated_line.strip()}")
                dockerfile_lines[i] = updated_line
                break

    with open(BUILDER_DOCKERFILE, "w") as file:
        file.writelines(dockerfile_lines)


if __name__ == "__main__":
    import sys

    if len(sys.argv) < 2:
        print("Usage: python update_config.py <yaml_attr1=value1> <yaml_attr2=value2> ...")
        sys.exit(1)

    # Parse the mappings from the command line arguments
    var_mappings = {}

    for mapping in sys.argv[1:]:
        yaml_attr, value = mapping.split("=", 1)
        var_mappings[yaml_attr] = value

    # Update the YAML file
    update_imagetags(var_mappings)

    # Update Dockerfile if the `sonataFlowBaseBuilderImageTag` is provided
    if "sonataFlowBaseBuilderImageTag" in var_mappings:
        update_dockerfile(var_mappings["sonataFlowBaseBuilderImageTag"])
