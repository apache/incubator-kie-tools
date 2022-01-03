import { Injectable } from "@angular/core";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { PingPongChannelApi, PingPongInitArgs } from "@kogito-tooling-examples/ping-pong-lib/dist/api";
import { PingPong } from "@kogito-tooling-examples/ping-pong-lib/dist/envelope";
import { ReplaySubject } from "rxjs";

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
  return window.performance.now();
}

@Injectable({
  providedIn: "root",
})
export class ApiService {
  channelApi: MessageBusClientApi<PingPongChannelApi>;
  initArgs: PingPongInitArgs;
  log = new ReplaySubject<LogEntry>(10);
  dotInterval: number;

  constructor() {}

  create(initArgs: PingPongInitArgs, channelApi: MessageBusClientApi<PingPongChannelApi>): PingPong {
    this.initArgs = initArgs;
    this.channelApi = channelApi;

    // Subscribe to ping notifications.
    this.channelApi.notifications.pingPongView__ping.subscribe((pingSource) => {
      // If this instance sent the PING, we ignore it.
      if (pingSource === this.initArgs.name) {
        return;
      }

      // Add a new line to our log, stating that we received a ping.
      this.log.next({ line: `PING from '${pingSource}'.`, time: getCurrentTime() });

      // Acknowledges the PING message by sending back a PONG message.
      this.channelApi.notifications.pingPongView__pong.send(this.initArgs.name, pingSource);
    });

    // Subscribe to pong notifications.
    this.channelApi.notifications.pingPongView__pong.subscribe((pongSource: string, replyingTo: string) => {
      // If this instance sent the PONG, or if this PONG was not meant to this instance, we ignore it.
      if (pongSource === this.initArgs.name || replyingTo !== this.initArgs.name) {
        return;
      }

      // Updates the log to show a feedback that a PONG message was observed.
      this.log.next({ line: `PONG from '${pongSource}'.`, time: getCurrentTime() });
    });

    // Populate the log with a dot each 2 seconds.
    this.dotInterval = window.setInterval(() => {
      this.log.next({ line: ".", time: getCurrentTime() });
    }, 2000);

    return {};
  }

  // Send a ping to the channel.
  ping() {
    this.channelApi.notifications.pingPongView__ping.send(this.initArgs.name);
  }

  ngOnDestroy() {
    window.clearInterval(this.dotInterval);
  }
}
