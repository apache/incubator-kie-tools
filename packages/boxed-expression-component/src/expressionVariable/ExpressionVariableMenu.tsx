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
import { useCallback, useEffect, useRef, useState } from "react";
import { PopoverMenu, PopoverMenuRef } from "../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditorContext";
import { DataTypeSelector } from "./DataTypeSelector";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { NavigationKeysUtils } from "../keysUtils/keyUtils";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import "./ExpressionVariableMenu.css";
import { Action, ExpressionChangedArgs, VariableChangedArgs } from "../api";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";
import { FeelInputTextbox } from "./FeelInputTextbox";

export type OnExpressionVariableUpdated = (args: {
  name: string;
  typeRef: string | undefined;
  changes: VariableChangedArgs;
}) => void;

export interface ExpressionVariableMenuProps {
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
  selectedDataType?: string;
  /** The pre-selected expression name */
  selectedExpressionName: string;
  /** Function to be called when the expression gets updated, passing the most updated version of it */
  onVariableUpdated: OnExpressionVariableUpdated;
  position?: PopoverPosition;
  /** The UUID of the variable. */
  variableUuid: string;
  /** If instead of plain text the content is a FEEL expression. */
  isContentAFeelExpression?: boolean;
}

export const DEFAULT_EXPRESSION_VARIABLE_NAME = "Expression Name";

export function ExpressionVariableMenu({
  children,
  appendTo,
  arrowPlacement,
  nameField,
  dataTypeField,
  selectedDataType = undefined,
  selectedExpressionName,
  onVariableUpdated,
  position,
  variableUuid,
  isContentAFeelExpression = false,
}: ExpressionVariableMenuProps) {
  const { editorRef, beeGwtService } = useBoxedExpressionEditor();
  const { i18n } = useBoxedExpressionEditorI18n();

  nameField = nameField ?? isContentAFeelExpression ? i18n.expression : i18n.name;
  dataTypeField = dataTypeField ?? i18n.dataType;
  appendTo = appendTo ?? editorRef?.current ?? undefined;

  const [dataType, setDataType] = useState(selectedDataType);
  const [expressionName, setExpressionName] = useState(selectedExpressionName);

  const expressionNameRef = useRef<HTMLInputElement>(null);
  const popoverMenuRef = useRef<PopoverMenuRef>();

  useEffect(() => {
    setExpressionName(selectedExpressionName);
  }, [selectedExpressionName]);

  useEffect(() => {
    setDataType(selectedDataType);
  }, [selectedDataType]);

  const onExpressionNameChange = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    setExpressionName(event.target.value);
  }, []);

  const onDataTypeChange = useCallback((dataType: string | undefined) => {
    setDataType(dataType);
  }, []);

  const openDataTypePage = useCallback(() => {
    return beeGwtService?.openDataTypePage();
  }, [beeGwtService]);

  const saveExpression = useCallback(() => {
    const expressionChangedArgs: ExpressionChangedArgs = {
      action: Action.VariableChanged,
      variableUuid: variableUuid,
      typeChange:
        dataType !== selectedDataType
          ? {
              from: dataType,
              to: selectedDataType,
            }
          : undefined,
      nameChange:
        expressionName !== selectedExpressionName
          ? {
              from: expressionName,
              to: selectedExpressionName,
            }
          : undefined,
    };

    if (expressionChangedArgs.nameChange || expressionChangedArgs.typeChange) {
      onVariableUpdated({ name: expressionName, typeRef: dataType, changes: expressionChangedArgs });
    }
  }, [expressionName, selectedExpressionName, dataType, selectedDataType, variableUuid, onVariableUpdated]);

  const resetFormData = useCallback(() => {
    setExpressionName(selectedExpressionName);
    setDataType(selectedDataType);
  }, [selectedExpressionName, selectedDataType]);

  // onCancel doesn't prevent the onHidden call
  // With this ref we ensure the "saveExpression" inside onHidden will not be called
  const cancelEdit = useRef<boolean>(false);

  const onHidden = useCallback(() => {
    if (cancelEdit.current) {
      cancelEdit.current = false;
      return;
    }
    saveExpression();
    popoverMenuRef?.current?.setIsVisible(false);
  }, [saveExpression]);

  const onCancel = useCallback(() => {
    cancelEdit.current = true;
    resetFormData();
    popoverMenuRef?.current?.setIsVisible(false);
  }, [resetFormData]);

  const onShown = useCallback(() => {
    // We need to refresh the expression name from the selectedExpressionName,
    // otherwise it will show the older name set in expressionName.
    setExpressionName(selectedExpressionName);
    expressionNameRef.current?.focus();
  }, [selectedExpressionName]);

  const onKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      // In macOS, we can not stopPropagation here because, otherwise, shortcuts are not handled
      // See https://github.com/apache/incubator-kie-issues/issues/1164
      if (!(getOperatingSystem() === OperatingSystem.MACOS && e.metaKey)) {
        e.stopPropagation();
      }

      if (NavigationKeysUtils.isEnter(e.key)) {
        saveExpression();
        popoverMenuRef?.current?.setIsVisible(false);
      } else if (NavigationKeysUtils.isEsc(e.key)) {
        resetFormData();
        popoverMenuRef?.current?.setIsVisible(false);
      }
    },
    [resetFormData, saveExpression]
  );

  return (
    <PopoverMenu
      ref={popoverMenuRef}
      arrowPlacement={arrowPlacement}
      appendTo={appendTo}
      onCancel={onCancel}
      onHidden={onHidden}
      onShown={onShown}
      position={position}
      distance={25}
      body={
        <div className="edit-expression-menu" onKeyDown={onKeyDown} onMouseDown={(e) => e.stopPropagation()}>
          <div className="expression-name">
            <label>{nameField}</label>
            {isContentAFeelExpression ? (
              <FeelInputTextbox
                value={expressionName}
                onChange={(e) => setExpressionName(e)}
                expressionId={variableUuid}
              />
            ) : (
              <input
                ref={expressionNameRef}
                type="text"
                id="expression-name"
                data-ouia-component-id="edit-expression-name"
                value={expressionName}
                onChange={onExpressionNameChange}
                onBlur={onExpressionNameChange}
                className="form-control pf-v5-c-form-control"
                placeholder={DEFAULT_EXPRESSION_VARIABLE_NAME}
                onKeyDown={onKeyDown}
              />
            )}
          </div>
          <div className="expression-data-type">
            <label>{dataTypeField}</label>
            <Button
              ouiaId="manage-data-type-link"
              variant="link"
              className="manage-datatype"
              icon={<CogIcon />}
              iconPosition="left"
              onClick={openDataTypePage}
            >
              {i18n.manage}
            </Button>
            <DataTypeSelector value={dataType} onChange={onDataTypeChange} onKeyDown={onKeyDown} />
          </div>
        </div>
      }
    >
      {children}
    </PopoverMenu>
  );
}
