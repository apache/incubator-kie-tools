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
import { DataListAction, DataListCell, DataListItem, DataListItemCells, DataListItemRow } from "@patternfly/react-core";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import "../organisms/OutputsTable.scss";
import { OutputsTableAction } from "../atoms";

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
    <DataListItem key={index} id={index.toString()} className="outputs__list-item" aria-labelledby={"output-" + index}>
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={4}>
              <div>{output.name}</div>
            </DataListCell>,
            <DataListCell key="1" width={2}>
              <div>{output.optype}</div>
            </DataListCell>,
            <DataListCell key="2" width={2}>
              <div>{output.dataType}</div>
            </DataListCell>,
            <DataListCell key="3" width={2}>
              <div>{output.targetField}</div>
            </DataListCell>,
            <DataListCell key="4" width={2}>
              <div>{output.feature}</div>
            </DataListCell>,
            <DataListCell key="5" width={2}>
              <div>{output.value}</div>
            </DataListCell>,
            <DataListCell key="6" width={2}>
              <div>{output.rank}</div>
            </DataListCell>,
            <DataListCell key="7" width={2}>
              <div>{output.rankOrder}</div>
            </DataListCell>,
            <DataListCell key="8" width={2}>
              <div>{output.segmentId}</div>
            </DataListCell>,
            <DataListCell key="9" width={2}>
              <div>{output.isFinalResult}</div>
            </DataListCell>,
            <DataListAction id="delete-output" aria-label="delete" aria-labelledby="delete-output" key="10" width={1}>
              <OutputsTableAction onEdit={() => onEdit()} onDelete={() => onDelete()} disabled={isDisabled} />
            </DataListAction>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};
