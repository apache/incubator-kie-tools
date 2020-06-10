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
import { EditorPage } from "../../../webview/editor/EditorPage";
import { usingTestingGlobalContext } from "../../testing_utils";
import { StateControl } from "@kogito-tooling/embedded-editor";
import { act } from "react-dom/test-utils";

const mockHistoryPush = jest.fn();

jest.mock("react-router", () => ({
  ...jest.requireActual("react-router"),
  useHistory: () => ({
    push: mockHistoryPush
  })
}));

const editorType = "";
const onClose = jest.fn(() => null);

describe("EditorPage", () => {
  let stateControl: StateControl;

  beforeEach(() => {
    stateControl = new StateControl();
  });

  describe("Unsaved Alert", () => {
    test("should not appear by default", () => {
      const { queryByTestId } = render(
        usingTestingGlobalContext(<EditorPage onClose={onClose} editorType={editorType}/>).wrapper
      );

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should not appear after an edit", () => {
      const { queryByTestId } = render(
        usingTestingGlobalContext(<EditorPage onClose={onClose} editorType={editorType}/>, { stateControl }).wrapper
      );

      act(() => stateControl.updateEventStack("1"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear when tries to close after an edit", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingGlobalContext(<EditorPage onClose={onClose} editorType={editorType}/>, { stateControl }).wrapper
      );

      act(() => stateControl.updateEventStack("1"));
      fireEvent.click(getByTestId("close-editor-button"));

      expect(queryByTestId("unsaved-alert")).toBeVisible();
    });

    test("should appear and then close after click on save", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingGlobalContext(<EditorPage onClose={onClose} editorType={editorType}/>, { stateControl }).wrapper
      );

      act(() => stateControl.updateEventStack("1"));
      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-save-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear and then close after click on close", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingGlobalContext(<EditorPage onClose={onClose} editorType={editorType}/>, { stateControl }).wrapper
      );

      act(() => stateControl.updateEventStack("1"));
      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-close-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear and then close after click on close without save", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingGlobalContext(<EditorPage onClose={onClose} editorType={editorType}/>, { stateControl }).wrapper
      );

      act(() => stateControl.updateEventStack("1"));
      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-close-without-save-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });
  });
});
