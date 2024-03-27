# Dev Deployment Kogito Quarkus Blank App Image

This image is ready to be used for Dev deployments on KIE Sandbox.
It starts the dev-deployment-upload-service and then places the uploaded files inside a blank Kogito Quarkus app.
These files can decisions or processes, all of them will be used as resources for the app.

## Build arguments

- `BUILDER_IMAGE_ARG`: The base image used for building this image (defaults to `dev-deployment-base-image`).

## Environment variables

### Pre defined (have a default value)

- `ROOT_PATH`: The root path for the Quarkus app and it's sub-applications (e.g. Swagger UI). Defaults to `""`, meaning the app will run at the root path.
- `DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR`: The directory to extract the files uploaded via the Upload Service. Defaults to `/app/src/main/resources` inside the container.
- `DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT`: The port where the Upload Service will listen on. Defaults to `8080`.

### Required

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY`: A string that represents the API Key the Upload Service will accepts. It should be passed as a Query Param when making requests to the service.

### Optional

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH`: If the Upload Service is not running in the root path of the URL, this variable should be specified. (Usually follows the same value as `ROOT_PATH`).

## Test locally

Run the image with:

- `docker run -p 8080:8080 -e DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY=123 quay.io/kie-tools/dev-deployment-kogito-quarkus-blank-app-image:daily-dev`

Then upload a zip file containing the resources (DMN, BPMN, etc)

- `curl -X POST -H "Content-Type: multipart/form-data" -F "myFile=@<ABSOLUTE_PATH_TO_YOUR_FILE>" 'http://localhost:8080/upload?apiKey=123'`
