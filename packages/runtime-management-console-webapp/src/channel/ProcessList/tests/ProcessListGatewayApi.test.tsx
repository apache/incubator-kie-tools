/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import {
  ProcessInstance,
  ProcessInstanceState,
  OrderBy,
  ProcessInstanceFilter,
  ProcessListSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OperationType } from "@kogito-apps/management-console-shared/dist/components/BulkList";
import { ProcessListGatewayApi, ProcessListGatewayApiImpl } from "../ProcessListGatewayApi";
import { ProcessListQueries } from "../ProcessListQueries";

export const processInstance: ProcessInstance = {
  id: "a1e139d5-4e77-48c9-84ae-34578e904e5a",
  processId: "hotelBooking",
  businessKey: "T1234HotelBooking01",
  parentProcessInstanceId: null,
  processName: "HotelBooking",
  rootProcessInstanceId: null,
  roles: [],
  state: ProcessInstanceState.Active,
  start: new Date("2020-02-19T11:11:56.282Z"),
  end: new Date("2020-02-19T11:11:56.282Z"),
  lastUpdate: new Date("2020-02-19T11:11:56.282Z"),
  serviceUrl: "http://localhost:4000",
  endpoint: "http://localhost:4000",
  error: {
    nodeDefinitionId: "a1e139d5-4e77-48c9-84ae-34578e904e6b",
    message: "some thing went wrong",
  },
  addons: [],
  variables:
    '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: "1",
      name: "End Event 1",
      definitionId: "EndEvent_1",
      id: "27107f38-d888-4edf-9a4f-11b9e6d751b6",
      enter: new Date("2020-02-19T11:11:56.282Z"),
      exit: new Date("2020-02-19T11:11:56.282Z"),
      type: "EndNode",
    },
  ],
  childProcessInstances: [],
};

jest.mock("@kogito-apps/runtime-gateway-api", () => ({
  handleProcessSkip: jest.fn(),
  handleProcessRetry: jest.fn(),
  handleProcessAbort: jest.fn(),
  handleProcessMultipleAction: jest.fn(),
}));

const getProcessInstancesMock = jest.fn();
const getChildProcessInstancesMock = jest.fn();
const handleProcessSkipMock = jest.fn();
const handleProcessAbortMock = jest.fn();
const handleProcessRetryMock = jest.fn();
const handleProcessMultipleActionMock = jest.fn();

const MockProcessListQueries = jest.fn<ProcessListQueries, []>(() => ({
  getProcessInstances: getProcessInstancesMock,
  getChildProcessInstances: getChildProcessInstancesMock,
  handleProcessSkip: handleProcessSkipMock,
  handleProcessAbort: handleProcessAbortMock,
  handleProcessMultipleAction: handleProcessMultipleActionMock,
  handleProcessRetry: handleProcessRetryMock,
}));

let queries: ProcessListQueries;
let gatewayApi: ProcessListGatewayApi;
const processListFilters: ProcessInstanceFilter = {
  status: [ProcessInstanceState.Active],
  businessKey: [],
};
const sortBy: ProcessListSortBy = { lastUpdate: OrderBy.DESC };
const rootProcessInstanceId: string = "a1e139d5-4e77-48c9-84ae-34578e904e5a";
describe("ProcessListChannelApiImpl tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    queries = new MockProcessListQueries();
    gatewayApi = new ProcessListGatewayApiImpl(queries);
    getProcessInstancesMock.mockReturnValue(Promise.resolve([]));
    getChildProcessInstancesMock.mockReturnValue(Promise.resolve([]));
    handleProcessSkipMock.mockReturnValue(Promise.resolve());
    handleProcessAbortMock.mockReturnValue(Promise.resolve());
    handleProcessMultipleActionMock.mockReturnValue(Promise.resolve());
  });

  it("Initial load", () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    expect(gatewayApi.processListState).toStrictEqual({
      filters: processListFilters,
      sortBy: sortBy,
    });
  });

  it("applyFilter", () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    gatewayApi.applyFilter(processListFilters);
    expect(gatewayApi.processListState.filters).toBe(processListFilters);
  });

  it("applySorting", () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    gatewayApi.applySorting(sortBy);
    expect(gatewayApi.processListState.sortBy).toBe(sortBy);
  });

  it("handleProcessSkip", async () => {
    await gatewayApi.handleProcessSkip(processInstance);
    expect(handleProcessSkipMock).toHaveBeenCalledWith(processInstance);
  });

  it("handleProcessRetry", async () => {
    await gatewayApi.handleProcessRetry(processInstance);
    expect(handleProcessRetryMock).toHaveBeenCalledWith(processInstance);
  });

  it("handleProcessAbort", async () => {
    await gatewayApi.handleProcessAbort(processInstance);
    expect(handleProcessAbortMock).toHaveBeenCalledWith(processInstance);
  });

  it("handle multi action", async () => {
    await gatewayApi.handleProcessMultipleAction([processInstance], OperationType.ABORT);
    expect(handleProcessMultipleActionMock).toHaveBeenCalledWith([processInstance], OperationType.ABORT);
  });

  it("process instance query", () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    gatewayApi.applySorting(sortBy);
    gatewayApi.query(0, 10);
    expect(queries.getProcessInstances).toHaveBeenCalledWith(0, 10, processListFilters, sortBy);
  });

  it("process instance child query", () => {
    gatewayApi.getChildProcessesQuery(rootProcessInstanceId);
    expect(queries.getChildProcessInstances).toHaveBeenCalledWith(rootProcessInstanceId);
  });
});
