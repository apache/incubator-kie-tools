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
  Association,
  EnvelopeBus,
  EnvelopeBusMessage,
  EnvelopeBusMessageManager,
  KogitoChannelApi,
  KogitoEnvelopeApi,
  KogitoEnvelopeMessageTypes
} from "@kogito-tooling/microeditor-envelope-protocol";
import { KogitoEdit, ResourceContentOptions, ResourceListOptions, StateControlCommand } from "@kogito-tooling/microeditor-envelope-protocol";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";

export class KogitoEnvelopeBus {
  public targetOrigin: string;
  public associatedBusId: string;
  public eventListener?: any;
  public readonly manager: EnvelopeBusMessageManager<KogitoEnvelopeApi, KogitoChannelApi>;

  public get client() {
    return this.manager.client;
  }

  constructor(private readonly bus: EnvelopeBus, private readonly api: KogitoEnvelopeApi) {
    this.manager = new EnvelopeBusMessageManager(message => this.send(message), api, "KogitoEnvelopeBus");
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

  public send<T>(message: EnvelopeBusMessage<T, KogitoEnvelopeMessageTypes>) {
    if (!this.targetOrigin) {
      throw new Error("Tried to send message without targetOrigin set");
    }
    this.bus.postMessage({ ...message, busId: this.associatedBusId }, this.targetOrigin);
  }

  public notify_setContentError(errorMessage: string) {
    return this.client.notify("receive_setContentError", errorMessage);
  }

  public notify_ready() {
    return this.client.notify("receive_ready");
  }

  public notify_newEdit(edit: KogitoEdit) {
    return this.client.notify("receive_newEdit", edit);
  }

  public notify_openFile(path: string) {
    return this.client.notify("receive_openFile", path);
  }

  public notify_guidedTourRefresh(userInteraction: UserInteraction) {
    return this.client.notify("receive_guidedTourUserInteraction", userInteraction);
  }

  public notify_guidedTourRegisterTutorial(tutorial: Tutorial) {
    return this.client.notify("receive_guidedTourRegisterTutorial", tutorial);
  }

  public notify_stateControlCommandUpdate(stateControlCommand: StateControlCommand) {
    return this.client.notify("receive_stateControlCommandUpdate", stateControlCommand);
  }

  public request_languageResponse() {
    return this.client.request("receive_languageRequest");
  }

  public request_contentResponse() {
    return this.client.request("receive_contentRequest");
  }

  public request_resourceContent(path: string, opts?: ResourceContentOptions) {
    return this.client.request("receive_resourceContentRequest", { path: path, opts: opts });
  }

  public request_resourceList(pattern: string, opts?: ResourceListOptions) {
    return this.client.request("receive_resourceListRequest", { pattern: pattern, opts: opts });
  }

  public receive(message: EnvelopeBusMessage<any, KogitoEnvelopeMessageTypes>) {
    this.manager.server.receive(message);
  }
}
