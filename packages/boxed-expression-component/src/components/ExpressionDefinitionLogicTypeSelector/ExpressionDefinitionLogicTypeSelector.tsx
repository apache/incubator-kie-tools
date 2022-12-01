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

import "./ExpressionDefinitionLogicTypeSelector.css";
import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  DecisionTableExpressionDefinition,
  ExpressionDefinition,
  FunctionExpressionDefinitionKind,
  FunctionExpressionDefinition,
  generateUuid,
  InvocationExpressionDefinition,
  ListExpressionDefinition,
  LiteralExpressionDefinition,
  ExpressionDefinitionLogicType,
  PmmlLiteralExpressionDefinition,
  RelationExpressionDefinition,
} from "../../api";
import { LiteralExpression, PmmlLiteralExpression } from "../LiteralExpression";
import { RelationExpression } from "../RelationExpression";
import { ContextExpression } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core";
import * as _ from "lodash";
import { useContextMenuHandler } from "../../hooks";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DecisionTableExpression } from "../DecisionTableExpression";
import { ListExpression } from "../ListExpression";
import { InvocationExpression } from "../InvocationExpression";
import { FunctionExpression } from "../FunctionExpression";

export interface ExpressionDefinitionLogicTypeSelectorProps {
  /** Expression properties */
  selectedExpression: ExpressionDefinition;
  /** Function to be invoked when logic type changes */
  onLogicTypeUpdating: (logicType: ExpressionDefinitionLogicType) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeResetting: () => void;
  /** Function to be invoked to update expression's name and datatype */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DmnBuiltInDataType) => void;
  /** Function to be invoked to retrieve the DOM reference to be used for selector placement */
  getPlacementRef: () => HTMLDivElement;
  /** True to have no header for this specific expression component, used in a recursive expression */
  isHeadless?: boolean;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionDefinition) => void;
}

export const LOGIC_TYPE_SELECTOR_CLASS = "logic-type-selector";

