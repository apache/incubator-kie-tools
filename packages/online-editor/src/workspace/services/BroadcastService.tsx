/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { ChannelKind, Event } from "../model/Event";

export class BroadcastService {
  private readonly registry: Map<string, BroadcastChannel> = new Map();

  public register(kinds: ChannelKind[]): void {
    kinds.forEach((kind: ChannelKind) => {
      if (this.registry.has(kind)) {
        throw new Error(`Broadcast channel ${kind} already registered`);
      }
      this.registry.set(kind, new BroadcastChannel(kind));
    });
  }

  public send<T extends Event>(kind: ChannelKind, message: T): void {
    this.getChannel(kind).postMessage(message);
  }

  public onEvent<T extends Event>(kind: ChannelKind, handler: (event: T) => void): void {
    this.getChannel(kind).onmessage = (event: MessageEvent<T>) => {
      handler(event.data);
    };
  }

  public close(kind: ChannelKind): void {
    this.getChannel(kind).close();
  }

  private getChannel(kind: ChannelKind): BroadcastChannel {
    const channel = this.registry.get(kind);
    if (!channel) {
      throw new Error(`Broadcast channel ${channel} not registered`);
    }
    return channel;
  }
}
