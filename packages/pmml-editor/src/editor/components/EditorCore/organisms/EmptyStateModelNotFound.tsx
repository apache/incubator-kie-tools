/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { EmptyState, EmptyStateBody, EmptyStateIcon, EmptyStateVariant, Title } from "@patternfly/react-core";
import { ExclamationTriangleIcon } from "@patternfly/react-icons";

export const EmptyStateModelNotFound = () => (
  <EmptyState data-testid="empty-state-model-not-found" variant={EmptyStateVariant.small}>
    <EmptyStateIcon icon={ExclamationTriangleIcon} />
    <Title headingLevel="h4" size="lg">
      Not found.
    </Title>
    <EmptyStateBody>Something went wrong. An attempt has been made to view a Model that does not exist.</EmptyStateBody>
  </EmptyState>
);
