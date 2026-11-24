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

// Writes the consolidated LICENSE and NOTICE of the binary distributions of Apache KIE Tools into <dest-dir>.
//   LICENSE = <repo root>/LICENSE + scripts/release/BINARY-LICENSE-APPENDIX.txt
//   NOTICE  = <repo root>/NOTICE
// This is the single source for the legal files shipped in the release artifacts (npm packages, VS Code and Chrome
// extensions, web apps, Helm charts, upload-service binaries) and in the container images.
// Usage: node build-legal.js <dest-dir>

const fs = require("fs");
const path = require("path");

const destDir = process.argv[2];
if (!destDir) {
  console.error("Usage: node build-legal.js <dest-dir>");
  process.exit(1);
}

const repoRoot = path.resolve(__dirname, "..", "..");

fs.mkdirSync(destDir, { recursive: true });
fs.writeFileSync(
  path.join(destDir, "LICENSE"),
  fs.readFileSync(path.join(repoRoot, "LICENSE")) + fs.readFileSync(path.join(__dirname, "BINARY-LICENSE-APPENDIX.txt"))
);
fs.copyFileSync(path.join(repoRoot, "NOTICE"), path.join(destDir, "NOTICE"));
