# OpenShift Serverless Logic Management Console Image

Package based on [`sonataflow-management-console-image`](../sonataflow-management-console-image).
This image is based on its version upstream, adding a thin layer to pull the base Management Console WebApp from a given repository.
Internally, the package will run a few preparation scritps to:

1. Pull the webapp in zip formant from a given URL. This app must be based on the [`sonataflow-management-console-webapp`](../sonataflow-management-console-webapp).
2. Save the artifact in zip format to the local Cekit Cache.
3. Update the [`generated/modules`](generated/modules) directory with MD5 and artifact name.
4. Save everything needed to build this image with Cekit in the [`generated`](generated) directory. This directory must be commited to the git branch since it will be used by internal tools to build the final image.
5. Finally build the image locally, so users can verify and run tests if necessary.

## Usage

Export the following variables before running it:

```shell
export KIE_TOOLS_BUILD__buildContainerImages=true
# The SonataFlow Management Console WebApp artifact in zip format
export OSL_MANAGEMENT_CONSOLE_IMAGE__artifactUrl=<artifact URL>
# Image name/tag information. This information is optional, you can see the env/index.js for the default values.
export OSL_MANAGEMENT_CONSOLE_IMAGE__registry=registry.access.redhat.com
export OSL_MANAGEMENT_CONSOLE_IMAGE__account=openshift-serverless-1
export OSL_MANAGEMENT_CONSOLE_IMAGE__name=logic-management-console-rhel8
export OSL_MANAGEMENT_CONSOLE_IMAGE__buildTag=1.34
# Quarkus/Kogito version. This information will be set in the image labels and internal builds.
# Optionally you can also use Cekit overrides when building the final image in the internal systems.
export QUARKUS_PLATFORM_version=3.8.6
export KOGITO_RUNTIME_version=9.101-redhat

pnpm build:dev # build:prod also works, does the same currently.
```

Then you can export the directory `generated` to any system to build the image via Cekit.

> [!IMPORTANT]
> Do not modify any file in the `generated` directory. Always use the `pnpm build:dev` to generate this package.

> [!IMPORTANT]
> The file in `generated/modules/osl-management-console/added/image-env-to-json-standalone` is not tracked in git because it's a binary file that changes every time `pnpm build:dev` runs and rebuild the whole dependencies structure. For that reason, before running cekit standalone run `pnpm copy:image-env-to-json`. Or simply copy this binary from `packages/image-env-to-json/dist/linux/image-env-to-json-standalone`.
