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

import * as pingrequest from "./requests/PingRequest";
import * as pingresponse from "./requests/PingResponse";
import * as vscode from "vscode";

export class Connection {
  readonly startConnectionHeartbeatCommandUID: string = "extended-services-vscode-extension.startConnectionHeartbeat";
  readonly stopConnectionHeartbeatCommandUID: string = "extended-services-vscode-extension.stopConnectionHeartbeat";

  private startConnectionHeartbeatCommand: vscode.Disposable;
  private stopConnectionHeartbeatCommand: vscode.Disposable;

  private readonly startConnectionHeartbeatCommandHandler = (
    extendedServicesURL: URL,
    connectionHeartbeatIntervalInSecs: number
  ) => {
    this.start(extendedServicesURL, connectionHeartbeatIntervalInSecs);
  };
  private readonly stopConnectionHeartbeatCommandHandler = () => {
    this.stop();
  };

  private connectedHandler: (() => void) | null = null;
  private disconnectedHandler: (() => void) | null = null;

  private timeout: NodeJS.Timeout | null = null;
  private lastPingStarted: boolean = false;

  constructor() {
    this.startConnectionHeartbeatCommand = vscode.commands.registerCommand(
      this.startConnectionHeartbeatCommandUID,
      this.startConnectionHeartbeatCommandHandler
    );
    this.stopConnectionHeartbeatCommand = vscode.commands.registerCommand(
      this.stopConnectionHeartbeatCommandUID,
      this.stopConnectionHeartbeatCommandHandler
    );
  }

  private async performHeartbeatCheck(serviceURL: URL): Promise<void> {
    const pingResponse: pingresponse.PingResponse = await pingrequest.ping(serviceURL);
    if (pingResponse.started != this.lastPingStarted) {
      if (pingResponse.started) {
        this.fireConnectedEvent();
      } else {
        this.fireDisconnectedEvent();
      }
      this.lastPingStarted = pingResponse.started;
    }
  }

  private async start(extendedServicesURL: URL, connectionHeartbeatInterval: number): Promise<void> {
    this.performHeartbeatCheck(extendedServicesURL);
    this.timeout = setInterval(async () => {
      this.performHeartbeatCheck(extendedServicesURL);
    }, connectionHeartbeatInterval * 1000);
  }

  private stop(): void {
    if (this.timeout) {
      this.fireDisconnectedEvent();
      clearInterval(this.timeout);
      this.timeout = null;
      this.lastPingStarted = false;
    }
  }

  private fireConnectedEvent() {
    this.connectedHandler?.();
  }

  private fireDisconnectedEvent() {
    this.disconnectedHandler?.();
  }

  public subscribeConnected(handler: () => void) {
    this.connectedHandler = handler;
  }

  public subscribeDisconnected(handler: () => void) {
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
    this.unsubscribeConnected();
    this.unsubscribeDisconnected();
    this.startConnectionHeartbeatCommand.dispose();
    this.stopConnectionHeartbeatCommand.dispose();
  }
}
