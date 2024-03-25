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

import "@patternfly/react-styles/css/components/Drawer/drawer.css";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { BeeGwtService, DmnDataType, ExpressionDefinition, PmmlDocument } from "../../api";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { ExpressionDefinitionRoot } from "../ExpressionDefinitionRoot";
import { BoxedExpressionEditorContextProvider } from "./BoxedExpressionEditorContext";
import { FeelVariables } from "@kie-tools/dmn-feel-antlr4-parser";
import "./base-no-reset-wrapped.css";
import "../../@types/react-table";

export interface BoxedExpressionEditorProps {
  /** The API methods which BoxedExpressionEditor component can use to dialog with GWT layer. Although the GWT layer is deprecated, and the new DMN Editor does not have GWT, some methods here are still necessary. */
  beeGwtService?: BeeGwtService;
  /** Identifier of the Decision or BKM containing `expression` */
  expressionHolderId: string;
  /** The name of the expression */
  expressionName?: string;
  /** The boxed expression itself */
  expression: ExpressionDefinition | undefined;
  /** Called every time something changes on the expression */
  onExpressionChange: React.Dispatch<React.SetStateAction<ExpressionDefinition | undefined>>;
  /** KIE Extension to represent IDs of individual columns or expressions */
  widthsById: Map<string, number[]>;
  /** Called every time a width changes on the expression */
  onWidthsChange: React.Dispatch<React.SetStateAction<Map<string, number[]>>>;
  /** A boolean used for making (or not) the reset button available on the root expression. BKMs, for example, can't be reset, as they need to be a Boxed Function. */
  isResetSupportedOnRootExpression?: boolean;
  /** The Data Types available */
  dataTypes: DmnDataType[];
  /** PMML models available to use on Boxed PMML Function */
  pmmlDocuments?: PmmlDocument[];
  /** The containing HTMLElement which is scrollable */
  scrollableParentRef: React.RefObject<HTMLElement>;
  /** Parsed variables used for syntax coloring and auto-complete */
  variables?: FeelVariables;
}

export function BoxedExpressionEditor({
  dataTypes,
  expressionHolderId,
  expression,
  onExpressionChange,
  beeGwtService,
  isResetSupportedOnRootExpression,
  scrollableParentRef,
  pmmlDocuments,
  variables,
  widthsById,
  onWidthsChange,
  expressionName,
}: BoxedExpressionEditorProps) {
  return (
    <I18nDictionariesProvider
      defaults={boxedExpressionEditorI18nDefaults}
      dictionaries={boxedExpressionEditorDictionaries}
      initialLocale={navigator.language}
      ctx={BoxedExpressionEditorI18nContext}
    >
      <BoxedExpressionEditorContextProvider
        scrollableParentRef={scrollableParentRef}
        beeGwtService={beeGwtService}
        expressionHolderId={expressionHolderId}
        expression={expression}
        onExpressionChange={onExpressionChange}
        onWidthsChange={onWidthsChange}
        dataTypes={dataTypes}
        pmmlDocuments={pmmlDocuments}
        variables={variables}
        widthsById={widthsById}
        expressionName={expressionName}
      >
        <ExpressionDefinitionRoot
          expressionHolderId={expressionHolderId}
          expression={expression}
          isResetSupported={isResetSupportedOnRootExpression}
          expressionName={expressionName}
        />
      </BoxedExpressionEditorContextProvider>
    </I18nDictionariesProvider>
  );
}
