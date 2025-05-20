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
import React, { ReactElement, useCallback } from "react";
import { TaskFormChannelApiImpl } from "./TaskFormChannelApiImpl";
import TaskFormContext from "./TaskFormContext";
import { useKogitoAppContext } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";

export interface TaskFormContextArgs {
  children: ReactElement;
  options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined; token?: string };
}

export const TaskFormContextProvider: React.FC<TaskFormContextArgs> = ({ children, options }) => {
  const appContext = useKogitoAppContext();

  return (
    <TaskFormContext.Provider value={new TaskFormChannelApiImpl(() => appContext.getCurrentUser(), options)}>
      {children}
    </TaskFormContext.Provider>
  );
};

export default TaskFormContextProvider;
