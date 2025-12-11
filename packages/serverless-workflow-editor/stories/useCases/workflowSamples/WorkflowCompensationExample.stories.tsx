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
import type { Meta, StoryObj } from "@storybook/react";
import { Empty } from "../../misc/empty/Empty.stories";
import { SwfEditor, SwfEditorProps } from "../../../src/SwfEditor";
import { StorybookSwfEditorProps } from "../../swfEditorStoriesWrapper";
import { Specification, WorkflowValidator } from "@serverlessworkflow/sdk-typescript";

const workflow = {
  id: "compensation",
  version: "1.0",
  specVersion: "0.8",
  name: "Workflow Compensation example",
  description: "An example of how compensation works",
  start: "printStatus",
  states: [
    {
      name: "printStatus",
      type: "inject",
      data: {
        compensated: false,
      },
      compensatedBy: "compensating",
      transition: "branch",
    },
    {
      name: "branch",
      type: "switch",
      dataConditions: [
        {
          condition: ".shouldCompensate==true",
          transition: {
            nextState: "finish_compensate",
            compensate: true,
          },
        },
        {
          condition: ".shouldCompensate==false",
          transition: {
            nextState: "finish_not_compensate",
            compensate: false,
          },
        },
      ],
      defaultCondition: {
        transition: "finish_not_compensate",
      },
    },
    {
      name: "compensating",
      usedForCompensation: true,
      type: "inject",
      data: {
        compensated: true,
      },
      transition: "compensating_more",
    },
    {
      name: "compensating_more",
      usedForCompensation: true,
      type: "inject",
      data: {
        compensating_more: "Real Betis Balompie",
      },
    },
    {
      name: "finish_compensate",
      type: "operation",
      actions: [],
      end: {
        terminate: true,
      },
    },
    {
      name: "finish_not_compensate",
      type: "operation",
      actions: [],
      end: {
        terminate: true,
      },
    },
  ],
};

const initialContent = JSON.stringify(workflow);

const meta: Meta<SwfEditorProps> = {
  title: "Use cases/Workflow Compensation Example",
  component: SwfEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookSwfEditorProps>;
const model = Specification.Workflow.fromSource(initialContent, true);

if (!model) {
  const validator = new WorkflowValidator(model);
  const errors = validator.isValid ? [] : validator.errors;

  errors.forEach((error) => {
    console.log(error.message);
  });

  throw new Error("SWF - model is null!!!!");
}

export const WorkflowCompensationExample: Story = {
  render: Empty.render,
  args: {
    model: model,
    issueTrackerHref: "",
    isReadOnly: true,
    rawContent: initialContent,
  },
};
