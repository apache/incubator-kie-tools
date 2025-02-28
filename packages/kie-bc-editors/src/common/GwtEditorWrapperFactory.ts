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
  KogitoEditorEnvelopeApi,
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

export class GwtEditorWrapperFactory<E extends GwtEditorWrapper>
  implements EditorFactory<E, KogitoEditorEnvelopeApi, KogitoEditorChannelApi>
{
  constructor(
    private readonly languageData: GwtLanguageData,
    private readonly gwtEditorDelegate: (factory: GwtEditorWrapperFactory<E>, initArgs: EditorInitArgs) => E,
    public readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean },
    public readonly textFormatter: TextFormatter = new DefaultTextFormatter(),
    public readonly gwtAppFormerConsumedInteropApi = new GwtAppFormerConsumedInteropApi(),
    public readonly gwtStateControlService = new GwtStateControlService(),
    public readonly kieBcEditorsI18n = new I18n(kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries)
  ) {}

  public gwtEditor: E;

  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>,
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
        this.gwtEditor = this.gwtEditorDelegate(this, initArgs);
        res(this.gwtEditor);
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
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>,
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
        // Everything coming from or going into the Editors must be relative to the open file.
        get: (normalizedPosixPathRelativeToTheOpenFile: string, opts?: ResourceContentOptions) => {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = __path
            .resolve(
              __path.dirname(this.gwtEditor.normalizedPosixPathRelativeToTheWorkspaceRoot),
              normalizedPosixPathRelativeToTheOpenFile
            )
            .substring(1); // Remove leading slash. __path.resolve always adds it.

          return (
            envelopeContext.channelApi.requests
              // Everything going to the channel must be relative to the workpsace root.
              .kogitoWorkspace_resourceContentRequest({ normalizedPosixPathRelativeToTheWorkspaceRoot, opts })
              .then((r) => r?.content)
          );
        },
        list: (pattern: string, opts?: ResourceListOptions) => {
          return envelopeContext.channelApi.requests.kogitoWorkspace_resourceListRequest({ pattern, opts }).then((r) =>
            r.normalizedPosixPathsRelativeToTheWorkspaceRoot
              .map((p) => {
                const normalizedPosixPathRelativeToTheOpenFile = __path.relative(
                  __path.dirname(this.gwtEditor.normalizedPosixPathRelativeToTheWorkspaceRoot),
                  p
                );

                // Everything coming from or going into the Editors must be relative to the open file.
                return normalizedPosixPathRelativeToTheOpenFile;
              })
              .sort()
          );
        },
      },
      workspaceService: {
        // Everything coming from or going into the Editors must be relative to the open file.
        openFile: (normalizedPosixPathRelativeToTheOpenFile: string): void => {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = __path
            .resolve(
              __path.dirname(this.gwtEditor.normalizedPosixPathRelativeToTheWorkspaceRoot),
              normalizedPosixPathRelativeToTheOpenFile
            )
            .substring(1); // Remove leading slash. __path.resolve always adds it.

          envelopeContext.channelApi.notifications.kogitoWorkspace_openFile.send(
            normalizedPosixPathRelativeToTheWorkspaceRoot // Everything going to the channel must be relative to the workpsace root.
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
          // This is wrong, because Notification has a property called `normalizedPosixPathRelativeToTheWorkspaceRoot`.
          // The Editor should be sending a Notification object with a `normalizedPosixPathRelativeToTheOpenFile` instead.
          // The Editor should've not been using Envelope APIs directly.
          envelopeContext.channelApi.notifications.kogitoNotifications_createNotification.send(notification);
        },
        // Everything coming from or going into the Editors must be relative to the open file.
        removeNotifications: (normalizedPosixPathRelativeToTheOpenFile: string) => {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = __path
            .resolve(
              __path.dirname(this.gwtEditor.normalizedPosixPathRelativeToTheWorkspaceRoot),
              normalizedPosixPathRelativeToTheOpenFile
            )
            .substring(1); // Remove leading slash. __path.resolve always adds it.

          envelopeContext.channelApi.notifications.kogitoNotifications_removeNotifications.send(
            normalizedPosixPathRelativeToTheWorkspaceRoot // Everything going to the channel must be relative to the workpsace root.
          );
        },

        // Everything coming from or going into the Editors must be relative to the open file.
        setNotifications: (normalizedPosixPathRelativeToTheOpenFile: string, notifications: Notification[]) => {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = __path
            .resolve(
              __path.dirname(this.gwtEditor.normalizedPosixPathRelativeToTheWorkspaceRoot),
              normalizedPosixPathRelativeToTheOpenFile
            )
            .substring(1); // Remove leading slash. __path.resolve always adds it.

          envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications.send(
            normalizedPosixPathRelativeToTheWorkspaceRoot, // Everything going to the channel must be relative to the workpsace root.
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
