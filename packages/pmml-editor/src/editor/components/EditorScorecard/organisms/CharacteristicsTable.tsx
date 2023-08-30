/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { Characteristic, DataField, PMML } from "@kie-tools/pmml-editor-marshaller";
import { Form } from "@patternfly/react-core/dist/js/components/Form";
import { CharacteristicsTableEditRow, CharacteristicsTableRow } from "../molecules";
import { Operation } from "../Operation";
import { useSelector } from "react-redux";
import { useOperation } from "../OperationContext";
import { Interaction } from "../../../types";

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
    onCancel,
  } = props;

  const addCharacteristicRowRef = useRef<HTMLDivElement | null>(null);

  const [characteristicFocusIndex, setCharacteristicFocusIndex] = useState<number | undefined>(undefined);

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

  //Set the focus on a Characteristic as required
  useEffect(() => {
    if (characteristicFocusIndex !== undefined) {
      document.querySelector<HTMLElement>(`#characteristic-n${characteristicFocusIndex}`)?.focus();
    }
  }, [characteristics, characteristicFocusIndex]);

  const onEdit = (index: number | undefined) => {
    setSelectedCharacteristicIndex(index);
    setActiveOperation(Operation.UPDATE_CHARACTERISTIC);
  };

  const handleDelete = (index: number, interaction: Interaction) => {
    onDelete(index);
    if (interaction === "mouse") {
      //If the Characteristic was deleted by clicking on the delete icon we need to blur
      //the element otherwise the CSS :focus-within persists on the deleted element.
      //See https://issues.redhat.com/browse/FAI-570 for the root cause.
      if (document.activeElement instanceof HTMLElement) {
        document.activeElement?.blur();
      }
    } else if (interaction === "keyboard") {
      //If the Characteristic was deleted by pressing enter on the delete icon when focused
      //we need to set the focus to the next Characteristic. The index of the _next_ item
      //is identical to the index of the deleted item.
      setCharacteristicFocusIndex(index);
    }
    setSelectedCharacteristicIndex(undefined);
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
      data-testid="characteristics-table"
      onSubmit={(e) => {
        e.stopPropagation();
        e.preventDefault();
      }}
      className="characteristics-container__overview__form"
    >
      {characteristics.map((ic) => {
        const isRowInEditMode =
          selectedCharacteristicIndex === ic.index && activeOperation === Operation.UPDATE_CHARACTERISTIC;
        return (
          <article
            key={ic.index}
            className={`editable-item characteristic-item-n${selectedCharacteristicIndex} ${
              isRowInEditMode ? "editable-item--editing" : ""
            }`}
          >
            {isRowInEditMode && (
              <div ref={addCharacteristicRowRef}>
                <CharacteristicsTableEditRow
                  modelIndex={modelIndex}
                  areReasonCodesUsed={areReasonCodesUsed}
                  scorecardBaselineScore={scorecardBaselineScore}
                  characteristic={ic}
                  validateCharacteristicName={(_name) => onValidateCharacteristicName(ic.index, _name)}
                  viewAttribute={viewAttribute}
                  onAddAttribute={onAddAttribute}
                  onCommitAndClose={onCommitAndClose}
                  onCommit={onCommit}
                  onCancel={onCancel}
                />
              </div>
            )}
            {!isRowInEditMode && (
              <CharacteristicsTableRow
                modelIndex={modelIndex}
                characteristicIndex={ic.index}
                areReasonCodesUsed={areReasonCodesUsed}
                scorecardBaselineScore={scorecardBaselineScore}
                characteristic={ic}
                dataFields={dataFields}
                onEdit={() => onEdit(ic.index)}
                onDelete={(interaction) => handleDelete(ic.index, interaction)}
              />
            )}
          </article>
        );
      })}
    </Form>
  );
};
