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
import { ApolloClient } from "apollo-client";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { GraphQL } from "@kie-tools/runtime-tools-process-gateway-api/dist/graphql";
import { User } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { QueryFilter, SortBy } from "@kie-tools/runtime-tools-process-enveloped-components/src/taskInbox";
import { buildTaskInboxWhereArgument, getOrderByObject } from "../utils/QueryUtils";

export interface TaskInboxQueries {
  getUserTaskById(taskId: string): Promise<UserTaskInstance | undefined>;

  getUserTasks(
    user: User,
    start: number,
    end: number,
    filters: QueryFilter,
    sortBy: SortBy
  ): Promise<UserTaskInstance[]>;
}

export class GraphQLTaskInboxQueries implements TaskInboxQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  getUserTaskById(taskId: string): Promise<UserTaskInstance | undefined> {
    return new Promise<UserTaskInstance | undefined>((resolve, reject) => {
      this.client
        .query({
          query: GraphQL.GetUserTaskByIdDocument,
          variables: {
            id: taskId,
          },
          fetchPolicy: "network-only",
        })
        .then((value) => {
          if (value.data.UserTaskInstances && value.data.UserTaskInstances.length > 0) {
            resolve(value.data.UserTaskInstances[0]);
            return;
          }
          resolve(undefined);
        })
        .catch((reason) => reject(reason));
    });
  }

  getUserTasks(
    user: User,
    offset: number,
    limit: number,
    filters: QueryFilter,
    sortBy: SortBy
  ): Promise<UserTaskInstance[]> {
    return new Promise<UserTaskInstance[]>((resolve, reject) => {
      this.client
        .query({
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            whereArgument: buildTaskInboxWhereArgument(user, filters),
            offset: offset,
            limit: limit,
            orderBy: getOrderByObject(sortBy),
          },
          fetchPolicy: "network-only",
        })
        .then((value) => {
          resolve(value.data.UserTaskInstances);
        })
        .catch((reason) => reject(reason));
    });
  }
}
