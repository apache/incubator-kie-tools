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
import { CharacteristicsTableAction } from "../atoms";

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

  const [selectedDataListItemId, setSelectedDataListItemId] = useState("");

  const onSelectDataListItem = (id: string) => {
    setSelectedDataListItemId(id);
    onRowClick(Number(id));
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
                  id="delete-characteristic-header"
                  aria-label="delete header"
                  aria-labelledby="delete-characteristic-header"
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
        selectedDataListItemId={selectedDataListItemId}
        onSelectDataListItem={onSelectDataListItem}
      >
        {characteristics.map((ic, index) => {
          const c: Characteristic = ic.characteristic;
          return (
            <DataListItem
              key={index}
              id={index.toString()}
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
                      id="delete-characteristic"
                      aria-label="delete"
                      aria-labelledby="delete-characteristic"
                      key="4"
                      width={1}
                    >
                      <CharacteristicsTableAction onEdit={() => null} onDelete={() => onRowDelete(index)} />
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
