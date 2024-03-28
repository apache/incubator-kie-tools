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

import * as React from "react";
import { useImperativeHandle, useState } from "react";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { WorkflowDetailsChannelApi, WorkflowDetailsInitArgs } from "../api";
import WorkflowDetails from "./components/WorkflowDetails/WorkflowDetails";
import WorkflowDetailsEnvelopeViewDriver from "./WorkflowDetailsEnvelopeViewDriver";
import { WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

export interface WorkflowDetailsEnvelopeViewApi {
  initialize: (initArgs: WorkflowDetailsInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<WorkflowDetailsChannelApi>;
}

export const WorkflowDetailsEnvelopeView = React.forwardRef<WorkflowDetailsEnvelopeViewApi, Props>(
  (props, forwardedRef) => {
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
    const [workflowInstance, setWorkflowInstance] = useState<WorkflowInstance>({} as WorkflowInstance);
    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: /* istanbul ignore next */ (initArgs) => {
          setWorkflowInstance(initArgs.workflowInstance);
          setEnvelopeConnectedToChannel(true);
        },
      }),
      []
    );

    return (
      <React.Fragment>
        <WorkflowDetails
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          driver={new WorkflowDetailsEnvelopeViewDriver(props.channelApi)}
          workflowDetails={workflowInstance}
        />
      </React.Fragment>
    );
  }
);

export default WorkflowDetailsEnvelopeView;
