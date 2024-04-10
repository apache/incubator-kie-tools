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
import { BoxedExpression } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu";
import { MenuItemWithHelp } from "../../contextMenu/MenuWithHelp";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainer } from "../../resizing/NestedExpressionContainerContext";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { ContextExpression } from "../ContextExpression/ContextExpression";
import { DecisionTableExpression } from "../DecisionTableExpression/DecisionTableExpression";
import { FunctionExpression } from "../FunctionExpression/FunctionExpression";
import { InvocationExpression } from "../InvocationExpression/InvocationExpression";
import { ListExpression } from "../ListExpression/ListExpression";
import { LiteralExpression } from "../LiteralExpression/LiteralExpression";
import { RelationExpression } from "../RelationExpression/RelationExpression";
import {
  BoxedExpressionClipboard,
  DMN_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE,
  buildClipboardFromExpression,
} from "../../clipboard/clipboard";
import { findAllIdsDeep, mutateExpressionRandomizingIds } from "../../ids/ids";
import "./ExpressionDefinitionLogicTypeSelector.css";
import { NavigationKeysUtils } from "../../keysUtils/keyUtils";
import { ConditionalExpression } from "../ConditionalExpression/ConditionalExpression";

export interface ExpressionDefinitionLogicTypeSelectorProps {
  /** Expression properties */
  expression?: BoxedExpression;
  /** Function to be invoked when logic type changes */
  onLogicTypeSelected: (logicType: BoxedExpression["__$$element"] | undefined) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeReset: () => void;
  /** Function to be invoked to retrieve the DOM reference to be used for selector placement */
  getPlacementRef: () => HTMLDivElement;
  isResetSupported: boolean;
  isNested: boolean;
  parentElementId: string;
  hideDmn14BoxedExpressions?: boolean;
}

