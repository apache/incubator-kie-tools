#!/bin/bash

ENV_VAR_NAMES=(
  "KIE_TOOLING_EXTENDED_SERVICES_HOST"
  "KIE_TOOLING_EXTENDED_SERVICES_PORT"
)

/kogito/image-env-to-json.js -d /var/www/html -n "${ENV_VAR_NAMES[@]}"

httpd -D FOREGROUND
