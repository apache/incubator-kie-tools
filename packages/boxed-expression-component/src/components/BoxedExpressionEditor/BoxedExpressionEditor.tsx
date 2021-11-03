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
import { useCallback, useEffect, useMemo, useState } from "react";
import { ExpressionProps, PMMLParams } from "../../api";
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
  /**
   * A boolean used for making (or not) the clear button available on the root expression
   * Note that this parameter will be used only for the root expression.
   *
   * Each expression (internally) has a `noClearAction` property (ExpressionProps interface).
   * You can set directly it for enabling or not the clear button for such expression.
   * */
  clearSupportedOnRootExpression?: boolean;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
}

export function BoxedExpressionEditor(props: BoxedExpressionEditorProps) {
  const noClearAction = useMemo(
    () => props.clearSupportedOnRootExpression === false,
    [props.clearSupportedOnRootExpression]
  );
  const [expressionDefinition, setExpressionDefinition] = useState<ExpressionProps>({
    ...props.expressionDefinition,
    noClearAction: props.clearSupportedOnRootExpression === false,
  });

  useEffect(() => {
    setExpressionDefinition({
      ...props.expressionDefinition,
      noClearAction,
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.expressionDefinition]);

  const onExpressionChange = useCallback(
    (updatedExpression: ExpressionProps) => setExpressionDefinition(updatedExpression),
    []
  );

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
