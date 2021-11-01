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

import { PopoverMenu } from "../PopoverMenu";
import { Menu, MenuItem, MenuList } from "@patternfly/react-core";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useContext } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { BoxedExpressionGlobalContext } from "../../context";
import { FunctionKind } from "../../api";

export interface FunctionKindSelectorProps {
  /** Pre-selected function kind */
  selectedFunctionKind: FunctionKind;
  /** Callback invoked when function kind selection changes */
  onFunctionKindSelect: (functionKind: FunctionKind) => void;
}

export const FunctionKindSelector: React.FunctionComponent<FunctionKindSelectorProps> = ({
  selectedFunctionKind,
  onFunctionKindSelect,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const functionKindSelectionCallback = useCallback(
    (hide: () => void) => (event?: React.MouseEvent, itemId?: string | number) => {
      onFunctionKindSelect(itemId as FunctionKind);
      hide();
    },
    [onFunctionKindSelect]
  );

  const renderFunctionKindItems = useCallback(
    () =>
      _.map(Object.values(FunctionKind), (key) => (
        <MenuItem key={key} itemId={key} data-ouia-component-id={key}>
          {key}
        </MenuItem>
      )),
    []
  );

  return (
    <PopoverMenu
      title={i18n.selectFunctionKind}
      appendTo={globalContext.boxedExpressionEditorRef?.current ?? undefined}
      className="function-kind-popover"
      hasAutoWidth
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
