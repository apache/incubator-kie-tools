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
import * as fs from "fs";
import { getPortPromise } from "portfinder";
import { LocalHttpServer } from "../api";
import * as utils from "./utils";

export class QuarkusLocalServer extends LocalHttpServer {
  private activeProcess: cp.ChildProcess | undefined;

  public constructor(private readonly jarFilePath: string) {
    super();
  }

  public identify(): string {
    return "QUARKUS_LOCAL_SERVER";
  }

  public async start(): Promise<void> {
    this.activeProcess = cp.spawn("java", [`-Dquarkus.http.port=${this.port}`, "-jar", this.jarFilePath]);

    const timeoutPromise = new Promise((resolve) => {
      setTimeout(() => {
        resolve(false);
      }, 5000);
    });

    const checkServerPromise = new Promise((resolve) => {
      if (!this.activeProcess || !this.activeProcess.stdout) {
        resolve(false);
        return;
      }

      this.activeProcess.stdout.on("data", (data) => {
        if (data.toString().includes("Listening on")) {
          resolve(true);
        }
      });
    });

    return Promise.race([timeoutPromise, checkServerPromise]).then((result: boolean) => {
      if (!result) {
        throw new Error("Could not start the Quarkus local server.");
      }
    });
  }

  public stop(): void {
    if (!this.activeProcess) {
      return;
    }
    utils.killProcess(this.activeProcess);
    this.activeProcess = undefined;
  }

  public async satisfyRequirements(): Promise<boolean> {
    if (!fs.existsSync(this.jarFilePath)) {
      console.error(`${this.jarFilePath} does not exist.`);
      return false;
    }

    if (!(await utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 }))) {
      console.error("Java 11.0.0+ could not be identified.");
      return false;
    }

    try {
      this.port = await getPortPromise({ port: 8082 });
      return true;
    } catch (e) {
      console.error(e);
      return false;
    }
  }
}
