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

import React, { useContext } from "react";
import { CustomLabels } from "../../api/CustomLabels";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/api";

export interface DevUIAppContext {
  availablePages?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  getDevUIUrl(): string;
  getIsStunnerEnabled(): boolean;
  getOpenApiPath(): string;
  isLocalCluster?: boolean;
  isStunnerEnabled: boolean;
  isWorkflowEnabled: boolean;
  omittedWorkflowTimelineEvents: string[];
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export type DevUIAppContextArgs = {
  availablePages?: string[];
  devUIUrl: string;
  diagramPreviewSize?: DiagramPreviewSize;
  isLocalCluster?: boolean;
  isStunnerEnabled: boolean;
  isWorkflowEnabled: boolean;
  omittedWorkflowTimelineEvents: string[];
  openApiBaseUrl: string;
  openApiPath: string;
};

export class DevUIAppContextImpl implements DevUIAppContext {
  constructor(private readonly args: DevUIAppContextArgs) {}

  getDevUIUrl(): string {
    return this.args.devUIUrl;
  }

  getOpenApiBaseUrl(): string {
    return this.args.openApiBaseUrl;
  }

  getOpenApiPath(): string {
    return this.args.openApiPath;
  }

  getIsStunnerEnabled(): boolean {
    return this.args.isStunnerEnabled;
  }

  get isWorkflowEnabled(): boolean {
    return this.args.isWorkflowEnabled;
  }

  get isStunnerEnabled(): boolean {
    return this.args.isStunnerEnabled;
  }

  get availablePages(): string[] {
    return this.args.availablePages || [];
  }

  get omittedWorkflowTimelineEvents(): string[] {
    return this.args.omittedWorkflowTimelineEvents;
  }

  get diagramPreviewSize(): DiagramPreviewSize | undefined {
    return this.args.diagramPreviewSize;
  }

  get isLocalCluster(): boolean {
    return this.args.isLocalCluster || false;
  }
}

const RuntimeToolsDevUIAppContext = React.createContext<DevUIAppContext>({} as DevUIAppContext);

export default RuntimeToolsDevUIAppContext;

export const useDevUIAppContext = () => useContext<DevUIAppContext>(RuntimeToolsDevUIAppContext);
