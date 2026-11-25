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

// Writes the LICENSE and NOTICE of the binary distributions of Apache KIE Tools into <dest-dir>.
//   LICENSE = <repo root>/LICENSE + the entries of scripts/release/BINARY-LICENSE-APPENDIX.txt selected by <profile>
//   NOTICE  = <repo root>/NOTICE
// BINARY-LICENSE-APPENDIX.txt is the single shared inventory of bundled third-party components. It is split in blocks
// by "#@section <a>,<b>" marker lines ("*" = always). <profile> is a comma-separated list of sections:
//   js   JavaScript bundles (PatternFly, Monaco, React, Lodash, elkjs, Red Hat fonts, ...)
//   go   Go binaries (dev-deployment-upload-service, image-env-to-json)
//   drl  drools-lsp-server jar
//   ext  extended-services-java jars
// Without <profile> only the root LICENSE is written (artifacts that bundle no third-party component).
// Usage: node build-legal.js <dest-dir> [profile]

const fs = require("fs");
const path = require("path");

const destDir = process.argv[2];
const profile = (process.argv[3] || "").split(",").filter(Boolean);
if (!destDir) {
  console.error("Usage: node build-legal.js <dest-dir> [profile]");
  process.exit(1);
}

const repoRoot = path.resolve(__dirname, "..", "..");
let license = fs.readFileSync(path.join(repoRoot, "LICENSE"), "utf8");

if (profile.length > 0) {
  let include = false;
  const lines = fs.readFileSync(path.join(__dirname, "BINARY-LICENSE-APPENDIX.txt"), "utf8").split("\n");
  const selected = [];
  for (const line of lines) {
    const marker = line.match(/^#@section (.+)$/);
    if (marker) {
      const sections = marker[1].split(",").map((s) => s.trim());
      include = sections.includes("*") || sections.some((s) => profile.includes(s));
    } else if (include) {
      selected.push(line);
    }
  }
  license += "\n" + selected.join("\n");
}

fs.mkdirSync(destDir, { recursive: true });
fs.writeFileSync(path.join(destDir, "LICENSE"), license);
fs.copyFileSync(path.join(repoRoot, "NOTICE"), path.join(destDir, "NOTICE"));
