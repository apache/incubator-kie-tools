#!/usr/bin/env python3
"""
update_quarkus_config.py

Dev script that updates:
  resources/modules/sonataflow/common/quarkus/registry/.quarkus/config.yaml

to contain exactly the registries passed as a comma-separated list.

Usage:
  ./update_quarkus_config.py registry1,registry2,registry3

Example:
  ./update_quarkus_config.py registry.acme.com,internal.registry.local,registry.quarkus.io
"""

import sys
from ruamel.yaml import YAML

def main():
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} registry1,registry2,...")
        sys.exit(1)

    regs = [r.strip() for r in sys.argv[1].split(",") if r.strip()]
    if not regs:
        print("Error: You must pass at least one registry URL.")
        sys.exit(1)

    file_path = "resources/modules/sonataflow/common/quarkus/registry/.quarkus/config.yaml"

    yaml = YAML()
    yaml.preserve_quotes = True

    try:
        with open(file_path, "r") as f:
            data = yaml.load(f) or {}
    except FileNotFoundError:
        data = {}

    new_list = []
    for r in regs:
        new_list.append({r: {"update-policy": "never"}})

    data["registries"] = new_list

    with open(file_path, "w") as f:
        yaml.dump(data, f)

    print(f"→ Updated '{file_path}' with registries: {', '.join(regs)}")

if __name__ == "__main__":
    main()
