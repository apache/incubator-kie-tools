/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";
import * as fs from "fs";
import * as path from "path";
import * as cp from "child_process";
import { TestRunnerCapability, ServiceId } from "@kogito-tooling/backend-channel-api";
import { Service, CapabilityResponse } from "@kogito-tooling/backend-api";
import { isMavenAvailable } from "@kogito-tooling/backend-node";

export class VsCodeTestRunnerService implements Service, TestRunnerCapability {
  private activeProcess: cp.ChildProcess | undefined;

  public identify(): string {
    return ServiceId.TEST_RUNNER;
  }

  public async start(): Promise<void> {
    /* Nothing to do here */
  }

  public stop(): void {
    if (!this.activeProcess) {
      return;
    }

    this.activeProcess.kill("SIGINT");
    this.activeProcess = undefined;
  }

  public async satisfyRequirements(): Promise<boolean> {
    if (!(await isMavenAvailable({ major: 3, minor: 6, patch: 2 }))) {
      console.error("Maven 3.6.0+ could not be identified.");
      return false;
    }

    if (!vscode.workspace.workspaceFolders || vscode.workspace.workspaceFolders.length === 0) {
      console.error("There isn't any workspace folder on VS Code.");
      return false;
    }

    return true;
  }

  public execute(baseDir: string, runnerClass: string): Promise<CapabilityResponse<string>> {
    if (!fs.existsSync(path.join(baseDir, "pom.xml"))) {
      return Promise.reject(`Unable to find a pom.xml file inside ${baseDir}`);
    }

    if (!fs.existsSync(path.join(baseDir, "src", "test", "java", runnerClass.replace(".", path.sep) + ".java"))) {
      return Promise.reject(
        "Unable to find the given scenario simulation runner class file inside the src/test/java folder"
      );
    }

    return new Promise((resolve, reject) => {
      const command = `mvn clean test -f ${baseDir}`;
      this.activeProcess = cp.exec(command, () => {
        const resultFile = path.join(baseDir, "target", "surefire-reports", runnerClass + ".txt");
        if (!fs.existsSync(resultFile)) {
          reject("Test report file could not be found.");
          return;
        }
        resolve(CapabilityResponse.ok(fs.readFileSync(resultFile).toString()));
      });
    });
  }
}
