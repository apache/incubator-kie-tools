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

import * as childProcess from "child_process";
import * as path from "path";
import * as vscode from "vscode";

export class LocalExtendedServices {
  readonly startLocalExtendedServicesCommandUID: string =
    "extended-services-vscode-extension.startLocalExtendedServices";
  readonly stopLocalExtendedServicesCommandUID: string = "extended-services-vscode-extension.stopLocalExtendedServices";

  private startLocalExtendedServicesCommand: vscode.Disposable;
  private stopLocalExtendedServicesCommand: vscode.Disposable;

  private startLocalExtendedServicesCommandHandler = (extendedServicesURL: URL, extensionAbsolutePath: string) => {
    this.start(extendedServicesURL, extensionAbsolutePath);
  };
  private stopLocalExtendedServicesCommandHandler = () => {
    this.stop();
  };

  private localExtendedServicesStartedHandler: (() => void) | null = null;
  private localExtendedServicesStoppedHandler: (() => void) | null = null;

  private readonly esNormalizedFsPathRelativeToWorkspace: string = "dist/extended-services-java";
  private readonly esJarFileName: string = "quarkus-run.jar";

  private serviceProcess: childProcess.ChildProcess | null = null;

  constructor() {
    this.startLocalExtendedServicesCommand = vscode.commands.registerCommand(
      this.startLocalExtendedServicesCommandUID,
      this.startLocalExtendedServicesCommandHandler
    );
    this.stopLocalExtendedServicesCommand = vscode.commands.registerCommand(
      this.stopLocalExtendedServicesCommandUID,
      this.stopLocalExtendedServicesCommandHandler
    );
  }

  private start(extendedServicesURL: URL, extensionAbsoluteFsPath: string): void {
    const jarAbsoluteFilePath: string = path.join(
      extensionAbsoluteFsPath,
      this.esNormalizedFsPathRelativeToWorkspace,
      this.esJarFileName
    );
    const hostname = extendedServicesURL.hostname;
    const port = extendedServicesURL.port;
    const command =
      "java -jar -Dquarkus.http.host=" + hostname + " -Dquarkus.http.port=" + port + " " + jarAbsoluteFilePath;

    const options: childProcess.SpawnOptions = {
      shell: true,
      stdio: "pipe",
    };

    try {
      this.serviceProcess = childProcess.spawn(command, options);

      if (this.serviceProcess.stdout) {
        this.serviceProcess.stdout.on("data", (data) => {
          const output = data.toString();
          if (output.includes("Listening on: " + extendedServicesURL.origin)) {
            this.fireLocalExtendedServicesStartedEvent();
          }
        });
      }

      if (this.serviceProcess.stderr) {
        this.serviceProcess.stderr.on("data", (data) => {
          const errorOutput = data.toString();
          vscode.window.showErrorMessage(errorOutput);
        });
      }

      this.serviceProcess.on("exit", (code, signal) => {
        this.fireLocalExtendedServicesStoppedEvent();
      });
    } catch (error) {
      vscode.window.showErrorMessage("Error running local service: " + error.message);
    }
  }

  private stop(): void {
    if (this.serviceProcess) {
      if (!this.serviceProcess.kill()) {
        vscode.window.showErrorMessage("Local service failed to terminate.");
      }
      this.serviceProcess = null;
    }
  }

  private fireLocalExtendedServicesStartedEvent() {
    this.localExtendedServicesStartedHandler?.();
  }

  private fireLocalExtendedServicesStoppedEvent() {
    this.localExtendedServicesStoppedHandler?.();
  }

  public subscribeLocalExtendedServicesStarted(handler: () => void) {
    this.localExtendedServicesStartedHandler = handler;
  }

  public subscribeLocalExtendedServicesStopped(handler: () => void) {
    this.localExtendedServicesStoppedHandler = handler;
  }

  public unsubscribeLocalExtendedServicesStarted() {
    this.localExtendedServicesStartedHandler = null;
  }

  public unsubscribeLocalExtendedServicesStopped() {
    this.localExtendedServicesStoppedHandler = null;
  }

  public dispose(): void {
    this.stop();
    this.unsubscribeLocalExtendedServicesStarted();
    this.unsubscribeLocalExtendedServicesStopped();
    this.startLocalExtendedServicesCommand.dispose();
    this.stopLocalExtendedServicesCommand.dispose();
  }
}
