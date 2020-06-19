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
import { fireEvent, render } from "@testing-library/react";
import { EditorToolbar } from "../../editor/EditorToolbar";
import { usingTestingGlobalContext } from "../testing_utils";
import { StateControl } from "@kogito-tooling/embedded-editor";
import { act } from "react-dom/test-utils";

const mockHistoryPush = jest.fn();

jest.mock("react-router", () => ({
  ...jest.requireActual("react-router"),
  useHistory: () => ({
    push: mockHistoryPush
  })
}));

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
    describe("with isDirty indicator", () => {
      test("should show the isDirty indicator - new edit", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        act(() => stateControl.updateCommandStack("1"));
        expect(queryByTestId("is-dirty-indicator")).toBeVisible();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });

      test("should show the isDirty indicator - save and make a new edit", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        act(() => stateControl.updateCommandStack("1"));
        fireEvent.click(getByTestId("save-button"));
        expect(queryByTestId("is-dirty-indicator")).toBeNull();

        act(() => stateControl.updateCommandStack("2"));
        expect(queryByTestId("is-dirty-indicator")).toBeVisible();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });

      test("should show the isDirty indicator - save and undo the last edit", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        act(() => stateControl.updateCommandStack("1"));
        fireEvent.click(getByTestId("save-button"));
        expect(queryByTestId("is-dirty-indicator")).toBeNull();

        act(() => stateControl.undo());
        expect(queryByTestId("is-dirty-indicator")).toBeVisible();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });
    });

    describe("without isDirty indicator", () => {
      test("shouldn't show the isDirty indicator - new file", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        expect(queryByTestId("is-dirty-indicator")).toBeNull();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });

      test("should show the isDirty indicator - make an edit and save", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        act(() => stateControl.updateCommandStack("1"));
        fireEvent.click(getByTestId("save-button"));

        expect(queryByTestId("is-dirty-indicator")).toBeNull();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });

      test("should show the isDirty indicator - make an edit and undo it", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        act(() => stateControl.updateCommandStack("1"));
        expect(queryByTestId("is-dirty-indicator")).toBeVisible();

        act(() => stateControl.undo());
        expect(queryByTestId("is-dirty-indicator")).toBeNull();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });

      test("should show the isDirty indicator - make an edit, save, undo it, redo it", () => {
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
              isEdited={true}
            />
          ).wrapper
        );

        act(() => stateControl.updateCommandStack("1"));
        expect(queryByTestId("is-dirty-indicator")).toBeVisible();

        fireEvent.click(getByTestId("save-button"));
        expect(queryByTestId("is-dirty-indicator")).toBeNull();

        act(() => stateControl.undo());
        expect(queryByTestId("is-dirty-indicator")).toBeVisible();

        act(() => stateControl.redo());
        expect(queryByTestId("is-dirty-indicator")).toBeNull();
        expect(getByTestId("toolbar-title")).toMatchSnapshot();
      });
    });
  });
});
