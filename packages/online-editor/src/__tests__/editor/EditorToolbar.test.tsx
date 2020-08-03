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
import { render } from "@testing-library/react";
import { EditorToolbar } from "../../editor/EditorToolbar";
import { usingTestingGlobalContext } from "../testing_utils";
import { StateControl } from "@kogito-tooling/editor/dist/embedded";
const onFileNameChanged = jest.fn((file: string) => null);
const enterFullscreen = jest.fn(() => null);
const requestSave = jest.fn(() => null);
const close = jest.fn(() => null);
const requestCopyContentToClipboard = jest.fn(() => null);
const fullscreen = false;
const requestPreview = jest.fn(() => null);
const requestExportGist = jest.fn(() => null);

describe("EditorToolbar", () => {
  let stateControl: StateControl;
  let requestDownload: () => null;

  beforeEach(() => {
    stateControl = new StateControl();
    requestDownload = jest.fn().mockImplementation(() => {
      stateControl.setSavedCommand();
    });
  });

  describe("is dirty indicator", () => {
    test("should show the isDirty indicator when isEdited is true", () => {
      const isEdited = true;

      const { queryByTestId, getByTestId } = render(
        usingTestingGlobalContext(
          <EditorToolbar
            onFullScreen={enterFullscreen}
            onSave={requestSave}
            onDownload={requestDownload}
            onClose={close}
            onFileNameChanged={onFileNameChanged}
            onCopyContentToClipboard={requestCopyContentToClipboard}
            isPageFullscreen={fullscreen}
            onPreview={requestPreview}
            onExportGist={requestExportGist}
            isEdited={isEdited}
          />
        ).wrapper
      );

      expect(queryByTestId("is-dirty-indicator")).toBeVisible();
      expect(getByTestId("toolbar-title")).toMatchSnapshot();
    });

    test("shouldn't show the isDirty indicator when isEdited is false", () => {
      const isEdited = false;

      const { queryByTestId, getByTestId } = render(
        usingTestingGlobalContext(
          <EditorToolbar
            onFullScreen={enterFullscreen}
            onSave={requestSave}
            onDownload={requestDownload}
            onClose={close}
            onFileNameChanged={onFileNameChanged}
            onCopyContentToClipboard={requestCopyContentToClipboard}
            isPageFullscreen={fullscreen}
            onPreview={requestPreview}
            onExportGist={requestExportGist}
            isEdited={isEdited}
          />
        ).wrapper
      );

      expect(queryByTestId("is-dirty-indicator")).toBeNull();
      expect(getByTestId("toolbar-title")).toMatchSnapshot();
    });
  });
});
