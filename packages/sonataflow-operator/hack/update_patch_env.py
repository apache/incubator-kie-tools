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
import sys

PATCH_FILE = "config/manager/manager_env_patch.yaml"
BUILDER_DOCKERFILE = "config/manager/SonataFlow-Builder.containerfile"

yaml = YAML()
yaml.preserve_quotes = True

def update_kustomize_patch(env_var_mappings):
    with open(PATCH_FILE, "r") as file:
        data = yaml.load(file)

    containers = data.get("spec", {}).get("template", {}).get("spec", {}).get("containers", [])
    for container in containers:
        if container.get("name") == "manager":
            env_list = container.setdefault("env", [])
            for name, value in env_var_mappings.items():
                found = False
                for env in env_list:
                    if env.get("name") == name:
                        env["value"] = value
                        found = True
                        break
                if not found:
                    env_list.append({"name": name, "value": value})
            break

    with open(PATCH_FILE, "w") as file:
        yaml.dump(data, file)

def update_dockerfile(base_image_tag):
    with open(BUILDER_DOCKERFILE, "r") as file:
        dockerfile_lines = file.readlines()

    for i, line in enumerate(dockerfile_lines):
        if line.strip().startswith("FROM"):
            match = re.match(r"FROM\s+(\S+)(.*)", line.strip())
            if match:
                trailing = match.group(2)
                dockerfile_lines[i] = f"FROM {base_image_tag}{trailing}\n"
                print(f"📋 Updated Dockerfile FROM clause to: {dockerfile_lines[i].strip()}")
                break

    with open(BUILDER_DOCKERFILE, "w") as file:
        file.writelines(dockerfile_lines)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python update_patch_env.py <ENV_VAR1=value1> <ENV_VAR2=value2> ...")
        sys.exit(1)

    env_mappings = {}
    for mapping in sys.argv[1:]:
        k, v = mapping.split("=", 1)
        env_mappings[k] = v

    update_kustomize_patch(env_mappings)

    if "RELATED_IMAGE_BASE_BUILDER" in env_mappings:
        update_dockerfile(env_mappings["RELATED_IMAGE_BASE_BUILDER"])
