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
import { SwfEditor, SwfEditorProps, SwfEditorRef } from "../src/SwfEditor";
import { diff } from "deep-object-diff";
import { Specification } from "@serverlessworkflow/sdk-typescript";

export type StorybookSwfEditorProps = SwfEditorProps & { rawContent: string };

export function SwfEditorWrapper(props?: Partial<StorybookSwfEditorProps>) {
  const [args, updateArgs] = useArgs<StorybookSwfEditorProps>();
  const argsCopy = useRef(args);
  const ref = useRef<SwfEditorRef>(null);
  const [modelArgs, setModelArgs] = useState<Specification.IWorkflow>(args.model);
  const model = useMemo(() => props?.model ?? modelArgs, [modelArgs, props?.model]);
  const [modelChanged, setModelChange] = useState<boolean>(false);
  const [isReadOnly, setIsReadOnly] = useState(props?.isReadOnly ?? args.isReadOnly ?? false);

  const onModelChange = useMemo(
    () => (props?.onModelChange ? props.onModelChange : setModelArgs),
    [props?.onModelChange]
  );

  useEffect(() => {
    if (args.isReadOnly !== undefined) {
      setIsReadOnly(args.isReadOnly);
    }
  }, [args.isReadOnly]);

  useEffect(() => {
    if (
      Object.keys(diff(cleanConstructorProperty(argsCopy.current.model), cleanConstructorProperty(model))).length !== 0
    ) {
      updateArgs({
        ...argsCopy.current,
        model: model,
        rawContent: JSON.stringify(model),
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

  const onModelDebounceStateChanged = useCallback((changed: boolean) => {
    setModelChange(changed);
  }, []);

  return (
    <>
      {modelChanged && (
        <div data-testid={"storybook--swf-editor-model"} style={{ display: "none" }}>
          {JSON.stringify(model)}
        </div>
      )}
      <button
        data-testid={"storybook--swf-editor-toggle-read-only"}
        style={{ display: "none" }}
        onClick={() => setIsReadOnly((currentValue) => !currentValue)}
      >
        {isReadOnly.toString()}
      </button>
      <div style={{ position: "absolute", width: "100%", height: "100%", top: "0px", left: "0px" }}>
        <SwfEditor
          ref={ref}
          model={model}
          isReadOnly={isReadOnly}
          onModelChange={onModelChange}
          issueTrackerHref={props?.issueTrackerHref ?? args.issueTrackerHref}
          onModelDebounceStateChanged={onModelDebounceStateChanged}
          locale={args.locale}
        />
      </div>
    </>
  );
}

// Storybook has a bug that polutes objects with _constructor-name_ property
// Issue https://github.com/storybookjs/telejson/issues/104
function cleanConstructorProperty(obj: any): any {
  const constructorProperty = "_constructor-name_";

  if (typeof obj !== "object" || obj === null) {
    return obj;
  }
  const newObj: any = {};

  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      if (key === constructorProperty) {
        continue;
      } else if (typeof obj[key] === "object" && obj[key] !== null) {
        newObj[key] = cleanConstructorProperty(obj[key]);
      } else {
        newObj[key] = obj[key];
      }
    }
  }
  return newObj;
}
