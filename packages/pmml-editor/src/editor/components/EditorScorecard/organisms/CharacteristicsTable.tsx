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
  Form,
  FormGroup,
  Label,
  TextInput
} from "@patternfly/react-core";
import { ExclamationCircleIcon, TrashIcon } from "@patternfly/react-icons";
import "./CharacteristicsTable.scss";
import { EmptyStateNoCharacteristics } from "../molecules";
import { CharacteristicsTableAction, CharacteristicsTableEditModeAction } from "../atoms";
import { ValidatedType } from "../../../types";

export interface IndexedCharacteristic {
  index: number | undefined;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  isEditActive: boolean;
  setEditActive: (active: boolean) => void;
  characteristics: IndexedCharacteristic[];
  onRowClick: (index: number) => void;
  onRowDelete: (index: number) => void;
  onAddCharacteristic: () => void;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
  commitCharacteristicUpdate: (props: IndexedCharacteristic) => void;
}

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const { isEditActive, setEditActive, characteristics, onRowClick, onRowDelete, onAddCharacteristic } = props;

  const [selectedItemIndex, setSelectedItemIndex] = useState<number | undefined>(undefined);
  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [reasonCode, setReasonCode] = useState<string | undefined>();
  const [baselineScore, setBaselineScore] = useState<number | undefined>();

  const onSelectDataListItem = (id: string) => {
    if (editItemIndex !== undefined) {
      return;
    }
    const index: number | undefined = Number(id);
    setSelectedItemIndex(index);
    onRowClick(index);
  };

  const onEdit = (index: number | undefined) => {
    if (index !== undefined) {
      const characteristic = characteristics[index].characteristic;
      setName({
        value: characteristic.name,
        valid: props.validateCharacteristicName(index, characteristic.name)
      });
      setReasonCode(characteristic.reasonCode);
      setBaselineScore(characteristic.baselineScore);
    } else {
      setName({
        value: undefined,
        valid: props.validateCharacteristicName(index, undefined)
      });
      setReasonCode(undefined);
      setBaselineScore(undefined);
    }
    setEditItemIndex(index);
    setEditActive(true);
  };

  const onDelete = (index: number | undefined) => {
    if (index) {
      onRowDelete(index);
    }
  };

  const onCommit = (index: number | undefined) => {
    let characteristic: Characteristic;
    if (index === undefined) {
      characteristic = { Attribute: [] };
    } else {
      characteristic = characteristics[index].characteristic;
    }

    //Avoid commits with no change
    if (
      characteristic.name === name.value &&
      characteristic.baselineScore === baselineScore &&
      characteristic.reasonCode === reasonCode
    ) {
      return;
    }

    const _characteristic: Characteristic = Object.assign({}, characteristic, {
      name: name.value,
      baselineScore: baselineScore,
      reasonCode: reasonCode
    });
    const _indexedCharacteristic: IndexedCharacteristic = {
      index: index,
      characteristic: _characteristic
    };
    props.commitCharacteristicUpdate(_indexedCharacteristic);

    setEditItemIndex(undefined);
    setEditActive(false);
  };

  const onCancel = (index: number | undefined) => {
    setEditItemIndex(undefined);
    setEditActive(false);
  };

  const toNumber = (value: string): number | undefined => {
    if (value === "") {
      return undefined;
    }
    const n = Number(value);
    if (isNaN(n)) {
      return undefined;
    }
    return n;
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
            const c: Characteristic = ic.characteristic;
            return (
              <DataListItem
                key={index}
                id={ic.index?.toString()}
                className="characteristics__list-item"
                aria-labelledby={"characteristic-" + index}
              >
                <DataListItemRow style={{ minHeight: "64 px" }}>
                  {editItemIndex === ic.index && (
                    <DataListItemCells
                      dataListCells={[
                        <DataListCell key="0" width={2}>
                          <FormGroup
                            fieldId="characteristic-form-name-helper"
                            helperText="Please provide a name for the Characteristic."
                            helperTextInvalid="Name must be unique and present"
                            helperTextInvalidIcon={<ExclamationCircleIcon />}
                            validated={name.valid ? "default" : "error"}
                          >
                            <TextInput
                              type="text"
                              id="characteristic-name"
                              name="characteristic-name"
                              aria-describedby="characteristic-name-helper"
                              value={name.value ?? ""}
                              validated={name.valid ? "default" : "error"}
                              onChange={e =>
                                setName({
                                  value: e,
                                  valid: props.validateCharacteristicName(index, e)
                                })
                              }
                            />
                          </FormGroup>
                        </DataListCell>,
                        <DataListCell key="1" width={2}>
                          <Label>{c.Attribute.length}</Label>
                        </DataListCell>,
                        <DataListCell key="2" width={2}>
                          <FormGroup
                            fieldId="characteristic-reason-code-helper"
                            helperText="A Reason Code is mapped to a Business reason."
                          >
                            <TextInput
                              type="text"
                              id="characteristic-reason-code"
                              name="characteristic-reason-code"
                              aria-describedby="characteristic-reason-code-helper"
                              value={reasonCode ?? ""}
                              onChange={e => setReasonCode(e)}
                            />
                          </FormGroup>
                        </DataListCell>,
                        <DataListCell key="3" width={2}>
                          <FormGroup
                            fieldId="characteristic-baseline-score-helper"
                            helperText="Helps to determine the ranking of Reason Codes."
                          >
                            <TextInput
                              type="number"
                              id="characteristic-baseline-score"
                              name="characteristic-baseline-score"
                              aria-describedby="characteristic-baseline-score-helper"
                              value={baselineScore ?? ""}
                              onChange={e => setBaselineScore(toNumber(e))}
                            />
                          </FormGroup>
                        </DataListCell>,
                        <DataListAction
                          id="characteristic-actions"
                          aria-label="actions"
                          aria-labelledby="characteristic-actions"
                          key="4"
                          width={1}
                        >
                          <CharacteristicsTableEditModeAction
                            onCommit={() => onCommit(ic.index)}
                            onCancel={() => onCancel(ic.index)}
                            disableCommit={!name.valid}
                          />
                        </DataListAction>
                      ]}
                    />
                  )}
                  {editItemIndex !== ic.index && (
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
                          <CharacteristicsTableAction
                            onEdit={() => onEdit(ic.index)}
                            onDelete={() => onDelete(ic.index)}
                            disabled={!(editItemIndex === undefined || editItemIndex === ic.index) || isEditActive}
                          />
                        </DataListAction>
                      ]}
                    />
                  )}
                </DataListItemRow>
              </DataListItem>
            );
          })}
        </DataList>
      </Form>
      {characteristics.length === 0 && <EmptyStateNoCharacteristics createCharacteristic={onAddCharacteristic} />}
    </div>
  );
};
