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

const { env } = require("./env");
const cp = require("child_process");

const version = env.vscodeJavaCodeCompletionExtensionPlugin.version;

// Custom installMvnw that disables the 'plugin' profile to avoid Tycho version validation issues
// The 'plugin' profile triggers source bundle generation which fails with single-segment versions like 111-SNAPSHOT
console.info(`[maven-base] Installing mvnw...`);
console.time(`[maven-base] Installing mvnw...`);

const cmd = `mvn -e org.apache.maven.plugins:maven-wrapper-plugin:3.3.0:wrapper -P-include-1st-party-dependencies,-plugin`;

if (process.platform === "win32") {
  cp.execSync(cmd.replaceAll(" -", " `-"), { stdio: "inherit", shell: "powershell.exe" });
} else {
  cp.execSync(cmd, { stdio: "inherit" });
}

console.timeEnd(`[maven-base] Installing mvnw...`);

// Manifest file

const fs = require("fs");
const path = require("path");

const MANIFEST_FILE = path.resolve("vscode-java-code-completion-extension-plugin-core/META-INF/MANIFEST.MF");

console.info("[vscode-java-code-completion-extension-plugin-install] Updating manifest file...");
const manifestFile = fs.readFileSync(MANIFEST_FILE, "utf-8");

fs.writeFileSync(
  MANIFEST_FILE,
  manifestFile
    .split("\n")
    .map((line) => (line.startsWith(`Bundle-Version: `) ? `Bundle-Version: ${version}` : line))
    .join("\n")
);
console.info("[vscode-java-code-completion-extension-plugin-install] Done.");
