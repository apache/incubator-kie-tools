/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import * as React from "react";
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { Label } from "@patternfly/react-core";
import "./PropertiesLabels.scss";

interface PropertiesLabelsProps {
  dataType: DDDataField;
  editingIndex?: number;
  onPropertyDelete?: (dataType: DDDataField, index: number) => void;
}

const PropertiesLabels = (props: PropertiesLabelsProps) => {
  const { dataType, editingIndex, onPropertyDelete } = props;

  const propertyLabel = (name: string, value: any, updatedField: DDDataField) => {
    return (
      <Label
        className="properties-labels__item"
        color="orange"
        closeBtnProps={{ className: "ignore-onclickoutside" }}
        onClose={editingIndex ? () => onPropertyDelete?.(updatedField, editingIndex) : undefined}
      >
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </Label>
    );
  };

  return (
    <>
      {dataType.displayName !== undefined &&
        propertyLabel("Display Name", dataType.displayName, { ...dataType, displayName: undefined })}
      {dataType.isCyclic !== undefined &&
        propertyLabel("Is Cyclic", dataType.isCyclic ? "Yes" : "No", { ...dataType, isCyclic: undefined })}
      {dataType.invalidValue !== undefined &&
        propertyLabel("Invalid Value", dataType.invalidValue, { ...dataType, invalidValue: undefined })}
      {dataType.missingValue !== undefined &&
        propertyLabel("Missing Value", dataType.missingValue, { ...dataType, missingValue: undefined })}
    </>
  );
};

export default PropertiesLabels;
