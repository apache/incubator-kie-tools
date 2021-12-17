#!/bin/bash

cd /var/www/html

ENV_VAR_NAMES=(
  "KIE_TOOLING_EXTENDED_SERVICES_HOST"
  "KIE_TOOLING_EXTENDED_SERVICES_PORT"
)

if [ ! -e env.json ]; then
  echo "{}" >env.json
fi

for env_var_name in "${ENV_VAR_NAMES[@]}"; do
  if env | grep -q ^$env_var_name=; then
    env_var_value="${!env_var_name}"
    jq --arg key "$env_var_name" --arg value "$env_var_value" '.[$key] = $value' env.json >env.tmp && mv env.tmp env.json
  fi
done

httpd -D FOREGROUND
