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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { Interaction } from "../../../types";

interface AttributesTableActionProps {
  index: number;
  onDelete: (interaction: Interaction) => void;
}

export const AttributesTableAction = (props: AttributesTableActionProps) => {
  const { index, onDelete } = props;

  const handleDelete = (e: React.MouseEvent | React.KeyboardEvent, interaction: Interaction) => {
    e.stopPropagation();
    e.preventDefault();
    if (onDelete) {
      onDelete(interaction);
    }
  };

  return (
    <Flex alignItems={{ default: "alignItemsCenter" }} style={{ height: "100%" }}>
      <FlexItem>
        <Button
          id={`attribute-n${index}__delete`}
          data-testid={`attribute-n${index}__delete`}
          // We cannot use the regular "editable-item__delete" as Attributes are nested inside a Characteristic that
          // is an "editable-item" and hence the ":focus-within" selector leads to the delete icon always being visible.
          className="attribute-item__delete"
          ouiaId="delete-attribute"
          variant="plain"
          onClick={(e) => handleDelete(e, "mouse")}
          onKeyDown={(event) => {
            if (event.key === "Enter") {
              handleDelete(event, "keyboard");
            }
          }}
        >
          <TrashIcon />
        </Button>
      </FlexItem>
    </Flex>
  );
};
