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

import "./LogicTypeSelector.css";
import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  ContextProps,
  DataType,
  DecisionTableProps,
  ExpressionProps,
  FunctionKind,
  FunctionProps,
  generateUuid,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  LogicType,
  PMMLLiteralExpressionProps,
  RelationProps,
} from "../../api";
import { LiteralExpression, PMMLLiteralExpression } from "../LiteralExpression";
import { RelationExpression } from "../RelationExpression";
import { ContextExpression } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core";
import * as _ from "lodash";
import { useContextMenuHandler } from "../../hooks";
import { useBoxedExpression } from "../../context";
import { DecisionTableExpression } from "../DecisionTableExpression";
import { ListExpression } from "../ListExpression";
import { InvocationExpression } from "../InvocationExpression";
import { FunctionExpression } from "../FunctionExpression";

export interface LogicTypeSelectorProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
  /** Function to be invoked when logic type changes */
  onLogicTypeUpdating: (logicType: LogicType) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeResetting: () => void;
  /** Function to be invoked to update expression's name and datatype */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DataType) => void;
  /** Function to be invoked to retrieve the DOM reference to be used for selector placement */
  getPlacementRef: () => HTMLDivElement;
  /** True to have no header for this specific expression component, used in a recursive expression */
  isHeadless?: boolean;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionProps) => void;
}

export const LOGIC_TYPE_SELECTOR_CLASS = "logic-type-selector";

export const LogicTypeSelector: React.FunctionComponent<LogicTypeSelectorProps> = ({
  selectedExpression,
  onLogicTypeUpdating,
  onLogicTypeResetting,
  onUpdatingNameAndDataType,
  getPlacementRef,
  isHeadless,
  onUpdatingRecursiveExpression,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const boxedExpression = useBoxedExpression();

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
    () => selectedExpression.logicType && selectedExpression.logicType !== LogicType.Undefined,
    [selectedExpression.logicType]
  );

  const {
    contextMenuRef,
    contextMenuXPos,
    contextMenuYPos,
    contextMenuVisibility,
    setContextMenuVisibility,
    targetElement,
  } = useContextMenuHandler(boxedExpression.editorRef?.current ?? document);

  const { setIsContextMenuOpen } = useBoxedExpression();

  const renderExpression = useMemo(() => {
    switch (expression.logicType) {
      case LogicType.LiteralExpression:
        return <LiteralExpression {...(expression as LiteralExpressionProps)} />;
      case LogicType.PMMLLiteralExpression:
        return <PMMLLiteralExpression {...(expression as PMMLLiteralExpressionProps)} />;
      case LogicType.Relation:
        return <RelationExpression {...(expression as RelationProps)} />;
      case LogicType.Context:
        return <ContextExpression {...(expression as ContextProps)} />;
      case LogicType.DecisionTable:
        return <DecisionTableExpression {...(expression as DecisionTableProps)} />;
      case LogicType.Invocation:
        return <InvocationExpression {...(expression as InvocationProps)} />;
      case LogicType.List:
        return <ListExpression {...(expression as ListProps)} />;
      case LogicType.Function:
        return <FunctionExpression {..._.defaults(expression, { functionKind: FunctionKind.Feel } as FunctionProps)} />;
      default:
        return expression.logicType;
    }
    // logicType is enough for deciding when to re-execute this function
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expression]);

  const getSelectableLogicTypes = useCallback(
    () =>
      Object.values(LogicType).filter(
        (logicType) => !_.includes([LogicType.Undefined, LogicType.PMMLLiteralExpression], logicType)
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
    return boxedExpression.editorRef?.current ?? getArrowPlacement;
  }, [getArrowPlacement, boxedExpression.editorRef]);

  const onLogicTypeSelect = useCallback(
    (event?: React.MouseEvent, itemId?: string | number) => {
      boxedExpression.boxedExpressionEditorGWTService?.notifyUserAction();
      const selectedLogicType = itemId as LogicType;
      onLogicTypeUpdating(selectedLogicType);
      setIsContextMenuOpen(false);
      if (!isHeadless) {
        boxedExpression.boxedExpressionEditorGWTService?.onLogicTypeSelect(selectedLogicType);
      }
    },
    [boxedExpression.boxedExpressionEditorGWTService, isHeadless, onLogicTypeUpdating, setIsContextMenuOpen]
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
    [i18n.selectLogicType, getArrowPlacement, getAppendToPlacement, onLogicTypeSelect, renderLogicTypeItems]
  );

  const executeClearAction = useCallback(() => {
    setContextMenuVisibility(false);
    onLogicTypeResetting();
  }, [onLogicTypeResetting, setContextMenuVisibility]);

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
          <MenuGroup label={(expression?.logicType ?? LogicType.Undefined).toLocaleUpperCase()}>
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
    const clickedOnTableRemainderContent = !_.isNil((targetElement as HTMLElement)?.closest(".row-remainder-content"));
    const clickedOnAllowedTableSection = notClickedOnTable || clickedOnTableRemainderContent;

    return !selectedExpression.noClearAction && contextMenuVisibility && clickedOnAllowedTableSection;
  }, [contextMenuVisibility, selectedExpression.noClearAction, targetElement]);

  const cssClasses = useMemo(() => {
    const classes = [];
    if (!isHeadless) {
      classes.push(`${boxedExpression.decisionNodeId}`);
    }
    classes.push(LOGIC_TYPE_SELECTOR_CLASS);
    if (isLogicTypeSelected) {
      classes.push("logic-type-selected");
    } else {
      classes.push("logic-type-not-present");
    }
    return classes.join(" ");
  }, [boxedExpression.decisionNodeId, isHeadless, isLogicTypeSelected]);

  const onRootSelectorClick = useCallback(
    (event) => {
      if (!isHeadless && event.target === contextMenuRef.current) {
        boxedExpression.boxedExpressionEditorGWTService?.selectObject(boxedExpression.decisionNodeId);
      }
    },
    [boxedExpression.boxedExpressionEditorGWTService, boxedExpression.decisionNodeId, contextMenuRef, isHeadless]
  );

  return (
    <div className={cssClasses} ref={contextMenuRef} onClick={onRootSelectorClick}>
      {isLogicTypeSelected ? renderExpression : i18n.selectExpression}
      {!isLogicTypeSelected && buildLogicSelectorMenu}
      {shouldClearContextMenuBeOpened && buildContextMenu}
    </div>
  );
};
