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
import { JavaClass } from "./model/JavaClass";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { JavaField } from "./model/JavaField";
import { DMNSimpleType, JAVA_TO_DMN_MAP } from "./model/DMNSimpleType";
import { getJavaClassSimpleName } from "./model/JavaClassUtils";
import { useCallback, useState } from "react";
import { useImportJavaClassesWizardI18n } from "../../i18n";

export interface ImportJavaClassesWizardFieldListTableProps {
  /** List of the selected classes by user */
  selectedJavaClassFields: JavaClass[];
  /** Function to call when the Fetch button is clicked */
  loadJavaClass?: (fullClassName: string) => void;
}

export const ImportJavaClassesWizardFieldListTable = (props: ImportJavaClassesWizardFieldListTableProps) => {
  return (
    <TableComposable aria-label="field-table">
      {props.selectedJavaClassFields.map((javaClass, index) => {
        return (
          <TableJavaClassItem
            key={javaClass.name}
            javaClass={javaClass}
            index={index}
            loadJavaClass={props.loadJavaClass}
          />
        );
      })}
    </TableComposable>
  );
};

const TableJavaClassItem = ({
  javaClass,
  index,
  loadJavaClass,
}: {
  javaClass: JavaClass;
  index: number;
  loadJavaClass?: (fullClassName: string) => void;
}) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const [isExpanded, setExpanded] = useState(true);

  const isFetchable = useCallback((field: JavaField) => {
    /* Temporary disable isFetchable, because we need FQCN of the Classes, no longer avaialble */
    /* https://github.com/kiegroup/kie-issues/issues/114 */
    return false;
    //return field.dmnTypeRef === DMNSimpleType.ANY && !JAVA_TO_DMN_MAP.has(getJavaClassSimpleName(field.type));
  }, []);

  const parentRow = (
    <Tr key={`${javaClass.name}_tr`}>
      <Td
        key={`${javaClass.name}_td0`}
        expand={
          javaClass.fields && javaClass.fields.length > 0
            ? {
                rowIndex: index,
                isExpanded: isExpanded,
                onToggle: () => setExpanded((prevState) => !prevState),
              }
            : undefined
        }
      />
      <Td key={`${javaClass.name}_td1`}>
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
          return (
            <Tr key={`${field.name}_tr`} isExpanded={isExpanded}>
              <Td key={`${field.name}_td0`} />
              <Td key={`${field.name}_td1`}>
                <ExpandableRowContent>
                  <span>{field.name}</span>
                  <span className={"dmn-type-name"}>{`(${field.dmnTypeRef})`}</span>
                  {loadJavaClass && isFetchable(field) && (
                    <Button
                      className={"fetch-button"}
                      onClick={() => loadJavaClass(field.type)}
                      variant="primary"
                      isSmall
                    >
                      {`${i18n.modalWizard.fieldTable.fetchButtonLabel} "${getJavaClassSimpleName(field.type)}" class`}
                    </Button>
                  )}
                </ExpandableRowContent>
              </Td>
            </Tr>
          );
        })
      : undefined;

  return (
    <Tbody key={index} isExpanded={isExpanded}>
      {parentRow}
      {childRow}
    </Tbody>
  );
};
