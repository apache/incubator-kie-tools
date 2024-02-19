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

export class LocalServiceManager {
  readonly startLocalServiceCommandUID: string = "extended-services-vscode-extension.startLocalService";
  readonly stopLocalServiceCommandUID: string = "extended-services-vscode-extension.stopLocalService";

  private readonly relativePath: string = "/dist-dev/extended-services-java";
  private readonly jarFileName: string = "quarkus-run.jar";

  private localServiceRunningHandler: LocalServiceRunningHandler | null = null;
  private localServiceTerminatedHandler: LocalServiceTerminatedHandler | null = null;

  private context: vscode.ExtensionContext;
  private startLocalServiceCommand: vscode.Disposable;
  private stopLocalServiceCommand: vscode.Disposable;
  private serviceProcess: childProcess.ChildProcess | undefined;

  constructor(context: vscode.ExtensionContext) {
    this.context = context;
    this.initializeCommands();
  }

  private initializeCommands(): void {
    const startLocalServiceCommandHandler = (serviceURL: URL) => {
      this.start(serviceURL);
    };

    const stopLocalServiceCommandHandler = () => {
      this.stop();
    };

    this.startLocalServiceCommand = vscode.commands.registerCommand(
      this.startLocalServiceCommandUID,
      startLocalServiceCommandHandler
    );
    this.stopLocalServiceCommand = vscode.commands.registerCommand(
      this.stopLocalServiceCommandUID,
      stopLocalServiceCommandHandler
    );

    this.context.subscriptions.push(this.startLocalServiceCommand);
    this.context.subscriptions.push(this.stopLocalServiceCommand);
  }

  private fireLocalServicesRunningEvent() {
    if (this.localServiceRunningHandler) {
      this.localServiceRunningHandler();
    }
  }

  private fireLocalServicesTerminatedEvent() {
    if (this.localServiceTerminatedHandler) {
      this.localServiceTerminatedHandler();
    }
  }

  public subscribeLocalServicesRunning(handler: LocalServiceRunningHandler) {
    this.localServiceRunningHandler = handler;
  }

  public subscribeLocalServicesTerminated(handler: LocalServiceTerminatedHandler) {
    this.localServiceTerminatedHandler = handler;
  }

  public unsubscribeLocalServicesRunning() {
    this.localServiceRunningHandler = null;
  }

  public unsubscribeLocalServicesTerminated() {
    this.localServiceTerminatedHandler = null;
  }

  private runLocalService(serviceURL: URL): void {
    const absolutePath = this.context.extensionPath;
    const jarFilePath = path.join(absolutePath, this.relativePath, this.jarFileName);
    const hostname = serviceURL.hostname;
    const port = serviceURL.port;
    const command = "java -jar -Dquarkus.http.host=" + hostname + " -Dquarkus.http.port=" + port + " " + jarFilePath;

    const options: childProcess.SpawnOptions = {
      shell: true,
      stdio: "pipe",
    };

    try {
      this.serviceProcess = childProcess.spawn(command, options);

      if (this.serviceProcess.stdout) {
        this.serviceProcess.stdout.on("data", (data) => {
          const output = data.toString();
          if (output.includes("Listening on: " + serviceURL.origin)) {
            this.fireLocalServicesRunningEvent();
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
        this.fireLocalServicesTerminatedEvent();
      });
    } catch (error) {
      vscode.window.showErrorMessage("Error running local service: " + error.message);
    }
  }

  private terminateLocalService(): void {
    if (this.serviceProcess) {
      if (!this.serviceProcess.kill()) {
        vscode.window.showErrorMessage("Local service failed to terminate.");
      }
      this.serviceProcess = undefined;
    }
  }

  private start(serviceURL: URL): void {
    this.runLocalService(serviceURL);
  }

  private stop(): void {
    this.terminateLocalService();
  }

  public dispose(): void {
    this.stop();
    this.startLocalServiceCommand.dispose();
    this.stopLocalServiceCommand.dispose();
  }
}

interface LocalServiceRunningHandler {
  (): void;
}

interface LocalServiceTerminatedHandler {
  (): void;
}
