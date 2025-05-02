/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import React from "react";
import { OUIAProps, componentOuiaProps } from "../../ouiaTools";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { EmptyState, EmptyStateIcon, EmptyStateHeader, EmptyStateFooter } from "@patternfly/react-core/components";
import { ClusterIcon } from "@patternfly/react-icons/dist/js/icons/cluster-icon";

export const KeycloakUnavailablePage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  return (
    <Page>
      <PageSection>
        <Bullseye>
          <EmptyState {...componentOuiaProps(ouiaId, "server-unavailable", ouiaSafe)}>
            <EmptyStateHeader
              titleText="503: We couldn't contact the server"
              icon={<EmptyStateIcon icon={ClusterIcon} />}
              headingLevel="h1"
            />
            <EmptyStateFooter>
              <Text component={TextVariants.blockquote}>
                We could not reach the server, you can contact the administrator or try to reload the page by clicking
                on the button below.
              </Text>
              <Button variant="primary" onClick={() => window.location.reload()} isInline>
                Retry
              </Button>
            </EmptyStateFooter>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </Page>
  );
};
