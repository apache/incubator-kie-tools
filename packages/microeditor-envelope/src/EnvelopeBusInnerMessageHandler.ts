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

import { EnvelopeBusApi, EnvelopeBusMessage, EnvelopeBusMessageType } from "appformer-js-microeditor-envelope-protocol";
import { LanguageData } from "appformer-js-core";

export interface Impl {
  receive_contentResponse(content: string): void;
  receive_languageResponse(languageData: LanguageData): void;
  receive_contentRequest(): void;
}

export class EnvelopeBusInnerMessageHandler {
  private readonly envelopeBusApi: EnvelopeBusApi;
  private readonly impl: Impl;

  public capturedInitRequestYet = false;
  public targetOrigin: string;
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
    this.envelopeBusApi.postMessage(message, this.targetOrigin);
  }

  public respond_initRequest() {
    return this.send({ type: EnvelopeBusMessageType.RETURN_INIT, data: undefined });
  }

  public respond_contentRequest(content: string) {
    return this.send({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: content });
  }

  public request_languageResponse() {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined });
  }

  public request_contentResponse() {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });
  }

  public notify_dirtyIndicatorChange(isDirty: boolean) {
    return this.send({ type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE, data: isDirty });
  }

  private receive_initRequest(targetOrigin: string) {
    if (this.capturedInitRequestYet) {
      return;
    }

    this.capturedInitRequestYet = true;
    this.targetOrigin = targetOrigin;

    this.respond_initRequest();
    this.request_languageResponse();
  }

  public receive(message: EnvelopeBusMessage<any>) {
    switch (message.type) {
      case EnvelopeBusMessageType.REQUEST_INIT:
        this.receive_initRequest(message.data as string);
        break;
      case EnvelopeBusMessageType.RETURN_LANGUAGE:
        this.impl.receive_languageResponse(message.data as LanguageData);
        break;
      case EnvelopeBusMessageType.RETURN_CONTENT:
        this.impl.receive_contentResponse(message.data as string);
        break;
      case EnvelopeBusMessageType.REQUEST_CONTENT:
        this.impl.receive_contentRequest();
        break;
      default:
        console.info(`Unknown message type received: ${message.type}"`);
        break;
    }
  }
}
