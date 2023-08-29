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
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
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
        color="cyan"
        closeBtnProps={{ className: "ignore-onclickoutside" }}
        onClose={editingIndex !== undefined ? () => onPropertyDelete?.(updatedField, editingIndex) : undefined}
        data-ouia-component-id={name}
        data-ouia-component-type="data-props-label"
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
