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
import { Menu } from "@patternfly/react-core/dist/js/components/Menu/Menu";
import { MenuGroup } from "@patternfly/react-core/dist/js/components/Menu/MenuGroup";
import { MenuItem } from "@patternfly/react-core/dist/js/components/Menu/MenuItem";
import { MenuList } from "@patternfly/react-core/dist/js/components/Menu/MenuList";
import { CompressIcon } from "@patternfly/react-icons/dist/js/icons/compress-icon";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { CutIcon } from "@patternfly/react-icons/dist/js/icons/cut-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { PasteIcon } from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ExpressionDefinition, ExpressionDefinitionLogicType, generateUuid } from "../../../api";
import { useCustomContextMenuHandler } from "../../../contextMenu";
import { MenuItemWithHelp } from "../../../contextMenu/MenuWithHelp/MenuItemWithHelp";
import { PopoverMenu } from "../../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../../i18n";
import { useNestedExpressionContainer } from "../../../resizing/NestedExpressionContainerContext";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextExpression } from "../../ContextExpression";
import { DecisionTableExpression } from "../../DecisionTableExpression";
import { FunctionExpression } from "../../FunctionExpression";
import { InvocationExpression } from "../../InvocationExpression";
import { ListExpression } from "../../ListExpression";
import { LiteralExpression } from "../../LiteralExpression";
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
  isNested: boolean;
}

