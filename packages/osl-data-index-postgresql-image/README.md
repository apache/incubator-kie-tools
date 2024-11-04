# OpenShift Serverless Logic Data Index Postgresql Image

Package based on [`kogito-data-index-postgresql-image`](../kogito-data-index-postgresql-image).
This image is based on its version upstream. Internally, the package will run a few preparation scritps to:

1. Pull the app in zip format from a given URL. This app must be based on the [`kogito-data-index-postgresql-image`](../kogito-data-index-postgresql-image).
2. Save the artifact in zip format to the local Cekit Cache.
3. Update the [`generated/modules`](generated/modules) directory with MD5 and artifact name.
4. Save everything needed to build this image with Cekit in the [`generated`](generated) directory. This directory must be commited to the git branch since it will be used by internal tools to build the final image.
5. Finally build the image locally, so users can verify and run tests if necessary.

## Usage

Export the following variables before running it:

```shell
# REQUIRED ENV:
export KIE_TOOLS_BUILD__buildContainerImages=true

# OPTIONAL ENVs:
# The WebApp artifact in zip format
export OSL_DATA_INDEX_POSTGRESQL_IMAGE__artifactUrl=<artifact URL>
# Image name/tag information. This information is optional, this package has these set by default.
export OSL_DATA_INDEX_POSTGRESQL_IMAGE__registry__registry=registry.access.redhat.com
export OSL_DATA_INDEX_POSTGRESQL_IMAGE__registry__account=openshift-serverless-1
export OSL_DATA_INDEX_POSTGRESQL_IMAGE__registry__name=logic-data-index-postgresql-rhel8
# This version is also optional since once branched, we can update the `root-env` package env `KIE_TOOLS_BUILD__streamName` to the OSL version.
export OSL_DATA_INDEX_POSTGRESQL_IMAGE__registry__buildTag=1.34
# Quarkus/Kogito version. This information will be set in the image labels and internal builds in `root-env`.
# Optionally you can also use Cekit overrides when building the final image in the internal systems.
export QUARKUS_PLATFORM_version=3.8.6
export KOGITO_RUNTIME_version=9.101-redhat

pnpm build:dev # build:prod also works, does the same currently.
```

Then you can export the directory `generated` to any system to build the image via Cekit.

> [!IMPORTANT]
> Do not modify any file in the `generated` directory. Always use the `pnpm build:dev` to generate this package.

The script `pnpm build:dev` won't build the image, but will make the Cekit files available in the `generated` directory.
Since it requires Red Hat specific libraries and tools, it might not be ideal to build the image immediately.
There's a command just to build the image `pnpm image:cekit:build`.
