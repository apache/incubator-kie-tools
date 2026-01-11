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

import * as React from "react";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { useBpmnEditorStore } from "../../store/StoreContext";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { TypeaheadSelect, TypeaheadSelectOption } from "../../typeaheadSelect/TypeaheadSelect";
import { SelectOptionProps } from "@patternfly/react-core/dist/js/components/Select";
import { useCallback, useMemo } from "react";
import "./VariableSelector.css";
import { useBpmnEditorI18n } from "../../i18n";

export type OnVariableChange = (newVariableRef: string | undefined, args: { isExpression: boolean }) => void;

export function VariableSelector({
  value,
  onChange,
  allowExpressions,
  omitIds,
}: {
  value: string | undefined;
  onChange: OnVariableChange;
  allowExpressions: boolean;
  omitIds?: string[];
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const variablesById = useBpmnEditorStore(
    (s) =>
      new Map(
        s.bpmn.model.definitions.rootElement
          ?.find((s) => s.__$$element === "process")
          ?.property?.map((p) => [p["@_id"], p])
      )
  );

  const { options, isExpression } = useMemo(() => {
    const options: TypeaheadSelectOption[] = [...variablesById.values()].map((v) => ({
      value: v["@_id"],
      children: v["@_name"],
    }));

    let isExpression = false;

    if (!value) {
      // Don't add anything.
    } else if (variablesById.has(value)) {
      // Don't add anything.
    }
    // In this case, value is an expression
    else if (allowExpressions) {
      isExpression = true;
      options.push({
        value: value,
        children: value,
        customLabel: <i>{`Expr: ${value}`}</i>,
      });
    }
    // In this case, value is a reference to an unknown variable
    else {
      options.push({
        value,
        children: `<Unknown> (${value})`,
      });
    }

    return { options, isExpression };
  }, [allowExpressions, value, variablesById]);

  const onChangeExpression = useCallback(
    (newOptionLabel: string): string => {
      const expr = newOptionLabel;
      onChange(expr, { isExpression: true });
      return expr;
    },
    [onChange]
  );

  const onChangeVariableRef = useCallback(
    (newSelected: string | undefined, _: string, { triggeredByCreateNewOption }): void => {
      if (triggeredByCreateNewOption) {
        return;
      }

      return onChange(newSelected, { isExpression: false });
    },
    [onChange]
  );

  return (
    <>
      <InputGroup>
        <InputGroupText>
          {isExpression ? (
            <span style={{ whiteSpace: "nowrap" }}>
              <b>
                <i>{i18n.propertiesPanel.expr}</i>
              </b>
            </span>
          ) : (
            <span style={{ whiteSpace: "nowrap" }}>
              <b>{i18n.propertiesPanel.var}</b>
            </span>
          )}
        </InputGroupText>
        <TypeaheadSelect
          isMultiple={false}
          showCreateOptionWhen={allowExpressions ? "different-than-current" : "never"}
          emptyStateText={
            allowExpressions ? i18n.propertiesPanel.enteringExpressions : i18n.propertiesPanel.noVariablesYet
          }
          id={`variable-selector-${generateUuid()}`}
          selected={value}
          setSelected={onChangeVariableRef}
          options={options}
          createNewOptionLabel={allowExpressions ? i18n.propertiesPanel.expression : undefined}
          onCreateNewOption={allowExpressions ? onChangeExpression : undefined}
          isDisabled={isReadOnly}
        />
      </InputGroup>
    </>
  );
}
