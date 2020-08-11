/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import * as ReactDOM from "react-dom";
import { App } from "./App";
import { newFile } from "@kogito-tooling/editor/dist/embedded";
import {
  extractEditorFileExtensionFromUrl,
  extractFileExtension,
  removeDirectories,
  removeFileExtension
} from "./common/utils";
import { GithubService } from "./common/GithubService";
import { Alert, AlertActionLink, AlertVariant } from "@patternfly/react-core";
import { EditorEnvelopeLocator } from "@kogito-tooling/editor/dist/api";

const urlParams = new URLSearchParams(window.location.search);
const githubService = new GithubService();
const editorEnvelopeLocator: EditorEnvelopeLocator = {
  targetOrigin: window.location.origin,
  mapping: new Map([
    ["bpmn", { resourcesPathPrefix: "../gwt-editors/bpmn", envelopePath: "envelope/envelope.html" }],
    ["bpmn2", { resourcesPathPrefix: "../gwt-editors/bpmn", envelopePath: "envelope/envelope.html" }],
    ["dmn", { resourcesPathPrefix: "../gwt-editors/dmn", envelopePath: "envelope/envelope.html" }]
  ])
};

if (urlParams.has("ext")) {
  waitForEventWithFileData();
} else if (urlParams.has("file")) {
  openFileByUrl();
} else {
  openDefaultOnlineEditor();
}

function openDefaultOnlineEditor() {
  ReactDOM.render(
    <App
      file={newFile(extractEditorFileExtensionFromUrl([...editorEnvelopeLocator.mapping.keys()]) ?? "dmn")}
      readonly={false}
      external={false}
      githubService={githubService}
      editorEnvelopeLocator={editorEnvelopeLocator}
    />,
    document.getElementById("app")!
  );
}

function waitForEventWithFileData() {
  window.addEventListener("loadOnlineEditor", (e: CustomEvent) => {
    const file = {
      isReadOnly: false,
      fileExtension: extractFileExtension(e.detail.fileName)!,
      fileName: removeFileExtension(e.detail.fileName),
      getFileContents: () => Promise.resolve(e.detail.fileContent)
    };
    ReactDOM.render(
      <App
        file={file}
        readonly={e.detail.readonly}
        external={true}
        senderTabId={e.detail.senderTabId}
        githubService={githubService}
        editorEnvelopeLocator={editorEnvelopeLocator}
      />,
      document.getElementById("app")!
    );
  });
}

function openFileByUrl() {
  const filePath = urlParams.get("file")!;
  if (githubService.isGithub(filePath)) {
    githubService
      .fetchGithubFile(filePath)
      .then(response => {
        openFile(filePath, Promise.resolve(response));
      })
      .catch(error => {
        showFetchError(error.toString());
      });
  } else {
    fetch(filePath)
      .then(response => {
        if (response.ok) {
          openFile(filePath, response.text());
        } else {
          showResponseError(response.status, response.statusText);
        }
      })
      .catch(error => {
        showFetchError(error.toString());
      });
  }
}

function openFile(filePath: string, getFileContent: Promise<string>) {
  const file = {
    isReadOnly: false,
    fileExtension: extractFileExtension(removeDirectories(filePath) ?? "")!,
    fileName: removeFileExtension(removeDirectories(filePath) ?? ""),
    getFileContents: () => getFileContent
  };
  ReactDOM.render(
    <App
      file={file}
      readonly={false}
      external={false}
      githubService={githubService}
      editorEnvelopeLocator={editorEnvelopeLocator}
    />,
    document.getElementById("app")!
  );
}

function showResponseError(statusCode: number, description: string) {
  ReactDOM.render(
    <div className={"kogito--alert-container"}>
      <Alert
        variant={AlertVariant.danger}
        title="An error happened while fetching your file"
        actionClose={<AlertActionLink onClick={goToHomePage}>Go to Home Page</AlertActionLink>}
      >
        <br />
        <b>Error details: </b>
        {statusCode}
        {statusCode && description && " - "}
        {description}
      </Alert>
    </div>,
    document.getElementById("app")!
  );
}

function showFetchError(description: string) {
  ReactDOM.render(
    <div className={"kogito--alert-container"}>
      <Alert
        variant={AlertVariant.danger}
        title="An unexpected error happened while trying to fetch your file"
        actionClose={<AlertActionLink onClick={goToHomePage} children={"Go to Home Page"} />}
      >
        <br />
        <b>Error details: </b>
        {description}
        <br />
        <br />
        <b>Possible cause: </b>
        The URL to your file must allow CORS in its response, which should contain the following header:
        <br />
        <pre>Access-Control-Allow-Origin: *</pre>
      </Alert>
    </div>,
    document.getElementById("app")!
  );
}

function goToHomePage() {
  window.location.href = window.location.href.split("?")[0].split("#")[0];
}
