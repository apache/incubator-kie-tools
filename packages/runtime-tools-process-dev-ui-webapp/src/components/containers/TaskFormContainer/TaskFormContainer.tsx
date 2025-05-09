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
import React, { useMemo } from "react";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { useTaskFormChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskForms";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { EmbeddedTaskForm } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

interface Props {
  userTask: UserTaskInstance;
  onSubmitSuccess: (message: string) => void;
  onSubmitError: (message: string, details?: string) => void;
}

const TaskFormContainer: React.FC<Props> = ({ userTask, onSubmitSuccess, onSubmitError }) => {
  const channelApi = useTaskFormChannelApi();
  const appContext = useDevUIAppContext();

  const extendedChannelApi = useMemo(
    () => ({
      taskForm__doSubmit(userTask: UserTaskInstance, phase?: string, payload?: any): Promise<any> {
        return new Promise<any>((resolve, reject) => {
          channelApi
            .taskForm__doSubmit(userTask, phase, payload)
            .then((response) => {
              onSubmitSuccess(phase);
              resolve(response);
            })
            .catch((error) => {
              const details = error.response?.data?.message ? error.response.data.message : error.message;
              onSubmitError(phase ?? "", details);
              reject(error);
            });
        });
      },
      taskForm__getTaskFormSchema(userTask: UserTaskInstance): Promise<Record<string, any>> {
        return channelApi.taskForm__getTaskFormSchema(userTask);
      },
      taskForm__getCustomForm(userTask: UserTaskInstance): Promise<Form> {
        return channelApi.taskForm__getCustomForm(userTask);
      },
      taskForm__getTaskPhases(userTask: UserTaskInstance): Promise<string[]> {
        return channelApi.taskForm__getTaskPhases(userTask);
      },
    }),
    [channelApi, onSubmitError, onSubmitSuccess]
  );

  return (
    <EmbeddedTaskForm
      userTask={userTask}
      user={appContext.getCurrentUser()}
      channelApi={extendedChannelApi}
      targetOrigin={appContext.getDevUIUrl()}
    />
  );
};

export default TaskFormContainer;
