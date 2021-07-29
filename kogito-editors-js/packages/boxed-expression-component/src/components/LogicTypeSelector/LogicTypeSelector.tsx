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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import {
  ContextProps,
  DataType,
  DecisionTableProps,
  ExpressionProps,
  FunctionKind,
  FunctionProps,
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
import nextId from "react-id-generator";
import { BoxedExpressionGlobalContext } from "../../context";
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

export const LogicTypeSelector: React.FunctionComponent<LogicTypeSelectorProps> = ({
  selectedExpression,
  onLogicTypeUpdating,
  onLogicTypeResetting,
  onUpdatingNameAndDataType,
  getPlacementRef,
  isHeadless = false,
  onUpdatingRecursiveExpression,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const expression = _.extend(selectedExpression, {
    uid: selectedExpression.uid || nextId(),
    isHeadless,
    onUpdatingNameAndDataType,
    onUpdatingRecursiveExpression,
  });

  const isLogicTypeSelected = (logicType?: LogicType) => !_.isEmpty(logicType) && logicType !== LogicType.Undefined;

  const [logicTypeSelected, setLogicTypeSelected] = useState(isLogicTypeSelected(expression.logicType));

  useEffect(() => {
    setLogicTypeSelected(isLogicTypeSelected(selectedExpression.logicType));
  }, [selectedExpression.logicType]);

  const {
    contextMenuRef,
    contextMenuXPos,
    contextMenuYPos,
    contextMenuVisibility,
    setContextMenuVisibility,
    targetElement,
  } = useContextMenuHandler();

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
  }, [expression.logicType]);

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
    return globalContext.boxedExpressionEditorRef?.current ?? getArrowPlacement;
  }, [getArrowPlacement, globalContext.boxedExpressionEditorRef]);

  const onLogicTypeSelect = useCallback(
    (event?: React.MouseEvent, itemId?: string | number) => {
      setLogicTypeSelected(true);
      const selectedLogicType = itemId as LogicType;
      onLogicTypeUpdating(selectedLogicType);
    },
    [onLogicTypeUpdating]
  );

  const buildLogicSelectorMenu = useCallback(
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
    setLogicTypeSelected(false);
    setContextMenuVisibility(false);
    onLogicTypeResetting();
  }, [onLogicTypeResetting, setContextMenuVisibility]);

  const buildContextMenu = useCallback(
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
              <MenuItem isDisabled={!logicTypeSelected} onClick={executeClearAction}>
                {i18n.clear}
              </MenuItem>
            </MenuList>
          </MenuGroup>
        </Menu>
      </div>
    ),
    [contextMenuYPos, contextMenuXPos, expression.logicType, logicTypeSelected, executeClearAction, i18n.clear]
  );

  const shouldClearContextMenuBeOpened = useMemo(() => {
    const notClickedOnTable = _.isNil((targetElement as HTMLElement)?.closest("table"));
    const clickedOnTableRemainderContent = !_.isNil((targetElement as HTMLElement)?.closest(".row-remainder-content"));
    const clickedOnAllowedTableSection = notClickedOnTable || clickedOnTableRemainderContent;

    return !selectedExpression.noClearAction && contextMenuVisibility && clickedOnAllowedTableSection;
  }, [contextMenuVisibility, selectedExpression.noClearAction, targetElement]);

  return (
    <div
      className={`logic-type-selector ${logicTypeSelected ? "logic-type-selected" : "logic-type-not-present"}`}
      ref={contextMenuRef}
    >
      {logicTypeSelected ? renderExpression : i18n.selectExpression}
      {!logicTypeSelected ? buildLogicSelectorMenu() : null}
      {shouldClearContextMenuBeOpened ? buildContextMenu() : null}
    </div>
  );
};
