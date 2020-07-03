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

import { EditorContent } from "@kogito-tooling/core-api";
import { EnvelopeBus, EnvelopeBusMessage, EnvelopeBusMessageManager, FunctionPropertyNames } from "../bus";
import { KogitoChannelApi } from "./KogitoChannelApi";
import { KogitoEnvelopeApi } from "./KogitoEnvelopeApi";

export type KogitoEnvelopeMessageTypes =
  | FunctionPropertyNames<KogitoChannelApi>
  | FunctionPropertyNames<KogitoEnvelopeApi>;

export class KogitoChannelBus {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 100;

  public readonly manager: EnvelopeBusMessageManager<KogitoChannelApi, KogitoEnvelopeApi>;

  public initPolling: any | false;
  public initPollingTimeout: any | false;
  public busId: string;

  public get client() {
    return this.manager.client;
  }

  public constructor(public bus: EnvelopeBus, api: KogitoChannelApi) {
    this.initPolling = false;
    this.initPollingTimeout = false;
    this.manager = new EnvelopeBusMessageManager(message => this.bus.postMessage(message), api);
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

  public receive(message: EnvelopeBusMessage<unknown, KogitoEnvelopeMessageTypes>) {
    if (message.busId !== this.busId) {
      return;
    }

    this.manager.server.receive(message);
  }

  //

  public notify_editorUndo() {
    this.manager.client.notify("receive_editorUndo");
  }

  public notify_editorRedo() {
    this.manager.client.notify("receive_editorRedo");
  }

  public notify_contentChanged(content: EditorContent) {
    this.manager.client.notify("receive_contentChanged", content);
  }

  public request_contentResponse() {
    return this.manager.client.request("receive_contentRequest");
  }

  public request_previewResponse() {
    return this.manager.client.request("receive_previewRequest");
  }

  public request_initResponse(origin: string) {
    return this.manager.client.request("receive_initRequest", { origin: origin, busId: this.busId });
  }

  public request_guidedTourElementPositionResponse(selector: string) {
    return this.manager.client.request("receive_guidedTourElementPositionRequest", selector);
  }
}
