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
import { every } from "lodash";
import { ConstraintType, DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import "./ConstraintsLabel.scss";

interface ConstraintsLabelProps {
  dataType: DDDataField;
  dataTypeIndex: number;
  editMode?: boolean;
  onConstraintsDelete?: () => void;
}

const ConstraintsLabel = (props: ConstraintsLabelProps) => {
  const { dataType, dataTypeIndex, editMode = false, onConstraintsDelete } = props;

  const onClose = useMemo(() => {
    if (editMode && !areConstraintsRequired(dataType)) {
      return (event: React.MouseEvent) => {
        event.nativeEvent.stopImmediatePropagation();
        onConstraintsDelete?.();
      };
    }
  }, [dataTypeIndex, dataType]);

  const missingRequiredConstraints = useMemo(() => {
    return !dataType.constraints && areConstraintsRequired(dataType);
  }, [dataType]);

  const constraintValue = useMemo(() => {
    if (dataType.constraints) {
      switch (dataType.constraints.type) {
        case ConstraintType.RANGE:
          return dataType.constraints.value
            .map((range) => {
              return (
                `${range.start.included ? "[" : "("}` +
                `${range.start.value || `${String.fromCharCode(8722, 8734)}`}, ` +
                `${range.end.value || `${String.fromCharCode(43, 8734)}`}` +
                `${range.end.included ? "]" : ")"}`
              );
            })
            .join(" ");

        case ConstraintType.ENUMERATION:
          if (every(dataType.constraints.value, (value) => value === "")) {
            return <em>No values</em>;
          }
          return dataType.constraints.value.map((item) => `"${item}"`).join(", ");
        default:
          return "";
      }
    }
    return "";
  }, [dataType.constraints]);

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forDataDictionary().forDataField(dataTypeIndex).build()),
    [dataTypeIndex, dataType]
  );

  return (
    <>
      {missingRequiredConstraints && (
        <ValidationIndicatorLabel validations={validations} cssClass="constraints-label">
          <em>Missing required constraints</em>
        </ValidationIndicatorLabel>
      )}
      {!missingRequiredConstraints && dataType.constraints && (
        <>
          {validations.length > 0 && (
            <ValidationIndicatorLabel
              validations={validations}
              onClose={onClose}
              cssClass="constraints-label"
              data-ouia-component-id="invalid-constraints"
              data-ouia-component-type="data-props-label"
            >
              <>
                <strong>Constraints:</strong>&nbsp;
                <span>{constraintValue}</span>
              </>
            </ValidationIndicatorLabel>
          )}
          {validations.length === 0 && (
            <Label
              color="cyan"
              className="constraints-label"
              onClose={onClose}
              data-ouia-component-id="constraints"
              data-ouia-component-type="data-props-label"
            >
              <strong>Constraints:</strong>&nbsp;
              <span>{constraintValue}</span>
            </Label>
          )}
        </>
      )}
    </>
  );
};

export default ConstraintsLabel;

const areConstraintsRequired = (dataType: DDDataField) => {
  return dataType.isCyclic || (dataType.type === "string" && dataType.optype === "ordinal");
};
