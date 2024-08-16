/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import { User } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";

export const RuntimeToolsDevUIEnvelopeView = React.forwardRef<RuntimeToolsDevUIEnvelopeViewApi>(
  (props, forwardingRef) => {
    const [dataIndexUrl, setDataIndexUrl] = React.useState("");
    const [quarkusAppOrigin, setQuarkusAppOrigin] = React.useState("");
    const [quarkusAppRootPath, setQuarkusAppRootPath] = React.useState("");
    const [shouldReplaceQuarkusAppOriginWithWebappOrigin, setShouldReplaceQuarkusAppOriginWithWebappOrigin] =
      React.useState<boolean>(false);
    const [DevUiUsers, setDevUiUsers] = React.useState<User[]>([]);
    const [navigate, setNavigate] = React.useState<string>("");
    const [devUIOrigin, setDevUIOrigin] = React.useState<string>("");
    const [devUIUrl, setDevUIUrl] = React.useState<string>("");
    const [isProcessEnabled, setProcessEnabled] = React.useState(false);
    const [availablePages, setAvailablePages] = React.useState<string[]>([]);
    const [customLabels, setCustomLabels] = React.useState(undefined);
    const [omittedProcessTimelineEvents, setOmittedProcessTimelineEvents] = React.useState<string[]>([]);
    const [diagramPreviewSize, setDiagramPreviewSize] = React.useState<DiagramPreviewSize>();

    useImperativeHandle(forwardingRef, () => {
      return {
        setDataIndexUrl: (dataIndexUrl) => {
          setDataIndexUrl(dataIndexUrl);
        },
        setQuarkusAppOrigin: (quarkusAppOrigin: string) => {
          setQuarkusAppOrigin(quarkusAppOrigin);
        },
        setQuarkusAppRootPath: (quarkusAppRootPath: string) => {
          setQuarkusAppRootPath(quarkusAppRootPath);
        },
        setShouldReplaceQuarkusAppOriginWithWebappOrigin: (shouldReplaceQuarkusAppOriginWithWebappOrigin: boolean) => {
          setShouldReplaceQuarkusAppOriginWithWebappOrigin(shouldReplaceQuarkusAppOriginWithWebappOrigin);
        },
        setUsers: (users) => {
          setDevUiUsers(users);
        },
        navigateTo: (page) => {
          setNavigate(page);
        },
        setDevUIUrl: (url) => {
          setDevUIUrl(url);
        },
        setDevUIOrigin: (url) => {
          setDevUIOrigin(url);
        },
        setProcessEnabled: (isProcessEnabled) => {
          setProcessEnabled(isProcessEnabled);
        },
        setAvailablePages: (availablePages) => {
          setAvailablePages(availablePages);
        },
        setCustomLabels: (customLabels) => {
          setCustomLabels(customLabels);
        },
        setOmittedProcessTimelineEvents: (omittedProcessTimelineEvents) => {
          setOmittedProcessTimelineEvents(omittedProcessTimelineEvents);
        },
        setDiagramPreviewSize: (diagramPreviewSize) => {
          setDiagramPreviewSize(diagramPreviewSize);
        },
      };
    }, []);
    return (
      <>
        {isProcessEnabled && navigate.length > 0 && (
          <RuntimeTools
            users={DevUiUsers}
            dataIndexUrl={dataIndexUrl}
            navigate={navigate}
            devUIOrigin={devUIOrigin}
            devUIUrl={devUIUrl}
            isProcessEnabled={isProcessEnabled}
            availablePages={availablePages}
            customLabels={customLabels}
            omittedProcessTimelineEvents={omittedProcessTimelineEvents}
            diagramPreviewSize={diagramPreviewSize}
            quarkusAppOrigin={quarkusAppOrigin}
            quarkusAppRootPath={quarkusAppRootPath}
            shouldReplaceQuarkusAppOriginWithWebappOrigin={shouldReplaceQuarkusAppOriginWithWebappOrigin}
          />
        )}
      </>
    );
  }
);
