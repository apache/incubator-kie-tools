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

import { readFileSync } from "fs";
import * as __path from "path";

export function getModelXmlForTestFixtures(args: { normalizedPosixPathRelativeToTheWorkspaceRoot: string }): string {
  return readFileSync(
    __path.resolve(__dirname, "..", args.normalizedPosixPathRelativeToTheWorkspaceRoot.split("/").join(__path.sep)), // TODO: Use `toFsPath` from `@kie-tools-core/operating-system.
    "utf8"
  );
}

export function asyncGetModelXmlForTestFixtures(args: {
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
}): Promise<string> {
  return Promise.resolve(getModelXmlForTestFixtures(args));
}
