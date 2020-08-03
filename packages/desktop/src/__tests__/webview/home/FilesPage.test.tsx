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
import { fireEvent, render, screen } from "@testing-library/react";
import { FilesPage } from "../../../webview/home/FilesPage";
import { usingTestingDesktopI18nContext, usingTestingGlobalContext } from "../../testing_utils";
import { act } from "react-dom/test-utils";

beforeEach(() => {
  document.execCommand = () => true;
});

describe("FilesPage", () => {
  test("empty page", () => {
    const openFile = jest.fn();
    const openFileByPath = jest.fn();

    const component = render(
      usingTestingDesktopI18nContext(
        usingTestingGlobalContext(<FilesPage openFile={openFile} openFileByPath={openFileByPath} />).wrapper
      ).wrapper
    );

    expect(component.asFragment()).toMatchSnapshot();
  });

  test("three recent files listed", () => {
    const openFile = jest.fn();
    const openFileByPath = jest.fn();

    const component = render(
      usingTestingDesktopI18nContext(
        usingTestingGlobalContext(<FilesPage openFile={openFile} openFileByPath={openFileByPath} />).wrapper
      ).wrapper
    );

    act(() =>
      electron.ipcRenderer.send("returnLastOpenedFiles", {
        lastOpenedFiles: [
          { filePath: "/a/b.dmn", preview: "" },
          { filePath: "/b/a.dmn", preview: "" },
          { filePath: "/c/c.dmn", preview: "" }
        ]
      })
    );
    expect(component.asFragment()).toMatchSnapshot();
  });

  test("three recent files in different directories listed ordered alphabetically", () => {
    const openFile = jest.fn();
    const openFileByPath = jest.fn();

    const component = render(
      usingTestingDesktopI18nContext(
        usingTestingGlobalContext(<FilesPage openFile={openFile} openFileByPath={openFileByPath} />).wrapper
      ).wrapper
    );

    act(() =>
      electron.ipcRenderer.send("returnLastOpenedFiles", {
        lastOpenedFiles: [
          { filePath: "/a/b.dmn", preview: "" },
          { filePath: "/b/a.dmn", preview: "" },
          { filePath: "/c/c.dmn", preview: "" }
        ]
      })
    );

    // alphabetical order
    fireEvent.click(component.getByTestId("orderAlphabeticallyButton"));
    expect(component.asFragment()).toMatchSnapshot();
  });

  test("three recent files in the same directory listed ordered alphabetically", () => {
    const openFile = jest.fn();
    const openFileByPath = jest.fn();

    const component = render(
      usingTestingDesktopI18nContext(
        usingTestingGlobalContext(<FilesPage openFile={openFile} openFileByPath={openFileByPath} />).wrapper
      ).wrapper
    );

    act(() =>
      electron.ipcRenderer.send("returnLastOpenedFiles", {
        lastOpenedFiles: [
          { filePath: "/a/a.dmn", preview: "" },
          { filePath: "/a/b.dmn", preview: "" },
          { filePath: "/a/b.bpmn", preview: "" },
          { filePath: "/a/c.bpmn", preview: "" }
        ]
      })
    );

    // alphabetical order
    fireEvent.click(component.getByTestId("orderAlphabeticallyButton"));
    expect(component.asFragment()).toMatchSnapshot();
  });

  test("three recent files listed ordered by access time", () => {
    const openFile = jest.fn();
    const openFileByPath = jest.fn();

    const component = render(
      usingTestingDesktopI18nContext(
        usingTestingGlobalContext(<FilesPage openFile={openFile} openFileByPath={openFileByPath} />).wrapper
      ).wrapper
    );

    act(() =>
      electron.ipcRenderer.send("returnLastOpenedFiles", {
        lastOpenedFiles: [
          { filePath: "/a/b.dmn", preview: "" },
          { filePath: "/b/a.dmn", preview: "" },
          { filePath: "/c/c.dmn", preview: "" }
        ]
      })
    );

    // alphabetical order
    fireEvent.click(component.getByTestId("orderAlphabeticallyButton"));
    // access time order
    fireEvent.click(component.getByTestId("orderAlphabeticallyButton"));
    expect(component.asFragment()).toMatchSnapshot();
  });
});
