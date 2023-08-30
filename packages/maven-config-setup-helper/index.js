/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const fs = require("fs");
const path = require("path");

const MVN_CONFIG_ORIGINAL_FILE_PATH = path.join(".mvn", "maven.config.original");
const MVN_CONFIG_FILE_PATH = path.join(".mvn", "maven.config");

module.exports = {
  setup: (mavenConfigString) => {
    let originalMvnConfigString;
    if (fs.existsSync(MVN_CONFIG_ORIGINAL_FILE_PATH)) {
      console.info(`[maven-config-setup-helper] Found '${MVN_CONFIG_ORIGINAL_FILE_PATH}'.`);
      originalMvnConfigString = fs.readFileSync(MVN_CONFIG_ORIGINAL_FILE_PATH, "utf-8");
    } else if (fs.existsSync(MVN_CONFIG_FILE_PATH)) {
      console.info(`[maven-config-setup-helper] Found '${MVN_CONFIG_FILE_PATH}'.`);
      originalMvnConfigString = fs.readFileSync(MVN_CONFIG_FILE_PATH, "utf-8");
    } else {
      console.info(`[maven-config-setup-helper] No previous config found.`);
      originalMvnConfigString = "";
    }

    fs.mkdirSync(".mvn", { recursive: true });

    console.info(`[maven-config-setup-helper] Writing '${MVN_CONFIG_ORIGINAL_FILE_PATH}'...`);
    console.info(`${originalMvnConfigString}` || "<empty>");
    fs.writeFileSync(MVN_CONFIG_ORIGINAL_FILE_PATH, originalMvnConfigString);

    const trimmedMavenConfigString = mavenConfigString
      .trim()
      .split("\n")
      .map((l) => l.trim())
      .join("\n");

    const newMavenConfigString = `${originalMvnConfigString.trim()}\n${trimmedMavenConfigString.trim()}`.trim();
    console.info(`[maven-config-setup-helper] Writing '${MVN_CONFIG_FILE_PATH}'...`);
    console.info(newMavenConfigString);
    fs.writeFileSync(MVN_CONFIG_FILE_PATH, newMavenConfigString);

    console.info(`[maven-config-setup-helper] Done.`);
  },
};
