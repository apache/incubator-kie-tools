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

import { ParseTreeListener } from "antlr4";

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
 * This interface defines a complete listener for a parse tree produced by
 * `FEEL_1_1Parser`.
 */
export default class FEEL_1_1Listener extends ParseTreeListener {
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.compilation_unit`.
   * @param ctx the parse tree
   */
  enterCompilation_unit?: (ctx: Compilation_unitContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.compilation_unit`.
   * @param ctx the parse tree
   */
  exitCompilation_unit?: (ctx: Compilation_unitContext) => void;
  /**
   * Enter a parse tree produced by the `expressionTextual`
   * labeled alternative in `FEEL_1_1Parser.expression`.
   * @param ctx the parse tree
   */
  enterExpressionTextual?: (ctx: ExpressionTextualContext) => void;
  /**
   * Exit a parse tree produced by the `expressionTextual`
   * labeled alternative in `FEEL_1_1Parser.expression`.
   * @param ctx the parse tree
   */
  exitExpressionTextual?: (ctx: ExpressionTextualContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.textualExpression`.
   * @param ctx the parse tree
   */
  enterTextualExpression?: (ctx: TextualExpressionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.textualExpression`.
   * @param ctx the parse tree
   */
  exitTextualExpression?: (ctx: TextualExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `parametersEmpty`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   */
  enterParametersEmpty?: (ctx: ParametersEmptyContext) => void;
  /**
   * Exit a parse tree produced by the `parametersEmpty`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   */
  exitParametersEmpty?: (ctx: ParametersEmptyContext) => void;
  /**
   * Enter a parse tree produced by the `parametersNamed`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   */
  enterParametersNamed?: (ctx: ParametersNamedContext) => void;
  /**
   * Exit a parse tree produced by the `parametersNamed`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   */
  exitParametersNamed?: (ctx: ParametersNamedContext) => void;
  /**
   * Enter a parse tree produced by the `parametersPositional`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   */
  enterParametersPositional?: (ctx: ParametersPositionalContext) => void;
  /**
   * Exit a parse tree produced by the `parametersPositional`
   * labeled alternative in `FEEL_1_1Parser.parameters`.
   * @param ctx the parse tree
   */
  exitParametersPositional?: (ctx: ParametersPositionalContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.namedParameters`.
   * @param ctx the parse tree
   */
  enterNamedParameters?: (ctx: NamedParametersContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.namedParameters`.
   * @param ctx the parse tree
   */
  exitNamedParameters?: (ctx: NamedParametersContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.namedParameter`.
   * @param ctx the parse tree
   */
  enterNamedParameter?: (ctx: NamedParameterContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.namedParameter`.
   * @param ctx the parse tree
   */
  exitNamedParameter?: (ctx: NamedParameterContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.positionalParameters`.
   * @param ctx the parse tree
   */
  enterPositionalParameters?: (ctx: PositionalParametersContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.positionalParameters`.
   * @param ctx the parse tree
   */
  exitPositionalParameters?: (ctx: PositionalParametersContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.forExpression`.
   * @param ctx the parse tree
   */
  enterForExpression?: (ctx: ForExpressionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.forExpression`.
   * @param ctx the parse tree
   */
  exitForExpression?: (ctx: ForExpressionContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.iterationContexts`.
   * @param ctx the parse tree
   */
  enterIterationContexts?: (ctx: IterationContextsContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.iterationContexts`.
   * @param ctx the parse tree
   */
  exitIterationContexts?: (ctx: IterationContextsContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.iterationContext`.
   * @param ctx the parse tree
   */
  enterIterationContext?: (ctx: IterationContextContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.iterationContext`.
   * @param ctx the parse tree
   */
  exitIterationContext?: (ctx: IterationContextContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.ifExpression`.
   * @param ctx the parse tree
   */
  enterIfExpression?: (ctx: IfExpressionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.ifExpression`.
   * @param ctx the parse tree
   */
  exitIfExpression?: (ctx: IfExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `quantExprSome`
   * labeled alternative in `FEEL_1_1Parser.quantifiedExpression`.
   * @param ctx the parse tree
   */
  enterQuantExprSome?: (ctx: QuantExprSomeContext) => void;
  /**
   * Exit a parse tree produced by the `quantExprSome`
   * labeled alternative in `FEEL_1_1Parser.quantifiedExpression`.
   * @param ctx the parse tree
   */
  exitQuantExprSome?: (ctx: QuantExprSomeContext) => void;
  /**
   * Enter a parse tree produced by the `quantExprEvery`
   * labeled alternative in `FEEL_1_1Parser.quantifiedExpression`.
   * @param ctx the parse tree
   */
  enterQuantExprEvery?: (ctx: QuantExprEveryContext) => void;
  /**
   * Exit a parse tree produced by the `quantExprEvery`
   * labeled alternative in `FEEL_1_1Parser.quantifiedExpression`.
   * @param ctx the parse tree
   */
  exitQuantExprEvery?: (ctx: QuantExprEveryContext) => void;
  /**
   * Enter a parse tree produced by the `listType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  enterListType?: (ctx: ListTypeContext) => void;
  /**
   * Exit a parse tree produced by the `listType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  exitListType?: (ctx: ListTypeContext) => void;
  /**
   * Enter a parse tree produced by the `contextType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  enterContextType?: (ctx: ContextTypeContext) => void;
  /**
   * Exit a parse tree produced by the `contextType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  exitContextType?: (ctx: ContextTypeContext) => void;
  /**
   * Enter a parse tree produced by the `qnType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  enterQnType?: (ctx: QnTypeContext) => void;
  /**
   * Exit a parse tree produced by the `qnType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  exitQnType?: (ctx: QnTypeContext) => void;
  /**
   * Enter a parse tree produced by the `functionType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  enterFunctionType?: (ctx: FunctionTypeContext) => void;
  /**
   * Exit a parse tree produced by the `functionType`
   * labeled alternative in `FEEL_1_1Parser.type`.
   * @param ctx the parse tree
   */
  exitFunctionType?: (ctx: FunctionTypeContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.list`.
   * @param ctx the parse tree
   */
  enterList?: (ctx: ListContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.list`.
   * @param ctx the parse tree
   */
  exitList?: (ctx: ListContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.functionDefinition`.
   * @param ctx the parse tree
   */
  enterFunctionDefinition?: (ctx: FunctionDefinitionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.functionDefinition`.
   * @param ctx the parse tree
   */
  exitFunctionDefinition?: (ctx: FunctionDefinitionContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.formalParameters`.
   * @param ctx the parse tree
   */
  enterFormalParameters?: (ctx: FormalParametersContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.formalParameters`.
   * @param ctx the parse tree
   */
  exitFormalParameters?: (ctx: FormalParametersContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.formalParameter`.
   * @param ctx the parse tree
   */
  enterFormalParameter?: (ctx: FormalParameterContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.formalParameter`.
   * @param ctx the parse tree
   */
  exitFormalParameter?: (ctx: FormalParameterContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.context`.
   * @param ctx the parse tree
   */
  enterContext?: (ctx: ContextContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.context`.
   * @param ctx the parse tree
   */
  exitContext?: (ctx: ContextContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.contextEntries`.
   * @param ctx the parse tree
   */
  enterContextEntries?: (ctx: ContextEntriesContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.contextEntries`.
   * @param ctx the parse tree
   */
  exitContextEntries?: (ctx: ContextEntriesContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.contextEntry`.
   * @param ctx the parse tree
   */
  enterContextEntry?: (ctx: ContextEntryContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.contextEntry`.
   * @param ctx the parse tree
   */
  exitContextEntry?: (ctx: ContextEntryContext) => void;
  /**
   * Enter a parse tree produced by the `keyName`
   * labeled alternative in `FEEL_1_1Parser.key`.
   * @param ctx the parse tree
   */
  enterKeyName?: (ctx: KeyNameContext) => void;
  /**
   * Exit a parse tree produced by the `keyName`
   * labeled alternative in `FEEL_1_1Parser.key`.
   * @param ctx the parse tree
   */
  exitKeyName?: (ctx: KeyNameContext) => void;
  /**
   * Enter a parse tree produced by the `keyString`
   * labeled alternative in `FEEL_1_1Parser.key`.
   * @param ctx the parse tree
   */
  enterKeyString?: (ctx: KeyStringContext) => void;
  /**
   * Exit a parse tree produced by the `keyString`
   * labeled alternative in `FEEL_1_1Parser.key`.
   * @param ctx the parse tree
   */
  exitKeyString?: (ctx: KeyStringContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.nameDefinition`.
   * @param ctx the parse tree
   */
  enterNameDefinition?: (ctx: NameDefinitionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.nameDefinition`.
   * @param ctx the parse tree
   */
  exitNameDefinition?: (ctx: NameDefinitionContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.nameDefinitionWithEOF`.
   * @param ctx the parse tree
   */
  enterNameDefinitionWithEOF?: (ctx: NameDefinitionWithEOFContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.nameDefinitionWithEOF`.
   * @param ctx the parse tree
   */
  exitNameDefinitionWithEOF?: (ctx: NameDefinitionWithEOFContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.nameDefinitionTokens`.
   * @param ctx the parse tree
   */
  enterNameDefinitionTokens?: (ctx: NameDefinitionTokensContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.nameDefinitionTokens`.
   * @param ctx the parse tree
   */
  exitNameDefinitionTokens?: (ctx: NameDefinitionTokensContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.iterationNameDefinition`.
   * @param ctx the parse tree
   */
  enterIterationNameDefinition?: (ctx: IterationNameDefinitionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.iterationNameDefinition`.
   * @param ctx the parse tree
   */
  exitIterationNameDefinition?: (ctx: IterationNameDefinitionContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.iterationNameDefinitionTokens`.
   * @param ctx the parse tree
   */
  enterIterationNameDefinitionTokens?: (ctx: IterationNameDefinitionTokensContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.iterationNameDefinitionTokens`.
   * @param ctx the parse tree
   */
  exitIterationNameDefinitionTokens?: (ctx: IterationNameDefinitionTokensContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.additionalNameSymbol`.
   * @param ctx the parse tree
   */
  enterAdditionalNameSymbol?: (ctx: AdditionalNameSymbolContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.additionalNameSymbol`.
   * @param ctx the parse tree
   */
  exitAdditionalNameSymbol?: (ctx: AdditionalNameSymbolContext) => void;
  /**
   * Enter a parse tree produced by the `condOr`
   * labeled alternative in `FEEL_1_1Parser.conditionalOrExpression`.
   * @param ctx the parse tree
   */
  enterCondOr?: (ctx: CondOrContext) => void;
  /**
   * Exit a parse tree produced by the `condOr`
   * labeled alternative in `FEEL_1_1Parser.conditionalOrExpression`.
   * @param ctx the parse tree
   */
  exitCondOr?: (ctx: CondOrContext) => void;
  /**
   * Enter a parse tree produced by the `condOrAnd`
   * labeled alternative in `FEEL_1_1Parser.conditionalOrExpression`.
   * @param ctx the parse tree
   */
  enterCondOrAnd?: (ctx: CondOrAndContext) => void;
  /**
   * Exit a parse tree produced by the `condOrAnd`
   * labeled alternative in `FEEL_1_1Parser.conditionalOrExpression`.
   * @param ctx the parse tree
   */
  exitCondOrAnd?: (ctx: CondOrAndContext) => void;
  /**
   * Enter a parse tree produced by the `condAndComp`
   * labeled alternative in `FEEL_1_1Parser.conditionalAndExpression`.
   * @param ctx the parse tree
   */
  enterCondAndComp?: (ctx: CondAndCompContext) => void;
  /**
   * Exit a parse tree produced by the `condAndComp`
   * labeled alternative in `FEEL_1_1Parser.conditionalAndExpression`.
   * @param ctx the parse tree
   */
  exitCondAndComp?: (ctx: CondAndCompContext) => void;
  /**
   * Enter a parse tree produced by the `condAnd`
   * labeled alternative in `FEEL_1_1Parser.conditionalAndExpression`.
   * @param ctx the parse tree
   */
  enterCondAnd?: (ctx: CondAndContext) => void;
  /**
   * Exit a parse tree produced by the `condAnd`
   * labeled alternative in `FEEL_1_1Parser.conditionalAndExpression`.
   * @param ctx the parse tree
   */
  exitCondAnd?: (ctx: CondAndContext) => void;
  /**
   * Enter a parse tree produced by the `compExpression`
   * labeled alternative in `FEEL_1_1Parser.comparisonExpression`.
   * @param ctx the parse tree
   */
  enterCompExpression?: (ctx: CompExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `compExpression`
   * labeled alternative in `FEEL_1_1Parser.comparisonExpression`.
   * @param ctx the parse tree
   */
  exitCompExpression?: (ctx: CompExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `compExpressionRel`
   * labeled alternative in `FEEL_1_1Parser.comparisonExpression`.
   * @param ctx the parse tree
   */
  enterCompExpressionRel?: (ctx: CompExpressionRelContext) => void;
  /**
   * Exit a parse tree produced by the `compExpressionRel`
   * labeled alternative in `FEEL_1_1Parser.comparisonExpression`.
   * @param ctx the parse tree
   */
  exitCompExpressionRel?: (ctx: CompExpressionRelContext) => void;
  /**
   * Enter a parse tree produced by the `relExpressionBetween`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  enterRelExpressionBetween?: (ctx: RelExpressionBetweenContext) => void;
  /**
   * Exit a parse tree produced by the `relExpressionBetween`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  exitRelExpressionBetween?: (ctx: RelExpressionBetweenContext) => void;
  /**
   * Enter a parse tree produced by the `relExpressionValue`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  enterRelExpressionValue?: (ctx: RelExpressionValueContext) => void;
  /**
   * Exit a parse tree produced by the `relExpressionValue`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  exitRelExpressionValue?: (ctx: RelExpressionValueContext) => void;
  /**
   * Enter a parse tree produced by the `relExpressionTestList`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  enterRelExpressionTestList?: (ctx: RelExpressionTestListContext) => void;
  /**
   * Exit a parse tree produced by the `relExpressionTestList`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  exitRelExpressionTestList?: (ctx: RelExpressionTestListContext) => void;
  /**
   * Enter a parse tree produced by the `relExpressionAdd`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  enterRelExpressionAdd?: (ctx: RelExpressionAddContext) => void;
  /**
   * Exit a parse tree produced by the `relExpressionAdd`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  exitRelExpressionAdd?: (ctx: RelExpressionAddContext) => void;
  /**
   * Enter a parse tree produced by the `relExpressionInstanceOf`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  enterRelExpressionInstanceOf?: (ctx: RelExpressionInstanceOfContext) => void;
  /**
   * Exit a parse tree produced by the `relExpressionInstanceOf`
   * labeled alternative in `FEEL_1_1Parser.relationalExpression`.
   * @param ctx the parse tree
   */
  exitRelExpressionInstanceOf?: (ctx: RelExpressionInstanceOfContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.expressionList`.
   * @param ctx the parse tree
   */
  enterExpressionList?: (ctx: ExpressionListContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.expressionList`.
   * @param ctx the parse tree
   */
  exitExpressionList?: (ctx: ExpressionListContext) => void;
  /**
   * Enter a parse tree produced by the `addExpressionMult`
   * labeled alternative in `FEEL_1_1Parser.additiveExpression`.
   * @param ctx the parse tree
   */
  enterAddExpressionMult?: (ctx: AddExpressionMultContext) => void;
  /**
   * Exit a parse tree produced by the `addExpressionMult`
   * labeled alternative in `FEEL_1_1Parser.additiveExpression`.
   * @param ctx the parse tree
   */
  exitAddExpressionMult?: (ctx: AddExpressionMultContext) => void;
  /**
   * Enter a parse tree produced by the `addExpression`
   * labeled alternative in `FEEL_1_1Parser.additiveExpression`.
   * @param ctx the parse tree
   */
  enterAddExpression?: (ctx: AddExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `addExpression`
   * labeled alternative in `FEEL_1_1Parser.additiveExpression`.
   * @param ctx the parse tree
   */
  exitAddExpression?: (ctx: AddExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `multExpressionPow`
   * labeled alternative in `FEEL_1_1Parser.multiplicativeExpression`.
   * @param ctx the parse tree
   */
  enterMultExpressionPow?: (ctx: MultExpressionPowContext) => void;
  /**
   * Exit a parse tree produced by the `multExpressionPow`
   * labeled alternative in `FEEL_1_1Parser.multiplicativeExpression`.
   * @param ctx the parse tree
   */
  exitMultExpressionPow?: (ctx: MultExpressionPowContext) => void;
  /**
   * Enter a parse tree produced by the `multExpression`
   * labeled alternative in `FEEL_1_1Parser.multiplicativeExpression`.
   * @param ctx the parse tree
   */
  enterMultExpression?: (ctx: MultExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `multExpression`
   * labeled alternative in `FEEL_1_1Parser.multiplicativeExpression`.
   * @param ctx the parse tree
   */
  exitMultExpression?: (ctx: MultExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `powExpressionUnary`
   * labeled alternative in `FEEL_1_1Parser.powerExpression`.
   * @param ctx the parse tree
   */
  enterPowExpressionUnary?: (ctx: PowExpressionUnaryContext) => void;
  /**
   * Exit a parse tree produced by the `powExpressionUnary`
   * labeled alternative in `FEEL_1_1Parser.powerExpression`.
   * @param ctx the parse tree
   */
  exitPowExpressionUnary?: (ctx: PowExpressionUnaryContext) => void;
  /**
   * Enter a parse tree produced by the `powExpression`
   * labeled alternative in `FEEL_1_1Parser.powerExpression`.
   * @param ctx the parse tree
   */
  enterPowExpression?: (ctx: PowExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `powExpression`
   * labeled alternative in `FEEL_1_1Parser.powerExpression`.
   * @param ctx the parse tree
   */
  exitPowExpression?: (ctx: PowExpressionContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.filterPathExpression`.
   * @param ctx the parse tree
   */
  enterFilterPathExpression?: (ctx: FilterPathExpressionContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.filterPathExpression`.
   * @param ctx the parse tree
   */
  exitFilterPathExpression?: (ctx: FilterPathExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `signedUnaryExpressionPlus`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  enterSignedUnaryExpressionPlus?: (ctx: SignedUnaryExpressionPlusContext) => void;
  /**
   * Exit a parse tree produced by the `signedUnaryExpressionPlus`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  exitSignedUnaryExpressionPlus?: (ctx: SignedUnaryExpressionPlusContext) => void;
  /**
   * Enter a parse tree produced by the `signedUnaryExpressionMinus`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  enterSignedUnaryExpressionMinus?: (ctx: SignedUnaryExpressionMinusContext) => void;
  /**
   * Exit a parse tree produced by the `signedUnaryExpressionMinus`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  exitSignedUnaryExpressionMinus?: (ctx: SignedUnaryExpressionMinusContext) => void;
  /**
   * Enter a parse tree produced by the `fnInvocation`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  enterFnInvocation?: (ctx: FnInvocationContext) => void;
  /**
   * Exit a parse tree produced by the `fnInvocation`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  exitFnInvocation?: (ctx: FnInvocationContext) => void;
  /**
   * Enter a parse tree produced by the `nonSignedUnaryExpression`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  enterNonSignedUnaryExpression?: (ctx: NonSignedUnaryExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `nonSignedUnaryExpression`
   * labeled alternative in `FEEL_1_1Parser.unaryExpression`.
   * @param ctx the parse tree
   */
  exitNonSignedUnaryExpression?: (ctx: NonSignedUnaryExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `uenpmPrimary`
   * labeled alternative in `FEEL_1_1Parser.unaryExpressionNotPlusMinus`.
   * @param ctx the parse tree
   */
  enterUenpmPrimary?: (ctx: UenpmPrimaryContext) => void;
  /**
   * Exit a parse tree produced by the `uenpmPrimary`
   * labeled alternative in `FEEL_1_1Parser.unaryExpressionNotPlusMinus`.
   * @param ctx the parse tree
   */
  exitUenpmPrimary?: (ctx: UenpmPrimaryContext) => void;
  /**
   * Enter a parse tree produced by the `primaryLiteral`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryLiteral?: (ctx: PrimaryLiteralContext) => void;
  /**
   * Exit a parse tree produced by the `primaryLiteral`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryLiteral?: (ctx: PrimaryLiteralContext) => void;
  /**
   * Enter a parse tree produced by the `primaryForExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryForExpression?: (ctx: PrimaryForExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `primaryForExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryForExpression?: (ctx: PrimaryForExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `primaryQuantifiedExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryQuantifiedExpression?: (ctx: PrimaryQuantifiedExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `primaryQuantifiedExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryQuantifiedExpression?: (ctx: PrimaryQuantifiedExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `primaryIfExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryIfExpression?: (ctx: PrimaryIfExpressionContext) => void;
  /**
   * Exit a parse tree produced by the `primaryIfExpression`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryIfExpression?: (ctx: PrimaryIfExpressionContext) => void;
  /**
   * Enter a parse tree produced by the `primaryInterval`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryInterval?: (ctx: PrimaryIntervalContext) => void;
  /**
   * Exit a parse tree produced by the `primaryInterval`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryInterval?: (ctx: PrimaryIntervalContext) => void;
  /**
   * Enter a parse tree produced by the `primaryList`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryList?: (ctx: PrimaryListContext) => void;
  /**
   * Exit a parse tree produced by the `primaryList`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryList?: (ctx: PrimaryListContext) => void;
  /**
   * Enter a parse tree produced by the `primaryContext`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryContext?: (ctx: PrimaryContextContext) => void;
  /**
   * Exit a parse tree produced by the `primaryContext`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryContext?: (ctx: PrimaryContextContext) => void;
  /**
   * Enter a parse tree produced by the `primaryParens`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryParens?: (ctx: PrimaryParensContext) => void;
  /**
   * Exit a parse tree produced by the `primaryParens`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryParens?: (ctx: PrimaryParensContext) => void;
  /**
   * Enter a parse tree produced by the `primaryUnaryTest`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryUnaryTest?: (ctx: PrimaryUnaryTestContext) => void;
  /**
   * Exit a parse tree produced by the `primaryUnaryTest`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryUnaryTest?: (ctx: PrimaryUnaryTestContext) => void;
  /**
   * Enter a parse tree produced by the `primaryName`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  enterPrimaryName?: (ctx: PrimaryNameContext) => void;
  /**
   * Exit a parse tree produced by the `primaryName`
   * labeled alternative in `FEEL_1_1Parser.primary`.
   * @param ctx the parse tree
   */
  exitPrimaryName?: (ctx: PrimaryNameContext) => void;
  /**
   * Enter a parse tree produced by the `numberLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  enterNumberLiteral?: (ctx: NumberLiteralContext) => void;
  /**
   * Exit a parse tree produced by the `numberLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  exitNumberLiteral?: (ctx: NumberLiteralContext) => void;
  /**
   * Enter a parse tree produced by the `boolLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  enterBoolLiteral?: (ctx: BoolLiteralContext) => void;
  /**
   * Exit a parse tree produced by the `boolLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  exitBoolLiteral?: (ctx: BoolLiteralContext) => void;
  /**
   * Enter a parse tree produced by the `atLiteralLabel`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  enterAtLiteralLabel?: (ctx: AtLiteralLabelContext) => void;
  /**
   * Exit a parse tree produced by the `atLiteralLabel`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  exitAtLiteralLabel?: (ctx: AtLiteralLabelContext) => void;
  /**
   * Enter a parse tree produced by the `stringLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  enterStringLiteral?: (ctx: StringLiteralContext) => void;
  /**
   * Exit a parse tree produced by the `stringLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  exitStringLiteral?: (ctx: StringLiteralContext) => void;
  /**
   * Enter a parse tree produced by the `nullLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  enterNullLiteral?: (ctx: NullLiteralContext) => void;
  /**
   * Exit a parse tree produced by the `nullLiteral`
   * labeled alternative in `FEEL_1_1Parser.literal`.
   * @param ctx the parse tree
   */
  exitNullLiteral?: (ctx: NullLiteralContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.atLiteral`.
   * @param ctx the parse tree
   */
  enterAtLiteral?: (ctx: AtLiteralContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.atLiteral`.
   * @param ctx the parse tree
   */
  exitAtLiteral?: (ctx: AtLiteralContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.atLiteralValue`.
   * @param ctx the parse tree
   */
  enterAtLiteralValue?: (ctx: AtLiteralValueContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.atLiteralValue`.
   * @param ctx the parse tree
   */
  exitAtLiteralValue?: (ctx: AtLiteralValueContext) => void;
  /**
   * Enter a parse tree produced by the `positiveUnaryTestIneqInterval`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   */
  enterPositiveUnaryTestIneqInterval?: (ctx: PositiveUnaryTestIneqIntervalContext) => void;
  /**
   * Exit a parse tree produced by the `positiveUnaryTestIneqInterval`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   */
  exitPositiveUnaryTestIneqInterval?: (ctx: PositiveUnaryTestIneqIntervalContext) => void;
  /**
   * Enter a parse tree produced by the `positiveUnaryTestIneq`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   */
  enterPositiveUnaryTestIneq?: (ctx: PositiveUnaryTestIneqContext) => void;
  /**
   * Exit a parse tree produced by the `positiveUnaryTestIneq`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   */
  exitPositiveUnaryTestIneq?: (ctx: PositiveUnaryTestIneqContext) => void;
  /**
   * Enter a parse tree produced by the `positiveUnaryTestInterval`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   */
  enterPositiveUnaryTestInterval?: (ctx: PositiveUnaryTestIntervalContext) => void;
  /**
   * Exit a parse tree produced by the `positiveUnaryTestInterval`
   * labeled alternative in `FEEL_1_1Parser.simplePositiveUnaryTest`.
   * @param ctx the parse tree
   */
  exitPositiveUnaryTestInterval?: (ctx: PositiveUnaryTestIntervalContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.simplePositiveUnaryTests`.
   * @param ctx the parse tree
   */
  enterSimplePositiveUnaryTests?: (ctx: SimplePositiveUnaryTestsContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.simplePositiveUnaryTests`.
   * @param ctx the parse tree
   */
  exitSimplePositiveUnaryTests?: (ctx: SimplePositiveUnaryTestsContext) => void;
  /**
   * Enter a parse tree produced by the `positiveSimplePositiveUnaryTests`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   */
  enterPositiveSimplePositiveUnaryTests?: (ctx: PositiveSimplePositiveUnaryTestsContext) => void;
  /**
   * Exit a parse tree produced by the `positiveSimplePositiveUnaryTests`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   */
  exitPositiveSimplePositiveUnaryTests?: (ctx: PositiveSimplePositiveUnaryTestsContext) => void;
  /**
   * Enter a parse tree produced by the `negatedSimplePositiveUnaryTests`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   */
  enterNegatedSimplePositiveUnaryTests?: (ctx: NegatedSimplePositiveUnaryTestsContext) => void;
  /**
   * Exit a parse tree produced by the `negatedSimplePositiveUnaryTests`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   */
  exitNegatedSimplePositiveUnaryTests?: (ctx: NegatedSimplePositiveUnaryTestsContext) => void;
  /**
   * Enter a parse tree produced by the `positiveUnaryTestDash`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   */
  enterPositiveUnaryTestDash?: (ctx: PositiveUnaryTestDashContext) => void;
  /**
   * Exit a parse tree produced by the `positiveUnaryTestDash`
   * labeled alternative in `FEEL_1_1Parser.simpleUnaryTests`.
   * @param ctx the parse tree
   */
  exitPositiveUnaryTestDash?: (ctx: PositiveUnaryTestDashContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.positiveUnaryTest`.
   * @param ctx the parse tree
   */
  enterPositiveUnaryTest?: (ctx: PositiveUnaryTestContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.positiveUnaryTest`.
   * @param ctx the parse tree
   */
  exitPositiveUnaryTest?: (ctx: PositiveUnaryTestContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.positiveUnaryTests`.
   * @param ctx the parse tree
   */
  enterPositiveUnaryTests?: (ctx: PositiveUnaryTestsContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.positiveUnaryTests`.
   * @param ctx the parse tree
   */
  exitPositiveUnaryTests?: (ctx: PositiveUnaryTestsContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.unaryTestsRoot`.
   * @param ctx the parse tree
   */
  enterUnaryTestsRoot?: (ctx: UnaryTestsRootContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.unaryTestsRoot`.
   * @param ctx the parse tree
   */
  exitUnaryTestsRoot?: (ctx: UnaryTestsRootContext) => void;
  /**
   * Enter a parse tree produced by the `unaryTests_negated`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   */
  enterUnaryTests_negated?: (ctx: UnaryTests_negatedContext) => void;
  /**
   * Exit a parse tree produced by the `unaryTests_negated`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   */
  exitUnaryTests_negated?: (ctx: UnaryTests_negatedContext) => void;
  /**
   * Enter a parse tree produced by the `unaryTests_positive`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   */
  enterUnaryTests_positive?: (ctx: UnaryTests_positiveContext) => void;
  /**
   * Exit a parse tree produced by the `unaryTests_positive`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   */
  exitUnaryTests_positive?: (ctx: UnaryTests_positiveContext) => void;
  /**
   * Enter a parse tree produced by the `unaryTests_empty`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   */
  enterUnaryTests_empty?: (ctx: UnaryTests_emptyContext) => void;
  /**
   * Exit a parse tree produced by the `unaryTests_empty`
   * labeled alternative in `FEEL_1_1Parser.unaryTests`.
   * @param ctx the parse tree
   */
  exitUnaryTests_empty?: (ctx: UnaryTests_emptyContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.endpoint`.
   * @param ctx the parse tree
   */
  enterEndpoint?: (ctx: EndpointContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.endpoint`.
   * @param ctx the parse tree
   */
  exitEndpoint?: (ctx: EndpointContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.interval`.
   * @param ctx the parse tree
   */
  enterInterval?: (ctx: IntervalContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.interval`.
   * @param ctx the parse tree
   */
  exitInterval?: (ctx: IntervalContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.qualifiedName`.
   * @param ctx the parse tree
   */
  enterQualifiedName?: (ctx: QualifiedNameContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.qualifiedName`.
   * @param ctx the parse tree
   */
  exitQualifiedName?: (ctx: QualifiedNameContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.nameRef`.
   * @param ctx the parse tree
   */
  enterNameRef?: (ctx: NameRefContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.nameRef`.
   * @param ctx the parse tree
   */
  exitNameRef?: (ctx: NameRefContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.nameRefOtherToken`.
   * @param ctx the parse tree
   */
  enterNameRefOtherToken?: (ctx: NameRefOtherTokenContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.nameRefOtherToken`.
   * @param ctx the parse tree
   */
  exitNameRefOtherToken?: (ctx: NameRefOtherTokenContext) => void;
  /**
   * Enter a parse tree produced by `FEEL_1_1Parser.reusableKeywords`.
   * @param ctx the parse tree
   */
  enterReusableKeywords?: (ctx: ReusableKeywordsContext) => void;
  /**
   * Exit a parse tree produced by `FEEL_1_1Parser.reusableKeywords`.
   * @param ctx the parse tree
   */
  exitReusableKeywords?: (ctx: ReusableKeywordsContext) => void;
}
