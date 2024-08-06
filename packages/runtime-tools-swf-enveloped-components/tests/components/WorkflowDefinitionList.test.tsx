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
import { render, waitFor, screen, fireEvent, act } from "@testing-library/react";
import "@testing-library/jest-dom";
import WorkflowDefinitionList from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDefinitions/envelope/components/WorkflowDefinitionList/WorkflowDefinitionList";

const mockDriver = {
  getWorkflowDefinitionsQuery: jest.fn(),
  openWorkflowForm: jest.fn(),
  openTriggerCloudEvent: jest.fn(),
  setWorkflowDefinitionFilter: jest.fn(),
  getWorkflowDefinitionFilter: jest.fn(),
};

const sampleWorkflowDefinitions = [
  { workflowName: "Test Workflow 1", endpoint: "/endpoint1", serviceUrl: "http://example.com/1" },
  { workflowName: "Test Workflow 2", endpoint: "/endpoint2", serviceUrl: "http://example.com/2" },
];

describe("WorkflowDefinitionList component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should fetch and set workflow definitions correctly", async () => {
    mockDriver.getWorkflowDefinitionsQuery.mockResolvedValue(sampleWorkflowDefinitions);
    const component = render(
      <WorkflowDefinitionList
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver}
        ouiaId="test-ouia-id"
        ouiaSafe={true}
      />
    );

    await waitFor(() => {
      expect(mockDriver.getWorkflowDefinitionsQuery).toHaveBeenCalled();
      expect(component.queryByText("Test Workflow 1")).toBeInTheDocument();
      expect(component.queryByText("Test Workflow 2")).toBeInTheDocument();
    });
  });

  test("should maintain filter after refresh", async () => {
    mockDriver.getWorkflowDefinitionsQuery.mockResolvedValue(sampleWorkflowDefinitions);

    const component = render(
      <WorkflowDefinitionList
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver}
        ouiaId="test-ouia-id"
        ouiaSafe={true}
      />
    );

    await (async () => {
      await mockDriver.setWorkflowDefinitionFilter();
    });

    expect(component.queryByText("Test Workflow 1")).toBeInTheDocument();
    expect(component.queryByText("Test Workflow 2")).toBeInTheDocument();

    fireEvent.change(component.getByTestId("workflow-filter-input"), {
      target: { value: "Test Workflow 1" },
    });

    fireEvent.click(component.getByTestId("apply-filter"));
    expect(component.queryByText("Test Workflow 2")).not.toBeInTheDocument();

    fireEvent.click(component.getByTestId("refresh"));
    expect(component.queryByText("Test Workflow 2")).not.toBeInTheDocument();
  });
});
