#!/usr/bin/env bash
set -e

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "--- Test: Build And Run"
"${script_dir_path}"/build-and-run.sh