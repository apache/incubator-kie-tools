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
import { useMemo } from "react";
import { Attribute, Characteristic } from "@kie-tools/pmml-editor-marshaller";
import { useValidationRegistry } from "../../../validation";
import { AttributeLabel } from "./AttributeLabel";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import { Builder } from "../../../paths";

interface AttributeLabelsProps {
  modelIndex: number;
  characteristic: Characteristic;
  characteristicIndex: number;
  activeAttributeIndex: number;
  activeAttribute: Attribute;
  areReasonCodesUsed: boolean;
  characteristicReasonCode: Characteristic["reasonCode"];
  commit?: (partial: Partial<Attribute>) => void;
}

export const AttributeLabels = (props: AttributeLabelsProps) => {
  const {
    modelIndex,
    characteristic,
    characteristicIndex,
    activeAttributeIndex,
    activeAttribute,
    areReasonCodesUsed,
    characteristicReasonCode,
    commit,
  } = props;

  const { validationRegistry } = useValidationRegistry();
  const reasonCodeValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(activeAttributeIndex)
          .forReasonCode()
          .build()
      ),
    [
      modelIndex,
      characteristicIndex,
      areReasonCodesUsed,
      activeAttribute,
      activeAttributeIndex,
      characteristicReasonCode,
    ]
  );
  const partialScoreValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(activeAttributeIndex)
          .forPartialScore()
          .build()
      ),
    [modelIndex, characteristicIndex, characteristic, activeAttribute, activeAttributeIndex]
  );

  return (
    <>
      {areReasonCodesUsed && activeAttribute.reasonCode !== undefined && reasonCodeValidation.length === 0 && (
        <>
          {commit && (
            <AttributeLabel
              name={"Reason code"}
              value={activeAttribute.reasonCode}
              onClose={() => commit({ reasonCode: undefined })}
            />
          )}
          {!commit && <AttributeLabel name={"Reason code"} value={activeAttribute.reasonCode} />}
        </>
      )}
      {areReasonCodesUsed && reasonCodeValidation.length > 0 && (
        <ValidationIndicatorLabel validations={reasonCodeValidation} cssClass="characteristic-list__item__label">
          <>
            <strong>Reason code:</strong>&nbsp;
            <em>Missing</em>
          </>
        </ValidationIndicatorLabel>
      )}
      {partialScoreValidation.length === 0 && activeAttribute.partialScore !== undefined && (
        <>
          {commit && (
            <AttributeLabel
              name={"Partial score"}
              value={activeAttribute.partialScore}
              onClose={() => commit({ partialScore: undefined })}
            />
          )}
          {!commit && <AttributeLabel name={"Partial score"} value={activeAttribute.partialScore} />}
        </>
      )}
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
