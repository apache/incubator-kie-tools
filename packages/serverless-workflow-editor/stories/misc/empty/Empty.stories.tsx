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

import type { Meta, StoryObj } from "@storybook/react";

import { SwfEditorWrapper, StorybookSwfEditorProps } from "../../swfEditorStoriesWrapper";
import { SwfEditor, SwfEditorProps } from "../../../src/SwfEditor";
import { Specification, WorkflowValidator } from "@serverlessworkflow/sdk-typescript";

const emptyModel = {
  id: "Empty",
  version: "1.0",
  specVersion: "0.8",
  name: "Empty Workflow",
  description: "",
  start: "",
  states: [],
} as unknown as Specification.IWorkflow;

const meta: Meta<SwfEditorProps> = {
  title: "Misc/Empty",
  component: SwfEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookSwfEditorProps>;

export const Empty: Story = {
  render: (args) => SwfEditorWrapper(),
  args: {
    model: emptyModel,
    issueTrackerHref: "",
    isReadOnly: false,
    rawContent: JSON.stringify(emptyModel),
  },
};
