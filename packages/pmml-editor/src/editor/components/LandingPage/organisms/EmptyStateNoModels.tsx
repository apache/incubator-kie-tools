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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { BoxesIcon } from "@patternfly/react-icons/dist/js/icons/boxes-icon";

interface EmptyStateNoModelsProps {
  createModel: () => void;
}

export const EmptyStateNoModels = (props: EmptyStateNoModelsProps) => (
  <EmptyState data-testid="empty-state-no-models" variant={EmptyStateVariant.small}>
    <EmptyStateIcon icon={BoxesIcon} />
    <Title headingLevel="h4" size="lg">
      {"You don't have any PMML Models"}
    </Title>
    <EmptyStateBody>
      PMML uses XML to represent predictive models. One or more predictive models can be contained in a PMML document.
    </EmptyStateBody>
    <Button data-testid="empty-state-no-models__create-model" variant="primary" onClick={props.createModel}>
      Create Model
    </Button>
  </EmptyState>
);
