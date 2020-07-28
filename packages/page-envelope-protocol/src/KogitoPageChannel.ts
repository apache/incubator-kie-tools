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

import { Channel } from "@kogito-tooling/editor-envelope-protocol";
import { EnvelopeBus, EnvelopeBusMessage, FunctionPropertyNames } from "@kogito-tooling/envelope-bus";
import { KogitoPageChannelApi, KogitoPageEnvelopeApi, PageInitArgs } from "./index";

export class KogitoPageChannel {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 100;

  public initPolling?: ReturnType<typeof setInterval>;
  public initPollingTimeout?: ReturnType<typeof setTimeout>;

  public get client() {
    return this.channel.client;
  }

  public get busId() {
    return this.channel.busId;
  }

  public constructor(
    bus: EnvelopeBus,
    private readonly channel = new Channel<KogitoPageChannelApi, KogitoPageEnvelopeApi>(bus)
  ) {}

  public startInitPolling(origin: string, initArgs: PageInitArgs) {
    this.initPolling = setInterval(() => {
      this.client.request("init", { origin, busId: this.channel.busId }, initArgs).then(() => {
        this.stopInitPolling();
      });
    }, KogitoPageChannel.INIT_POLLING_INTERVAL_IN_MS);

    this.initPollingTimeout = setTimeout(() => {
      this.stopInitPolling();
      console.info("Init polling timed out. Looks like the micropage-envelope is not responding accordingly.");
    }, KogitoPageChannel.INIT_POLLING_TIMEOUT_IN_MS);
  }

  public stopInitPolling() {
    clearInterval(this.initPolling!);
    this.initPolling = undefined;
    clearTimeout(this.initPollingTimeout!);
    this.initPollingTimeout = undefined;
  }

  public receive(
    message: EnvelopeBusMessage<
      unknown,
      FunctionPropertyNames<KogitoPageEnvelopeApi> | FunctionPropertyNames<KogitoPageChannelApi>
    >,
    api: KogitoPageChannelApi
  ) {
    this.channel.receive(message, api);
  }
}
