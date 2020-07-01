/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import {
  EnvelopeBusApi,
  EnvelopeBusMessage,
  EnvelopeBusMessageType
} from "@kogito-tooling/microeditor-envelope-protocol";
import {
  EditorContent,
  KogitoEdit,
  LanguageData,
  ResourceContent,
  ResourceContentOptions,
  ResourcesList,
  ResourceListOptions,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { UserInteraction, Tutorial, Rect } from "@kogito-tooling/guided-tour";

export interface Impl {
  receive_contentResponse(content: EditorContent): void;
  receive_languageResponse(languageData: LanguageData): void;
  receive_contentRequest(): void;
  receive_resourceContentResponse(content: ResourceContent): void;
  receive_resourceContentList(list: ResourcesList): void;
  receive_editorUndo(): void;
  receive_editorRedo(): void;
  receive_previewRequest(): void;
  receive_guidedTourElementPositionRequest(selector: string): void;
}

export class EnvelopeBusInnerMessageHandler {
  private readonly envelopeBusApi: EnvelopeBusApi;
  private readonly impl: Impl;

  public capturedInitRequestYet = false;
  public targetOrigin: string;
  public id: string;
  public eventListener?: any;

  constructor(busApi: EnvelopeBusApi, impl: (_this: EnvelopeBusInnerMessageHandler) => Impl) {
    this.envelopeBusApi = busApi;
    this.impl = impl(this);
  }

  public startListening() {
    if (this.eventListener) {
      return;
    }

    this.eventListener = (event: any) => this.receive(event.data);
    window.addEventListener("message", this.eventListener);
  }

  public stopListening() {
    window.removeEventListener("message", this.eventListener);
  }

  public send<T>(message: EnvelopeBusMessage<T>) {
    if (!this.targetOrigin) {
      throw new Error("Tried to send message without targetOrigin set");
    }
    this.envelopeBusApi.postMessage({ ...message, busId: this.id }, this.targetOrigin);
  }

  public respond_initRequest() {
    return this.send({ type: EnvelopeBusMessageType.RETURN_INIT, data: undefined });
  }

  public respond_contentRequest(content: EditorContent) {
    return this.send({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: content });
  }

  public request_languageResponse() {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined });
  }

  public request_contentResponse() {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });
  }

  public notify_setContentError(errorMessage: string) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR, data: errorMessage });
  }

  public notify_dirtyIndicatorChange(isDirty: boolean) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE, data: isDirty });
  }

  public notify_ready() {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_READY, data: undefined });
  }

  public request_resourceContent(path: string, opts?: ResourceContentOptions) {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT, data: { path: path, opts: opts } });
  }

  public request_resourceList(pattern: string, opts?: ResourceListOptions) {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_LIST, data: { pattern: pattern, opts: opts } });
  }

  public notify_newEdit(edit: KogitoEdit) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_NEW_EDIT, data: edit });
  }

  public notify_openFile(path: string) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_OPEN_FILE, data: path });
  }

  public notify_guidedTourRefresh(userInteraction: UserInteraction) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_GUIDED_TOUR_USER_INTERACTION, data: userInteraction });
  }

  public notify_guidedTourRegisterTutorial(tutorial: Tutorial) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_GUIDED_TOUR_REGISTER_TUTORIAL, data: tutorial });
  }

  public respond_guidedTourElementPositionRequest(rect: Rect) {
    return this.send({ type: EnvelopeBusMessageType.RETURN_GUIDED_TOUR_ELEMENT_POSITION, data: rect });
  }

  public respond_previewRequest(previewSvg: string) {
    return this.send({ type: EnvelopeBusMessageType.RETURN_PREVIEW, data: previewSvg });
  }
  public notify_stateControlCommandUpdate(stateControlCommand: StateControlCommand) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_STATE_CONTROL_COMMAND_UPDATE, data: stateControlCommand });
  }

  private receive_initRequest(init: { origin: string; busId: string }) {
    this.targetOrigin = init.origin;
    this.id = init.busId;

    this.respond_initRequest();

    if (this.capturedInitRequestYet) {
      return;
    }

    this.capturedInitRequestYet = true;
    this.request_languageResponse();
  }

  public receive(message: EnvelopeBusMessage<any>) {
    switch (message.type) {
      case EnvelopeBusMessageType.REQUEST_INIT:
        const origin = message.data as string;
        this.receive_initRequest({ origin: origin, busId: message.busId as string });
        break;
      case EnvelopeBusMessageType.RETURN_LANGUAGE:
        this.impl.receive_languageResponse(message.data as LanguageData);
        break;
      case EnvelopeBusMessageType.RETURN_CONTENT:
        this.impl.receive_contentResponse(message.data as EditorContent);
        break;
      case EnvelopeBusMessageType.REQUEST_CONTENT:
        this.impl.receive_contentRequest();
        break;
      case EnvelopeBusMessageType.RETURN_RESOURCE_CONTENT:
        const resourceContent = message.data as ResourceContent;
        this.impl.receive_resourceContentResponse(resourceContent);
        break;
      case EnvelopeBusMessageType.RETURN_RESOURCE_LIST:
        const resourcesList = message.data as ResourcesList;
        this.impl.receive_resourceContentList(resourcesList);
        break;
      case EnvelopeBusMessageType.NOTIFY_EDITOR_UNDO:
        this.impl.receive_editorUndo();
        break;
      case EnvelopeBusMessageType.NOTIFY_EDITOR_REDO:
        this.impl.receive_editorRedo();
        break;
      case EnvelopeBusMessageType.REQUEST_PREVIEW:
        this.impl.receive_previewRequest();
        break;
      case EnvelopeBusMessageType.REQUEST_GUIDED_TOUR_ELEMENT_POSITION:
        const selector = message.data as Rect;
        this.impl.receive_guidedTourElementPositionRequest(selector);
        break;
      default:
        console.info(`[Bus ${this.id}]: Unknown message type received: ${message.type}`);
        break;
    }
  }
}
