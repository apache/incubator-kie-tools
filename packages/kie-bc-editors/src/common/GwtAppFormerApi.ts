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

import { Rect } from "@kogito-tooling/guided-tour/dist/api";
import { Notification } from "@kogito-tooling/notifications/dist/api";

declare global {
  //Exposed API of AppFormerGwt
  interface Window {
    gwtEditorBeans: Map<string, { get(): GwtEditor }>;
    appFormerGwtFinishedLoading: () => any;
    erraiBusApplicationRoot: string;
    erraiBusRemoteCommunicationEnabled: boolean;
    JsInterop__Envelope__GuidedTour__GuidedTourCustomSelectorPositionProvider: GuidedTourCustomSelectorPositionProvider;
  }
}

export const getGuidedTourElementPosition = (selector: string) => {
  return window.JsInterop__Envelope__GuidedTour__GuidedTourCustomSelectorPositionProvider.getInstance().getPosition(
    selector
  );
};

interface GuidedTourCustomSelectorPositionProvider {
  getPosition(querySelector: string): Rect;
  getInstance(): GuidedTourCustomSelectorPositionProvider;
}

export interface GwtEditor {
  getContent(): Promise<string>;
  setContent(path: string, content: string): Promise<void>;
  getPreview(): Promise<string | undefined>;
  validate(): Promise<Notification[]>;
}

export class GwtAppFormerApi {
  public onFinishedLoading(callback: () => Promise<any>) {
    window.appFormerGwtFinishedLoading = callback;
  }

  public getEditor(editorId: string) {
    const gwtEditor = window.gwtEditorBeans.get(editorId);
    if (!gwtEditor) {
      throw new Error(`GwtEditor with id '${editorId}' was not found`);
    }

    return gwtEditor.get();
  }

  public setClientSideOnly(clientSideOnly: boolean) {
    window.erraiBusRemoteCommunicationEnabled = !clientSideOnly;
  }
}
