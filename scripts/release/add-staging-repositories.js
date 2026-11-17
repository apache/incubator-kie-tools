#!/usr/bin/env node
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Adds one or more Maven repositories (e.g. Apache Nexus staging repositories) to a Maven
// settings.xml as an always-active profile, for both artifacts and plugins. If the settings file
// does not exist yet, it is created.
//
// This is what makes a release candidate able to resolve upstream artifacts that are only
// published to staging (e.g. kogito-apps-bom:10.3.0) before they reach Maven Central, without
// having to hand-edit the credentials-bearing settings file managed in Jenkins.
//
// In CI, pass a NEW file here and wire it through `--global-settings=<file>` in the `MAVEN_ARGS` environment
// variable (see `setupPnpm` in .ci/jenkins/shared-scripts/buildUtils.groovy). Do not write it to `.mvn/maven.config`:
// some of those files are copied into container images, where the CI path does not exist.
// Do not add it to the file passed as `--settings`: `pnpm bootstrap` prepends its own `--settings`
// and Maven honors only the first one, so a profile added there would be silently ignored.
//
// Usage: node add-staging-repositories.js <settings.xml> "<url>[,<url>...]"
// URLs can be separated by commas, spaces or new lines. Running it twice is a no-op.

const fs = require("fs");

const PROFILE_ID = "kie-release-staging";

function fail(message) {
  console.error(`ERROR: ${message}`);
  process.exit(1);
}

const [settingsFile, urlsArg] = process.argv.slice(2);
if (!settingsFile || urlsArg === undefined) {
  fail('Usage: add-staging-repositories.js <settings.xml> "<url>[,<url>...]"');
}

const urls = urlsArg
  .split(/[\s,]+/)
  .map((url) => url.trim())
  .filter(Boolean);

if (urls.length === 0) {
  console.log("No staging repositories requested. Nothing to do.");
  process.exit(0);
}

for (const url of urls) {
  if (!/^https:\/\/[A-Za-z0-9._~:\/-]+$/.test(url)) {
    fail(`Invalid staging repository URL (https and plain URL characters only): '${url}'`);
  }
}

let settings = fs.existsSync(settingsFile) ? fs.readFileSync(settingsFile, "utf8") : "<settings>\n</settings>\n";

if (settings.includes(`<id>${PROFILE_ID}</id>`)) {
  console.log(`Profile '${PROFILE_ID}' already present in ${settingsFile}. Nothing to do.`);
  process.exit(0);
}

if (!settings.includes("</settings>")) {
  fail(`${settingsFile} does not look like a Maven settings file (no </settings>)`);
}

const repositoryXml = (tag, url, index) => `
          <${tag}>
            <id>${PROFILE_ID}-${index}</id>
            <name>KIE release staging ${index}</name>
            <url>${url}</url>
            <releases><enabled>true</enabled></releases>
            <snapshots><enabled>false</enabled></snapshots>
          </${tag}>`;

const profileXml = `
    <profile>
      <id>${PROFILE_ID}</id>
      <repositories>${urls.map((url, i) => repositoryXml("repository", url, i + 1)).join("")}
      </repositories>
      <pluginRepositories>${urls.map((url, i) => repositoryXml("pluginRepository", url, i + 1)).join("")}
      </pluginRepositories>
    </profile>
`;

const activeProfileXml = `    <activeProfile>${PROFILE_ID}</activeProfile>\n`;

function insertBefore(source, closingTag, content) {
  const index = source.lastIndexOf(closingTag);
  return source.slice(0, index) + content + source.slice(index);
}

if (settings.includes("</profiles>")) {
  settings = insertBefore(settings, "</profiles>", profileXml + "  ");
} else {
  settings = insertBefore(settings, "</settings>", `  <profiles>${profileXml}  </profiles>\n`);
}

if (settings.includes("</activeProfiles>")) {
  settings = insertBefore(settings, "</activeProfiles>", activeProfileXml + "  ");
} else {
  settings = insertBefore(settings, "</settings>", `  <activeProfiles>\n${activeProfileXml}  </activeProfiles>\n`);
}

fs.writeFileSync(settingsFile, settings);
console.log(`Added ${urls.length} staging repositories to ${settingsFile}:`);
urls.forEach((url) => console.log(`  - ${url}`));
