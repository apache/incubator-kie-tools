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

import * as vscode from "vscode";
import { JITCommands } from "./jit-executor/jit-commands";
import { PingResponse } from "./jit-executor/responses";

export class ConnectionManager {
  readonly startConnectionHeartbeatCommandUID: string = "extended-services-vscode-extension.startConnectionHeartbeat";
  readonly stopConnectionHeartbeatCommandUID: string = "extended-services-vscode-extension.stopConnectionHeartbeat";

  private startConnectionHeartbeatCommand: vscode.Disposable;
  private stopConnectionHeartbeatCommand: vscode.Disposable;

  private connectedHandler: ConnectedHandler | null = null;
  private disconnectedHandler: DisconnectedHandler | null = null;

  private context: vscode.ExtensionContext;
  private lastPingStarted: boolean = false;

  private timeout: NodeJS.Timeout | null = null;

  constructor(context: vscode.ExtensionContext) {
    this.context = context;
    this.initializeCommands();
  }

  private initializeCommands(): void {
    const startConnectionHeartbeatCommandHandler = (serviceURL: URL, connectionHeartbeatInterval: number) => {
      this.start(serviceURL, connectionHeartbeatInterval);
    };

    const stopConnectionHeartbeatCommandHandler = () => {
      this.stop();
    };

    this.startConnectionHeartbeatCommand = vscode.commands.registerCommand(
      this.startConnectionHeartbeatCommandUID,
      startConnectionHeartbeatCommandHandler
    );
    this.stopConnectionHeartbeatCommand = vscode.commands.registerCommand(
      this.stopConnectionHeartbeatCommandUID,
      stopConnectionHeartbeatCommandHandler
    );

    this.context.subscriptions.push(this.startConnectionHeartbeatCommand);
    this.context.subscriptions.push(this.stopConnectionHeartbeatCommand);
  }

  private async performHeartbeatCheck(serviceURL: URL): Promise<void> {
    try {
      const pingResponse: PingResponse = await JITCommands.ping(serviceURL);
      if (pingResponse.started != this.lastPingStarted) {
        if (pingResponse.started) {
          this.fireConnectedEvent();
        } else {
          this.fireDisconnectedEvent();
        }
        this.lastPingStarted = pingResponse.started;
      }
    } catch (error) {
      this.fireDisconnectedEvent();
      this.stopHeartbeatCheck();
      vscode.window.showErrorMessage("Error performing heartbeat check: " + error.message);
    }
  }

  private stopHeartbeatCheck(): void {
    if (this.timeout) {
      this.fireDisconnectedEvent();
      clearInterval(this.timeout);
      this.lastPingStarted = false;
    }
  }

  private fireConnectedEvent() {
    if (this.connectedHandler) {
      this.connectedHandler();
    }
  }

  private fireDisconnectedEvent() {
    if (this.disconnectedHandler) {
      this.disconnectedHandler();
    }
  }

  private async start(serviceURL: URL, connectionHeartbeatInterval: number): Promise<void> {
    this.performHeartbeatCheck(serviceURL);
    this.timeout = setInterval(async () => {
      this.performHeartbeatCheck(serviceURL);
    }, connectionHeartbeatInterval * 1000);
  }

  private stop(): void {
    this.stopHeartbeatCheck();
  }

  public subscribeConnected(handler: ConnectedHandler) {
    this.connectedHandler = handler;
  }

  public subscribeDisconnected(handler: DisconnectedHandler) {
    this.disconnectedHandler = handler;
  }

  public unsubscribeConnected() {
    this.connectedHandler = null;
  }

  public unsubscribeDisconnected() {
    this.disconnectedHandler = null;
  }

  public dispose(): void {
    this.stop();
    this.startConnectionHeartbeatCommand.dispose();
    this.stopConnectionHeartbeatCommand.dispose();
    this.unsubscribeConnected();
    this.unsubscribeDisconnected();
    this.timeout = null;
  }
}

interface ConnectedHandler {
  (): void;
}

interface DisconnectedHandler {
  (): void;
}
