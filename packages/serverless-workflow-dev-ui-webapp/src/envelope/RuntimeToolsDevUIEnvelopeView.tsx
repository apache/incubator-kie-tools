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

import React, { useImperativeHandle } from "react";
import "@patternfly/patternfly/patternfly.css";
import "@patternfly/react-core/dist/styles/base.css";
import { RuntimeToolsDevUIEnvelopeViewApi } from "./RuntimeToolsDevUIEnvelopeViewApi";
import RuntimeTools from "../components/DevUI/RuntimeTools/RuntimeTools";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/api";

export const RuntimeToolsDevUIEnvelopeView = React.forwardRef<RuntimeToolsDevUIEnvelopeViewApi>(
  (props, forwardingRef) => {
    const [dataIndexUrl, setDataIndexUrl] = React.useState("");
    const [navigate, setNavigate] = React.useState<string>("");
    const [devUIUrl, setDevUIUrl] = React.useState<string>("");
    const [openApiBaseUrl, setOpenApiBaseUrl] = React.useState<string>("");
    const [openApiPath, setOpenApiPath] = React.useState<string>("");
    const [isWorkflowEnabled, setWorkflowEnabled] = React.useState(false);
    const [availablePages, setAvailablePages] = React.useState<string[]>([]);
    const [omittedWorkflowTimelineEvents, setOmittedWorkflowTimelineEvents] = React.useState<string[]>([]);
    const [diagramPreviewSize, setDiagramPreviewSize] = React.useState<DiagramPreviewSize>();
    const [isStunnerEnabled, setIsStunnerEnabled] = React.useState<boolean>(false);

    useImperativeHandle(
      forwardingRef,
      () => {
        return {
          setDataIndexUrl: (dataIndexUrl) => {
            setDataIndexUrl(dataIndexUrl);
          },
          navigateTo: (page) => {
            setNavigate(page);
          },
          setDevUIUrl: (url) => {
            setDevUIUrl(url);
          },
          setOpenApiBaseUrl: (baseUrl) => {
            setOpenApiBaseUrl(baseUrl);
          },
          setOpenApiPath: (path) => {
            setOpenApiPath(path);
          },
          setWorkflowEnabled: (isWorkflowEnabled) => {
            setWorkflowEnabled(isWorkflowEnabled);
          },
          setAvailablePages: (availablePages) => {
            setAvailablePages(availablePages);
          },
          setOmittedWorkflowTimelineEvents: (omittedWorkflowTimelineEvents) => {
            setOmittedWorkflowTimelineEvents(omittedWorkflowTimelineEvents);
          },
          setDiagramPreviewSize: (diagramPreviewSize) => {
            setDiagramPreviewSize(diagramPreviewSize);
          },
          setIsStunnerEnabled: (isStunnerEnabled) => {
            setIsStunnerEnabled(isStunnerEnabled);
          },
        };
      },
      []
    );
    return (
      <>
        {isWorkflowEnabled && navigate.length > 0 && (
          <RuntimeTools
            dataIndexUrl={dataIndexUrl}
            navigate={navigate}
            openApiBaseUrl={openApiBaseUrl}
            openApiPath={openApiPath}
            devUIUrl={devUIUrl}
            isWorkflowEnabled={isWorkflowEnabled}
            availablePages={availablePages}
            omittedWorkflowTimelineEvents={omittedWorkflowTimelineEvents}
            diagramPreviewSize={diagramPreviewSize}
            isStunnerEnabled={isStunnerEnabled}
          />
        )}
      </>
    );
  }
);
