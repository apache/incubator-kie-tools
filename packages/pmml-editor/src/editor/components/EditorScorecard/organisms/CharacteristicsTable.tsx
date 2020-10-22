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
import { useCallback, useState } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import {
  Button,
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Label
} from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import "./CharacteristicsTable.scss";
import { EmptyStateNoCharacteristics } from "./EmptyStateNoCharacteristics";

export interface IndexedCharacteristic {
  index: number;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  characteristics: IndexedCharacteristic[];
  selectedCharacteristic: IndexedCharacteristic | undefined;
  onRowClick: (index: number) => void;
  onRowDelete: (index: number) => void;
  onAddCharacteristic: () => void;
}

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const { characteristics, selectedCharacteristic, onRowClick, onRowDelete, onAddCharacteristic } = props;

  const [selectedDataListItemId, setSelectedDataListItemId] = useState("");

  const onSelectDataListItem = (id: string) => {
    setSelectedDataListItemId(id);
    onRowClick(Number(id));
  };

  const onDeleteCharacteristic = useCallback(
    (event, index) => {
      event.stopPropagation();
      onRowDelete(index);
    },
    [characteristics]
  );

  return (
    <div>
      <table className="characteristics__table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Attributes</th>
            <th>Reason Code</th>
            <th>Baseline score</th>
            <th>&nbsp;</th>
          </tr>
        </thead>
      </table>
      <React.Fragment>
        <DataList
          aria-label="selectable data list example"
          selectedDataListItemId={selectedDataListItemId}
          onSelectDataListItem={onSelectDataListItem}
        >
          {characteristics.map((ic, index) => {
            const c: Characteristic = ic.characteristic;
            return (
              <DataListItem
                key={index}
                id={index.toString()}
                style={{ outline: "none" }}
                aria-labelledby={"selectable-item" + index}
              >
                <DataListItemRow>
                  <DataListItemCells
                    dataListCells={[
                      <DataListCell key="0">
                        <div>{c.name}</div>
                      </DataListCell>,
                      <DataListCell key="1">
                        <Label>{c.Attribute.length}</Label>
                      </DataListCell>,
                      <DataListCell key="2">
                        <div>{c.reasonCode}</div>
                      </DataListCell>,
                      <DataListCell key="3">
                        <div>{c.baselineScore}</div>
                      </DataListCell>,
                      <DataListCell key="4">
                        <Button variant="link" icon={<TrashIcon />} onClick={e => onDeleteCharacteristic(e, index)}>
                          &nbsp;
                        </Button>
                      </DataListCell>
                    ]}
                  />
                </DataListItemRow>
              </DataListItem>
            );
          })}
        </DataList>
      </React.Fragment>
      {characteristics.length === 0 && <EmptyStateNoCharacteristics createCharacteristic={onAddCharacteristic} />}
    </div>
  );
};
