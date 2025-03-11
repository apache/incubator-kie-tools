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
import { useContext, useMemo, useRef } from "react";
import { DmnEditorProps } from "./DmnEditor";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";

export type DmnEditorContextProviderProps = Pick<
  DmnEditorProps,
  | "externalContextDescription"
  | "externalContextName"
  | "issueTrackerHref"
  | "model"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
  | "evaluationResultsByNodeId"
>;

export type DmnModelBeforeEditing = DmnLatestModel;

export type DmnEditorContextType = Pick<
  DmnEditorContextProviderProps,
  | "externalContextDescription"
  | "externalContextName"
  | "issueTrackerHref"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
  | "evaluationResultsByNodeId"
> & {
  dmnModelBeforeEditingRef: React.MutableRefObject<DmnModelBeforeEditing>;
  dmnEditorRootElementRef: React.RefObject<HTMLDivElement>;
};

const DmnEditorContext = React.createContext<DmnEditorContextType>({} as any);

export function useDmnEditor() {
  return useContext(DmnEditorContext);
}

export function DmnEditorContextProvider(props: React.PropsWithChildren<DmnEditorContextProviderProps>) {
  const dmnModelBeforeEditingRef = useRef<DmnModelBeforeEditing>(props.model);
  const dmnEditorRootElementRef = useRef<HTMLDivElement>(null);

  const value = useMemo<DmnEditorContextType>(
    () => ({
      dmnModelBeforeEditingRef,
      dmnEditorRootElementRef,
      externalContextDescription: props.externalContextDescription,
      externalContextName: props.externalContextName,
      issueTrackerHref: props.issueTrackerHref,
      onRequestToJumpToPath: props.onRequestToJumpToPath,
      onRequestToResolvePath: props.onRequestToResolvePath,
      evaluationResultsByNodeId: props.evaluationResultsByNodeId,
    }),
    [
      props.externalContextDescription,
      props.externalContextName,
      props.issueTrackerHref,
      props.onRequestToJumpToPath,
      props.onRequestToResolvePath,
      props.evaluationResultsByNodeId,
    ]
  );
  return <DmnEditorContext.Provider value={value}>{props.children}</DmnEditorContext.Provider>;
}
