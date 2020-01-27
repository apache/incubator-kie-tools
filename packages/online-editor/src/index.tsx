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
import { EMPTY_FILE } from "./common/File";
import { removeDirectories, removeFileExtension } from "./common/utils";
import { Alert, AlertActionLink, AlertVariant } from "@patternfly/react-core";

const urlParams = new URLSearchParams(window.location.search);

if (urlParams.has("ext")) {
  waitForEventWithFileData();
} else if (urlParams.has("file")) {
  openFileByUrl();
} else {
  openDefaultOnlineEditor();
}

function openDefaultOnlineEditor() {
  ReactDOM.render(
    <App iframeTemplateRelativePath={"envelope/index.html"} file={EMPTY_FILE} readonly={false} external={false} />,
    document.getElementById("app")!
  );
}

function waitForEventWithFileData() {
  window.addEventListener("loadOnlineEditor", (e: CustomEvent) => {
    const file = { fileName: e.detail.fileName, getFileContents: () => Promise.resolve(e.detail.fileContent) };
    ReactDOM.render(
      <App
        iframeTemplateRelativePath={"envelope/index.html"}
        file={file}
        readonly={e.detail.readonly}
        external={true}
        senderTabId={e.detail.senderTabId}
      />,
      document.getElementById("app")!
    );
  });
}

function openFileByUrl() {
  const filePath = urlParams.get("file")!;

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

function openFile(filePath: string, getFileContent: Promise<string>) {
  const file = {
    fileName: removeFileExtension(removeDirectories(filePath)!)!,
    getFileContents: () => getFileContent
  };
  ReactDOM.render(
    <App iframeTemplateRelativePath={"envelope/index.html"} file={file} readonly={false} external={false} />,
    document.getElementById("app")!
  );
}

function showResponseError(statusCode: number, description: string) {
  ReactDOM.render(
    <div className={"kogito--alert-container"}>
      <Alert
        variant={AlertVariant.danger}
        title="An error happened while fetching your file"
        action={<AlertActionLink onClick={goToHomePage}>Go to Home Page</AlertActionLink>}
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
        action={<AlertActionLink onClick={goToHomePage} children={"Go to Home Page"} />}
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
