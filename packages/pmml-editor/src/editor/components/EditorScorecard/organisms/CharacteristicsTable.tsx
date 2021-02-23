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
import { useEffect, useRef } from "react";
import { Characteristic, DataField, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Form } from "@patternfly/react-core";
import { CharacteristicsTableEditRow, CharacteristicsTableRow } from "../molecules";
import { Operation } from "../Operation";
import { useSelector } from "react-redux";
import { useOperation } from "../OperationContext";

export interface IndexedCharacteristic {
  index: number;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  modelIndex: number;
  areReasonCodesUsed: boolean;
  scorecardBaselineScore: number | undefined;
  characteristics: IndexedCharacteristic[];
  characteristicsUnfilteredLength: number;
  selectedCharacteristicIndex: number | undefined;
  setSelectedCharacteristicIndex: (index: number | undefined) => void;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
  viewAttribute: (index: number | undefined) => void;
  deleteCharacteristic: (index: number) => void;
  onAddAttribute: () => void;
  onCommitAndClose: () => void;
  onCommit: (partial: Partial<Characteristic>) => void;
  onCancel: () => void;
}

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const {
    modelIndex,
    areReasonCodesUsed,
    scorecardBaselineScore,
    characteristics,
    characteristicsUnfilteredLength,
    selectedCharacteristicIndex,
    setSelectedCharacteristicIndex,
    validateCharacteristicName,
    viewAttribute,
    deleteCharacteristic,
    onAddAttribute,
    onCommitAndClose,
    onCommit,
    onCancel
  } = props;

  const addCharacteristicRowRef = useRef<HTMLDivElement | null>(null);

  const { activeOperation, setActiveOperation } = useOperation();

  const dataFields: DataField[] = useSelector<PMML, DataField[]>((state: PMML) => {
    return state.DataDictionary.DataField;
  });

  useEffect(() => {
    if (activeOperation === Operation.UPDATE_CHARACTERISTIC && addCharacteristicRowRef.current) {
      addCharacteristicRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  //Exit "edit mode" when the User adds a new entry and then immediately undoes it.
  useEffect(() => {
    if (selectedCharacteristicIndex === characteristicsUnfilteredLength) {
      setSelectedCharacteristicIndex(undefined);
      setActiveOperation(Operation.NONE);
    }
  }, [characteristics, selectedCharacteristicIndex]);

  const onEdit = (index: number | undefined) => {
    setSelectedCharacteristicIndex(index);
    setActiveOperation(Operation.UPDATE_CHARACTERISTIC);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      setActiveOperation(Operation.NONE);
      deleteCharacteristic(index);
    }
  };

  const onValidateCharacteristicName = (index: number | undefined, name: string | undefined): boolean => {
    return validateCharacteristicName(index, name);
  };

  return (
    <Form
      onSubmit={e => {
        e.stopPropagation();
        e.preventDefault();
      }}
    >
      <section>
        {characteristics.map(ic => (
          <article
            key={ic.index}
            className={`editable-item output-item-n${selectedCharacteristicIndex} ${
              selectedCharacteristicIndex === ic.index && activeOperation === Operation.UPDATE_CHARACTERISTIC
                ? "editable-item--editing"
                : ""
            }`}
          >
            {selectedCharacteristicIndex === ic.index && activeOperation === Operation.UPDATE_CHARACTERISTIC && (
              <div ref={addCharacteristicRowRef}>
                <CharacteristicsTableEditRow
                  modelIndex={modelIndex}
                  areReasonCodesUsed={areReasonCodesUsed}
                  scorecardBaselineScore={scorecardBaselineScore}
                  characteristic={ic}
                  validateCharacteristicName={_name => onValidateCharacteristicName(ic.index, _name)}
                  viewAttribute={viewAttribute}
                  onAddAttribute={onAddAttribute}
                  onCommitAndClose={onCommitAndClose}
                  onCommit={onCommit}
                  onCancel={onCancel}
                />
              </div>
            )}
            {(selectedCharacteristicIndex !== ic.index || activeOperation !== Operation.UPDATE_CHARACTERISTIC) && (
              <CharacteristicsTableRow
                modelIndex={modelIndex}
                characteristicIndex={ic.index}
                areReasonCodesUsed={areReasonCodesUsed}
                scorecardBaselineScore={scorecardBaselineScore}
                characteristic={ic}
                dataFields={dataFields}
                onEdit={() => onEdit(ic.index)}
                onDelete={() => onDelete(ic.index)}
              />
            )}
          </article>
        ))}
      </section>
    </Form>
  );
};
