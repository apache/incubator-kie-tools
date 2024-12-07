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

import { useCallback, RefObject, useState, useMemo } from "react";
import {
  PingPongApi,
  PingPongChannelApi,
} from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/api";

export function usePingPongApiCallbacks(refs: RefObject<PingPongApi>[]) {
  const onClearLogs = useCallback(() => {
    refs.forEach((ref) => {
      ref.current?.clearLogs();
    });
  }, [refs]);

  const onGetLastPingTimestamp = useCallback(() => {
    const getTimestamps = async () => Promise.all(refs.map((ref) => ref.current?.getLastPingTimestamp() || 0));
    return getTimestamps().then((timestamps) => Math.max(...timestamps));
  }, [refs]);

  return { onClearLogs, onGetLastPingTimestamp };
}

export function usePingPongChannelApi() {
  const [lastPing, setLastPing] = useState<string>("-");
  const [lastPong, setLastPong] = useState<string>("-");
  const [pingsCount, setPingsCount] = useState(0);
  const [pongsCount, setPongsCount] = useState(0);

  const apiImpl: PingPongChannelApi = useMemo(() => {
    return {
      pingPongView__ping(source: string) {
        setPingsCount((currentPingCount) => currentPingCount + 1);
        setLastPing(source);
      },
      pingPongView__pong(source: string, _replyingTo: string) {
        setPongsCount((currentPongCount) => currentPongCount + 1);
        setLastPong(source);
      },
    };
  }, []);

  return {
    pingsCount,
    pongsCount,
    lastPing,
    lastPong,
    apiImpl,
  };
}
