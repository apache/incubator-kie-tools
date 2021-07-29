/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import "./EditExpressionMenu.css";
import * as React from "react";
import { useCallback, useContext, useEffect, useState } from "react";
import { PopoverMenu } from "../PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataType, ExpressionProps } from "../../api";
import { BoxedExpressionGlobalContext } from "../../context";
import { DataTypeSelector } from "./DataTypeSelector";

export interface EditExpressionMenuProps {
  /** Optional children element to be considered for triggering the edit expression menu */
  children?: React.ReactElement;
  /** The node where to append the popover content */
  appendTo?: HTMLElement | ((ref?: HTMLElement) => HTMLElement);
  /** A function which returns the HTMLElement where the popover's arrow should be placed */
  arrowPlacement?: () => HTMLElement;
  /** The label for the field 'Name' */
  nameField?: string;
  /** The label for the field 'Data Type' */
  dataTypeField?: string;
  /** The title of the popover menu */
  title?: string;
  /** The pre-selected data type */
  selectedDataType?: DataType;
  /** The pre-selected expression name */
  selectedExpressionName: string;
  /** Function to be called when the expression gets updated, passing the most updated version of it */
  onExpressionUpdate: (expression: ExpressionProps) => void;
}

export const EXPRESSION_NAME = "Expression Name";

export const EditExpressionMenu: React.FunctionComponent<EditExpressionMenuProps> = ({
  children,
  appendTo,
  arrowPlacement,
  title,
  nameField,
  dataTypeField,
  selectedDataType = DataType.Undefined,
  selectedExpressionName,
  onExpressionUpdate,
}: EditExpressionMenuProps) => {
  const globalContext = useContext(BoxedExpressionGlobalContext);
  const { i18n } = useBoxedExpressionEditorI18n();
  title = title ?? i18n.editExpression;
  nameField = nameField ?? i18n.name;
  dataTypeField = dataTypeField ?? i18n.dataType;
  appendTo = appendTo ?? globalContext.boxedExpressionEditorRef?.current ?? undefined;

  const [dataType, setDataType] = useState(selectedDataType);
  const [expressionName, setExpressionName] = useState(selectedExpressionName);

  useEffect(() => {
    setExpressionName(selectedExpressionName);
  }, [selectedExpressionName]);

  useEffect(() => {
    setDataType(selectedDataType);
  }, [selectedDataType]);

  const onExpressionNameChange = useCallback(
    (event) => {
      setExpressionName(event.target.value);
      if (event.type === "blur") {
        onExpressionUpdate({
          name: event.target.value,
          dataType,
        });
      }
    },
    [dataType, onExpressionUpdate]
  );

  const onDataTypeChange = useCallback(
    (dataType: DataType) => {
      setDataType(dataType);
      onExpressionUpdate({
        name: expressionName,
        dataType: dataType,
      });
    },
    [expressionName, onExpressionUpdate]
  );

  return (
    <PopoverMenu
      title={title}
      arrowPlacement={arrowPlacement}
      appendTo={appendTo}
      body={
        <div className="edit-expression-menu">
          <div className="expression-name">
            <label>{nameField}</label>
            <input
              type="text"
              id="expression-name"
              data-ouia-component-id="edit-expression-name"
              value={expressionName}
              onChange={onExpressionNameChange}
              onBlur={onExpressionNameChange}
              className="form-control pf-c-form-control"
              placeholder={EXPRESSION_NAME}
            />
          </div>
          <div className="expression-data-type">
            <label>{dataTypeField}</label>
            <DataTypeSelector selectedDataType={dataType} onDataTypeChange={onDataTypeChange} />
          </div>
        </div>
      }
    >
      {children}
    </PopoverMenu>
  );
};
