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
  TaskListQueryFilter,
  TaskListSortBy,
  UserTaskInstance,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ApolloClient } from "apollo-client";
import {
  OnOpenTaskListener,
  OnUpdateTaskListStateListener,
  TaskListChannelApi,
  TaskListState,
  UnSubscribeHandler,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskList";
import { buildTaskListWhereArgument, getOrderByObject } from "../utils";
import { GraphQL } from "@kie-tools/runtime-tools-process-gateway-api/dist/graphql";

export class TaskListChannelApiImpl implements TaskListChannelApi {
  private readonly onOpenTaskListeners: OnOpenTaskListener[] = [];
  private readonly onUpdateTaskListStateListeners: OnUpdateTaskListStateListener[] = [];
  private _taskListState: TaskListState;
  private activeTask: UserTaskInstance | null;

  constructor(private readonly client: ApolloClient<any>) {}

  taskList__setInitialState(initState: TaskListState): Promise<void> {
    this._taskListState = initState;
    return Promise.resolve();
  }

  taskList__applyFilter(filter: TaskListQueryFilter): Promise<void> {
    this._taskListState.filters = filter;
    this.onUpdateTaskListStateListeners.forEach((listener) => listener.onUpdate(this._taskListState));
    return Promise.resolve();
  }

  taskList__applySorting(sortBy: TaskListSortBy): Promise<void> {
    this._taskListState.sortBy = sortBy;
    this.onUpdateTaskListStateListeners.forEach((listener) => listener.onUpdate(this._taskListState));
    return Promise.resolve();
  }

  taskList__query(offset: number, limit: number): Promise<UserTaskInstance[]> {
    return new Promise<UserTaskInstance[]>((resolve, reject) => {
      this.client
        .query({
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            whereArgument: buildTaskListWhereArgument(this._taskListState.filters),
            offset: offset,
            limit: limit,
            orderBy: getOrderByObject(this._taskListState.sortBy),
          },
          fetchPolicy: "network-only",
        })
        .then((value) => {
          this._taskListState.currentPage = { offset, limit };
          resolve(value.data.UserTaskInstances);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  taskList__openTask(task: UserTaskInstance): void {
    this.activeTask = task;
    this.onOpenTaskListeners.forEach((listener) => listener.onOpen(task));
  }

  taskList__getTaskById(uuid: string): Promise<UserTaskInstance | undefined> {
    if (this.activeTask && this.activeTask.id === uuid) {
      return Promise.resolve(this.activeTask);
    }
    return new Promise<UserTaskInstance | undefined>((resolve, reject) => {
      this.client
        .query({
          query: GraphQL.GetUserTaskByIdDocument,
          variables: {
            id: uuid,
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

  taskList__clearOpenTask() {
    this.activeTask = null;
    return Promise.resolve();
  }

  taskList__onOpenTaskListen(listener: OnOpenTaskListener): Promise<UnSubscribeHandler> {
    this.onOpenTaskListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenTaskListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenTaskListeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }

  taskList__onUpdateTaskListState(listener: OnUpdateTaskListStateListener): Promise<UnSubscribeHandler> {
    this.onUpdateTaskListStateListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onUpdateTaskListStateListeners.indexOf(listener);
      if (index > -1) {
        this.onUpdateTaskListStateListeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }
}
