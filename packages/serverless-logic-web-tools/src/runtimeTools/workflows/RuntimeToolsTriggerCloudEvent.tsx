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

import React from "react";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useHistory } from "react-router";
import CloudEventFormContainer from "./CloudEventFormContainer/CloudEventFormContainer";

const PAGE_TITLE = "Trigger Cloud Event";

export function RuntimeToolsTriggerCloudEvent() {
  const history = useHistory();

  return (
    <>
      <Page>
        <PageSection variant={"light"}>
          <TextContent>
            <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
            <Text component={TextVariants.p}>
              Trigger a cloud event to start new workflow instances or to send HTTP Cloud Events to active workflow
              instances that are waiting for an event to advance.
            </Text>
          </TextContent>
        </PageSection>

        <PageSection isFilled aria-label="trigger-cloud-event-section">
          <CloudEventFormContainer isTriggerNewInstance={true} />
        </PageSection>
      </Page>
    </>
  );
}
