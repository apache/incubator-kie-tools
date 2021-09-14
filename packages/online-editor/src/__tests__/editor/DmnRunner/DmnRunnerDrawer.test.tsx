/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Drawer, DrawerContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { act, render } from "@testing-library/react";
import * as React from "react";
import { DmnRunnerDrawer } from "../../../editor/DmnRunner/DmnRunnerDrawer";
import {
  usingTestingDmnRunnerContext,
  usingTestingGlobalContext,
  usingTestingKieToolingExtendedServicesContext,
  usingTestingNotificationsPanelContext,
  usingTestingOnlineI18nContext,
} from "../../testing_utils";
import { DmnRunnerService } from "./__mocks__/DmnRunnerService";

afterEach(() => {
  jest.resetAllMocks();
});

const editor: any = {
  isReady: true,
  getContent: () => new Promise((res) => res("")),
  getStateControl: () => ({
    subscribe: (callback: () => void) => {
      callback();
    },
    unsubscribe: (callback: () => void) => {},
  }),
};

describe("DmnRunnerDrawer", () => {
  it("should render the dmn runner drawer - no form", async () => {
    jest.useFakeTimers();
    const { getByText } = render(
      usingTestingOnlineI18nContext(
        usingTestingGlobalContext(
          usingTestingKieToolingExtendedServicesContext(
            usingTestingNotificationsPanelContext(
              usingTestingDmnRunnerContext(
                <Drawer isExpanded={true}>
                  <DrawerContent panelContent={<DmnRunnerDrawer editor={editor} />} />
                </Drawer>,
                editor
              )
            )
          ).wrapper
        ).wrapper
      ).wrapper
    );

    act(() => {
      jest.advanceTimersToNextTimer(3);
    });

    expect(getByText("No Form")).toMatchSnapshot();
  });

  it("should render the dmn runner drawer - with form", async () => {
    jest.useFakeTimers();

    const formSchema = {
      definitions: {
        InputSet: {
          type: "object",
          properties: {
            "InputData-1": {
              "x-dmn-type": "FEEL:Any",
            },
          },
        },
      },
      $ref: "#/definitions/InputSet",
    };

    jest
      .spyOn(DmnRunnerService.prototype, "formSchema")
      .mockImplementation(() => new Promise((res) => res(formSchema)));

    const { findByText } = render(
      usingTestingOnlineI18nContext(
        usingTestingGlobalContext(
          usingTestingKieToolingExtendedServicesContext(
            usingTestingNotificationsPanelContext(
              usingTestingDmnRunnerContext(
                <Drawer isExpanded={true}>
                  <DrawerContent panelContent={<DmnRunnerDrawer editor={editor} />} />
                </Drawer>,
                editor
              )
            )
          ).wrapper
        ).wrapper
      ).wrapper
    );

    act(() => {
      jest.advanceTimersToNextTimer(3);
    });

    expect(await findByText("InputData-1")).toMatchSnapshot();
  });

  it("should render the dmn runner drawer - with response", async () => {
    jest.setTimeout(10000);
    const result = {
      decisionResults: [
        {
          decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
          decisionName: "Decision-1",
          result: null,
          messages: [],
          evaluationStatus: "FAILED",
        },
      ],
    };

    jest.spyOn(DmnRunnerService.prototype, "result").mockImplementation(() => new Promise((res) => res(result)));
    jest.useFakeTimers();

    const { findByText } = render(
      usingTestingOnlineI18nContext(
        usingTestingGlobalContext(
          usingTestingKieToolingExtendedServicesContext(
            usingTestingNotificationsPanelContext(
              usingTestingDmnRunnerContext(
                <Drawer isExpanded={true}>
                  <DrawerContent panelContent={<DmnRunnerDrawer editor={editor} />} />
                </Drawer>,
                editor
              )
            )
          ).wrapper
        ).wrapper
      ).wrapper
    );

    act(() => {
      jest.advanceTimersToNextTimer(3);
      jest.advanceTimersByTime(1200);
    });

    expect(await findByText("Decision-1")).toMatchSnapshot();
  });
});
