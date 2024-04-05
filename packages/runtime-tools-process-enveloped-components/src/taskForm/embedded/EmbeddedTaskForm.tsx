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
import React, { useCallback } from "react";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tools-core/envelope/dist/embedded";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { TaskFormApi, TaskFormChannelApi, TaskFormDriver, TaskFormEnvelopeApi, User } from "../api";
import { EmbeddedTaskFormChannelApiImpl } from "./EmbeddedTaskFormChannelApiImpl";
import { init } from "../envelope";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";

export interface EmbeddedTaskFormProps {
  targetOrigin: string;
  userTask: UserTaskInstance;
  driver: TaskFormDriver;
  user: User;
}

export const EmbeddedTaskForm = React.forwardRef(
  (props: EmbeddedTaskFormProps, forwardedRef: React.Ref<TaskFormApi>) => {
    const refDelegate = useCallback(
      (envelopeServer: EnvelopeServer<TaskFormChannelApi, TaskFormEnvelopeApi>): TaskFormApi => ({}),
      []
    );
    const pollInit = useCallback(
      (envelopeServer: EnvelopeServer<TaskFormChannelApi, TaskFormEnvelopeApi>, container: () => HTMLDivElement) => {
        init({
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
          targetOrigin: props.targetOrigin,
        });
        return envelopeServer.envelopeApi.requests.taskForm__init(
          {
            origin: envelopeServer.origin,
            envelopeServerId: envelopeServer.id,
          },
          { userTask: props.userTask, user: props.user }
        );
      },
      []
    );

    return (
      <EmbeddedTaskFormEnvelope
        ref={forwardedRef}
        apiImpl={new EmbeddedTaskFormChannelApiImpl(props.driver)}
        origin={props.targetOrigin}
        refDelegate={refDelegate}
        pollInit={pollInit}
        config={{ containerType: ContainerType.DIV }}
      />
    );
  }
);

const EmbeddedTaskFormEnvelope = React.forwardRef<
  TaskFormApi,
  EmbeddedEnvelopeProps<TaskFormChannelApi, TaskFormEnvelopeApi, TaskFormApi>
>(RefForwardingEmbeddedEnvelope);
