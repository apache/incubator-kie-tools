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
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import axios from "axios";
import { User } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface TaskFormGatewayApi {
  getTaskFormSchema(userTask: UserTaskInstance): Promise<Record<string, any>>;

  getCustomForm(userTask: UserTaskInstance): Promise<Form>;

  doSubmit(userTask: UserTaskInstance, phase: string, payload: any): Promise<any>;
}

export class TaskFormGatewayApiImpl implements TaskFormGatewayApi {
  constructor(private readonly getCurrentUser: () => User) {}

  doSubmit(userTask: UserTaskInstance, phase: string, payload: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      const endpoint = `${userTask.endpoint}?phase=${phase}&${getTaskEndpointSecurityParams(this.getCurrentUser())}`;
      axios
        .post(endpoint, payload, {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
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

  getTaskFormSchema(userTask: UserTaskInstance): Promise<Record<string, any>> {
    return new Promise<Record<string, any>>((resolve, reject) => {
      const endpoint = getTaskSchemaEndPoint(userTask, this.getCurrentUser());
      axios
        .get(endpoint, {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
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

  getCustomForm(userTask: UserTaskInstance): Promise<Form> {
    return Promise.reject();
  }
}

function getTaskSchemaEndPoint(task: UserTaskInstance, user: User): string {
  let params = "";
  let endpoint = task.endpoint;

  if (task.completed) {
    // if task is completed we load the schema for the task definition
    endpoint = endpoint!.slice(0, -(task.id.length + 1));
    endpoint = endpoint.replace(task.processInstanceId + "/", "");
  } else {
    params = `?${getTaskEndpointSecurityParams(user)}`;
  }

  return `${endpoint}/schema${params}`;
}

function getTaskEndpointSecurityParams(user: User): string {
  let groups = "";

  if (user.groups && user.groups.length > 0) {
    groups = `&group=${user.groups.join("&group=")}`;
  }
  return `user=${user.id}${groups}`;
}
