/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as _ from "underscore";
import * as fs from "fs";
import { BaseEditorResources } from "../common/EditorResources";
import { ServerlessworkflowDiagramEditorResources } from "../swf/resources/SwfDiagramEditorResources";
import { ServerlessworkflowMermaidViewerResources } from "../swf/resources/SwfMermaidViewerResources";
import { ServerlessworkflowTextEditorResources } from "../swf/resources/SwfTextEditorResources";
import { ServerlessworkflowCombinedEditorResources } from "../swf/resources/SwfCombinedEditorResources";

function main() {
  const editorsResources: BaseEditorResources[] = [
    new ServerlessworkflowCombinedEditorResources(),
    new ServerlessworkflowDiagramEditorResources(),
    new ServerlessworkflowMermaidViewerResources(),
    new ServerlessworkflowTextEditorResources(),
  ];

  editorsResources.forEach((editorResources) => {
    const template = _.template(fs.readFileSync(editorResources.getTemplatePath()).toString());
    const result = template({
      editorResources: editorResources.get({
        resourcesPathPrefix: editorResources.getEditorResourcesPath(),
      }),
    });

    fs.writeFileSync(editorResources.getHtmlOutputPath(), result);
  });
}

main();
