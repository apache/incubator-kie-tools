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
import { Label, Nav, NavItem, NavList, Title } from "@patternfly/react-core";

interface Props {
  lastPing: string;
  lastPong: string;
  pings: number;
  pongs: number;
}

/**
 * A sidebar that shows the current stats of the pings/pongs
 *
 * @param props
 * @constructor
 */
export function StatsSidebar(props: Props) {
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
              <Label>{props.pings}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Pongs: &nbsp;
              <Label>{props.pongs}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Last ping: &nbsp;
              <Label>{props.lastPing}</Label>
            </div>
          </NavItem>
          <NavItem>
            <div>
              Last pong: &nbsp;
              <Label>{props.lastPong}</Label>
            </div>
          </NavItem>
        </NavList>
      </Nav>
    </div>
  );
}
