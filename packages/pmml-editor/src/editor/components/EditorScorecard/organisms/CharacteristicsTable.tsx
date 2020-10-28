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
import { useState } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import {
  Button,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Label
} from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import "./CharacteristicsTable.scss";
import { EmptyStateNoCharacteristics } from "../molecules";
import { CharacteristicsTableAction, CharacteristicsTableEditModeAction } from "../atoms";

export interface IndexedCharacteristic {
  index: number | undefined;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  characteristics: IndexedCharacteristic[];
  onRowClick: (index: number) => void;
  onRowDelete: (index: number) => void;
  onAddCharacteristic: () => void;
}

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const { characteristics, onRowClick, onRowDelete, onAddCharacteristic } = props;

  const [selectedItemIndex, setSelectedItemIndex] = useState<number | undefined>(undefined);
  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);

  const onSelectDataListItem = (id: string) => {
    const index: number | undefined = Number(id);
    setSelectedItemIndex(index);
    onRowClick(index);
  };

  const onEdit = (index: number | undefined) => {
    setEditItemIndex(index);
  };

  const onDelete = (index: number | undefined) => {
    if (index) {
      onRowDelete(index);
    }
  };

  return (
    <div>
      <DataList className="characteristics__header" aria-label="characteristics header">
        <DataListItem className="characteristics__header__row" key={"none"} aria-labelledby="characteristics-header">
          <DataListItemRow>
            <DataListItemCells
              dataListCells={[
                <DataListCell key="0" width={2}>
                  <div>Name</div>
                </DataListCell>,
                <DataListCell key="1" width={2}>
                  <div>Attributes</div>
                </DataListCell>,
                <DataListCell key="2" width={2}>
                  <div>Reason Code</div>
                </DataListCell>,
                <DataListCell key="3" width={2}>
                  <div>Baseline Score</div>
                </DataListCell>,
                <DataListAction
                  id="characteristic-actions-header"
                  aria-label="actions header"
                  aria-labelledby="characteristic-actions-header"
                  key="4"
                  width={1}
                >
                  {/*This is a hack to ensure the column layout is correct*/}
                  <Button variant="link" icon={<TrashIcon />} isDisabled={true} style={{ visibility: "hidden" }}>
                    &nbsp;
                  </Button>
                </DataListAction>
              ]}
            />
          </DataListItemRow>
        </DataListItem>
      </DataList>
      <DataList
        aria-label="characteristics list"
        selectedDataListItemId={selectedItemIndex?.toString()}
        onSelectDataListItem={onSelectDataListItem}
      >
        {characteristics.map((ic, index) => {
          const c: Characteristic = ic.characteristic;
          return (
            <DataListItem
              key={index}
              id={ic.index?.toString()}
              className="characteristics__list-item"
              aria-labelledby={"characteristic-" + index}
            >
              <DataListItemRow>
                <DataListItemCells
                  dataListCells={[
                    <DataListCell key="0" width={2}>
                      <div>{c.name}</div>
                    </DataListCell>,
                    <DataListCell key="1" width={2}>
                      <Label>{c.Attribute.length}</Label>
                    </DataListCell>,
                    <DataListCell key="2" width={2}>
                      <div>{c.reasonCode}</div>
                    </DataListCell>,
                    <DataListCell key="3" width={2}>
                      <div>{c.baselineScore}</div>
                    </DataListCell>,
                    <DataListAction
                      id="characteristic-actions"
                      aria-label="actions"
                      aria-labelledby="characteristic-actions"
                      key="4"
                      width={1}
                    >
                      {editItemIndex === ic.index && (
                        <CharacteristicsTableEditModeAction onCommit={() => null} onCancel={() => null} />
                      )}
                      {editItemIndex !== ic.index && (
                        <CharacteristicsTableAction
                          onEdit={() => onEdit(ic.index)}
                          onDelete={() => onDelete(ic.index)}
                          disabled={!(editItemIndex === undefined || editItemIndex === ic.index)}
                        />
                      )}
                    </DataListAction>
                  ]}
                />
              </DataListItemRow>
            </DataListItem>
          );
        })}
      </DataList>
      {characteristics.length === 0 && <EmptyStateNoCharacteristics createCharacteristic={onAddCharacteristic} />}
    </div>
  );
};
