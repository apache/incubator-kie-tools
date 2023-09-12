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
import { BaseSyntheticEvent, useCallback } from "react";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import {
  AttributeLabels,
  CharacteristicLabels,
  CharacteristicPredicateLabel,
  CharacteristicsTableAction,
} from "../atoms";
import { Characteristic, DataField } from "@kie-tools/pmml-editor-marshaller";
import { IndexedCharacteristic } from "../organisms";
import "./CharacteristicsTableRow.scss";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { Interaction } from "../../../types";

interface CharacteristicsTableRowProps {
  modelIndex: number;
  characteristicIndex: number;
  characteristic: IndexedCharacteristic;
  areReasonCodesUsed: boolean;
  scorecardBaselineScore: number | undefined;
  dataFields: DataField[];
  onEdit: () => void;
  onDelete: (interaction: Interaction) => void;
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
    onDelete,
  } = props;

  const handleEdit = (event: BaseSyntheticEvent) => {
    event.preventDefault();
    event.stopPropagation();
    onEdit();
  };

  return (
    <article
      id={`characteristic-n${characteristicIndex}`}
      data-testid={`characteristic-n${characteristicIndex}`}
      className={"editable-item__inner"}
      onClick={handleEdit}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          handleEdit(e);
        }
      }}
      data-ouia-component-type="characteristic-item"
      tabIndex={0}
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
          <CharacteristicsTableAction index={characteristicIndex} onDelete={onDelete} />
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
    (attributeIndex) =>
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
            characteristic={characteristic}
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
