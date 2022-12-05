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

import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { ExpressionDefinition, ExpressionDefinitionLogicType } from "../../api";
import { useContextMenuHandler } from "../../hooks";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextExpression } from "../ContextExpression";
import { DecisionTableExpression } from "../DecisionTableExpression";
import { FunctionExpression } from "../FunctionExpression";
import { InvocationExpression } from "../InvocationExpression";
import { ListExpression } from "../ListExpression";
import { LiteralExpression, PmmlLiteralExpression } from "../LiteralExpression";
import { PopoverMenu } from "../PopoverMenu";
import { RelationExpression } from "../RelationExpression";
import "./ExpressionDefinitionLogicTypeSelector.css";

export interface ExpressionDefinitionLogicTypeSelectorProps {
  /** Expression properties */
  expression: ExpressionDefinition;
  /** Function to be invoked when logic type changes */
  onLogicTypeSelected: (logicType: ExpressionDefinitionLogicType) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeReset: () => void;
  /** Function to be invoked to retrieve the DOM reference to be used for selector placement */
  getPlacementRef: () => HTMLDivElement;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionDefinition) => void;
}

export const LOGIC_TYPE_SELECTOR_CLASS = "logic-type-selector";

const NON_SELECTABLE_LOGIC_TYPES = [
  ExpressionDefinitionLogicType.Undefined,
  ExpressionDefinitionLogicType.PmmlLiteralExpression,
];

const SELECTABLE_LOGIC_TYPES = Object.values(ExpressionDefinitionLogicType).filter((logicType) => {
  return !NON_SELECTABLE_LOGIC_TYPES.includes(logicType);
});

export function ExpressionDefinitionLogicTypeSelector({
  expression,
  onLogicTypeSelected,
  onLogicTypeReset,
  getPlacementRef,
}: ExpressionDefinitionLogicTypeSelectorProps) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const isLogicTypeSelected = useMemo(
    () => expression.logicType && expression.logicType !== ExpressionDefinitionLogicType.Undefined,
    [expression.logicType]
  );

  const {
    contextMenuRef,
    contextMenuXPos,
    contextMenuYPos,
    isContextMenuVisible,
    setContextMenuVisible,
    targetElement,
  } = useContextMenuHandler(boxedExpressionEditor.editorRef?.current ?? document);

  const renderExpression = useMemo(() => {
    const logicType = expression.logicType;
    switch (logicType) {
      case ExpressionDefinitionLogicType.LiteralExpression:
        return <LiteralExpression {...expression} />;
      case ExpressionDefinitionLogicType.PmmlLiteralExpression:
        return <PmmlLiteralExpression {...expression} />;
      case ExpressionDefinitionLogicType.Relation:
        return <RelationExpression {...expression} />;
      case ExpressionDefinitionLogicType.Context:
        return <ContextExpression {...expression} />;
      case ExpressionDefinitionLogicType.DecisionTable:
        return <DecisionTableExpression {...expression} />;
      case ExpressionDefinitionLogicType.Invocation:
        return <InvocationExpression {...expression} />;
      case ExpressionDefinitionLogicType.List:
        return <ListExpression {...expression} />;
      case ExpressionDefinitionLogicType.Function:
        return <FunctionExpression {...expression} />;
      case ExpressionDefinitionLogicType.Undefined:
        return <></>; // Shouldn't ever reach this point, though
      default:
        assertUnreachable(logicType);
    }
  }, [expression]);

  const getPopoverArrowPlacement = useCallback(() => {
    return getPlacementRef() as HTMLDivElement;
  }, [getPlacementRef]);

  const getPopoverContainer = useCallback(() => {
    return boxedExpressionEditor.editorRef?.current ?? getPopoverArrowPlacement;
  }, [getPopoverArrowPlacement, boxedExpressionEditor.editorRef]);

  const selectLogicType = useCallback(
    (_: React.MouseEvent, itemId?: string | number) => {
      onLogicTypeSelected(itemId as ExpressionDefinitionLogicType);
    },
    [onLogicTypeSelected]
  );

  const resetLogicType = useCallback(() => {
    setContextMenuVisible(false);
    onLogicTypeReset();
  }, [onLogicTypeReset, setContextMenuVisible]);

  const isContextMenuOpen = useMemo(() => {
    const target = targetElement as HTMLElement;
    const notClickedOnTable = _.isNil(target?.closest("table"));
    const clickedOnTableRemainderContent = !_.isNil(target?.closest(".row-remainder-content"));
    const clickedOnAllowedTableSection = notClickedOnTable || clickedOnTableRemainderContent;

    return !expression.noClearAction && isContextMenuVisible && clickedOnAllowedTableSection;
  }, [isContextMenuVisible, expression.noClearAction, targetElement]);

  const cssClasses = useMemo(() => {
    const classes = [];
    classes.push(LOGIC_TYPE_SELECTOR_CLASS);
    if (isLogicTypeSelected) {
      classes.push("logic-type-selected");
    } else {
      classes.push("logic-type-not-present");
    }
    return classes.join(" ");
  }, [isLogicTypeSelected]);

  return (
    <div className={cssClasses} ref={contextMenuRef}>
      {isLogicTypeSelected ? renderExpression : i18n.selectExpression}

      {!isLogicTypeSelected && (
        <PopoverMenu
          title={i18n.selectLogicType}
          arrowPlacement={getPopoverArrowPlacement}
          appendTo={getPopoverContainer()}
          className="logic-type-popover"
          hasAutoWidth={true}
          body={
            <Menu onSelect={selectLogicType}>
              <MenuList>
                {SELECTABLE_LOGIC_TYPES.map((key) => (
                  <MenuItem key={key} itemId={key}>
                    {key}
                  </MenuItem>
                ))}
              </MenuList>
            </Menu>
          }
        />
      )}

      {isContextMenuOpen && isLogicTypeSelected && (
        <div
          className="context-menu-container no-table-context-menu"
          style={{ top: contextMenuYPos, left: contextMenuXPos }}
        >
          <Menu className="table-handler-menu">
            <MenuGroup label={expression.logicType.toLocaleUpperCase()}>
              <MenuList>
                <MenuItem onClick={resetLogicType}>{i18n.clear}</MenuItem>
              </MenuList>
            </MenuGroup>
          </Menu>
        </div>
      )}
    </div>
  );
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here");
}