export const ExpressionDefinitionLogicTypeSelector: React.FunctionComponent<ExpressionDefinitionLogicTypeSelectorProps> =
  ({
    selectedExpression,
    onLogicTypeUpdating,
    onLogicTypeResetting,
    onUpdatingNameAndDataType,
    getPlacementRef,
    isHeadless,
    onUpdatingRecursiveExpression,
  }) => {
    const { i18n } = useBoxedExpressionEditorI18n();
    const boxedExpressionEditor = useBoxedExpressionEditor();

    const expression = useMemo(() => {
      return {
        ...selectedExpression,
        id: selectedExpression.id ?? generateUuid(),
        isHeadless: isHeadless ?? false,
        onUpdatingNameAndDataType,
        onUpdatingRecursiveExpression,
      };
    }, [selectedExpression, isHeadless, onUpdatingNameAndDataType, onUpdatingRecursiveExpression]);

    const isLogicTypeSelected = useMemo(
      () => selectedExpression.logicType && selectedExpression.logicType !== ExpressionDefinitionLogicType.Undefined,
      [selectedExpression.logicType]
    );

    const {
      contextMenuRef,
      contextMenuXPos,
      contextMenuYPos,
      isContextMenuVisible,
      setContextMenuVisible,
      targetElement,
    } = useContextMenuHandler(boxedExpressionEditor.editorRef?.current ?? document);

    const { setContextMenuOpen } = useBoxedExpressionEditor();

    const renderExpression = useMemo(() => {
      switch (expression.logicType) {
        case ExpressionDefinitionLogicType.LiteralExpression:
          return <LiteralExpression {...(expression as LiteralExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.PmmlLiteralExpression:
          return <PmmlLiteralExpression {...(expression as PmmlLiteralExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.Relation:
          return <RelationExpression {...(expression as RelationExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.Context:
          return <ContextExpression {...(expression as ContextExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.DecisionTable:
          return <DecisionTableExpression {...(expression as DecisionTableExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.Invocation:
          return <InvocationExpression {...(expression as InvocationExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.List:
          return <ListExpression {...(expression as ListExpressionDefinition)} />;
        case ExpressionDefinitionLogicType.Function:
          return (
            <FunctionExpression
              {..._.defaults(expression, {
                functionKind: FunctionExpressionDefinitionKind.Feel,
              } as FunctionExpressionDefinition)}
            />
          );
        default:
          return expression.logicType;
      }
      // logicType is enough for deciding when to re-execute this function
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [expression]);

    const getSelectableLogicTypes = useCallback(
      () =>
        Object.values(ExpressionDefinitionLogicType).filter(
          (logicType) =>
            !_.includes(
              [ExpressionDefinitionLogicType.Undefined, ExpressionDefinitionLogicType.PmmlLiteralExpression],
              logicType
            )
        ),
      []
    );

    const renderLogicTypeItems = useCallback(
      () =>
        _.map(getSelectableLogicTypes(), (key) => (
          <MenuItem key={key} itemId={key}>
            {key}
          </MenuItem>
        )),
      [getSelectableLogicTypes]
    );

    const getArrowPlacement = useCallback(() => getPlacementRef() as HTMLElement, [getPlacementRef]);

    const getAppendToPlacement = useCallback(() => {
      return boxedExpressionEditor.editorRef?.current ?? getArrowPlacement;
    }, [getArrowPlacement, boxedExpressionEditor.editorRef]);

    const onLogicTypeSelect = useCallback(
      (event?: React.MouseEvent, itemId?: string | number) => {
        boxedExpressionEditor.beeGwtService?.notifyUserAction();
        const selectedLogicType = itemId as ExpressionDefinitionLogicType;
        onLogicTypeUpdating(selectedLogicType);
        setContextMenuOpen(false);
        if (!isHeadless) {
          boxedExpressionEditor.beeGwtService?.onLogicTypeSelect(selectedLogicType);
        }
      },
      [boxedExpressionEditor.beeGwtService, isHeadless, onLogicTypeUpdating, setContextMenuOpen]
    );

    const buildLogicSelectorMenu = useMemo(
      () => (
        <PopoverMenu
          title={i18n.selectLogicType}
          arrowPlacement={getArrowPlacement}
          appendTo={getAppendToPlacement()}
          className="logic-type-popover"
          hasAutoWidth
          body={
            <Menu onSelect={onLogicTypeSelect}>
              <MenuList>{renderLogicTypeItems()}</MenuList>
            </Menu>
          }
        />
      ),
      [i18n, getArrowPlacement, getAppendToPlacement, onLogicTypeSelect, renderLogicTypeItems]
    );

    const executeClearAction = useCallback(() => {
      setContextMenuVisible(false);
      onLogicTypeResetting();
    }, [onLogicTypeResetting, setContextMenuVisible]);

    const buildContextMenu = useMemo(
      () => (
        <div
          className="context-menu-container no-table-context-menu"
          style={{
            top: contextMenuYPos,
            left: contextMenuXPos,
          }}
        >
          <Menu className="table-handler-menu">
            <MenuGroup label={(expression?.logicType ?? ExpressionDefinitionLogicType.Undefined).toLocaleUpperCase()}>
              <MenuList>
                <MenuItem isDisabled={!isLogicTypeSelected} onClick={executeClearAction}>
                  {i18n.clear}
                </MenuItem>
              </MenuList>
            </MenuGroup>
          </Menu>
        </div>
      ),
      [contextMenuYPos, contextMenuXPos, expression.logicType, isLogicTypeSelected, executeClearAction, i18n.clear]
    );

    const shouldClearContextMenuBeOpened = useMemo(() => {
      const notClickedOnTable = _.isNil((targetElement as HTMLElement)?.closest("table"));
      const clickedOnTableRemainderContent = !_.isNil(
        (targetElement as HTMLElement)?.closest(".row-remainder-content")
      );
      const clickedOnAllowedTableSection = notClickedOnTable || clickedOnTableRemainderContent;

      return !selectedExpression.noClearAction && isContextMenuVisible && clickedOnAllowedTableSection;
    }, [isContextMenuVisible, selectedExpression.noClearAction, targetElement]);

    const cssClasses = useMemo(() => {
      const classes = [];
      if (!isHeadless) {
        classes.push(`${boxedExpressionEditor.decisionNodeId}`);
      }
      classes.push(LOGIC_TYPE_SELECTOR_CLASS);
      if (isLogicTypeSelected) {
        classes.push("logic-type-selected");
      } else {
        classes.push("logic-type-not-present");
      }
      return classes.join(" ");
    }, [boxedExpressionEditor.decisionNodeId, isHeadless, isLogicTypeSelected]);

    const onRootSelectorClick = useCallback(
      (event) => {
        if (!isHeadless && event.target === contextMenuRef.current) {
          boxedExpressionEditor.beeGwtService?.selectObject(boxedExpressionEditor.decisionNodeId);
        }
      },
      [boxedExpressionEditor.beeGwtService, boxedExpressionEditor.decisionNodeId, contextMenuRef, isHeadless]
    );

    return (
      <div className={cssClasses} ref={contextMenuRef} onClick={onRootSelectorClick}>
        {isLogicTypeSelected ? renderExpression : i18n.selectExpression}
        {!isLogicTypeSelected && buildLogicSelectorMenu}
        {shouldClearContextMenuBeOpened && buildContextMenu}
      </div>
    );
  };
