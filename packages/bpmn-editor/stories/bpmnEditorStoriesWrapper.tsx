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
import { useCallback, useState, useRef, useMemo, useEffect } from "react";
import { useArgs } from "@storybook/preview-api";
import { BpmnEditor, BpmnEditorProps, BpmnEditorRef } from "../src/BpmnEditor";
import { BpmnLatestModel, getMarshaller } from "@kie-tools/bpmn-marshaller";
import { normalize } from "@kie-tools/bpmn-editor/dist/normalization/normalize";
import { diff } from "deep-object-diff";
import { generateEmptyBpmn20 } from "./misc/empty/Empty.stories";

export type StorybookBpmnEditorProps = BpmnEditorProps & { xml: string };

export function BpmnEditorWrapper(props?: Partial<StorybookBpmnEditorProps>) {
  const [args, updateArgs] = useArgs<StorybookBpmnEditorProps>();
  const argsCopy = useRef(args);
  const ref = useRef<BpmnEditorRef>(null);
  const [modelArgs, setModelArgs] = useState<BpmnLatestModel>(args.model);
  const model = useMemo(() => props?.model ?? modelArgs, [modelArgs, props?.model]);
  const [modelChanged, setModelChange] = useState<boolean>(false);

  const onModelChange = useMemo(
    () => (props?.onModelChange ? props.onModelChange : setModelArgs),
    [props?.onModelChange]
  );

  useEffect(() => {
    if (Object.keys(diff(argsCopy.current.model, model)).length !== 0) {
      updateArgs({
        ...argsCopy.current,
        model: model,
        xml: getMarshaller(generateEmptyBpmn20(), { upgradeTo: "latest" }).builder.build(model),
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
    onModelChange(normalize(args.model));
  }, [args, model, onModelChange]);

  const onModelDebounceStateChanged = useCallback((changed: boolean) => {
    setModelChange(changed);
  }, []);

  return (
    <>
      {modelChanged && (
        <div data-testid={"storybook--bpmn-editor-model"} style={{ display: "none" }}>
          {JSON.stringify(model)}
        </div>
      )}
      <div style={{ position: "absolute", width: "100%", height: "100%", top: "0px", left: "0px" }}>
        <BpmnEditor
          ref={ref}
          model={model}
          customTasks={props?.customTasks ?? args.customTasks}
          customTasksPaletteIcon={props?.customTasksPaletteIcon ?? args.customTasksPaletteIcon}
          originalVersion={props?.originalVersion ?? args.originalVersion}
          onModelChange={onModelChange}
          externalContextName={props?.externalContextName ?? args.externalContextName}
          externalContextDescription={props?.externalContextDescription ?? args.externalContextDescription}
          issueTrackerHref={props?.issueTrackerHref ?? args.issueTrackerHref}
          onRequestToJumpToPath={props?.onRequestToJumpToPath ?? args.onRequestToJumpToPath}
          onRequestExternalModelByPath={props?.onRequestExternalModelByPath ?? args.onRequestExternalModelByPath}
          onRequestExternalModelsAvailableToInclude={
            props?.onRequestExternalModelsAvailableToInclude ?? args.onRequestExternalModelsAvailableToInclude
          }
          onModelDebounceStateChanged={onModelDebounceStateChanged}
          locale="en-US"
        />
      </div>
    </>
  );
}
