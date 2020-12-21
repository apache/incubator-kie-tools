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

const glob = require('glob');
const compressing = require('compressing');
const lerna = require('./lerna.json');
const fs = require("fs")
const path = require("path")

const destDir = path.join(__dirname, `kogito-tooling.${lerna.version}`)
if (!fs.existsSync(destDir)) {
  fs.mkdirSync(destDir)
}

console.log("[INFO] Searching for kogito-tooling*.tgz files.")


glob('./packages/**/kogito-tooling*.tgz', function (err, files) {
  console.log(`[INFO] Found ${files.length} files.`, files);
  files.forEach(file => {
    const sourceFile = path.join(__dirname, file);
    const destFile = path.join(destDir, path.basename(file))
    try {
      console.log(`[INFO] Copying file ${path.basename(sourceFile)} to distribution folder.`)
      fs.copyFileSync(sourceFile, destFile, { recursive: true })
    } catch (err) {
      throw err
    }
  })

  console.log(`[INFO] Compressing files from ${destDir}`)
  const compressedFile = path.join(__dirname, `kogito-tooling.${lerna.version}.zip`);
  compressing.zip.compressDir(destDir, compressedFile, { ignoreBase: true })
    .then(() => {
      console.log(`[INFO] Compression done into ${compressedFile}`);
      fs.rmdirSync(destDir, { recursive: true });
    })
    .catch(e => console.log("[ERROR] Error compressing folder", e))
});
