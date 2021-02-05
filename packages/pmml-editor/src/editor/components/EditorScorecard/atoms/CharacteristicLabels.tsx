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
import { useMemo } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicLabel } from "./CharacteristicLabel";
import { useValidationService } from "../../../validation";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";

interface CharacteristicLabelsProps {
  modelIndex: number;
  characteristicIndex: number;
  activeCharacteristic: Characteristic;
  areReasonCodesUsed: boolean;
  scorecardBaselineScore: number | undefined;
}

export const CharacteristicLabels = (props: CharacteristicLabelsProps) => {
  const { modelIndex, characteristicIndex, activeCharacteristic, areReasonCodesUsed, scorecardBaselineScore } = props;

  const validationService = useValidationService().service;
  const reasonCodeValidation = useMemo(
    () =>
      validationService.get(`models[${modelIndex}].Characteristics.Characteristic[${characteristicIndex}].reasonCode`),
    [modelIndex, characteristicIndex, areReasonCodesUsed, activeCharacteristic]
  );
  const baselineScoreValidation = useMemo(
    () =>
      validationService.get(
        `models[${modelIndex}].Characteristics.Characteristic[${characteristicIndex}].baselineScore`
      ),
    [modelIndex, characteristicIndex, areReasonCodesUsed, scorecardBaselineScore, activeCharacteristic]
  );
  return (
    <>
      {areReasonCodesUsed &&
        activeCharacteristic.reasonCode !== undefined &&
        reasonCodeValidation.length === 0 &&
        CharacteristicLabel("Reason code", activeCharacteristic.reasonCode)}
      {areReasonCodesUsed && reasonCodeValidation.length > 0 && (
        <ValidationIndicatorLabel validations={reasonCodeValidation} cssClass="characteristic-list__item__label">
          <>
            <strong>Reason code:</strong>&nbsp;
            <em>Missing</em>
          </>
        </ValidationIndicatorLabel>
      )}

      {activeCharacteristic.baselineScore !== undefined &&
        baselineScoreValidation.length === 0 &&
        CharacteristicLabel("Baseline score", activeCharacteristic.baselineScore)}
      {areReasonCodesUsed && activeCharacteristic.baselineScore === undefined && baselineScoreValidation.length > 0 && (
        <ValidationIndicatorLabel validations={baselineScoreValidation} cssClass="characteristic-list__item__label">
          <>
            <strong>Baseline score:</strong>&nbsp;
            <em>Missing</em>
          </>
        </ValidationIndicatorLabel>
      )}
    </>
  );
};
