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

export const enableAutoRunID: string = "extendedServices.enableAutorun";
export const connectionHeartbeatIntervalinSecsID: string = "extendedServices.connectionHeartbeatIntervalinSecs";
export const extendedServicesURLID: string = "extendedServices.extendedServicesURL";

export class Configuration {
  readonly enableAutoRun: boolean;
  readonly connectionHeartbeatIntervalinSecs: number;
  readonly extendedServicesURL: URL;

  constructor(enableAutoRun: boolean, connectionHeartbeatIntervalinSecs: number, extendedServicesURL: URL) {
    this.enableAutoRun = enableAutoRun;
    this.connectionHeartbeatIntervalinSecs = connectionHeartbeatIntervalinSecs;
    this.extendedServicesURL = extendedServicesURL;
  }
}

function fetchEnableAutoRun(): boolean | undefined {
  const enableAutoRun = vscode.workspace.getConfiguration().get<boolean>(enableAutoRunID);
  if (!enableAutoRun) {
    vscode.window.showErrorMessage("Enable Auto Run configuration not found");
  }
  return enableAutoRun;
}

function fetchConnectionHeartbeatIntervalinSecs(): number | undefined {
  const connectionHeartbeatIntervalinSecs = vscode.workspace
    .getConfiguration()
    .get<number>(connectionHeartbeatIntervalinSecsID);
  if (!connectionHeartbeatIntervalinSecs) {
    vscode.window.showErrorMessage("Connection Heartbeat Interval configuration not found");
  }

  return connectionHeartbeatIntervalinSecs;
}

function fetchExtendedServicesURL(): URL | undefined {
  const extendedServicesURL = vscode.workspace.getConfiguration().get<string>(extendedServicesURLID);
  if (!extendedServicesURL) {
    vscode.window.showErrorMessage("Extended Services URL configuration not found");
    return undefined;
  }

  try {
    return new URL(extendedServicesURL);
  } catch (error) {
    vscode.window.showErrorMessage("Invalid service URL:" + error.message);
    return undefined;
  }
}

export function fetchConfiguration(): Configuration | undefined {
  const enableAutoRun = fetchEnableAutoRun();
  const connectionHeartbeatIntervalinSecs = fetchConnectionHeartbeatIntervalinSecs();
  const extendedServicesURL = fetchExtendedServicesURL();

  let configuration;

  if (enableAutoRun && connectionHeartbeatIntervalinSecs && extendedServicesURL) {
    configuration = new Configuration(enableAutoRun, connectionHeartbeatIntervalinSecs, extendedServicesURL);
  }

  return configuration;
}
