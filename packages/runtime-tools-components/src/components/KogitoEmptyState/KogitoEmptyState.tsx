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
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/components";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { SearchIcon } from "@patternfly/react-icons/dist/js/icons/search-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { OUIAProps, componentOuiaProps } from "../../ouiaTools";

export enum KogitoEmptyStateType {
  Search,
  Refresh,
  Reset,
  Info,
}

interface IOwnProps {
  type: KogitoEmptyStateType;
  title: string;
  body: string;
  onClick?: () => void;
}

export const KogitoEmptyState: React.FC<IOwnProps & OUIAProps> = ({ type, title, body, onClick, ouiaId, ouiaSafe }) => {
  return (
    <Bullseye {...componentOuiaProps(ouiaId, "kogito-empty-state", ouiaSafe)}>
      <EmptyState variant={EmptyStateVariant.full}>
        {type === KogitoEmptyStateType.Search && <EmptyStateIcon icon={SearchIcon} />}
        {(type === KogitoEmptyStateType.Refresh || type === KogitoEmptyStateType.Reset) && (
          <EmptyStateIcon icon={ExclamationTriangleIcon} color="var(--pf-v5-global--warning-color--100)" />
        )}
        {type === KogitoEmptyStateType.Info && (
          <EmptyStateIcon icon={InfoCircleIcon} color="var(--pf-v5-global--info-color--100)" />
        )}

        <EmptyStateHeader titleText={<>{title}</>} headingLevel="h5" />

        <EmptyStateBody>{body}</EmptyStateBody>
        <EmptyStateFooter>
          {type === KogitoEmptyStateType.Refresh && (
            <Button variant="primary" onClick={onClick}>
              Refresh
            </Button>
          )}

          {type === KogitoEmptyStateType.Reset && (
            <Button variant="link" onClick={onClick}>
              Reset to default
            </Button>
          )}
        </EmptyStateFooter>
      </EmptyState>
    </Bullseye>
  );
};

export default KogitoEmptyState;
