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

import * as __http from "http";
import * as __path from "path";
import * as __fs from "fs";
import __url from "url";
import { spawn as __spawn } from "child_process";
import __serveStatic from "serve-static";
import __finalhandler from "finalhandler";

//
//
//
// validate args

const portArg = process.argv[2];
const port = Number.parseInt(portArg);
if (isNaN(port)) {
  console.error(`[git-repo-http-dev-server] Invalid port number '${portArg}'.`);
  printUsage();
  process.exit(1);
}

const contentRootArg = process.argv[3];
const contentRoot = __path.resolve(contentRootArg);
if (!__fs.existsSync(contentRoot)) {
  console.error(`[git-repo-http-dev-server] Can't serve content from non-existent directory '${contentRoot}'.`);
  printUsage();
  process.exit(1);
}

//
//
//
// main

const serveAsStaticContent = __serveStatic(contentRoot);

__http
  .createServer((req, res) => {
    // bare git repos
    const [pathname, _] = req.url.split("?");
    if (pathname?.split("/").at(-1).endsWith(".git")) {
      console.log(`[git-repo-http-dev-server] Received request for '${req.url}'...`);
      console.log(`[git-repo-http-dev-server] Serving as "smart" HTTP for Git.`);
      serveAsGitSmartHttp(req, res);
    }
    // static content
    else {
      console.log(`[git-repo-http-dev-server] Received request for '${req.url}'...`);
      console.log("[git-repo-http-dev-server] Serving as static content.");
      serveAsStaticContent(req, res, __finalhandler(req, res));
    }
  })
  .listen(port, () => {
    printSummary();
  });

//
//
//
// functions

function serveAsGitSmartHttp(req, res) {
  const gitHttpBackend = __spawn("git", ["http-backend"], { env: getEnvForGitHttpBackend(req) });

  req.pipe(gitHttpBackend.stdin);

  const buffers = {
    header: [],
    body: [],
    completedHeader: false,
  };

  gitHttpBackend.stdout.on("data", (chunk) => writeData(chunk, buffers, res));
  gitHttpBackend.on("close", () => {
    res.end();
  });
}

const gitHttpBackendVariableNames = [
  "QUERY_STRING",
  "REMOTE_USER",
  "CONTENT_LENGTH",
  "HTTP_CONTENT_ENCODING",
  "REMOTE_USER",
  "REMOTE_ADDR",
  "GIT_COMMITTER_NAME",
  "GIT_COMMITTER_EMAIL",
  "CONTENT_TYPE",
  "PATH_INFO",
  "GIT_PROJECT_ROOT",
  "PATH_TRANSLATED",
  "SERVER_PROTOCOL",
  "REQUEST_METHOD",
  "GIT_HTTP_EXPORT_ALL",
  "GIT_HTTP_MAX_REQUEST_BUFFER",
];

function getEnvForGitHttpBackend(req) {
  const [pathname, queryString] = req.url.split("?");
  const envVars = {};

  for (let header in req.headers) {
    const name = header.toUpperCase().replace(/-/g, "_");
    if (gitHttpBackendVariableNames.includes(name)) {
      envVars[name] = req.headers[header];
    }
  }

  envVars["GIT_PROJECT_ROOT"] = contentRoot;
  envVars["PATH_TRANSLATED"] = contentRoot + pathname;
  envVars["PATH_INFO"] = pathname;
  envVars["REQUEST_METHOD"] = req.method;
  envVars["GIT_HTTP_EXPORT_ALL"] = "1";
  envVars["QUERY_STRING"] = queryString;

  return envVars;
}

//
//
//
// plumbing methods

function writeData(chunk, buffers, res) {
  if (buffers.completedHeader) {
    res.write(chunk);
  } else {
    buffers.completedHeader = readMaybeHeaderBuffer(chunk, buffers);
    if (buffers.completedHeader) {
      writeHeader(buffers.header, res);
      writeBody(buffers.body, res);
    }
  }
}

function writeHeader(header, res) {
  const headerLines = Buffer.concat(header).toString().split("\r\n");
  for (let headerLine of headerLines) {
    const headerSplit = headerLine.split(":");
    const headerKey = headerSplit[0];
    const headerVal = headerSplit[1];
    res.setHeader(headerKey, headerVal);
  }
}

function writeBody(body, res) {
  body.forEach((b) => res.write(b));
}

function readMaybeHeaderBuffer(nextBuffer, buffers) {
  const length = Buffer.from("\r\n\r\n", "utf-8").length;
  const offset = nextBuffer.indexOf("\r\n\r\n", 0, "utf-8");
  if (offset <= 0) {
    return false;
  }

  const headerLines = nextBuffer.slice(0, offset);
  buffers.header.push(headerLines);
  buffers.body.push(nextBuffer.slice(offset + length));
  return true;
}

// logs

function printSummary() {
  const dirs = __fs.readdirSync(contentRoot);
  console.log(`[git-repo-http-dev-server] Starting...`);

  const bareGitRepoDirs = dirs.filter((s) => s.endsWith(".git"));
  console.log(`[git-repo-http-dev-server] Found ${bareGitRepoDirs.length} bare Git repo(s):`);
  bareGitRepoDirs.forEach((d) =>
    console.log(
      `[git-repo-http-dev-server] ./${__path.join(__path.relative(".", contentRoot), d)} (clone with 'git clone http://localhost:${port}/${d}')`
    )
  );

  const staticContentDirs = dirs.filter((s) => !s.endsWith(".git"));
  console.log(`[git-repo-http-dev-server] Found ${staticContentDirs.length} static content dir(s):`);
  staticContentDirs.forEach((d) =>
    console.log(
      `[git-repo-http-dev-server] ./${__path.join(__path.relative(".", contentRoot), d)}/ (access via http://localhost:${port}/${d}/*)`
    )
  );

  console.log(`[git-repo-http-dev-server] Listening to HTTP port ${port}.`);
  console.log(`[git-repo-http-dev-server] ========================================`);
}

function printUsage() {
  console.error(`[git-repo-http-dev-server] Usage: node server.mjs [port] [content-root-path]`);
}
