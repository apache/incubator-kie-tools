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

import { EditorContent } from "./api";
import { EnvelopeBus, EnvelopeBusMessage, FunctionPropertyNames } from "@kogito-tooling/envelope-bus/dist/api";
import { KogitoEditorChannelApi } from "./KogitoEditorChannelApi";
import { EditorInitArgs, KogitoEditorEnvelopeApi } from "./KogitoEditorEnvelopeApi";
import { ChannelEnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";

type KogitoEditorMessageBusType =
  | FunctionPropertyNames<KogitoEditorChannelApi>
  | FunctionPropertyNames<KogitoEditorEnvelopeApi>;

export class KogitoEditorChannelEnvelopeServer {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 100;

  public initPolling?: ReturnType<typeof setInterval>;
  public initPollingTimeout?: ReturnType<typeof setTimeout>;

  public get client() {
    return this.envelopeServer.client;
  }

  public get busId() {
    return this.envelopeServer.busId;
  }

  public constructor(
    bus: EnvelopeBus,
    private readonly envelopeServer = new ChannelEnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(bus)
  ) {}

  public startInitPolling(origin: string, initArgs: EditorInitArgs) {
    this.initPolling = setInterval(() => {
      this.request_initResponse(origin, initArgs).then(() => {
        this.stopInitPolling();
      });
    }, KogitoEditorChannelEnvelopeServer.INIT_POLLING_INTERVAL_IN_MS);

    this.initPollingTimeout = setTimeout(() => {
      this.stopInitPolling();
      console.info("Init polling timed out. Looks like the Editor Envelope is not responding accordingly.");
    }, KogitoEditorChannelEnvelopeServer.INIT_POLLING_TIMEOUT_IN_MS);
  }

  public stopInitPolling() {
    clearInterval(this.initPolling!);
    this.initPolling = undefined;
    clearTimeout(this.initPollingTimeout!);
    this.initPollingTimeout = undefined;
  }

  public receive(message: EnvelopeBusMessage<unknown, KogitoEditorMessageBusType>, api: KogitoEditorChannelApi) {
    this.envelopeServer.receive(message, api);
  }

  public notify_editorUndo() {
    this.client.notify("receive_editorUndo");
  }

  public notify_editorRedo() {
    this.client.notify("receive_editorRedo");
  }

  public notify_contentChanged(content: EditorContent) {
    this.client.notify("receive_contentChanged", content);
  }

  public request_contentResponse() {
    return this.client.request("receive_contentRequest");
  }

  public request_previewResponse() {
    return this.client.request("receive_previewRequest");
  }

  public request_initResponse(origin: string, initArgs: EditorInitArgs) {
    return this.client.request("receive_initRequest", { origin: origin, busId: this.envelopeServer.busId }, initArgs);
  }

  public request_guidedTourElementPositionResponse(selector: string) {
    return this.client.request("receive_guidedTourElementPositionRequest", selector);
  }
}
