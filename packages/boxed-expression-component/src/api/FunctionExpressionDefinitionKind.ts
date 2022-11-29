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

import { ExpressionDefinition } from "./ExpressionDefinition";
import { ContextExpressionDefinitionEntryInfo } from "./ContextExpressionDefinitionEntry";

export enum FunctionExpressionDefinitionKind {
  Feel = "FEEL",
  Java = "Java",
  Pmml = "PMML",
}

export interface FeelFunctionExpressionDefinition {
  /** Feel Function */
  functionKind: FunctionExpressionDefinitionKind.Feel;
  /** The Expression related to the function */
  expression?: ExpressionDefinition;
}

export interface JavaFunctionExpressionDefinition {
  /** Java Function */
  functionKind: FunctionExpressionDefinitionKind.Java;
  /** Java class */
  className?: string;
  /** Method signature */
  methodName?: string;
  /** Class text field identifier */
  classFieldId?: string;
  /** Method text field identifier */
  methodFieldId?: string;
}

export interface PmmlParam {
  document: string;
  modelsFromDocument?: {
    model: string;
    parametersFromModel?: ContextExpressionDefinitionEntryInfo[];
  }[];
}

export interface PmmlFunctionExpressionDefinition {
  /** Pmml Function */
  functionKind: FunctionExpressionDefinitionKind.Pmml;
  /** Selected PMML document */
  document?: string;
  /** Selected PMML model */
  model?: string;
  /** Document dropdown field identifier */
  documentFieldId?: string;
  /** Model dropdown field identifier */
  modelFieldId?: string;
}
