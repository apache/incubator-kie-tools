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

import * as React from "react";
import { useState, useEffect } from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

interface Props {
  lastPing: string;
  lastPong: string;
  pings: number;
  pongs: number;
  onClearLogs?: () => void;
  onGetLastPingTimestamp?: () => Promise<number>;
}

/**
 * A sidebar that shows the current stats of the pings/pongs
 */
export function StatsSidebar(props: Props) {
  const [lastPingTimestamp, setLastPingTimestamp] = useState<string>("-");

  const { pings, pongs, lastPing, lastPong, onClearLogs, onGetLastPingTimestamp } = props;

  useEffect(() => {
    if (pings) {
      onGetLastPingTimestamp?.().then((timestamp) => {
        setLastPingTimestamp(new Date(timestamp).toLocaleTimeString());
      });
    }
  }, [pings, onGetLastPingTimestamp]);

  return (
    <div style={{ padding: "16px" }}>
      <Stack hasGutter={true}>
        <StackItem>
          <b>Stats</b>
        </StackItem>
        <StackItem>
          <div>
            Pings: &nbsp;
            <Label>{pings}</Label>
          </div>
        </StackItem>
        <StackItem>
          <div>
            Pongs: &nbsp;
            <Label>{pongs}</Label>
          </div>
        </StackItem>
        <StackItem>
          <div>
            Last ping: &nbsp;
            <Label>{lastPing}</Label>
          </div>
        </StackItem>
        <StackItem>
          <div>
            Last ping timestamp: &nbsp;
            <Label>{lastPingTimestamp}</Label>
          </div>
        </StackItem>
        <StackItem>
          <div>
            Last pong: &nbsp;
            <Label>{lastPong}</Label>
          </div>
        </StackItem>
        <StackItem>
          <div>
            <button onClick={onClearLogs}>Clear logs!</button>
          </div>
        </StackItem>
      </Stack>
    </div>
  );
}
