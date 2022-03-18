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

import * as React from "react";
import { DataListCell, DataListCheck, DataListItem, DataListItemRow } from "@patternfly/react-core";
import { useCallback, useState } from "react";

export interface ImportJavaClassesWizardClassListTableItemsProps {
  /** Item class name */
  fullClassName: string;
  /** Item checkbox status */
  selected: boolean;
  /** Function to call when an item related checkbox is pressed by the user */
  onJavaClassItemSelected: (fullClassName: string, add: boolean) => void;
}

export const ImportJavaClassesWizardClassListTableItems: React.FunctionComponent<ImportJavaClassesWizardClassListTableItemsProps> =
  ({ fullClassName, selected, onJavaClassItemSelected }: ImportJavaClassesWizardClassListTableItemsProps) => {
    const [itemChecked, setItemChecked] = useState(selected);
    const onDataListCheckChange = useCallback(() => {
      onJavaClassItemSelected(fullClassName, !itemChecked);
      setItemChecked(!itemChecked);
    }, [fullClassName, itemChecked, onJavaClassItemSelected]);

    return (
      <DataListItem name={fullClassName}>
        <DataListItemRow>
          <DataListCheck aria-labelledby={fullClassName} checked={itemChecked} onChange={onDataListCheckChange} />
          <DataListCell>
            <span id={fullClassName}>{fullClassName}</span>
          </DataListCell>
        </DataListItemRow>
      </DataListItem>
    );
  };
