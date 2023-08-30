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
import { EmbeddedDivPingPong, EmbeddedIFramePingPong } from "@kie-tools-examples/ping-pong-view/dist/embedded";
import { PingPongApi } from "@kie-tools-examples/ping-pong-view/dist/api";
import { StatsSidebar } from "../StatsSidebar";
import { pingPongEnvelopViewRenderDiv as renderReactDiv } from "@kie-tools-examples/ping-pong-view-react";
import { pingPongEnvelopViewRenderDiv as renderAngularDiv } from "@kie-tools-examples/ping-pong-view-angular/dist/wc/lib";
import { usePingPongApiCallbacks, usePingPongChannelApi } from "../hooks";

const reactEnvelopePath = "envelope/ping-pong-view-react-impl.html";
const angularEnvelopePath = "envelope/angular/index.html";

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
          <PageSection style={{ flex: "1 1 25%" }}>
            <EmbeddedIFramePingPong
              apiImpl={apiImpl}
              name={"Angular iFrame"}
              ref={angularIFrame}
              targetOrigin={window.location.origin}
              envelopePath={angularEnvelopePath}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1 25%" }}>
            <EmbeddedDivPingPong
              apiImpl={apiImpl}
              name={"Angular Div"}
              ref={angularDiv}
              targetOrigin={window.location.origin}
              renderView={renderAngularDiv}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1 25%" }}>
            <EmbeddedIFramePingPong
              apiImpl={apiImpl}
              name={"React iFrame"}
              ref={reactIFrame}
              targetOrigin={window.location.origin}
              envelopePath={reactEnvelopePath}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1 25%" }}>
            <EmbeddedDivPingPong
              apiImpl={apiImpl}
              name={"React Div"}
              ref={reactDiv}
              targetOrigin={window.location.origin}
              renderView={renderReactDiv}
            />
          </PageSection>
        </div>
      </div>
    </Page>
  );
}
