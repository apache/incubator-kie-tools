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
import { ChildProcess, SpawnOptions, spawn } from "child_process";
import { Configuration } from "./configurations/Configuration";

export class LocalExtendedServices {
  private localExtendedServicesStartedHandler: ((configuration: Configuration) => void) | null = null;
  private localExtendedServicesOutputChangedHandler: ((output: string) => void) | null = null;
  private localExtendedServicesErrorOutputChangedHandler: ((output: string) => void) | null = null;
  private localExtendedServicesStoppedHandler: (() => void) | null = null;

  private serviceProcess: ChildProcess | null = null;

  public start(configuration: Configuration, extensionAbsoluteFsPath: string): void {
    const distDirectory: string = "dist";
    const extendedServicesDirectory: string = "extended-services-java";
    const extendedServicesJarFileName: string = "quarkus-run.jar";
    const jarAbsoluteFilePath: string = path.join(
      extensionAbsoluteFsPath,
      distDirectory,
      extendedServicesDirectory,
      extendedServicesJarFileName
    );

    const hostname = configuration.extendedServicesURL.hostname;
    const port = configuration.extendedServicesURL.port;
    const command =
      "java -jar -Dquarkus.http.host=" + hostname + " -Dquarkus.http.port=" + port + " " + jarAbsoluteFilePath;

    const options: SpawnOptions = {
      shell: true,
      stdio: "pipe",
    };

    try {
      this.serviceProcess = spawn(command, options);

      if (this.serviceProcess.stdout) {
        this.serviceProcess.stdout.on("data", (data) => {
          const output = data.toString();
          if (output.includes("Listening on: " + configuration.extendedServicesURL.origin)) {
            this.fileLocalExtendedServicesOutputChangedEvent(output);
            this.fireLocalExtendedServicesStartedEvent(configuration);
          }
        });
      }

      if (this.serviceProcess.stderr) {
        this.serviceProcess.stderr.on("data", (data) => {
          const errorOutput = data.toString();
          this.fileLocalExtendedServicesErrorOutputChangedEvent(errorOutput);
        });
      }

      this.serviceProcess.on("exit", () => {
        this.fireLocalExtendedServicesStoppedEvent();
      });
    } catch (error) {
      throw new Error("LOCAL EXTENDED SERVICES ERROR: " + error.message);
    }
  }

  public stop(): void {
    if (this.serviceProcess && this.serviceProcess.pid) {
      process.kill(this.serviceProcess.pid);
    }

    this.serviceProcess = null;
  }

  private fireLocalExtendedServicesStartedEvent(configuration: Configuration) {
    this.localExtendedServicesStartedHandler?.(configuration);
  }

  private fileLocalExtendedServicesOutputChangedEvent(output: string) {
    this.localExtendedServicesOutputChangedHandler?.(output);
  }

  private fileLocalExtendedServicesErrorOutputChangedEvent(output: string) {
    this.localExtendedServicesErrorOutputChangedHandler?.(output);
  }

  private fireLocalExtendedServicesStoppedEvent() {
    this.localExtendedServicesStoppedHandler?.();
  }

  public subscribeLocalExtendedServicesStarted(handler: (configuration: Configuration) => void) {
    this.localExtendedServicesStartedHandler = handler;
  }

  public subscribeLocalExtendedServicesOutputChanged(handler: (output: string) => void) {
    this.localExtendedServicesOutputChangedHandler = handler;
  }

  public subscribeLocalExtendedServicesErrorOutputChanged(handler: (output: string) => void) {
    this.localExtendedServicesErrorOutputChangedHandler = handler;
  }

  public subscribeLocalExtendedServicesStopped(handler: () => void) {
    this.localExtendedServicesStoppedHandler = handler;
  }

  public unsubscribeLocalExtendedServicesStarted() {
    this.localExtendedServicesStartedHandler = null;
  }

  public unsubscribeLocalExtendedServicesOutputChanged() {
    this.localExtendedServicesOutputChangedHandler = null;
  }

  public unsubscribeLocalExtendedServicesErrorOutputChanged() {
    this.localExtendedServicesErrorOutputChangedHandler = null;
  }

  public unsubscribeLocalExtendedServicesStopped() {
    this.localExtendedServicesStoppedHandler = null;
  }

  public dispose(): void {
    this.stop();
    this.unsubscribeLocalExtendedServicesStarted();
    this.unsubscribeLocalExtendedServicesOutputChanged();
    this.unsubscribeLocalExtendedServicesStopped();
  }
}
