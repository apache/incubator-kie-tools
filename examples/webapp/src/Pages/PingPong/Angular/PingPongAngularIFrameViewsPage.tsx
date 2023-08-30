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
import { EmbeddedIFramePingPong } from "@kie-tools-examples/ping-pong-view/dist/embedded";
import { PingPongApi } from "@kie-tools-examples/ping-pong-view/dist/api";
import { StatsSidebar } from "../StatsSidebar";
import { usePingPongApiCallbacks, usePingPongChannelApi } from "../hooks";

const envelopePath = "envelope/angular/index.html";

export function PingPongAngularIFrameViewsPage() {
  const { pingsCount, pongsCount, lastPing, lastPong, apiImpl } = usePingPongChannelApi();

  const angular1 = useRef<PingPongApi>(null);
  const angular2 = useRef<PingPongApi>(null);
  const angular3 = useRef<PingPongApi>(null);

  const refs = useMemo(() => [angular1, angular2, angular3], [angular1, angular2, angular3]);

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
            <EmbeddedIFramePingPong
              apiImpl={apiImpl}
              name={"Angular 1"}
              ref={angular1}
              targetOrigin={window.location.origin}
              envelopePath={envelopePath}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1" }}>
            <EmbeddedIFramePingPong
              apiImpl={apiImpl}
              name={"Angular 2"}
              ref={angular2}
              targetOrigin={window.location.origin}
              envelopePath={envelopePath}
            />
          </PageSection>

          <PageSection style={{ flex: "1 1" }}>
            <EmbeddedIFramePingPong
              apiImpl={apiImpl}
              name={"Angular 3"}
              ref={angular3}
              targetOrigin={window.location.origin}
              envelopePath={envelopePath}
            />
          </PageSection>
        </div>
      </div>
    </Page>
  );
}
