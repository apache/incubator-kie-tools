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

import * as Monaco from "@kie-tools-core/monaco-editor";
import { Element } from "./themes/Element";
import { FeelSyntacticSymbolNature, FeelIdentifiers, ParsedExpression } from "@kie-tools/dmn-feel-antlr4-parser";

export class SemanticTokensProvider implements Monaco.languages.DocumentSemanticTokensProvider {
  constructor(
    private feelIdentifiers: FeelIdentifiers | undefined,
    private expressionId: string | undefined,
    private setCurrentParsedExpression: (
      value: ((prevState: ParsedExpression) => ParsedExpression) | ParsedExpression
    ) => void
  ) {}

  onDidChange?: Monaco.IEvent<void> | undefined;

  getLegend(): Monaco.languages.SemanticTokensLegend {
    return {
      tokenTypes: Object.values(Element).filter((x) => typeof x === "string") as string[],
      tokenModifiers: [],
    };
  }

  provideDocumentSemanticTokens(
    model: Monaco.editor.ITextModel,
    lastResultId: string | null,
    token: Monaco.CancellationToken
  ): Monaco.languages.ProviderResult<Monaco.languages.SemanticTokens> {
    const tokenTypes = new Array<number>();

    if (!this.feelIdentifiers) {
      return;
    }

    const text = model.getValue().replaceAll("\r\n", "\n");
    const contentByLines = model.getLinesContent();
    const parsedExpression = this.feelIdentifiers.parse({
      identifierContextUuid: this.expressionId ?? "",
      expression: text,
    });

    // This is to autocomplete, so we don't need to parse it again.
    this.setCurrentParsedExpression(parsedExpression);

    // The startIndex is set by parse relative to the ENTIRE EXPRESSION.
    // But, here, we need a startIndex relative to LINE, because that's how Monaco works.
    //
    // For example, consider the expression:
    // "a" +
    // "b" + someVar
    //
    // To the parser, the index of "someVar" is 13, because it reads the expression in this format:
    // "a" + "b" + someVar
    //
    // But, here, the real index of "someVar" is 7.
    //
    // The code bellow does this calculation fixing the startIndex solved by the parser to the
    // startIndex we need here, relative to the LINE where the variable is, not to the full expression.
    for (const feelIdentifiedSymbol of parsedExpression.feelIdentifiedSymbols) {
      let lineOffset = 0;
      for (let i = 0; i < feelIdentifiedSymbol.startLine; i++) {
        lineOffset += contentByLines[i].length + 1; // +1 = is the line break
      }
      feelIdentifiedSymbol.startIndex -= lineOffset;
    }

    let startOfPreviousVariable = 0;
    let previousLine = 0;
    for (const feelIdentifiedSymbol of parsedExpression.feelIdentifiedSymbols) {
      if (previousLine != feelIdentifiedSymbol.startLine) {
        startOfPreviousVariable = 0;
      }

      // It is a variable that it is NOT split in multiple-lines
      if (feelIdentifiedSymbol.startLine === feelIdentifiedSymbol.endLine) {
        tokenTypes.push(
          feelIdentifiedSymbol.startLine - previousLine, // lineIndex = relative to the PREVIOUS line
          feelIdentifiedSymbol.startIndex - startOfPreviousVariable, // columnIndex = relative to the start of the PREVIOUS token NOT to the start of the line
          feelIdentifiedSymbol.length,
          this.getTokenTypeIndex(feelIdentifiedSymbol.feelSymbolNature),
          0 // token modifier = not used so we keep it 0
        );

        previousLine = feelIdentifiedSymbol.startLine;
        startOfPreviousVariable = feelIdentifiedSymbol.startIndex;
      } else {
        // It is a MULTILINE variable.
        // We colorize the first line of the variable and then other lines.
        tokenTypes.push(
          feelIdentifiedSymbol.startLine - previousLine,
          feelIdentifiedSymbol.startIndex - startOfPreviousVariable,
          contentByLines[feelIdentifiedSymbol.startLine - previousLine].length - feelIdentifiedSymbol.startIndex,
          this.getTokenTypeIndex(feelIdentifiedSymbol.feelSymbolNature),
          0
        );

        let remainingChars =
          feelIdentifiedSymbol.length -
          1 -
          (contentByLines[feelIdentifiedSymbol.startLine - previousLine].length - feelIdentifiedSymbol.startIndex); // -1 = line break
        const remainingLines = feelIdentifiedSymbol.endLine - feelIdentifiedSymbol.startLine;
        let currentLine = feelIdentifiedSymbol.startLine + 1;

        // We colorize the remaining lines here. It can be one of the following cases:
        // 1. The entire line is part of the variable, colorize the entire line;
        // 2. Only a few chars at the start of the currentLine is part of the variable.
        for (let i = 0; i < remainingLines; i++) {
          // We try to colorize everything but, if it overflows the line, it means that the variable does not end here.
          let toColorize = remainingChars;
          if (toColorize > contentByLines[currentLine].length) {
            toColorize = contentByLines[currentLine].length;
          }

          tokenTypes.push(1, 0, toColorize, this.getTokenTypeIndex(feelIdentifiedSymbol.feelSymbolNature), 0);

          remainingChars -= toColorize + 1;
          currentLine++;
        }

        // We need to track where is the start to previous colorized variable, because it is used to calculate
        // where we're going to paint the next variable. Monaco utilizes that as the index NOT the start of
        // the line. So, here, we're setting it to 0 because the last painted "part of the variable"
        // was painted at position 0 of the line.
        startOfPreviousVariable = 0;
        previousLine = feelIdentifiedSymbol.endLine;
      }
    }

    return {
      data: new Uint32Array(tokenTypes),
      resultId: undefined,
    };
  }

  releaseDocumentSemanticTokens(resultId: string | undefined): void {
    // Do nothing
  }

  private getTokenTypeIndex(symbolType: FeelSyntacticSymbolNature) {
    switch (symbolType) {
      default:
      case FeelSyntacticSymbolNature.LocalVariable:
      case FeelSyntacticSymbolNature.GlobalVariable:
        return Element.Variable;
      case FeelSyntacticSymbolNature.DynamicVariable:
        return Element.DynamicVariable;
      case FeelSyntacticSymbolNature.Unknown:
        return Element.UnknownVariable;
      case FeelSyntacticSymbolNature.Invocable:
        return Element.FunctionCall;
      case FeelSyntacticSymbolNature.Parameter:
        return Element.FunctionParameterVariable;
    }
  }
}
