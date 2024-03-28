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
import TaskFormContainer from "../TaskFormContainer";
import { TaskFormGatewayApi } from "../../../../../../channel/forms";
import * as TaskFormContext from "../../../../../../channel/forms/TaskFormContext";
import { UserTaskInstance } from "@kogito-apps/task-console-shared";
import { KogitoAppContextProvider } from "@kogito-apps/consoles-common/dist/environment/context";
import { DefaultUser, User } from "@kogito-apps/consoles-common/dist/environment/auth";

const testUserTask: UserTaskInstance = {
  id: "45a73767-5da3-49bf-9c40-d533c3e77ef3",
  description: null,
  name: "VisaApplication",
  referenceName: "Apply for visa",
  priority: "1",
  processInstanceId: "9ae7ce3b-d49c-4f35-b843-8ac3d22fa427",
  processId: "travels",
  rootProcessInstanceId: null,
  rootProcessId: null,
  state: "Ready",
  actualOwner: null,
  adminGroups: [],
  adminUsers: [],
  completed: null,
  started: new Date("2020-02-19T11:11:56.282Z"),
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: "{}",
  lastUpdate: new Date("2020-02-19T11:11:56.282Z"),
  endpoint:
    "http://localhost:8080/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3",
};

const MockTaskFormGatewayApi = jest.fn<TaskFormGatewayApi, []>(() => ({
  getTaskFormSchema: jest.fn(),
  getCustomForm: jest.fn(),
  doSubmit: jest.fn(),
}));

jest.spyOn(TaskFormContext, "useTaskFormGatewayApi").mockImplementation(() => new MockTaskFormGatewayApi());

const user: User = new DefaultUser("jon", []);

describe("TaskFormContainer tests", () => {
  it("Snapshot", () => {
    const onSubmit = jest.fn();
    const onFailure = jest.fn();
    const wrapper = mount(
      <KogitoAppContextProvider
        userContext={{
          getCurrentUser: () => user,
        }}
      >
        <TaskFormContainer userTask={testUserTask} onSubmitSuccess={onSubmit} onSubmitError={onFailure} />
      </KogitoAppContextProvider>
    ).find("TaskFormContainer");

    expect(wrapper).toMatchSnapshot();

    const forwardRef = wrapper.childAt(0);
    expect(forwardRef.props().driver).not.toBeNull();
    expect(forwardRef.props().user).toStrictEqual(user);
    expect(forwardRef.props().targetOrigin).toBe("http://localhost");
  });
});
