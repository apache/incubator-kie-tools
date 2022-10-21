/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { Element } from "../model";
import { DecisionTableProps, LogicType } from "@kie-tools/boxed-expression-component/dist/api";
import {
  Clause,
  DataType,
  DecisionTableRule,
  ExpressionProps,
  LiteralExpressionProps,
} from "@kie-tools/boxed-expression-component/dist/api";

export function generateDecisionExpressionDefinition(element: Element): ExpressionProps {
  const decisionType = element.logic.type;

  switch (decisionType) {
    case "DecisionTable":
      const inputClauses = element.logic.inputs?.map((input) => generateClause(input));
      const outputClauses = element.logic.outputComponents?.map((output) => generateClause(output));
      return {
        annotations: [{ name: "annotation-1a", id: "111" }],
        dataType: DataType.Any,
        input: inputClauses,
        logicType: LogicType.DecisionTable,
        name: element.name,
        output: outputClauses,
        rules: generateDecisionTableRule(element.logic.rules!, element.logic.inputs?.length),
      } as DecisionTableProps;
    case "LiteralExpression":
      return {
        dataType: DataType.Any,
        name: element.name,
        logicType: LogicType.LiteralExpression,
        content: element.logic.expression,
      } as LiteralExpressionProps;
    default:
      return {};
  }
}

function generateClause(clauseName: string): Clause {
  return {
    id: clauseName + Math.floor(Math.random() * 6) + 1,
    name: clauseName,
    dataType: DataType.Any,
  } as Clause;
}

function generateDecisionTableRule(rules: string[][], inputLength: number | undefined): DecisionTableRule[] {
  const l = inputLength ? inputLength : 0;
  const decisionTableRules: DecisionTableRule[] = [];
  let index = 0;
  rules.map((rule) => {
    decisionTableRules.push({
      annotationEntries: [""],
      id: index.toString(),
      inputEntries: rule.slice(0, l),
      outputEntries: rule.slice(l),
    });
    index++;
  });

  return decisionTableRules;
}
