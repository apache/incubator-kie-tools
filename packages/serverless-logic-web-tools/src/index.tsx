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
import * as React from "react";
import { App } from "./App";
import "../static/resources/style.css";
import * as incompatibleBrowser from "./workspace/startupBlockers/IncompatibleBrowser";
import { createRoot } from "react-dom/client";

async function main() {
  const appContainer = document.getElementById("app")!;

  const Component = (await incompatibleBrowser.isTrue()) ? <incompatibleBrowser.Component /> : <App />;

  const root = createRoot(appContainer);

  root.render(Component);
}

main();
