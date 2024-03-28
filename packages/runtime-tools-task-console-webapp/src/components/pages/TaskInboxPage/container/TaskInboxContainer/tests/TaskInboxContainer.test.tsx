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
import React from "react";
import { mount } from "enzyme";
import { DefaultUser } from "@kogito-apps/consoles-common/dist/environment/auth";
import TaskInboxContainer from "../TaskInboxContainer";
import * as TaskInboxContext from "../../../../../../channel/inbox/TaskInboxContext";
import { TaskInboxQueries } from "../../../../../../channel/inbox/TaskInboxQueries";
import { TaskInboxGatewayApiImpl } from "../../../../../../channel/inbox/TaskInboxGatewayApi";

const MockQueries = jest.fn<TaskInboxQueries, []>(() => ({
  getUserTaskById: jest.fn(),
  getUserTasks: jest.fn(),
}));

jest
  .spyOn(TaskInboxContext, "useTaskInboxGatewayApi")
  .mockImplementation(() => new TaskInboxGatewayApiImpl(new DefaultUser("jon", []), new MockQueries()));

describe("TaskInboxContainer tests", () => {
  it("Snapshot", () => {
    const wrapper = mount(<TaskInboxContainer />).find("TaskInboxContainer");

    expect(wrapper).toMatchSnapshot();

    const forwardRef = wrapper.childAt(0);
    expect(forwardRef.props().activeTaskStates).toStrictEqual(["Ready", "Reserved"]);
    expect(forwardRef.props().allTaskStates).toStrictEqual(["Ready", "Reserved", "Completed", "Aborted", "Skipped"]);
    expect(forwardRef.props().driver).not.toBeNull();
    expect(forwardRef.props().targetOrigin).toBe("http://localhost");
  });
});
