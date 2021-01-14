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
import { Split, SplitItem } from "@patternfly/react-core";
import {
  AttributeLabels,
  CharacteristicLabels,
  CharacteristicPredicateLabel,
  CharacteristicsTableAction
} from "../atoms";
import { Characteristic, DataField } from "@kogito-tooling/pmml-editor-marshaller";
import { IndexedCharacteristic } from "../organisms";
import { toText } from "../../../reducers";
import "./CharacteristicsTableRow.scss";

interface CharacteristicsTableRowProps {
  characteristic: IndexedCharacteristic;
  areReasonCodesUsed: boolean;
  isBaselineScoreRequired: boolean;
  dataFields: DataField[];
  onEdit: () => void;
  onDelete: () => void;
}

export const CharacteristicsTableRow = (props: CharacteristicsTableRowProps) => {
  const { characteristic, areReasonCodesUsed, isBaselineScoreRequired, dataFields, onEdit, onDelete } = props;

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
            activeCharacteristic={characteristic.characteristic}
            areReasonCodesUsed={areReasonCodesUsed}
            isBaselineScoreRequired={isBaselineScoreRequired}
            dataFields={dataFields}
          />
          <br />
          <CharacteristicAttributesList characteristic={characteristic.characteristic} dataFields={dataFields} />
        </SplitItem>
        <SplitItem>
          <CharacteristicsTableAction onDelete={onDelete} />
        </SplitItem>
      </Split>
    </article>
  );
};

interface CharacteristicAttributesListProps {
  characteristic: Characteristic;
  dataFields: DataField[];
}

const CharacteristicAttributesList = (props: CharacteristicAttributesListProps) => {
  const { characteristic, dataFields } = props;

  return (
    <ul>
      {characteristic.Attribute.map((item, index) => (
        <li key={index}>
          {CharacteristicPredicateLabel(toText(item.predicate, dataFields))}
          <AttributeLabels activeAttribute={item} />
        </li>
      ))}
    </ul>
  );
};
