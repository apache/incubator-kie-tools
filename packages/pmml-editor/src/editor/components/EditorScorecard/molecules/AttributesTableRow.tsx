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
import { BaseSyntheticEvent, useMemo } from "react";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Attribute, Characteristic, DataField, MiningField } from "@kie-tools/pmml-editor-marshaller";
import "./AttributesTableRow.scss";
import { AttributeLabels, AttributesTableAction } from "../atoms";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import { toText } from "../organisms";
import { Interaction } from "../../../types";

interface AttributesTableRowProps {
  modelIndex: number;
  characteristicIndex: number;
  characteristic: Characteristic;
  attributeIndex: number;
  attribute: Attribute;
  areReasonCodesUsed: boolean;
  characteristicReasonCode: Characteristic["reasonCode"];
  dataFields: DataField[];
  miningFields: MiningField[];
  onEdit: () => void;
  onDelete: (interaction: Interaction) => void;
  onCommit: (partial: Partial<Attribute>) => void;
}

export const AttributesTableRow = (props: AttributesTableRowProps) => {
  const {
    modelIndex,
    characteristicIndex,
    characteristic,
    attributeIndex,
    attribute,
    areReasonCodesUsed,
    characteristicReasonCode,
    dataFields,
    miningFields,
    onEdit,
    onDelete,
    onCommit,
  } = props;

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forPredicate()
          .build()
      ),
    [modelIndex, characteristicIndex, attributeIndex, attribute, miningFields]
  );

  const handleEdit = (event: BaseSyntheticEvent) => {
    event.preventDefault();
    event.stopPropagation();
    onEdit();
  };

  return (
    <article
      id={`attribute-n${attributeIndex}`}
      data-testid={`attribute-n${attributeIndex}`}
      className={"attribute-item"}
      onClick={() => onEdit()}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          handleEdit(e);
        }
      }}
      data-ouia-component-type="attribute-item"
      tabIndex={0}
    >
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <>
            {validations.length > 0 && (
              <ValidationIndicatorLabel validations={validations} cssClass="characteristic-list__item__label">
                <>
                  {attribute.predicate && <pre>{toText(attribute.predicate, dataFields)}</pre>}
                  {!attribute.predicate && (
                    <>
                      <strong>Predicate:</strong>&nbsp;
                      <em>Missing</em>
                    </>
                  )}
                </>
              </ValidationIndicatorLabel>
            )}
            {validations.length === 0 && (
              <Label
                tabIndex={0}
                color="blue"
                onClose={(e) => {
                  e.nativeEvent.stopImmediatePropagation();
                  e.stopPropagation();
                  onCommit({ predicate: undefined });
                }}
              >
                <pre>{toText(attribute.predicate, dataFields)}</pre>
              </Label>
            )}
          </>
        </SplitItem>
        <SplitItem isFilled={true}>
          <AttributeLabels
            modelIndex={modelIndex}
            characteristicIndex={characteristicIndex}
            characteristic={characteristic}
            activeAttributeIndex={attributeIndex}
            activeAttribute={attribute}
            areReasonCodesUsed={areReasonCodesUsed}
            characteristicReasonCode={characteristicReasonCode}
            commit={onCommit}
          />
        </SplitItem>
        <SplitItem>
          <AttributesTableAction index={attributeIndex} onDelete={onDelete} />
        </SplitItem>
      </Split>
    </article>
  );
};
