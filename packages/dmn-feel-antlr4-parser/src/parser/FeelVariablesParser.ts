/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { VariablesRepository } from "./VariablesRepository";
import { CharStream, CommonTokenStream } from "antlr4";
import FEEL_1_1Parser from "./grammar/generated-parser/FEEL_1_1Parser";
import FEEL_1_1Lexer from "./grammar/generated-parser/FEEL_1_1Lexer";
import { DataType } from "./DataType";
import { Type } from "./grammar/Type";
import { FeelVariable } from "./FeelVariable";
import { MapBackedType } from "./grammar/MapBackedType";
import { VariableContext } from "./VariableContext";
import { ParsedExpression } from "./ParsedExpression";

export class FeelVariablesParser {
  private variablesRepository: VariablesRepository;

  constructor(variablesSource: VariablesRepository) {
    this.variablesRepository = variablesSource;
    this.refreshExpressions();
  }

  public refreshExpressions() {
    for (const expression of this.variablesRepository.expressions.values()) {
      for (const variable of expression.variables) {
        variable.source?.expressions.delete(expression.uuid);
      }
      const parsedExpression = this.parse(expression.uuid, expression.fullExpression);
      expression.variables = parsedExpression.feelVariables;
      for (const variable of parsedExpression.feelVariables) {
        variable.source?.expressions.set(expression.uuid, expression);
      }
    }
  }

  public parse(variableContextUuid: string, expression: string): ParsedExpression {
    const variables = new Array<FeelVariable>();
    const chars = new CharStream(expression);
    const lexer = new FEEL_1_1Lexer(chars);
    const feelTokens = new CommonTokenStream(lexer);
    const parser = new FEEL_1_1Parser(feelTokens);

    const variableContext = this.variablesRepository.variables.get(variableContextUuid);
    if (variableContext) {
      this.defineVariables(variableContext, parser);
    }

    // We're ignoring the errors for now
    parser.removeErrorListeners();

    parser.expression();

    variables.push(...parser.helper.variables);

    return {
      availableSymbols: parser.helper.availableSymbols,
      feelVariables: variables,
    };
  }

  private defineVariables(variableContext: VariableContext, parser: FEEL_1_1Parser) {
    this.defineInputVariables(variableContext.inputVariables, parser);
    this.addToParser(parser, variableContext);

    if (variableContext.parent) {
      this.defineParentVariable(variableContext.parent, parser);
    }

    for (const inputVariableContext of variableContext.inputVariables) {
      const localVariable = this.variablesRepository.variables.get(inputVariableContext);
      if (localVariable) {
        this.addToParser(parser, localVariable);
      }
    }
  }

  private defineParentVariable(variableNode: VariableContext, parser: FEEL_1_1Parser) {
    this.defineInputVariables(variableNode.inputVariables, parser);
    this.addToParser(parser, variableNode);

    if (variableNode.parent) {
      this.defineParentVariable(variableNode.parent, parser);
    }
  }

  private createType(dataType: DataType | string): Type {
    if (typeof dataType !== "string") {
      const type = new MapBackedType(dataType.name, dataType.typeRef ?? dataType.name);

      for (const property of dataType.properties) {
        const innerType = this.createType(property[1]);
        type.properties.set(property[0], innerType);
      }

      return type;
    } else {
      return {
        name: dataType,
        typeRef: dataType,
      };
    }
  }

  private defineInputVariables(inputVariables: Array<string>, parser: FEEL_1_1Parser) {
    for (const inputVariableId of inputVariables) {
      const inputVariable = this.variablesRepository.variables.get(inputVariableId);
      if (inputVariable) {
        this.addToParser(parser, inputVariable);
      }
    }
  }

  private addToParser(parser: FEEL_1_1Parser, context: VariableContext) {
    if (context.variable.value !== "") {
      parser.helper.defineVariable(
        context.variable.value,
        context.variable.typeRef ? this.createType(context.variable.typeRef) : undefined,
        context.variable.feelSyntacticSymbolNature,
        context.variable
      );
    }
  }
}
