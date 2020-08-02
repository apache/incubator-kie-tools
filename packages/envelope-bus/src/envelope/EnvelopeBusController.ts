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
  ApiDefinition,
  EnvelopeBus,
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  FunctionPropertyNames
} from "../api";
import { EnvelopeBusMessageManager } from "../common";

export class EnvelopeBusController<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  public targetOrigin?: string;
  public associatedBusId?: string;
  public eventListener?: any;
  public readonly manager: EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>;

  public get client() {
    return this.manager.client;
  }

  constructor(private readonly bus: EnvelopeBus) {
    this.manager = new EnvelopeBusMessageManager(message => this.send(message), "KogitoEnvelopeBus");
  }

  public associate(origin: string, busId: string) {
    this.targetOrigin = origin;
    this.associatedBusId = busId;
  }

  public startListening(api: ApiToProvide) {
    if (this.eventListener) {
      return;
    }

    this.eventListener = (event: any) => this.receive(event.data, api);
    window.addEventListener("message", this.eventListener);
  }

  public stopListening() {
    window.removeEventListener("message", this.eventListener);
  }

  public send<T>(
    message: EnvelopeBusMessage<T, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>
  ) {
    if (!this.targetOrigin) {
      throw new Error("Tried to send message without targetOrigin set");
    }
    this.bus.postMessage({ ...message, busId: this.associatedBusId }, this.targetOrigin);
  }

  public receive(
    message: EnvelopeBusMessage<any, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    api: ApiToProvide
  ) {
    if (!message.busId) {
      this.manager.server.receive(message, api);
    } else if (message.busId && message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      this.manager.server.receive(message, {} as any);
    }
  }
}
