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
import { useEffect, useMemo, useState } from "react";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { ConstraintType, DDDataField, RangeConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import ConstraintsRangeEdit from "../ConstraintsRangeEdit/ConstraintsRangeEdit";
import ConstraintsEnumEdit from "../ConstraintsEnumEdit/ConstraintsEnumEdit";
import "./ConstraintsEdit.scss";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface ConstraintsEditProps {
  dataType: DDDataField;
  dataFieldIndex: number | undefined;
  onSave: (payload: Partial<DDDataField>) => void;
}

const ConstraintsEdit = (props: ConstraintsEditProps) => {
  const { dataType, dataFieldIndex, onSave } = props;
  const [constraintType, setConstraintType] = useState<string>(dataType.constraints?.type ?? "");
  const [typeSelectIsOpen, setTypeSelectIsOpen] = useState(false);
  const { typeOptions, enabledTypeOptionsCount } = useMemo(() => getConstraintsTypeOptions(dataType), [dataType]);
  const typeDescription = useMemo(() => getConstraintsTypeDescription(dataType), [dataType]);

  const [ranges, setRanges] = useState<RangeConstraint[] | undefined>(
    dataType.constraints?.type === ConstraintType.RANGE ? dataType.constraints.value : undefined
  );
  const [enums, setEnums] = useState(
    dataType.constraints?.type === ConstraintType.ENUMERATION ? dataType.constraints.value : undefined
  );

  const rangeConstraintLimit = useMemo(
    () => (dataType.optype === "continuous" && dataType.isCyclic ? 1 : undefined),
    [dataType]
  );

  const handleTypeChange = (event: React.MouseEvent | React.ChangeEvent, value: string) => {
    if (value !== constraintType) {
      setConstraintType(value);
      if (value === ConstraintType.RANGE) {
        onSave({
          constraints: {
            type: ConstraintType.RANGE,
            value: [
              {
                start: {
                  value: "",
                  included: true,
                },
                end: {
                  value: "",
                  included: true,
                },
              },
            ],
          },
        });
      }
      if (value === ConstraintType.ENUMERATION) {
        onSave({
          constraints: {
            type: ConstraintType.ENUMERATION,
            value: [""],
          },
        });
      }
      if (value === "") {
        onSave({
          constraints: undefined,
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
        type: ConstraintType.RANGE,
        value: updatedRanges,
      },
    });
  };

  const handleRangeAdd = () => {
    const updatedRanges = [...(ranges ?? [])];
    updatedRanges.push({
      start: {
        value: "",
        included: true,
      },
      end: {
        value: "",
        included: true,
      },
    });
    onSave({
      constraints: {
        type: ConstraintType.RANGE,
        value: updatedRanges,
      },
    });
  };

  const handleRangeDelete = (index: number) => {
    const updatedRanges = [...(ranges ?? [])];
    updatedRanges.splice(index, 1);
    onSave({
      constraints: {
        type: ConstraintType.RANGE,
        value: updatedRanges,
      },
    });
  };

  const handleEnumsChange = (value: string, index: number) => {
    const updatedEnums = [...(enums ?? [])];
    updatedEnums[index] = value;
    onSave({
      constraints: {
        type: ConstraintType.ENUMERATION,
        value: updatedEnums,
      },
    });
  };

  const handleEnumsDelete = (index: number) => {
    const updatedEnums = [...(enums ?? [])];
    updatedEnums.splice(index, 1);
    onSave({
      constraints: {
        type: ConstraintType.ENUMERATION,
        value: updatedEnums,
      },
    });
  };

  const handleAddEnum = () => {
    const updatedEnums = [...(enums ?? []), ""];
    onSave({
      constraints: {
        type: ConstraintType.ENUMERATION,
        value: updatedEnums,
      },
    });
  };

  const handleEnumSort = (oldIndex: number, newIndex: number) => {
    if (enums) {
      const updatedEnums = reorderArray(enums, oldIndex, newIndex);
      onSave({
        constraints: {
          type: ConstraintType.ENUMERATION,
          value: updatedEnums,
        },
      });
    }
  };

  useEffect(() => {
    setConstraintType(dataType.constraints?.type ?? "");
    if (dataType.constraints?.type === ConstraintType.RANGE) {
      setRanges(dataType.constraints.value);
    }
    if (dataType.constraints?.type === ConstraintType.ENUMERATION) {
      setEnums(dataType.constraints.value);
    }
  }, [dataType.constraints]);

  return (
    <section className="constraints__form">
      <FormGroup
        fieldId="constraints-type"
        label="Constraints Type"
        labelIcon={
          typeDescription.length > 0 ? (
            <Tooltip content={typeDescription}>
              <button
                aria-label="More info for Constraints Type"
                onClick={(e) => e.preventDefault()}
                aria-describedby="constraints-type"
                className="pf-v5-c-form__group-label-help"
              >
                <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
              </button>
            </Tooltip>
          ) : (
            <></>
          )
        }
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
            isDisabled={enabledTypeOptionsCount === 1}
            ouiaId="constraints-type"
          >
            {typeOptions.map((item, index) => (
              <SelectOption
                key={index}
                value={item.value}
                isDisabled={item.disabled}
                data-ouia-component-type="select-option"
              >
                {item.label}
              </SelectOption>
            ))}
          </Select>
        </div>
        <FormHelperText>
          <HelperText>
            <HelperTextItem variant="error">
              {enabledTypeOptionsCount > 1 ? "Select the type of constraint and then fill in the required fields." : ""}
            </HelperTextItem>
          </HelperText>
        </FormHelperText>
      </FormGroup>
      {constraintType === ConstraintType.RANGE && ranges !== undefined && (
        <Card isCompact={true} className="constraints__card">
          <CardBody>
            <ConstraintsRangeEdit
              dataFieldIndex={dataFieldIndex}
              ranges={ranges}
              onAdd={handleRangeAdd}
              onChange={handleRangeSave}
              onDelete={handleRangeDelete}
              countLimit={rangeConstraintLimit}
            />
          </CardBody>
        </Card>
      )}
      {constraintType === ConstraintType.ENUMERATION && enums !== undefined && (
        <Card isCompact={true} className="constraints__card">
          <CardBody>
            <ConstraintsEnumEdit
              dataFieldIndex={dataFieldIndex}
              enumerations={enums}
              onChange={handleEnumsChange}
              onDelete={handleEnumsDelete}
              onAdd={handleAddEnum}
              onSort={handleEnumSort}
            />
          </CardBody>
        </Card>
      )}
    </section>
  );
};

export default ConstraintsEdit;

const reorderArray = <T extends unknown>(list: T[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};

const getConstraintsTypeOptions = (dataType: DDDataField) => {
  const typeOptions = [
    { value: ConstraintType.NONE, label: "Select a type", disabled: false },
    { value: ConstraintType.RANGE, label: "Interval", disabled: false },
    { value: ConstraintType.ENUMERATION, label: "Value", disabled: false },
  ];
  if (dataType.type === "string" && dataType.optype === "ordinal") {
    typeOptions[0].disabled = true;
    typeOptions[1].disabled = true;
  }
  if (dataType.optype !== "continuous") {
    typeOptions[1].disabled = true;
  }
  if (dataType.isCyclic) {
    typeOptions[0].disabled = true;
  }
  const enabledTypeOptionsCount = typeOptions.filter((option) => !option.disabled).length;
  return { typeOptions, enabledTypeOptionsCount };
};

const getConstraintsTypeDescription = (dataType: DDDataField) => {
  if (dataType.optype === "ordinal" && dataType.isCyclic) {
    return "Cyclic ordinal data types must have Value constraints";
  }
  if (dataType.optype === "continuous" && dataType.isCyclic) {
    return "Cyclic continuous data types must have constraints";
  }
  if (dataType.type === "string" && dataType.optype === "ordinal") {
    return "Ordinal strings must have Value constraints";
  }
  if (dataType.optype !== "continuous") {
    return "Only continuous data types can have Interval constraints";
  }
  return "";
};
