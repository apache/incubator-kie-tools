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
import * as H from "history";
import { MemoryRouter } from "react-router";
import { act } from "react-dom/test-utils";
import wait from "waait";
import { ServerErrors } from "@kogito-apps/components-common/dist/components/ServerErrors";
import { KogitoEmptyState } from "@kogito-apps/components-common/dist/components/KogitoEmptyState";
import { mount } from "enzyme";
import { UserTaskInstance } from "@kogito-apps/task-console-shared";
import { TaskInboxGatewayApi } from "../../../../channel/inbox";
import * as TaskInboxContext from "../../../../channel/inbox/TaskInboxContext";
import TaskDetailsPage from "../TaskDetailsPage";
import TaskFormContainer from "../components/TaskFormContainer/TaskFormContainer";
import FormNotification from "../components/FormNotification/FormNotification";

import { Button, DrawerCloseButton, DrawerPanelContent } from "@patternfly/react-core";

const userTask: UserTaskInstance = {
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

const props = {
  match: {
    params: {
      taskId: "45a73767-5da3-49bf-9c40-d533c3e77ef3",
    },
    url: "",
    isExact: false,
    path: "",
  },
  location: {
    hash: "",
    pathname: "/",
    search: "",
    state: undefined,
  },
  history: H.createBrowserHistory(),
};

const pushSpy = jest.spyOn(props.history, "push");
pushSpy.mockImplementation(() => {
  // do nothing
});

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock("../components/FormNotification/FormNotification");
jest.mock("../components/TaskFormContainer/TaskFormContainer");

jest.mock("@patternfly/react-core", () =>
  Object.assign({}, jest.requireActual("@patternfly/react-core"), {
    Breadcrumb: () => {
      return <MockedComponent />;
    },
    BreadcrumbItem: () => {
      return <MockedComponent />;
    },
    Button: () => {
      return <MockedComponent />;
    },
  })
);

jest.mock("@kogito-apps/components-common/dist/components/ServerErrors", () =>
  Object.assign({}, jest.requireActual("@kogito-apps/components-common"), {
    ServerErrors: () => {
      return <MockedComponent />;
    },
  })
);

jest.mock("@kogito-apps/components-common/dist/components/KogitoEmptyState", () =>
  Object.assign({}, jest.requireActual("@kogito-apps/components-common"), {
    KogitoEmptyState: () => {
      return <MockedComponent />;
    },
  })
);

jest.mock("@kogito-apps/consoles-common/dist/components/layout/PageTitle", () =>
  Object.assign({}, jest.requireActual("@kogito-apps/consoles-common"), {
    PageTitle: () => {
      return <MockedComponent />;
    },
  })
);

jest.mock("@kogito-apps/task-details", () =>
  Object.assign({}, jest.requireActual("@kogito-apps/task-details"), {
    EmbeddedTaskDetails: () => {
      return <MockedComponent />;
    },
  })
);

const getUserTaskByIdMock = jest.fn();

const MockTaskInboxGatewayApi = jest.fn<TaskInboxGatewayApi, []>(() => ({
  setInitialState: jest.fn(),
  applyFilter: jest.fn(),
  applySorting: jest.fn(),
  query: jest.fn(),
  getTaskById: getUserTaskByIdMock,
  openTask: jest.fn(),
  clearOpenTask: jest.fn(),
  onOpenTaskListen: jest.fn(),
  taskInboxState: undefined,
}));

jest.spyOn(TaskInboxContext, "useTaskInboxGatewayApi").mockImplementation(() => gatewayApi);

let gatewayApi: TaskInboxGatewayApi;

const getTaskDetailsPageWrapper = async () => {
  let wrapper = null;
  await act(async () => {
    await act(async () => {
      wrapper = mount(
        <MemoryRouter keyLength={0} initialEntries={["/"]}>
          <TaskDetailsPage {...props} />
        </MemoryRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find("TaskDetailsPage");
    });
    wait();
  });

  return wrapper;
};

describe("TaskDetailsPage tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new MockTaskInboxGatewayApi();
  });

  it("Empty state", async () => {
    const wrapper = await getTaskDetailsPageWrapper();

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeTruthy();

    expect(emptyState.props().body).toStrictEqual("Cannot find task with id '45a73767-5da3-49bf-9c40-d533c3e77ef3'");
    expect(emptyState.props().title).toStrictEqual("Cannot find task");
  });

  it("Error state", async () => {
    getUserTaskByIdMock.mockImplementation(() => {
      throw new Error("Error: Something went wrong on server!");
    });

    const wrapper = await getTaskDetailsPageWrapper();

    expect(wrapper).toMatchSnapshot();

    const serverErrors = wrapper.find(ServerErrors);
    expect(serverErrors.exists()).toBeTruthy();
    expect(serverErrors.props().error.message).toEqual("Error: Something went wrong on server!");
  });

  it("Normal State", async () => {
    getUserTaskByIdMock.mockReturnValue(userTask);

    const wrapper = await getTaskDetailsPageWrapper();

    expect(wrapper).toMatchSnapshot();

    const taskForm = wrapper.find(TaskFormContainer);

    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().userTask).toStrictEqual(userTask);
  });

  it("Success notification", async () => {
    getUserTaskByIdMock.mockReturnValue(userTask);

    let wrapper = await getTaskDetailsPageWrapper();

    const taskForm = wrapper.find(TaskFormContainer);

    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().userTask).toStrictEqual(userTask);

    await act(async () => {
      taskForm.props().onSubmitSuccess("complete");
      wait();
    });

    wrapper = wrapper.update().find(TaskDetailsPage);

    expect(wrapper).toMatchSnapshot();

    const notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeTruthy();

    const notification = notificationComponent.props().notification;

    expect(notification).not.toBeNull();
    expect(notification.type).toStrictEqual("success");
    expect(notification.message).toStrictEqual("Task 'Apply for visa' successfully transitioned to phase 'complete'.");
    expect(notification.details).toBeUndefined();
    expect(notification.customAction).not.toBeNull();

    await act(async () => {
      notification.close();
    });

    wrapper = wrapper.update().find(TaskDetailsPage);
    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(FormNotification).exists()).toBeFalsy();
  });

  it("Success notification - go to inbox link", async () => {
    getUserTaskByIdMock.mockReturnValue(userTask);

    let wrapper = await getTaskDetailsPageWrapper();

    const taskForm = wrapper.find(TaskFormContainer);

    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().userTask).toStrictEqual(userTask);

    await act(async () => {
      taskForm.props().onSubmitSuccess("complete");
      wait();
    });

    wrapper = wrapper.update().find(TaskDetailsPage);

    const notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeTruthy();

    const notification = notificationComponent.props().notification;

    expect(notification).not.toBeNull();
    expect(notification.type).toStrictEqual("success");

    await act(async () => {
      notification.customAction.onClick();
      wait();
    });

    expect(pushSpy).toBeCalledWith("/");
  });

  it("Error notification", async () => {
    getUserTaskByIdMock.mockReturnValue(userTask);

    let wrapper = await getTaskDetailsPageWrapper();

    const taskForm = wrapper.find(TaskFormContainer);

    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().userTask).toStrictEqual(userTask);

    await act(async () => {
      taskForm.props().onSubmitError("complete", "Extra info!");
      wait();
    });

    wrapper = wrapper.update().find(TaskDetailsPage);

    expect(wrapper).toMatchSnapshot();

    const notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeTruthy();

    const notification = notificationComponent.props().notification;

    expect(notification).not.toBeNull();
    expect(notification.type).toStrictEqual("error");
    expect(notification.message).toStrictEqual("Task 'Apply for visa' couldn't transition to phase 'complete'.");
    expect(notification.details).not.toBeUndefined();
    expect(notification.customAction).not.toBeNull();

    await act(async () => {
      notification.close();
    });

    wrapper = wrapper.update().find(TaskDetailsPage);

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(FormNotification).exists()).toBeFalsy();
  });

  it("Task details Drawer", async () => {
    getUserTaskByIdMock.mockReturnValue(userTask);

    let wrapper = await getTaskDetailsPageWrapper();

    // open details drawer
    await act(async () => {
      const button = wrapper.find("Button").findWhere((node) => node.props().id === "view-details");

      button.at(0).props().onClick();
    });
    wrapper = wrapper.update();

    const detailsPanel = wrapper.find(DrawerPanelContent);

    expect(detailsPanel.exists()).toBeTruthy();

    expect(detailsPanel.find("EmbeddedTaskDetails").props().userTask).toStrictEqual(userTask);

    // close details drawer
    await act(async () => {
      detailsPanel.find(DrawerCloseButton).find("button").simulate("click");
    });
    wrapper = wrapper.update();
  });
});
