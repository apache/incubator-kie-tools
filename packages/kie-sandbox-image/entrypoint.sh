#!/bin/bash


# FIXME: Tiago -> This needs to be type-checked.

ENV_VAR_NAMES=(
  "KIE_SANDBOX_EXTENDED_SERVICES_URL"
  "KIE_SANDBOX_GIT_CORS_PROXY_URL"
  "KIE_SANDBOX_AUTH_PROVIDERS"
)

/kie-sandbox/image-env-to-json-standalone -d /var/www/html -n "${ENV_VAR_NAMES[@]}"

httpd -D FOREGROUND
