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
import { Attribute, Characteristic, DataField } from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicLabel, CharacteristicLabelAttribute } from "./CharacteristicLabel";
import { toText } from "../../../reducers";

interface CharacteristicLabelsProps {
  activeCharacteristic: Characteristic;
  areReasonCodesUsed: boolean;
  isBaselineScoreRequired: boolean;
  dataFields: DataField[];
}

export const CharacteristicLabels = (props: CharacteristicLabelsProps) => {
  const { activeCharacteristic, areReasonCodesUsed, isBaselineScoreRequired, dataFields } = props;

  return (
    <>
      {activeCharacteristic.Attribute.length > 0 &&
        CharacteristicLabelAttribute(
          "Attributes",
          attributesToTruncatedText(activeCharacteristic.Attribute, dataFields),
          attributesToFullText(activeCharacteristic.Attribute, dataFields)
        )}
      {activeCharacteristic.reasonCode !== undefined &&
        areReasonCodesUsed &&
        CharacteristicLabel("Reason code", activeCharacteristic.reasonCode)}
      {activeCharacteristic.baselineScore !== undefined &&
        isBaselineScoreRequired &&
        CharacteristicLabel("Baseline score", activeCharacteristic.baselineScore)}
    </>
  );
};

const attributesToTruncatedText = (attributes: Attribute[], fields: DataField[]): string => {
  const text: string[] = [];
  attributes.forEach(attribute => {
    let line: string = toText(attribute.predicate, fields);
    if (line.length > 32) {
      line = line.slice(0, 29) + "...";
    }
    text.push(line);
  });
  return text.join(" ");
};

const attributesToFullText = (attributes: Attribute[], fields: DataField[]): string => {
  const text: string[] = [];
  attributes.forEach(attribute => {
    const line: string = toText(attribute.predicate, fields);
    text.push(line);
  });
  return text.join("\n");
};
