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

import { CustomDashboardInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { getCustomDashboard } from "@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi";
import { CustomDashboardFilter } from "@kie-tools/runtime-tools-shared-enveloped-components/dist/customDashboardList";

/* eslint-disable @typescript-eslint/no-empty-interface */
export interface CustomDashboardListGatewayApi {
  getCustomDashboardFilter(): Promise<CustomDashboardFilter>;
  applyFilter(customDashboardFilter: CustomDashboardFilter): Promise<void>;
  getCustomDashboardsQuery(): Promise<CustomDashboardInfo[]>;
  openDashboard: (customDashboardInfo: CustomDashboardInfo) => Promise<void>;
  onOpenCustomDashboardListen: (listener: OnOpenDashboardListener) => UnSubscribeHandler;
}

export interface OnOpenDashboardListener {
  onOpen: (dashboardInfo: CustomDashboardInfo) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class CustomDashboardListGatewayApiImpl implements CustomDashboardListGatewayApi {
  private _CustomDashboardFilter: CustomDashboardFilter = {
    customDashboardNames: [],
  };
  private readonly listeners: OnOpenDashboardListener[] = [];

  getCustomDashboardFilter = (): Promise<CustomDashboardFilter> => {
    return Promise.resolve(this._CustomDashboardFilter);
  };

  applyFilter = (customDashboardFilter: CustomDashboardFilter): Promise<void> => {
    this._CustomDashboardFilter = customDashboardFilter;
    return Promise.resolve();
  };

  getCustomDashboardsQuery(): Promise<CustomDashboardInfo[]> {
    return getCustomDashboard(this._CustomDashboardFilter.customDashboardNames);
  }

  openDashboard = (customDashboardInfo: CustomDashboardInfo): Promise<void> => {
    this.listeners.forEach((listener) => listener.onOpen(customDashboardInfo));
    return Promise.resolve();
  };

  onOpenCustomDashboardListen(listener: OnOpenDashboardListener): UnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }
}
