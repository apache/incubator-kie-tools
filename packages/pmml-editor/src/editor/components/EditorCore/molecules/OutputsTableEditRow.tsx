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
import { useMemo, useState } from "react";
import { Flex, FlexItem, FormGroup, Select, SelectOption, SelectVariant, TextInput } from "@patternfly/react-core";
import "../organisms/OutputsTable.scss";
import { FieldName, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputLabelsEditMode, OutputsTableEditModeAction } from "../atoms";
import { ExclamationCircleIcon } from "@patternfly/react-icons";

interface OutputsTableEditRowProps {
  activeOutputFieldIndex: number | undefined;
  activeOutputField: OutputField;
  setActiveOutputField: (_output: OutputField) => void;
  validateOutputName: (name: string | undefined) => boolean;
  viewExtendedProperties: () => void;
  onCommit: () => void;
  onCancel: () => void;
}

const dataTypes = [
  "string",
  "integer",
  "float",
  "double",
  "boolean",
  "date",
  "time",
  "dateTime",
  "dateDaysSince[0]",
  "dateDaysSince[1960]",
  "dateDaysSince[1970]",
  "dateDaysSince[1980]",
  "timeSeconds",
  "dateTimeSecondsSince[0]",
  "dateTimeSecondsSince[1960]",
  "dateTimeSecondsSince[1970]",
  "dateTimeSecondsSince[1980]"
];

export const OutputsTableEditRow = (props: OutputsTableEditRowProps) => {
  const {
    activeOutputFieldIndex,
    activeOutputField,
    setActiveOutputField,
    validateOutputName,
    viewExtendedProperties,
    onCommit,
    onCancel
  } = props;

  const [isTypeSelectOpen, setIsTypeSelectOpen] = useState(false);
  const typeToggle = (isOpen: boolean) => {
    setIsTypeSelectOpen(isOpen);
  };

  const onSelectType = (event: any, selection: any, isPlaceholder: boolean) => {
    setActiveOutputField({ ...activeOutputField, dataType: isPlaceholder ? undefined : selection });
    setIsTypeSelectOpen(false);
  };

  const isValidName = useMemo(() => validateOutputName(activeOutputField.name.toString()), [
    activeOutputFieldIndex,
    activeOutputField
  ]);

  return (
    <article className={`output-item output-item-n${activeOutputFieldIndex}`}>
      <Flex alignItems={{ default: "alignItemsCenter" }} style={{ height: "100%" }}>
        <FlexItem>
          <FormGroup
            fieldId="output-name-helper"
            helperText="Please provide a name for the Output Field."
            helperTextInvalid="Name must be unique and present."
            helperTextInvalidIcon={<ExclamationCircleIcon />}
            validated={isValidName ? "default" : "error"}
            style={{ width: "12em" }}
          >
            <TextInput
              type="text"
              id="output-name"
              name="output-name"
              aria-describedby="output-name-helper"
              value={activeOutputField.name.toString()}
              validated={isValidName ? "default" : "error"}
              autoFocus={true}
              onChange={e => {
                setActiveOutputField({ ...activeOutputField, name: e as FieldName });
              }}
            />
          </FormGroup>
        </FlexItem>
        <FlexItem>
          <FormGroup
            fieldId="output-dataType-helper"
            helperText="Specifies the data type for the output field."
            style={{ width: "12em" }}
          >
            <Select
              id="output-dataType"
              name="output-dataType"
              aria-label="Select Input"
              aria-describedby="output-dataType-helper"
              variant={SelectVariant.single}
              onToggle={typeToggle}
              onSelect={onSelectType}
              selections={activeOutputField.dataType}
              isOpen={isTypeSelectOpen}
              placeholder="Type"
              menuAppendTo={"parent"}
            >
              {dataTypes.map((dt, _index) => (
                <SelectOption key={_index} value={dt} />
              ))}
            </Select>
          </FormGroup>
        </FlexItem>
        <FlexItem>
          <OutputLabelsEditMode
            activeOutputField={activeOutputField}
            setActiveOutputField={setActiveOutputField}
            viewExtendedProperties={viewExtendedProperties}
          />
        </FlexItem>
        <FlexItem align={{ default: "alignRight" }}>
          <OutputsTableEditModeAction onCommit={onCommit} onCancel={onCancel} disableCommit={false} />
        </FlexItem>
      </Flex>
    </article>
  );
};
