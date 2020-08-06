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
import { GwtEditorWrapper } from "./GwtEditorWrapper";
import { Editor, EditorFactory, KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor-api";
import { GwtLanguageData, Resource } from "./GwtLanguageData";
import { XmlFormatter } from "./XmlFormatter";
import { GwtStateControlService } from "./gwtStateControl";
import { DefaultXmlFormatter } from "./DefaultXmlFormatter";
import {
  EditorInitArgs,
  ResourceContentOptions,
  ResourceListOptions,
  Tutorial,
  UserInteraction
} from "@kogito-tooling/microeditor-envelope-protocol";
import { GuidedTourApi } from "./api/GuidedTourApi";
import { ResourceContentApi } from "./api/ResourceContentApi";
import { KeyboardShortcutsApi } from "./api/KeyboardShorcutsApi";
import { WorkspaceServiceApi } from "./api/WorkspaceServiceApi";
import { StateControlApi } from "./api/StateControlApi";
import { EditorContextApi } from "./api/EditorContextApi";
import { PMMLMarshallerApi } from "./api/PMMLMarshallerApi";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller/dist/marshaller/model/pmml4_4";
import { PMML2XML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { GwtEditorMapping } from "./GwtEditorMapping";

declare global {
  interface Window {
    gwt: {
      stateControl: StateControlApi;
    };
    envelope: {
      guidedTourService: GuidedTourApi;
      editorContext: EditorContextApi;
      resourceContentEditorService?: ResourceContentApi;
      keyboardShortcuts: KeyboardShortcutsApi;
      workspaceService: WorkspaceServiceApi;
      pmmlMarshallerService: PMMLMarshallerApi;
    };
  }
}

export class GwtEditorWrapperFactory implements EditorFactory {
  constructor(
    private readonly xmlFormatter: XmlFormatter = new DefaultXmlFormatter(),
    private readonly gwtAppFormerApi = new GwtAppFormerApi(),
    private readonly gwtStateControlService = new GwtStateControlService(),
    private readonly gwtEditorMapping = new GwtEditorMapping()
  ) {}

  public supports(fileExtension: string) {
    return (
      this.gwtEditorMapping.getLanguageData({ fileExtension: fileExtension, resourcesPathPrefix: "" }) !== undefined
    );
  }

  public createEditor(envelopeContext: KogitoEditorEnvelopeContextType, initArgs: EditorInitArgs) {
    this.gwtAppFormerApi.setClientSideOnly(true);

    const languageData = this.gwtEditorMapping.getLanguageData(initArgs);
    if (!languageData) {
      throw new Error("Language data does not exist");
    }

    this.exposeEnvelopeContext(envelopeContext);

    const gwtFinishedLoading = new Promise<Editor>(res => {
      this.gwtAppFormerApi.onFinishedLoading(() => {
        res(this.newGwtEditorWrapper(languageData, envelopeContext));
        return Promise.resolve();
      });
    });

    return Promise.all(languageData.resources.map(resource => this.loadResource(resource))).then(() => {
      return gwtFinishedLoading;
    });
  }

  private newGwtEditorWrapper(languageData: GwtLanguageData, envelopeContext: KogitoEditorEnvelopeContextType) {
    return new GwtEditorWrapper(
      languageData.editorId,
      this.gwtAppFormerApi.getEditor(languageData.editorId),
      envelopeContext.channelApi,
      this.xmlFormatter,
      this.gwtStateControlService
    );
  }

  private exposeEnvelopeContext(envelopeContext: KogitoEditorEnvelopeContextType) {
    window.gwt = {
      stateControl: this.gwtStateControlService.exposeApi(envelopeContext.channelApi)
    };

    window.envelope = {
      editorContext: envelopeContext.context,
      keyboardShortcuts: envelopeContext.services.keyboardShortcuts,
      guidedTourService: {
        refresh(userInteraction: UserInteraction): void {
          envelopeContext.channelApi.notify("receive_guidedTourUserInteraction", userInteraction);
        },
        registerTutorial(tutorial: Tutorial): void {
          envelopeContext.channelApi.notify("receive_guidedTourRegisterTutorial", tutorial);
        },
        isEnabled(): boolean {
          return envelopeContext.services.guidedTour.isEnabled();
        }
      },
      resourceContentEditorService: {
        get(path: string, opts?: ResourceContentOptions) {
          return envelopeContext.channelApi
            .request("receive_resourceContentRequest", { path, opts })
            .then(r => r?.content);
        },
        list(pattern: string, opts?: ResourceListOptions) {
          return envelopeContext.channelApi
            .request("receive_resourceListRequest", { pattern, opts })
            .then(r => r.paths.sort());
        }
      },
      workspaceService: {
        openFile(path: string): void {
          envelopeContext.channelApi.notify("receive_openFile", path);
        }
      },
      pmmlMarshallerService: {
        marshall(xmlContent: string) {
          return XML2PMML(xmlContent);
        },
        unmarshall(pmml: PMML) {
          return PMML2XML(pmml);
        }
      }
    };
  }

  private loadResource(resource: Resource) {
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
        return this.recursivelyLoadScriptsStartingFrom(resource.paths, 0);
    }
  }

  private recursivelyLoadScriptsStartingFrom(urls: string[], i: number) {
    if (i >= urls.length) {
      return Promise.resolve();
    }

    return new Promise<void>(res => {
      const script = document.createElement("script");
      script.type = "text/javascript";
      script.async = true;
      script.src = urls[i];
      script.addEventListener("load", () => this.recursivelyLoadScriptsStartingFrom(urls, i + 1).then(res), false);
      document.head.appendChild(script);
    });
  }
}
