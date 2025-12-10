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
import SwfCombinedEditor from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/envelope/components/SwfCombinedEditor/SwfCombinedEditor";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

global.TextDecoder = require("util").TextDecoder;

// Mock for envelope API
const mockColorNodesSend = jest.fn();
const mockSubscribe = jest.fn((callback) => {
  callback();
  return { unsubscribe: jest.fn() };
});

const mockEnvelopeAndChannelApi = {
  notifications: {
    kogitoSwfCombinedEditor_colorNodes: {
      send: mockColorNodesSend,
    },
    kogitoSwfCombinedEditor_combinedEditorReady: {
      subscribe: mockSubscribe,
    },
  },
};

// Mock useController to provide a mock editor with envelope API
jest.mock("@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/hooks/useController", () => ({
  useController: jest.fn(() => [
    {
      getEnvelopeServer: () => ({
        envelopeApi: mockEnvelopeAndChannelApi,
      }),
    },
    jest.fn(),
  ]),
}));

describe("SwfCombinedEditor - ForEach node coloring", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockColorNodesSend.mockClear();
  });

  it("should color ForEach nodes using nodeDefinitions metadata", async () => {
    const workflowInstance: WorkflowInstance = {
      id: "c55b6474-d83b-47c5-ac32-bf12c9adc9e1",
      processId: "foreach",
      state: WorkflowInstanceState.Completed,
      endpoint: "http://localhost:4000/foreach",
      nodes: [],
      start: new Date("2024-10-18T12:25:00.192Z"),
      lastUpdate: new Date("2024-10-18T12:25:00.192Z"),
      source:
        "id: foreach\nversion: '1.0'\nspecVersion: '0.8.0'\nname: foreach\nstart: ForEachState\nstates:\n- name: ForEachState\n  type: foreach\n  end: true\n",
      nodeDefinitions: [
        {
          id: "1",
          name: "Start",
          type: "StartNode",
          uniqueId: "1",
          metadata: {
            UniqueId: "1",
            state: "ForEachState",
            branch: null,
            action: null,
          },
        },
        {
          id: "2",
          name: "End",
          type: "EndNode",
          uniqueId: "2",
          metadata: {
            UniqueId: "2",
            state: "ForEachState",
            branch: null,
            action: null,
          },
        },
        {
          id: "3",
          name: "ForEach",
          type: "ForEachNode",
          uniqueId: "3",
          metadata: {
            UniqueId: "3",
            state: "ForEachState",
            branch: null,
            action: null,
          },
        },
      ],
    };

    render(<SwfCombinedEditor workflowInstance={workflowInstance} />);

    await waitFor(() => {
      expect(mockColorNodesSend).toHaveBeenCalled();
    });

    const callArgs = mockColorNodesSend.mock.calls[0][0];
    expect(callArgs.nodeNames).toContain("Start");
    expect(callArgs.nodeNames).toContain("ForEach");
    expect(callArgs.nodeNames).toContain("ForEachState");
    expect(callArgs.nodeNames).toContain("End");
  });
});
