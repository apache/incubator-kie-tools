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
import { useEffect, useMemo, useRef, useState } from "react";
import { useArgs } from "@storybook/preview-api";

import { TestScenarioEditor, TestScenarioEditorProps, TestScenarioEditorRef } from "../src/TestScenarioEditor";
import { SceSimModel } from "@kie-tools/scesim-marshaller";

export type StorybookSceSimEditorProps = TestScenarioEditorProps & { xml: string };

export function SceSimEditorWrapper(props: Partial<StorybookSceSimEditorProps>) {
  const [args, updateArgs] = useArgs<StorybookSceSimEditorProps>();
  const argsCopy = useRef(args);
  const ref = useRef<TestScenarioEditorRef>(null);
  const [modelArgs, setModelArgs] = useState<SceSimModel>(args.model);
  const model = useMemo(() => props?.model ?? modelArgs, [modelArgs, props?.model]);
  const [modelChanged, setModelChange] = useState<boolean>(false);
  const [isReadOnly, setIsReadOnly] = useState(props?.isReadOnly ?? args.isReadOnly ?? false);

  /*

  useEffect(() => {

    ref.current?.setContent(props.pathRelativeToTheWorkspaceRoot, props.content);
  }, [ref, props.content, props.pathRelativeToTheWorkspaceRoot]); 
*/
  return (
    <div>
      <TestScenarioEditor ref={ref} />
    </div>
  );
}
