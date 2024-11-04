#!/usr/bin/env python

import sys
import re
from pathlib import Path
from ruamel.yaml import YAML

def find_artifact_by_name(artifacts, file_name_suffix):
    for artifact in artifacts:
        if artifact.get('name') and artifact['name'] in file_name_suffix:
            return artifact
    return None

def update_yaml(yaml_file, file_name, md5_checksum):
    yaml = YAML()
    yaml.preserve_quotes = True

    with open(yaml_file, 'r') as f:
        data = yaml.load(f)

    match = re.split(r'(\d)', file_name, maxsplit=1)
    if len(match) > 1:
        file_name_suffix = match[0] 
    else:
        file_name_suffix = file_name  

    artifact = find_artifact_by_name(data.get('artifacts', []), file_name_suffix)

    if artifact:
        artifact['description'] = file_name
        artifact['md5'] = md5_checksum
        print(f"Updated artifact '{artifact['name']}' with description '{file_name}' and md5 '{md5_checksum}'")
    else:
        print(f"Error: No matching artifact found for file name suffix '{file_name_suffix}'")
        sys.exit(1)

    with open(yaml_file, 'w') as f:
        yaml.dump(data, f)

def main():
    if len(sys.argv) < 4:
        print("Usage: script.py <yaml_file> <file_name> <md5_checksum>")
        sys.exit(1)

    yaml_file = sys.argv[1]
    file_name = sys.argv[2]
    md5_checksum = sys.argv[3]

    if not Path(yaml_file).is_file():
        print("File '{yaml_file}' does not exist. Can't update the module.yaml file.")
        sys.exit(1)

    update_yaml(yaml_file, file_name, md5_checksum)

if __name__ == "__main__":
    main()