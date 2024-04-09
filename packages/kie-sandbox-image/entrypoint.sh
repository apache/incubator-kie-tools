#!/bin/bash

# Copying the KIE Sandbox assets here is essential for when the container is running with the readOnlyRootFilesystem flag.
# But, just like any other directory modified during runtime, the /var/www/html must be a mounted volume in the container in this case.
cp -R /kie-sandbox/app/* /var/www/html

/kie-sandbox/image-env-to-json-standalone --directory /var/www/html --json-schema /kie-sandbox/EnvJson.schema.json

httpd -D FOREGROUND