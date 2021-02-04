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
import { Attribute, Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicLabel } from "./CharacteristicLabel";
import { useValidationService } from "../../../validation";
import { useMemo } from "react";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";

interface AttributeLabelsProps {
  modelIndex: number;
  characteristicIndex: number;
  activeAttributeIndex: number;
  activeAttribute: Attribute;
  areReasonCodesUsed: boolean;
  characteristicReasonCode: Characteristic["reasonCode"];
}

export const AttributeLabels = (props: AttributeLabelsProps) => {
  const {
    modelIndex,
    characteristicIndex,
    activeAttributeIndex,
    activeAttribute,
    areReasonCodesUsed,
    characteristicReasonCode
  } = props;

  const validationService = useValidationService().service;
  const reasonCodeValidation = useMemo(
    () =>
      validationService.get(
        `models[${modelIndex}].Characteristics.Characteristic[${characteristicIndex}].Attribute[${activeAttributeIndex}].reasonCode`
      ),
    [
      modelIndex,
      characteristicIndex,
      areReasonCodesUsed,
      activeAttribute,
      activeAttributeIndex,
      characteristicReasonCode
    ]
  );
  const partialScoreValidation = useMemo(
    () =>
      validationService.get(
        `models[${modelIndex}].Characteristics.Characteristic[${characteristicIndex}].Attribute[${activeAttributeIndex}].partialScore`
      ),
    [modelIndex, characteristicIndex, activeAttribute, activeAttributeIndex]
  );

  return (
    <>
      {areReasonCodesUsed &&
        activeAttribute.reasonCode !== undefined &&
        reasonCodeValidation.length === 0 &&
        CharacteristicLabel("Reason code", activeAttribute.reasonCode)}
      {areReasonCodesUsed && reasonCodeValidation.length > 0 && (
        <ValidationIndicatorLabel validations={reasonCodeValidation} cssClass="characteristic-list__item__label">
          <>
            <strong>Reason code:</strong>&nbsp;
            <em>Missing</em>
          </>
        </ValidationIndicatorLabel>
      )}
      {partialScoreValidation.length === 0 &&
        activeAttribute.partialScore !== undefined &&
        CharacteristicLabel("Partial score", activeAttribute.partialScore)}
      {partialScoreValidation.length > 0 && (
        <ValidationIndicatorLabel validations={partialScoreValidation} cssClass="characteristic-list__item__label">
          <>
            <strong>Partial score:</strong>&nbsp;
            <em>Missing</em>
          </>
        </ValidationIndicatorLabel>
      )}
    </>
  );
};
