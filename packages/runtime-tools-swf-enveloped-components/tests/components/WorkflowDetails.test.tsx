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
import { render } from "@testing-library/react";
import "@testing-library/jest-dom";
import WorkflowDetails from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/envelope/components/WorkflowDetails/WorkflowDetails";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

const mockDriver = {
  jobsQuery: jest.fn(),
};

const sampleWorkflowDetails: WorkflowInstance = {
  id: "6d114aaf-b599-49da-b434-f18133b40072",
  processId: "timeout",
  processName: "timeout",
  variables: {
    workflowdata: {
      varName: "value",
    },
  },
  state: WorkflowInstanceState.Active,
  start: new Date("2024-10-18T12:25:00.192Z"),
  lastUpdate: new Date("2024-10-18T12:25:00.192Z"),
  endpoint: "http://localhost:4000/timeout",
  nodes: [],
};

describe("WorkflowDetails component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should render the workflow details correctly", () => {
    const component = render(
      <WorkflowDetails
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver as any}
        workflowDetails={sampleWorkflowDetails}
      />
    );

    expect(component.queryByText("6d114")).toBeInTheDocument();
  });

  test.each([
    [undefined, false],
    [null, false],
    [{}, false],
    [{ workflowdata: {} }, true],
    [sampleWorkflowDetails.variables, true],
  ])(
    "should render the workflow Variables card correctly when variables is `%j`",
    (variablesMock, variablesCardVisible) => {
      const workflowDetailsMock = { ...sampleWorkflowDetails, variables: variablesMock };
      const component = render(
        <WorkflowDetails
          isEnvelopeConnectedToChannel={true}
          driver={mockDriver as any}
          workflowDetails={workflowDetailsMock}
        />
      );

      if (variablesCardVisible) {
        expect(component.queryByText("Variables")).toBeInTheDocument();
        expect(component.queryByText("workflowdata")).toBeInTheDocument();
      } else {
        expect(component.queryByText("Variables")).not.toBeInTheDocument();
      }
    }
  );
});
