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
  EnvelopeBusMessageManager,
  FunctionPropertyNames
} from "@kogito-tooling/envelope-bus";

export class Channel<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  public get client() {
    return this.manager.client;
  }

  public readonly busId: string;

  constructor(
    bus: EnvelopeBus,
    private readonly manager = new EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>(
      message => bus.postMessage(message),
      "KogitoEditorChannel"
    )
  ) {
    this.busId = this.generateRandomId();
  }

  public receive(
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    api: ApiToProvide
  ) {
    if (message.busId !== this.busId) {
      return;
    }

    this.manager.server.receive(message, api);
  }

  public generateRandomId() {
    return (
      "_" +
      Math.random()
        .toString(36)
        .substr(2, 9)
    );
  }
}
