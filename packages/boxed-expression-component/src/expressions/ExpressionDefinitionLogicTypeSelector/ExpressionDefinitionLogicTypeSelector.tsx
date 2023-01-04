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
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextExpression } from "../ContextExpression";
import { DecisionTableExpression } from "../DecisionTableExpression";
import { FunctionExpression } from "../FunctionExpression";
import { InvocationExpression } from "../InvocationExpression";
import { ListExpression } from "../ListExpression";
import { LiteralExpression, PmmlLiteralExpression } from "../LiteralExpression";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { RelationExpression } from "../RelationExpression";
import "./ExpressionDefinitionLogicTypeSelector.css";
import CompressIcon from "@patternfly/react-icons/dist/js/icons/compress-icon";
import { CopyIcon, CutIcon, ListIcon, PasteIcon, TableIcon } from "@patternfly/react-icons";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export interface ExpressionDefinitionLogicTypeSelectorProps {
  /** Expression properties */
  expression: ExpressionDefinition;
  /** Function to be invoked when logic type changes */
  onLogicTypeSelected: (logicType: ExpressionDefinitionLogicType) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeReset: () => void;
  /** Function to be invoked to retrieve the DOM reference to be used for selector placement */
  getPlacementRef: () => HTMLDivElement;
  isResetSupported: boolean;
}

const NON_SELECTABLE_LOGIC_TYPES = [ExpressionDefinitionLogicType.Undefined, ExpressionDefinitionLogicType.PmmlLiteral];

const SELECTABLE_LOGIC_TYPES = Object.values(ExpressionDefinitionLogicType).filter((logicType) => {
  return !NON_SELECTABLE_LOGIC_TYPES.includes(logicType);
});

export function ExpressionDefinitionLogicTypeSelector({
  expression,
  onLogicTypeSelected,
  onLogicTypeReset,
  getPlacementRef,
  isResetSupported,
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
      case ExpressionDefinitionLogicType.Literal:
        return <LiteralExpression {...expression} />;
      case ExpressionDefinitionLogicType.PmmlLiteral:
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
      return `logic-type-selector logic-type-selected`;
    } else {
      return `logic-type-selector logic-type-not-present`;
    }
  }, [isLogicTypeSelected]);

  const resetContextMenuContainerRef = React.useRef<HTMLDivElement>(null);
  const {
    xPos: resetContextMenuXPos,
    yPos: resetContextMenuYPos,
    isOpen: isResetContextMenuOpen,
  } = useCustomContextMenuHandler(resetContextMenuContainerRef);

  const shouldRenderResetContextMenu = useMemo(() => {
    return isResetContextMenuOpen && isLogicTypeSelected && isResetSupported;
  }, [isResetContextMenuOpen, isResetSupported, isLogicTypeSelected]);

  const logicTypeIcon = useCallback((logicType: ExpressionDefinitionLogicType) => {
    switch (logicType) {
      case ExpressionDefinitionLogicType.Undefined:
        return ``;
      case ExpressionDefinitionLogicType.Literal:
        return (
          <div
            style={{
              fontSize: "0.8em",
              fontWeight: "bold",
              transform: "scaleX(0.7)",
              position: "absolute",
              top: "-8px",
              left: "-5px",
            }}
          >
            FEEL
          </div>
        );
      case ExpressionDefinitionLogicType.PmmlLiteral:
        return ``;
      case ExpressionDefinitionLogicType.Context:
        return (
          <div>
            <b>
              <i>{`{}`}</i>
            </b>
          </div>
        );
      case ExpressionDefinitionLogicType.DecisionTable:
        return <TableIcon />;
      case ExpressionDefinitionLogicType.Relation:
        return <TableIcon />;
      case ExpressionDefinitionLogicType.Function:
        return (
          <div>
            <b>
              <i>{"f"}</i>
            </b>
          </div>
        );
      case ExpressionDefinitionLogicType.Invocation:
        return (
          <div>
            <b>
              <i>{"f()"}</i>
            </b>
          </div>
        );
      case ExpressionDefinitionLogicType.List:
        return <ListIcon />;
      default:
        assertUnreachable(logicType);
    }
  }, []);

  const copyExpression = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(expression));
  }, [expression]);

  const cutExpression = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(expression));
    onLogicTypeReset();
  }, [expression, onLogicTypeReset]);

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const pasteExpression = useCallback(async () => {
    const expression: ExpressionDefinition = JSON.parse(await navigator.clipboard.readText());
    setExpression(expression);
  }, [setExpression]);

  const menuIconContainerStyle = useMemo(() => {
    return {
      width: "30px",
      userSelect: "none" as const,
      position: "relative" as const,
    };
  }, []);

  return (
    <>
      <div
        className={cssClass}
        ref={resetContextMenuContainerRef}
        style={{ opacity: shouldRenderResetContextMenu ? 0.5 : 1 }}
      >
        {isLogicTypeSelected ? renderExpression : i18n.selectExpression}

        {!isLogicTypeSelected && (
          <PopoverMenu
            arrowPlacement={getPopoverArrowPlacement}
            appendTo={getPopoverContainer()}
            className="logic-type-popover"
            hasAutoWidth={true}
            body={
              <>
                <Menu onSelect={selectLogicType}>
                  <MenuList>
                    {SELECTABLE_LOGIC_TYPES.map((key) => (
                      <MenuItem
                        key={key}
                        itemId={key}
                        icon={
                          <div style={menuIconContainerStyle}>
                            <>{logicTypeIcon(key)}</>
                          </div>
                        }
                      >
                        {key}
                      </MenuItem>
                    ))}
                  </MenuList>
                  <Divider style={{ padding: "16px" }} />
                </Menu>
                <Menu>
                  <MenuList>
                    <MenuItem
                      onClick={pasteExpression}
                      icon={
                        <div style={menuIconContainerStyle}>
                          <PasteIcon />
                        </div>
                      }
                    >
                      {i18n.terms.paste}
                    </MenuItem>
                  </MenuList>
                </Menu>
              </>
            }
          />
        )}
      </div>
      {shouldRenderResetContextMenu && (
        <div
          className="context-menu-container"
          style={{
            top: resetContextMenuYPos,
            left: resetContextMenuXPos,
            opacity: 1,
            minWidth: "150px",
          }}
        >
          <Menu className="table-context-menu">
            <MenuGroup label={`${expression.logicType.toLocaleUpperCase()} EXPRESSION`}>
              <MenuList>
                <MenuItem
                  onClick={resetLogicType}
                  icon={
                    <div style={menuIconContainerStyle}>
                      <CompressIcon />
                    </div>
                  }
                >
                  {i18n.terms.reset}
                </MenuItem>
                <Divider style={{ padding: "16px", margin: 0 }} />
                <MenuItem
                  onClick={copyExpression}
                  icon={
                    <div style={menuIconContainerStyle}>
                      <CopyIcon />
                    </div>
                  }
                >
                  {i18n.terms.copy}
                </MenuItem>
                <MenuItem
                  onClick={cutExpression}
                  icon={
                    <div style={menuIconContainerStyle}>
                      <CutIcon />
                    </div>
                  }
                >
                  {i18n.terms.cut}
                </MenuItem>
                <MenuItem
                  onClick={pasteExpression}
                  icon={
                    <div style={menuIconContainerStyle}>
                      <PasteIcon />
                    </div>
                  }
                >
                  {i18n.terms.paste}
                </MenuItem>
              </MenuList>
            </MenuGroup>
          </Menu>
        </div>
      )}
    </>
  );
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here: " + _x);
}
