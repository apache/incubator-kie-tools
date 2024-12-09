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

import {
  EmbeddedDivPingPong,
  EmbeddedIFramePingPong,
} from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/embedded";
import { PingPongApi } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/api";
import { pingPongEnvelopViewRenderDiv as renderReactDiv } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view-in-react";
import { pingPongEnvelopViewRenderDiv as renderAngularDiv } from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view-in-angular/dist/wc/lib";
import { usePingPongApiCallbacks, usePingPongChannelApi } from "../hooks";

import { StatsSidebar } from "../StatsSidebar";

const reactEnvelopePath = "ping-pong-view-in-react-envelope.html";
const angularEnvelopePath = "ping-pong-view-in-angular-envelope/index.html";

export function PingPongMixedViewsPage() {
  const { pingsCount, pongsCount, lastPing, lastPong, apiImpl } = usePingPongChannelApi();

  const angularIFrame = useRef<PingPongApi>(null);
  const angularDiv = useRef<PingPongApi>(null);
  const reactIFrame = useRef<PingPongApi>(null);
  const reactDiv = useRef<PingPongApi>(null);

  const refs = useMemo(
    () => [angularIFrame, angularDiv, reactIFrame, reactDiv],
    [angularIFrame, angularDiv, reactIFrame, reactDiv]
  );

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

        <GridItem span={2}>
          <EmbeddedIFramePingPong
            apiImpl={apiImpl}
            name={"Angular iFrame"}
            ref={angularIFrame}
            targetOrigin={window.location.origin}
            envelopePath={angularEnvelopePath}
          />
        </GridItem>

        <GridItem span={2}>
          <EmbeddedDivPingPong
            apiImpl={apiImpl}
            name={"Angular Div"}
            ref={angularDiv}
            targetOrigin={window.location.origin}
            renderView={renderAngularDiv}
          />
        </GridItem>

        <GridItem span={2}>
          <EmbeddedIFramePingPong
            apiImpl={apiImpl}
            name={"React iFrame"}
            ref={reactIFrame}
            targetOrigin={window.location.origin}
            envelopePath={reactEnvelopePath}
          />
        </GridItem>

        <GridItem span={2}>
          <EmbeddedDivPingPong
            apiImpl={apiImpl}
            name={"React Div"}
            ref={reactDiv}
            targetOrigin={window.location.origin}
            renderView={renderReactDiv}
          />
        </GridItem>
      </Grid>
    </PageSection>
  );
}
