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
import {
  OnRequestExternalModelsAvailableToInclude,
  OnRequestExternalModelByPath,
  ExternalModelsIndex,
} from "../BpmnEditor";

export interface BpmnEditorExternalModelsContextType {
  onRequestExternalModelByPath?: OnRequestExternalModelByPath;
  onRequestExternalModelsAvailableToInclude?: OnRequestExternalModelsAvailableToInclude;
  externalModelsByNamespace?: ExternalModelsIndex;
}

const BpmnEditorExternalModelsContext = React.createContext<BpmnEditorExternalModelsContextType>({} as any);

export function useExternalModels() {
  return useContext(BpmnEditorExternalModelsContext);
}

export function BpmnEditorExternalModelsContextProvider(
  _props: React.PropsWithChildren<BpmnEditorExternalModelsContextType>
) {
  const { children, ...props } = _props;

  const value = useMemo<BpmnEditorExternalModelsContextType>(() => {
    return {
      externalModelsByNamespace: props.externalModelsByNamespace,
      onRequestExternalModelByPath: props.onRequestExternalModelByPath,
      onRequestExternalModelsAvailableToInclude: props.onRequestExternalModelsAvailableToInclude,
    };
  }, [
    props.externalModelsByNamespace,
    props.onRequestExternalModelByPath,
    props.onRequestExternalModelsAvailableToInclude,
  ]);

  return <BpmnEditorExternalModelsContext.Provider value={value}>{children}</BpmnEditorExternalModelsContext.Provider>;
}
