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

import { EnvelopeBusMessage } from "@kie-tooling-core/envelope-bus/dist/api";

export class EnvelopeBusMessageBroadcaster {
  private readonly subscriptions: Array<(msg: EnvelopeBusMessage<unknown, any>) => void> = [];

  public broadcast(message: EnvelopeBusMessage<unknown, any>) {
    // Messages directly from the Channel to the Envelope do not have a targetEnvelopeServerId and should be ignored.
    // That means that messages that are broadcast-able come from other Envelopes to other Channels, so they have an targetEnvelopeServerId.
    if (!message.targetEnvelopeServerId) {
      return;
    }

    this.subscriptions.forEach((callback) => callback(message));
  }

  public subscribe(callback: (msg: EnvelopeBusMessage<unknown, any>) => void) {
    this.subscriptions.push(callback);
    return callback;
  }

  public unsubscribe(callback: (msg: EnvelopeBusMessage<unknown, any>) => void) {
    const subscriptionIndex = this.subscriptions.indexOf(callback);
    if (subscriptionIndex >= 0) {
      this.subscriptions.splice(subscriptionIndex, 1);
    }
  }
}
