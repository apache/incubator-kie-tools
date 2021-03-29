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
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import {
  AttributeLabels,
  CharacteristicLabels,
  CharacteristicPredicateLabel,
  CharacteristicsTableAction
} from "../atoms";
import { Characteristic, DataField } from "@kogito-tooling/pmml-editor-marshaller";
import { IndexedCharacteristic } from "../organisms";
import "./CharacteristicsTableRow.scss";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";

interface CharacteristicsTableRowProps {
  modelIndex: number;
  characteristicIndex: number;
  characteristic: IndexedCharacteristic;
  areReasonCodesUsed: boolean;
  scorecardBaselineScore: number | undefined;
  dataFields: DataField[];
  onEdit: () => void;
  onDelete: () => void;
}

export const CharacteristicsTableRow = (props: CharacteristicsTableRowProps) => {
  const {
    modelIndex,
    characteristicIndex,
    characteristic,
    areReasonCodesUsed,
    scorecardBaselineScore,
    dataFields,
    onEdit,
    onDelete
  } = props;

  return (
    <article
      className={"editable-item__inner"}
      tabIndex={0}
      onClick={onEdit}
      onKeyDown={e => {
        if (e.key === "Enter") {
          e.preventDefault();
          e.stopPropagation();
          onEdit();
        }
      }}
    >
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <strong>{characteristic.characteristic.name}</strong>
        </SplitItem>
        <SplitItem isFilled={true}>
          <CharacteristicLabels
            modelIndex={modelIndex}
            characteristicIndex={characteristicIndex}
            activeCharacteristic={characteristic.characteristic}
            areReasonCodesUsed={areReasonCodesUsed}
            scorecardBaselineScore={scorecardBaselineScore}
          />
          <CharacteristicAttributesList
            modelIndex={modelIndex}
            characteristicIndex={characteristicIndex}
            characteristic={characteristic.characteristic}
            areReasonCodesUsed={areReasonCodesUsed}
            dataFields={dataFields}
          />
        </SplitItem>
        <SplitItem>
          <CharacteristicsTableAction onDelete={onDelete} />
        </SplitItem>
      </Split>
    </article>
  );
};

interface CharacteristicAttributesListProps {
  modelIndex: number;
  characteristicIndex: number;
  characteristic: Characteristic;
  areReasonCodesUsed: boolean;
  dataFields: DataField[];
}

const CharacteristicAttributesList = (props: CharacteristicAttributesListProps) => {
  const { modelIndex, characteristicIndex, characteristic, areReasonCodesUsed, dataFields } = props;

  const { validationRegistry } = useValidationRegistry();

  const validations = useCallback(
    attributeIndex =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forPredicate()
          .build()
      ),
    [modelIndex, characteristicIndex, characteristic]
  );

  return (
    <ul>
      {characteristic.Attribute.map((item, index) => (
        <li key={index}>
          {CharacteristicPredicateLabel(item.predicate, dataFields, validations(index))}
          <AttributeLabels
            modelIndex={modelIndex}
            characteristicIndex={characteristicIndex}
            activeAttributeIndex={index}
            activeAttribute={item}
            areReasonCodesUsed={areReasonCodesUsed}
            characteristicReasonCode={characteristic.reasonCode}
          />
        </li>
      ))}
    </ul>
  );
};
