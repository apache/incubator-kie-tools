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

import { ping } from "./requests/PingRequest";
import { PingResponse } from "./requests/PingResponse";

export class Connection {
  private connectedHandler: (() => void) | null = null;
  private connectionLostHandler: ((errorMessage: string) => void) | null = null;
  private disconnectedHandler: (() => void) | null = null;

  private timeout: NodeJS.Timeout | null = null;
  private connected: boolean = false;

  public async start(extendedServicesURL: URL, connectionHeartbeatIntervalInSecs: number): Promise<void> {
    this.timeout = setInterval(async () => {
      this.performHeartbeatCheck(extendedServicesURL);
    }, connectionHeartbeatIntervalInSecs * 1000);
  }

  public stop(): void {
    console.debug("[Extended Services Extension] Disconnecting from Extended Service");
    if (this.timeout) {
      this.fireDisconnectedEvent();
      clearInterval(this.timeout);
      this.timeout = null;
      this.connected = false;
    }
  }

  private async performHeartbeatCheck(extendedServicesURL: URL): Promise<void> {
    try {
      const pingResponse: PingResponse = await ping(extendedServicesURL);
      if (pingResponse.started && !this.connected) {
        this.fireConnectedEvent();
        this.connected = true;
      }
    } catch (error) {
      this.fireConnectionLost(error.message);
    }
  }

  private fireConnectedEvent() {
    this.connectedHandler?.();
  }

  private fireConnectionLost(errorMessage: string) {
    this.connectionLostHandler?.(errorMessage);
  }

  private fireDisconnectedEvent() {
    this.disconnectedHandler?.();
  }

  public subscribeConnected(handler: () => void) {
    this.connectedHandler = handler;
  }

  public subscribeConnectionLost(handler: (errorMessage: string) => void) {
    this.connectionLostHandler = handler;
  }

  public subscribeDisconnected(handler: () => void) {
    this.disconnectedHandler = handler;
  }

  public unsubscribeConnected() {
    this.connectedHandler = null;
  }

  public unsubscribeConnectionLost() {
    this.connectionLostHandler = null;
  }

  public unsubscribeDisconnected() {
    this.disconnectedHandler = null;
  }

  public dispose(): void {
    this.stop();
    this.unsubscribeConnected();
    this.unsubscribeConnectionLost();
    this.unsubscribeDisconnected();
  }
}
