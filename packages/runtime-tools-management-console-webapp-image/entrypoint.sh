#!/bin/bash

# Copying the Task Console assets here is essential for when the container is running with the readOnlyRootFilesystem flag.
# But, just like any other directory modified during runtime, the /var/www/html must be a mounted volume in the container in this case.
cp -R /management-console/app/* /var/www/html

/management-console/image-env-to-json-standalone --directory /var/www/html --json-schema /management-console/EnvJson.schema.json

httpd -D FOREGROUND
