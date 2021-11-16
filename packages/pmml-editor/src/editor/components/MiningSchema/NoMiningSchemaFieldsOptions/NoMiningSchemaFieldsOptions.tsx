/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { BoxesIcon } from "@patternfly/react-icons/dist/js/icons/boxes-icon";

const NoMiningSchemaFieldsOptions = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={BoxesIcon} />
      <Title headingLevel="h4" size="lg" ouiaId="mining-schema-no-data-fields-title">
        No Data Fields defined
      </Title>
      <EmptyStateBody>
        Mining schema can only include fields from the Data Dictionary. It seems there are none yet. Go to the Data
        Dictionary and come back after creating them.
      </EmptyStateBody>
    </EmptyState>
  );
};

export default NoMiningSchemaFieldsOptions;
