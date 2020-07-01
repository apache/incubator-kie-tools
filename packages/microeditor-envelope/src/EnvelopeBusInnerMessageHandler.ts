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
  EnvelopeBusMessageManager,
  EnvelopeBusMessagePurpose,
  MessageTypesYouCanSendToTheChannel,
  MessageTypesYouCanSendToTheEnvelope
} from "@kogito-tooling/microeditor-envelope-protocol";
import {
  EditorContent,
  KogitoEdit,
  LanguageData,
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { Rect, Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";

export interface Impl {
  receive_contentChangedNotification(content: EditorContent): void;
  receive_editorUndo(): void;
  receive_editorRedo(): void;

  receive_initRequest(init: { origin: string; busId: string }): Promise<void>;
  receive_contentRequest(): Promise<EditorContent>;
  receive_previewRequest(): Promise<string>;
  receive_guidedTourElementPositionRequest(selector: string): Promise<Rect>;
}

export class EnvelopeBusInnerMessageHandler {
  public targetOrigin: string;
  public associatedBusId: string;
  public eventListener?: any;
  private readonly manager: EnvelopeBusMessageManager<MessageTypesYouCanSendToTheChannel>;

  constructor(private readonly busApi: EnvelopeBusApi, private readonly impl: Impl) {
    this.manager = new EnvelopeBusMessageManager<MessageTypesYouCanSendToTheChannel>(m => this.send(m));
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
    this.busApi.postMessage({ ...message, busId: this.associatedBusId }, this.targetOrigin);
  }

  public notify_setContentError(errorMessage: string) {
    return this.manager.notify(MessageTypesYouCanSendToTheChannel.NOTIFY_SET_CONTENT_ERROR, errorMessage);
  }

  public notify_ready() {
    return this.manager.notify(MessageTypesYouCanSendToTheChannel.NOTIFY_READY, undefined);
  }

  public notify_newEdit(edit: KogitoEdit) {
    return this.manager.notify(MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_NEW_EDIT, edit);
  }

  public notify_openFile(path: string) {
    return this.manager.notify(MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_OPEN_FILE, path);
  }

  public notify_guidedTourRefresh(userInteraction: UserInteraction) {
    return this.manager.notify(MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_USER_INTERACTION, userInteraction);
  }

  public notify_guidedTourRegisterTutorial(tutorial: Tutorial) {
    return this.manager.notify(MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_REGISTER_TUTORIAL, tutorial);
  }

  public notify_stateControlCommandUpdate(stateControlCommand: StateControlCommand) {
    return this.manager.notify(
      MessageTypesYouCanSendToTheChannel.NOTIFY_STATE_CONTROL_COMMAND_UPDATE,
      stateControlCommand
    );
  }

  public request_languageResponse() {
    return this.manager.request<LanguageData>(MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE, undefined);
  }

  public request_contentResponse() {
    return this.manager.request<EditorContent>(MessageTypesYouCanSendToTheChannel.REQUEST_CONTENT, undefined);
  }

  public request_resourceContent(path: string, opts?: ResourceContentOptions) {
    return this.manager.request<ResourceContent | undefined>(
      MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_CONTENT,
      {
        path: path,
        opts: opts
      }
    );
  }

  public request_resourceList(pattern: string, opts?: ResourceListOptions) {
    return this.manager.request<ResourcesList>(MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_LIST, {
      pattern: pattern,
      opts: opts
    });
  }

  public receive(message: EnvelopeBusMessage<any>) {
    if (message.purpose === EnvelopeBusMessagePurpose.RESPONSE) {
      this.manager.callback(message);
      return;
    }

    switch (message.type) {
      //NOTIFICATIONS
      case MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_UNDO:
        this.impl.receive_editorUndo();
        break;
      case MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_REDO:
        this.impl.receive_editorRedo();
        break;
      case MessageTypesYouCanSendToTheEnvelope.NOTIFY_CONTENT_CHANGED:
        this.impl.receive_contentChangedNotification(message.data as EditorContent);
        break;
      //REQUESTS
      case MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT:
        const init = message.data as { origin: string; busId: string };
        this.impl.receive_initRequest(init).then(() => this.manager.respond(message, {}));
        break;
      case MessageTypesYouCanSendToTheEnvelope.REQUEST_PREVIEW:
        this.impl.receive_previewRequest().then(previewSvg => this.manager.respond(message, previewSvg));
        break;
      case MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT:
        this.impl.receive_contentRequest().then(content => this.manager.respond(message, content));
        break;
      case MessageTypesYouCanSendToTheEnvelope.REQUEST_GUIDED_TOUR_ELEMENT_POSITION:
        const selector = message.data as string;
        this.impl
          .receive_guidedTourElementPositionRequest(selector)
          .then(position => this.manager.respond(message, position));
        break;
      default:
        console.info(`[Bus ${this.associatedBusId}]: Unknown message type received: ${message.type}`);
        break;
    }
  }
}
