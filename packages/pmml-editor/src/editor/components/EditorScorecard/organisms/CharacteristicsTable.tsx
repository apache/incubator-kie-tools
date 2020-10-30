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
import { useEffect, useRef, useState } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import {
  Button,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Form
} from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import "./CharacteristicsTable.scss";
import { EmptyStateNoCharacteristics } from "../molecules";
import { CharacteristicsTableEditRow } from "../molecules/CharacteristicsTableEditRow";
import { CharacteristicsTableRow } from "../molecules/CharacteristicsTableRow";
import { Operation } from "../../../types/Operation";

export interface IndexedCharacteristic {
  index: number | undefined;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  characteristics: IndexedCharacteristic[];
  onRowClick: (index: number) => void;
  onRowDelete: (index: number) => void;
  onAddCharacteristic: () => void;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
  commit: (
    index: number | undefined,
    name: string | undefined,
    reasonCode: string | undefined,
    baselineScore: number | undefined
  ) => void;
}

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const { activeOperation, setActiveOperation, characteristics, onRowClick, onRowDelete, onAddCharacteristic } = props;

  const [selectedItemIndex, setSelectedItemIndex] = useState<number | undefined>(undefined);
  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);

  const addCharacteristicRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.CREATE && addCharacteristicRowRef.current) {
      addCharacteristicRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onSelectDataListItem = (id: string) => {
    if (editItemIndex !== undefined) {
      return;
    }
    const index: number | undefined = Number(id);
    setSelectedItemIndex(index);
    onRowClick(index);
  };

  const onEdit = (index: number | undefined) => {
    setEditItemIndex(index);
    setActiveOperation(Operation.UPDATE);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      onRowDelete(index);
    }
  };

  const onValidateCharacteristicName = (index: number | undefined, name: string | undefined): boolean => {
    return props.validateCharacteristicName(index, name);
  };

  const onCommit = (
    index: number | undefined,
    name: string | undefined,
    reasonCode: string | undefined,
    baselineScore: number | undefined
  ) => {
    //Avoid commits with no change
    let characteristic: Characteristic;
    if (index === undefined) {
      characteristic = { Attribute: [] };
    } else {
      characteristic = characteristics[index].characteristic;
    }
    if (
      characteristic.name !== name ||
      characteristic.baselineScore !== baselineScore ||
      characteristic.reasonCode !== reasonCode
    ) {
      props.commit(index, name, reasonCode, baselineScore);
    }

    onCancel();
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
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
      <Form>
        <DataList
          aria-label="characteristics list"
          selectedDataListItemId={selectedItemIndex?.toString()}
          onSelectDataListItem={onSelectDataListItem}
        >
          {characteristics.map((ic, index) => {
            if (editItemIndex === ic.index) {
              return (
                <CharacteristicsTableEditRow
                  key={index}
                  characteristic={ic}
                  validateCharacteristicName={_name => onValidateCharacteristicName(ic.index, _name)}
                  onCommit={(_name, _reasonCode, _baselineScore) =>
                    onCommit(ic.index, _name, _reasonCode, _baselineScore)
                  }
                  onCancel={() => onCancel()}
                />
              );
            } else {
              return (
                <div key={index} ref={addCharacteristicRowRef}>
                  <CharacteristicsTableRow
                    characteristic={ic}
                    onEdit={() => onEdit(ic.index)}
                    onDelete={() => onDelete(ic.index)}
                    isDisabled={
                      !(editItemIndex === undefined || editItemIndex === ic.index) || activeOperation !== Operation.NONE
                    }
                  />
                </div>
              );
            }
          })}
          {activeOperation === Operation.CREATE && (
            <CharacteristicsTableEditRow
              key={undefined}
              characteristic={{ index: undefined, characteristic: { Attribute: [] } }}
              validateCharacteristicName={_name => onValidateCharacteristicName(undefined, _name)}
              onCommit={(_name, _reasonCode, _baselineScore) => onCommit(undefined, _name, _reasonCode, _baselineScore)}
              onCancel={() => onCancel()}
            />
          )}
        </DataList>
      </Form>
      {characteristics.length === 0 && <EmptyStateNoCharacteristics createCharacteristic={onAddCharacteristic} />}
    </div>
  );
};
