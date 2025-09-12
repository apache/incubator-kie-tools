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
import { SwfEditorProps } from "./SwfEditor";
import { Specification } from "@severlessworkflow/sdk-typescript";

export type SwfEditorContextProviderProps = Pick<SwfEditorProps, "issueTrackerHref" | "model">;

export type SwfModelBeforeEditing = Specification.Workflow;

export type SwfEditorContextType = Pick<SwfEditorContextProviderProps, "issueTrackerHref"> & {
  swfModelBeforeEditingRef: React.MutableRefObject<SwfModelBeforeEditing>;
  swfEditorRootElementRef: React.RefObject<HTMLDivElement>;
};

const SwfEditorContext = React.createContext<SwfEditorContextType>({} as any);

export function useSwfEditor() {
  return useContext(SwfEditorContext);
}

export function SwfEditorContextProvider(props: React.PropsWithChildren<SwfEditorContextProviderProps>) {
  const swfModelBeforeEditingRef = useRef<SwfModelBeforeEditing>(props.model);
  const swfEditorRootElementRef = useRef<HTMLDivElement>(null);

  const value = useMemo<SwfEditorContextType>(
    () => ({
      swfModelBeforeEditingRef,
      swfEditorRootElementRef,
      issueTrackerHref: props.issueTrackerHref,
    }),
    [props.issueTrackerHref]
  );
  return <SwfEditorContext.Provider value={value}>{props.children}</SwfEditorContext.Provider>;
}
