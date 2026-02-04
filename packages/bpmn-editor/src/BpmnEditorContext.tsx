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
import { BpmnEditorProps } from "./BpmnEditor";
import { BpmnLatestModel } from "@kie-tools/bpmn-marshaller";

export type BpmnEditorContextProviderProps = Pick<
  BpmnEditorProps,
  | "externalContextDescription"
  | "externalContextName"
  | "issueTrackerHref"
  | "model"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
>;

export type BpmnModelBeforeEditing = BpmnLatestModel;

export type BpmnEditorContextType = Pick<
  BpmnEditorContextProviderProps,
  | "externalContextDescription"
  | "externalContextName"
  | "issueTrackerHref"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
> & {
  bpmnModelBeforeEditingRef: React.MutableRefObject<BpmnModelBeforeEditing>;
  bpmnEditorRootElementRef: React.RefObject<HTMLDivElement>;
};

const BpmnEditorContext = React.createContext<BpmnEditorContextType>({} as any);

export function useBpmnEditor() {
  return useContext(BpmnEditorContext);
}

export function BpmnEditorContextProvider(props: React.PropsWithChildren<BpmnEditorContextProviderProps>) {
  const bpmnModelBeforeEditingRef = useRef<BpmnModelBeforeEditing>(props.model);
  const bpmnEditorRootElementRef = useRef<HTMLDivElement>(null);

  const value = useMemo<BpmnEditorContextType>(
    () => ({
      bpmnModelBeforeEditingRef,
      bpmnEditorRootElementRef,
      externalContextDescription: props.externalContextDescription,
      externalContextName: props.externalContextName,
      issueTrackerHref: props.issueTrackerHref,
      onRequestToJumpToPath: props.onRequestToJumpToPath,
      onRequestToResolvePath: props.onRequestToResolvePath,
    }),
    [
      props.externalContextDescription,
      props.externalContextName,
      props.issueTrackerHref,
      props.onRequestToJumpToPath,
      props.onRequestToResolvePath,
    ]
  );
  return <BpmnEditorContext.Provider value={value}>{props.children}</BpmnEditorContext.Provider>;
}
