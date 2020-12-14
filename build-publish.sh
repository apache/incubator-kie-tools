#!/bin/sh
# input parameter NPM Registry URL instead of default ones (npm, yarn)
npm install -g yarn
npm run install-locktt
npm run locktt -- --registry=$1
yarn install
yarn run init
yarn build:productization
yarn run publish --yes --cd-version patch 
