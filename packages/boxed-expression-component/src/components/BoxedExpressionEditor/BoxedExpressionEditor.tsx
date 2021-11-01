/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useState } from "react";
// import "@patternfly/react-core/dist/styles/base-no-reset.css";
// import "@patternfly/react-styles/css/components/Drawer/drawer.css";
import { executeIfExpressionDefinitionChanged, ExpressionProps, PMMLParams } from "../../api";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { BoxedExpressionProvider } from "./BoxedExpressionProvider";
import { ExpressionContainer } from "../ExpressionContainer";

export interface BoxedExpressionEditorProps {
  /** All expression properties used to define it */
  expressionDefinition: ExpressionProps;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
}

export function BoxedExpressionEditor(props: BoxedExpressionEditorProps) {
  const [expressionDefinition, setExpressionDefinition] = useState(props.expressionDefinition);

  const onExpressionChange = useCallback((updatedExpression: ExpressionProps) => {
    setExpressionDefinition(updatedExpression);
  }, []);

  useEffect(() => {
    executeIfExpressionDefinitionChanged(
      props.expressionDefinition,
      expressionDefinition,
      () => {
        setExpressionDefinition(props.expressionDefinition);
      },
      [
        "columns",
        "rows",
        "bindingEntries",
        "content",
        "contextEntries",
        "renderResult",
        "result",
        "functionKind",
        "formalParameters",
      ]
    );
  }, [expressionDefinition, props.expressionDefinition]);

  return (
    <I18nDictionariesProvider
      defaults={boxedExpressionEditorI18nDefaults}
      dictionaries={boxedExpressionEditorDictionaries}
      initialLocale={navigator.language}
      ctx={BoxedExpressionEditorI18nContext}
    >
      <BoxedExpressionProvider
        expressionDefinition={expressionDefinition}
        pmmlParams={props.pmmlParams}
        isRunnerTable={false}
      >
        <ExpressionContainer selectedExpression={expressionDefinition} onExpressionChange={onExpressionChange} />
      </BoxedExpressionProvider>
    </I18nDictionariesProvider>
  );
}
