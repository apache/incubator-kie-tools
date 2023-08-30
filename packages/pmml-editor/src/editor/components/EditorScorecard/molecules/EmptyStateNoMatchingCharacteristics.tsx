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
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { CalculatorIcon } from "@patternfly/react-icons/dist/js/icons/calculator-icon";

export const EmptyStateNoMatchingCharacteristics = () => (
  <EmptyState data-testid="empty-state-no-characteristics" variant={EmptyStateVariant.small}>
    <EmptyStateIcon icon={CalculatorIcon} />
    <Title headingLevel="h4" size="lg">
      No Characteristics match filter
    </Title>
    <EmptyStateBody>
      There are no Characteristics that match the name entered in the filter. Please amend or remove the filter and try
      again.
    </EmptyStateBody>
  </EmptyState>
);
