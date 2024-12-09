/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from "@angular/core";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import {
  PingPongChannelApi,
  PingPongInitArgs,
} from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/api";
import { PingPongFactory } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/envelope";
import { ReplaySubject, BehaviorSubject, Subject } from "rxjs";

declare global {
  interface Window {
    initArgs: PingPongInitArgs;
    channelApi: PingPongChannelApi;
  }
}

export interface LogEntry {
  line: string;
  time: number;
}

function getCurrentTime() {
  return Date.now();
}

@Injectable()
export class PingPongApiService implements PingPongFactory {
  channelApi?: MessageBusClientApi<PingPongChannelApi>;
  initArgs?: PingPongInitArgs;
  log = new ReplaySubject<LogEntry>(10);
  logCleared = new Subject();
  lastPingTimestamp = new BehaviorSubject<number>(0);
  dotInterval?: number;
  initialized = false;
  pingSubscription?: (source: string) => void;
  pongSubscription?: (source: string, replyingTo: string) => void;

  constructor() {}

  create(initArgs: PingPongInitArgs, channelApi: MessageBusClientApi<PingPongChannelApi>) {
    // Making sure we don't subscribe more than once.
    this.clearSubscriptions();
    this.clearInterval();

    this.initArgs = initArgs;
    this.channelApi = channelApi;

    // Subscribe to ping notifications.
    this.pingSubscription = this.channelApi.notifications.pingPongView__ping.subscribe((pingSource) => {
      // If this instance sent the PING, we ignore it.
      if (pingSource === initArgs.name) {
        return;
      }

      // Add a new line to our log, stating that we received a ping.
      this.log.next({ line: `PING from '${pingSource}'.`, time: getCurrentTime() });

      // Acknowledges the PING message by sending back a PONG message.
      channelApi.notifications.pingPongView__pong.send(initArgs.name, pingSource);
    });

    // Subscribe to pong notifications.
    this.pongSubscription = this.channelApi.notifications.pingPongView__pong.subscribe(
      (pongSource: string, replyingTo: string) => {
        // If this instance sent the PONG, or if this PONG was not meant to this instance, we ignore it.
        if (pongSource === initArgs.name || replyingTo !== initArgs.name) {
          return;
        }

        // Updates the log to show a feedback that a PONG message was observed.
        this.log.next({ line: `PONG from '${pongSource}'.`, time: getCurrentTime() });
      }
    );

    // Populate the log with a dot each 2 seconds.
    this.dotInterval = window.setInterval(() => {
      this.log.next({ line: ".", time: getCurrentTime() });
    }, 2000);

    this.initialized = true;

    return () => ({
      clearLogs: () => {
        this.log = new ReplaySubject<LogEntry>(10);
        // Emit a value to logCleared so we can re-subscribe to this.log wherever needed.
        this.logCleared.next(null);
      },
      getLastPingTimestamp: () => {
        return Promise.resolve(this.lastPingTimestamp.value);
      },
    });
  }

  // Send a ping to the channel.
  ping() {
    if (this.initArgs && this.channelApi) {
      this.channelApi.notifications.pingPongView__ping.send(this.initArgs.name);
      this.lastPingTimestamp.next(getCurrentTime());
    }
  }

  clearSubscriptions() {
    if (this.channelApi) {
      this.pingSubscription && this.channelApi.notifications.pingPongView__ping.unsubscribe(this.pingSubscription);
      this.pongSubscription && this.channelApi.notifications.pingPongView__pong.unsubscribe(this.pongSubscription);
    }
  }

  clearInterval() {
    window.clearInterval(this.dotInterval);
  }

  ngOnDestroy() {
    this.clearSubscriptions();
    this.clearInterval();
  }
}
