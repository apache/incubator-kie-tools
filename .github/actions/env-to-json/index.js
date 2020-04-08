/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

const core = require("@actions/core");
const github = require("@actions/github");
const fs = require("fs");

function filterObjectProperties(object, prefix) {
  return Object.keys(object)
    .filter(key => key.startsWith(prefix))
    .reduce((obj, key) => {
      const keyToSave = key.replace(prefix, "");
      obj[keyToSave] = object[key];
      return obj;
    }, {});
}

try {
  const json = filterObjectProperties(process.env, "env-to-json__");
  const path = core.getInput("path");

  console.log(JSON.stringify(json, null, 4));

  fs.writeFile(path, JSON.stringify(json), "utf8", function(err) {
    if (err) {
      console.log("An error occurred while writing JSON Object to File.");
      return console.log(err);
    }

    console.log("JSON file has been saved to '" + path + "'");
  });
} catch (error) {
  core.setFailed(error.message);
}
