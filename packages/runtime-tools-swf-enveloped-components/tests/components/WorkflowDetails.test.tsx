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
import { render, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import WorkflowDetails from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/envelope/components/WorkflowDetails/WorkflowDetails";
import {
  Job,
  JobStatus,
  WorkflowInstance,
  WorkflowInstanceState,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

jest.useFakeTimers();

const mockDriver = {
  jobsQuery: jest.fn((_id: string) => {
    return [{ ...sampleJob, expirationTime: new Date(Date.now() + 10000).toISOString() }];
  }),
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

const sampleJob: Job = {
  id: "a62d9d0a-87ea-4c13-87fb-67965d133020",
  priority: 0,
  lastUpdate: new Date("2024-10-30T15:31:46.709Z"),
  workflowId: sampleWorkflowDetails.processId,
  workflowInstanceId: sampleWorkflowDetails.id,
  status: JobStatus.Scheduled,
  expirationTime: new Date("2024-10-30T15:31:46.709Z"),
  callbackEndpoint:
    "http://localhost:4000/management/jobs/callback_state_timeouts/instances/9750c042-3fb2-40b7-96ba-ff10b6178c58/timers/-1",
  repeatInterval: 0,
  repeatLimit: 0,
  scheduledId: "143",
  retries: 0,
  endpoint: "http://localhost:4000/jobs",
  nodeInstanceId: "ee6c3f6e-8bc3-43dc-a249-dad1b19b52bb",
  executionCounter: 0,
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

  test("should render the job correctly", async () => {
    const component = render(
      <WorkflowDetails
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver as any}
        workflowDetails={sampleWorkflowDetails}
      />
    );

    await waitFor(() => expect(mockDriver.jobsQuery).toHaveBeenCalledWith(sampleWorkflowDetails.id));

    expect(component.queryByText("Jobs")).toBeInTheDocument();
    expect(component.queryByText("Scheduled")).toBeInTheDocument();
    expect(component.queryByText(sampleJob.id.slice(0, 7))).toBeInTheDocument();
  });

  test("should update sampleJob status to EXECUTED after 30 seconds", async () => {
    const component = render(
      <WorkflowDetails
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver as any}
        workflowDetails={sampleWorkflowDetails}
      />
    );

    await waitFor(() => expect(mockDriver.jobsQuery).toHaveBeenCalledWith(sampleWorkflowDetails.id));

    expect(component.queryByText("Jobs")).toBeInTheDocument();
    expect(component.queryByText("Scheduled")).toBeInTheDocument();
    expect(component.queryByText(sampleJob.id.slice(0, 7))).toBeInTheDocument();

    jest.advanceTimersByTime(10000);

    await waitFor(() => {
      expect(component.queryByText("Scheduled")).toBeInTheDocument();
    });

    mockDriver.jobsQuery.mockReturnValue([
      { ...sampleJob, expirationTime: new Date("2023-10-30T15:31:46.709Z").toISOString(), status: JobStatus.Executed },
    ]);
    jest.advanceTimersByTime(20000);

    await waitFor(() => {
      expect(component.queryByText("Executed")).toBeInTheDocument();
    });
  });
});
