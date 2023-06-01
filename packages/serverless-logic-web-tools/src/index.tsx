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
import "@patternfly/quickstarts/dist/quickstarts.min.css";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { App } from "./App";
import "../static/resources/style.css";
import "../static/resources/application-services.css";
import * as incompatibleBrowser from "./workspace/startupBlockers/IncompatibleBrowser";

async function main() {
  const appContainer = document.getElementById("app")!;

  if (await incompatibleBrowser.isTrue()) {
    ReactDOM.render(<incompatibleBrowser.Component />, appContainer);
  } else {
    ReactDOM.render(<App />, appContainer);
  }
}

main();
