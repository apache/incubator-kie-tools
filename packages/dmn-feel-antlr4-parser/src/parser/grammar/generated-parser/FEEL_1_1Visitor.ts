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

// Generated from FEEL_1_1.g4 by ANTLR 4.13.0

import { ParseTreeVisitor } from "antlr4";

import { Compilation_unitContext } from "./FEEL_1_1Parser";
import { ExpressionTextualContext } from "./FEEL_1_1Parser";
import { TextualExpressionContext } from "./FEEL_1_1Parser";
import { ParametersEmptyContext } from "./FEEL_1_1Parser";
import { ParametersNamedContext } from "./FEEL_1_1Parser";
import { ParametersPositionalContext } from "./FEEL_1_1Parser";
import { NamedParametersContext } from "./FEEL_1_1Parser";
import { NamedParameterContext } from "./FEEL_1_1Parser";
import { PositionalParametersContext } from "./FEEL_1_1Parser";
import { ForExpressionContext } from "./FEEL_1_1Parser";
import { IterationContextsContext } from "./FEEL_1_1Parser";
import { IterationContextContext } from "./FEEL_1_1Parser";
import { IfExpressionContext } from "./FEEL_1_1Parser";
import { QuantExprSomeContext } from "./FEEL_1_1Parser";
import { QuantExprEveryContext } from "./FEEL_1_1Parser";
import { ListTypeContext } from "./FEEL_1_1Parser";
import { ContextTypeContext } from "./FEEL_1_1Parser";
import { QnTypeContext } from "./FEEL_1_1Parser";
import { FunctionTypeContext } from "./FEEL_1_1Parser";
import { ListContext } from "./FEEL_1_1Parser";
import { FunctionDefinitionContext } from "./FEEL_1_1Parser";
import { FormalParametersContext } from "./FEEL_1_1Parser";
import { FormalParameterContext } from "./FEEL_1_1Parser";
import { ContextContext } from "./FEEL_1_1Parser";
import { ContextEntriesContext } from "./FEEL_1_1Parser";
import { ContextEntryContext } from "./FEEL_1_1Parser";
import { KeyNameContext } from "./FEEL_1_1Parser";
import { KeyStringContext } from "./FEEL_1_1Parser";
import { NameDefinitionContext } from "./FEEL_1_1Parser";
import { NameDefinitionWithEOFContext } from "./FEEL_1_1Parser";
import { NameDefinitionTokensContext } from "./FEEL_1_1Parser";
import { IterationNameDefinitionContext } from "./FEEL_1_1Parser";
import { IterationNameDefinitionTokensContext } from "./FEEL_1_1Parser";
import { AdditionalNameSymbolContext } from "./FEEL_1_1Parser";
import { CondOrContext } from "./FEEL_1_1Parser";
import { CondOrAndContext } from "./FEEL_1_1Parser";
import { CondAndCompContext } from "./FEEL_1_1Parser";
import { CondAndContext } from "./FEEL_1_1Parser";
import { CompExpressionContext } from "./FEEL_1_1Parser";
import { CompExpressionRelContext } from "./FEEL_1_1Parser";
import { RelExpressionBetweenContext } from "./FEEL_1_1Parser";
import { RelExpressionValueContext } from "./FEEL_1_1Parser";
import { RelExpressionTestListContext } from "./FEEL_1_1Parser";
import { RelExpressionAddContext } from "./FEEL_1_1Parser";
import { RelExpressionInstanceOfContext } from "./FEEL_1_1Parser";
import { ExpressionListContext } from "./FEEL_1_1Parser";
import { AddExpressionMultContext } from "./FEEL_1_1Parser";
import { AddExpressionContext } from "./FEEL_1_1Parser";
import { MultExpressionPowContext } from "./FEEL_1_1Parser";
import { MultExpressionContext } from "./FEEL_1_1Parser";
import { PowExpressionUnaryContext } from "./FEEL_1_1Parser";
import { PowExpressionContext } from "./FEEL_1_1Parser";
import { FilterPathExpressionContext } from "./FEEL_1_1Parser";
import { SignedUnaryExpressionPlusContext } from "./FEEL_1_1Parser";
import { SignedUnaryExpressionMinusContext } from "./FEEL_1_1Parser";
import { FnInvocationContext } from "./FEEL_1_1Parser";
import { NonSignedUnaryExpressionContext } from "./FEEL_1_1Parser";
import { UenpmPrimaryContext } from "./FEEL_1_1Parser";
import { PrimaryLiteralContext } from "./FEEL_1_1Parser";
import { PrimaryForExpressionContext } from "./FEEL_1_1Parser";
import { PrimaryQuantifiedExpressionContext } from "./FEEL_1_1Parser";
import { PrimaryIfExpressionContext } from "./FEEL_1_1Parser";
import { PrimaryIntervalContext } from "./FEEL_1_1Parser";
import { PrimaryListContext } from "./FEEL_1_1Parser";
import { PrimaryContextContext } from "./FEEL_1_1Parser";
import { PrimaryParensContext } from "./FEEL_1_1Parser";
import { PrimaryUnaryTestContext } from "./FEEL_1_1Parser";
import { PrimaryNameContext } from "./FEEL_1_1Parser";
import { NumberLiteralContext } from "./FEEL_1_1Parser";
import { BoolLiteralContext } from "./FEEL_1_1Parser";
import { AtLiteralLabelContext } from "./FEEL_1_1Parser";
import { StringLiteralContext } from "./FEEL_1_1Parser";
import { NullLiteralContext } from "./FEEL_1_1Parser";
import { AtLiteralContext } from "./FEEL_1_1Parser";
import { AtLiteralValueContext } from "./FEEL_1_1Parser";
import { PositiveUnaryTestIneqIntervalContext } from "./FEEL_1_1Parser";
import { PositiveUnaryTestIneqContext } from "./FEEL_1_1Parser";
import { PositiveUnaryTestIntervalContext } from "./FEEL_1_1Parser";
import { SimplePositiveUnaryTestsContext } from "./FEEL_1_1Parser";
import { PositiveSimplePositiveUnaryTestsContext } from "./FEEL_1_1Parser";
import { NegatedSimplePositiveUnaryTestsContext } from "./FEEL_1_1Parser";
import { PositiveUnaryTestDashContext } from "./FEEL_1_1Parser";
import { PositiveUnaryTestContext } from "./FEEL_1_1Parser";
import { PositiveUnaryTestsContext } from "./FEEL_1_1Parser";
import { UnaryTestsRootContext } from "./FEEL_1_1Parser";
import { UnaryTests_negatedContext } from "./FEEL_1_1Parser";
import { UnaryTests_positiveContext } from "./FEEL_1_1Parser";
import { UnaryTests_emptyContext } from "./FEEL_1_1Parser";
import { EndpointContext } from "./FEEL_1_1Parser";
import { IntervalContext } from "./FEEL_1_1Parser";
import { QualifiedNameContext } from "./FEEL_1_1Parser";
import { NameRefContext } from "./FEEL_1_1Parser";
import { NameRefOtherTokenContext } from "./FEEL_1_1Parser";
import { ReusableKeywordsContext } from "./FEEL_1_1Parser";

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by `FEEL_1_1Parser`.
 *
 * @param <Result> The return type of the visit operation. Use `void` for
 * operations with no return type.
 */
