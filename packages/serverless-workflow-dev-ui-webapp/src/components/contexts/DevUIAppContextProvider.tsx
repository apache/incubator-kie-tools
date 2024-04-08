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

import React from "react";
import RuntimeToolsDevUIAppContext, { DevUIAppContextImpl } from "./DevUIAppContext";
import { CustomLabels } from "../../api/CustomLabels";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/api";

interface IOwnProps {
  devUIUrl: string;
  openApiBaseUrl: string;
  openApiPath: string;
  isWorkflowEnabled: boolean;
  availablePages: string[];
  omittedWorkflowTimelineEvents: string[];
  diagramPreviewSize: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const DevUIAppContextProvider: React.FC<IOwnProps> = ({
  devUIUrl,
  openApiBaseUrl,
  openApiPath,
  isWorkflowEnabled: isWorkflowEnabled,
  availablePages,
  omittedWorkflowTimelineEvents,
  diagramPreviewSize,
  isStunnerEnabled,
  children,
}) => {
  return (
    <RuntimeToolsDevUIAppContext.Provider
      value={
        new DevUIAppContextImpl({
          devUIUrl,
          openApiBaseUrl,
          openApiPath,
          isWorkflowEnabled,
          availablePages,
          omittedWorkflowTimelineEvents,
          diagramPreviewSize,
          isStunnerEnabled,
        })
      }
    >
      {children}
    </RuntimeToolsDevUIAppContext.Provider>
  );
};

export default DevUIAppContextProvider;
