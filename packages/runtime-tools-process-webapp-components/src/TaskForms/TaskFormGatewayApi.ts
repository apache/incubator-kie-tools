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

export interface TaskFormGatewayApi {
  getTaskFormSchema(userTask: UserTaskInstance, headers?: any): Promise<Record<string, any>>;

  getCustomForm(userTask: UserTaskInstance, headers?: any): Promise<Form>;

  doSubmit(userTask: UserTaskInstance, phase: string, payload: any, headers?: any): Promise<any>;

  getTaskPhases(userTask: UserTaskInstance, headers?: any): Promise<string[]>;
}

export class TaskFormGatewayApiImpl implements TaskFormGatewayApi {
  constructor(
    private readonly getCurrentUser: () => User,
    private transformEndpointBaseUrl: (url: string) => string
  ) {}

  submitTaskForm(endpoint: string, transition: UserTaskTransitionInfo, headers?: any) {
    return new Promise<any>((resolve, reject) => {
      axios
        .post(this.transformEndpointBaseUrl(endpoint), transition, {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            ...headers,
          },
        })
        .then((response) => {
          if (response.status == 200) {
            resolve(response.data);
          } else {
            reject(response);
          }
        })
        .catch((error) => reject(error));
    });
  }

  fetchTaskFormSchema(endpoint: string, headers?: any) {
    return new Promise<Record<string, any>>((resolve, reject) => {
      axios
        .get(this.transformEndpointBaseUrl(endpoint), {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            ...headers,
          },
        })
        .then((response) => {
          if (response.status == 200) {
            resolve(response.data);
          } else {
            reject(response);
          }
        })
        .catch((error) => reject(error));
    });
  }

  fetchCustomForm(endpoint: string, headers?: any) {
    return new Promise<Form>((resolve, reject) => {
      axios
        .get(this.transformEndpointBaseUrl(endpoint), {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            ...headers,
          },
        })
        .then((response) => {
          if (response.status == 200) {
            resolve(response.data);
          } else {
            reject(response);
          }
        })
        .catch((error) => reject(error));
    });
  }

  fetchTaskTransitionPhases(endpoint: string, headers?: any) {
    return new Promise<string[]>((resolve, reject) => {
      axios
        .get(this.transformEndpointBaseUrl(endpoint), {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            ...headers,
          },
        })
        .then((response) => {
          if (response.status == 200) {
            const transitions: UserTaskTransition[] = response.data;
            resolve(transitions.map((transition) => transition.transitionId));
          } else {
            reject(response);
          }
        })
        .catch((error) => reject(error));
    });
  }

  doSubmit(userTask: UserTaskInstance, phase: string, payload: any, headers?: any): Promise<any> {
    const endpoint = getTaskTransitionsEndpoint(userTask, this.getCurrentUser());
    const transition: UserTaskTransitionInfo = {
      transitionId: phase,
      data: payload,
    };
    return this.submitTaskForm(endpoint, transition, headers);
  }

  getTaskFormSchema(userTask: UserTaskInstance, headers?: any): Promise<Record<string, any>> {
    const endpoint = getTaskSchemaEndPoint(userTask, this.getCurrentUser());
    return this.fetchTaskFormSchema(endpoint, headers);
  }

  getCustomForm(userTask: UserTaskInstance, headers?: any): Promise<Form> {
    const baseUrl = cleanUserTaskEndpoint(userTask);
    const endpoint = `${baseUrl}/forms/${userTask.processId}_${userTask.name}`;
    return this.fetchCustomForm(endpoint, headers);
  }

  getTaskPhases(userTask: UserTaskInstance, headers?: any): Promise<string[]> {
    const endpoint = getTaskTransitionsEndpoint(userTask, this.getCurrentUser());
    return this.fetchTaskTransitionPhases(endpoint, headers);
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
