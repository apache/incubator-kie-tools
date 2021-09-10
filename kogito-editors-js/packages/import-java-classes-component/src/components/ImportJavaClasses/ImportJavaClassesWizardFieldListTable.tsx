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
import { ExpandableRowContent, TableComposable, Tbody, Td, Tr } from "@patternfly/react-table";
import { JavaClass } from "./Model/JavaClass";

export interface ImportJavaClassesWizardFieldListTableProps {
  /** List of the selected classes by user */
  selectedJavaClassFields: JavaClass[];
}

export const ImportJavaClassesWizardFieldListTable: React.FunctionComponent<ImportJavaClassesWizardFieldListTableProps> =
  ({ selectedJavaClassFields }: ImportJavaClassesWizardFieldListTableProps) => {
    const [expanded, setExpanded] = React.useState(
      Object.fromEntries(
        selectedJavaClassFields.map((value, index) => [index, Boolean(value.fields && value.fields.length > 0)])
      )
    );
    const handleExpansionToggle = (event: React.MouseEvent, pairIndex: number) => {
      setExpanded({
        ...expanded,
        [pairIndex]: !expanded[pairIndex],
      });
    };
    const getJavaClassSimpleName = (className: string) => {
      return className.split(".").pop();
    };
    const decorateWithRoundBrackets = (className: string) => {
      return " (" + className + ")";
    };
    let rowIndex = -1;
    return (
      <TableComposable aria-label="field-table">
        {selectedJavaClassFields.map((javaClass, index) => {
          rowIndex += 1;
          const parentRow = (
            <Tr key={rowIndex}>
              <Td
                key={`${rowIndex}_0`}
                expand={
                  javaClass.fields && javaClass.fields.length > 0
                    ? {
                        rowIndex: index,
                        isExpanded: expanded[index],
                        onToggle: handleExpansionToggle,
                      }
                    : undefined
                }
              />
              <Td key={`${rowIndex}_${javaClass.name}`}>
                <span>
                  <strong>{getJavaClassSimpleName(javaClass.name)}</strong>
                </span>
                <span>{decorateWithRoundBrackets(javaClass.name)}</span>
              </Td>
            </Tr>
          );
          const childRow =
            javaClass.fields && javaClass.fields.length > 0
              ? javaClass.fields.map((field) => {
                  rowIndex += 1;
                  return (
                    <Tr key={rowIndex} isExpanded={expanded[index] === true}>
                      <Td key={`${rowIndex}_0`} />
                      <Td key={`${rowIndex}_${field.name}`}>
                        <ExpandableRowContent>
                          <span>{field.name}</span>
                          <span>{decorateWithRoundBrackets(field.type)}</span>
                        </ExpandableRowContent>
                      </Td>
                    </Tr>
                  );
                })
              : undefined;
          return (
            <Tbody key={index} isExpanded={expanded[index] === true}>
              {parentRow}
              {childRow}
            </Tbody>
          );
        })}
      </TableComposable>
    );
  };
