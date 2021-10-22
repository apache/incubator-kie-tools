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
import "./ImportJavaClassesWizardFieldListTable.css";
import { ExpandableRowContent, TableComposable, Tbody, Td, Tr } from "@patternfly/react-table";
import { JavaClass } from "./Model/JavaClass";
import { Button } from "@patternfly/react-core";
import { JavaField } from "./Model/JavaField";
import { DMNSimpleType } from "./Model/DMNSimpleType";
import { getJavaClassSimpleName } from "./Model/JavaClassUtils";
import { useCallback } from "react";

export interface ImportJavaClassesWizardFieldListTableProps {
  /** List of the selected classes by user */
  selectedJavaClassFields: JavaClass[];
  /** In ready only mode, fetch classes mechanism is not enabled */
  readOnly: boolean;
  /** Table css classname **/
  tableClassName?: string;
  /** Function to call when an Fetch button is clicked */
  onFetchButtonClick?: (fullClassName: string) => void;
  /** Fetch button label */
  fetchButtonLabel?: string;
}

export const ImportJavaClassesWizardFieldListTable: React.FunctionComponent<ImportJavaClassesWizardFieldListTableProps> =
  ({
    selectedJavaClassFields,
    readOnly,
    tableClassName,
    onFetchButtonClick,
    fetchButtonLabel,
  }: ImportJavaClassesWizardFieldListTableProps) => {
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
    const decorateWithRoundBrackets = (typeName: string) => {
      return "(" + typeName + ")";
    };
    const isFetchable = (field: JavaField) => {
      return field.dmnTypeRef === DMNSimpleType.ANY;
    };
    const fetchButton = useCallback(
      (field: JavaField) => {
        return (
          <Button
            className={"fetch-button"}
            onClick={onFetchButtonClick ? () => onFetchButtonClick(field.type) : undefined}
            variant="primary"
            isSmall
          >
            {fetchButtonLabel + ' "' + getJavaClassSimpleName(field.type) + '" class'}
          </Button>
        );
      },
      // eslint-disable-next-line
      [selectedJavaClassFields]
    );

    let rowIndex = -1;
    return (
      <div className={tableClassName}>
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
                  <span className={"dmn-type-name"}>(Structure)</span>
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
                            <span className={"dmn-type-name"}>{decorateWithRoundBrackets(field.dmnTypeRef)}</span>
                            {!readOnly && isFetchable(field) ? fetchButton(field) : null}
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
      </div>
    );
  };
