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
import { CalculatorIcon } from "@patternfly/react-icons/dist/js/icons/calculator-icon";

interface EmptyStateNoCharacteristicsProps {
  addCharacteristic: () => void;
}

export const EmptyStateNoCharacteristics = (props: EmptyStateNoCharacteristicsProps) => (
  <EmptyState data-testid="empty-state-no-characteristics" variant={EmptyStateVariant.small}>
    <EmptyStateIcon icon={CalculatorIcon} />
    <Title headingLevel="h4" size="lg" ouiaId="no-characteristics-defined-title">
      No Characteristics defined
    </Title>
    <EmptyStateBody>
      {`Characteristics define the point allocation strategy for the scorecard. Once point allocation between input
      attributes and partial scores takes place, each scorecard characteristic is assigned a single partial score which
      is used to compute the overall score. The overall score is simply the sum of all partial scores. Partial scores
      are assumed to be continuous values of type "double".`}
    </EmptyStateBody>
    <Button
      data-testid="empty-state-no-characteristics__create-characteristic"
      variant="primary"
      onClick={props.addCharacteristic}
      ouiaId="add-characteristic"
    >
      Add Characteristic
    </Button>
  </EmptyState>
);
