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

import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core/dist/js/components/Menu";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { ExpressionDefinition, ExpressionDefinitionLogicType } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextExpression } from "../ContextExpression";
import { DecisionTableExpression } from "../DecisionTableExpression";
import { FunctionExpression } from "../FunctionExpression";
import { InvocationExpression } from "../InvocationExpression";
import { ListExpression } from "../ListExpression";
import { LiteralExpression, PmmlLiteralExpression } from "../LiteralExpression";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
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
  isClearSupported?: boolean;
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
  isClearSupported = true,
}: ExpressionDefinitionLogicTypeSelectorProps) {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { setCurrentlyOpenContextMenu, editorRef } = useBoxedExpressionEditor();

  const isLogicTypeSelected = useMemo(
    () => expression.logicType && expression.logicType !== ExpressionDefinitionLogicType.Undefined,
    [expression.logicType]
  );

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
    return editorRef?.current ?? getPopoverArrowPlacement;
  }, [getPopoverArrowPlacement, editorRef]);

  const selectLogicType = useCallback(
    (_: React.MouseEvent, itemId?: string | number) => {
      onLogicTypeSelected(itemId as ExpressionDefinitionLogicType);
      setCurrentlyOpenContextMenu(undefined);
    },
    [onLogicTypeSelected, setCurrentlyOpenContextMenu]
  );

  const resetLogicType = useCallback(() => {
    setCurrentlyOpenContextMenu(undefined);
    onLogicTypeReset();
  }, [onLogicTypeReset, setCurrentlyOpenContextMenu]);

  const cssClass = useMemo(() => {
    if (isLogicTypeSelected) {
      return `${LOGIC_TYPE_SELECTOR_CLASS} logic-type-selected`;
    } else {
      return `${LOGIC_TYPE_SELECTOR_CLASS} logic-type-not-present`;
    }
  }, [isLogicTypeSelected]);

  const clearContextMenuContainerRef = React.useRef<HTMLDivElement>(null);
  const {
    xPos: clearContextMenuXPos,
    yPos: clearContextMenuYPos,
    isOpen: isClearContextMenuOpen,
  } = useCustomContextMenuHandler(clearContextMenuContainerRef);

  const shouldRenderClearContextMenu = useMemo(() => {
    return isClearContextMenuOpen && isLogicTypeSelected && isClearSupported;
  }, [isClearContextMenuOpen, isClearSupported, isLogicTypeSelected]);

  return (
    <>
      <div
        className={cssClass}
        ref={clearContextMenuContainerRef}
        style={{ opacity: shouldRenderClearContextMenu ? 0.5 : 1 }}
      >
        {isLogicTypeSelected ? renderExpression : i18n.selectExpression}

        {!isLogicTypeSelected && (
          <PopoverMenu
            title={i18n.selectExpression}
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
      </div>
      {shouldRenderClearContextMenu && (
        <div
          className="context-menu-container"
          style={{ top: clearContextMenuYPos, left: clearContextMenuXPos, opacity: 1 }}
        >
          <Menu className="table-context-menu">
            <MenuGroup label={expression.logicType.toLocaleUpperCase()}>
              <MenuList>
                <MenuItem onClick={resetLogicType}>{i18n.clear}</MenuItem>
              </MenuList>
            </MenuGroup>
          </Menu>
        </div>
      )}
    </>
  );
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here");
}
