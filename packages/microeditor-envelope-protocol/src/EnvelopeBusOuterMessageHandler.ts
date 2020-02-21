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
  LanguageData,
  ResourceContent,
  ResourcesList,
  EditorContent,
  ResourceContentRequest,
  ResourceContentService,
  KogitoEdit
} from "@kogito-tooling/core-api";
import { EnvelopeBusMessage } from "./EnvelopeBusMessage";
import { EnvelopeBusMessageType } from "./EnvelopeBusMessageType";
import { EnvelopeBusApi } from "./EnvelopeBusApi";

export interface EnvelopeBusOuterMessageHandlerImpl {
  pollInit(): void;
  receive_languageRequest(): void;
  receive_contentRequest(): void;
  receive_contentResponse(content: EditorContent): void;
  receive_setContentError(errorMessage: string): void;
  receive_dirtyIndicatorChange(isDirty: boolean): void;
  receive_resourceContentRequest(resourceContentService: ResourceContentRequest): void;
  receive_resourceListRequest(globPattern: string): void;
  receive_ready(): void;
}

export class EnvelopeBusOuterMessageHandler {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 10;

  public initPolling: any | false;
  public initPollingTimeout: any | false;
  public impl: EnvelopeBusOuterMessageHandlerImpl;
  public busApi: EnvelopeBusApi;
  public busId: string;

  public constructor(
    busApi: EnvelopeBusApi,
    impl: (self: EnvelopeBusOuterMessageHandler) => EnvelopeBusOuterMessageHandlerImpl
  ) {
    this.busId = EnvelopeBusOuterMessageHandler.generateRandomBusId();
    this.busApi = busApi;
    this.impl = impl(this);
    this.initPolling = false;
    this.initPollingTimeout = false;
  }

  public startInitPolling() {
    this.initPolling = setInterval(
      () => this.impl.pollInit(),
      EnvelopeBusOuterMessageHandler.INIT_POLLING_INTERVAL_IN_MS
    );

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

  public respond_languageRequest(languageData?: LanguageData) {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData });
  }

  public respond_contentRequest(content: EditorContent) {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: content });
  }

  public request_contentResponse() {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });
  }

  public request_editor_undo() {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_UNDO, data: undefined });
  }

  public request_editor_redo() {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_REDO, data: undefined });
  }

  public request_initResponse(origin: string) {
    this.busApi.postMessage({ busId: this.busId, type: EnvelopeBusMessageType.REQUEST_INIT, data: origin });
  }

  public respond_resourceContent(content: ResourceContent) {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.RETURN_RESOURCE_CONTENT, data: content });
  }

  public respond_resourceList(resourcesList: ResourcesList) {
    this.busApi.postMessage({ type: EnvelopeBusMessageType.RETURN_RESOURCE_LIST, data: resourcesList });
  }

  public receive(message: EnvelopeBusMessage<any>) {
    if (message.busId !== this.busId) {
      return;
    }

    switch (message.type) {
      case EnvelopeBusMessageType.RETURN_INIT:
        this.stopInitPolling();
        break;
      case EnvelopeBusMessageType.REQUEST_LANGUAGE:
        this.impl.receive_languageRequest();
        break;
      case EnvelopeBusMessageType.RETURN_CONTENT:
        this.impl.receive_contentResponse(message.data as EditorContent);
        break;
      case EnvelopeBusMessageType.REQUEST_CONTENT:
        this.impl.receive_contentRequest();
        break;
      case EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR:
        this.impl.receive_setContentError(message.data as string);
        break;
      case EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE:
        this.impl.receive_dirtyIndicatorChange(message.data as boolean);
        break;
      case EnvelopeBusMessageType.NOTIFY_READY:
        this.impl.receive_ready();
        break;
      case EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT:
        this.impl.receive_resourceContentRequest(message.data as ResourceContentRequest);
        break;
      case EnvelopeBusMessageType.REQUEST_RESOURCE_LIST:
        this.impl.receive_resourceListRequest(message.data as string);
        break;
      case EnvelopeBusMessageType.NOTIFY_EDITOR_NEW_EDIT:
        console.info("EnvelopeBusOuterMessageHandler: Received new edit: " + message.data);
        break;
      default:
        console.info(`Unknown message type received: ${message.type}`);
        break;
    }
  }

  private static generateRandomBusId() {
    return (
      "_" +
      Math.random()
        .toString(36)
        .substr(2, 9)
    );
  }
}
