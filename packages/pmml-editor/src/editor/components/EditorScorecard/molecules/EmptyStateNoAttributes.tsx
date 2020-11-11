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
import { Button, EmptyState, EmptyStateBody, EmptyStateIcon, EmptyStateVariant, Title } from "@patternfly/react-core";
import { DiceSixIcon } from "@patternfly/react-icons";

interface EmptyStateNoAttributesProps {
  createAttribute: () => void;
}

export const EmptyStateNoAttributes = (props: EmptyStateNoAttributesProps) => (
  <EmptyState data-testid="empty-state-no-attributes" variant={EmptyStateVariant.small}>
    <EmptyStateIcon icon={DiceSixIcon} />
    <Title headingLevel="h4" size="lg">
      No Attributes defined
    </Title>
    <EmptyStateBody>
      Input attributes for each scorecard characteristic are defined in terms of predicates. For numeric
      characteristics, predicates are used to implement the mapping from a range of continuous values to a partial
      score. For example, age range 20 to 29 may map to partial score "15". For categorical characteristics, predicates
      are used to implement the mapping of categorical values to partial scores.
    </EmptyStateBody>
    <Button data-testid="empty-state-no-attributes__create-attribute" variant="primary" onClick={props.createAttribute}>
      Add Attribute
    </Button>
  </EmptyState>
);
