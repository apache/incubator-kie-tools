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

import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import { MenuItemWithHelp } from "../../contextMenu/MenuWithHelp";
import { Menu } from "@patternfly/react-core/dist/js/components/Menu/Menu";
import { MenuGroup } from "@patternfly/react-core/dist/js/components/Menu/MenuGroup";
import { MenuList } from "@patternfly/react-core/dist/js/components/Menu/MenuList";
import { DMN15__tFunctionKind } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { BoxedFunctionKind } from "../../api";

export interface FunctionKindSelectorProps {
  /** Pre-selected function kind */
  selectedFunctionKind: DMN15__tFunctionKind;
  /** Callback invoked when function kind selection changes */
  onFunctionKindSelect: (functionKind: DMN15__tFunctionKind) => void;
  /** If should only display function kind */
  isReadOnly?: boolean;
}

export const FunctionKindSelector: React.FunctionComponent<FunctionKindSelectorProps> = ({
  selectedFunctionKind,
  onFunctionKindSelect,
  isReadOnly,
}) => {
  const { editorRef } = useBoxedExpressionEditor();

  const functionKindSelectionCallback = useCallback(
    (hide: () => void) => (event?: React.MouseEvent, itemId?: string | number) => {
      onFunctionKindSelect(itemId as DMN15__tFunctionKind);
      setVisibleHelp("");
      hide();
    },
    [onFunctionKindSelect]
  );

  const functionKindHelp = useCallback((functionKind: DMN15__tFunctionKind) => {
    switch (functionKind) {
      case "FEEL":
        return "Define function as a 'Friendly Enough Expression Language (FEEL)' expression. This is the default.";
      case "Java":
        return "Define the full qualified java class name and a public static method signature to invoke.\nThe method signature consists of the name of the method, followed by an argument list of the argument types.";
      case "PMML":
        return "Define 'Predictive Model Markup Language (PMML)' model to invoke.\nEditor parses and offers you all your PMML models from the workspace.";
      default:
        return "Not supported";
    }
  }, []);

  const [visibleHelp, setVisibleHelp] = React.useState<string>("");
  const toggleVisibleHelp = useCallback((help: string) => {
    setVisibleHelp((previousHelp) => (previousHelp !== help ? help : ""));
  }, []);

  const displaySelectedFunctionKind = useMemo(
    () => (
      <div className="selected-function-kind" data-testid="kie-tools--bee--selected-function-kind">
        {_.first(selectedFunctionKind)}
      </div>
    ),
    [selectedFunctionKind]
  );

  if (isReadOnly) {
    return displaySelectedFunctionKind;
  }

  return (
    <PopoverMenu
      onHidden={() => setVisibleHelp("")}
      appendTo={editorRef?.current ?? undefined}
      className="function-kind-popover"
      position={PopoverPosition.leftEnd}
      hasAutoWidth={true}
      body={(hide: () => void) => (
        <Menu onSelect={functionKindSelectionCallback(hide)} selected={selectedFunctionKind}>
          <MenuGroup className="menu-with-help">
            <MenuList>
              {_.map(Object.entries(BoxedFunctionKind), ([functionKindKey, functionKind]) => (
                <MenuItemWithHelp
                  key={functionKindKey}
                  menuItemKey={functionKind}
                  menuItemHelp={functionKindHelp(functionKind)}
                  setVisibleHelp={toggleVisibleHelp}
                  visibleHelp={visibleHelp}
                />
              ))}
            </MenuList>
          </MenuGroup>
        </Menu>
      )}
    >
      {displaySelectedFunctionKind}
    </PopoverMenu>
  );
};
