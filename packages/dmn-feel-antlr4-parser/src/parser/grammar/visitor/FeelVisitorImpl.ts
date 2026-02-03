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

import FEEL_1_1Visitor from "../generated-parser/FEEL_1_1Visitor";
import {
  AddExpressionContext,
  FilterPathExpressionContext,
  FnInvocationContext,
  NameRefContext,
  ParametersEmptyContext,
  ParametersNamedContext,
  ParametersPositionalContext,
  PrimaryParensContext,
  QualifiedNameContext,
  UenpmPrimaryContext,
} from "../generated-parser/FEEL_1_1Parser";
import { SemanticToken } from "./SemanticToken";
import { ParserRuleContext, ParseTree, TerminalNode } from "antlr4";
import { FeelFunctionReturningTypes } from "../../FeelFunctionReturningTypes";
import { FeelSyntacticSymbolNature } from "../../FeelSyntacticSymbolNature";
import { VisitorResult } from "./VisitorResult";
import { BuiltInTypes } from "../../BuiltInTypes";
import { DataType } from "../../DataType";
import FEEL_1_1Lexer from "../generated-parser/FEEL_1_1Lexer";

export class FeelVisitorImpl extends FEEL_1_1Visitor<VisitorResult> {
  private readonly handlers: Map<Function, (node: ParseTree) => VisitorResult>;
  private readonly _semanticTokens: Array<SemanticToken>;
  private readonly _normalizedFeelFunctionReturningTypes: ReadonlyMap<string, DataType>;

  public constructor() {
    super();
    this._semanticTokens = new Array<SemanticToken>();
    this.handlers = new Map<Function, (node: ParseTree) => VisitorResult>([
      [FilterPathExpressionContext, this.visitFilterPathExpression.bind(this)],
      [FnInvocationContext, this.visitFnInvocation.bind(this)],
      [NameRefContext, this.visitNameRef.bind(this)],
      [AddExpressionContext, this.visitAddExpression.bind(this)],
      [PrimaryParensContext, this.visitPrimaryParens.bind(this)],
      [UenpmPrimaryContext, this.visitUenpmPrimary.bind(this)],
    ]);

    this._normalizedFeelFunctionReturningTypes = new Map(
      Array.from(FeelFunctionReturningTypes.Index, ([key, value]) => [key.replaceAll(" ", ""), value])
    );
  }

  private getSpecializedHandlerIfPresent(tree: ParseTree) {
    for (const [klass, handler] of this.handlers) {
      if (tree instanceof klass) {
        return handler;
      }
    }
  }

  get semanticTokens(): Array<SemanticToken> {
    return this._semanticTokens;
  }

  private resolveNames(nameContext: QualifiedNameContext | NameRefContext) {
    const resolvedNames: VisitorResult[] = [];
    if (!nameContext.children) {
      return resolvedNames;
    }

    for (let i = 0; i < nameContext.children.length; i++) {
      const child = nameContext.getChild(i);
      if (child instanceof NameRefContext || child instanceof QualifiedNameContext) {
        resolvedNames.push(...this.resolveNames(child));
      } else {
        resolvedNames.push(this.visit(child));
      }
    }

    return resolvedNames;
  }

  public override visit = (tree: ParseTree) => {
    // If there is a specialized handler for that kind of node, we use it, because it will know how to handle it.
    // Otherwise, we just go through they children.
    const specializedHandler = this.getSpecializedHandlerIfPresent(tree);
    if (specializedHandler) {
      return specializedHandler(tree);
    } else if (tree instanceof ParserRuleContext) {
      return this.visitChildren(tree);
    } else if (tree instanceof TerminalNode) {
      return new VisitorResult({ text: tree.getText(), dataType: this.getBuiltInTypeFromNodeType(tree.symbol.type) });
    }
    return undefined!;
  };

  public override visitChildren(ctx: ParserRuleContext) {
    if (!ctx.children) {
      return undefined!;
    }

    const result = new Array<VisitorResult>();
    const count = ctx.children?.length ?? 0;
    for (let i = 0; i < count; i++) {
      result.push(this.visit(ctx.children[i])!);
    }

    // In case of multiple children, probably we need a specialized handler to deal with,
    // but we are not doing that now because is one of the cases that we don't
    // use in the autocomplete or colorize.
    return result[0];
  }

  public override visitUenpmPrimary = (ctx: UenpmPrimaryContext) => {
    if (ctx.primary() === null || ctx.qualifiedName() === null || ctx.children === null) {
      return this.visitChildren(ctx);
    }

    // something DOT property
    const beforeDot = this.visit(ctx.primary());
    const afterDot = this.resolveNames(ctx.qualifiedName());

    // Here, we care about the first result after dot
    if (beforeDot.dataType.properties.has(afterDot[0].text)) {
      const startIndex = ctx.qualifiedName().start.start - 1;
      this._semanticTokens.push(
        new SemanticToken({
          startIndex: startIndex,
          endIndex: startIndex + afterDot[0].text.length,
          startLine: ctx.qualifiedName().start.line,
          endLine: ctx.qualifiedName().start.line,
          symbolNature: FeelSyntacticSymbolNature.LocalVariable,
          dataTypeReturn: beforeDot.dataType.properties.get(afterDot[0].text)!,
          text: afterDot[0].text,
        })
      );
    }

    const lastChildren = ctx.children[ctx.children.length - 1];
    if (
      lastChildren instanceof ParametersPositionalContext ||
      lastChildren instanceof ParametersEmptyContext ||
      lastChildren instanceof ParametersNamedContext
    ) {
      const text = afterDot[afterDot.length - 1].text;

      // Each expression has a type of expected parameters.
      if (this._normalizedFeelFunctionReturningTypes.has(text)) {
        this._semanticTokens.push(
          new SemanticToken({
            startIndex: lastChildren.LPAREN().symbol.start,
            endIndex: lastChildren.RPAREN().symbol.start,
            startLine: lastChildren.LPAREN().symbol.line,
            endLine: lastChildren.RPAREN().symbol.line,
            symbolNature: FeelSyntacticSymbolNature.FunctionCall,
            dataTypeReturn: this._normalizedFeelFunctionReturningTypes.get(text)!,
            text: "", // Empty string. It doesn't have a text.
          })
        );

        return new VisitorResult({
          text: ctx.getText(),
          dataType: this._normalizedFeelFunctionReturningTypes.get(text),
        });
      }
    }

    return new VisitorResult({ text: ctx.getText(), dataType: beforeDot.dataType.properties.get(afterDot[0].text) });
  };

