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

export class Configuration {
  static readonly autoRunConfigurationID: string = "extendedServices.enableAutorun";
  static readonly serviceURLConfigurationID: string = "extendedServices.serviceURL";
  static readonly connectionHeartbeatIntervalConfigurationID: string = "extendedServices.connectionHeartbeatInterval";

  readonly autoRun: boolean;
  readonly serviceURL: URL;
  readonly connectionHeartbeatInterval: number;

  constructor(autoRun: boolean, serviceURL: URL, connectionHeartbeatInterval: number) {
    this.autoRun = autoRun;
    this.serviceURL = serviceURL;
    this.connectionHeartbeatInterval = connectionHeartbeatInterval;
  }

  private static fetchAutoRun(): boolean | undefined {
    const autoRunConfig = vscode.workspace.getConfiguration().get<boolean>(Configuration.autoRunConfigurationID);
    if (autoRunConfig !== undefined) {
      try {
        return autoRunConfig;
      } catch (error) {
        vscode.window.showErrorMessage("Invalid Auto Run:" + error.message);
        return undefined;
      }
    } else {
      vscode.window.showErrorMessage("Auto Run configuration not found");
      return undefined;
    }
  }

  private static fetchServiceURL(): URL | undefined {
    const serviceURLConfig = vscode.workspace.getConfiguration().get<string>(Configuration.serviceURLConfigurationID);
    if (serviceURLConfig !== undefined) {
      try {
        return new URL(serviceURLConfig);
      } catch (error) {
        vscode.window.showErrorMessage("Invalid service URL:" + error.message);
        return undefined;
      }
    } else {
      vscode.window.showErrorMessage("Service URL configuration not found");
      return undefined;
    }
  }

  private static fetchConnectionHeartbeatInterval(): number | undefined {
    const connectionHeartbeatIntervalConfig = vscode.workspace
      .getConfiguration()
      .get<number>(Configuration.connectionHeartbeatIntervalConfigurationID);
    if (connectionHeartbeatIntervalConfig !== undefined) {
      try {
        return connectionHeartbeatIntervalConfig;
      } catch (error) {
        vscode.window.showErrorMessage("Invalid Heartbeat Interval: " + error.message);
        return undefined;
      }
    } else {
      vscode.window.showErrorMessage("Connection Heartbeat Interval configuration not found");
      return undefined;
    }
  }

  public static fetchConfiguration(): Configuration | undefined {
    const autoRunConfig = Configuration.fetchAutoRun();
    const serviceURLConfig = Configuration.fetchServiceURL();
    const connectionHeartbeatIntervalConfig = Configuration.fetchConnectionHeartbeatInterval();
    if (
      autoRunConfig !== undefined &&
      serviceURLConfig !== undefined &&
      connectionHeartbeatIntervalConfig !== undefined
    ) {
      const configuration: Configuration = {
        autoRun: autoRunConfig,
        serviceURL: serviceURLConfig,
        connectionHeartbeatInterval: connectionHeartbeatIntervalConfig,
      };
      return configuration;
    } else {
      return undefined;
    }
  }
}
