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

import * as http from "http";
import * as path from "path";
import * as fs from "fs";
import { spawn } from "child_process";
import serveStatic from "serve-static";
import finalhandler from "finalhandler";

export interface GitHttpServerConfig {
  port: number;
  contentRoot: string;
  logPrefix?: string;
}

const gitHttpBackendVariableNames = [
  "QUERY_STRING",
  "REMOTE_USER",
  "CONTENT_LENGTH",
  "HTTP_CONTENT_ENCODING",
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

interface BufferState {
  header: Buffer[];
  body: Buffer[];
  completedHeader: boolean;
}

/**
 * Creates and starts a Git HTTP server
 * @param options Server configuration options
 * @returns HTTP server instance
 */
export function startGitHttpServer(options: GitHttpServerConfig): http.Server {
  const { port, contentRoot, logPrefix = "git-repo-http-dev-server" } = options;

  const log = (message: string) => console.log(`[${logPrefix}] ${message}`);

  if (!fs.existsSync(contentRoot)) {
    throw new Error(`Can't serve content from non-existent directory '${contentRoot}'.`);
  }

  const serveAsStaticContent = serveStatic(contentRoot);

  const server = http.createServer((req, res) => {
    // bare git repos
    if (req.url?.split("/")[1].endsWith(".git")) {
      log(`Received request for '${req.url}'...`);
      log(`Serving as "smart" HTTP for Git.`);
      serveAsGitSmartHttp(req, res, contentRoot);
    }
    // static content
    else {
      log(`Received request for '${req.url}'...`);
      log("Serving as static content.");
      serveAsStaticContent(req, res, finalhandler(req, res));
    }
  });

  server.listen(port, () => {
    printSummary(contentRoot, port, log);
  });

  return server;
}

function serveAsGitSmartHttp(req: http.IncomingMessage, res: http.ServerResponse, contentRoot: string): void {
  const gitHttpBackend = spawn("git", ["http-backend"], { env: getEnvForGitHttpBackend(req, contentRoot) });

  req.pipe(gitHttpBackend.stdin);

  const buffers: BufferState = {
    header: [],
    body: [],
    completedHeader: false,
  };

  gitHttpBackend.stdout.on("data", (chunk: Buffer) => writeData(chunk, buffers, res));
  gitHttpBackend.on("close", () => {
    res.end();
  });
}

function getEnvForGitHttpBackend(req: http.IncomingMessage, contentRoot: string): NodeJS.ProcessEnv {
  const [pathname, queryString] = (req.url || "").split("?");
  const envVars: NodeJS.ProcessEnv = {};

  for (const header in req.headers) {
    const name = header.toUpperCase().replace(/-/g, "_");
    if (gitHttpBackendVariableNames.includes(name)) {
      const headerValue = req.headers[header];
      if (typeof headerValue === "string") {
        envVars[name] = headerValue;
      } else if (Array.isArray(headerValue) && headerValue.length > 0) {
        envVars[name] = headerValue[0];
      }
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

function writeData(chunk: Buffer, buffers: BufferState, res: http.ServerResponse): void {
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

function writeHeader(header: Buffer[], res: http.ServerResponse): void {
  const headerLines = Buffer.concat(header).toString().split("\r\n");
  for (const headerLine of headerLines) {
    if (!headerLine) {
      continue;
    }
    const headerSplit = headerLine.split(":");
    if (headerSplit.length < 2) {
      continue;
    }
    const headerKey = headerSplit[0].trim();
    const headerVal = headerSplit.slice(1).join(":").trim();
    if (headerKey && headerVal) {
      res.setHeader(headerKey, headerVal);
    }
  }
}

function writeBody(body: Buffer[], res: http.ServerResponse): void {
  body.forEach((b) => res.write(b));
}

function readMaybeHeaderBuffer(nextBuffer: Buffer, buffers: BufferState): boolean {
  const length = Buffer.from("\r\n\r\n", "utf-8").length;
  const offset = nextBuffer.indexOf("\r\n\r\n", 0, "utf-8");
  if (offset < 0) {
    return false;
  }

  const headerLines = nextBuffer.subarray(0, offset);
  buffers.header.push(headerLines);
  buffers.body.push(nextBuffer.subarray(offset + length));
  return true;
}

function printSummary(contentRoot: string, port: number, log: (message: string) => void): void {
  const dirs = fs.readdirSync(contentRoot);
  log(`Starting...`);

  const bareGitRepoDirs = dirs.filter((s) => s.endsWith(".git"));
  log(`Found ${bareGitRepoDirs.length} bare Git repo(s):`);
  bareGitRepoDirs.forEach((d) =>
    log(`./${path.join(path.relative(".", contentRoot), d)} (clone with 'git clone http://localhost:${port}/${d}')`)
  );

  const staticContentDirs = dirs.filter((s) => !s.endsWith(".git"));
  log(`Found ${staticContentDirs.length} static content dir(s):`);
  staticContentDirs.forEach((d) =>
    log(`./${path.join(path.relative(".", contentRoot), d)}/ (access via http://localhost:${port}/${d}/*)`)
  );

  log(`Listening on HTTP port ${port}.`);
  log(`========================================`);
}
