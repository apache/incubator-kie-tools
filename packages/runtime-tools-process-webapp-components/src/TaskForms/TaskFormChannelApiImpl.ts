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
import {
  UserTaskInstance,
  UserTaskTransition,
  UserTaskTransitionInfo,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import axios from "axios";
import { ANONYMOUS_USER, User } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { TaskFormChannelApi } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";

export class TaskFormChannelApiImpl implements TaskFormChannelApi {
  constructor(
    private readonly getCurrentUser: () => User,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined; token?: string }
  ) {}

  taskForm__getTaskFormSchema(userTask: UserTaskInstance): Promise<Record<string, any>> {
    const endpoint = getTaskSchemaEndPoint(userTask, this.getCurrentUser());
    return this.fetchTaskFormSchema(endpoint, getHeaders(this.options?.token));
  }

  taskForm__getCustomForm(userTask: UserTaskInstance): Promise<Form> {
    const baseUrl = cleanUserTaskEndpoint(userTask);
    const endpoint = `${baseUrl}/forms/${userTask.processId}_${userTask.name}`;
    return this.fetchCustomForm(endpoint, getHeaders(this.options?.token));
  }

  taskForm__doSubmit(userTask: UserTaskInstance, phase: string, payload: any): Promise<any> {
    const endpoint = getTaskTransitionsEndpoint(userTask, this.getCurrentUser());
    const transition: UserTaskTransitionInfo = {
      transitionId: phase,
      data: payload,
    };
    return this.submitTaskForm(endpoint, transition, getHeaders(this.options?.token));
  }

  taskForm__getTaskPhases(userTask: UserTaskInstance): Promise<string[]> {
    const endpoint = getTaskTransitionsEndpoint(userTask, this.getCurrentUser());
    return this.fetchTaskTransitionPhases(endpoint, getHeaders(this.options?.token));
  }

  submitTaskForm(endpoint: string, transition: UserTaskTransitionInfo, headers?: any) {
    return axios
      .post(this.options?.transformEndpointBaseUrl?.(endpoint) ?? endpoint, transition, {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
          ...headers,
        },
      })
      .then((response) => {
        if (response.status == 200) {
          return response.data;
        } else {
          throw response;
        }
      });
  }

  fetchTaskFormSchema(endpoint: string, headers?: any) {
    return axios
      .get(this.options?.transformEndpointBaseUrl?.(endpoint) ?? endpoint, {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
          ...headers,
        },
      })
      .then((response) => {
        if (response.status == 200) {
          return response.data;
        } else {
          throw response;
        }
      });
  }

  fetchCustomForm(endpoint: string, headers?: any) {
    return axios
      .get(this.options?.transformEndpointBaseUrl?.(endpoint) ?? endpoint, {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
          ...headers,
        },
      })
      .then((response) => {
        if (response.status == 200) {
          return response.data;
        } else {
          throw response;
        }
      });
  }

  fetchTaskTransitionPhases(endpoint: string, headers?: any) {
    return axios
      .get(this.options?.transformEndpointBaseUrl?.(endpoint) ?? endpoint, {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
          ...headers,
        },
      })
      .then((response) => {
        if (response.status == 200) {
          const transitions: UserTaskTransition[] = response.data;
          return transitions.map((transition) => transition.transitionId);
        } else {
          throw response;
        }
      });
  }
}

function getTaskTransitionsEndpoint(task: UserTaskInstance, user?: User): string {
  const baseUrl = cleanUserTaskEndpoint(task);

  const params = getTaskEndpointSecurityParams(task, user);

  return `${baseUrl}/usertasks/instance/${task.id}/transition?${params}`;
}

function getTaskSchemaEndPoint(task: UserTaskInstance, user?: User): string {
  let endpoint = task.endpoint;

  // Getting the schema from the workItem schema endpoint to avoid phases calculation
  endpoint = endpoint!.slice(0, -(task.id.length + 1));
  endpoint = endpoint.replace(task.processInstanceId + "/", "");

  return `${endpoint}/schema`;
}

function getTaskEndpointSecurityParams(task: UserTaskInstance, user?: User): string {
  if (!user || user.id === ANONYMOUS_USER.id) {
    return `user=${task.potentialUsers?.[0] ?? "Dev User"}`;
  }

  let groups = "";

  if (user.groups && user.groups.length > 0) {
    groups = `&group=${user.groups.join("&group=")}`;
  }
  return `${user.id ? `user=${user.id}` : ""}${groups}`;
}

function cleanUserTaskEndpoint(task: UserTaskInstance): string {
  const suffix = calculateUserTaskEndpointSuffix(task);
  return task.endpoint!.slice(0, -suffix.length);
}

function calculateUserTaskEndpointSuffix(task: UserTaskInstance): string {
  return `/${task.processId}/${task.processInstanceId}/${task.name}/${task.externalReferenceId}`;
}

function getHeaders(token?: string) {
  if (!token) {
    return {};
  }
  return {
    Authorization: `Bearer ${token}`,
  };
}
