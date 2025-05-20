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
import React, { useCallback, useEffect, useImperativeHandle, useState } from "react";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { TaskFormChannelApi, TaskFormInitArgs, User } from "../api";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import CustomTaskFormDisplayer from "./components/CustomTaskFormDisplayer/CustomTaskFormDisplayer";
import TaskForm from "./components/TaskForm/TaskForm";

import "@patternfly/patternfly/patternfly.css";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { filterTaskPhases } from "./components/TaskFormRenderer/TaskPhasesUtils";

export interface TaskFormEnvelopeViewApi {
  initialize: (initArgs: TaskFormInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<TaskFormChannelApi>;
  targetOrigin: string;
}

export const TaskFormEnvelopeView = React.forwardRef<TaskFormEnvelopeViewApi, Props>(
  ({ channelApi, targetOrigin }, forwardedRef) => {
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
    const [userTask, setUserTask] = useState<UserTaskInstance>();
    const [user, setUser] = useState<User>();
    const [taskFormSchema, setTaskFormSchema] = useState<Record<string, any>>();
    const [customForm, setCustomForm] = useState<Form>();
    const [userTaskPhases, setUserTaskPhases] = useState<string[]>([]);

    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: (initArgs: TaskFormInitArgs) => {
          setEnvelopeConnectedToChannel(true);
          setUserTask(initArgs.userTask);
          setUser(initArgs.user);
        },
      }),
      []
    );

    useCancelableEffect(
      useCallback(
        ({ canceled }) => {
          if (!isEnvelopeConnectedToChannel || !userTask) {
            setIsLoading(true);
          } else {
            const customFormPromise = channelApi.requests
              .taskForm__getCustomForm(userTask)
              .then((customForm) => {
                if (canceled.get()) {
                  return;
                }
                setCustomForm(customForm);
                return;
              })
              .catch(() => {
                // no-op
                return;
              });

            const schemaPromise = channelApi.requests
              .taskForm__getTaskFormSchema(userTask)
              .then((schema) => {
                if (canceled.get()) {
                  return;
                }
                setTaskFormSchema(schema);
              })
              .catch(() => {
                // no-op
                return;
              });

            const phasesPromise = channelApi.requests
              .taskForm__getTaskPhases(userTask)
              .then((phases) => {
                if (canceled.get()) {
                  return;
                }
                setUserTaskPhases(filterTaskPhases(phases));
                return;
              })
              .catch(() => {
                // no-op
                return;
              });

            Promise.all([customFormPromise, schemaPromise, phasesPromise]).then(() => {
              setIsLoading(false);
            });
          }
        },
        [channelApi.requests, userTask, isEnvelopeConnectedToChannel]
      )
    );

    if (isLoading) {
      return (
        <Bullseye>
          <KogitoSpinner spinnerText={`Loading Task form...`} />
        </Bullseye>
      );
    }

    if (taskFormSchema) {
      if (customForm) {
        return (
          <CustomTaskFormDisplayer
            userTask={userTask!}
            schema={taskFormSchema}
            customForm={customForm}
            user={user!}
            channelApi={channelApi}
            phases={userTaskPhases}
            targetOrigin={targetOrigin}
          />
        );
      }
      return <TaskForm userTask={userTask!} schema={taskFormSchema} phases={userTaskPhases} channelApi={channelApi} />;
    }

    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Info}
        title="No form to show"
        body={`Cannot find form for task  ${userTask!.referenceName} (${userTask!.id.substring(0, 5)})`}
      />
    );
  }
);

export default TaskFormEnvelopeView;
