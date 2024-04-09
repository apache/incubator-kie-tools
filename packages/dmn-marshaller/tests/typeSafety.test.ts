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

import * as fs from "fs";
import * as path from "path";
import * as child_process from "child_process";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const files = [
  "../tests-data--manual/other/attachment.dmn",
  "../tests-data--manual/other/empty13.dmn",
  "../tests-data--manual/other/list.dmn",
  // FIXME: Tiago --> Failing due to empty boxed expression. According to the DMN spec, it's not valid.
  // "../tests-data--manual/other/list2.dmn",
  "../tests-data--manual/other/external.dmn",
  "../tests-data--manual/other/sample12.dmn",
  "../tests-data--manual/other/weird.dmn",
  // FIXME: Tiago --> This is failing due to vendor-specific properties. If we remove them manually, everything works well. How to do it?
  // "../tests-data--manual/dmn-1_4--examples/Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
];

const tmpDir = path.join(__dirname, "..", "dist-tests", "dmn-marshaller-type-safety-tests");

describe("type safety", () => {
  beforeAll(() => {
    if (fs.existsSync(tmpDir)) {
      fs.rmSync(tmpDir, { recursive: true });
    }
    fs.mkdirSync(tmpDir, { recursive: true });
    console.log(`[dmn-marshaller] Type safety tests running on '${tmpDir}'.`);
  });

  for (const file of files) {
    test(path.basename(file), () => {
      const xml = fs.readFileSync(path.join(__dirname, file), "utf-8");

      const { parser, version } = getMarshaller(xml, { upgradeTo: "latest" });
      const json = parser.parse();

      const minorVersion = version.split(".")[1];
      const tmpFile = `
import { DMN1${minorVersion}__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_${minorVersion}/ts-gen/types";
import "@kie-tools/dmn-marshaller/dist/kie-extensions";

const dmn: DMN1${minorVersion}__tDefinitions = ${JSON.stringify(json.definitions, undefined, 2)};`;

      const tmpFilePath = path.join(tmpDir, `${path.basename(file)}.ts`);
      fs.writeFileSync(tmpFilePath, tmpFile);

      const tsc = child_process.execSync(`pnpm tsc --noEmit --strict ${tmpFilePath}`, {
        stdio: "pipe",
      });

      expect(tsc.toString()).toStrictEqual("");
    });
  }
});
