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
import { useCallback, useRef, useState } from "react";
import { PmmlLiteralExpressionDefinition } from "../../api";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as _ from "lodash";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export const PmmlLiteralExpression: React.FunctionComponent<PmmlLiteralExpressionDefinition> = (
  pmmlLiteralExpression: PmmlLiteralExpressionDefinition
) => {
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const selection = useRef(pmmlLiteralExpression.selected);

  const [selectOpen, setSelectOpen] = useState(false);

  const onSelectToggle = useCallback(
    (isOpen) => {
      if (!pmmlLiteralExpression.getOptions() || !pmmlLiteralExpression.getOptions().length) {
        return;
      }
      setSelectOpen(isOpen);
      boxedExpressionEditor.setContextMenuOpen(isOpen);
    },
    [boxedExpressionEditor]
  );

  const onSelect = useCallback(
    (event, updatedSelection) => {
      setSelectOpen(false);
      selection.current = updatedSelection;
      pmmlLiteralExpression.onUpdatingRecursiveExpression?.({
        ...pmmlLiteralExpression,
        selected: updatedSelection,
      } as PmmlLiteralExpressionDefinition);
    },
    [pmmlLiteralExpression]
  );

  const getOptions = useCallback(() => {
    return _.map(pmmlLiteralExpression.getOptions(), (key) => (
      <SelectOption data-testid={`pmml-${key}`} key={key} value={key} data-ouia-component-id={key}>
        {key}
      </SelectOption>
    ));
  }, [pmmlLiteralExpression]);

  const getSelection = useCallback(() => {
    return _.includes(pmmlLiteralExpression.getOptions(), selection.current) ? selection.current : undefined;
  }, [pmmlLiteralExpression]);

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
