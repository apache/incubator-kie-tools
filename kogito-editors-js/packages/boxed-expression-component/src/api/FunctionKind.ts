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

import { ExpressionProps } from "./ExpressionProps";
import { EntryInfo } from "./ContextEntry";

export enum FunctionKind {
  Feel = "FEEL",
  Java = "Java",
  Pmml = "PMML",
}

export interface FeelFunctionProps {
  /** Feel Function */
  functionKind: FunctionKind.Feel;
  /** The Expression related to the function */
  expression?: ExpressionProps;
}

export interface JavaFunctionProps {
  /** Java Function */
  functionKind: FunctionKind.Java;
  /** Java class */
  className?: string;
  /** Method signature */
  methodName?: string;
}

interface PMMLParam {
  document: string;
  modelsFromDocument?: {
    model: string;
    parametersFromModel?: EntryInfo[];
  }[];
}

export type PMMLParams = PMMLParam[];

export interface PmmlFunctionProps {
  /** Pmml Function */
  functionKind: FunctionKind.Pmml;
  /** Selected PMML document */
  document?: string;
  /** Selected PMML model */
  model?: string;
}
