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

import * as React from "react";
import { useCallback, useImperativeHandle, useLayoutEffect, useMemo, useState } from "react";
import { PingPongApi, PingPongChannelApi, PingPongInitArgs } from "@kogito-tooling-examples/ping-pong-view/dist/api";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { useSubscription } from "@kie-tooling-core/envelope-bus/dist/hooks";
import "./styles.scss";

interface Props {
  initArgs: PingPongInitArgs;
  channelApi: MessageBusClientApi<PingPongChannelApi>;
}

interface LogEntry {
  line: string;
  time: number;
}

/**
 * This is a React implementation of a PingPongView.
 *
 *
 */
export const PingPongReactImpl = React.forwardRef<PingPongApi, Props>((props, forwardedRef) => {
  const pingPongViewApi: PingPongApi = useMemo(
    () => ({
      /*
       * Here's where your PingPongViewApi implementation would go if it wasn't empty.
       *
       * Since PingPongViews can't be controlled by its parents (PingPongViewEnvelopeView, or EmbeddedPingPongView),
       * we don't need anything here.
       */
    }),
    []
  );

  // This line turns your pingPongViewApi implementation into a `ref` of this component,
  // allowing this Ping-Pong View implementation to be controlled by its parents.
  useImperativeHandle(forwardedRef, () => pingPongViewApi, [pingPongViewApi]);

  // Create `log` state to display the messages exchanged by Ping-Pong Views
  const [log, setLog] = useState<LogEntry[]>([{ line: "Logs will show up here", time: 0 }]);

  // Function to be called when pressing the 'Ping others!' button.
  const ping = useCallback(() => {
    props.channelApi.notifications.pingPongView__ping.send(props.initArgs.name);
  }, []);

  // Subscribes to messages sent to `pingPongView__ping`.
  // Notice how this listens to messages received BY the Channel.
  useSubscription(props.channelApi.notifications.pingPongView__ping, (pingSource) => {
    // If this instance sent the PING, we ignore it.
    if (pingSource === props.initArgs.name) {
      return;
    }

    // Updates the log to show a feedback that a PING message was observed.
    setLog((prevLog) => [...prevLog, { line: `PING from '${pingSource}'.`, time: getCurrentTime() }]);

    // Acknowledges the PING message by sending back a PONG message.
    props.channelApi.notifications.pingPongView__pong.send(props.initArgs.name, pingSource);
  });

  // Subscribes to messages sent to `pingPongView__pong`.
  useSubscription(props.channelApi.notifications.pingPongView__pong, (pongSource: string, replyingTo: string) => {
    // If this instance sent the PONG, or if this PONG was not meant to this instance, we ignore it.
    if (pongSource === props.initArgs.name || replyingTo !== props.initArgs.name) {
      return;
    }

    // Updates the log to show a feedback that a PONG message was observed.
    setLog((prevLog) => [...prevLog, { line: `PONG from '${pongSource}'.`, time: getCurrentTime() }]);
  });

  // This effect simply keeps appending a dot to the log so that users have a sense of time passing.
  useLayoutEffect(() => {
    const interval = setInterval(() => setLog((prevLog) => [...prevLog, { line: ".", time: getCurrentTime() }]), 2000);
    return () => clearInterval(interval);
  }, []);

  return (
    <>
      <i>#{props.initArgs.name}</i>

      <div className={"ping-pong-view--header"}>
        <span>Hello from React!</span>
        <button onClick={ping}>Ping others!</button>
      </div>
      <div className={"ping-pong-view--log"}>
        {log.slice(-10).map((line) => (
          <p style={{ fontFamily: "monospace" }} key={line.time}>
            {line.line}
          </p>
        ))}
      </div>
    </>
  );
});

function getCurrentTime() {
  return window.performance.now();
}
