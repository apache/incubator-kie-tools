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

import _ from "lodash";
import {
  ContextProps,
  DecisionTableProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  RelationProps,
} from "./ExpressionProps";

export {};

declare global {
  // Set of Functions used by the BoxedExpressionEditor and the BoxedExpressionWrapper
  interface BeeApi {
    resetExpressionDefinition: (definition: ExpressionProps) => void;
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => void;
    broadcastRelationExpressionDefinition: (definition: RelationProps) => void;
    broadcastContextExpressionDefinition: (definition: ContextProps) => void;
    broadcastListExpressionDefinition: (definition: ListProps) => void;
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => void;
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => void;
    broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => void;
  }

  //API that BoxedExpressionEditor (bee) and its wrapper are expecting to be defined in the Window namespace
  interface Window {
    renderBoxedExpressionEditor: (selector: string, definition: ExpressionProps) => void;
    beeApi: BeeApi;
    beeApiWrapper: BeeApi;
  }
}

/**
 * First, checks if each field, specified in propertiesToCheck, is equal for prevDef and updatedDef
 * Second, if the condition specified above is true, executes functionToExecute
 * @param prevDef previous definition
 * @param updatedDef updated definition
 * @param functionToExecute function to be executed if criteria is met
 * @param propertiesToCheck properties to be checked in the input objects
 */
export const executeIfExpressionDefinitionChanged = (
  prevDef: ExpressionProps,
  updatedDef: ExpressionProps,
  functionToExecute: () => void,
  propertiesToCheck?: string[]
) => {
  const customizer = propertiesToCheck
    ? (prev: Record<string, any>, next: Record<string, any>) =>
        _.every(propertiesToCheck, (prop) => _.isEqual(prev[prop], next[prop]))
    : undefined;

  if (!_.isEqualWith(prevDef, updatedDef, customizer)) {
    functionToExecute();
  }
};
