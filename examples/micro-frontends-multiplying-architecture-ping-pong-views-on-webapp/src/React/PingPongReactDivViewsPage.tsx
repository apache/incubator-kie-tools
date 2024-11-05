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
import { useMemo, useRef } from "react";

import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";

import { EmbeddedDivPingPong } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/embedded";
import { PingPongApi } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/api";
import { pingPongEnvelopViewRenderDiv } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view-in-react";
import { usePingPongApiCallbacks, usePingPongChannelApi } from "../hooks";

import { StatsSidebar } from "../StatsSidebar";

export function PingPongReactDivViewsPage() {
  const { pingsCount, pongsCount, lastPing, lastPong, apiImpl } = usePingPongChannelApi();

  const react1 = useRef<PingPongApi>(null);
  const react2 = useRef<PingPongApi>(null);
  const react3 = useRef<PingPongApi>(null);

  const refs = useMemo(() => [react1, react2, react3], [react1, react2, react3]);

  const { onClearLogs, onGetLastPingTimestamp } = usePingPongApiCallbacks(refs);

  return (
    <PageSection isFilled={true}>
      <Grid hasGutter={true}>
        <GridItem span={2}>
          <StatsSidebar
            lastPing={lastPing}
            lastPong={lastPong}
            pings={pingsCount}
            pongs={pongsCount}
            onClearLogs={onClearLogs}
            onGetLastPingTimestamp={onGetLastPingTimestamp}
          />
        </GridItem>

        <GridItem span={1} />

        <GridItem span={2}>
          <EmbeddedDivPingPong
            apiImpl={apiImpl}
            ref={react1}
            name={"React 1"}
            targetOrigin={window.location.origin}
            renderView={pingPongEnvelopViewRenderDiv}
          />
        </GridItem>

        <GridItem span={2}>
          <EmbeddedDivPingPong
            apiImpl={apiImpl}
            ref={react2}
            name={"React 2"}
            targetOrigin={window.location.origin}
            renderView={pingPongEnvelopViewRenderDiv}
          />
        </GridItem>

        <GridItem span={2}>
          <EmbeddedDivPingPong
            apiImpl={apiImpl}
            ref={react3}
            name={"React 3"}
            targetOrigin={window.location.origin}
            renderView={pingPongEnvelopViewRenderDiv}
          />
        </GridItem>
      </Grid>
    </PageSection>
  );
}
