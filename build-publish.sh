#!/bin/sh
# input parameter NPM Registry URL instead of default ones (npm, yarn)
npm run install-locktt
npm run locktt --registry=$1
yarn run init
yarn build:prod
yarn publish:all
