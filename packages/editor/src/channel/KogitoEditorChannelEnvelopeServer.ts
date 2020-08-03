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

import { EditorContent, EditorInitArgs, KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "../api";
import { EnvelopeBus } from "@kogito-tooling/envelope-bus/dist/api";
import { ChannelEnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";

export class KogitoEditorChannelEnvelopeServer extends ChannelEnvelopeServer<
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi
> {
  constructor(bus: EnvelopeBus, origin: string, private readonly initArgs: EditorInitArgs) {
    super(bus, origin);
  }

  public pollInit(): Promise<any> {
    return this.request_initResponse(this.origin, this.initArgs);
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
    return this.client.request("receive_initRequest", { origin: origin, busId: this.busId }, initArgs);
  }
}
