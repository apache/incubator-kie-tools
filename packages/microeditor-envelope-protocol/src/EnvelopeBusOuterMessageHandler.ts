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
import { EnvelopeBusMessage, EnvelopeBusMessagePurpose } from "./EnvelopeBusMessage";
import { EnvelopeBusApi } from "./EnvelopeBusApi";
import { EnvelopeBusMessageManager } from "./EnvelopeBusMessageManager";
import { InnerEnvelopeBusMessageType } from "./InnerEnvelopeBusMessageType";
import { OuterEnvelopeBusMessageType } from "./OuterEnvelopeBusMessageType";

export interface EnvelopeBusOuterMessageHandlerImpl {
  //nofity
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
  receive_resourceContentRequest(resourceContentService: ResourceContentRequest): Promise<ResourceContent | undefined>;
  receive_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList>;
}

export class EnvelopeBusOuterMessageHandler {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 10;

  private readonly manager: EnvelopeBusMessageManager<InnerEnvelopeBusMessageType>;

  public initPolling: any | false;
  public initPollingTimeout: any | false;
  public busId: string;

  public constructor(public busApi: EnvelopeBusApi, public impl: EnvelopeBusOuterMessageHandlerImpl) {
    this.busId = EnvelopeBusMessageManager.generateRandomId();
    this.initPolling = false;
    this.initPollingTimeout = false;
    this.manager = new EnvelopeBusMessageManager<InnerEnvelopeBusMessageType>(m => this.busApi.postMessage(m));
  }

  public startInitPolling(origin: string) {
    this.initPolling = setInterval(() => {
      this.request_initResponse(origin).then(() => {
        this.stopInitPolling();
      });
    }, EnvelopeBusOuterMessageHandler.INIT_POLLING_INTERVAL_IN_MS);

    this.initPollingTimeout = setTimeout(() => {
      this.stopInitPolling();
      console.info("Init polling timed out. Looks like the microeditor-envelope is not responding accordingly.");
    }, EnvelopeBusOuterMessageHandler.INIT_POLLING_TIMEOUT_IN_MS);
  }

  public stopInitPolling() {
    clearInterval(this.initPolling as number);
    this.initPolling = false;
    clearTimeout(this.initPollingTimeout as number);
    this.initPollingTimeout = false;
  }

  //NOTIFICATIONS
  public notify_editorUndo() {
    this.manager.notify(InnerEnvelopeBusMessageType.NOTIFY_EDITOR_UNDO, undefined);
  }

  public notify_editorRedo() {
    this.manager.notify(InnerEnvelopeBusMessageType.NOTIFY_EDITOR_REDO, undefined);
  }

  public notify_contentChanged(content: EditorContent) {
    this.manager.notify(InnerEnvelopeBusMessageType.NOTIFY_CONTENT_CHANGED, content);
  }

  //REQUEST
  public request_contentResponse() {
    return this.manager.request<EditorContent>(InnerEnvelopeBusMessageType.REQUEST_CONTENT, {});
  }

  public request_previewResponse() {
    return this.manager.request<string>(InnerEnvelopeBusMessageType.REQUEST_PREVIEW, {});
  }

  public request_initResponse(origin: string) {
    return this.manager.request<void>(InnerEnvelopeBusMessageType.REQUEST_INIT, { origin: origin, busId: this.busId });
  }

  public request_guidedTourElementPositionResponse(selector: string) {
    return this.manager.request<Rect>(InnerEnvelopeBusMessageType.REQUEST_GUIDED_TOUR_ELEMENT_POSITION, selector);
  }

  public receive(message: EnvelopeBusMessage<any>) {
    if (message.busId !== this.busId) {
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.RESPONSE) {
      this.manager.callback(message);
      return;
    }

    switch (message.type) {
      //NOTIFICATIONS
      case OuterEnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR:
        this.impl.receive_setContentError(message.data as string);
        break;
      case OuterEnvelopeBusMessageType.NOTIFY_READY:
        this.impl.receive_ready();
        break;
      case OuterEnvelopeBusMessageType.NOTIFY_EDITOR_NEW_EDIT:
        this.impl.receive_newEdit(message.data as KogitoEdit);
        break;
      case OuterEnvelopeBusMessageType.NOTIFY_EDITOR_OPEN_FILE:
        this.impl.receive_openFile(message.data as string);
        break;
      case OuterEnvelopeBusMessageType.NOTIFY_STATE_CONTROL_COMMAND_UPDATE:
        this.impl.receive_stateControlCommandUpdate(message.data);
        break;
      case OuterEnvelopeBusMessageType.NOTIFY_GUIDED_TOUR_USER_INTERACTION:
        this.impl.receive_guidedTourUserInteraction(message.data as UserInteraction);
        break;
      case OuterEnvelopeBusMessageType.NOTIFY_GUIDED_TOUR_REGISTER_TUTORIAL:
        this.impl.receive_guidedTourRegisterTutorial(message.data as Tutorial);
        break;
      //REQUESTS
      case OuterEnvelopeBusMessageType.REQUEST_LANGUAGE:
        this.impl.receive_languageRequest().then(language => this.manager.respond(message, language));
        break;
      case OuterEnvelopeBusMessageType.REQUEST_CONTENT:
        this.impl.receive_contentRequest().then(content => this.manager.respond(message, content));
        break;
      case OuterEnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT:
        this.impl
          .receive_resourceContentRequest(message.data as ResourceContentRequest)
          .then(resourceContent => this.manager.respond(message, resourceContent!));
        break;
      case OuterEnvelopeBusMessageType.REQUEST_RESOURCE_LIST:
        this.impl
          .receive_resourceListRequest(message.data as ResourceListRequest)
          .then(resourceList => this.manager.respond(message, resourceList));
        break;
      //
      default:
        console.info(`Unknown message type received: ${message.type}`);
        break;
    }
  }
}
