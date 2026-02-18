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

import * as path from "path";
import { startGitHttpServer } from "./server";

function printUsage(): void {
  console.error(`[git-repo-http-dev-server] Usage: node cli.js [port] [content-root-path]`);
}

// Validate args
const portArg = process.argv[2];
const port = Number.parseInt(portArg);
if (isNaN(port)) {
  console.error(`[git-repo-http-dev-server] Invalid port number '${portArg}'.`);
  printUsage();
  process.exit(1);
}

const contentRootArg = process.argv[3];
const contentRoot = path.resolve(contentRootArg);

try {
  startGitHttpServer({
    port,
    contentRoot,
  });
} catch (error) {
  console.error(`[git-repo-http-dev-server] Error: ${error instanceof Error ? error.message : String(error)}`);
  printUsage();
  process.exit(1);
}
