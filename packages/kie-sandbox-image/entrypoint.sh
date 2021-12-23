#!/bin/bash

ENV_VAR_NAMES=(
  "KIE_TOOLING_EXTENDED_SERVICES_URL"
)

/kie-sandbox/image-env-to-json-standalone -d /var/www/html -n "${ENV_VAR_NAMES[@]}"

httpd -D FOREGROUND
