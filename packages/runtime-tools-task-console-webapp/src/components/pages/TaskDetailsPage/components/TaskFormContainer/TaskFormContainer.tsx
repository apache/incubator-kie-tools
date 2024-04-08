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
import React from "react";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { useKogitoAppContext } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { EmbeddedTaskForm } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { useTaskFormGatewayApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskForms";

interface Props {
  userTask: UserTaskInstance;
  onSubmitSuccess: (message: string) => void;
  onSubmitError: (message: string, details?: string) => void;
}

const TaskFormContainer: React.FC<Props & OUIAProps> = ({
  userTask,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi = useTaskFormGatewayApi();
  const kogitoAppContext = useKogitoAppContext();

  return (
    <EmbeddedTaskForm
      {...componentOuiaProps(ouiaId, "task-form-container", ouiaSafe)}
      userTask={userTask}
      user={kogitoAppContext.getCurrentUser()}
      driver={{
        doSubmit(phase?: string, payload?: any): Promise<any> {
          return gatewayApi
            .doSubmit(userTask, phase, payload)
            .then((result) => onSubmitSuccess(phase))
            .catch((error) => {
              const message = error.response ? error.response.data : error.message;
              onSubmitError(phase, message);
            });
        },
        getTaskFormSchema(): Promise<Record<string, any>> {
          return gatewayApi.getTaskFormSchema(userTask);
        },
        getCustomForm(): Promise<Form> {
          return gatewayApi.getCustomForm(userTask);
        },
      }}
      targetOrigin={window.location.origin}
    />
  );
};

export default TaskFormContainer;
