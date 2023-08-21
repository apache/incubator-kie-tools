# Test Scenario Editor

This module will host the next generation of Test Scenario Editor (\*.scesim) files.
The implementation is currently in PROGRESS, therefore we don't recommend any usage at the current time.

this README will be updated accordingly to its status.

## How to build it

Like most of the projects of this repository, pnpm and NodeJS are mandatory to build the project. Please refer to the
repository main README file to know more about the requested versions and installation steps. During the first project build,
don't forget to bootstrap the project by launching ONE of the following:

- `pnpm bootstrap`
- `pnpm bootstrap -F @kie-tools/scesim-editor...` if you are interested to bootstrap this module only.

It's recommended to run a `pnpm bootstrap` every time a change in any direct or indirect modules' `packages.json` is applied.

To build the project type in your terminal ONE of the below commands:

- `pnpm -F @kie-tools/scesim-editor... build:dev` This is fast, but not as strict. It skips tests, linters, and some type checks. Recommended for dev purposes.
- `pnpm -F @kie-tools/scesim-editor... build:prod` The default command to build production-ready packages. This is the recommended build for production purposes

The above command will build `scesim-editor` module AND its direct and indirect dependencies modules.
To build the `scesim-editor` module ONLY, you can use ONE of the below commands:

- `pnpm -F @kie-tools/scesim-editor build:dev` This is fast, but not as strict. It skips tests, linters, and some type checks. Recommended for dev purposes.
- `pnpm -F @kie-tools/scesim-editor build:prod` The default command to build production-ready packages. This is the recommended build for production purposes

## How to launch the Test Scenario Dev WebApp

After building the project, you can benefit of the Dev Webapp for development or testing scope.
To launch it, simply type in your terminal the following command:

`pnpm -F @kie-tools/scesim-editor start`

A web server with a Dev Webapp of Test Scenario editor will be launched, reachable at the following address:

http://localhost:9004/ or http://192.168.1.128:9004/
