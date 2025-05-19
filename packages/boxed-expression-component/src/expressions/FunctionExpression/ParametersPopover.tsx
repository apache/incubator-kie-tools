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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { OutlinedTrashAltIcon } from "@patternfly/react-icons/dist/js/icons/outlined-trash-alt-icon";
import * as React from "react";
import { ChangeEvent, useCallback } from "react";
import { Action, BoxedFunction, generateUuid, getNextAvailablePrefixedName, Normalized } from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { DMN15__tInformationItem } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DataTypeSelector } from "../../expressionVariable/DataTypeSelector";
import "./ParametersPopover.css";

export interface ParametersPopoverProps {
  parameters: Normalized<DMN15__tInformationItem>[];
}

export const ParametersPopover: React.FunctionComponent<ParametersPopoverProps> = ({ parameters }) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const addParameter = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFunction>) => {
          const newParameters = [
            ...(prev.formalParameter ?? []),
            {
              "@_id": generateUuid(),
              "@_name": getNextAvailablePrefixedName(
                (prev.formalParameter ?? []).map((p) => p["@_name"]),
                "p"
              ),
              "@_typeRef": undefined,
            },
          ];

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFunction> = {
            ...prev,
            formalParameter: newParameters,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.FunctionParameterAdded },
      });
    },
    [setExpression]
  );

  return (
    <div className="parameters-editor" onMouseDown={(e) => e.stopPropagation()}>
      {parameters.length ? (
        <>
          <Button variant="tertiary" onClickCapture={addParameter} className="add-parameter">
            {i18n.addParameter}
          </Button>
          <div className="parameters-container">
            {parameters.map((parameter, index) => (
              <ParameterEntry key={index} parameter={parameter} index={index} />
            ))}
          </div>
        </>
      ) : (
        <div className="parameters-container-empty">
          <EmptyState>
            <EmptyStateHeader
              titleText={<>{i18n.noParametersDefined}</>}
              icon={<EmptyStateIcon icon={CubesIcon} />}
              headingLevel="h4"
            />
            <EmptyStateFooter>
              <Button variant="primary" onClickCapture={addParameter}>
                {i18n.addParameter}
              </Button>
            </EmptyStateFooter>
          </EmptyState>
        </div>
      )}
    </div>
  );
};

function ParameterEntry({ parameter, index }: { parameter: DMN15__tInformationItem; index: number }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onNameChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      e.stopPropagation();
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFunction>) => {
          const newParameters = [...(prev.formalParameter ?? [])];
          newParameters[index] = {
            ...newParameters[index],
            "@_name": e.target.value,
          };
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFunction> = {
            ...prev,
            formalParameter: newParameters,
          };

          return ret;
        },
        expressionChangedArgs: {
          action: Action.VariableChanged,
          variableUuid: parameter["@_id"] ?? "",
          nameChange: {
            from: parameter["@_name"],
            to: e.target.value,
          },
        },
      });
    },
    [index, parameter, setExpression]
  );

  const onDataTypeChange = useCallback(
    (typeRef: string | undefined) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFunction>) => {
          const newParameters = [...(prev.formalParameter ?? [])];
          newParameters[index] = {
            ...newParameters[index],
            "@_typeRef": typeRef,
          };
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFunction> = {
            ...prev,
            formalParameter: newParameters,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.FunctionParameterTypeChanged },
      });
    },
    [index, setExpression]
  );

  const onParameterRemove = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFunction>) => {
          const newParameters = [...(prev.formalParameter ?? [])];
          newParameters.splice(index, 1);
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFunction> = {
            ...prev,
            formalParameter: newParameters,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.FunctionParameterRemoved },
      });
    },
    [index, setExpression]
  );

  return (
    <div key={`${parameter["@_name"]}_${index}`} className="parameter-entry">
      <input
        className="parameter-name"
        type="text"
        onBlur={onNameChange}
        placeholder={"Parameter Name"}
        defaultValue={parameter["@_name"]}
      />
      <DataTypeSelector value={parameter["@_typeRef"]} onChange={onDataTypeChange} menuAppendTo="parent" />
      <Button
        variant="danger"
        className="delete-parameter"
        icon={<OutlinedTrashAltIcon />}
        iconPosition="left"
        onClickCapture={onParameterRemove}
      />
    </div>
  );
}
