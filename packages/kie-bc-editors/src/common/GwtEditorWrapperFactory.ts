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

import { Notification } from "@kie-tools-core/notifications/dist/api";
import { ResourceContentOptions, ResourceListOptions } from "@kie-tools-core/workspace/dist/api";
import {
  EditorFactory,
  EditorInitArgs,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import {
  EditorContextExposedInteropApi,
  I18nExposedInteropApi,
  KeyboardShortcutsExposedInteropApi,
  NotificationsExposedInteropApi,
  ResourceContentExposedInteropApi,
  StateControlExposedInteropApi,
  WorkspaceExposedInteropApi,
} from "./exposedInteropApi";
import { DefaultTextFormatter, TextFormatter } from "./TextFormatter";
import { GwtAppFormerConsumedInteropApi } from "./consumedInteropApi/GwtAppFormerConsumedInteropApi";
import { GwtEditorWrapper } from "./GwtEditorWrapper";
import { GwtLanguageData, Resource } from "./GwtLanguageData";
import { GwtStateControlService } from "./gwtStateControl";
import { kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries } from "./i18n";
import * as __path from "path";

export interface CustomWindow extends Window {
  startStandaloneEditor?: () => void;
  gwt: {
    stateControlService: StateControlExposedInteropApi;
  };
  envelope: {
    editorContext: EditorContextExposedInteropApi;
    resourceContentEditorService?: ResourceContentExposedInteropApi;
    keyboardShortcutsService: KeyboardShortcutsExposedInteropApi;
    workspaceService: WorkspaceExposedInteropApi;
    i18nService: I18nExposedInteropApi;
    notificationsService: NotificationsExposedInteropApi;
  };
}

declare let window: CustomWindow;

export class GwtEditorWrapperFactory<E extends GwtEditorWrapper> implements EditorFactory<E, KogitoEditorChannelApi> {
  constructor(
    private readonly languageData: GwtLanguageData,
    private readonly gwtEditorDelegate: (factory: GwtEditorWrapperFactory<E>, initArgs: EditorInitArgs) => E,
    public readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean },
    public readonly textFormatter: TextFormatter = new DefaultTextFormatter(),
    public readonly gwtAppFormerConsumedInteropApi = new GwtAppFormerConsumedInteropApi(),
    public readonly gwtStateControlService = new GwtStateControlService(),
    public readonly kieBcEditorsI18n = new I18n(kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries)
  ) {}

  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    this.kieBcEditorsI18n.setLocale(initArgs.initialLocale);
    envelopeContext.services.i18n.subscribeToLocaleChange((locale) => {
      this.kieBcEditorsI18n.setLocale(locale);
      window.alert("This Editor doesn't support changing locales yet.");
    });

    this.appendGwtLocaleMetaTag();
    this.exposeEnvelopeContext(envelopeContext, initArgs);

    const gwtFinishedLoading = new Promise<E>((res) => {
      this.gwtAppFormerConsumedInteropApi.onFinishedLoading(() => {
        res(this.gwtEditorDelegate(this, initArgs));
        return Promise.resolve();
      });
    });

    if (!this.gwtEditorEnvelopeConfig.shouldLoadResourcesDynamically) {
      window.startStandaloneEditor?.();
      return gwtFinishedLoading;
    }

    return Promise.all(this.languageData.resources.map((resource) => this.loadResource(resource))).then(() => {
      return gwtFinishedLoading;
    });
  }

  private exposeEnvelopeContext(
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    window.gwt = {
      stateControlService: this.gwtStateControlService.getExposedInteropApi(envelopeContext.channelApi),
    };

    const exposedInteropApi: CustomWindow["envelope"] = {
      editorContext: {
        operatingSystem: envelopeContext.operatingSystem,
        channel: initArgs.channel,
        readOnly: initArgs.isReadOnly,
      },
      keyboardShortcutsService: envelopeContext.services.keyboardShortcuts,
      resourceContentEditorService: {
        get(pathRelativeToTheWorkspaceFile: string, opts?: ResourceContentOptions) {
          return envelopeContext.channelApi.requests
            .kogitoWorkspace_resourceContentRequest({
              pathRelativeToTheWorkspaceRoot: pathRelativeToTheWorkspaceFile, // FIXME: TIAGO/LUIZ: Fix this :)
              opts,
            })
            .then((r) => r?.content);
        },
        list(pattern: string, opts?: ResourceListOptions) {
          return envelopeContext.channelApi.requests
            .kogitoWorkspace_resourceListRequest({ pattern, opts })
            .then((r) => r.pathsRelativeToTheWorkspaceRoot.sort());
        },
      },
      workspaceService: {
        openFile(pathRelativeToTheOpenFile: string): void {
          const openFilePathRelativeToTheWorkspaceRoot = ""; // FIXME: TIAGO/LUIZ: Fix this :)

          const resolvedPathRelativeToTheWorkspaceRoot = __path
            .resolve(__path.dirname(openFilePathRelativeToTheWorkspaceRoot!), pathRelativeToTheOpenFile)
            .substring(1); // Remove leading slash.

          envelopeContext.channelApi.notifications.kogitoWorkspace_openFile.send(
            resolvedPathRelativeToTheWorkspaceRoot
          );
        },
      },
      i18nService: {
        getLocale: () => {
          return envelopeContext.channelApi.requests.kogitoI18n_getLocale();
        },
        onLocaleChange: (onLocaleChange: (locale: string) => void) => {
          envelopeContext.services.i18n.subscribeToLocaleChange(onLocaleChange);
        },
      },
      notificationsService: {
        createNotification: (notification: Notification) => {
          envelopeContext.channelApi.notifications.kogitoNotifications_createNotification.send(notification);
        },
        removeNotifications: (pathRelativeToTheWorkspaceFile: string) => {
          envelopeContext.channelApi.notifications.kogitoNotifications_removeNotifications.send(
            pathRelativeToTheWorkspaceFile
          );
        },
        setNotifications: (pathRelativeToTheWorkspaceFile: string, notifications: Notification[]) => {
          envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications.send(
            pathRelativeToTheWorkspaceFile,
            notifications
          );
        },
      },
    };

    window.envelope = {
      ...(window.envelope ?? {}),
      ...exposedInteropApi,
    };
  }

  private appendGwtLocaleMetaTag() {
    const meta = document.createElement("meta");
    meta.id = "gwt-locale";
    meta.name = "gwt.property";
    meta.content = `locale=${this.kieBcEditorsI18n.getLocale().split("-").join("_")}`;
    document.head.appendChild(meta);
  }

  private loadResource(resource: Resource) {
    switch (resource.type) {
      case "css":
        for (const sheet of resource.paths) {
          const link = document.createElement("link");
          link.href = sheet;
          link.rel = resource.rel ?? "text/css";
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

    return new Promise<void>((res) => {
      const script = document.createElement("script");
      script.type = "text/javascript";
      script.async = true;
      script.src = urls[i];
      script.addEventListener("load", () => this.recursivelyLoadScriptsStartingFrom(urls, i + 1).then(res), false);
      document.head.appendChild(script);
    });
  }
}
