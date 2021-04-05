/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { editors, GwtLanguageData } from "../../common";

export function getSceSimLanguageData(resourcesPathPrefix: string): GwtLanguageData {
  return {
    type: "gwt",
    editorId: editors.scesim.id,
    gwtModuleName: editors.scesim.name,
    resources: [
      {
        type: "css",
        paths: [`${resourcesPathPrefix}/${editors.scesim.name}/css/patternfly.min.css`]
      },
      {
        type: "js",
        paths: [
          `${resourcesPathPrefix}/model/Jsonix-all.js`,
          `${resourcesPathPrefix}/model/DC.js`,
          `${resourcesPathPrefix}/model/DI.js`,
          `${resourcesPathPrefix}/model/DMNDI12.js`,
          `${resourcesPathPrefix}/model/DMN12.js`,
          `${resourcesPathPrefix}/model/KIE.js`,
          `${resourcesPathPrefix}/model/MainJs.js`,
          `${resourcesPathPrefix}/model/SCESIM.js`,
          `${resourcesPathPrefix}/model/SCESIMMainJs.js`,
          `${resourcesPathPrefix}/${editors.scesim.name}/${editors.scesim.name}.nocache.js`
        ]
      }
    ]
  };
}
