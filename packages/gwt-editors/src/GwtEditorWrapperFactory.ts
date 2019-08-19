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

import { GwtAppFormerApi } from "./GwtAppFormerApi";
import * as AppFormer from "appformer-js-core";
import * as MicroEditorEnvelope from "appformer-js-microeditor-envelope";
import { EnvelopeBusInnerMessageHandler } from "appformer-js-microeditor-envelope";
import { GwtEditorWrapper } from "./GwtEditorWrapper";
import { GwtLanguageData, Resource } from "appformer-js-gwt-editors-common";

export class GwtEditorWrapperFactory implements MicroEditorEnvelope.EditorFactory<GwtLanguageData> {
  private readonly appFormerGwtApi: GwtAppFormerApi;

  constructor(appFormerGwtApi: GwtAppFormerApi) {
    this.appFormerGwtApi = appFormerGwtApi;
  }

  private delay(ms: number) {
    return new Promise(res => setTimeout(res, ms));
  }

  public createEditor(languageData: GwtLanguageData, messageBus: EnvelopeBusInnerMessageHandler) {
    return new Promise<AppFormer.Editor>(res => {
      this.appFormerGwtApi.setErraiDomain(languageData.erraiDomain); //needed only for backend communication

      this.appFormerGwtApi.onFinishedLoading(() => {
        res(new GwtEditorWrapper(this.appFormerGwtApi.getEditor(languageData.editorId)));
        return Promise.resolve();
      });

      languageData.resources.forEach(resource => {
        this.loadResource(resource);
      });
    });
  }

  private loadResource(resource: Resource) {
    let i = 0; //  set your counter to 1

    const myLoop = () => {
      setTimeout(() => {
        console.info(resource.paths[i]);
        try {
          switch (resource.type) {
            case "css":
              const link = document.createElement("link");
              link.href = resource.paths[i];
              link.rel = "text/css";
              document.body.appendChild(link);
              break;
            case "js":
              const script = document.createElement("script");
              script.src = resource.paths[i];
              script.type = "text/javascript";
              document.body.appendChild(script);
              break;
          }
        } finally {
          i++;
          if (i < resource.paths[i].length) {
            myLoop(); //  ..  again which will trigger another
          }
        }
      }, 200);
    };

    myLoop();
  }
}
