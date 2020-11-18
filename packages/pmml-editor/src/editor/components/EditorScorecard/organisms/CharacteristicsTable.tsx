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
import { Form } from "@patternfly/react-core";
import { CharacteristicsTableEditRow, CharacteristicsTableRow } from "../molecules";
import { Operation } from "../Operation";

export interface IndexedCharacteristic {
  index: number | undefined;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  characteristics: IndexedCharacteristic[];
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
  viewAttributes: (index: number | undefined) => void;
  deleteCharacteristic: (index: number) => void;
  commit: (
    index: number | undefined,
    name: string | undefined,
    reasonCode: string | undefined,
    baselineScore: number | undefined
  ) => void;
}

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const {
    activeOperation,
    setActiveOperation,
    characteristics,
    validateCharacteristicName,
    viewAttributes,
    deleteCharacteristic,
    commit
  } = props;

  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);
  const addCharacteristicRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.CREATE_CHARACTERISTIC && addCharacteristicRowRef.current) {
      addCharacteristicRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onEdit = (index: number | undefined) => {
    setEditItemIndex(index);
    setActiveOperation(Operation.UPDATE);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      deleteCharacteristic(index);
    }
  };

  const onViewAttributes = (index: number | undefined): void => {
    viewAttributes(index);
  };

  const onValidateCharacteristicName = (index: number | undefined, name: string | undefined): boolean => {
    return validateCharacteristicName(index, name);
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
      commit(index, name, reasonCode, baselineScore);
    }

    onCancel();
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  return (
    <Form>
      <section>
        {characteristics.map(ic => {
          if (editItemIndex === ic.index) {
            return (
              <CharacteristicsTableEditRow
                key={ic.index}
                setActiveOperation={setActiveOperation}
                characteristic={ic}
                viewAttributes={() => onViewAttributes(ic.index)}
                validateCharacteristicName={_name => onValidateCharacteristicName(ic.index, _name)}
                onCommit={(_name, _reasonCode, _baselineScore) =>
                  onCommit(ic.index, _name, _reasonCode, _baselineScore)
                }
                onDelete={() => onDelete(ic.index)}
                onCancel={onCancel}
              />
            );
          } else {
            return (
              <CharacteristicsTableRow
                key={ic.index}
                characteristic={ic}
                onEdit={() => onEdit(ic.index)}
                onDelete={() => onDelete(ic.index)}
              />
            );
          }
        })}
        {activeOperation === Operation.CREATE_CHARACTERISTIC && (
          <div key={undefined} ref={addCharacteristicRowRef}>
            <CharacteristicsTableEditRow
              key={"add"}
              setActiveOperation={setActiveOperation}
              characteristic={{ index: undefined, characteristic: { Attribute: [] } }}
              viewAttributes={() => onViewAttributes(undefined)}
              validateCharacteristicName={_name => onValidateCharacteristicName(undefined, _name)}
              onCommit={(_name, _reasonCode, _baselineScore) => onCommit(undefined, _name, _reasonCode, _baselineScore)}
              onCancel={onCancel}
            />
          </div>
        )}
      </section>
    </Form>
  );
};
