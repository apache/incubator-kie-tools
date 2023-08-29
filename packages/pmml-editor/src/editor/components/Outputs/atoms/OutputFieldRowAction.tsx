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
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { Interaction } from "../../../types";

interface OutputsTableActionProps {
  index: number;
  onDelete: (interaction: Interaction) => void;
}

export const OutputFieldRowAction = (props: OutputsTableActionProps) => {
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
          id={`output-field-n${index}__delete`}
          data-testid={`output-field-n${index}__delete`}
          className="editable-item__delete"
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
