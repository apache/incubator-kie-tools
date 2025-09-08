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
import React, { useCallback, useMemo } from "react";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tools-core/envelope/dist/embedded";
import { TaskListApi, TaskListChannelApi, TaskListEnvelopeApi, TaskListState } from "../api";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import { init } from "../envelope";

export interface Props {
  targetOrigin: string;
  initialState?: TaskListState;
  channelApi: TaskListChannelApi;
  allTaskStates?: string[];
  activeTaskStates?: string[];
}

export const EmbeddedTaskList = React.forwardRef((props: Props, forwardedRef: React.Ref<TaskListApi>) => {
  const refDelegate = useCallback(
    (envelopeServer: EnvelopeServer<TaskListChannelApi, TaskListEnvelopeApi>): TaskListApi => ({
      taskList__notify: (userName) => envelopeServer.envelopeApi.requests.taskList__notify(userName),
    }),
    []
  );
  const pollInit = useCallback(
    async (
      envelopeServer: EnvelopeServer<TaskListChannelApi, TaskListEnvelopeApi>,
      container: () => HTMLDivElement
    ) => {
      await init({
        config: {
          containerType: ContainerType.DIV,
          envelopeId: envelopeServer.id,
        },
        container: container(),
        bus: {
          postMessage(message, targetOrigin, transfer) {
            window.postMessage(message, targetOrigin!, transfer);
          },
        },
      });
      return await envelopeServer.envelopeApi.requests.taskList__init(
        {
          origin: envelopeServer.origin,
          envelopeServerId: envelopeServer.id,
        },
        {
          initialState: props.initialState,
          allTaskStates: props.allTaskStates,
          activeTaskStates: props.activeTaskStates,
        }
      );
    },
    [props.initialState, props.allTaskStates, props.activeTaskStates]
  );

  return (
    <EmbeddedTaskListEnvelope
      ref={forwardedRef}
      apiImpl={props.channelApi}
      origin={props.targetOrigin}
      refDelegate={refDelegate}
      pollInit={pollInit}
      config={{ containerType: ContainerType.DIV }}
    />
  );
});

const EmbeddedTaskListEnvelope = React.forwardRef<
  TaskListApi,
  EmbeddedEnvelopeProps<TaskListChannelApi, TaskListEnvelopeApi, TaskListApi>
>(RefForwardingEmbeddedEnvelope);
