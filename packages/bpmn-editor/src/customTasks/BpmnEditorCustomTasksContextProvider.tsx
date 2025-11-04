/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useContext, useMemo } from "react";
import { CustomTask } from "../BpmnEditor";

export interface BpmnEditorCustomTasksContextType {
  customTasks?: CustomTask[];
  customTasksPaletteIcon?: React.ReactNode;
}

const BpmnEditorCustomTasksContext = React.createContext<BpmnEditorCustomTasksContextType>({} as any);

export function useCustomTasks() {
  return useContext(BpmnEditorCustomTasksContext);
}

export function BpmnEditorCustomTasksContextProvider(
  _props: React.PropsWithChildren<BpmnEditorCustomTasksContextType>
) {
  const { children, ...props } = _props;

  const value = useMemo<BpmnEditorCustomTasksContextType>(
    () => ({
      customTasks: props.customTasks,
      customTasksPaletteIcon: props.customTasksPaletteIcon,
    }),
    [props.customTasks, props.customTasksPaletteIcon]
  );

  return <BpmnEditorCustomTasksContext.Provider value={value}>{children}</BpmnEditorCustomTasksContext.Provider>;
}
