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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useArgs } from "@storybook/preview-api";
import { diff } from "deep-object-diff";
import { SceSimModel, getMarshaller } from "@kie-tools/scesim-marshaller";
import { TestScenarioEditor, TestScenarioEditorProps, TestScenarioEditorRef } from "../src/TestScenarioEditor";
import { EMPTY_ONE_EIGHT } from "../src/resources/EmptyScesimFile";

export type StorybookTestScenarioEditorProps = TestScenarioEditorProps & { xml: string };

export function SceSimEditorWrapper(props: Partial<StorybookTestScenarioEditorProps>) {
  const [args, updateArgs] = useArgs<StorybookTestScenarioEditorProps>();
  const argsCopy = useRef(args);
  const ref = useRef<TestScenarioEditorRef>(null);
  const [modelArgs, setModelArgs] = useState<SceSimModel>(args.model);
  const model = useMemo(() => props?.model ?? modelArgs, [modelArgs, props?.model]);

  const onModelChange = useMemo(
    () => (props?.onModelChange ? props.onModelChange : setModelArgs),
    [props?.onModelChange]
  );

  useEffect(() => {
    if (Object.keys(diff(argsCopy.current.model, model)).length !== 0) {
      updateArgs({
        ...argsCopy.current,
        model: model,
        xml: getMarshaller(EMPTY_ONE_EIGHT).builder.build(model),
      });
    }
  }, [updateArgs, model]);

  useEffect(() => {
    if (Object.keys(diff(argsCopy.current, args)).length === 0) {
      return;
    }
    argsCopy.current = args;
    if (Object.keys(diff(args.model, model)).length === 0) {
      return;
    }
    onModelChange(args.model);
  }, [args, model, onModelChange]);

  const onModelDebounceStateChanged = useCallback(() => {
    console.debug("[scesimEditorStoriesWrapper] Model Debounce state");
  }, []);

  return (
    <div style={{ position: "absolute", width: "100%", height: "100%", top: "0px", left: "0px" }}>
      <TestScenarioEditor
        ref={ref}
        externalModelsByNamespace={props?.externalModelsByNamespace ?? args.externalModelsByNamespace}
        issueTrackerHref={props?.issueTrackerHref ?? args.issueTrackerHref}
        model={model}
        onModelChange={onModelChange}
        onModelDebounceStateChanged={onModelDebounceStateChanged}
        onRequestExternalModelByPath={props?.onRequestExternalModelByPath ?? args.onRequestExternalModelByPath}
        onRequestExternalModelsAvailableToInclude={
          props?.onRequestExternalModelsAvailableToInclude ?? args.onRequestExternalModelsAvailableToInclude
        }
        openFileNormalizedPosixPathRelativeToTheWorkspaceRoot={
          props?.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot ??
          args.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot
        }
        onRequestToJumpToPath={props?.onRequestToJumpToPath ?? args.onRequestToJumpToPath}
        onRequestToResolvePath={props?.onRequestToResolvePath ?? args.onRequestToResolvePath}
      />
    </div>
  );
}
