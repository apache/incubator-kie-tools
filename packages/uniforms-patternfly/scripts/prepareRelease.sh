#!/bin/bash
echo "Preparing release"

set -e

rm -Rf node_modules
yarn
yarn clean
yarn test
yarn lint

# don't run in CI
if [ ! "$CI" = true ]; then
  npm publish
fi

echo "Repository is ready for release."