  public override visitPrimaryParens = (ctx: PrimaryParensContext) => {
    const innerExpressionResult = this.visit(ctx.expression());

    this._semanticTokens.push(
      new SemanticToken({
        startIndex: ctx.RPAREN().symbol.start - 1,
        endIndex: ctx.RPAREN().symbol.start,
        startLine: ctx.RPAREN().symbol.line,
        endLine: ctx.RPAREN().symbol.line,
        symbolNature: FeelSyntacticSymbolNature.FunctionCall,
        dataTypeReturn: innerExpressionResult.dataType,
        text: ")",
      })
    );

    return innerExpressionResult;
  };

  public override visitAddExpression = (ctx: AddExpressionContext) => {
    const additiveExpression = ctx.additiveExpression();
    const multiplicativeExpression = ctx.multiplicativeExpression();

    const addResult = this.visit(additiveExpression);
    const multResult = this.visit(multiplicativeExpression);

    if (addResult.dataType === multResult.dataType) {
      if (addResult.dataType === BuiltInTypes.Date) {
        return new VisitorResult({ text: ctx.getText(), dataType: BuiltInTypes.DaysAndTimeDuration });
      } else {
        return new VisitorResult({ text: ctx.getText(), dataType: addResult.dataType });
      }
    } else {
      // We return he right-most result, because the expression is inconsistent.
      // In this case, we can assume that the user is typing.
      return new VisitorResult({ text: ctx.getText(), dataType: multResult.dataType });
    }
  };

  public override visitNameRef = (ctx: NameRefContext) => {
    return this.visitChildren(ctx);
  };

  public override visitFilterPathExpression = (ctx: FilterPathExpressionContext) => {
    if (ctx.filterPathExpression() === null || ctx.qualifiedName() === null || ctx.qualifiedName().children === null) {
      return this.visitChildren(ctx);
    }

    const beforeDot = this.visit(ctx.filterPathExpression());
    const afterDot = this.resolveNames(ctx.qualifiedName());

    // Here, we care about the first result after dot
    if (beforeDot.dataType.properties.has(afterDot[0].text)) {
      const startIndex = ctx.qualifiedName().start.start - 1;
      this._semanticTokens.push(
        new SemanticToken({
          startIndex: startIndex,
          endIndex: startIndex + afterDot[0].text.length,
          startLine: ctx.qualifiedName().start.line,
          endLine: ctx.qualifiedName().start.line,
          symbolNature: FeelSyntacticSymbolNature.LocalVariable,
          dataTypeReturn: beforeDot.dataType.properties.get(afterDot[0].text)!,
          text: afterDot[0].text,
        })
      );
    }

    return new VisitorResult({ text: ctx.getText(), dataType: beforeDot.dataType.properties.get(afterDot[0].text) });
  };

  public override visitFnInvocation = (ctx: FnInvocationContext) => {
    if (ctx.children?.length != 2) {
      // It is a malformed expression.
      return undefined!;
    }

    const left = ctx.getChild(0).getText();
    const right = ctx.getChild(1);

    // Each expression has a type of expected parameters.
    if (
      this._normalizedFeelFunctionReturningTypes.has(left) &&
      (right instanceof ParametersPositionalContext ||
        right instanceof ParametersEmptyContext ||
        right instanceof ParametersNamedContext)
    ) {
      this._semanticTokens.push(
        new SemanticToken({
          startIndex: right.LPAREN().symbol.start,
          endIndex: right.RPAREN().symbol.start,
          startLine: right.LPAREN().symbol.line,
          endLine: right.RPAREN().symbol.line,
          symbolNature: FeelSyntacticSymbolNature.FunctionCall,
          dataTypeReturn: this._normalizedFeelFunctionReturningTypes.get(left)!,
          text: "", // Empty string. It doesn't have a text.
        })
      );

      return new VisitorResult({ text: ctx.getText(), dataType: this._normalizedFeelFunctionReturningTypes.get(left) });
    } else {
      return this.visit(right);
    }
  };

  private getBuiltInTypeFromNodeType(nodeType: number) {
    switch (nodeType) {
      case FEEL_1_1Lexer.BooleanLiteral:
        return BuiltInTypes.Boolean;
      case FEEL_1_1Lexer.IntegerLiteral:
      case FEEL_1_1Lexer.FloatingPointLiteral:
        return BuiltInTypes.Number;
      case FEEL_1_1Lexer.StringLiteral:
        return BuiltInTypes.String;

      // The other types we just don't care
      default:
        return BuiltInTypes.Any;
    }
  }
}
