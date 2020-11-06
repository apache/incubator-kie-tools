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
import { Button, Flex, FlexItem } from "@patternfly/react-core";
import { EditAltIcon, TrashIcon } from "@patternfly/react-icons";

interface OutputsTableActionProps {
  disabled: boolean;
  onEdit: () => void;
  onDelete: () => void;
}

export const OutputsTableAction = (props: OutputsTableActionProps) => {
  const { disabled, onEdit, onDelete } = props;

  return (
    <Flex alignItems={{ default: "alignItemsCenter" }} style={{ height: "100%" }}>
      <FlexItem>
        <Button variant="plain" onClick={e => onEdit()} isDisabled={disabled}>
          <EditAltIcon />
        </Button>
        <Button variant="plain" onClick={e => onDelete()} isDisabled={disabled}>
          <TrashIcon />
        </Button>
      </FlexItem>
    </Flex>
  );
};
