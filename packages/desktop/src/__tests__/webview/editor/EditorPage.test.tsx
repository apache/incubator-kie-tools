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
import { usingTestingDesktopI18nContext, usingTestingGlobalContext } from "../../testing_utils";

const fileExtension = "";
const onClose = jest.fn(() => null);
const onFilenameChange = jest.fn((filePath: string) => null);

function mockFunctions() {
  const original = jest.requireActual("@kie-tooling-core/editor/dist/embedded");
  return {
    ...original,
    useDirtyState: jest.fn(() => true).mockImplementationOnce(() => false),
  };
}
jest.mock("@kie-tooling-core/editor/dist/embedded", () => mockFunctions());

describe("EditorPage", () => {
  describe("Unsaved Alert", () => {
    test("should not appear by default with isDirty equal to false", () => {
      const { queryByTestId } = render(
        usingTestingDesktopI18nContext(
          usingTestingGlobalContext(<EditorPage onFilenameChange={onFilenameChange} onClose={onClose} />).wrapper
        ).wrapper
      );

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should not appear by default with isDirty equal to true", () => {
      const { queryByTestId } = render(
        usingTestingDesktopI18nContext(
          usingTestingGlobalContext(<EditorPage onFilenameChange={onFilenameChange} onClose={onClose} />).wrapper
        ).wrapper
      );

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear when tries to close after an edit with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingDesktopI18nContext(
          usingTestingGlobalContext(<EditorPage onFilenameChange={onFilenameChange} onClose={onClose} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("close-editor-button"));

      expect(queryByTestId("unsaved-alert")).toBeVisible();
    });

    test("should appear and then close after click on save with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingDesktopI18nContext(
          usingTestingGlobalContext(<EditorPage onFilenameChange={onFilenameChange} onClose={onClose} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-save-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear and then close after click on close with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingDesktopI18nContext(
          usingTestingGlobalContext(<EditorPage onFilenameChange={onFilenameChange} onClose={onClose} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-close-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear and then close after click on close without save with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingDesktopI18nContext(
          usingTestingGlobalContext(<EditorPage onFilenameChange={onFilenameChange} onClose={onClose} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-close-without-save-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });
  });
});
