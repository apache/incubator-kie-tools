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

export const enableAutoRunID = "extendedServices.enableAutorun";
export const connectionHeartbeatIntervalInSecsID = "extendedServices.connectionHeartbeatIntervalInSecs";
export const extendedServicesURLID = "extendedServices.extendedServicesURL";

type ConfigurationProperty = string | number | boolean;

export class Configuration {
  readonly enableAutoRun: boolean;
  readonly connectionHeartbeatIntervalInSecs: number;
  readonly extendedServicesURL: URL;

  constructor(enableAutoRun: boolean, connectionHeartbeatIntervalInSecs: number, extendedServicesURL: URL) {
    this.enableAutoRun = enableAutoRun;
    this.connectionHeartbeatIntervalInSecs = connectionHeartbeatIntervalInSecs;
    this.extendedServicesURL = extendedServicesURL;
  }
}

function fetchExtendedServicesURL(): URL {
  const defaultExtendedServicesURL = `http://${process.env.WEBPACK_REPLACE__extendedServicesUrlHost}:${process.env.WEBPACK_REPLACE__extendedServicesUrlPort}`;
  const extendedServicesURL = getConfigurationPropertyValue<string>(extendedServicesURLID, defaultExtendedServicesURL);
  try {
    return new URL(extendedServicesURL);
  } catch (error) {
    throw new Error(`URL configuration ${extendedServicesURL} is invalid: ${error.message}`);
  }
}

const getConfigurationPropertyValue = <T extends ConfigurationProperty | null>(
  property: string,
  defaultValue: T
): T => {
  let value = vscode.workspace.getConfiguration().get(property) as T;
  if (value === null) {
    console.warn(`Property: ${property} is missing, using the default: ${defaultValue}`);
    value = defaultValue;
  }
  return value;
};

export function fetchConfiguration(): Configuration {
  const enableAutoRun = getConfigurationPropertyValue<boolean>(enableAutoRunID, true);
  const connectionHeartbeatIntervalInSecs = getConfigurationPropertyValue<number>(
    connectionHeartbeatIntervalInSecsID,
    10
  );
  const extendedServicesURL = fetchExtendedServicesURL();

  return new Configuration(enableAutoRun, connectionHeartbeatIntervalInSecs, extendedServicesURL);
}
