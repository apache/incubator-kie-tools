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
import {
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionHitPolicy,
  DecisionTableExpressionDefinitionClause,
  DecisionTableExpressionDefinitionRule,
  ExpressionDefinition,
  LiteralExpressionDefinition,
} from "@kie-tools/boxed-expression-component/dist/api/ExpressionDefinition";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api/DmnBuiltInDataType";
import { ExpressionDefinitionLogicType } from "@kie-tools/boxed-expression-component/dist/api/ExpressionDefinitionLogicType";

export function generateDecisionExpressionDefinition(element: Element): ExpressionDefinition {
  const decisionType = element.logic.type;

  switch (decisionType) {
    case "DecisionTable":
      const inputClauses = element.logic.inputs?.map((input) => generateClause(input));
      const outputClauses = element.logic.outputComponents?.map((output) => generateClause(output));
      return {
        annotations: [{ name: "annotation-1a", id: "111" }],
        hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
        input: inputClauses,
        logicType: ExpressionDefinitionLogicType.DecisionTable,
        name: element.name,
        output: outputClauses,
        rules: generateDecisionTableRule(element.logic.rules!, element.logic.inputs?.length),
      } as DecisionTableExpressionDefinition;
    default:
      return {
        dataType: DmnBuiltInDataType.Any,
        name: element.name,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: element.logic.expression,
      } as LiteralExpressionDefinition;
  }
}

function generateClause(clauseName: string): DecisionTableExpressionDefinitionClause {
  return {
    id: clauseName + Math.floor(Math.random() * 6) + 1,
    name: clauseName,
    dataType: DmnBuiltInDataType.Any,
  } as DecisionTableExpressionDefinitionClause;
}

function generateDecisionTableRule(
  rules: string[][],
  inputLength: number | undefined
): DecisionTableExpressionDefinitionRule[] {
  const l = inputLength ? inputLength : 0;
  const decisionTableRules: DecisionTableExpressionDefinitionRule[] = [];
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