export function ExpressionDefinitionLogicTypeSelector({
  expression,
  onLogicTypeSelected,
  onLogicTypeReset,
  getPlacementRef,
  isResetSupported,
  isNested,
}: ExpressionDefinitionLogicTypeSelectorProps) {
  const nonSelectableLogicTypes = useMemo(
    () =>
      isNested
        ? new Set([ExpressionDefinitionLogicType.Undefined])
        : new Set([ExpressionDefinitionLogicType.Undefined]),
    [isNested]
  );

  const selectableLogicTypes = useMemo(
    () =>
      Object.values(ExpressionDefinitionLogicType).filter((logicType) => {
        return !nonSelectableLogicTypes.has(logicType);
      }),
    [nonSelectableLogicTypes]
  );

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
        return <LiteralExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.Relation:
        return <RelationExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.Context:
        return <ContextExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.DecisionTable:
        return <DecisionTableExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.Invocation:
        return <InvocationExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.List:
        return <ListExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.Function:
        return <FunctionExpression {...expression} isNested={isNested} />;
      case ExpressionDefinitionLogicType.Undefined:
        return <></>; // Shouldn't ever reach this point, though
      default:
        assertUnreachable(logicType);
    }
  }, [expression, isNested]);

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
      setPasteExpressionError("");
      setVisibleHelp("");
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
          <span>
            <b>
              <i>FEEL</i>
            </b>
          </span>
        );
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

  const [pasteExpressionError, setPasteExpressionError] = React.useState<string>("");

  const pasteExpression = useCallback(async () => {
    try {
      const expression = JSON.parse(await navigator.clipboard.readText(), (key: string, value: string) => {
        // We can't allow ids to be repeated, so we generate new ids for every expression that is part of the pasted expression.
        if (key === "id") {
          return generateUuid();
        } else {
          return value;
        }
      });

      if (!expression?.id) {
        // FIXME: The ideal here would be validating the expression as a whole, but just looking at the ID already prevents errors when pasting plain strings and numbers.
        throw new Error("Pasted expression doesn't have an ID. This means that it's not a valid JSON.");
      }

      setExpression(expression);
      setDropdownOpen(false);
      setCurrentlyOpenContextMenu(undefined);
      setPasteExpressionError("");
    } catch (err) {
      setPasteExpressionError(err);
    }
  }, [setCurrentlyOpenContextMenu, setExpression]);

  const menuIconContainerStyle = useMemo(() => {
    return {
      width: "40px",
      userSelect: "none" as const,
      position: "relative" as const,
    };
  }, []);

  const showExpressionHeader = useMemo(() => {
    if (!isNested) {
      return true;
    }

    return (
      expression.logicType !== ExpressionDefinitionLogicType.Literal &&
      !nonSelectableLogicTypes.has(expression.logicType)
    );
  }, [expression.logicType, isNested, nonSelectableLogicTypes]);

  const logicTypeHelp = useCallback((logicType: ExpressionDefinitionLogicType) => {
    switch (logicType) {
      case ExpressionDefinitionLogicType.Literal:
        return "A boxed literal expression in DMN is a literal FEEL expression as text in a table cell, typically with a labeled column and an assigned data type.";
      case ExpressionDefinitionLogicType.Context:
        return "A boxed context expression in DMN is a set of variable names and values with a result value. Each name-value pair is a context entry.";
      case ExpressionDefinitionLogicType.DecisionTable:
        return "A decision table in DMN is a visual representation of one or more business rules in a tabular format.";
      case ExpressionDefinitionLogicType.Relation:
        return "A boxed relation expression in DMN is a traditional data table with information about given entities, listed as rows. You use boxed relation tables to define decision data for relevant entities in a decision at a particular node.";
      case ExpressionDefinitionLogicType.Function:
        return "A boxed function expression in DMN is a parameterized boxed expression containing a literal FEEL expression, a nested context expression of an external JAVA or PMML function, or a nested boxed expression of any type.";
      case ExpressionDefinitionLogicType.Invocation:
        return "A boxed invocation expression in DMN is a boxed expression that invokes a business knowledge model. A boxed invocation expression contains the name of the business knowledge model to be invoked and a list of parameter bindings.";
      case ExpressionDefinitionLogicType.List:
        return "A boxed list expression in DMN represents a FEEL list of items. You use boxed lists to define lists of relevant items for a particular node in a decision.";
      default:
        return "";
    }
  }, []);

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
          className={pasteExpressionError ? "paste-from-clipboard-error" : ""}
          description={pasteExpressionError ? "Paste operation was not successful" : ""}
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
  }, [
    pasteExpressionError,
    copyExpression,
    cutExpression,
    i18n,
    menuIconContainerStyle,
    pasteExpression,
    resetLogicType,
  ]);

  const [isDropdownOpen, setDropdownOpen] = useState(false);

  useEffect(() => {
    if (isResetContextMenuOpen) {
      setDropdownOpen(false);
    }
  }, [isResetContextMenuOpen]);

  const nestedExpressionContainer = useNestedExpressionContainer();
  const [visibleHelp, setVisibleHelp] = React.useState<string>("");

  const toggleVisibleHelp = useCallback((help: string) => {
    setVisibleHelp((previousHelp) => (previousHelp !== help ? help : ""));
  }, []);

  return (
    <>
      <div
        className={cssClass}
        ref={resetContextMenuContainerRef}
        style={
          !isLogicTypeSelected && nestedExpressionContainer.resizingWidth
            ? { width: `${nestedExpressionContainer.resizingWidth?.value}px` }
            : {}
        }
      >
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
                      tabIndex={-1}
                    >
                      {expression.logicType}
                      {expression.logicType === ExpressionDefinitionLogicType.Function &&
                        ` (${expression.functionKind})`}
                    </DropdownToggle>
                  }
                >
                  <Menu className="table-context-menu" style={{ width: "200px", fontSize: "larger" }}>
                    <>{contextMenuItems}</>
                  </Menu>
                </Dropdown>
              </div>
            )}
            {renderExpression}
          </>
        ) : (
          i18n.selectExpression
        )}

        {!isLogicTypeSelected && (
          <PopoverMenu
            onHide={() => {
              setPasteExpressionError("");
              setVisibleHelp("");
            }}
            arrowPlacement={getPopoverArrowPlacement}
            appendTo={getPopoverContainer()}
            className="logic-type-popover"
            hasAutoWidth={true}
            body={
              <>
                <Menu onSelect={selectLogicType}>
                  <MenuGroup className="menu-with-help">
                    <MenuList>
                      <>
                        {selectableLogicTypes.map((key) => (
                          <MenuItemWithHelp
                            key={key}
                            menuItemKey={key}
                            menuItemHelp={logicTypeHelp(key)}
                            menuItemIcon={logicTypeIcon(key)}
                            menuItemIconStyle={menuIconContainerStyle}
                            setVisibleHelp={toggleVisibleHelp}
                            visibleHelp={visibleHelp}
                          />
                        ))}
                        <Divider style={{ padding: "16px" }} />
                      </>
                    </MenuList>
                  </MenuGroup>
                </Menu>
                <Menu>
                  <MenuList>
                    <MenuItem
                      className={pasteExpressionError ? "paste-from-clipboard-error" : ""}
                      description={pasteExpressionError ? "Paste operation was not successful" : ""}
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
