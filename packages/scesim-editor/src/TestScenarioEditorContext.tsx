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
import { SceSimModel } from "@kie-tools/scesim-marshaller";
import { TestScenarioEditorProps } from "./TestScenarioEditor";

export type SceSimModelBeforeEditing = SceSimModel;

export type TestScenarioEditorContextProviderProps = Pick<
  TestScenarioEditorProps,
  | "issueTrackerHref"
  | "model"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
  | "openFileNormalizedPosixPathRelativeToTheWorkspaceRoot"
>;

export type TestScenarioEditorContextType = Pick<
  TestScenarioEditorContextProviderProps,
  | "issueTrackerHref"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
  | "openFileNormalizedPosixPathRelativeToTheWorkspaceRoot"
> & {
  testScenarioEditorModelBeforeEditingRef: React.MutableRefObject<SceSimModelBeforeEditing>;
  testScenarioEditorRootElementRef: React.RefObject<HTMLDivElement>;
};

const TestScenarioEditorContext = React.createContext<TestScenarioEditorContextType>({} as any);

export function useTestScenarioEditor() {
  return useContext(TestScenarioEditorContext);
}

export function TestScenarioEditorContextProvider(
  props: React.PropsWithChildren<TestScenarioEditorContextProviderProps>
) {
  const testScenarioEditorModelBeforeEditingRef = useRef<SceSimModelBeforeEditing>(props.model);
  const testScenarioEditorRootElementRef = useRef<HTMLDivElement>(null);

  const value = useMemo<TestScenarioEditorContextType>(
    () => ({
      issueTrackerHref: props.issueTrackerHref,
      testScenarioEditorModelBeforeEditingRef,
      testScenarioEditorRootElementRef,
      onRequestToJumpToPath: props.onRequestToJumpToPath,
      onRequestToResolvePath: props.onRequestToResolvePath,
      openFileNormalizedPosixPathRelativeToTheWorkspaceRoot:
        props.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
    }),
    [
      props.issueTrackerHref,
      props.onRequestToJumpToPath,
      props.onRequestToResolvePath,
      props.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
    ]
  );
  return <TestScenarioEditorContext.Provider value={value}>{props.children}</TestScenarioEditorContext.Provider>;
}
