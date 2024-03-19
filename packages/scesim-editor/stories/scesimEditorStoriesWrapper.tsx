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
import { useArgs } from "@storybook/preview-api";
import { useRef, useMemo, useEffect } from "react";
import { SceSimModel } from "@kie-tools/scesim-marshaller";
import { TestScenarioEditor, TestScenarioEditorProps, TestScenarioEditorRef } from "../src/TestScenarioEditor";
import { diff } from "deep-object-diff";

export function SceSimEditorWrapper(props?: Partial<TestScenarioEditorProps>) {
  const [args, updateArgs] = useArgs<TestScenarioEditorProps>();
  const argsCopy = useRef(args);
  const ref = useRef<TestScenarioEditorRef>(null);
  const [modelArgs, setModelArgs] = React.useState<SceSimModel>(args.model);
  const model = React.useMemo(() => props?.model ?? modelArgs, [modelArgs, props?.model]);

  const onModelChange = useMemo(
    () => (props?.onModelChange ? props.onModelChange : setModelArgs),
    [props?.onModelChange]
  );

  useEffect(() => {
    if (Object.keys(diff(argsCopy.current.model, model)).length !== 0) {
      updateArgs({ ...argsCopy.current, model: model });
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
  useEffect(() => {
    /* Simulating a call from "Foundation" code */
    setTimeout(() => {
      ref.current?.setContent("Untitled.scesim", "");
    }, 1000);
  }, [ref]);

  return (
    <div style={{ position: "absolute", width: "100vw", height: "100vh", top: "0px", left: "0px" }}>
      <TestScenarioEditor ref={ref} model={model} onModelChange={onModelChange} />
    </div>
  );
}
