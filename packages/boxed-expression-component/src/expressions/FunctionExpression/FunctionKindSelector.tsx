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

import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as _ from "lodash";
import * as React from "react";
import { useCallback } from "react";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { FunctionExpressionDefinitionKind } from "../../api";
import { MenuItemWithHelp } from "../../contextMenu/MenuWithHelp/MenuItemWithHelp";
import { Menu } from "@patternfly/react-core/dist/js/components/Menu/Menu";
import { MenuGroup } from "@patternfly/react-core/dist/js/components/Menu/MenuGroup";
import { MenuList } from "@patternfly/react-core/dist/js/components/Menu/MenuList";

export interface FunctionKindSelectorProps {
  /** Pre-selected function kind */
  selectedFunctionKind: FunctionExpressionDefinitionKind;
  /** Callback invoked when function kind selection changes */
  onFunctionKindSelect: (functionKind: FunctionExpressionDefinitionKind) => void;
}

export const FunctionKindSelector: React.FunctionComponent<FunctionKindSelectorProps> = ({
  selectedFunctionKind,
  onFunctionKindSelect,
}) => {
  const { editorRef } = useBoxedExpressionEditor();

  const functionKindSelectionCallback = useCallback(
    (hide: () => void) => (event?: React.MouseEvent, itemId?: string | number) => {
      onFunctionKindSelect(itemId as FunctionExpressionDefinitionKind);
      setVisibleHelp("");
      hide();
      setTimeout(() => {
        onFunctionKindSelect(itemId as FunctionExpressionDefinitionKind);
      }, 0);
    },
    [onFunctionKindSelect]
  );

  const functionKindHelp = useCallback((functionKind: FunctionExpressionDefinitionKind) => {
    switch (functionKind) {
      case FunctionExpressionDefinitionKind.Feel:
        return "Define function as a 'Friendly Enough Expression Language (FEEL)' expression. This is the default.";

      case FunctionExpressionDefinitionKind.Java:
        return "Define the full qualified java class name and a public static method signature to invoke.\nThe method signature consists of the name of the method, followed by an argument list of the argument types.";

      case FunctionExpressionDefinitionKind.Pmml:
        return "Define 'Predictive Model Markup Language (PMML)' model to invoke.\nEditor parses and offers you all your PMML models from the workspace.";
      default:
        return "Not supported";
    }
  }, []);

  const [visibleHelp, setVisibleHelp] = React.useState<string>("");
  const toggleVisibleHelp = useCallback((help: string) => {
    setVisibleHelp((previousHelp) => (previousHelp !== help ? help : ""));
  }, []);

  return (
    <PopoverMenu
      onHide={() => setVisibleHelp("")}
      appendTo={editorRef?.current ?? undefined}
      className="function-kind-popover"
      position={PopoverPosition.leftEnd}
      hasAutoWidth={true}
      body={(hide: () => void) => (
        <Menu onSelect={functionKindSelectionCallback(hide)} selected={selectedFunctionKind}>
          <MenuGroup className="menu-with-help">
            <MenuList>
              {_.map(Object.entries(FunctionExpressionDefinitionKind), ([functionKindKey, functionKind]) => (
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
      <div className="selected-function-kind">{_.first(selectedFunctionKind)}</div>
    </PopoverMenu>
  );
};
