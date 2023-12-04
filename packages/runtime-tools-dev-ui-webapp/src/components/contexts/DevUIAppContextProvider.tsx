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
import { User } from "@kie-tools/runtime-tools-components/dist/consolesCommon/environment/auth";
import RuntimeToolsDevUIAppContext, { DevUIAppContextImpl } from "./DevUIAppContext";
import { CustomLabels } from "../../api/CustomLabels";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowDetails/api";

interface IOwnProps {
  users: User[];
  devUIUrl: string;
  openApiPath: string;
  isProcessEnabled: boolean;
  isTracingEnabled: boolean;
  availablePages: string[];
  customLabels?: CustomLabels;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const DevUIAppContextProvider: React.FC<IOwnProps> = ({
  users,
  devUIUrl,
  openApiPath,
  isProcessEnabled,
  isTracingEnabled,
  availablePages,
  customLabels,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  isStunnerEnabled,
  children,
}) => {
  return (
    <RuntimeToolsDevUIAppContext.Provider
      value={
        new DevUIAppContextImpl({
          users,
          devUIUrl,
          openApiPath,
          isProcessEnabled,
          isTracingEnabled,
          availablePages,
          customLabels,
          omittedProcessTimelineEvents,
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
