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
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { EmbeddedDivPingPong } from "@kie-tools-examples/ping-pong-view/dist/embedded";
import { PingPongApi } from "@kie-tools-examples/ping-pong-view/dist/api";
import { pingPongEnvelopViewRenderDiv } from "@kie-tools-examples/ping-pong-view-react";
import { StatsSidebar } from "../StatsSidebar";
import { usePingPongApiCallbacks, usePingPongChannelApi } from "../hooks";

export function PingPongReactDivViewsPage() {
  const { pingsCount, pongsCount, lastPing, lastPong, apiImpl } = usePingPongChannelApi();

  const react1 = useRef<PingPongApi>(null);
  const react2 = useRef<PingPongApi>(null);
  const react3 = useRef<PingPongApi>(null);

  const refs = useMemo(() => [react1, react2, react3], [react1, react2, react3]);

  const { onClearLogs, onGetLastPingTimestamp } = usePingPongApiCallbacks(refs);

  return (
    <Page>
      <div className={"webapp--page-main-div"}>
        <StatsSidebar
          lastPing={lastPing}
          lastPong={lastPong}
          pings={pingsCount}
          pongs={pongsCount}
          onClearLogs={onClearLogs}
          onGetLastPingTimestamp={onGetLastPingTimestamp}
        />
        <div className={"webapp--page-ping-pong-view"}>
          <PageSection style={{ flex: "1 1" }}>
            <EmbeddedDivPingPong
              apiImpl={apiImpl}
              ref={react1}
              name={"React 1"}
              targetOrigin={window.location.origin}
              renderView={pingPongEnvelopViewRenderDiv}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1" }}>
            <EmbeddedDivPingPong
              apiImpl={apiImpl}
              ref={react2}
              name={"React 2"}
              targetOrigin={window.location.origin}
              renderView={pingPongEnvelopViewRenderDiv}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1" }}>
            <EmbeddedDivPingPong
              apiImpl={apiImpl}
              ref={react3}
              name={"React 3"}
              targetOrigin={window.location.origin}
              renderView={pingPongEnvelopViewRenderDiv}
            />
          </PageSection>
        </div>
      </div>
    </Page>
  );
}
