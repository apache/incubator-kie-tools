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
import { EMPTY_FILE_BPMN, EMPTY_FILE_DMN, EMPTY_FILE_PMML, StateControl } from "@kie-tooling-core/editor/dist/channel";
import { usingTestingGlobalContext, usingTestingOnlineI18nContext } from "../testing_utils";
import { GithubService } from "../../common/GithubService";
import { EditorPage } from "../../editor/EditorPage";

const onFileNameChanged = jest.fn((file: string) => null);
const enterFullscreen = jest.fn(() => null);
const requestSave = jest.fn(() => null);
const close = jest.fn(() => null);
const requestCopyContentToClipboard = jest.fn(() => null);
const fullscreen = false;
const requestPreview = jest.fn(() => null);
const requestGistIt = jest.fn(() => null);
const requestSetGitHubToken = jest.fn(() => null);
const requestEmbed = jest.fn(() => null);

function mockFunctions() {
  const original = jest.requireActual("../../common/Hooks");
  return {
    ...original,
    useFileUrl: jest.fn().mockImplementation(() => "gist.githubusercontent.com/?file=something"),
  };
}

jest.mock("../../common/Hooks", () => mockFunctions());

afterAll(() => {
  jest.resetAllMocks();
});

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
        usingTestingOnlineI18nContext(
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
              onSetGitHubToken={requestSetGitHubToken}
              onGistIt={requestGistIt}
              onEmbed={requestEmbed}
              isEdited={isEdited}
            />
          ).wrapper
        ).wrapper
      );

      expect(queryByTestId("is-dirty-indicator")).toBeVisible();
      expect(getByTestId("toolbar-title")).toMatchSnapshot();
    });

    test("shouldn't show the isDirty indicator when isEdited is false", () => {
      const isEdited = false;

      const { queryByTestId, getByTestId } = render(
        usingTestingOnlineI18nContext(
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
              onSetGitHubToken={requestSetGitHubToken}
              onGistIt={requestGistIt}
              onEmbed={requestEmbed}
              isEdited={isEdited}
            />
          ).wrapper
        ).wrapper
      );

      expect(queryByTestId("is-dirty-indicator")).toBeNull();
      expect(getByTestId("toolbar-title")).toMatchSnapshot();
    });
  });

  describe("file actions", () => {
    test("Gist it button should be disable without token", async () => {
      const githubService = new GithubService();

      const { getByTestId } = render(
        usingTestingOnlineI18nContext(
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
              onSetGitHubToken={requestSetGitHubToken}
              onGistIt={requestGistIt}
              onEmbed={requestEmbed}
              isEdited={false}
            />,
            { githubService }
          ).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("share-menu"));
      expect(getByTestId("gist-it-button")).toBeVisible();
      expect(getByTestId("gist-it-button")).toHaveAttribute("aria-disabled", "true");
      expect(getByTestId("share-menu")).toMatchSnapshot();
    });

    test("Set GitHub token button should open a GitHubTokenModal", async () => {
      const { getByTestId } = render(
        usingTestingOnlineI18nContext(
          usingTestingGlobalContext(<EditorPage onFileNameChanged={onFileNameChanged} />).wrapper
        ).wrapper
      );

      fireEvent.click(getByTestId("share-menu"));
      fireEvent.click(getByTestId("set-github-token"));
      expect(getByTestId("github-token-modal")).toBeVisible();
      expect(getByTestId("github-token-modal")).toMatchSnapshot();
    });
  });

  describe("share dropdown items", () => {
    const toolbar = (
      <EditorToolbar
        onFullScreen={enterFullscreen}
        onSave={requestSave}
        onDownload={requestDownload}
        onClose={close}
        onFileNameChanged={onFileNameChanged}
        onCopyContentToClipboard={requestCopyContentToClipboard}
        isPageFullscreen={fullscreen}
        onPreview={requestPreview}
        onSetGitHubToken={requestSetGitHubToken}
        onGistIt={requestGistIt}
        onEmbed={requestEmbed}
        isEdited={false}
      />
    );
    const context = usingTestingGlobalContext(toolbar);

    test("should include Download SVG when dmn", () => {
      context.ctx.file = EMPTY_FILE_DMN;
      const { getByTestId } = render(usingTestingOnlineI18nContext(context.wrapper).wrapper);

      expect(getByTestId("share-menu")).toBeVisible();
      fireEvent.click(getByTestId("share-menu"));
      expect(getByTestId("dropdown-download-svg")).toBeVisible();
    });

    test("should include Download SVG when bpmn", () => {
      context.ctx.file = EMPTY_FILE_BPMN;
      const { getByTestId } = render(usingTestingOnlineI18nContext(context.wrapper).wrapper);

      expect(getByTestId("share-menu")).toBeVisible();
      fireEvent.click(getByTestId("share-menu"));
      expect(getByTestId("dropdown-download-svg")).toBeVisible();
    });

    test("should exclude Download SVG when pmml", () => {
      context.ctx.file = EMPTY_FILE_PMML;
      const { queryByTestId, getByTestId } = render(usingTestingOnlineI18nContext(context.wrapper).wrapper);

      expect(getByTestId("share-menu")).toBeVisible();
      fireEvent.click(getByTestId("share-menu"));
      expect(queryByTestId("dropdown-download-svg")).toBeNull();
    });

    test("should include Embed when dmn", () => {
      context.ctx.file = EMPTY_FILE_DMN;
      const { getByTestId } = render(usingTestingOnlineI18nContext(context.wrapper).wrapper);

      expect(getByTestId("share-menu")).toBeVisible();
      fireEvent.click(getByTestId("share-menu"));
      expect(getByTestId("dropdown-embed")).toBeVisible();
    });

    test("should include Embed when bpmn", () => {
      context.ctx.file = EMPTY_FILE_BPMN;
      const { getByTestId } = render(usingTestingOnlineI18nContext(context.wrapper).wrapper);

      expect(getByTestId("share-menu")).toBeVisible();
      fireEvent.click(getByTestId("share-menu"));
      expect(getByTestId("dropdown-embed")).toBeVisible();
    });

    test("should exclude Embed when pmml", () => {
      context.ctx.file = EMPTY_FILE_PMML;
      const { queryByTestId, getByTestId } = render(usingTestingOnlineI18nContext(context.wrapper).wrapper);

      expect(getByTestId("share-menu")).toBeVisible();
      fireEvent.click(getByTestId("share-menu"));
      expect(queryByTestId("dropdown-embed")).toBeNull();
    });
  });
});
