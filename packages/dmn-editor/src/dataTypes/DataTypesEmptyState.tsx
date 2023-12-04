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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
  EmptyStateSecondaryActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { PasteIcon } from "@patternfly/react-icons/dist/js/icons/paste-icon";

export function DataTypesEmptyState({ onAdd, onPaste }: { onAdd: () => void; onPaste: () => void }) {
  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateIcon icon={CubesIcon} />
        <Title size={"lg"} headingLevel={"h4"}>
          {`No custom data types have been defined.`}
        </Title>
        <EmptyStateBody>
          {`Data types are referenced in the input and output values for decision tables. Custom data types allow you to reference more complex data types, beyond the simple "default" types.`}
        </EmptyStateBody>
        <br />
        <EmptyStatePrimary>
          <Button variant={ButtonVariant.primary} onClick={onAdd}>
            Create a custom data type
          </Button>
        </EmptyStatePrimary>
        <br />
        <br />
        or
        <br />
        <EmptyStateSecondaryActions>
          <Button variant={ButtonVariant.link} onClick={onPaste} icon={<PasteIcon />}>
            Paste data type
          </Button>
        </EmptyStateSecondaryActions>
      </EmptyState>
    </Flex>
  );
}
