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
const jbang = require("@jbangdev/jbang");
const buildEnv = require("../../env");

export function executeJBangScript(scriptPath: string, ...args: string[]) {
  /* Windows requires double quotes to wrap the argument, while in POSIX it must be wrapped by single quotes */
  const isWindowsPath = path.sep !== "/";
  const quoteChar = isWindowsPath ? '"' : "'";
  jbang.exec("--java 17", "properties@jbangdev", "java.version", "--verbose");
  jbang.exec(
    "-Dkogito-runtime.version=" + buildEnv.env.kogitoRuntime.version,
    scriptPath,
    args.map((arg) => quoteChar + arg + quoteChar).join(" ")
  );
}