export default class FEEL_1_1Visitor<Result> extends ParseTreeVisitor<Result> {
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.compilation_unit`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCompilation_unit?: (ctx: Compilation_unitContext) => Result;
  /**
   * Visit a parse tree produced by the `expressionTextual`
   * labeled alternative in `FEEL_1_1Parser.expression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitExpressionTextual?: (ctx: ExpressionTextualContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.textualExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitTextualExpression?: (ctx: TextualExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `parametersEmpty`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitParametersEmpty?: (ctx: ParametersEmptyContext) => Result;
  /**
   * Visit a parse tree produced by the `parametersNamed`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitParametersNamed?: (ctx: ParametersNamedContext) => Result;
  /**
   * Visit a parse tree produced by the `parametersPositional`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitParametersPositional?: (ctx: ParametersPositionalContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.namedParameters`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNamedParameters?: (ctx: NamedParametersContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.namedParameter`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNamedParameter?: (ctx: NamedParameterContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.positionalParameters`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositionalParameters?: (ctx: PositionalParametersContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.forExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitForExpression?: (ctx: ForExpressionContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.iterationContexts`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitIterationContexts?: (ctx: IterationContextsContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.iterationContext`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitIterationContext?: (ctx: IterationContextContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.ifExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitIfExpression?: (ctx: IfExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `quantExprSome`
   * labeled alternative in `FEEL_1_1Parser.quantifiedExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitQuantExprSome?: (ctx: QuantExprSomeContext) => Result;
  /**
   * Visit a parse tree produced by the `quantExprEvery`
   * labeled alternative in `FEEL_1_1Parser.quantifiedExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitQuantExprEvery?: (ctx: QuantExprEveryContext) => Result;
  /**
   * Visit a parse tree produced by the `listType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitListType?: (ctx: ListTypeContext) => Result;
  /**
   * Visit a parse tree produced by the `contextType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitContextType?: (ctx: ContextTypeContext) => Result;
  /**
   * Visit a parse tree produced by the `qnType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitQnType?: (ctx: QnTypeContext) => Result;
  /**
   * Visit a parse tree produced by the `functionType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitFunctionType?: (ctx: FunctionTypeContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.list`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitList?: (ctx: ListContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.functionDefinition`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitFunctionDefinition?: (ctx: FunctionDefinitionContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.formalParameters`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitFormalParameters?: (ctx: FormalParametersContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.formalParameter`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitFormalParameter?: (ctx: FormalParameterContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.context`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitContext?: (ctx: ContextContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.contextEntries`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitContextEntries?: (ctx: ContextEntriesContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.contextEntry`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitContextEntry?: (ctx: ContextEntryContext) => Result;
  /**
   * Visit a parse tree produced by the `keyName`
   * labeled alternative in `FEEL_1_1Parser.key`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitKeyName?: (ctx: KeyNameContext) => Result;
  /**
   * Visit a parse tree produced by the `keyString`
   * labeled alternative in `FEEL_1_1Parser.key`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitKeyString?: (ctx: KeyStringContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.nameDefinition`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNameDefinition?: (ctx: NameDefinitionContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.nameDefinitionWithEOF`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNameDefinitionWithEOF?: (ctx: NameDefinitionWithEOFContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.nameDefinitionTokens`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNameDefinitionTokens?: (ctx: NameDefinitionTokensContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.iterationNameDefinition`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitIterationNameDefinition?: (ctx: IterationNameDefinitionContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.iterationNameDefinitionTokens`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitIterationNameDefinitionTokens?: (ctx: IterationNameDefinitionTokensContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.additionalNameSymbol`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitAdditionalNameSymbol?: (ctx: AdditionalNameSymbolContext) => Result;
  /**
   * Visit a parse tree produced by the `condOr`
   * labeled alternative in `FEEL_1_1Parser.conditionalOrExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCondOr?: (ctx: CondOrContext) => Result;
  /**
   * Visit a parse tree produced by the `condOrAnd`
   * labeled alternative in `FEEL_1_1Parser.conditionalOrExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCondOrAnd?: (ctx: CondOrAndContext) => Result;
  /**
   * Visit a parse tree produced by the `condAndComp`
   * labeled alternative in `FEEL_1_1Parser.conditionalAndExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCondAndComp?: (ctx: CondAndCompContext) => Result;
  /**
   * Visit a parse tree produced by the `condAnd`
   * labeled alternative in `FEEL_1_1Parser.conditionalAndExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCondAnd?: (ctx: CondAndContext) => Result;
  /**
   * Visit a parse tree produced by the `compExpression`
   * labeled alternative in `FEEL_1_1Parser.comparisonExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCompExpression?: (ctx: CompExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `compExpressionRel`
   * labeled alternative in `FEEL_1_1Parser.comparisonExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitCompExpressionRel?: (ctx: CompExpressionRelContext) => Result;
  /**
   * Visit a parse tree produced by the `relExpressionBetween`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitRelExpressionBetween?: (ctx: RelExpressionBetweenContext) => Result;
  /**
   * Visit a parse tree produced by the `relExpressionValue`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitRelExpressionValue?: (ctx: RelExpressionValueContext) => Result;
  /**
   * Visit a parse tree produced by the `relExpressionTestList`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitRelExpressionTestList?: (ctx: RelExpressionTestListContext) => Result;
  /**
   * Visit a parse tree produced by the `relExpressionAdd`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitRelExpressionAdd?: (ctx: RelExpressionAddContext) => Result;
  /**
   * Visit a parse tree produced by the `relExpressionInstanceOf`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitRelExpressionInstanceOf?: (ctx: RelExpressionInstanceOfContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.expressionList`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitExpressionList?: (ctx: ExpressionListContext) => Result;
  /**
   * Visit a parse tree produced by the `addExpressionMult`
   * labeled alternative in `FEEL_1_1Parser.additiveExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitAddExpressionMult?: (ctx: AddExpressionMultContext) => Result;
  /**
   * Visit a parse tree produced by the `addExpression`
   * labeled alternative in `FEEL_1_1Parser.additiveExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitAddExpression?: (ctx: AddExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `multExpressionPow`
   * labeled alternative in `FEEL_1_1Parser.multiplicativeExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitMultExpressionPow?: (ctx: MultExpressionPowContext) => Result;
  /**
   * Visit a parse tree produced by the `multExpression`
   * labeled alternative in `FEEL_1_1Parser.multiplicativeExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitMultExpression?: (ctx: MultExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `powExpressionUnary`
   * labeled alternative in `FEEL_1_1Parser.powerExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPowExpressionUnary?: (ctx: PowExpressionUnaryContext) => Result;
  /**
   * Visit a parse tree produced by the `powExpression`
   * labeled alternative in `FEEL_1_1Parser.powerExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPowExpression?: (ctx: PowExpressionContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.filterPathExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitFilterPathExpression?: (ctx: FilterPathExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `signedUnaryExpressionPlus`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitSignedUnaryExpressionPlus?: (ctx: SignedUnaryExpressionPlusContext) => Result;
  /**
   * Visit a parse tree produced by the `signedUnaryExpressionMinus`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitSignedUnaryExpressionMinus?: (ctx: SignedUnaryExpressionMinusContext) => Result;
  /**
   * Visit a parse tree produced by the `fnInvocation`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitFnInvocation?: (ctx: FnInvocationContext) => Result;
  /**
   * Visit a parse tree produced by the `nonSignedUnaryExpression`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNonSignedUnaryExpression?: (ctx: NonSignedUnaryExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `uenpmPrimary`
   * labeled alternative in `FEEL_1_1Parser.unaryExpressionNotPlusMinus`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitUenpmPrimary?: (ctx: UenpmPrimaryContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryLiteral`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryLiteral?: (ctx: PrimaryLiteralContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryForExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryForExpression?: (ctx: PrimaryForExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryQuantifiedExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryQuantifiedExpression?: (ctx: PrimaryQuantifiedExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryIfExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryIfExpression?: (ctx: PrimaryIfExpressionContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryInterval`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryInterval?: (ctx: PrimaryIntervalContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryList`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryList?: (ctx: PrimaryListContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryContext`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryContext?: (ctx: PrimaryContextContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryParens`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryParens?: (ctx: PrimaryParensContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryUnaryTest`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryUnaryTest?: (ctx: PrimaryUnaryTestContext) => Result;
  /**
   * Visit a parse tree produced by the `primaryName`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPrimaryName?: (ctx: PrimaryNameContext) => Result;
  /**
   * Visit a parse tree produced by the `numberLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNumberLiteral?: (ctx: NumberLiteralContext) => Result;
  /**
   * Visit a parse tree produced by the `boolLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitBoolLiteral?: (ctx: BoolLiteralContext) => Result;
  /**
   * Visit a parse tree produced by the `atLiteralLabel`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitAtLiteralLabel?: (ctx: AtLiteralLabelContext) => Result;
  /**
   * Visit a parse tree produced by the `stringLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitStringLiteral?: (ctx: StringLiteralContext) => Result;
  /**
   * Visit a parse tree produced by the `nullLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNullLiteral?: (ctx: NullLiteralContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.atLiteral`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitAtLiteral?: (ctx: AtLiteralContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.atLiteralValue`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitAtLiteralValue?: (ctx: AtLiteralValueContext) => Result;
  /**
   * Visit a parse tree produced by the `positiveUnaryTestIneqInterval`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveUnaryTestIneqInterval?: (ctx: PositiveUnaryTestIneqIntervalContext) => Result;
  /**
   * Visit a parse tree produced by the `positiveUnaryTestIneq`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveUnaryTestIneq?: (ctx: PositiveUnaryTestIneqContext) => Result;
  /**
   * Visit a parse tree produced by the `positiveUnaryTestInterval`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveUnaryTestInterval?: (ctx: PositiveUnaryTestIntervalContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.simplePositiveUnaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitSimplePositiveUnaryTests?: (ctx: SimplePositiveUnaryTestsContext) => Result;
  /**
   * Visit a parse tree produced by the `positiveSimplePositiveUnaryTests`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveSimplePositiveUnaryTests?: (ctx: PositiveSimplePositiveUnaryTestsContext) => Result;
  /**
   * Visit a parse tree produced by the `negatedSimplePositiveUnaryTests`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNegatedSimplePositiveUnaryTests?: (ctx: NegatedSimplePositiveUnaryTestsContext) => Result;
  /**
   * Visit a parse tree produced by the `positiveUnaryTestDash`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveUnaryTestDash?: (ctx: PositiveUnaryTestDashContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.positiveUnaryTest`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveUnaryTest?: (ctx: PositiveUnaryTestContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.positiveUnaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitPositiveUnaryTests?: (ctx: PositiveUnaryTestsContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.unaryTestsRoot`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitUnaryTestsRoot?: (ctx: UnaryTestsRootContext) => Result;
  /**
   * Visit a parse tree produced by the `unaryTests_negated`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitUnaryTests_negated?: (ctx: UnaryTests_negatedContext) => Result;
  /**
   * Visit a parse tree produced by the `unaryTests_positive`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitUnaryTests_positive?: (ctx: UnaryTests_positiveContext) => Result;
  /**
   * Visit a parse tree produced by the `unaryTests_empty`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitUnaryTests_empty?: (ctx: UnaryTests_emptyContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.endpoint`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitEndpoint?: (ctx: EndpointContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.interval`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitInterval?: (ctx: IntervalContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.qualifiedName`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitQualifiedName?: (ctx: QualifiedNameContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.nameRef`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNameRef?: (ctx: NameRefContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.nameRefOtherToken`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitNameRefOtherToken?: (ctx: NameRefOtherTokenContext) => Result;
  /**
   * Visit a parse tree produced by `FEEL_1_1Parser.reusableKeywords`.
   * @param ctx the parse tree
   * @return the visitor result
   */
  visitReusableKeywords?: (ctx: ReusableKeywordsContext) => Result;
}
