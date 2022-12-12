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
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as _ from "lodash";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export const PmmlLiteralExpression: React.FunctionComponent<PmmlLiteralExpressionDefinition> = (
  pmmlLiteralExpression: PmmlLiteralExpressionDefinition
) => {
  const boxedExpressionEditor = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const [selectOpen, setSelectOpen] = useState(false);

  const options = useMemo<string[]>(() => {
    if (pmmlLiteralExpression.kind === PmmlLiteralExpressionDefinitionKind.Document) {
      return (boxedExpressionEditor.pmmlParams ?? []).map(({ document }) => document);
    } else if (pmmlLiteralExpression.kind === PmmlLiteralExpressionDefinitionKind.Model) {
      return (
        (boxedExpressionEditor.pmmlParams ?? [])
          // .filter((s) => s.document === selectedDocumentOnFunctionExpression) // FIXME: Tiago -> STATE GAP
          .flatMap(({ modelsFromDocument }) => modelsFromDocument ?? [])
          .map(({ model }) => model)
      );
    } else {
      throw new Error("Shouldn't ever reach here.");
    }
  }, [boxedExpressionEditor.pmmlParams, pmmlLiteralExpression.kind]);

  const onSelectToggle = useCallback(
    (isOpen) => {
      if (!options) {
        return;
      }
      setSelectOpen(isOpen);
      boxedExpressionEditor.setContextMenuOpen(isOpen);
    },
    [boxedExpressionEditor, options]
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

  const onSelectorClick = useCallback(() => {
    boxedExpressionEditor.beeGwtService?.selectObject(pmmlLiteralExpression.id);
  }, [boxedExpressionEditor.beeGwtService, pmmlLiteralExpression.id]);

  return (
    <div onClick={onSelectorClick} className={`${pmmlLiteralExpression.id} pmml-literal-expression`}>
      <Select
        className={`pmml-selector ${showingPlaceholder() ? "showing-placeholder" : ""}`}
        menuAppendTo={boxedExpressionEditor.editorRef?.current ?? "inline"}
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
};
