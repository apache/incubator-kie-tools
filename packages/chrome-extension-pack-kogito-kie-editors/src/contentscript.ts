/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { ChromeRouter } from "./ChromeRouter";
import { GwtEditorRoutes } from "@kogito-tooling/gwt-editors";
import { startExtension } from "@kogito-tooling/chrome-extension";

startExtension({
  name: "Kogito :: BPMN and DMN editors",
  editorIndexPath: "envelope/index.html",
  router: new ChromeRouter(new GwtEditorRoutes({ bpmnPath: "bpmn", dmnPath: "dmn" }))
});
