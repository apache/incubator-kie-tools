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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { OutlinedTrashAltIcon } from "@patternfly/react-icons/dist/js/icons/outlined-trash-alt-icon";
import * as React from "react";
import { ChangeEvent, useCallback } from "react";
import {
  ContextExpressionDefinitionEntryInfo,
  DmnBuiltInDataType,
  FunctionExpressionDefinition,
  generateUuid,
  getNextAvailablePrefixedName,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DataTypeSelector } from "../ExpressionDefinitionHeaderMenu";
import "./ParametersPopover.css";

export interface ParametersPopoverProps {
  parameters: ContextExpressionDefinitionEntryInfo[];
}

export const ParametersPopover: React.FunctionComponent<ParametersPopoverProps> = ({ parameters }) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const addParameter = useCallback(() => {
    setExpression((prev: FunctionExpressionDefinition) => {
      const newParameters = [
        ...prev.formalParameters,
        {
          id: generateUuid(),
          name: getNextAvailablePrefixedName(
            prev.formalParameters.map((p) => p.name),
            "p"
          ),
          dataType: DmnBuiltInDataType.Undefined,
        },
      ];

      return {
        ...prev,
        formalParameters: newParameters,
      };
    });
  }, [setExpression]);

  return (
    <div className="parameters-editor" onMouseDown={(e) => e.stopPropagation()}>
      <Button variant="tertiary" onClick={addParameter} className="add-parameter">
        {i18n.addParameter}
      </Button>
      <div className="parameters-container">
        {parameters.map((parameter, index) => (
          <ParameterEntry key={index} parameter={parameter} index={index} />
        ))}
      </div>
    </div>
  );
};

function ParameterEntry({ parameter, index }: { parameter: ContextExpressionDefinitionEntryInfo; index: number }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onNameChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      e.stopPropagation();
      setExpression((prev: FunctionExpressionDefinition) => {
        const newParameters = [...prev.formalParameters];
        newParameters[index].name = e.target.value;
        return {
          ...prev,
          formalParameters: newParameters,
        };
      });
    },
    [index, setExpression]
  );

  const onDataTypeChange = useCallback(
    (dataType: DmnBuiltInDataType) => {
      setExpression((prev: FunctionExpressionDefinition) => {
        const newParameters = [...prev.formalParameters];
        newParameters[index].dataType = dataType;
        return {
          ...prev,
          formalParameters: newParameters,
        };
      });
    },
    [index, setExpression]
  );

  const onParameterRemove = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      setExpression((prev: FunctionExpressionDefinition) => {
        const newParameters = [...prev.formalParameters];
        newParameters.splice(index, 1);
        return {
          ...prev,
          formalParameters: newParameters,
        };
      });
    },
    [index, setExpression]
  );

  return (
    <div key={`${parameter.name}_${index}`} className="parameter-entry">
      <input className="parameter-name" type="text" onBlur={onNameChange} defaultValue={parameter.name} />
      <DataTypeSelector value={parameter.dataType} onChange={onDataTypeChange} menuAppendTo="parent" />
      <Button
        variant="danger"
        className="delete-parameter"
        icon={<OutlinedTrashAltIcon />}
        iconPosition="left"
        onClick={onParameterRemove}
      />
    </div>
  );
}
