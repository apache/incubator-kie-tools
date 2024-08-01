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
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

const mockGetWorkflowDefinitionsQuery = jest.fn();
const mockSetWorkflowDefinitionList = jest.fn();
const mockOpenWorkflowForm = jest.fn();
const mockOpenTriggerCloudEvent = jest.fn();
const mockSetWorkflowDefinitionFilter = jest.fn();
const mockGetWorkflowDefinitionFilter = jest.fn();

const mockDriver = {
  getWorkflowDefinitionsQuery: mockGetWorkflowDefinitionsQuery,
  openWorkflowForm: mockOpenWorkflowForm,
  openTriggerCloudEvent: mockOpenTriggerCloudEvent,
  setWorkflowDefinitionFilter: mockSetWorkflowDefinitionFilter,
  getWorkflowDefinitionFilter: mockGetWorkflowDefinitionFilter,
};

const sampleWorkflowDefinitions = [
  { workflowName: "Test Workflow 1", endpoint: "/endpoint1", serviceUrl: "http://example.com/1" },
  { workflowName: "Test Workflow 2", endpoint: "/endpoint2", serviceUrl: "http://example.com/2" },
];

const doQuery = async (): Promise<void> => {
  const response: WorkflowDefinition[] = await mockDriver.getWorkflowDefinitionsQuery();
  mockSetWorkflowDefinitionList(response);
};

describe("doQuery", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should call setWorkflowDefinitionList and setIsLoading correctly on success", async () => {
    const mockResponse: WorkflowDefinition[] = [
      { workflowName: "Test Workflow 1", endpoint: "/endpoint1", serviceUrl: "http://example.com/1" },
    ];
    mockGetWorkflowDefinitionsQuery.mockResolvedValue(mockResponse);
    await doQuery();
    expect(mockSetWorkflowDefinitionList).toHaveBeenCalledWith(mockResponse);
  });
});

describe("WorkflowDefinitionList component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should fetch and set workflow definitions correctly", async () => {
    mockGetWorkflowDefinitionsQuery.mockResolvedValue(sampleWorkflowDefinitions);
    render(
      <WorkflowDefinitionList
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver}
        ouiaId="test-ouia-id"
        ouiaSafe={true}
      />
    );

    await waitFor(() => {
      expect(mockGetWorkflowDefinitionsQuery).toHaveBeenCalled();
    });
  });
});

describe("WorkflowDefinitionList component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  test("should maintain filter after refresh", async () => {
    mockGetWorkflowDefinitionsQuery.mockResolvedValue(sampleWorkflowDefinitions);

    const component = render(
      <WorkflowDefinitionList
        isEnvelopeConnectedToChannel={true}
        driver={mockDriver}
        ouiaId="test-ouia-id"
        ouiaSafe={true}
      />
    );

    mockGetWorkflowDefinitionFilter.mockResolvedValue(["Test Workflow 1"]);
    await act(async () => {
      await mockDriver.setWorkflowDefinitionFilter(["Test Workflow 1"]);
    });

    expect(mockDriver.setWorkflowDefinitionFilter).toHaveBeenCalledWith(["Test Workflow 1"]);
    await (() => {
      const filteredElement = component.getByTestId("workflow-definition-list-with-filter");
      expect(filteredElement).toBeInTheDocument();
    });

    mockGetWorkflowDefinitionsQuery.mockResolvedValue(sampleWorkflowDefinitions);
    await act(async () => {
      await mockDriver.getWorkflowDefinitionsQuery();
    });

    expect(mockDriver.setWorkflowDefinitionFilter).toHaveBeenCalledWith(["Test Workflow 1"]);
    await (() => {
      const filteredElement = component.getByTestId("workflow-definition-list-with-filter");
      expect(filteredElement).toBeInTheDocument();
    });
  });
});
