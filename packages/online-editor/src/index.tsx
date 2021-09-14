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

import "@patternfly/react-core/dist/styles/base.css";
import "@patternfly/patternfly/patternfly-addons.scss";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { App } from "./App";
import { extractFileExtension, removeFileExtension } from "./common/utils";
import "../static/resources/style.css";
import { QueryParams } from "./queryParams/QueryParamsContext";

function main() {
  const queryParams = new URLSearchParams(window.location.search);
  if (!queryParams.has(QueryParams.EXT)) {
    ReactDOM.render(<App />, document.getElementById("app")!);
    return;
  }

  window.addEventListener("loadOnlineEditor", (e: CustomEvent) => {
    const externalFile = {
      isReadOnly: false,
      fileExtension: extractFileExtension(e.detail.fileName)!,
      fileName: removeFileExtension(e.detail.fileName),
      getFileContents: () => Promise.resolve(e.detail.fileContent),
    };

    ReactDOM.render(
      <App externalFile={externalFile} senderTabId={e.detail.senderTabId} />,
      document.getElementById("app")!
    );
  });
}

main();
