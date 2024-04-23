# kogito-swf-common utility scripts

This directory contains a set of python scripts used to help to perform some tasks during the build and configuration of the `kogito-swf` images:

## Common script

The [common.py](common.py) defines the basic functions that will be used by other scripts

## Retrieve Versions script

The [retrieve_versions.py](retrieve_version.py) script is used to read the actual image version from the
`kogito-project-versions` module during the image build process.

## Versions Manager

The [versions_manager.py](versions_manager.py) script offers a CLI that helps upgrading versions properties in
the images yaml descriptor or cekit modules. This script is being used during the image build time, but it's also used
to update the images / cekit modules versions when bootstraping the `@kie-tools` repo.

Usage:

- Bumping images / cekit modules versions of a package in `@kie-tools`
  Args:

  - `--bump-to`: bumps the image and module versions (in the `resources` folder) to the specified version.
  - `--source-folder`: specifies the path to the `resources` folder.

- Upgrading platform dependencies in all images / modules envs and labels during the image build process.
  Args:
- `--quarkus-version`: Sets the Quarkus version
- `--kogito-version`: Sets the Kogito version
