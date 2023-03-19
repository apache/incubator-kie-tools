# image-env-to-json

This package contains a CLI tool to convert environment variables to a JSON file.

It is designed mainly to be used by container images.

## Build

Run the following in the root folder of the repository to build the package:

```bash
$ pnpm build:prod @kie-tools/image-env-to-json...
```

The output artifacts will be a JS version and a standalone executable in the `packages/image-env-to-json/dist` directory.

## Usage

```
$ image-env-to-json [options]

Options:
  -V, --version                output the version number
  -d, --directory <directory>  directory to create or update an existing env.json file
  -n, --names <names...>       environment variable names to look for
  -h, --help                   display help for command
```

## Example

Suppose the host environment contains the following environment variables:

```
ENV_A=value_a
ENV_B=value_b
```

When running:

```bash
$ image-env-to-json -d /my/directory -n ENV_A ENV_B ENV_C
```

The following JSON content will be written to `/my/directory/env.json`:

```json
{
  "ENV_A": "value_a",
  "ENV_B": "value_b"
}
```

**Note**: Only existing environment variables will be written to the file.
