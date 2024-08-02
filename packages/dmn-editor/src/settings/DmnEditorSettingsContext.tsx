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

export interface DmnEditorSettingsContextType {
  readOnly: boolean;
}

const DmnEditorSettingsContext = React.createContext<DmnEditorSettingsContextType>({} as any);

export function useSettings() {
  return useContext(DmnEditorSettingsContext);
}

export function DmnEditorSettingsContextProvider(_props: React.PropsWithChildren<{ readOnly?: boolean }>) {
  const { children, ...props } = _props;

  const value = useMemo<DmnEditorSettingsContextType>(
    () => ({
      readOnly: props.readOnly ?? false,
    }),
    [props.readOnly]
  );

  return <DmnEditorSettingsContext.Provider value={value}>{children}</DmnEditorSettingsContext.Provider>;
}
