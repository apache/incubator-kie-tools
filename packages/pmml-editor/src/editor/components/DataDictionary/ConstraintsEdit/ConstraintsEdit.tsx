/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useState } from "react";
import { Card, CardBody, CardTitle, FormGroup, Select, SelectOption, SelectVariant } from "@patternfly/react-core";

import { DDDataField, RangeConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import ConstraintsRangeEdit from "../ConstraintsRangeEdit/ConstraintsRangeEdit";
import ConstraintsEnumEdit from "../ConstraintsEnumEdit/ConstraintsEnumEdit";
import { Validated } from "../../../types";

interface ConstraintsEditProps {
  dataType: DDDataField;
  dataFieldIndex: number | undefined;
  onSave: (payload: Partial<DDDataField>) => void;
}

const ConstraintsEdit = (props: ConstraintsEditProps) => {
  const { dataType, dataFieldIndex, onSave } = props;
  const [constraintType, setConstraintType] = useState<string>(dataType.constraints?.type ?? "");
  const [typeSelectIsOpen, setTypeSelectIsOpen] = useState(false);
  const constraintsTypes = [{ value: "", isPlaceholder: true }, { value: "Range" }, { value: "Enumeration" }];
  const [ranges, setRanges] = useState<RangeConstraint[] | undefined>(
    dataType.constraints?.type === "Range" ? dataType.constraints.value : undefined
  );
  const [enums, setEnums] = useState(
    dataType.constraints?.type === "Enumeration" ? dataType.constraints.value : undefined
  );
  const [validation, setValidation] = useState<FormValidation>({
    form: "default",
    fields: { start: "default", end: "default", enums: "default" }
  });

  const handleTypeChange = (event: React.MouseEvent | React.ChangeEvent, value: string) => {
    if (value !== constraintType) {
      setConstraintType(value);
      if (value === "Range") {
        onSave({
          constraints: {
            type: "Range",
            value: [
              {
                start: {
                  value: "",
                  included: true
                },
                end: {
                  value: "",
                  included: true
                }
              }
            ]
          }
        });
      }
      if (value === "Enumeration") {
        onSave({
          constraints: {
            type: "Enumeration",
            value: [""]
          }
        });
      }
    }
    setTypeSelectIsOpen(false);
  };
  const handleTypeToggle = () => {
    setTypeSelectIsOpen(!typeSelectIsOpen);
  };

  const handleRangeSave = (updatedRanges: RangeConstraint[]) => {
    onSave({
      constraints: {
        type: "Range",
        value: updatedRanges
      }
    });
  };

  const handleRangeAdd = () => {
    const updatedRanges = [...ranges];
    updatedRanges.push({
      start: {
        value: "",
        included: true
      },
      end: {
        value: "",
        included: true
      }
    });
    onSave({
      constraints: {
        type: "Range",
        value: updatedRanges
      }
    });
  };

  const handleRangeDelete = (index: number) => {
    const updatedRanges = [...ranges];
    updatedRanges.splice(index, 1);
    onSave({
      constraints: {
        type: "Range",
        value: updatedRanges
      }
    });
  };

  const handleEnumsChange = (value: string, index: number) => {
    const updatedEnums = [...enums];
    updatedEnums[index] = value;
    onSave({
      constraints: {
        type: "Enumeration",
        value: updatedEnums
      }
    });
  };

  const handleEnumsDelete = (index: number) => {
    const updatedEnums = [...enums];
    updatedEnums.splice(index, 1);
    onSave({
      constraints: {
        type: "Enumeration",
        value: updatedEnums
      }
    });
  };

  const handleAddEnum = () => {
    const updatedEnums = [...enums, ""];
    onSave({
      constraints: {
        type: "Enumeration",
        value: updatedEnums
      }
    });
  };

  const handleEnumSort = (oldIndex: number, newIndex: number) => {
    if (enums) {
      const updatedEnums = reorderArray(enums, oldIndex, newIndex);
      onSave({
        constraints: {
          type: "Enumeration",
          value: updatedEnums
        }
      });
    }
  };

  useEffect(() => {
    setConstraintType(dataType.constraints?.type ?? "");
    if (dataType.constraints?.type === "Range") {
      setRanges(dataType.constraints.value);
    }
    if (dataType.constraints?.type === "Enumeration") {
      setEnums(dataType.constraints.value);
    }
  }, [dataType.constraints]);

  return (
    <section className="constraints__form">
      <FormGroup
        fieldId="constraints-type"
        label="Constraints Type"
        helperText="Select the type of constraint and then fill in the required fields."
      >
        {/*PF 2020.08 has a bug setting width of a Select*/}
        <div style={{ width: 300 }}>
          <Select
            id="constraints-type"
            variant={SelectVariant.single}
            aria-label="Select Constraint Type"
            onToggle={handleTypeToggle}
            onSelect={handleTypeChange}
            selections={constraintType}
            isOpen={typeSelectIsOpen}
            placeholderText={"Select a type"}
          >
            {constraintsTypes.map((item, index) => (
              <SelectOption key={index} value={item.value}>
                {item.isPlaceholder ? "Select a type" : item.value}
              </SelectOption>
            ))}
          </Select>
        </div>
      </FormGroup>
      {constraintType === "Range" && ranges !== undefined && (
        <Card isCompact={true} style={{ margin: "1em 0" }}>
          <CardTitle>Range Constraint</CardTitle>
          <CardBody>
            <ConstraintsRangeEdit
              dataFieldIndex={dataFieldIndex}
              ranges={ranges}
              onAdd={handleRangeAdd}
              onChange={handleRangeSave}
              onDelete={handleRangeDelete}
              validation={validation}
            />
          </CardBody>
        </Card>
      )}
      {constraintType === "Enumeration" && enums !== undefined && (
        <Card isCompact={true} style={{ margin: "1em 0" }}>
          <CardTitle>Enumerations List</CardTitle>
          <CardBody>
            <ConstraintsEnumEdit
              enumerations={enums}
              onChange={handleEnumsChange}
              onDelete={handleEnumsDelete}
              onAdd={handleAddEnum}
              onSort={handleEnumSort}
              validation={validation}
            />
          </CardBody>
        </Card>
      )}
    </section>
  );
};

export default ConstraintsEdit;

export interface FormValidation {
  form: Validated;
  fields: { [key: string]: Validated };
}

const reorderArray = <T extends unknown>(list: T[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
