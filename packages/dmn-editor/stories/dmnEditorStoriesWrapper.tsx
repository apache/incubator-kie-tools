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
import {
  DmnEditor,
  DmnEditorProps,
  DmnEditorRef,
  EvaluationResultsByNodeId,
  ValidationMessages,
} from "../src/DmnEditor";
import { DmnLatestModel, getMarshaller } from "@kie-tools/dmn-marshaller";
import { normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { diff } from "deep-object-diff";
import { generateEmptyDmn15 } from "./misc/empty/Empty.stories";

export const evaluationResultsByNodeId: EvaluationResultsByNodeId = new Map();
export const validationMessages: ValidationMessages = {};

export type StorybookDmnEditorProps = DmnEditorProps & { xml: string };

export function DmnEditorWrapper(props?: Partial<StorybookDmnEditorProps>) {
  const [args, updateArgs] = useArgs<StorybookDmnEditorProps>();
  const argsCopy = useRef(args);
  const ref = useRef<DmnEditorRef>(null);
  const [modelArgs, setModelArgs] = useState<DmnLatestModel>(args.model);
  const model = useMemo(() => props?.model ?? modelArgs, [modelArgs, props?.model]);
  const [modelChanged, setModelChange] = useState<boolean>(false);
  const [isReadOnly, setIsReadOnly] = useState(props?.isReadOnly ?? args.isReadOnly ?? false);

  const onModelChange = useMemo(
    () => (props?.onModelChange ? props.onModelChange : setModelArgs),
    [props?.onModelChange]
  );

  const onOpenedBoxedExpressionEditorNodeChangeNoOperation = useMemo(
    () => (newOpenedNodeId: string | undefined) => {},
    []
  );

  useEffect(() => {
    if (args.isReadOnly !== undefined) {
      setIsReadOnly(args.isReadOnly);
    }
  }, [args.isReadOnly]);

  useEffect(() => {
    if (Object.keys(diff(argsCopy.current.model, model)).length !== 0) {
      updateArgs({
        ...argsCopy.current,
        model: model,
        xml: getMarshaller(generateEmptyDmn15(), { upgradeTo: "latest" }).builder.build(model),
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
        <div data-testid={"storybook--dmn-editor-model"} style={{ display: "none" }}>
          {JSON.stringify(model)}
        </div>
      )}
      <button
        data-testid={"storybook--dmn-editor-toggle-read-only"}
        style={{ display: "none" }}
        onClick={() => setIsReadOnly((currentValue) => !currentValue)}
      >
        {isReadOnly.toString()}
      </button>
      <div style={{ position: "absolute", width: "100%", height: "100%", top: "0px", left: "0px" }}>
        <DmnEditor
          ref={ref}
          model={model}
          originalVersion={props?.originalVersion ?? args.originalVersion}
          isEvaluationHighlightsSupported={
            props?.isEvaluationHighlightsSupported ?? args.isEvaluationHighlightsSupported
          }
          isReadOnly={isReadOnly}
          onModelChange={onModelChange}
          onOpenedBoxedExpressionEditorNodeChange={onOpenedBoxedExpressionEditorNodeChangeNoOperation}
          onRequestExternalModelByPath={props?.onRequestExternalModelByPath ?? args.onRequestExternalModelByPath}
          onRequestExternalModelsAvailableToInclude={
            props?.onRequestExternalModelsAvailableToInclude ?? args.onRequestExternalModelsAvailableToInclude
          }
          externalModelsByNamespace={props?.externalModelsByNamespace ?? args.externalModelsByNamespace}
          externalContextName={props?.externalContextName ?? args.externalContextName}
          externalContextDescription={props?.externalContextDescription ?? args.externalContextDescription}
          validationMessages={props?.validationMessages ?? args.validationMessages}
          evaluationResultsByNodeId={props?.evaluationResultsByNodeId ?? args.evaluationResultsByNodeId}
          issueTrackerHref={props?.issueTrackerHref ?? args.issueTrackerHref}
          onRequestToJumpToPath={props?.onRequestToJumpToPath ?? args.onRequestToJumpToPath}
          onModelDebounceStateChanged={onModelDebounceStateChanged}
        />
      </div>
    </>
  );
}
