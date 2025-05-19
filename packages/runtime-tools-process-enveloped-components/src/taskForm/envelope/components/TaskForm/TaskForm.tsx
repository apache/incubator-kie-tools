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
import React, { useCallback, useEffect, useState } from "react";
import get from "lodash/get";
import has from "lodash/has";
import isEmpty from "lodash/isEmpty";
import set from "lodash/set";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { TaskFormChannelApi } from "../../../api";
import EmptyTaskForm from "../EmptyTaskForm/EmptyTaskForm";
import TaskFormRenderer from "../TaskFormRenderer/TaskFormRenderer";
import { parseTaskSchema, TaskDataAssignments } from "../utils/TaskFormDataUtils";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export interface TaskFormProps {
  userTask: UserTaskInstance;
  schema: Record<string, any>;
  channelApi: MessageBusClientApi<TaskFormChannelApi>;
  phases: string[];
}

enum State {
  READY,
  SUBMITTING,
  SUBMITTED,
}

export const TaskForm: React.FC<TaskFormProps> = ({ userTask, schema, channelApi, phases }) => {
  const [formData, setFormData] = useState<any>(null);
  const [formState, setFormState] = useState<State>(State.READY);
  const [taskFormSchema, setTaskFormSchema] = useState<Record<string, any>>();
  const [taskFormAssignments, setTaskFormAssignments] = useState<TaskDataAssignments>();

  useEffect(() => {
    const parsedSchema = parseTaskSchema(schema);
    setTaskFormSchema(parsedSchema.schema);
    setTaskFormAssignments(parsedSchema.assignments);
  }, [schema]);

  const doSubmit = useCallback(
    async (phase: string, data: any, onSuccess?: (response: any) => void, onFailure?: (response: any) => void) => {
      try {
        setFormState(State.SUBMITTING);
        setFormData(data);

        const payload = {};

        taskFormAssignments!.outputs.forEach((output) => {
          if (has(data, output)) {
            set(payload, output, get(data, output));
          }
        });

        const result = await channelApi.requests.taskForm__doSubmit(userTask, phase, payload);
        if (onSuccess) {
          onSuccess(result);
        }
      } catch (err) {
        if (onFailure) {
          onFailure(err);
        }
      } finally {
        setFormState(State.SUBMITTED);
      }
    },
    [channelApi.requests, taskFormAssignments, userTask]
  );

  if (formState === State.SUBMITTING) {
    return (
      <Bullseye>
        <KogitoSpinner spinnerText={`Submitting for task ${userTask.referenceName} (${userTask.id.substring(0, 5)})`} />
      </Bullseye>
    );
  }

  if (formState === State.READY || formState === State.SUBMITTED) {
    if (!taskFormSchema) {
      return (
        <Bullseye>
          <KogitoSpinner spinnerText={`Loading Task form...`} />
        </Bullseye>
      );
    }

    if (isEmpty(taskFormSchema.properties)) {
      return (
        <EmptyTaskForm
          userTask={userTask}
          enabled={formState == State.READY}
          formSchema={taskFormSchema}
          submit={(phase) => doSubmit(phase, {})}
        />
      );
    }

    return (
      <TaskFormRenderer
        userTask={userTask}
        formSchema={taskFormSchema}
        formData={formData}
        enabled={formState == State.READY}
        phases={phases}
        submit={doSubmit}
      />
    );
  }

  return null;
};

export default TaskForm;
