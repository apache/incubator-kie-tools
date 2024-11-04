#!/usr/bin/env python3

import os
import sys
from ruamel.yaml import YAML
import argparse

def update_yaml_branch(version, file_path):
    full_version = f"openshift-serverless-{version}-rhel-8"

    yaml = YAML()
    yaml.preserve_quotes = True
    
    with open(file_path, "r") as file:
        data = yaml.load(file)

    data['osbs']['repository']['branch'] = full_version

    with open(file_path, "w") as file:
        yaml.dump(data, file)

    print(f"osbs.repository.branch updated to: {full_version} in {file_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Update the osbs.repository.branch in a specified YAML file.")
    parser.add_argument("--version", required=True, type=str, help="The version number (e.g., '1.35') to set for osbs.repository.branch.")
    parser.add_argument("--file_path", required=True, type=str, help="Path to the YAML file to update.")
    args = parser.parse_args()

    if not os.path.exists(args.file_path):
        print(f"Error: The file '{args.file_path}' does not exist.")
        sys.exit(1)

    update_yaml_branch(args.version, args.file_path)
