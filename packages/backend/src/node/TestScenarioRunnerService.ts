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

import * as cp from "child_process";
import { XMLParser } from "fast-xml-parser";
import * as fs from "fs";
import * as path from "path";
import { CapabilityResponse, Service } from "../api";
import { ServiceId, TestResult, TestScenarioRunnerCapability } from "../channel-api";
import * as utils from "./utils";

export class TestScenarioRunnerService implements Service, TestScenarioRunnerCapability {
  private activeProcess: cp.ChildProcess | undefined;

  public identify(): string {
    return ServiceId.TEST_SCENARIO_RUNNER;
  }

  public async start(): Promise<void> {
    /* Nothing to do here */
  }

  public stop(): void {
    if (!this.activeProcess) {
      return;
    }

    utils.killProcess(this.activeProcess);
    this.activeProcess = undefined;
  }

  public async satisfyRequirements(): Promise<boolean> {
    if (!(await utils.isMavenAvailable({ major: 3, minor: 6, patch: 2 }))) {
      console.error("Maven 3.6.2+ could not be identified.");
      return false;
    }

    if (!(await utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 }))) {
      console.error("Java 11.0.0+ could not be identified.");
      return false;
    }

    return true;
  }

  public stopActiveExecution() {
    this.stop();
  }

  public execute(baseDir: string, runnerClass: string): Promise<CapabilityResponse<TestResult>> {
    if (!fs.existsSync(path.join(baseDir, "pom.xml"))) {
      return Promise.reject(`Unable to find a pom.xml file inside ${baseDir}`);
    }

    if (!fs.existsSync(path.join(baseDir, "src", "test", "java", runnerClass.replace(".", path.sep) + ".java"))) {
      return Promise.reject(`Unable to find ${runnerClass} file in the src/test/java folder`);
    }

    return new Promise((resolve, reject) => {
      this.stopActiveExecution();

      this.activeProcess = cp.spawn("mvn", ["clean", "test", "-f", baseDir]);

      this.activeProcess.on("exit", (code: number | null) => {
        if (code !== 0) {
          resolve(CapabilityResponse.ok());
          return;
        }

        const resultFilePath = path.join(baseDir, "target", "surefire-reports", runnerClass + ".txt");

        if (!fs.existsSync(resultFilePath)) {
          reject("Test report file could not be found.");
          return;
        }

        const resultXmlPath = path.join(baseDir, "target", "surefire-reports", "TEST-" + runnerClass + ".xml");
        const parser = new XMLParser({
          attributeNamePrefix: "",
          removeNSPrefix: true,
          ignoreAttributes: false,
          parseAttributeValue: true,
          trimValues: true,
        });
        const attrsMap = parser.parse(fs.readFileSync(resultXmlPath).toString()).testsuites.testsuite[0];

        resolve(
          CapabilityResponse.ok({
            filePath: resultFilePath,
            tests: attrsMap.tests,
            errors: attrsMap.errors,
            failures: attrsMap.failures,
            skipped: attrsMap.skipped,
          })
        );
      });
    });
  }
}
