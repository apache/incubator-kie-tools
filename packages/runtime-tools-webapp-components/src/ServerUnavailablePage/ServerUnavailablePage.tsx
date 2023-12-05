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
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateBody,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface Props {
  displayName?: string;
  reload: () => void;
}

export const ServerUnavailablePage: React.FC<Props & OUIAProps> = ({ displayName, reload, ouiaId, ouiaSafe }) => {
  const name = displayName || process.env.KOGITO_APP_NAME;

  return (
    <PageSection variant="light" {...componentOuiaProps(ouiaId, "server-unavailable", ouiaSafe)}>
      <Bullseye>
        <EmptyState variant={EmptyStateVariant.full}>
          <EmptyStateIcon icon={ExclamationCircleIcon} color="var(--pf-global--danger-color--100)" />
          <Title headingLevel="h1" size="4xl">
            Error connecting server
          </Title>
          <EmptyStateBody data-testid="empty-state-body">
            {`The ${name} could not access the server to display content.`}
          </EmptyStateBody>
          <EmptyStateBody data-testid="empty-state-body">
            Try reloading the page, or contact your administrator for more information.
          </EmptyStateBody>
          <Button variant="primary" onClick={reload} data-testid="refresh-button">
            Refresh
          </Button>
        </EmptyState>
      </Bullseye>
    </PageSection>
  );
};
