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
import { Flex, FlexItem, Label } from "@patternfly/react-core";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputLabels, OutputsTableAction } from "../atoms";
import "./OutputsTableRow.scss";

interface OutputsTableRowProps {
  index: number;
  output: OutputField;
  onEdit: () => void;
  onDelete: () => void;
  isDisabled: boolean;
}

export const OutputsTableRow = (props: OutputsTableRowProps) => {
  const { index, output, onEdit, onDelete, isDisabled } = props;

  return (
    <article className={`output-item output-item-n${index}`}>
      <Flex alignItems={{ default: "alignItemsCenter" }} style={{ height: "100%" }}>
        <FlexItem>
          <strong>{output.name}</strong>
        </FlexItem>
        <FlexItem>
          <Label color="blue" className="output-item__type-label">
            {output.dataType}
          </Label>
        </FlexItem>
        <FlexItem>
          <OutputLabels output={output} />
        </FlexItem>
        <FlexItem align={{ default: "alignRight" }}>
          <OutputsTableAction onEdit={() => onEdit()} onDelete={() => onDelete()} disabled={isDisabled} />
        </FlexItem>
      </Flex>
    </article>
  );
};
