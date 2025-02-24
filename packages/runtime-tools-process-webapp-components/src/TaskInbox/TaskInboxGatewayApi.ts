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
  QueryFilter,
  SortBy,
  TaskInboxState,
} from "@kie-tools/runtime-tools-process-enveloped-components/src/taskInbox";
import { TaskInboxQueries } from "./TaskInboxQueries";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { User } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";

export interface TaskInboxGatewayApi {
  taskInboxState: TaskInboxState;
  setInitialState: (initialState: TaskInboxState) => Promise<void>;
  applyFilter(filter: QueryFilter): Promise<void>;
  applySorting(sortBy: SortBy): Promise<void>;
  query(offset: number, limit: number): Promise<UserTaskInstance[]>;
  getTaskById(uuid: string): Promise<UserTaskInstance | undefined>;
  openTask: (userTask: UserTaskInstance) => void;
  clearOpenTask: () => Promise<void>;
  onOpenTaskListen: (listener: OnOpenTaskListener) => UnSubscribeHandler;
  onUpdateTaskListState(listener: OnUpdateTaskListStateListener): UnSubscribeHandler;
}

export interface OnOpenTaskListener {
  onOpen: (userTask: UserTaskInstance) => void;
}

export interface OnUpdateTaskListStateListener {
  onUpdate: (taskInboxState: TaskInboxState) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class TaskInboxGatewayApiImpl implements TaskInboxGatewayApi {
  private readonly onOpenTaskListeners: OnOpenTaskListener[] = [];
  private readonly onUpdateTaskListStateListeners: OnUpdateTaskListStateListener[] = [];
  private getCurrentUser: () => User;
  private readonly queries: TaskInboxQueries;
  private _taskInboxState: TaskInboxState;
  private activeTask: UserTaskInstance | null;

  constructor(queries: TaskInboxQueries, getCurrentUser: () => User) {
    this.queries = queries;
    this.getCurrentUser = getCurrentUser;
  }

  get taskInboxState(): TaskInboxState {
    return this._taskInboxState;
  }

  setInitialState(taskInboxState: TaskInboxState): Promise<void> {
    this._taskInboxState = taskInboxState;
    return Promise.resolve();
  }

  public clearOpenTask(): Promise<void> {
    this.activeTask = null;
    return Promise.resolve();
  }

  openTask(task: UserTaskInstance): Promise<void> {
    this.activeTask = task;
    this.onOpenTaskListeners.forEach((listener) => listener.onOpen(task));
    return Promise.resolve();
  }

  applyFilter(filter: QueryFilter): Promise<void> {
    this._taskInboxState.filters = filter;
    this.onUpdateTaskListStateListeners.forEach((listener) => listener.onUpdate(this._taskInboxState));
    return Promise.resolve();
  }

  applySorting(sortBy: SortBy): Promise<void> {
    this._taskInboxState.sortBy = sortBy;
    this.onUpdateTaskListStateListeners.forEach((listener) => listener.onUpdate(this._taskInboxState));
    return Promise.resolve();
  }

  getTaskById(taskId: string): Promise<UserTaskInstance | undefined> {
    if (this.activeTask && this.activeTask.id === taskId) {
      return Promise.resolve(this.activeTask);
    }
    return this.queries.getUserTaskById(taskId);
  }

  query(offset: number, limit: number): Promise<UserTaskInstance[]> {
    return new Promise<UserTaskInstance[]>((resolve, reject) => {
      this.queries
        .getAllTasks(offset, limit, this._taskInboxState.filters, this._taskInboxState.sortBy)
        .then((value) => {
          this._taskInboxState.currentPage = { offset, limit };
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  onOpenTaskListen(listener: OnOpenTaskListener): UnSubscribeHandler {
    this.onOpenTaskListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenTaskListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenTaskListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }

  onUpdateTaskListState(listener: OnUpdateTaskListStateListener): UnSubscribeHandler {
    this.onUpdateTaskListStateListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onUpdateTaskListStateListeners.indexOf(listener);
      if (index > -1) {
        this.onUpdateTaskListStateListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }
}
