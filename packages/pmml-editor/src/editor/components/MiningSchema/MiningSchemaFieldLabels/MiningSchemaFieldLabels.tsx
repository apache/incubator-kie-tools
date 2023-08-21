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
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-alt-circle-right-icon";
import { MiningField } from "@kie-tools/pmml-editor-marshaller";
import "./MiningSchemaFieldLabels.scss";
import { useValidationRegistry, ValidationEntry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import {
  areLowHighValuesRequired,
  isInvalidValueReplacementRequired,
  isMissingValueReplacementRequired,
} from "../../../validation/MiningSchema";

interface MiningSchemaFieldLabelsProps {
  index: number;
  modelIndex: number;
  field: MiningField;
  onEdit: () => void;
  onDelete: (updatedField: MiningField) => void;
  editing: boolean;
}

const MiningSchemaFieldLabels = (props: MiningSchemaFieldLabelsProps) => {
  const { index, modelIndex, field, onEdit, onDelete, editing } = props;

  const BasicMiningLabel = (name: string, value: any, onClose: () => void) => {
    return (
      <Label
        color="cyan"
        className="mining-schema-list__item__label"
        closeBtnProps={{ className: "ignore-onclickoutside" }}
        onClose={editing ? onClose : undefined}
        data-ouia-component-id={name}
        data-ouia-component-type="mining-label"
      >
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </Label>
    );
  };

  const InvalidMiningLabel = (
    name: string,
    value: any,
    onClose: (() => void) | undefined,
    validations: ValidationEntry[]
  ) => {
    return (
      <ValidationIndicatorLabel
        validations={validations}
        cssClass="mining-schema-list__item__label"
        onClose={editing ? onClose : undefined}
      >
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </ValidationIndicatorLabel>
    );
  };

  const MissingValueAwareMiningLabel = (
    name: string,
    value: any,
    isValueRequired: boolean,
    validations: ValidationEntry[],
    onClose: () => void
  ) => {
    return (
      <>
        {isValueRequired && value !== undefined && BasicMiningLabel(name, value, onClose)}
        {isValueRequired && value === undefined && InvalidMiningLabel(name, <em>Missing</em>, undefined, validations)}
        {!isValueRequired && value !== undefined && InvalidMiningLabel(name, value, onClose, validations)}
      </>
    );
  };

  const { validationRegistry } = useValidationRegistry();
  const validationsImportance = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).forImportance().build()
      ),
    [modelIndex, index, field]
  );

  const validationsLowValue = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).forLowValue().build()
      ),
    [modelIndex, index, field]
  );
  const validationsHighValue = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).forHighValue().build()
      ),
    [modelIndex, index, field]
  );
  const _areLowHighValuesRequired = useMemo(() => areLowHighValuesRequired(field.outliers), [modelIndex, index, field]);

  const validationsMissingValueReplacement = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).forMissingValueReplacement().build()
      ),
    [modelIndex, index, field]
  );
  const _isMissingValueReplacementRequired = useMemo(
    () => isMissingValueReplacementRequired(field.missingValueTreatment),
    [modelIndex, index, field]
  );

  const validationsInvalidValueReplacement = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).forInvalidValueReplacement().build()
      ),
    [modelIndex, index, field]
  );
  const _isInvalidValueReplacementRequired = useMemo(
    () => isInvalidValueReplacementRequired(field.invalidValueTreatment),
    [modelIndex, index, field]
  );

  return (
    <>
      {field.usageType !== undefined &&
        BasicMiningLabel("Usage Type", field.usageType, () =>
          onDelete({
            ...field,
            usageType: undefined,
          })
        )}

      {field.optype !== undefined &&
        BasicMiningLabel("Op Type", field.optype, () =>
          onDelete({
            ...field,
            optype: undefined,
          })
        )}

      {field.importance !== undefined && (
        <>
          {validationsImportance.length === 0 &&
            BasicMiningLabel("Importance", field.importance, () =>
              onDelete({
                ...field,
                importance: undefined,
              })
            )}
          {validationsImportance.length > 0 &&
            InvalidMiningLabel(
              "Importance",
              field.importance,
              () => onDelete({ ...field, importance: undefined }),
              validationsImportance
            )}
        </>
      )}

      {field.outliers !== undefined &&
        BasicMiningLabel("Outliers", field.outliers, () =>
          onDelete({
            ...field,
            outliers: undefined,
          })
        )}

      {MissingValueAwareMiningLabel("Low Value", field.lowValue, _areLowHighValuesRequired, validationsLowValue, () =>
        onDelete({
          ...field,
          lowValue: undefined,
        })
      )}

      {MissingValueAwareMiningLabel(
        "High Value",
        field.highValue,
        _areLowHighValuesRequired,
        validationsHighValue,
        () =>
          onDelete({
            ...field,
            highValue: undefined,
          })
      )}

      {field.missingValueTreatment !== undefined &&
        BasicMiningLabel("Missing Value Treatment", field.missingValueTreatment, () =>
          onDelete({
            ...field,
            missingValueTreatment: undefined,
          })
        )}

      {MissingValueAwareMiningLabel(
        "Missing Value Replacement",
        field.missingValueReplacement,
        _isMissingValueReplacementRequired,
        validationsMissingValueReplacement,
        () =>
          onDelete({
            ...field,
            missingValueReplacement: undefined,
          })
      )}

      {field.invalidValueTreatment !== undefined &&
        BasicMiningLabel("Invalid Value Treatment", field.invalidValueTreatment, () =>
          onDelete({
            ...field,
            invalidValueTreatment: undefined,
          })
        )}

      {MissingValueAwareMiningLabel(
        "Invalid Value Replacement",
        field.invalidValueReplacement,
        _isInvalidValueReplacementRequired,
        validationsInvalidValueReplacement,
        () =>
          onDelete({
            ...field,
            invalidValueReplacement: undefined,
          })
      )}

      {editing && (
        <Label
          className="mining-schema-list__item__label"
          variant="outline"
          color="cyan"
          href="#"
          icon={<ArrowAltCircleRightIcon />}
          onClick={(event) => {
            event.preventDefault();
            onEdit();
          }}
          data-ouia-component-id="edit-properties"
          data-ouia-component-type="mf-label"
        >
          Edit Properties
        </Label>
      )}
    </>
  );
};

export default MiningSchemaFieldLabels;
