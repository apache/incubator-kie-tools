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
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useApp } from "../context/AppContext";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { AppToolbar } from "../components/AppToolbar";
import { ErrorPage } from "./ErrorPage";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { DashboardViewer } from "../components/DashboardViewer";

export function HomePage() {
  const app = useApp();

  return (
    <Page>
      <PromiseStateWrapper
        promise={app.appDataPromise}
        pending={<LoadingSpinner />}
        rejected={(_errors) => <ErrorPage />}
        resolved={(data) =>
          app.current && (
            <Page header={<AppToolbar dashboard={app.current} showDisclaimer={!!data.showDisclaimer} />}>
              <PageSection padding={{ default: "noPadding" }} isFilled={true} hasOverflowScroll={false}>
                <DashboardViewer dashboard={app.current} />
              </PageSection>
            </Page>
          )
        }
      />
    </Page>
  );
}
