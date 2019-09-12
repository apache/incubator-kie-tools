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

import { AppFormerGwtApi } from "./AppFormerGwtApi";
import * as AppFormer from "@kogito-tooling/core-api";
import * as MicroEditorEnvelope from "@kogito-tooling/microeditor-envelope";
import { GwtEditorWrapper } from "./GwtEditorWrapper";
import { Resource, KogitoLanguageData } from "../../common/KogitoLanguageData";
import {EnvelopeBusInnerMessageHandler} from "@kogito-tooling/microeditor-envelope";

export class GwtEditorWrapperFactory implements MicroEditorEnvelope.EditorFactory<KogitoLanguageData> {
  private readonly appFormerGwtApi: AppFormerGwtApi;

  constructor(appFormerGwtApi: AppFormerGwtApi) {
    this.appFormerGwtApi = appFormerGwtApi;
  }

  public createEditor(languageData: KogitoLanguageData, messageBus: EnvelopeBusInnerMessageHandler) {
    return new Promise<AppFormer.Editor>(res => {
      this.appFormerGwtApi.setErraiDomain(languageData.erraiDomain); //needed only for backend communication

      this.appFormerGwtApi.onFinishedLoading(() => {
        res(new GwtEditorWrapper(this.appFormerGwtApi.getEditor(languageData.editorId), messageBus));
        return Promise.resolve();
      });

      languageData.resources.forEach(resource => {
        this.loadResource(resource);
      });
    });
  }

  private loadResource(resource: Resource) {
    resource.paths.forEach(path => {
      switch (resource.type) {
        case "css":
          const link = document.createElement("link");
          link.href = path;
          link.rel = "text/css";
          document.body.appendChild(link);
          break;
        case "js":
          const script = document.createElement("script");
          script.src = path;
          script.type = "text/javascript";
          document.body.appendChild(script);
          break;
      }
    });
  }
}
