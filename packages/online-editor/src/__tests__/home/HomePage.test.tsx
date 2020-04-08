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
import { fireEvent, render, waitFor } from "@testing-library/react";
import { HomePage } from "../../home/HomePage";
import { usingTestingGlobalContext } from "../testing_utils";
import { File as UploadFile } from "../../common/File";
import GithubService from "./GithubServiceExportDefault";

const mockHistoryPush = jest.fn();

jest.mock("react-router", () => ({
  ...jest.requireActual("react-router"),
  useHistory: () => ({
    push: mockHistoryPush
  })
}));

jest.mock("./GithubServiceExportDefault");

describe("HomePage", () => {
  describe("open from url", () => {
    test("invalid url", () => {
      const onFileOpened = (file: UploadFile) => true;
      const { getByText, getByTestId } = render(
        usingTestingGlobalContext(<HomePage onFileOpened={onFileOpened} />).wrapper
      );

      const invalidUrls = [".", "something", "something.com"];

      invalidUrls.forEach(url => {
        fireEvent.change(getByTestId("url-text-input"), { target: { value: url } });
        expect(getByText(`This URL is not valid (don't forget "https://"!).`)).toBeTruthy();
      });
    });

    test("no file extension", async () => {
      const onFileOpened = (file: UploadFile) => true;
      const { getByText, getByTestId } = render(
        usingTestingGlobalContext(<HomePage onFileOpened={onFileOpened} />).wrapper
      );

      const urlsNoFile = ["https://github.com/something", "https://dropbox.com/teste"];

      urlsNoFile.forEach(url => {
        fireEvent.change(getByTestId("url-text-input"), { target: { value: "https://github.com/something" } });
        expect(getByText("This URL is not from a file.")).toBeTruthy();
      });
    });

    test("invalid extension", async () => {
      const onFileOpened = (file: UploadFile) => true;
      const { getByText, getByTestId } = render(
        usingTestingGlobalContext(<HomePage onFileOpened={onFileOpened} />).wrapper
      );

      const urlsInvalidFileExtension = [
        "https://github.com/something.test",
        "https://github.com/kiegroup/kogito-tooling/blob/master/README.md",
        "https://dropbox.com/test.png"
      ];

      urlsInvalidFileExtension.forEach(url => {
        fireEvent.change(getByTestId("url-text-input"), { target: { value: url } });
        expect(getByText(`The file type of this URL is not supported.`)).toBeTruthy();
      });
    });

    test("not found url", async () => {
      const checkGithubFileExistence = jest.fn(() => {
        return Promise.reject();
      });
      (GithubService as jest.Mock).mockImplementation(() => {
        return {
          isGithub: () => true,
          checkGithubFileExistence
        };
      });

      const onFileOpened = (file: UploadFile) => true;
      const { getByText, getByTestId } = render(
        usingTestingGlobalContext(<HomePage onFileOpened={onFileOpened} />).wrapper
      );

      const urlsNotFound = ["https://github.com/something.dmn"];

      urlsNotFound.forEach(url => {
        fireEvent.change(getByTestId("url-text-input"), { target: { value: url } });
        waitFor(() => expect(getByText(`This URL does not exist.`)).toBeTruthy())
      });
    });
  });
});
