#!/usr/bin/env node

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const decompress = require('decompress');
const http = require('http');
const https = require("https");
const fs = require('fs');
const path = require('path');

const download = function (url, dest, cb) {
  const file = fs.createWriteStream(dest);
  (url.startsWith("https://") ? https : http).get(url, function (response) {
    response.pipe(file);
    file.on('finish', function () {
      file.close(cb(dest));
    });
  }).on('error', function (err) {
    console.error("[ERROR] Error downloading file", err);
    if (cb) cb(dest);
  });
};

const urls = process.argv.slice(2);
fs.mkdirSync("unpacked");

urls.forEach(url => download(url, path.join(".", "unpacked", url.substring(url.lastIndexOf('/') + 1)), file => {
  console.log(`${file} downloaded. Uncompressing it...`);

  decompress(file, `${file.replace(/\./g, "_")}`)
    .then(
      () => console.log(`File ${file} unpacked.`),
      error => console.error(`Error unpackaging file ${file}.`, error)
    )
}));
