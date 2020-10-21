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
import { useCallback } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { Button, Label } from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import "./CharacteristicsTable.scss";

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

  const onDeleteCharacteristic = useCallback(
    (event, index) => {
      event.stopPropagation();
      onRowDelete(index);
    },
    [characteristics]
  );

  return (
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
      <tbody>
        {characteristics.map((ic, index) => {
          const c: Characteristic = ic.characteristic;
          const additionalClassName =
            selectedCharacteristic?.index === index ? "characteristics__table__row__selected" : "";
          return (
            <tr key={index} className={additionalClassName} onClick={e => onRowClick(index)}>
              <td>{c.name}</td>
              <td>
                <Label>{c.Attribute.length}</Label>
              </td>
              <td>{c.reasonCode}</td>
              <td>{c.baselineScore}</td>
              <td>
                <Button variant="link" icon={<TrashIcon />} onClick={e => onDeleteCharacteristic(e, index)}>
                  &nbsp;
                </Button>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
};
