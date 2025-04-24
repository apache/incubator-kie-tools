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
import * as React from "react";
import { useImperativeHandle, useState } from "react";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { DiagramPreviewSize, ProcessDetailsChannelApi, ProcessDetailsInitArgs } from "../api";
import ProcessDetails from "./components/ProcessDetails/ProcessDetails";
import "@patternfly/patternfly/patternfly.css";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export interface ProcessDetailsEnvelopeViewApi {
  initialize: (initArgs: ProcessDetailsInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>;
}

export const ProcessDetailsEnvelopeView = React.forwardRef<ProcessDetailsEnvelopeViewApi, Props>(
  (props, forwardedRef) => {
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
    const [processInstance, setProcessInstance] = useState<ProcessInstance>({} as ProcessInstance);
    const [omittedProcessTimelineEvents, setOmittedProcessTimelineEvents] = useState<string[]>([]);
    const [diagramPreviewSize, setDiagramPreviewSize] = useState<DiagramPreviewSize>();
    const [singularProcessLabel, setSingularProcessLabel] = useState<string>("");
    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: /* istanbul ignore next */ (initArgs) => {
          setProcessInstance(initArgs.processInstance);
          setOmittedProcessTimelineEvents(initArgs.omittedProcessTimelineEvents!);
          setDiagramPreviewSize(initArgs.diagramPreviewSize);
          setSingularProcessLabel(initArgs.singularProcessLabel);
          setEnvelopeConnectedToChannel(true);
        },
      }),
      []
    );

    return (
      <React.Fragment>
        <ProcessDetails
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          channelApi={props.channelApi}
          processDetails={processInstance}
          omittedProcessTimelineEvents={omittedProcessTimelineEvents}
          diagramPreviewSize={diagramPreviewSize}
          singularProcessLabel={singularProcessLabel}
        />
      </React.Fragment>
    );
  }
);

export default ProcessDetailsEnvelopeView;
