/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import * as electron from "electron";
import { render, screen, act, waitForElementToBeRemoved } from "@testing-library/react";
import { App } from "../../webview/App";
import { usingTestingGlobalContext } from "../testing_utils";

describe("invalid file type alert", () => {
  test("alert is closed after 3000ms", async () => {
    const component = render(usingTestingGlobalContext(<App />).wrapper);

    act(() => {
      electron.ipcRenderer.send("openFile", {
        file: { filePath: "/a/a.invalid", fileType: "invalid", fileContent: "" }
      });
    });

    expect(component.asFragment()).toMatchSnapshot();
    await waitForElementToBeRemoved(screen.getByText("This file extension is not supported."), { timeout: 4000 });
  });

  test("alert is closed immediately after leaving the home page", async () => {
    const component = render(usingTestingGlobalContext(<App />).wrapper);

    act(() => {
      electron.ipcRenderer.send("openFile", {
        file: { filePath: "/a/a.invalid", fileType: "invalid", fileContent: "" }
      });
    });

    expect(component.asFragment()).toMatchSnapshot();

    act(() => {
      electron.ipcRenderer.send("openFile", {
        file: { filePath: "/a/a.dmn", fileType: "dmn", fileContent: "" }
      });
    });

    expect(component.asFragment()).toMatchSnapshot();
  });
});
