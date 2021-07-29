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
import { DataList } from "@patternfly/react-core";
import { ImportJavaClassesWizardClassListTableItems } from "./ImportJavaClassesWizardClassListTableItems";

export interface ImportJavaClassesWizardClassListTableProps {
  /** Previously selected Java Classes from the user */
  selectedJavaClasses: string[];
  /** Retrieved JavaClasses from external service */
  retrievedJavaClasses: string[];
  /** Function to call when an item related checkbox is pressed by the user */
  onJavaClassItemSelected: (fullClassName: string, add: boolean) => void;
}

export const ImportJavaClassesWizardClassListTable: React.FunctionComponent<ImportJavaClassesWizardClassListTableProps> =
  ({
    selectedJavaClasses,
    retrievedJavaClasses,
    onJavaClassItemSelected,
  }: ImportJavaClassesWizardClassListTableProps) => {
    const classesSet = new Set(selectedJavaClasses);
    return (
      <DataList aria-label={"class-data-list"} isCompact>
        {selectedJavaClasses.map((value) => (
          <ImportJavaClassesWizardClassListTableItems
            key={value}
            fullClassName={value}
            selected={true}
            onJavaClassItemSelected={onJavaClassItemSelected}
          />
        ))}
        {retrievedJavaClasses.map((value) => {
          if (!classesSet.has(value)) {
            return (
              <ImportJavaClassesWizardClassListTableItems
                key={value}
                fullClassName={value}
                selected={false}
                onJavaClassItemSelected={onJavaClassItemSelected}
              />
            );
          }
        })}
      </DataList>
    );
  };
