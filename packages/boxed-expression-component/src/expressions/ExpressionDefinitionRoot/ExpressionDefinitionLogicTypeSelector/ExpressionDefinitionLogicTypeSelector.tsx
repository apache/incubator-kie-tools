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

import { Dropdown, DropdownToggle } from "@patternfly/react-core/dist/esm/components/Dropdown";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core/dist/js/components/Menu";
import { CopyIcon, CutIcon, ListIcon, PasteIcon, TableIcon } from "@patternfly/react-icons";
import CompressIcon from "@patternfly/react-icons/dist/js/icons/compress-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ExpressionDefinition, ExpressionDefinitionLogicType, generateUuid } from "../../../api";
import { useCustomContextMenuHandler } from "../../../contextMenu";
import { PopoverMenu } from "../../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../../i18n";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextExpression } from "../../ContextExpression";
import { DecisionTableExpression } from "../../DecisionTableExpression";
import { FunctionExpression } from "../../FunctionExpression";
import { InvocationExpression } from "../../InvocationExpression";
import { ListExpression } from "../../ListExpression";
import { LiteralExpression, PmmlLiteralExpression } from "../../LiteralExpression";
import { RelationExpression } from "../../RelationExpression";
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
  isResetSupported: boolean;
  isHeadless: boolean;
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
  isHeadless,
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
        return <LiteralExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.PmmlLiteral:
        return <PmmlLiteralExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.Relation:
        return <RelationExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.Context:
        return <ContextExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.DecisionTable:
        return <DecisionTableExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.Invocation:
        return <InvocationExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.List:
        return <ListExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.Function:
        return <FunctionExpression {...expression} isHeadless={isHeadless} />;
      case ExpressionDefinitionLogicType.Undefined:
        return <></>; // Shouldn't ever reach this point, though
      default:
        assertUnreachable(logicType);
    }
  }, [expression, isHeadless]);

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
    setDropdownOpen(false);
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
          <span
            style={{
              fontSize: "0.8em",
              fontWeight: "bold",
            }}
          >
            FEEL
          </span>
        );
      case ExpressionDefinitionLogicType.PmmlLiteral:
        return ``;
      case ExpressionDefinitionLogicType.Context:
        return (
          <span>
            <b>
              <i>{`{}`}</i>
            </b>
          </span>
        );
      case ExpressionDefinitionLogicType.DecisionTable:
        return <TableIcon />;
      case ExpressionDefinitionLogicType.Relation:
        return <TableIcon />;
      case ExpressionDefinitionLogicType.Function:
        return (
          <span>
            <b>
              <i>{"f"}</i>
            </b>
          </span>
        );
      case ExpressionDefinitionLogicType.Invocation:
        return (
          <span>
            <b>
              <i>{"f()"}</i>
            </b>
          </span>
        );
      case ExpressionDefinitionLogicType.List:
        return <ListIcon />;
      default:
        assertUnreachable(logicType);
    }
  }, []);

  const copyExpression = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(expression));
    setDropdownOpen(false);
  }, [expression]);

  const cutExpression = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(expression));
    onLogicTypeReset();
    setDropdownOpen(false);
  }, [expression, onLogicTypeReset]);

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const pasteExpression = useCallback(async () => {
    const expression: ExpressionDefinition = JSON.parse(await navigator.clipboard.readText(), (key, value) => {
      // We can't allow ids to be repeated, so we generate new ids for every expression that is part of the pasted expression.
      if (key === "id") {
        return generateUuid();
      } else {
        return value;
      }
    });
    setExpression(expression);
    setDropdownOpen(false);
  }, [setExpression]);

  const menuIconContainerStyle = useMemo(() => {
    return {
      width: "30px",
      userSelect: "none" as const,
      position: "relative" as const,
    };
  }, []);

  const showExpressionHeader = useMemo(() => {
    if (!isHeadless) {
      return true;
    }

    return (
      expression.logicType !== ExpressionDefinitionLogicType.Literal &&
      expression.logicType !== ExpressionDefinitionLogicType.PmmlLiteral
    );
  }, [expression.logicType, isHeadless]);

  const contextMenuItems = useMemo(() => {
    return (
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
    );
  }, [copyExpression, cutExpression, i18n, menuIconContainerStyle, pasteExpression, resetLogicType]);

  const [isDropdownOpen, setDropdownOpen] = useState(false);

  useEffect(() => {
    if (isResetContextMenuOpen) {
      setDropdownOpen(false);
    }
  }, [isResetContextMenuOpen]);

  return (
    <>
      <div className={cssClass} ref={resetContextMenuContainerRef}>
        {isLogicTypeSelected ? (
          <>
            {showExpressionHeader && (
              <div className={"logic-type-selected-header"}>
                <Dropdown
                  isPlain={true}
                  isOpen={isDropdownOpen}
                  toggle={
                    <DropdownToggle
                      icon={<>{logicTypeIcon(expression.logicType)}</>}
                      style={{ padding: 0 }}
                      onToggle={setDropdownOpen}
                    >
                      {expression.logicType}
                    </DropdownToggle>
                  }
                >
                  <Menu className="table-context-menu" style={{ width: "200px", fontSize: "larger" }}>
                    <>{contextMenuItems}</>
                  </Menu>
                </Dropdown>
                {/* <div
                style={{ textAlign: "left", display: "inline" }}
              >{`Depth: ${depth}, Active: ${currentDepth.active} (max: ${currentDepth.max})`}</div> */}
              </div>
            )}
            {renderExpression}
          </>
        ) : (
          i18n.selectExpression
        )}

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
            <MenuGroup label={`${expression.logicType.toLocaleUpperCase()} EXPRESSION`}></MenuGroup>
            {contextMenuItems}
          </Menu>
        </div>
      )}
    </>
  );
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here: " + _x);
}
