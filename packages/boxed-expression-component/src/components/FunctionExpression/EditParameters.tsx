/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./EditParameters.css";
import * as _ from "lodash";
import { DataTypeSelector } from "../EditExpressionMenu";
import { Button } from "@patternfly/react-core";
import { OutlinedTrashAltIcon } from "@patternfly/react-icons";
import * as React from "react";
import { ChangeEvent, useCallback } from "react";
import { DataType, EntryInfo, generateNextAvailableEntryName } from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";

export interface EditParametersProps {
  /** List of parameters */
  parameters: EntryInfo[];
  /** Function for mutating parameters state */
  setParameters: React.Dispatch<React.SetStateAction<EntryInfo[]>>;
}

export const EditParameters: React.FunctionComponent<EditParametersProps> = ({ parameters, setParameters }) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const addParameter = useCallback(() => {
    setParameters((parameters) => [
      ...parameters,
      {
        name: generateNextAvailableEntryName(parameters, "p"),
        dataType: DataType.Undefined,
      },
    ]);
  }, [setParameters]);

  const onNameChange = useCallback(
    (index: number) => (event: ChangeEvent<HTMLInputElement>) =>
      setParameters((parameters) => {
        parameters[index].name = event.target.value;
        return [...parameters];
      }),
    [setParameters]
  );

  const onDataTypeChange = useCallback(
    (index: number) => (dataType: DataType) => {
      setParameters((parameters) => {
        parameters[index].dataType = dataType;
        return [...parameters];
      });
    },
    [setParameters]
  );

  const onParameterRemove = useCallback(
    (index: number) => () =>
      setParameters((parameters) => [...parameters.slice(0, index), ...parameters.slice(index + 1)]),
    [setParameters]
  );

  return (
    <div className="parameters-editor">
      <Button variant="tertiary" onClick={addParameter} className="add-parameter">
        {i18n.addParameter}
      </Button>
      <div className="parameters-container">
        {_.map(parameters, (parameter, index) => {
          return (
            <div key={`${parameter.name}_${index}`} className="parameter-entry">
              <input
                className="parameter-name"
                type="text"
                onBlur={onNameChange(index)}
                defaultValue={parameter.name}
              />
              <DataTypeSelector
                selectedDataType={parameter.dataType}
                onDataTypeChange={onDataTypeChange(index)}
                menuAppendTo="parent"
              />
              <Button
                variant="danger"
                className="delete-parameter"
                icon={<OutlinedTrashAltIcon />}
                iconPosition="left"
                onClick={onParameterRemove(index)}
              >
                {i18n.delete}
              </Button>
            </div>
          );
        })}
      </div>
    </div>
  );
};
