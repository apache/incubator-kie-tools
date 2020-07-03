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

export interface KogitoEnvelopeApi {
  //notifications
  receive_contentChangedNotification(content: EditorContent): void;
  receive_editorUndo(): void;
  receive_editorRedo(): void;
  //requests
  receive_initRequest(association: Association): Promise<void>;
  receive_contentRequest(): Promise<EditorContent>;
  receive_previewRequest(): Promise<string>;
  receive_guidedTourElementPositionRequest(selector: string): Promise<Rect>;
}

export interface Association {
  origin: string;
  busId: string;
}

export class KogitoEnvelopeBus {
  public targetOrigin: string;
  public associatedBusId: string;
  public eventListener?: any;
  public readonly manager: EnvelopeBusMessageManager<
    MessageTypesYouCanSendToTheChannel,
    MessageTypesYouCanSendToTheEnvelope,
    KogitoEnvelopeApi
  >;

  constructor(private readonly busApi: EnvelopeBusApi, private readonly api: KogitoEnvelopeApi) {
    this.manager = new EnvelopeBusMessageManager(
      message => this.send(message),
      api,
      new Map([
        [
          MessageTypesYouCanSendToTheEnvelope.REQUEST_GUIDED_TOUR_ELEMENT_POSITION,
          "receive_guidedTourElementPositionRequest"
        ],
        [MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT, "receive_initRequest"],
        [MessageTypesYouCanSendToTheEnvelope.REQUEST_PREVIEW, "receive_previewRequest"],
        [MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT, "receive_contentRequest"],
        [MessageTypesYouCanSendToTheEnvelope.NOTIFY_CONTENT_CHANGED, "receive_contentChangedNotification"],
        [MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_REDO, "receive_editorRedo"],
        [MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_UNDO, "receive_editorUndo"]
      ])
    );
  }

  public associate(association: Association) {
    this.targetOrigin = association.origin;
    this.associatedBusId = association.busId;
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

  public send<T>(
    message: EnvelopeBusMessage<T, MessageTypesYouCanSendToTheChannel | MessageTypesYouCanSendToTheEnvelope>
  ) {
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

  public receive(
    message: EnvelopeBusMessage<any, MessageTypesYouCanSendToTheEnvelope | MessageTypesYouCanSendToTheChannel>
  ) {
    this.manager.receive(message);
  }
}
