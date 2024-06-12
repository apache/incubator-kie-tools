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
import * as fs from "fs";

export const __ROOT_PKG_NAME = "kie-tools-root";

export const __PACKAGES_ROOT_DIRS = ["packages", "examples"];

export const __NON_SOURCE_FILES_PATTERNS = stdoutArray(
  fs.readFileSync(path.resolve(__dirname, "../patterns/non-source-files-patterns.txt"), "utf-8")
);

export function stdoutArray(output: string) {
  return output
    .trim()
    .split(/\s/)
    .filter((s) => !!s);
}
