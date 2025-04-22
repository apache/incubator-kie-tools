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
import { TaskListState } from "./TaskListApi";

export interface OnOpenTaskListener {
  onOpen: (userTask: UserTaskInstance) => void;
}

export interface OnUpdateTaskListStateListener {
  onUpdate: (taskListState: TaskListState) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

/**
 * Channel Api for Task Inbox
 */
export interface TaskListChannelApi {
  /**
   * Initializes the channel with an initial state. This will only be called if the channel doesn't provide a default
   * state to TaskList.
   * @param initState
   */
  taskList__setInitialState(initState: TaskListState): Promise<void>;

  /**
   * Sets a filter to be applied to the queries.
   * @param filter
   */
  taskList__applyFilter(filter: TaskListQueryFilter): Promise<void>;

  /**
   * Sets a sorting to be applied to the queries.
   * @param sortBy
   */
  taskList__applySorting(sortBy: TaskListSortBy): Promise<void>;

  /**
   * Requests the channel to query a range of user tasks. The query must apply the filters and sorting configured.
   * @param offset - the starting index of the query.
   * @param limit - the maximum number of results expected.
   *
   * @return a Promise<UserTaskInstance[]> that will be resolved with the query result
   */
  taskList__query(offset: number, limit: number): Promise<UserTaskInstance[]>;

  /**
   * Notifies the channel that a UserTaskInstance has been opened in TaskList
   * @param task
   */
  taskList__openTask(task: UserTaskInstance): void;

  /**
   * Get a UserTaskInstance by its unique identifier.
   * @param uuid - the unique identifier of the UserTaskInstance to be fetched.
   */
  taskList__getTaskById(uuid: string): Promise<UserTaskInstance | undefined>;

  /**
   * Clears the active open task from the state.
   */
  taskList__clearOpenTask: () => Promise<void>;

  /**
   * Register a listener for when a UserTaskInstance is opened.
   * @param listener - the listener to be notified when a UserTaskInstance is opened.
   * @returns a function that can be called to unsubscribe the listener.
   */
  taskList__onOpenTaskListen(listener: OnOpenTaskListener): Promise<UnSubscribeHandler>;

  /**
   *
   * @param listener - the listener to be notified when the TaskList state is updated.
   */
  taskList__onUpdateTaskListState(listener: OnUpdateTaskListStateListener): Promise<UnSubscribeHandler>;
}
