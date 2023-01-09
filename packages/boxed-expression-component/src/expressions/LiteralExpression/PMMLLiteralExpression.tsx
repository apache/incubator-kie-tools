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

import "./PmmlLiteralExpression.css";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { PmmlLiteralExpressionDefinition, PmmlLiteralExpressionDefinitionKind } from "../../api";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as _ from "lodash";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export function PmmlLiteralExpression(
  pmmlLiteralExpression: PmmlLiteralExpressionDefinition & { isHeadless: boolean }
) {
  const { pmmlParams, editorRef } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const [selectOpen, setSelectOpen] = useState(false);

  const options = useMemo<string[]>(() => {
    if (pmmlLiteralExpression.kind === PmmlLiteralExpressionDefinitionKind.Document) {
      return (pmmlParams ?? []).map(({ document }) => document);
    } else if (pmmlLiteralExpression.kind === PmmlLiteralExpressionDefinitionKind.Model) {
      return (
        (pmmlParams ?? [])
          // .filter((s) => s.document === selectedDocumentOnFunctionExpression) // FIXME: Tiago -> STATE GAP
          .flatMap(({ modelsFromDocument }) => modelsFromDocument ?? [])
          .map(({ model }) => model)
      );
    } else {
      throw new Error("Shouldn't ever reach here.");
    }
  }, [pmmlParams, pmmlLiteralExpression.kind]);

  const onSelectToggle = useCallback(
    (isOpen) => {
      if (!options) {
        return;
      }
      setSelectOpen(isOpen);
    },
    [options]
  );

  const onSelect = useCallback(
    (event, updatedSelection) => {
      setSelectOpen(false);
      setExpression((prev: PmmlLiteralExpressionDefinition) => ({
        ...prev,
        selected: updatedSelection,
      }));
    },
    [setExpression]
  );

  const getOptions = useCallback(() => {
    return _.map(options, (key) => (
      <SelectOption data-testid={`pmml-${key}`} key={key} value={key} data-ouia-component-id={key}>
        {key}
      </SelectOption>
    ));
  }, [options]);

  const getSelection = useCallback(() => {
    return _.includes(options, pmmlLiteralExpression.selected) ? pmmlLiteralExpression.selected : undefined;
  }, [options, pmmlLiteralExpression.selected]);

  const showingPlaceholder = useCallback(() => _.isEmpty(getSelection()), [getSelection]);

  return (
    <div className={`${pmmlLiteralExpression.id} pmml-literal-expression`}>
      <Select
        className={`pmml-selector ${showingPlaceholder() ? "showing-placeholder" : ""}`}
        menuAppendTo={editorRef?.current ?? "inline"}
        ouiaId="pmml-literal-expression-selector"
        placeholderText={pmmlLiteralExpression.noOptionsLabel}
        aria-placeholder={pmmlLiteralExpression.noOptionsLabel}
        variant={SelectVariant.single}
        onToggle={onSelectToggle}
        onSelect={onSelect}
        isOpen={selectOpen}
        selections={getSelection()}
        data-testid={pmmlLiteralExpression.testId}
      >
        {getOptions()}
      </Select>
    </div>
  );
}
