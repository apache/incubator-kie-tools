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
import { WorkflowListChannelApi, WorkflowListInitArgs } from "../api";
import WorkflowList from "./components/WorkflowList/WorkflowList";
import WorkflowListEnvelopeViewDriver from "./WorkflowListEnvelopeViewDriver";

export interface WorkflowListEnvelopeViewApi {
  initialize: (initialState?: WorkflowListInitArgs) => void;
}
interface Props {
  channelApi: MessageBusClientApi<WorkflowListChannelApi>;
}

export const WorkflowListEnvelopeView = React.forwardRef<WorkflowListEnvelopeViewApi, Props>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
  const [workflowInitialState, setWorkflowInitialState] = useState<WorkflowListInitArgs>({} as WorkflowListInitArgs);
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (initialState: WorkflowListInitArgs) => {
        setEnvelopeConnectedToChannel(false);
        setWorkflowInitialState(initialState);
        setEnvelopeConnectedToChannel(true);
      },
    }),
    []
  );

  return (
    <React.Fragment>
      <WorkflowList
        isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
        driver={new WorkflowListEnvelopeViewDriver(props.channelApi)}
        initialState={workflowInitialState.initialState}
      />
    </React.Fragment>
  );
});

export default WorkflowListEnvelopeView;
