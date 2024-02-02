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

import { spawn } from "child_process";

const argv = process.argv.slice(2).flatMap((e) => e.split("="));

let storybookArgs: string[] = [];
if (argv.indexOf("--storybookArgs") !== -1) {
  storybookArgs = argv[argv.indexOf("--storybookArgs") + 1].split(" ");
}

if (argv.indexOf("--env") !== -1 && argv[argv.indexOf("--env") + 1] === "live") {
  process.env.STORYBOOK_BASE_WRAPPER_INTERNAL__liveReload = "true";
}

const storybook = spawn(`storybook`, [...storybookArgs, "-p", process.env.STORYBOOK_PORT ?? ""], { shell: true });

storybook.stdout.setEncoding("utf8");
storybook.stdout.on("data", (data) => {
  console.log(data);
});

storybook.stderr.setEncoding("utf8");
storybook.stderr.on("data", (data) => {
  if (data.includes("[webpack.Progress]")) {
    return;
  }
  if (data.includes("[webpack-dev-middleware]")) {
    const cleanLog = data.replace(/(\r\n|\n|\r)/gm, "");
    console.log(cleanLog);
    return;
  }
  console.log(data);
});

storybook.on("close", (code) => {
  console.log(`[STORYBOOK CLOSE]: child process exited with code ${code}`);
});

storybook.on("error", (error) => {
  console.log(`[STORYBOOK ERROR]: ${error}`);
});
