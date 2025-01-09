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

function fetchEnableAutoRun(): boolean {
  const enableAutoRun = vscode.workspace.getConfiguration().get<boolean>(enableAutoRunID);
  if (!enableAutoRun) {
    throw new Error("Enable Auto Run configuration not found");
  }
  return enableAutoRun;
}

function fetchConnectionHeartbeatIntervalinSecs(): number {
  const connectionHeartbeatIntervalinSecs = vscode.workspace
    .getConfiguration()
    .get<number>(connectionHeartbeatIntervalinSecsID);
  if (!connectionHeartbeatIntervalinSecs) {
    throw new Error("Connection Heartbeat Interval configuration not found");
  }

  return connectionHeartbeatIntervalinSecs;
}

function fetchExtendedServicesURL(): URL {
  const extendedServicesURL = vscode.workspace.getConfiguration().get<string>(extendedServicesURLID);
  if (!extendedServicesURL) {
    throw new Error("URL configuration not found");
  }

  try {
    return new URL(extendedServicesURL);
  } catch (error) {
    throw new Error("Invalid service URL:" + error.message);
  }
}

export function fetchConfiguration(): Configuration {
  let errorMessages: string[] = [];
  let enableAutoRun: any;
  let connectionHeartbeatIntervalinSecs: any;
  let extendedServicesURL: any;

  try {
    enableAutoRun = fetchEnableAutoRun();
  } catch (error) {
    errorMessages.push(error.message);
  }

  try {
    connectionHeartbeatIntervalinSecs = fetchConnectionHeartbeatIntervalinSecs();
  } catch (error) {
    errorMessages.push(error.message);
  }

  try {
    extendedServicesURL = fetchExtendedServicesURL();
  } catch (error) {
    errorMessages.push(error.message);
  }

  if (errorMessages.length < 0) {
    throw new Error("CONFIGURATION ERROR - " + errorMessages.join(", "));
  } else {
    return new Configuration(enableAutoRun, connectionHeartbeatIntervalinSecs, extendedServicesURL);
  }
}
