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
import { Menu, MenuItem, MenuList } from "@patternfly/react-core/dist/js/components/Menu";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as _ from "lodash";
import * as React from "react";
import { useCallback } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { FunctionExpressionDefinitionKind } from "../../api";

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
  const { i18n } = useBoxedExpressionEditorI18n();
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const functionKindSelectionCallback = useCallback(
    (hide: () => void) => (event?: React.MouseEvent, itemId?: string | number) => {
      boxedExpressionEditor.beeGwtService?.notifyUserAction();
      onFunctionKindSelect(itemId as FunctionExpressionDefinitionKind);
      hide();
    },
    [boxedExpressionEditor.beeGwtService, onFunctionKindSelect]
  );

  const renderFunctionKindItems = useCallback(
    () =>
      _.map(Object.values(FunctionExpressionDefinitionKind), (key) => (
        <MenuItem key={key} itemId={key} data-ouia-component-id={key}>
          {key}
        </MenuItem>
      )),
    []
  );

  return (
    <PopoverMenu
      title={i18n.selectFunctionKind}
      appendTo={boxedExpressionEditor.editorRef?.current ?? undefined}
      className="function-kind-popover"
      position={PopoverPosition.leftEnd}
      hasAutoWidth={true}
      body={(hide: () => void) => (
        <Menu onSelect={functionKindSelectionCallback(hide)}>
          <MenuList>{renderFunctionKindItems()}</MenuList>
        </Menu>
      )}
    >
      <div className="selected-function-kind">{_.first(selectedFunctionKind)}</div>
    </PopoverMenu>
  );
};
