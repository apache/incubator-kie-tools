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
  id: "jsongreet",
  version: "1.0",
  specVersion: "0.8",
  name: "Greeting workflow",
  description: "JSON based greeting workflow",
  start: "ChooseOnLanguage",
  functions: [
    {
      name: "greetFunction",
      type: "custom",
      operation: "sysout",
    },
  ],
  states: [
    {
      name: "ChooseOnLanguage",
      type: "switch",
      dataConditions: [
        {
          condition: '${ .language == "English" }',
          transition: "GreetInEnglish",
        },
        {
          condition: '${ .language == "Spanish" }',
          transition: "GreetInSpanish",
        },
      ],
      defaultCondition: {
        transition: "GreetInEnglish",
      },
    },
    {
      name: "GreetInEnglish",
      type: "inject",
      data: {
        greeting: "Hello from JSON Workflow, ",
      },
      transition: "GreetPerson",
    },
    {
      name: "GreetInSpanish",
      type: "inject",
      data: {
        greeting: "Saludos desde JSON Workflow, ",
      },
      transition: "GreetPerson",
    },
    {
      name: "GreetPerson",
      type: "operation",
      actions: [
        {
          name: "greetAction",
          functionRef: {
            refName: "greetFunction",
            arguments: {
              message: ".greeting+.name",
            },
          },
        },
      ],
      end: {
        terminate: true,
      },
    },
  ],
};

const initialContent = JSON.stringify(workflow);

const meta: Meta<SwfEditorProps> = {
  title: "Use cases/Greeting Workflow",
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

export const GreetingWorkflow: Story = {
  render: Empty.render,
  args: {
    model: model,
    issueTrackerHref: "",
    isReadOnly: true,
    rawContent: initialContent,
  },
};
