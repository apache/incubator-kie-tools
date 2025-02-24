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
import { getMarshaller } from "@kie-tools/scesim-marshaller";

const files = [
  "../tests-data--manual/dmn/collection.scesim",
  "../tests-data--manual/dmn/expression.scesim",
  "../tests-data--manual/dmn/imported.scesim",
  "../tests-data--manual/dmn/simple.scesim",
  "../tests-data--manual/dmn/simpleTypes.scesim",
  "../tests-data--manual/dmn/TrafficViolationTest.scesim",
  "../tests-data--manual/dmn/undefined.scesim",
  "../tests-data--manual/rule/OldEnoughTest.scesim",
];

const tmpDir = path.join(__dirname, "..", "dist-tests", "scesim-marshaller-type-safety-tests");

describe("type safety", () => {
  beforeAll(() => {
    if (fs.existsSync(tmpDir)) {
      fs.rmSync(tmpDir, { recursive: true });
    }
    fs.mkdirSync(tmpDir, { recursive: true });
    console.log(`[scesim-marshaller] Type safety tests running on '${tmpDir}'.`);
  });

  for (const file of files) {
    test(path.basename(file), () => {
      const xml = fs.readFileSync(path.join(__dirname, file), "utf-8");
      const { parser, version } = getMarshaller(xml);

      const json = parser.parse();

      const minorVersion = version.split(".")[1];
      const tmpFile = `
import { SceSim__ScenarioSimulationModelType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_${minorVersion}/ts-gen/types";

const scesim: SceSim__ScenarioSimulationModelType = ${JSON.stringify(json.ScenarioSimulationModel, undefined, 2)};`;

      const tmpFilePath = path.join(tmpDir, `${path.basename(file)}.ts`);
      fs.writeFileSync(tmpFilePath, tmpFile);

      const tsc = child_process.execSync(`pnpm tsc --noEmit --strict ${tmpFilePath}`, {
        stdio: "pipe",
      });

      expect(tsc.toString()).toStrictEqual("");
    });
  }
});
