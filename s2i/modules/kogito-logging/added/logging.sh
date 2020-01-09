#!/usr/bin/env bash

BLACK='\033[0;30m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
DEFAULT='\033[0m'

function log_warning() {
  local message="${1}"

  echo >&2 -e "${YELLOW}WARN ${message}${DEFAULT}"
}

function log_error() {
  local message="${1}"

  echo >&2 -e "${RED}ERROR ${message}${DEFAULT}"
}

function log_info() {
  local message="${1}"

  echo >&2 -e "INFO ${message}"
}
