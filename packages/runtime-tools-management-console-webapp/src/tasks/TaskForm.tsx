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
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { EmbeddedTaskForm } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { useTaskFormGatewayApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskForms";

interface Props {
  userTask: UserTaskInstance;
  onSubmitFormSuccess: (message: string) => void;
  onSubmitFormError: (message: string, details?: string) => void;
  onUnauthorized: (error: any) => void;
  accessToken?: string;
  username?: string;
}

export const TaskForm: React.FC<Props> = ({
  userTask,
  onSubmitFormSuccess,
  onSubmitFormError,
  onUnauthorized,
  accessToken,
  username,
}) => {
  const gatewayApi = useTaskFormGatewayApi();
  const requestHeaders = useMemo(
    () =>
      accessToken
        ? {
            Authorization: `Bearer ${accessToken}`,
          }
        : undefined,
    [accessToken]
  );

  return (
    <EmbeddedTaskForm
      userTask={userTask}
      user={{ id: username ?? "", groups: [] }}
      driver={{
        doSubmit(phase?: string, payload?: any): Promise<any> {
          return gatewayApi
            .doSubmit(userTask, phase ?? "", payload, requestHeaders)
            .then((result) => onSubmitFormSuccess(phase ?? ""))
            .catch(async (error) => {
              if (error?.response?.status === 401) {
                await onUnauthorized(error);
              }
              const details = error.response?.data?.message ? error.response.data.message : error.message;
              onSubmitFormError(phase ?? "", details);
            });
        },
        getTaskFormSchema(): Promise<Record<string, any>> {
          return gatewayApi.getTaskFormSchema(userTask, requestHeaders).catch(async (error) => {
            if (error?.response?.status === 401) {
              await onUnauthorized(error);
            }
            return error;
          });
        },
        getCustomForm(): Promise<Form> {
          // No-op
          return gatewayApi.getCustomForm(userTask);
        },
        getTaskPhases(): Promise<string[]> {
          return gatewayApi.getTaskPhases(userTask, requestHeaders).catch(async (error) => {
            if (error?.response?.status === 401) {
              await onUnauthorized(error);
            }
            return error;
          });
        },
      }}
      targetOrigin={window.location.origin}
    />
  );
};
