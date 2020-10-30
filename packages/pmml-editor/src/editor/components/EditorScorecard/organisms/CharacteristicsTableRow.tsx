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
import {
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Label
} from "@patternfly/react-core";
import "./CharacteristicsTable.scss";
import { CharacteristicsTableAction } from "../atoms";
import { IndexedCharacteristic } from "./CharacteristicsTable";

interface CharacteristicsTableRowProps {
  characteristic: IndexedCharacteristic;
  onEdit: () => void;
  onDelete: () => void;
  isDisabled: boolean;
}

export const CharacteristicsTableRow = (props: CharacteristicsTableRowProps) => {
  const { isDisabled, characteristic, onEdit, onDelete } = props;

  const index = characteristic.index;

  return (
    <DataListItem
      id={index?.toString()}
      className="characteristics__list-item"
      aria-labelledby={"characteristic-" + index}
    >
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={2}>
              <div>{characteristic.characteristic.name}</div>
            </DataListCell>,
            <DataListCell key="1" width={2}>
              <Label>{characteristic.characteristic.Attribute.length}</Label>
            </DataListCell>,
            <DataListCell key="2" width={2}>
              <div>{characteristic.characteristic.reasonCode}</div>
            </DataListCell>,
            <DataListCell key="3" width={2}>
              <div>{characteristic.characteristic.baselineScore}</div>
            </DataListCell>,
            <DataListAction
              id="characteristic-actions"
              aria-label="actions"
              aria-labelledby="characteristic-actions"
              key="4"
              width={1}
            >
              <CharacteristicsTableAction onEdit={() => onEdit()} onDelete={() => onDelete()} disabled={isDisabled} />
            </DataListAction>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};
