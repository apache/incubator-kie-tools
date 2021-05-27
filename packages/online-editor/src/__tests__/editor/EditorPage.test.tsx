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
import { EditorPage } from "../../editor/EditorPage";
import { usingTestingGlobalContext, usingTestingOnlineI18nContext } from "../testing_utils";

const onFileNameChanged = jest.fn((file: string) => null);

function mockFunctions() {
  const original = jest.requireActual("@kogito-tooling/editor/dist/embedded");
  return {
    ...original,
    useDirtyState: jest.fn(() => true).mockImplementationOnce(() => false),
  };
}
jest.mock("@kogito-tooling/editor/dist/embedded", () => mockFunctions());

afterAll(() => {
  jest.resetAllMocks();
});

describe("EditorPage", () => {
  describe("Unsaved Alert", () => {
    test("should not appear by default with isDirty equal to false", () => {
      const { queryByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should not appear by default with isDirty equal to true", () => {
      const { queryByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear when tries to close with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("view-kebab"));
      fireEvent.click(getByTestId("close-editor-button"));

      expect(queryByTestId("unsaved-alert")).toBeVisible();
    });

    test("should appear and then close after click on save with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("view-kebab"));
      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-save-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear and then close after click on close with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("view-kebab"));
      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-close-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });

    test("should appear and then close after click on close without save with isDirty equal to true", () => {
      const { getByTestId, queryByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("view-kebab"));
      fireEvent.click(getByTestId("close-editor-button"));
      fireEvent.click(getByTestId("unsaved-alert-close-without-save-button"));

      expect(queryByTestId("unsaved-alert")).toBeNull();
    });
  });
});
