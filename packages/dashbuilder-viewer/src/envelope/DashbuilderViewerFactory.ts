/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DashbuilderViewerView } from "./DashbuilderViewerView";
import {
  Editor,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { DashbuilderViewerChannelApi } from "./DashbuilderViewerChannelApi";
import { getDashbuilderLanguageData, Resource } from "../api/DashbuilderLanguageData";

export class DashbuilderViewerFactory
  implements EditorFactory<Editor, KogitoEditorEnvelopeApi, DashbuilderViewerChannelApi>
{
  public async createEditor(
    ctx: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, DashbuilderViewerChannelApi>,
    initArgs: EditorInitArgs
  ) {
    appendLoaderContainer();
    const langData = getDashbuilderLanguageData(initArgs.resourcesPathPrefix);
    langData.resources.forEach((resource) => loadResource(resource));
    return new DashbuilderViewerView(ctx, initArgs);
  }
}

const appendLoaderContainer = () => {
  const loaderContainer = document.createElement("div");
  const loaderDiv = document.createElement("div");
  loaderContainer.id = "loading";
  loaderContainer.className = "loader_container";
  loaderDiv.className = "db_loader";
  loaderContainer.appendChild(loaderDiv);
  document.body.appendChild(loaderContainer);
};

const loadResource = (resource: Resource) => {
  switch (resource.type) {
    case "css":
      for (const sheet of resource.paths) {
        const link = document.createElement("link");
        link.href = sheet;
        link.rel = "text/css";
        document.head.appendChild(link);
      }
      return Promise.resolve();
    case "js":
      return recursivelyLoadScriptsStartingFrom(resource.paths, 0);
  }
};

const recursivelyLoadScriptsStartingFrom = (urls: string[], i: number) => {
  if (i >= urls.length) {
    return Promise.resolve();
  }

  return new Promise<void>((res) => {
    const script = document.createElement("script");
    script.type = "text/javascript";
    script.async = true;
    script.src = urls[i];
    script.addEventListener("load", () => recursivelyLoadScriptsStartingFrom(urls, i + 1).then(res), false);
    document.head.appendChild(script);
  });
};