export function ExpressionDefinitionLogicTypeSelector({
  expression,
  onLogicTypeSelected,
  onLogicTypeReset,
  getPlacementRef,
  isResetSupported,
  isNested,
  parentElementId,
  hideDmn14BoxedExpressions,
}: ExpressionDefinitionLogicTypeSelectorProps) {
  const nonSelectableLogicTypes = useMemo<Set<BoxedExpression["__$$element"] | undefined>>(
    () => (isNested ? new Set([undefined]) : new Set([undefined, "functionDefinition"])),
    [isNested]
  );

  const selectableLogicTypes = useMemo<Array<BoxedExpression["__$$element"]>>(
    () => [
      "literalExpression",
      "relation",
      "context",
      "decisionTable",
      "list",
      "invocation",
      ...(isNested ? (["functionDefinition"] as const) : []),
      ...(!hideDmn14BoxedExpressions ? (["conditional"] as const) : []),
      // "for",
      // "every",
      // "some",
      // "filter",
    ],
    [isNested]
  );

  const { i18n } = useBoxedExpressionEditorI18n();

  const { setCurrentlyOpenContextMenu, editorRef, widthsById } = useBoxedExpressionEditor();

  const renderExpression = useMemo(() => {
    const logicType = expression?.__$$element;
    if (!logicType) {
      return <></>;
    }
    switch (logicType) {
      case "literalExpression":
        return <LiteralExpression {...expression} isNested={isNested} />;
      case "relation":
        return <RelationExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "context":
        return <ContextExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "decisionTable":
        return <DecisionTableExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "list":
        return <ListExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "invocation":
        return <InvocationExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "functionDefinition":
        return <FunctionExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "conditional":
        return <ConditionalExpression {...expression} isNested={isNested} parentElementId={parentElementId} />;
      case "for":
      case "every":
      case "some":
      case "filter":
        return <></>;
      default:
        assertUnreachable(logicType);
    }
  }, [expression, isNested, parentElementId]);

  const getPopoverArrowPlacement = useCallback(() => {
    return getPlacementRef() as HTMLDivElement;
  }, [getPlacementRef]);

  const getPopoverContainer = useCallback(() => {
    return editorRef?.current ?? getPopoverArrowPlacement;
  }, [getPopoverArrowPlacement, editorRef]);

  const selectLogicType = useCallback(
    (_: React.MouseEvent, itemId?: string | number) => {
      onLogicTypeSelected(itemId as BoxedExpression["__$$element"] | undefined);
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
    if (expression) {
      return `logic-type-selector logic-type-selected`;
    } else {
      return `logic-type-selector logic-type-not-present`;
    }
  }, [expression]);

  const resetContextMenuContainerRef = React.useRef<HTMLDivElement>(null);
  const {
    xPos: resetContextMenuXPos,
    yPos: resetContextMenuYPos,
    isOpen: isResetContextMenuOpen,
  } = useCustomContextMenuHandler(resetContextMenuContainerRef);

  const shouldRenderResetContextMenu = useMemo(() => {
    return isResetContextMenuOpen && expression && isResetSupported;
  }, [isResetContextMenuOpen, isResetSupported, expression]);

  const logicTypeIcon = useCallback((logicType: BoxedExpression["__$$element"] | undefined) => {
    switch (logicType) {
      case undefined:
        return ``;
      case "literalExpression":
        return (
          <span>
            <b>
              <i>FEEL</i>
            </b>
          </span>
        );
      case "context":
        return (
          <span>
            <b>
              <i>{`{}`}</i>
            </b>
          </span>
        );
      case "decisionTable":
        return <TableIcon />;
      case "relation":
        return <TableIcon />;
      case "functionDefinition":
        return (
          <span>
            <b>
              <i>{"f"}</i>
            </b>
          </span>
        );
      case "invocation":
        return (
          <span>
            <b>
              <i>{"f()"}</i>
            </b>
          </span>
        );
      case "list":
        return <ListIcon />;
      case "conditional":
        return (
          <span>
            <b>
              <i>{"if"}</i>
            </b>
          </span>
        );
      case "for":
      case "every":
      case "some":
      case "filter":
        return <></>;
      default:
        assertUnreachable(logicType);
    }
  }, []);

  const copyExpression = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(buildClipboardFromExpression(expression!, widthsById)));
    setDropdownOpen(false);
  }, [expression, widthsById]);

  const cutExpression = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(buildClipboardFromExpression(expression!, widthsById)));
    onLogicTypeReset();
    setDropdownOpen(false);
  }, [expression, onLogicTypeReset, widthsById]);

  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();

  const [pasteExpressionError, setPasteExpressionError] = React.useState<string>("");

  const pasteExpression = useCallback(async () => {
    try {
      const clipboard: BoxedExpressionClipboard = JSON.parse(await navigator.clipboard.readText());
      if (clipboard.mimeType !== DMN_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE) {
        throw new Error(
          "Pasted expression doesn't have the correct mime-type. Likely not copied from the Boxed Expression Editor."
        );
      }

      const newIdsByOriginalId = mutateExpressionRandomizingIds(clipboard.expression);

      let oldExpression: BoxedExpression | undefined;
      setExpression((prev: BoxedExpression) => {
        oldExpression = prev;
        return clipboard.expression;
      }); // This is mutated to have new IDs by the ID randomizer above.

      setWidthsById(({ newMap }) => {
        for (const id of findAllIdsDeep(oldExpression)) {
          newMap.delete(id);
        }
        for (const originalId in clipboard.widthsById) {
          newMap.set(newIdsByOriginalId.get(originalId)!, clipboard.widthsById[originalId]);
        }
      });

      setDropdownOpen(false);
      setCurrentlyOpenContextMenu(undefined);
      setPasteExpressionError("");
    } catch (err) {
      setPasteExpressionError(err);
    }
  }, [setCurrentlyOpenContextMenu, setExpression, setWidthsById]);

  const menuIconContainerStyle = useMemo(() => {
    return {
      width: "40px",
      userSelect: "none" as const,
      position: "relative" as const,
    };
  }, []);

  const showExpressionHeader = useMemo(() => {
    if (!expression) {
      return false;
    }
    if (!isNested) {
      return true;
    }

    return expression.__$$element !== "literalExpression" && !nonSelectableLogicTypes.has(expression.__$$element);
  }, [expression, isNested, nonSelectableLogicTypes]);

  const logicTypeHelp = useCallback((logicType: BoxedExpression["__$$element"] | undefined) => {
    switch (logicType) {
      case "literalExpression":
        return "A boxed literal expression in DMN is a literal FEEL expression as text in a table cell, typically with a labeled column and an assigned data type.";
      case "context":
        return "A boxed context expression in DMN is a set of variable names and values with a result value. Each name-value pair is a context entry.";
      case "decisionTable":
        return "A decision table in DMN is a visual representation of one or more business rules in a tabular format.";
      case "relation":
        return "A boxed relation expression in DMN is a traditional data table with information about given entities, listed as rows. You use boxed relation tables to define decision data for relevant entities in a decision at a particular node.";
      case "functionDefinition":
        return "A boxed function expression in DMN is a parameterized boxed expression containing a literal FEEL expression, a nested context expression of an external JAVA or PMML function, or a nested boxed expression of any type.";
      case "invocation":
        return "A boxed invocation expression in DMN is a boxed expression that invokes a business knowledge model. A boxed invocation expression contains the name of the business knowledge model to be invoked and a list of parameter bindings.";
      case "list":
        return "A boxed list expression in DMN represents a FEEL list of items. You use boxed lists to define lists of relevant items for a particular node in a decision.";
      case "conditional":
        return 'A boxed conditional offers a visual representation of an if statement using three rows. The expression in the "if" part MUST resolve to a boolean.';
      default:
        return "";
    }
  }, []);

  const contextMenuItems = useMemo(() => {
    return (
      <MenuList>
        {isResetSupported && (
          <>
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
          </>
        )}
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
        {isResetSupported && (
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
        )}
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
    isResetSupported,
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
          !expression && nestedExpressionContainer.resizingWidth
            ? { width: `${nestedExpressionContainer.resizingWidth?.value}px` }
            : {}
        }
      >
        {expression ? (
          <>
            {showExpressionHeader && (
              <div className={"logic-type-selected-header"}>
                <Dropdown
                  data-testid={"logic-type-selected-header"}
                  isPlain={true}
                  isOpen={isDropdownOpen}
                  onKeyDown={(e) => {
                    if (NavigationKeysUtils.isEsc(e.key)) {
                      setDropdownOpen(false);
                    }
                  }}
                  toggle={
                    <DropdownToggle
                      data-testid={"logic-type-button-test-id"}
                      icon={<>{logicTypeIcon(expression.__$$element)}</>}
                      style={{ padding: 0 }}
                      onToggle={setDropdownOpen}
                      tabIndex={-1}
                    >
                      {getLogicTypeLabel(expression?.__$$element)}
                      {expression.__$$element === "functionDefinition" && ` (${expression["@_kind"]})`}
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

        {!expression && (
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
                        {selectableLogicTypes.map((key) => {
                          const label = getLogicTypeLabel(key);
                          return (
                            <MenuItemWithHelp
                              key={key}
                              menuItemKey={key}
                              menuItemHelp={logicTypeHelp(key)}
                              menuItemIcon={logicTypeIcon(key)}
                              menuItemCustomText={label}
                              menuItemIconStyle={menuIconContainerStyle}
                              setVisibleHelp={toggleVisibleHelp}
                              visibleHelp={visibleHelp}
                            />
                          );
                        })}
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
          onKeyDown={(e) => {
            if (NavigationKeysUtils.isEsc(e.key)) {
              setDropdownOpen(false);
            }
          }}
        >
          <Menu className="table-context-menu">
            <MenuGroup
              label={`${getLogicTypeLabel(expression?.__$$element).toLocaleUpperCase()} EXPRESSION`}
            ></MenuGroup>
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

function getLogicTypeLabel(logicType: BoxedExpression["__$$element"] | undefined) {
  switch (logicType) {
    case undefined:
      return "Undefined";
    case "context":
      return "Context";
    case "literalExpression":
      return "Literal";
    case "relation":
      return "Relation";
    case "decisionTable":
      return "Decision table";
    case "list":
      return "List";
    case "invocation":
      return "Invocation";
    case "functionDefinition":
      return "Function";
    case "for":
      return "For";
    case "every":
      return "Every";
    case "some":
      return "Some";
    case "conditional":
      return "Conditional";
    case "filter":
      return "Filter";
  }
}
