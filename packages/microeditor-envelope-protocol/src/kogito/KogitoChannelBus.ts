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
  EditorContent,
  KogitoEdit,
  LanguageData,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { Rect, Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";
import { EnvelopeBus, EnvelopeBusMessage, EnvelopeBusMessageManager } from "../bus";
import { MessageTypesYouCanSendToTheEnvelope } from "./MessageTypesYouCanSendToTheEnvelope";
import { MessageTypesYouCanSendToTheChannel } from "./MessageTypesYouCanSendToTheChannel";

export interface KogitoChannelApi {
  //notification
  receive_setContentError(errorMessage: string): void;
  receive_ready(): void;
  receive_openFile(path: string): void;
  receive_guidedTourUserInteraction(userInteraction: UserInteraction): void;
  receive_guidedTourRegisterTutorial(tutorial: Tutorial): void;
  receive_newEdit(edit: KogitoEdit): void;
  receive_stateControlCommandUpdate(command: StateControlCommand): void;
  //requests
  receive_languageRequest(): Promise<LanguageData | undefined>;
  receive_contentRequest(): Promise<EditorContent>;
  receive_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined>;
  receive_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList>;
}

export class KogitoChannelBus {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 100;

  public readonly manager: EnvelopeBusMessageManager<
    MessageTypesYouCanSendToTheEnvelope,
    MessageTypesYouCanSendToTheChannel,
    KogitoChannelApi
  >;

  public initPolling: any | false;
  public initPollingTimeout: any | false;
  public busId: string;

  public constructor(public bus: EnvelopeBus, api: KogitoChannelApi) {
    this.initPolling = false;
    this.initPollingTimeout = false;
    this.manager = new EnvelopeBusMessageManager(
      message => this.bus.postMessage(message),
      api,
      new Map([
        [MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_LIST, "receive_resourceListRequest"],
        [MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_CONTENT, "receive_resourceContentRequest"],
        [MessageTypesYouCanSendToTheChannel.REQUEST_CONTENT, "receive_contentRequest"],
        [MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE, "receive_languageRequest"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_REGISTER_TUTORIAL, "receive_guidedTourRegisterTutorial"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_USER_INTERACTION, "receive_guidedTourUserInteraction"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_STATE_CONTROL_COMMAND_UPDATE, "receive_stateControlCommandUpdate"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_OPEN_FILE, "receive_openFile"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_NEW_EDIT, "receive_newEdit"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_READY, "receive_ready"],
        [MessageTypesYouCanSendToTheChannel.NOTIFY_SET_CONTENT_ERROR, "receive_setContentError"]
      ])
    );
    this.busId = this.manager.generateRandomId();
  }

  public startInitPolling(origin: string) {
    this.initPolling = setInterval(() => {
      this.request_initResponse(origin).then(() => {
        this.stopInitPolling();
      });
    }, KogitoChannelBus.INIT_POLLING_INTERVAL_IN_MS);

    this.initPollingTimeout = setTimeout(() => {
      this.stopInitPolling();
      console.info("Init polling timed out. Looks like the microeditor-envelope is not responding accordingly.");
    }, KogitoChannelBus.INIT_POLLING_TIMEOUT_IN_MS);
  }

  public stopInitPolling() {
    clearInterval(this.initPolling as number);
    this.initPolling = false;
    clearTimeout(this.initPollingTimeout as number);
    this.initPollingTimeout = false;
  }

  public receive(
    message: EnvelopeBusMessage<any, MessageTypesYouCanSendToTheChannel | MessageTypesYouCanSendToTheEnvelope>
  ) {
    if (message.busId !== this.busId) {
      return;
    }

    this.manager.receive(message);
  }

  //NOTIFICATIONS
  public notify_editorUndo() {
    this.manager.notify(MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_UNDO, undefined);
  }

  public notify_editorRedo() {
    this.manager.notify(MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_REDO, undefined);
  }

  public notify_contentChanged(content: EditorContent) {
    this.manager.notify(MessageTypesYouCanSendToTheEnvelope.NOTIFY_CONTENT_CHANGED, content);
  }

  //REQUEST
  public request_contentResponse() {
    return this.manager.request<EditorContent>(MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT, {});
  }

  public request_previewResponse() {
    return this.manager.request<string>(MessageTypesYouCanSendToTheEnvelope.REQUEST_PREVIEW, {});
  }

  public request_initResponse(origin: string) {
    return this.manager.request<void>(MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT, {
      origin: origin,
      busId: this.busId
    });
  }

  public request_guidedTourElementPositionResponse(selector: string) {
    return this.manager.request<Rect>(
      MessageTypesYouCanSendToTheEnvelope.REQUEST_GUIDED_TOUR_ELEMENT_POSITION,
      selector
    );
  }
}
