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
import {
  Editor,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorEnvelopeContextType
} from "@kogito-tooling/editor/dist/api";
import { GwtLanguageData, Resource } from "./GwtLanguageData";
import { XmlFormatter } from "./XmlFormatter";
import { GwtStateControlService } from "./gwtStateControl";
import { DefaultXmlFormatter } from "./DefaultXmlFormatter";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour/dist/api";
import { ResourceContentOptions, ResourceListOptions } from "@kogito-tooling/channel-common-api";
import { GuidedTourApi } from "./api/GuidedTourApi";
import { ResourceContentApi } from "./api/ResourceContentApi";
import { KeyboardShortcutsApi } from "./api/KeyboardShorcutsApi";
import { WorkspaceServiceApi } from "./api/WorkspaceServiceApi";
import { StateControlApi } from "./api/StateControlApi";
import { EditorContextApi } from "./api/EditorContextApi";
import { GwtEditorMapping } from "./GwtEditorMapping";
import { I18nServiceApi } from "./api/I18nServiceApi";
import { kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries } from "./i18n";
import { I18n } from "@kogito-tooling/i18n/dist/core";
import { PMMLEditorMarshallerApi } from "./api/PMMLEditorMarshallerApi";
import { PMMLEditorMarshallerService } from "@kogito-tooling/pmml-editor-marshaller";

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
      i18nService: I18nServiceApi;
      pmmlEditorMarshallerService: PMMLEditorMarshallerApi;
    };
  }
}


export class GwtEditorWrapperFactory implements EditorFactory {
  constructor(
    private readonly args = { shouldLoadResourcesDynamically: true },
    private readonly xmlFormatter: XmlFormatter = new DefaultXmlFormatter(),
    private readonly gwtAppFormerApi = new GwtAppFormerApi(),
    private readonly gwtStateControlService = new GwtStateControlService(),
    private readonly gwtEditorMapping = new GwtEditorMapping(),
    private readonly kieBcEditorsI18n = new I18n(kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries)
  ) {}

  public supports(fileExtension: string) {
    return (
      this.gwtEditorMapping.getLanguageData({
        fileExtension: fileExtension,
        resourcesPathPrefix: "",
        initialLocale: "",
        isReadOnly: false
      }) !== undefined
    );
  }

  public createEditor(envelopeContext: KogitoEditorEnvelopeContextType, initArgs: EditorInitArgs) {
    this.gwtAppFormerApi.setClientSideOnly(true);

    const languageData = this.gwtEditorMapping.getLanguageData(initArgs);
    if (!languageData) {
      throw new Error("Language data does not exist");
    }

    this.kieBcEditorsI18n.setLocale(initArgs.initialLocale);
    envelopeContext.services.i18n.subscribeToLocaleChange(locale => {
      this.kieBcEditorsI18n.setLocale(locale);
      window.alert("This Editor doesn't support changing locales yet.");
    });

    this.appendGwtLocaleMetaTag();
    this.exposeEnvelopeContext(envelopeContext, initArgs);

    const gwtFinishedLoading = new Promise<Editor>(res => {
      this.gwtAppFormerApi.onFinishedLoading(() => {
        res(this.newGwtEditorWrapper(languageData, envelopeContext));
        return Promise.resolve();
      });
    });

    if (!this.args.shouldLoadResourcesDynamically) {
      return gwtFinishedLoading;
    }

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
      this.gwtStateControlService,
      this.kieBcEditorsI18n
    );
  }

  private exposeEnvelopeContext(envelopeContext: KogitoEditorEnvelopeContextType, initArgs: EditorInitArgs) {
    window.gwt = {
      stateControl: this.gwtStateControlService.exposeApi(envelopeContext.channelApi)
    };

    window.envelope = {
      editorContext: {
        operatingSystem: envelopeContext.context.operatingSystem,
        channel: envelopeContext.context.channel,
        readOnly: initArgs.isReadOnly
      },
      keyboardShortcuts: envelopeContext.services.keyboardShortcuts,
      guidedTourService: {
        refresh(userInteraction: UserInteraction): void {
          envelopeContext.channelApi.notifications.receive_guidedTourUserInteraction(userInteraction);
        },
        registerTutorial(tutorial: Tutorial): void {
          envelopeContext.channelApi.notifications.receive_guidedTourRegisterTutorial(tutorial);
        },
        isEnabled(): boolean {
          return envelopeContext.services.guidedTour.isEnabled();
        }
      },
      resourceContentEditorService: {
        get(path: string, opts?: ResourceContentOptions) {
          return envelopeContext.channelApi.requests
            .receive_resourceContentRequest({ path, opts })
            .then(r => r?.content);
        },
        list(pattern: string, opts?: ResourceListOptions) {
          return envelopeContext.channelApi.requests
            .receive_resourceListRequest({ pattern, opts })
            .then(r => r.paths.sort());
        }
      },
      workspaceService: {
        openFile(path: string): void {
          envelopeContext.channelApi.notifications.receive_openFile(path);
        }
      },
      i18nService: {
        getLocale: () => {
          return envelopeContext.channelApi.requests.receive_getLocale();
        },
        onLocaleChange: (onLocaleChange: (locale: string) => void) => {
          envelopeContext.services.i18n.subscribeToLocaleChange(onLocaleChange);
        }
      },
      pmmlEditorMarshallerService: new PMMLEditorMarshallerService()
    };
  }

  private appendGwtLocaleMetaTag() {
    const meta = document.createElement("meta");
    meta.id = "gwt-locale";
    meta.name = "gwt.property";
    meta.content = `locale=${this.kieBcEditorsI18n
      .getLocale()
      .split("-")
      .join("_")}`;
    document.head.appendChild(meta);
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
