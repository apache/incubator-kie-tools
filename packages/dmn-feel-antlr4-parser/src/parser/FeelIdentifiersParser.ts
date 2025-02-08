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

import { IdentifiersRepository } from "./IdentifiersRepository";
import { CharStream, CommonTokenStream } from "antlr4";
import FEEL_1_1Parser from "./grammar/generated-parser/FEEL_1_1Parser";
import FEEL_1_1Lexer from "./grammar/generated-parser/FEEL_1_1Lexer";
import { DataType } from "./DataType";
import { Type } from "./grammar/Type";
import { FeelIdentifiedSymbol } from "./FeelIdentifiedSymbol";
import { MapBackedType } from "./grammar/MapBackedType";
import { IdentifierContext } from "./IdentifierContext";
import { ParsedExpression } from "./ParsedExpression";
import { FeelSyntacticSymbolNature } from "./FeelSyntacticSymbolNature";
import { Expression } from "./Expression";

export class FeelIdentifiersParser {
  private repository: IdentifiersRepository;

  constructor(identifiersRepository: IdentifiersRepository) {
    this.repository = identifiersRepository;
  }

  public parse(variableContextUuid: string, expression: string): ParsedExpression {
    const variables = new Array<FeelIdentifiedSymbol>();
    const chars = new CharStream(expression);
    const lexer = new FEEL_1_1Lexer(chars);
    const feelTokens = new CommonTokenStream(lexer);
    const parser = new FEEL_1_1Parser(feelTokens);

    const variableContext = this.repository.identifiers.get(variableContextUuid);
    if (variableContext) {
      this.defineVariables(variableContext, parser);
    }

    // We're ignoring the errors for now
    parser.removeErrorListeners();

    parser.expression();

    variables.push(...parser.helper.variables);

    return {
      availableSymbols: parser.helper.availableSymbols,
      feelIdentifiedSymbols: variables,
    };
  }

  private defineVariables(variableContext: IdentifierContext, parser: FEEL_1_1Parser) {
    this.defineInputVariables(variableContext.inputIdentifiers, parser);
    this.addToParser(parser, variableContext);

    if (variableContext.parent) {
      this.defineParentVariable(variableContext.parent, parser);
    }

    for (const inputVariableContext of variableContext.inputIdentifiers) {
      const localVariable = this.repository.identifiers.get(inputVariableContext);
      if (localVariable) {
        this.addToParser(parser, localVariable);
      }
    }
  }

  private defineParentVariable(variableNode: IdentifierContext, parser: FEEL_1_1Parser) {
    this.defineInputVariables(variableNode.inputIdentifiers, parser);
    this.addToParser(parser, variableNode);

    if (variableNode.parent) {
      this.defineParentVariable(variableNode.parent, parser);
    }
  }

  private createType(dataType: DataType | string): Type {
    if (typeof dataType !== "string") {
      const type = new MapBackedType(dataType.name, dataType.typeRef ?? dataType.name, dataType.source);

      for (const property of dataType.properties) {
        const innerType = this.createType(property[1]);
        type.properties.set(property[0], innerType);
      }

      return type;
    } else {
      return {
        name: dataType,
        typeRef: dataType,
        source: {
          value: dataType,
          feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
          expressionsThatUseTheIdentifier: new Map<string, Expression>(),
        },
      };
    }
  }

  private defineInputVariables(inputVariables: Array<string>, parser: FEEL_1_1Parser) {
    for (const inputVariableId of inputVariables) {
      const inputVariable = this.repository.identifiers.get(inputVariableId);
      if (inputVariable) {
        this.addToParser(parser, inputVariable, true);
      }
    }
  }

  private addToParser(parser: FEEL_1_1Parser, context: IdentifierContext, addInvisibleVariables?: boolean) {
    if (
      context.identifier.value !== "" &&
      ((!addInvisibleVariables &&
        context.identifier.feelSyntacticSymbolNature != FeelSyntacticSymbolNature.InvisibleVariables) ||
        addInvisibleVariables)
    ) {
      parser.helper.defineVariable(
        context.identifier.value,
        context.identifier.typeRef ? this.createType(context.identifier.typeRef) : undefined,
        context.identifier.feelSyntacticSymbolNature,
        context.identifier,
        context.allowDynamicVariables
      );
    }
  }
}
