# Dev Deployment Base Image

Docker image with Java and Maven, as well as the dev-deployment-upload-service binary installed and ready to be used.

## Build arguments

- `BUILDER_IMAGE_ARG`: The base image used for building this image (defaults to `registry.access.redhat.com/ubi9/openjdk-17:1.18`).
  - Tested with:
    - registry.access.redhat.com/ubi9/openjdk-17:1.18
    - icr.io/appcafe/ibm-semeru-runtimes:open-17-jdk-ubi-minimal

## Environment variables

### Pre defined (have a default value)

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR`: The directory to extract the files uploaded via the Upload Service. Defaults to `/app` inside the container.
- `DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT`: The port where the Upload Service will listen on. Defaults to `8080`.

### Required

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY`: A string that represents the API Key the Upload Service accepts. It should be passed as a Query Param when making requests to the service.

### Optional

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH`: If the Upload Service is not running in the root path of the URL, this variable should be specified.

## Test locally

Run the image with:

- `docker run -p 8080:8080 -e DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY=123 quay.io/kie-tools/dev-deployment-base-image:daily-dev 'dev-deployment-upload-service && ./mvnw quarkus:dev'`

Then upload a zip file containing the resources (full Java project)

- `curl -X POST -H "Content-Type: multipart/form-data" -F "myFile=@<ABSOLUTE_PATH_TO_YOUR_FILE>" 'http://localhost:8080/upload?apiKey=123'`
