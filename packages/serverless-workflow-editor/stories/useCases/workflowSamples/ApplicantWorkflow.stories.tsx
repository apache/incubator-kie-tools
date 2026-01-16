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
  id: "applicantworkflow",
  name: "Applicant Workflow",
  description: "Job Application Events Example",
  expressionLang: "jsonpath",
  version: "1.0",
  specVersion: "0.8",
  start: "HandleNewApplicant",
  events: [
    {
      name: "NewApplicantEvent",
      source: "",
      type: "applicants",
    },
    {
      name: "ApplicantDecisionEvent",
      source: "",
      type: "decisions",
    },
  ],
  states: [
    {
      name: "HandleNewApplicant",
      type: "event",
      onEvents: [
        {
          eventRefs: ["NewApplicantEvent"],
          actions: [],
        },
      ],
      transition: "VerifyNewApplicant",
    },
    {
      name: "VerifyNewApplicant",
      type: "switch",
      dataConditions: [
        {
          condition: "{{ $.[?(@.salary >= 3000)] }}",
          transition: "HandleApproved",
        },
        {
          condition: "{{ $.[?(@.salary < 3000)] }}",
          transition: "HandleDenied",
        },
      ],
      defaultCondition: {
        transition: "HandleDenied",
      },
    },
    {
      name: "HandleApproved",
      type: "inject",
      data: {
        decision: "Approved",
      },
      end: {
        produceEvents: [
          {
            eventRef: "ApplicantDecisionEvent",
          },
        ],
      },
    },
    {
      name: "HandleDenied",
      type: "inject",
      data: {
        decision: "Denied",
      },
      end: {
        produceEvents: [
          {
            eventRef: "ApplicantDecisionEvent",
          },
        ],
      },
    },
  ],
};

const initialContent = JSON.stringify(workflow);

const meta: Meta<SwfEditorProps> = {
  title: "Use cases/Applicant Workflow",
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

export const ApplicantWorkflow: Story = {
  render: Empty.render,
  args: {
    model: model,
    issueTrackerHref: "",
    isReadOnly: true,
    rawContent: initialContent,
  },
};
