#!/bin/bash

/kie-sandbox/image-env-to-json-standalone --directory /var/www/html --json-schema /kie-sandbox/EnvJson.schema.json

httpd -D FOREGROUND
