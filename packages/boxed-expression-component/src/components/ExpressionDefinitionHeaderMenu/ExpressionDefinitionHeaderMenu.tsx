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

import "./ExpressionDefinitionHeaderMenu.css";
import * as React from "react";
import { useCallback, useEffect, useState, useRef } from "react";
import { PopoverMenu, PopoverMenuRef } from "../PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DmnBuiltInDataType, ExpressionDefinition } from "../../api";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DataTypeSelector } from "./DataTypeSelector";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { Button } from "@patternfly/react-core";
import { NavigationKeysUtils } from "../../keysUtils";

export interface ExpressionDefinitionHeaderMenuProps {
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
  selectedDataType?: DmnBuiltInDataType;
  /** The pre-selected expression name */
  selectedExpressionName: string;
  /** Function to be called when the expression gets updated, passing the most updated version of it */
  onExpressionHeaderUpdated: (expressionHeader: Pick<ExpressionDefinition, "name" | "dataType">) => void;
}

export const EXPRESSION_NAME = "Expression Name";

export const ExpressionDefinitionHeaderMenu: React.FunctionComponent<ExpressionDefinitionHeaderMenuProps> = ({
  children,
  appendTo,
  arrowPlacement,
  title,
  nameField,
  dataTypeField,
  selectedDataType = DmnBuiltInDataType.Undefined,
  selectedExpressionName,
  onExpressionHeaderUpdated,
}: ExpressionDefinitionHeaderMenuProps) => {
  const boxedExpressionEditor = useBoxedExpressionEditor();
  const { i18n } = useBoxedExpressionEditorI18n();

  title = title ?? i18n.editExpression;
  nameField = nameField ?? i18n.name;
  dataTypeField = dataTypeField ?? i18n.dataType;
  appendTo = appendTo ?? boxedExpressionEditor.editorRef?.current ?? undefined;

  const [dataType, setDataType] = useState(selectedDataType);
  const [expressionName, setExpressionName] = useState(selectedExpressionName);
  const [isDataTypeSelectorOpen, setDataTypeSelectorOpen] = useState(false);

  const expressionNameRef = useRef<HTMLInputElement>(null);
  const popoverMenuRef = useRef<PopoverMenuRef>();

  useEffect(() => {
    setExpressionName(selectedExpressionName);
  }, [selectedExpressionName]);

  useEffect(() => {
    setDataType(selectedDataType);
  }, [selectedDataType]);

  const onExpressionNameChange = useCallback((event) => {
    setExpressionName(event.target.value);
  }, []);

  const onDataTypeChange = useCallback((dataType: DmnBuiltInDataType) => {
    setDataType(dataType);
  }, []);

  const openManageDataType = useCallback(
    () => boxedExpressionEditor.beeGwtService?.openManageDataType(),
    [boxedExpressionEditor.beeGwtService]
  );

  /**
   * save the expression data from the PopoverMenu
   */
  const saveExpression = useCallback(() => {
    boxedExpressionEditor.beeGwtService?.notifyUserAction();
    onExpressionHeaderUpdated({
      name: expressionName,
      dataType: dataType,
    });
  }, [boxedExpressionEditor.beeGwtService, expressionName, onExpressionHeaderUpdated, dataType]);

  const onHide = useCallback(() => {
    saveExpression();
  }, [saveExpression]);

  /**
   * reset the inputs of the popover to the original state
   */
  const resetFormData = useCallback(() => {
    setExpressionName(selectedExpressionName);
    setDataType(selectedDataType);
  }, [selectedExpressionName, selectedDataType]);

  const onCancel = useCallback(
    (_event: MouseEvent | KeyboardEvent) => {
      resetFormData();
    },
    [resetFormData]
  );

  const onShown = useCallback(() => {
    expressionNameRef.current?.focus();
    popoverMenuRef?.current?.setIsVisible(true);
  }, []);

  const onExpressionNameKeyPress = useCallback(
    (e: React.KeyboardEvent) => {
      if (NavigationKeysUtils.isEnter(e.key)) {
        saveExpression();
        popoverMenuRef?.current?.setIsVisible(false);
      }
    },
    [saveExpression]
  );

  const onExpressionNameKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (NavigationKeysUtils.isEscape(e.key) && !isDataTypeSelectorOpen) {
        resetFormData();
        popoverMenuRef?.current?.setIsVisible(false);
      }
    },
    [resetFormData, isDataTypeSelectorOpen]
  );

  const onDataTypeKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (NavigationKeysUtils.isEscape(e.key) && !isDataTypeSelectorOpen) {
        popoverMenuRef?.current?.setIsVisible(false);
      }
    },
    [isDataTypeSelectorOpen]
  );

  const onDataTypeToggle = useCallback((isOpen: boolean) => {
    setDataTypeSelectorOpen(isOpen);
  }, []);

  return (
    <PopoverMenu
      ref={popoverMenuRef}
      title={title}
      arrowPlacement={arrowPlacement}
      appendTo={appendTo}
      onCancel={onCancel}
      onHide={onHide}
      onShown={onShown}
      body={
        <div className="edit-expression-menu" onKeyDown={onExpressionNameKeyDown}>
          <div className="expression-name">
            <label>{nameField}</label>
            <input
              ref={expressionNameRef}
              type="text"
              id="expression-name"
              data-ouia-component-id="edit-expression-name"
              value={expressionName}
              onChange={onExpressionNameChange}
              onBlur={onExpressionNameChange}
              className="form-control pf-c-form-control"
              placeholder={EXPRESSION_NAME}
              onKeyPress={onExpressionNameKeyPress}
            />
          </div>
          <div className="expression-data-type">
            <label>{dataTypeField}</label>
            <Button
              ouiaId="manage-data-type-link"
              variant="link"
              className="manage-datatype"
              icon={<CogIcon />}
              iconPosition="left"
              onClick={openManageDataType}
            >
              {i18n.manage}
            </Button>
            <DataTypeSelector
              value={dataType}
              onChange={onDataTypeChange}
              onToggle={onDataTypeToggle}
              onKeyDown={onDataTypeKeyDown}
            />
          </div>
        </div>
      }
    >
      {children}
    </PopoverMenu>
  );
};
