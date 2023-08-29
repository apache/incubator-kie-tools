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
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
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
 *
 * @param props
 * @constructor
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
    <div>
      <Nav className={"webapp--page-navigation webapp--page-ping-pong-view-navigation"}>
        <div className={"webapp--page-navigation-title-div"}>
          <Title className={"webapp--page-navigation-title-h3"} headingLevel="h3" size="xl">
            Stats
          </Title>
        </div>
        <NavList>
          <NavItem>
            <div>
              Pings: &nbsp;
              <Label>{pings}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Pongs: &nbsp;
              <Label>{pongs}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Last ping: &nbsp;
              <Label>{lastPing}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Last ping timestamp: &nbsp;
              <Label>{lastPingTimestamp}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Last pong: &nbsp;
              <Label>{lastPong}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              <button onClick={onClearLogs}>Clear logs!</button>
            </div>
          </NavItem>
        </NavList>
      </Nav>
    </div>
  );
}
