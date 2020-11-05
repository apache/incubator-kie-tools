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
import { useEffect, useState } from "react";
import {
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  FormGroup,
  TextInput
} from "@patternfly/react-core";
import "../organisms/OutputsTable.scss";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputsTableEditModeAction } from "../atoms";
import { ValidatedType } from "../../../types";
import { ExclamationCircleIcon } from "@patternfly/react-icons";

interface OutputsTableEditRowProps {
  index: number | undefined;
  output: OutputField;
  validateName: (name: string) => boolean;
  onCommit: (text: string | undefined, dataType: string | undefined) => void;
  onCancel: () => void;
}

export const OutputsTableEditRow = (props: OutputsTableEditRowProps) => {
  const { index, output, validateName, onCommit, onCancel } = props;

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [dataType, setDataType] = useState<string | undefined>();

  useEffect(() => {
    const _name = output.name.toString();
    setName({
      value: _name,
      valid: validateName(_name)
    });
    setDataType(output.dataType);
  }, [props]);

  const toNumber = (value: string): number | undefined => {
    if (value === "") {
      return undefined;
    }
    const n = Number(value);
    if (isNaN(n)) {
      return undefined;
    }
    return n;
  };

  return (
    <DataListItem id={index?.toString()} className="outputs__list-item" aria-labelledby={"output-" + index}>
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={4}>
              <FormGroup
                fieldId="output-name-helper"
                helperText="Please provide a name for the Output Field."
                helperTextInvalid="Name must be unique and present."
                helperTextInvalidIcon={<ExclamationCircleIcon />}
                validated={name.valid ? "default" : "error"}
              >
                <TextInput
                  type="text"
                  id="output-name"
                  name="output-name"
                  aria-describedby="output-name-helper"
                  value={(name.value ?? "").toString()}
                  validated={name.valid ? "default" : "error"}
                  autoFocus={true}
                  onChange={e =>
                    setName({
                      value: e,
                      valid: validateName(e)
                    })
                  }
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="1" width={4}>
              <FormGroup fieldId="output-dataType-helper" helperText="Specifies the data type for the output field.">
                <TextInput
                  type="text"
                  id="output-dataType"
                  name="output-dataType"
                  aria-describedby="output-dataType-helper"
                  value={dataType}
                  onChange={e => setDataType(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="2" width={5}>
              <div>Labels here...</div>
            </DataListCell>,
            <DataListAction id="delete-output" aria-label="delete" aria-labelledby="delete-output" key="3" width={1}>
              <OutputsTableEditModeAction
                onCommit={() => onCommit(name.value, dataType)}
                onCancel={() => onCancel()}
                disableCommit={!name.valid}
              />
            </DataListAction>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};
