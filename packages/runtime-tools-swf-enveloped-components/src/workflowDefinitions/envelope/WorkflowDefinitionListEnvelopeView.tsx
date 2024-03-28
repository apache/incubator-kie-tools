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

import React, { useImperativeHandle, useState } from "react";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { WorkflowDefinitionListChannelApi, WorkflowDefinitionListInitArgs } from "../api";
import WorkflowDefinitionList from "./components/WorkflowDefinitionList/WorkflowDefinitionList";
import WorkflowDefinitionListEnvelopeViewDriver from "./WorkflowDefinitionListEnvelopeViewDriver";

export interface WorkflowDefinitionListEnvelopeViewApi {
  initialize: (initArgs: WorkflowDefinitionListInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<WorkflowDefinitionListChannelApi>;
}

export const WorkflowDefinitionListEnvelopeView = React.forwardRef<WorkflowDefinitionListEnvelopeViewApi, Props>(
  (props, forwardedRef) => {
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);

    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: (initArgs) => {
          setEnvelopeConnectedToChannel(true);
        },
      }),
      []
    );

    return (
      <React.Fragment>
        <WorkflowDefinitionList
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          driver={new WorkflowDefinitionListEnvelopeViewDriver(props.channelApi)}
        />
      </React.Fragment>
    );
  }
);

export default WorkflowDefinitionListEnvelopeView;
