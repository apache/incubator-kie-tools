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

import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { BeeGwtService, DmnDataType, ExpressionDefinition, PmmlParam } from "../../api";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { BoxedExpressionEditorContextProvider } from "./BoxedExpressionEditorContext";
import { ExpressionDefinitionRoot } from "../ExpressionDefinitionRoot";
import "@patternfly/react-styles/css/components/Drawer/drawer.css";
import "./base-no-reset-wrapped.css";

export interface BoxedExpressionEditorProps {
  /** The API methods which BoxedExpressionEditor component can use to dialog with GWT Layer */
  beeGwtService?: BeeGwtService;
  /** Identifier of the decision node, where the expression will be hold */
  decisionNodeId: string;
  /** All expression properties used to define it */
  expressionDefinition: ExpressionDefinition;
  setExpressionDefinition: React.Dispatch<React.SetStateAction<ExpressionDefinition>>;
  /**
   * A boolean used for making (or not) the clear button available on the root expression
   * Note that this parameter will be used only for the root expression.
   *
   * Each expression (internally) has a `noClearAction` property (ExpressionProps interface).
   * You can set directly it for enabling or not the clear button for such expression.
   * */
  isClearSupportedOnRootExpression?: boolean;
  /** The data type elements that can be used in the editor */
  dataTypes: DmnDataType[];
  /** PMML parameters */
  pmmlParams?: PmmlParam[];
}

export function BoxedExpressionEditor({
  dataTypes,
  decisionNodeId,
  expressionDefinition,
  setExpressionDefinition,
  beeGwtService,
  isClearSupportedOnRootExpression,
  pmmlParams,
}: BoxedExpressionEditorProps) {
  const noClearAction = useMemo(() => {
    return isClearSupportedOnRootExpression === false;
  }, [isClearSupportedOnRootExpression]);

  useEffect(() => {
    setExpressionDefinition((prev) => ({
      ...prev,
      noClearAction,
    }));
  }, [noClearAction, setExpressionDefinition]);

  return (
    <I18nDictionariesProvider
      defaults={boxedExpressionEditorI18nDefaults}
      dictionaries={boxedExpressionEditorDictionaries}
      initialLocale={navigator.language}
      ctx={BoxedExpressionEditorI18nContext}
    >
      <BoxedExpressionEditorContextProvider
        beeGwtService={beeGwtService}
        decisionNodeId={decisionNodeId}
        expressionDefinition={expressionDefinition}
        setExpressionDefinition={setExpressionDefinition}
        dataTypes={dataTypes}
        pmmlParams={pmmlParams}
        isRunnerTable={false}
      >
        <ExpressionDefinitionRoot decisionNodeId={decisionNodeId} expression={expressionDefinition} />
      </BoxedExpressionEditorContextProvider>
    </I18nDictionariesProvider>
  );
}
