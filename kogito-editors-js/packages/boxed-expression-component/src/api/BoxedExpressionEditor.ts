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
  //API that BoxedExpressionEditor (bee) is expecting to be defined in the Window namespace
  interface Window {
    renderBoxedExpressionEditor: (selector: string, definition: ExpressionProps) => void;
    beeApi: {
      resetExpressionDefinition: (definition: ExpressionProps) => void;
      broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => void;
      broadcastRelationExpressionDefinition: (definition: RelationProps) => void;
      broadcastContextExpressionDefinition: (definition: ContextProps) => void;
      broadcastListExpressionDefinition: (definition: ListProps) => void;
      broadcastInvocationExpressionDefinition: (definition: InvocationProps) => void;
      broadcastFunctionExpressionDefinition: (definition: FunctionProps) => void;
      broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => void;
    };
  }
}
