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
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { SingleEditorApp } from "@kie-tooling-core/chrome-extension/dist/app/components/single/SingleEditorApp";
import {
  usingTestingChromeExtensionI18nContext,
  usingTestingGitHubContext,
  usingTestingGlobalContext,
} from "../../../testing_utils";
import { removeAllChildren } from "@kie-tooling-core/chrome-extension/dist/app/utils";

beforeAll(() => {
  chrome.extension = {
    getURL: jest.fn((path: string) => {
      return `chrome-testing://${path}`;
    }),
  } as any;
});

beforeEach(() => {
  removeAllChildren(document.body);
});

const testFileInfo = {
  repo: "test-repo",
  org: "test-org",
  path: "test/path/to/file.txt",
  gitRef: "test-branch",
};

function newDivOnBody() {
  const div = document.createElement("div");
  document.body.appendChild(div);
  return div;
}

describe("SingleEditorApp", () => {
  test("readonly", async () => {
    render(
      usingTestingChromeExtensionI18nContext(
        usingTestingGlobalContext(
          usingTestingGitHubContext(
            <SingleEditorApp
              openFileExtension={"txt"}
              readonly={false}
              getFileName={jest.fn()}
              getFileContents={jest.fn()}
              toolbarContainer={newDivOnBody()}
              iframeContainer={newDivOnBody()}
              githubTextEditorToReplace={newDivOnBody()}
              fileInfo={testFileInfo}
            />
          ).wrapper
        ).wrapper
      ).wrapper
    );

    expect(document.body).toMatchSnapshot();
  });

  test("not readonly", async () => {
    render(
      usingTestingChromeExtensionI18nContext(
        usingTestingGlobalContext(
          usingTestingGitHubContext(
            <SingleEditorApp
              openFileExtension={"txt"}
              readonly={true}
              getFileName={jest.fn()}
              getFileContents={jest.fn()}
              toolbarContainer={newDivOnBody()}
              iframeContainer={newDivOnBody()}
              githubTextEditorToReplace={newDivOnBody()}
              fileInfo={testFileInfo}
            />
          ).wrapper
        ).wrapper
      ).wrapper
    );

    expect(document.body).toMatchSnapshot();
  });

  test("go fullscreen", async () => {
    render(
      usingTestingChromeExtensionI18nContext(
        usingTestingGlobalContext(
          usingTestingGitHubContext(
            <SingleEditorApp
              openFileExtension={"txt"}
              readonly={false}
              getFileName={jest.fn()}
              getFileContents={jest.fn()}
              toolbarContainer={newDivOnBody()}
              iframeContainer={newDivOnBody()}
              githubTextEditorToReplace={newDivOnBody()}
              fileInfo={testFileInfo}
            />
          ).wrapper
        ).wrapper
      ).wrapper
    );

    fireEvent.click(screen.getByTestId("go-fullscreen-button"));
    expect(document.body).toMatchSnapshot();
  });

  test("go fullscreen and back", async () => {
    render(
      usingTestingChromeExtensionI18nContext(
        usingTestingGlobalContext(
          usingTestingGitHubContext(
            <SingleEditorApp
              openFileExtension={"txt"}
              readonly={false}
              getFileName={jest.fn()}
              getFileContents={jest.fn()}
              toolbarContainer={newDivOnBody()}
              iframeContainer={newDivOnBody()}
              githubTextEditorToReplace={newDivOnBody()}
              fileInfo={testFileInfo}
            />
          ).wrapper
        ).wrapper
      ).wrapper
    );

    fireEvent.click(screen.getByTestId("go-fullscreen-button"));
    fireEvent.click(screen.getByTestId("exit-fullscreen-button"));

    expect(document.body).toMatchSnapshot();
  });

  test("open external editor", async () => {
    let globalContext: ReturnType<typeof usingTestingGlobalContext>;
    render(
      usingTestingChromeExtensionI18nContext(
        (globalContext = usingTestingGlobalContext(
          usingTestingGitHubContext(
            <SingleEditorApp
              openFileExtension={"txt"}
              readonly={false}
              getFileName={jest.fn(() => "file.txt")}
              getFileContents={jest.fn(() => Promise.resolve("file contents"))}
              toolbarContainer={newDivOnBody()}
              iframeContainer={newDivOnBody()}
              githubTextEditorToReplace={newDivOnBody()}
              fileInfo={testFileInfo}
            />
          ).wrapper
        )).wrapper
      ).wrapper
    );

    fireEvent.click(screen.getByTestId("open-ext-editor-button"));
    await waitFor(() => expect(globalContext.ctx.externalEditorManager?.open).toHaveBeenCalled());
    expect(document.body).toMatchSnapshot();
  });

  test("open external editor and wait for come back", async () => {
    let globalContext: ReturnType<typeof usingTestingGlobalContext>;

    render(
      usingTestingChromeExtensionI18nContext(
        (globalContext = usingTestingGlobalContext(
          usingTestingGitHubContext(
            <SingleEditorApp
              openFileExtension={"txt"}
              readonly={false}
              getFileName={jest.fn(() => "file.txt")}
              getFileContents={jest.fn(() => Promise.resolve("file contents 1"))}
              toolbarContainer={newDivOnBody()}
              iframeContainer={newDivOnBody()}
              githubTextEditorToReplace={newDivOnBody()}
              fileInfo={testFileInfo}
            />
          ).wrapper
        )).wrapper
      ).wrapper
    );

    fireEvent.click(screen.getByTestId("open-ext-editor-button"));
    await waitFor(() => expect(globalContext.ctx.externalEditorManager?.open).toHaveBeenCalled());
    //TODO: Simulate comeback
    //TODO: Match snapshot with new file name and content
    expect(document.body).toMatchSnapshot();
  });
});
