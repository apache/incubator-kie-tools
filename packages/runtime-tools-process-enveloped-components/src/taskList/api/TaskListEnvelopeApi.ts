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
  QueryPage,
  TaskListQueryFilter,
  TaskListSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { TaskListState } from "./TaskListApi";

/**
 * Envelope Api
 */
export interface TaskListEnvelopeApi {
  /**
   * Initializes the envelope.
   * @param association
   * @param initArgs
   */
  taskList__init(association: Association, initArgs: TaskListInitArgs): Promise<void>;
  taskList__notify(userName: string): Promise<void>;
}

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export interface TaskListInitArgs {
  initialState?: TaskListState;
  allTaskStates?: string[];
  activeTaskStates?: string[];
}
