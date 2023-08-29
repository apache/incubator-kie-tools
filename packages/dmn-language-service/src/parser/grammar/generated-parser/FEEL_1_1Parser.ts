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
// noinspection ES6UnusedImports,JSUnusedGlobalSymbols,JSUnusedLocalSymbols

import {
  ATN,
  ATNDeserializer,
  DecisionState,
  DFA,
  FailedPredicateException,
  RecognitionException,
  NoViableAltException,
  BailErrorStrategy,
  Parser,
  ParserATNSimulator,
  RuleContext,
  ParserRuleContext,
  PredictionMode,
  PredictionContextCache,
  TerminalNode,
  RuleNode,
  Token,
  TokenStream,
  Interval,
  IntervalSet,
} from "antlr4";
import FEEL_1_1Listener from "./FEEL_1_1Listener";
// for running tests with parameters, TODO: discuss strategy for typed parameters in CI
// eslint-disable-next-line no-unused-vars
type int = number;

import { ParserHelper } from "../ParserHelper";

export default class FEEL_1_1Parser extends Parser {
  public static readonly BooleanLiteral = 1;
  public static readonly FOR = 2;
  public static readonly RETURN = 3;
  public static readonly IN = 4;
  public static readonly IF = 5;
  public static readonly THEN = 6;
  public static readonly ELSE = 7;
  public static readonly SOME = 8;
  public static readonly EVERY = 9;
  public static readonly SATISFIES = 10;
  public static readonly INSTANCE = 11;
  public static readonly OF = 12;
  public static readonly FUNCTION = 13;
  public static readonly EXTERNAL = 14;
  public static readonly OR = 15;
  public static readonly AND = 16;
  public static readonly BETWEEN = 17;
  public static readonly NULL = 18;
  public static readonly TRUE = 19;
  public static readonly FALSE = 20;
  public static readonly QUOTE = 21;
  public static readonly IntegerLiteral = 22;
  public static readonly FloatingPointLiteral = 23;
  public static readonly StringLiteral = 24;
  public static readonly LPAREN = 25;
  public static readonly RPAREN = 26;
  public static readonly LBRACE = 27;
  public static readonly RBRACE = 28;
  public static readonly LBRACK = 29;
  public static readonly RBRACK = 30;
  public static readonly COMMA = 31;
  public static readonly ELIPSIS = 32;
  public static readonly DOT = 33;
  public static readonly EQUAL = 34;
  public static readonly GT = 35;
  public static readonly LT = 36;
  public static readonly LE = 37;
  public static readonly GE = 38;
  public static readonly NOTEQUAL = 39;
  public static readonly COLON = 40;
  public static readonly RARROW = 41;
  public static readonly POW = 42;
  public static readonly ADD = 43;
  public static readonly SUB = 44;
  public static readonly MUL = 45;
  public static readonly DIV = 46;
  public static readonly BANG = 47;
  public static readonly NOT = 48;
  public static readonly AT = 49;
  public static readonly Identifier = 50;
  public static readonly WS = 51;
  public static readonly COMMENT = 52;
  public static readonly LINE_COMMENT = 53;
  public static readonly ANY_OTHER_CHAR = 54;
  public static readonly EOF = Token.EOF;
  public static readonly RULE_compilation_unit = 0;
  public static readonly RULE_expression = 1;
  public static readonly RULE_textualExpression = 2;
  public static readonly RULE_parameters = 3;
  public static readonly RULE_namedParameters = 4;
  public static readonly RULE_namedParameter = 5;
  public static readonly RULE_positionalParameters = 6;
  public static readonly RULE_forExpression = 7;
  public static readonly RULE_iterationContexts = 8;
  public static readonly RULE_iterationContext = 9;
  public static readonly RULE_ifExpression = 10;
  public static readonly RULE_quantifiedExpression = 11;
  public static readonly RULE_type = 12;
  public static readonly RULE_list = 13;
  public static readonly RULE_functionDefinition = 14;
  public static readonly RULE_formalParameters = 15;
  public static readonly RULE_formalParameter = 16;
  public static readonly RULE_context = 17;
  public static readonly RULE_contextEntries = 18;
  public static readonly RULE_contextEntry = 19;
  public static readonly RULE_key = 20;
  public static readonly RULE_nameDefinition = 21;
  public static readonly RULE_nameDefinitionWithEOF = 22;
  public static readonly RULE_nameDefinitionTokens = 23;
  public static readonly RULE_iterationNameDefinition = 24;
  public static readonly RULE_iterationNameDefinitionTokens = 25;
  public static readonly RULE_additionalNameSymbol = 26;
  public static readonly RULE_conditionalOrExpression = 27;
  public static readonly RULE_conditionalAndExpression = 28;
  public static readonly RULE_comparisonExpression = 29;
  public static readonly RULE_relationalExpression = 30;
  public static readonly RULE_expressionList = 31;
  public static readonly RULE_additiveExpression = 32;
  public static readonly RULE_multiplicativeExpression = 33;
  public static readonly RULE_powerExpression = 34;
  public static readonly RULE_filterPathExpression = 35;
  public static readonly RULE_unaryExpression = 36;
  public static readonly RULE_unaryExpressionNotPlusMinus = 37;
  public static readonly RULE_primary = 38;
  public static readonly RULE_literal = 39;
  public static readonly RULE_atLiteral = 40;
  public static readonly RULE_atLiteralValue = 41;
  public static readonly RULE_simplePositiveUnaryTest = 42;
  public static readonly RULE_simplePositiveUnaryTests = 43;
  public static readonly RULE_simpleUnaryTests = 44;
  public static readonly RULE_positiveUnaryTest = 45;
  public static readonly RULE_positiveUnaryTests = 46;
  public static readonly RULE_unaryTestsRoot = 47;
  public static readonly RULE_unaryTests = 48;
  public static readonly RULE_endpoint = 49;
  public static readonly RULE_interval = 50;
  public static readonly RULE_qualifiedName = 51;
  public static readonly RULE_nameRef = 52;
  public static readonly RULE_nameRefOtherToken = 53;
  public static readonly RULE_reusableKeywords = 54;
  public static readonly literalNames: (string | null)[] = [
    null,
    null,
    "'for'",
    "'return'",
    "'in'",
    "'if'",
    "'then'",
    "'else'",
    "'some'",
    "'every'",
    "'satisfies'",
    "'instance'",
    "'of'",
    "'function'",
    "'external'",
    "'or'",
    "'and'",
    "'between'",
    "'null'",
    "'true'",
    "'false'",
    "'''",
    null,
    null,
    null,
    "'('",
    "')'",
    "'{'",
    "'}'",
    "'['",
    "']'",
    "','",
    "'..'",
    "'.'",
    "'='",
    "'>'",
    "'<'",
    "'<='",
    "'>='",
    "'!='",
    "':'",
    "'->'",
    "'**'",
    "'+'",
    "'-'",
    "'*'",
    "'/'",
    "'!'",
    "'not'",
    "'@'",
  ];
  public static readonly symbolicNames: (string | null)[] = [
    null,
    "BooleanLiteral",
    "FOR",
    "RETURN",
    "IN",
    "IF",
    "THEN",
    "ELSE",
    "SOME",
    "EVERY",
    "SATISFIES",
    "INSTANCE",
    "OF",
    "FUNCTION",
    "EXTERNAL",
    "OR",
    "AND",
    "BETWEEN",
    "NULL",
    "TRUE",
    "FALSE",
    "QUOTE",
    "IntegerLiteral",
    "FloatingPointLiteral",
    "StringLiteral",
    "LPAREN",
    "RPAREN",
    "LBRACE",
    "RBRACE",
    "LBRACK",
    "RBRACK",
    "COMMA",
    "ELIPSIS",
    "DOT",
    "EQUAL",
    "GT",
    "LT",
    "LE",
    "GE",
    "NOTEQUAL",
    "COLON",
    "RARROW",
    "POW",
    "ADD",
    "SUB",
    "MUL",
    "DIV",
    "BANG",
    "NOT",
    "AT",
    "Identifier",
    "WS",
    "COMMENT",
    "LINE_COMMENT",
    "ANY_OTHER_CHAR",
  ];
  // tslint:disable:no-trailing-whitespace
  public static readonly ruleNames: string[] = [
    "compilation_unit",
    "expression",
    "textualExpression",
    "parameters",
    "namedParameters",
    "namedParameter",
    "positionalParameters",
    "forExpression",
    "iterationContexts",
    "iterationContext",
    "ifExpression",
    "quantifiedExpression",
    "type",
    "list",
    "functionDefinition",
    "formalParameters",
    "formalParameter",
    "context",
    "contextEntries",
    "contextEntry",
    "key",
    "nameDefinition",
    "nameDefinitionWithEOF",
    "nameDefinitionTokens",
    "iterationNameDefinition",
    "iterationNameDefinitionTokens",
    "additionalNameSymbol",
    "conditionalOrExpression",
    "conditionalAndExpression",
    "comparisonExpression",
    "relationalExpression",
    "expressionList",
    "additiveExpression",
    "multiplicativeExpression",
    "powerExpression",
    "filterPathExpression",
    "unaryExpression",
    "unaryExpressionNotPlusMinus",
    "primary",
    "literal",
    "atLiteral",
    "atLiteralValue",
    "simplePositiveUnaryTest",
    "simplePositiveUnaryTests",
    "simpleUnaryTests",
    "positiveUnaryTest",
    "positiveUnaryTests",
    "unaryTestsRoot",
    "unaryTests",
    "endpoint",
    "interval",
    "qualifiedName",
    "nameRef",
    "nameRefOtherToken",
    "reusableKeywords",
  ];
  public get grammarFileName(): string {
    return "FEEL_1_1.g4";
  }
  public get literalNames(): (string | null)[] {
    return FEEL_1_1Parser.literalNames;
  }
  public get symbolicNames(): (string | null)[] {
    return FEEL_1_1Parser.symbolicNames;
  }
  public get ruleNames(): string[] {
    return FEEL_1_1Parser.ruleNames;
  }
  public get serializedATN(): number[] {
    return FEEL_1_1Parser._serializedATN;
  }

  protected createFailedPredicateException(predicate?: string, message?: string): FailedPredicateException {
    return new FailedPredicateException(this, predicate, message);
  }

  public readonly helper = new ParserHelper();

  constructor(input: TokenStream) {
    super(input);
    this._interp = new ParserATNSimulator(
      this,
      FEEL_1_1Parser._ATN,
      FEEL_1_1Parser.DecisionsToDFA,
      new PredictionContextCache()
    );
  }
  // @RuleVersion(0)
  public compilation_unit(): Compilation_unitContext {
    const localctx: Compilation_unitContext = new Compilation_unitContext(this, this._ctx, this.state);
    this.enterRule(localctx, 0, FEEL_1_1Parser.RULE_compilation_unit);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 110;
        this.expression();
        this.state = 111;
        this.match(FEEL_1_1Parser.EOF);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public expression(): ExpressionContext {
    let localctx: ExpressionContext = new ExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 2, FEEL_1_1Parser.RULE_expression);
    try {
      localctx = new ExpressionTextualContext(this, localctx);
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 113;
        (localctx as ExpressionTextualContext)._expr = this.textualExpression();
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public textualExpression(): TextualExpressionContext {
    const localctx: TextualExpressionContext = new TextualExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 4, FEEL_1_1Parser.RULE_textualExpression);
    try {
      this.state = 117;
      this._errHandler.sync(this);
      switch (this._input.LA(1)) {
        case 13:
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 115;
            this.functionDefinition();
          }
          break;
        case 1:
        case 2:
        case 5:
        case 8:
        case 9:
        case 18:
        case 22:
        case 23:
        case 24:
        case 25:
        case 27:
        case 29:
        case 30:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 43:
        case 44:
        case 48:
        case 49:
        case 50:
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 116;
            this.conditionalOrExpression(0);
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public parameters(): ParametersContext {
    let localctx: ParametersContext = new ParametersContext(this, this._ctx, this.state);
    this.enterRule(localctx, 6, FEEL_1_1Parser.RULE_parameters);
    try {
      this.state = 129;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 1, this._ctx)) {
        case 1:
          localctx = new ParametersEmptyContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 119;
            this.match(FEEL_1_1Parser.LPAREN);
            this.state = 120;
            this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 2:
          localctx = new ParametersNamedContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 121;
            this.match(FEEL_1_1Parser.LPAREN);
            this.state = 122;
            this.namedParameters();
            this.state = 123;
            this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 3:
          localctx = new ParametersPositionalContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 125;
            this.match(FEEL_1_1Parser.LPAREN);
            this.state = 126;
            this.positionalParameters();
            this.state = 127;
            this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public namedParameters(): NamedParametersContext {
    const localctx: NamedParametersContext = new NamedParametersContext(this, this._ctx, this.state);
    this.enterRule(localctx, 8, FEEL_1_1Parser.RULE_namedParameters);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 131;
        this.namedParameter();
        this.state = 136;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 132;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 133;
              this.namedParameter();
            }
          }
          this.state = 138;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public namedParameter(): NamedParameterContext {
    const localctx: NamedParameterContext = new NamedParameterContext(this, this._ctx, this.state);
    this.enterRule(localctx, 10, FEEL_1_1Parser.RULE_namedParameter);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 139;
        localctx._name = this.nameDefinition();
        this.state = 140;
        this.match(FEEL_1_1Parser.COLON);
        this.state = 141;
        localctx._value = this.expression();
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public positionalParameters(): PositionalParametersContext {
    const localctx: PositionalParametersContext = new PositionalParametersContext(this, this._ctx, this.state);
    this.enterRule(localctx, 12, FEEL_1_1Parser.RULE_positionalParameters);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 143;
        this.expression();
        this.state = 148;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 144;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 145;
              this.expression();
            }
          }
          this.state = 150;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public forExpression(): ForExpressionContext {
    const localctx: ForExpressionContext = new ForExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 14, FEEL_1_1Parser.RULE_forExpression);

    this.helper.pushScope();

    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 151;
        this.match(FEEL_1_1Parser.FOR);
        this.state = 152;
        this.iterationContexts();
        this.state = 153;
        this.match(FEEL_1_1Parser.RETURN);
        this.helper.enableDynamicResolution();
        this.state = 155;
        this.expression();
        this.helper.disableDynamicResolution();
      }
      this._ctx.stop = this._input.LT(-1);

      this.helper.popScope();
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public iterationContexts(): IterationContextsContext {
    const localctx: IterationContextsContext = new IterationContextsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 16, FEEL_1_1Parser.RULE_iterationContexts);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 158;
        this.iterationContext();
        this.state = 163;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 159;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 160;
              this.iterationContext();
            }
          }
          this.state = 165;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public iterationContext(): IterationContextContext {
    const localctx: IterationContextContext = new IterationContextContext(this, this._ctx, this.state);
    this.enterRule(localctx, 18, FEEL_1_1Parser.RULE_iterationContext);
    try {
      this.state = 177;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 5, this._ctx)) {
        case 1:
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 166;
            if (!this.helper.isFeatDMN12EnhancedForLoopEnabled()) {
              throw this.createFailedPredicateException("this.helper.isFeatDMN12EnhancedForLoopEnabled()");
            }
            this.state = 167;
            this.iterationNameDefinition();
            this.state = 168;
            this.match(FEEL_1_1Parser.IN);
            this.state = 169;
            this.expression();
            this.state = 170;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 171;
            this.expression();
          }
          break;
        case 2:
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 173;
            this.iterationNameDefinition();
            this.state = 174;
            this.match(FEEL_1_1Parser.IN);
            this.state = 175;
            this.expression();
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public ifExpression(): IfExpressionContext {
    const localctx: IfExpressionContext = new IfExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 20, FEEL_1_1Parser.RULE_ifExpression);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 179;
        this.match(FEEL_1_1Parser.IF);
        this.state = 180;
        localctx._c = this.expression();
        this.state = 181;
        this.match(FEEL_1_1Parser.THEN);
        this.state = 182;
        localctx._t = this.expression();
        this.state = 183;
        this.match(FEEL_1_1Parser.ELSE);
        this.state = 184;
        localctx._e = this.expression();
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public quantifiedExpression(): QuantifiedExpressionContext {
    let localctx: QuantifiedExpressionContext = new QuantifiedExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 22, FEEL_1_1Parser.RULE_quantifiedExpression);

    this.helper.pushScope();

    try {
      this.state = 200;
      this._errHandler.sync(this);
      switch (this._input.LA(1)) {
        case 8:
          localctx = new QuantExprSomeContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 186;
            this.match(FEEL_1_1Parser.SOME);
            this.state = 187;
            this.iterationContexts();
            this.state = 188;
            this.match(FEEL_1_1Parser.SATISFIES);
            this.helper.enableDynamicResolution();
            this.state = 190;
            this.expression();
            this.helper.disableDynamicResolution();
          }
          break;
        case 9:
          localctx = new QuantExprEveryContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 193;
            this.match(FEEL_1_1Parser.EVERY);
            this.state = 194;
            this.iterationContexts();
            this.state = 195;
            this.match(FEEL_1_1Parser.SATISFIES);
            this.helper.enableDynamicResolution();
            this.state = 197;
            this.expression();
            this.helper.disableDynamicResolution();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
      this._ctx.stop = this._input.LT(-1);

      this.helper.popScope();
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public type_(): TypeContext {
    let localctx: TypeContext = new TypeContext(this, this._ctx, this.state);
    this.enterRule(localctx, 24, FEEL_1_1Parser.RULE_type);

    this.helper.pushScope();

    let _la: number;
    try {
      this.state = 242;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 10, this._ctx)) {
        case 1:
          localctx = new ListTypeContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 202;
            if (!(this._input.LT(1).text === "list")) {
              throw this.createFailedPredicateException('this._input.LT(1).text === "list"');
            }
            this.state = 203;
            (localctx as ListTypeContext)._sk = this.match(FEEL_1_1Parser.Identifier);
            this.state = 204;
            this.match(FEEL_1_1Parser.LT);
            this.state = 205;
            this.type_();
            this.state = 206;
            this.match(FEEL_1_1Parser.GT);
          }
          break;
        case 2:
          localctx = new ContextTypeContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 208;
            if (!(this._input.LT(1).text === "context")) {
              throw this.createFailedPredicateException('this._input.LT(1).text === "context"');
            }
            this.state = 209;
            (localctx as ContextTypeContext)._sk = this.match(FEEL_1_1Parser.Identifier);
            this.state = 210;
            this.match(FEEL_1_1Parser.LT);
            this.state = 211;
            this.match(FEEL_1_1Parser.Identifier);
            this.state = 212;
            this.match(FEEL_1_1Parser.COLON);
            this.state = 213;
            this.type_();
            this.state = 220;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            while (_la === 31) {
              {
                {
                  this.state = 214;
                  this.match(FEEL_1_1Parser.COMMA);
                  this.state = 215;
                  this.match(FEEL_1_1Parser.Identifier);
                  this.state = 216;
                  this.match(FEEL_1_1Parser.COLON);
                  this.state = 217;
                  this.type_();
                }
              }
              this.state = 222;
              this._errHandler.sync(this);
              _la = this._input.LA(1);
            }
            this.state = 223;
            this.match(FEEL_1_1Parser.GT);
          }
          break;
        case 3:
          localctx = new QnTypeContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 225;
            this.match(FEEL_1_1Parser.FUNCTION);
          }
          break;
        case 4:
          localctx = new FunctionTypeContext(this, localctx);
          this.enterOuterAlt(localctx, 4);
          {
            this.state = 226;
            this.match(FEEL_1_1Parser.FUNCTION);
            this.state = 227;
            this.match(FEEL_1_1Parser.LT);
            this.state = 236;
            this._errHandler.sync(this);
            switch (this._interp.adaptivePredict(this._input, 9, this._ctx)) {
              case 1:
                {
                  this.state = 228;
                  this.type_();
                  this.state = 233;
                  this._errHandler.sync(this);
                  _la = this._input.LA(1);
                  while (_la === 31) {
                    {
                      {
                        this.state = 229;
                        this.match(FEEL_1_1Parser.COMMA);
                        this.state = 230;
                        this.type_();
                      }
                    }
                    this.state = 235;
                    this._errHandler.sync(this);
                    _la = this._input.LA(1);
                  }
                }
                break;
            }
            this.state = 238;
            this.match(FEEL_1_1Parser.GT);
            this.state = 239;
            this.match(FEEL_1_1Parser.RARROW);
            this.state = 240;
            this.type_();
          }
          break;
        case 5:
          localctx = new QnTypeContext(this, localctx);
          this.enterOuterAlt(localctx, 5);
          {
            this.state = 241;
            this.qualifiedName();
          }
          break;
      }
      this._ctx.stop = this._input.LT(-1);

      this.helper.popScope();
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public list(): ListContext {
    const localctx: ListContext = new ListContext(this, this._ctx, this.state);
    this.enterRule(localctx, 26, FEEL_1_1Parser.RULE_list);
    try {
      this.state = 250;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 11, this._ctx)) {
        case 1:
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 244;
            this.match(FEEL_1_1Parser.LBRACK);
            this.state = 245;
            this.match(FEEL_1_1Parser.RBRACK);
          }
          break;
        case 2:
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 246;
            this.match(FEEL_1_1Parser.LBRACK);
            this.state = 247;
            this.expressionList();
            this.state = 248;
            this.match(FEEL_1_1Parser.RBRACK);
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public functionDefinition(): FunctionDefinitionContext {
    const localctx: FunctionDefinitionContext = new FunctionDefinitionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 28, FEEL_1_1Parser.RULE_functionDefinition);

    this.helper.pushScope();

    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 252;
        this.match(FEEL_1_1Parser.FUNCTION);
        this.state = 253;
        this.match(FEEL_1_1Parser.LPAREN);
        this.state = 255;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if (_la === 50) {
          {
            this.state = 254;
            this.formalParameters();
          }
        }

        this.state = 257;
        this.match(FEEL_1_1Parser.RPAREN);
        this.state = 259;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if (_la === 14) {
          {
            this.state = 258;
            localctx._external = this.match(FEEL_1_1Parser.EXTERNAL);
          }
        }

        this.helper.enableDynamicResolution();
        this.state = 262;
        localctx._body = this.expression();
        this.helper.disableDynamicResolution();
      }
      this._ctx.stop = this._input.LT(-1);

      this.helper.popScope();
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public formalParameters(): FormalParametersContext {
    const localctx: FormalParametersContext = new FormalParametersContext(this, this._ctx, this.state);
    this.enterRule(localctx, 30, FEEL_1_1Parser.RULE_formalParameters);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 265;
        this.formalParameter();
        this.state = 270;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 266;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 267;
              this.formalParameter();
            }
          }
          this.state = 272;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public formalParameter(): FormalParameterContext {
    const localctx: FormalParameterContext = new FormalParameterContext(this, this._ctx, this.state);
    this.enterRule(localctx, 32, FEEL_1_1Parser.RULE_formalParameter);
    try {
      this.state = 278;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 15, this._ctx)) {
        case 1:
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 273;
            this.nameDefinition();
            this.state = 274;
            this.match(FEEL_1_1Parser.COLON);
            this.state = 275;
            this.type_();
          }
          break;
        case 2:
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 277;
            this.nameDefinition();
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public context(): ContextContext {
    const localctx: ContextContext = new ContextContext(this, this._ctx, this.state);
    this.enterRule(localctx, 34, FEEL_1_1Parser.RULE_context);

    this.helper.pushScope();

    try {
      this.state = 286;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 16, this._ctx)) {
        case 1:
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 280;
            this.match(FEEL_1_1Parser.LBRACE);
            this.state = 281;
            this.match(FEEL_1_1Parser.RBRACE);
          }
          break;
        case 2:
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 282;
            this.match(FEEL_1_1Parser.LBRACE);
            this.state = 283;
            this.contextEntries();
            this.state = 284;
            this.match(FEEL_1_1Parser.RBRACE);
          }
          break;
      }
      this._ctx.stop = this._input.LT(-1);

      this.helper.popScope();
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public contextEntries(): ContextEntriesContext {
    const localctx: ContextEntriesContext = new ContextEntriesContext(this, this._ctx, this.state);
    this.enterRule(localctx, 36, FEEL_1_1Parser.RULE_contextEntries);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 288;
        this.contextEntry();
        this.state = 293;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 289;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 290;
              this.contextEntry();
            }
          }
          this.state = 295;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public contextEntry(): ContextEntryContext {
    const localctx: ContextEntryContext = new ContextEntryContext(this, this._ctx, this.state);
    this.enterRule(localctx, 38, FEEL_1_1Parser.RULE_contextEntry);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 296;
        localctx._key = this.key();
        this.helper.pushName(localctx._key);
        this.state = 298;
        this.match(FEEL_1_1Parser.COLON);
        this.state = 299;
        this.expression();
        this.helper.popName();
        this.helper.defineVariable(localctx._key);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public key(): KeyContext {
    let localctx: KeyContext = new KeyContext(this, this._ctx, this.state);
    this.enterRule(localctx, 40, FEEL_1_1Parser.RULE_key);
    try {
      this.state = 304;
      this._errHandler.sync(this);
      switch (this._input.LA(1)) {
        case 50:
          localctx = new KeyNameContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 302;
            this.nameDefinition();
          }
          break;
        case 24:
          localctx = new KeyStringContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 303;
            this.match(FEEL_1_1Parser.StringLiteral);
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public nameDefinition(): NameDefinitionContext {
    const localctx: NameDefinitionContext = new NameDefinitionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 42, FEEL_1_1Parser.RULE_nameDefinition);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 306;
        localctx._nameDefinitionTokens = this.nameDefinitionTokens();
        this.helper.defineVariable(localctx._nameDefinitionTokens);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public nameDefinitionWithEOF(): NameDefinitionWithEOFContext {
    const localctx: NameDefinitionWithEOFContext = new NameDefinitionWithEOFContext(this, this._ctx, this.state);
    this.enterRule(localctx, 44, FEEL_1_1Parser.RULE_nameDefinitionWithEOF);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 309;
        this.nameDefinition();
        this.state = 310;
        this.match(FEEL_1_1Parser.EOF);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public nameDefinitionTokens(): NameDefinitionTokensContext {
    const localctx: NameDefinitionTokensContext = new NameDefinitionTokensContext(this, this._ctx, this.state);
    this.enterRule(localctx, 46, FEEL_1_1Parser.RULE_nameDefinitionTokens);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 312;
        this.match(FEEL_1_1Parser.Identifier);
        this.state = 321;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (
          ((_la & ~0x1f) === 0 && ((1 << _la) & 16777212) !== 0) ||
          (((_la - 33) & ~0x1f) === 0 && ((1 << (_la - 33)) & 179201) !== 0)
        ) {
          {
            this.state = 319;
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
              case 50:
                {
                  this.state = 313;
                  this.match(FEEL_1_1Parser.Identifier);
                }
                break;
              case 21:
              case 33:
              case 43:
              case 44:
              case 45:
              case 46:
                {
                  this.state = 314;
                  this.additionalNameSymbol();
                }
                break;
              case 22:
                {
                  this.state = 315;
                  this.match(FEEL_1_1Parser.IntegerLiteral);
                }
                break;
              case 23:
                {
                  this.state = 316;
                  this.match(FEEL_1_1Parser.FloatingPointLiteral);
                }
                break;
              case 2:
              case 3:
              case 5:
              case 6:
              case 7:
              case 8:
              case 9:
              case 10:
              case 11:
              case 12:
              case 13:
              case 14:
              case 15:
              case 16:
              case 17:
              case 18:
              case 19:
              case 20:
              case 48:
                {
                  this.state = 317;
                  this.reusableKeywords();
                }
                break;
              case 4:
                {
                  this.state = 318;
                  this.match(FEEL_1_1Parser.IN);
                }
                break;
              default:
                throw new NoViableAltException(this);
            }
          }
          this.state = 323;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public iterationNameDefinition(): IterationNameDefinitionContext {
    const localctx: IterationNameDefinitionContext = new IterationNameDefinitionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 48, FEEL_1_1Parser.RULE_iterationNameDefinition);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 324;
        localctx._iterationNameDefinitionTokens = this.iterationNameDefinitionTokens();
        this.helper.defineVariable(localctx._iterationNameDefinitionTokens);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public iterationNameDefinitionTokens(): IterationNameDefinitionTokensContext {
    const localctx: IterationNameDefinitionTokensContext = new IterationNameDefinitionTokensContext(
      this,
      this._ctx,
      this.state
    );
    this.enterRule(localctx, 50, FEEL_1_1Parser.RULE_iterationNameDefinitionTokens);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 327;
        this.match(FEEL_1_1Parser.Identifier);
        this.state = 335;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (
          ((_la & ~0x1f) === 0 && ((1 << _la) & 16777196) !== 0) ||
          (((_la - 33) & ~0x1f) === 0 && ((1 << (_la - 33)) & 179201) !== 0)
        ) {
          {
            this.state = 333;
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
              case 50:
                {
                  this.state = 328;
                  this.match(FEEL_1_1Parser.Identifier);
                }
                break;
              case 21:
              case 33:
              case 43:
              case 44:
              case 45:
              case 46:
                {
                  this.state = 329;
                  this.additionalNameSymbol();
                }
                break;
              case 22:
                {
                  this.state = 330;
                  this.match(FEEL_1_1Parser.IntegerLiteral);
                }
                break;
              case 23:
                {
                  this.state = 331;
                  this.match(FEEL_1_1Parser.FloatingPointLiteral);
                }
                break;
              case 2:
              case 3:
              case 5:
              case 6:
              case 7:
              case 8:
              case 9:
              case 10:
              case 11:
              case 12:
              case 13:
              case 14:
              case 15:
              case 16:
              case 17:
              case 18:
              case 19:
              case 20:
              case 48:
                {
                  this.state = 332;
                  this.reusableKeywords();
                }
                break;
              default:
                throw new NoViableAltException(this);
            }
          }
          this.state = 337;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public additionalNameSymbol(): AdditionalNameSymbolContext {
    const localctx: AdditionalNameSymbolContext = new AdditionalNameSymbolContext(this, this._ctx, this.state);
    this.enterRule(localctx, 52, FEEL_1_1Parser.RULE_additionalNameSymbol);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 338;
        _la = this._input.LA(1);
        if (!(((_la - 21) & ~0x1f) === 0 && ((1 << (_la - 21)) & 62918657) !== 0)) {
          this._errHandler.recoverInline(this);
        } else {
          this._errHandler.reportMatch(this);
          this.consume();
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }

  public conditionalOrExpression(): ConditionalOrExpressionContext;
  public conditionalOrExpression(_p: number): ConditionalOrExpressionContext;
  // @RuleVersion(0)
  public conditionalOrExpression(_p?: number): ConditionalOrExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: ConditionalOrExpressionContext = new ConditionalOrExpressionContext(this, this._ctx, _parentState);
    let _prevctx: ConditionalOrExpressionContext = localctx;
    const _startState: number = 54;
    this.enterRecursionRule(localctx, 54, FEEL_1_1Parser.RULE_conditionalOrExpression, _p);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new CondOrAndContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 341;
          this.conditionalAndExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 348;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 23, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              {
                localctx = new CondOrContext(this, new ConditionalOrExpressionContext(this, _parentctx, _parentState));
                (localctx as CondOrContext)._left = _prevctx;
                this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_conditionalOrExpression);
                this.state = 343;
                if (!this.precpred(this._ctx, 1)) {
                  throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                }
                this.state = 344;
                (localctx as CondOrContext)._op = this.match(FEEL_1_1Parser.OR);
                this.state = 345;
                (localctx as CondOrContext)._right = this.conditionalAndExpression(0);
              }
            }
          }
          this.state = 350;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 23, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public conditionalAndExpression(): ConditionalAndExpressionContext;
  public conditionalAndExpression(_p: number): ConditionalAndExpressionContext;
  // @RuleVersion(0)
  public conditionalAndExpression(_p?: number): ConditionalAndExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: ConditionalAndExpressionContext = new ConditionalAndExpressionContext(this, this._ctx, _parentState);
    let _prevctx: ConditionalAndExpressionContext = localctx;
    const _startState: number = 56;
    this.enterRecursionRule(localctx, 56, FEEL_1_1Parser.RULE_conditionalAndExpression, _p);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new CondAndCompContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 352;
          this.comparisonExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 359;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 24, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              {
                localctx = new CondAndContext(
                  this,
                  new ConditionalAndExpressionContext(this, _parentctx, _parentState)
                );
                (localctx as CondAndContext)._left = _prevctx;
                this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_conditionalAndExpression);
                this.state = 354;
                if (!this.precpred(this._ctx, 1)) {
                  throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                }
                this.state = 355;
                (localctx as CondAndContext)._op = this.match(FEEL_1_1Parser.AND);
                this.state = 356;
                (localctx as CondAndContext)._right = this.comparisonExpression(0);
              }
            }
          }
          this.state = 361;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 24, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public comparisonExpression(): ComparisonExpressionContext;
  public comparisonExpression(_p: number): ComparisonExpressionContext;
  // @RuleVersion(0)
  public comparisonExpression(_p?: number): ComparisonExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: ComparisonExpressionContext = new ComparisonExpressionContext(this, this._ctx, _parentState);
    let _prevctx: ComparisonExpressionContext = localctx;
    const _startState: number = 58;
    this.enterRecursionRule(localctx, 58, FEEL_1_1Parser.RULE_comparisonExpression, _p);
    let _la: number;
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new CompExpressionRelContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 363;
          this.relationalExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 370;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 25, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              {
                localctx = new CompExpressionContext(
                  this,
                  new ComparisonExpressionContext(this, _parentctx, _parentState)
                );
                (localctx as CompExpressionContext)._left = _prevctx;
                this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_comparisonExpression);
                this.state = 365;
                if (!this.precpred(this._ctx, 1)) {
                  throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                }
                this.state = 366;
                (localctx as CompExpressionContext)._op = this._input.LT(1);
                _la = this._input.LA(1);
                if (!(((_la - 34) & ~0x1f) === 0 && ((1 << (_la - 34)) & 63) !== 0)) {
                  (localctx as CompExpressionContext)._op = this._errHandler.recoverInline(this);
                } else {
                  this._errHandler.reportMatch(this);
                  this.consume();
                }
                this.state = 367;
                (localctx as CompExpressionContext)._right = this.relationalExpression(0);
              }
            }
          }
          this.state = 372;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 25, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public relationalExpression(): RelationalExpressionContext;
  public relationalExpression(_p: number): RelationalExpressionContext;
  // @RuleVersion(0)
  public relationalExpression(_p?: number): RelationalExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: RelationalExpressionContext = new RelationalExpressionContext(this, this._ctx, _parentState);
    let _prevctx: RelationalExpressionContext = localctx;
    const _startState: number = 60;
    this.enterRecursionRule(localctx, 60, FEEL_1_1Parser.RULE_relationalExpression, _p);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new RelExpressionAddContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 374;
          this.additiveExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 397;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 27, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              this.state = 395;
              this._errHandler.sync(this);
              switch (this._interp.adaptivePredict(this._input, 26, this._ctx)) {
                case 1:
                  {
                    localctx = new RelExpressionBetweenContext(
                      this,
                      new RelationalExpressionContext(this, _parentctx, _parentState)
                    );
                    (localctx as RelExpressionBetweenContext)._val = _prevctx;
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_relationalExpression);
                    this.state = 376;
                    if (!this.precpred(this._ctx, 4)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 4)");
                    }
                    this.state = 377;
                    this.match(FEEL_1_1Parser.BETWEEN);
                    this.state = 378;
                    (localctx as RelExpressionBetweenContext)._start = this.additiveExpression(0);
                    this.state = 379;
                    this.match(FEEL_1_1Parser.AND);
                    this.state = 380;
                    (localctx as RelExpressionBetweenContext)._end = this.additiveExpression(0);
                  }
                  break;
                case 2:
                  {
                    localctx = new RelExpressionTestListContext(
                      this,
                      new RelationalExpressionContext(this, _parentctx, _parentState)
                    );
                    (localctx as RelExpressionTestListContext)._val = _prevctx;
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_relationalExpression);
                    this.state = 382;
                    if (!this.precpred(this._ctx, 3)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 3)");
                    }
                    this.state = 383;
                    this.match(FEEL_1_1Parser.IN);
                    this.state = 384;
                    this.match(FEEL_1_1Parser.LPAREN);
                    this.state = 385;
                    this.positiveUnaryTests();
                    this.state = 386;
                    this.match(FEEL_1_1Parser.RPAREN);
                  }
                  break;
                case 3:
                  {
                    localctx = new RelExpressionValueContext(
                      this,
                      new RelationalExpressionContext(this, _parentctx, _parentState)
                    );
                    (localctx as RelExpressionValueContext)._val = _prevctx;
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_relationalExpression);
                    this.state = 388;
                    if (!this.precpred(this._ctx, 2)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 2)");
                    }
                    this.state = 389;
                    this.match(FEEL_1_1Parser.IN);
                    this.state = 390;
                    this.expression();
                  }
                  break;
                case 4:
                  {
                    localctx = new RelExpressionInstanceOfContext(
                      this,
                      new RelationalExpressionContext(this, _parentctx, _parentState)
                    );
                    (localctx as RelExpressionInstanceOfContext)._val = _prevctx;
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_relationalExpression);
                    this.state = 391;
                    if (!this.precpred(this._ctx, 1)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                    }
                    this.state = 392;
                    this.match(FEEL_1_1Parser.INSTANCE);
                    this.state = 393;
                    this.match(FEEL_1_1Parser.OF);
                    this.state = 394;
                    this.type_();
                  }
                  break;
              }
            }
          }
          this.state = 399;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 27, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }
  // @RuleVersion(0)
  public expressionList(): ExpressionListContext {
    const localctx: ExpressionListContext = new ExpressionListContext(this, this._ctx, this.state);
    this.enterRule(localctx, 62, FEEL_1_1Parser.RULE_expressionList);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 400;
        this.expression();
        this.state = 405;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 401;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 402;
              this.expression();
            }
          }
          this.state = 407;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }

  public additiveExpression(): AdditiveExpressionContext;
  public additiveExpression(_p: number): AdditiveExpressionContext;
  // @RuleVersion(0)
  public additiveExpression(_p?: number): AdditiveExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: AdditiveExpressionContext = new AdditiveExpressionContext(this, this._ctx, _parentState);
    let _prevctx: AdditiveExpressionContext = localctx;
    const _startState: number = 64;
    this.enterRecursionRule(localctx, 64, FEEL_1_1Parser.RULE_additiveExpression, _p);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new AddExpressionMultContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 409;
          this.multiplicativeExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 419;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 30, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              this.state = 417;
              this._errHandler.sync(this);
              switch (this._interp.adaptivePredict(this._input, 29, this._ctx)) {
                case 1:
                  {
                    localctx = new AddExpressionContext(
                      this,
                      new AdditiveExpressionContext(this, _parentctx, _parentState)
                    );
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_additiveExpression);
                    this.state = 411;
                    if (!this.precpred(this._ctx, 2)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 2)");
                    }
                    this.state = 412;
                    (localctx as AddExpressionContext)._op = this.match(FEEL_1_1Parser.ADD);
                    this.state = 413;
                    this.multiplicativeExpression(0);
                  }
                  break;
                case 2:
                  {
                    localctx = new AddExpressionContext(
                      this,
                      new AdditiveExpressionContext(this, _parentctx, _parentState)
                    );
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_additiveExpression);
                    this.state = 414;
                    if (!this.precpred(this._ctx, 1)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                    }
                    this.state = 415;
                    (localctx as AddExpressionContext)._op = this.match(FEEL_1_1Parser.SUB);
                    this.state = 416;
                    this.multiplicativeExpression(0);
                  }
                  break;
              }
            }
          }
          this.state = 421;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 30, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public multiplicativeExpression(): MultiplicativeExpressionContext;
  public multiplicativeExpression(_p: number): MultiplicativeExpressionContext;
  // @RuleVersion(0)
  public multiplicativeExpression(_p?: number): MultiplicativeExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: MultiplicativeExpressionContext = new MultiplicativeExpressionContext(this, this._ctx, _parentState);
    let _prevctx: MultiplicativeExpressionContext = localctx;
    const _startState: number = 66;
    this.enterRecursionRule(localctx, 66, FEEL_1_1Parser.RULE_multiplicativeExpression, _p);
    let _la: number;
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new MultExpressionPowContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 423;
          this.powerExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 430;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 31, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              {
                localctx = new MultExpressionContext(
                  this,
                  new MultiplicativeExpressionContext(this, _parentctx, _parentState)
                );
                this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_multiplicativeExpression);
                this.state = 425;
                if (!this.precpred(this._ctx, 1)) {
                  throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                }
                this.state = 426;
                (localctx as MultExpressionContext)._op = this._input.LT(1);
                _la = this._input.LA(1);
                if (!(_la === 45 || _la === 46)) {
                  (localctx as MultExpressionContext)._op = this._errHandler.recoverInline(this);
                } else {
                  this._errHandler.reportMatch(this);
                  this.consume();
                }
                this.state = 427;
                this.powerExpression(0);
              }
            }
          }
          this.state = 432;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 31, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public powerExpression(): PowerExpressionContext;
  public powerExpression(_p: number): PowerExpressionContext;
  // @RuleVersion(0)
  public powerExpression(_p?: number): PowerExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: PowerExpressionContext = new PowerExpressionContext(this, this._ctx, _parentState);
    let _prevctx: PowerExpressionContext = localctx;
    const _startState: number = 68;
    this.enterRecursionRule(localctx, 68, FEEL_1_1Parser.RULE_powerExpression, _p);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          localctx = new PowExpressionUnaryContext(this, localctx);
          this._ctx = localctx;
          _prevctx = localctx;

          this.state = 434;
          this.filterPathExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 441;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 32, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              {
                localctx = new PowExpressionContext(this, new PowerExpressionContext(this, _parentctx, _parentState));
                this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_powerExpression);
                this.state = 436;
                if (!this.precpred(this._ctx, 1)) {
                  throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                }
                this.state = 437;
                (localctx as PowExpressionContext)._op = this.match(FEEL_1_1Parser.POW);
                this.state = 438;
                this.filterPathExpression(0);
              }
            }
          }
          this.state = 443;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 32, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public filterPathExpression(): FilterPathExpressionContext;
  public filterPathExpression(_p: number): FilterPathExpressionContext;
  // @RuleVersion(0)
  public filterPathExpression(_p?: number): FilterPathExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: FilterPathExpressionContext = new FilterPathExpressionContext(this, this._ctx, _parentState);
    let _prevctx: FilterPathExpressionContext = localctx;
    const _startState: number = 70;
    this.enterRecursionRule(localctx, 70, FEEL_1_1Parser.RULE_filterPathExpression, _p);

    let count = 0;

    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        {
          this.state = 445;
          this.unaryExpression(0);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 462;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 34, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              this.state = 460;
              this._errHandler.sync(this);
              switch (this._interp.adaptivePredict(this._input, 33, this._ctx)) {
                case 1:
                  {
                    localctx = new FilterPathExpressionContext(this, _parentctx, _parentState);
                    localctx._n0 = _prevctx;
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_filterPathExpression);
                    this.state = 447;
                    if (!this.precpred(this._ctx, 2)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 2)");
                    }
                    this.state = 448;
                    this.match(FEEL_1_1Parser.LBRACK);
                    this.helper.enableDynamicResolution();
                    this.state = 450;
                    localctx._filter = this.expression();
                    this.helper.disableDynamicResolution();
                    this.state = 452;
                    this.match(FEEL_1_1Parser.RBRACK);
                  }
                  break;
                case 2:
                  {
                    localctx = new FilterPathExpressionContext(this, _parentctx, _parentState);
                    localctx._n1 = _prevctx;
                    this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_filterPathExpression);
                    this.state = 454;
                    if (!this.precpred(this._ctx, 1)) {
                      throw this.createFailedPredicateException("this.precpred(this._ctx, 1)");
                    }
                    this.state = 455;
                    this.match(FEEL_1_1Parser.DOT);
                    count = this.helper.fphStart(localctx._n1, this);
                    this.helper.enableDynamicResolution();
                    this.state = 457;
                    this.qualifiedName();
                    this.helper.disableDynamicResolution();
                    this.helper.fphEnd(count);
                  }
                  break;
              }
            }
          }
          this.state = 464;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 34, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }

  public unaryExpression(): UnaryExpressionContext;
  public unaryExpression(_p: number): UnaryExpressionContext;
  // @RuleVersion(0)
  public unaryExpression(_p?: number): UnaryExpressionContext {
    if (_p === undefined) {
      _p = 0;
    }

    const _parentctx: ParserRuleContext = this._ctx;
    const _parentState: number = this.state;
    let localctx: UnaryExpressionContext = new UnaryExpressionContext(this, this._ctx, _parentState);
    let _prevctx: UnaryExpressionContext = localctx;
    const _startState: number = 72;
    this.enterRecursionRule(localctx, 72, FEEL_1_1Parser.RULE_unaryExpression, _p);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 471;
        this._errHandler.sync(this);
        switch (this._input.LA(1)) {
          case 44:
            {
              localctx = new SignedUnaryExpressionMinusContext(this, localctx);
              this._ctx = localctx;
              _prevctx = localctx;

              this.state = 466;
              this.match(FEEL_1_1Parser.SUB);
              this.state = 467;
              this.unaryExpression(3);
            }
            break;
          case 1:
          case 2:
          case 5:
          case 8:
          case 9:
          case 18:
          case 22:
          case 23:
          case 24:
          case 25:
          case 27:
          case 29:
          case 30:
          case 34:
          case 35:
          case 36:
          case 37:
          case 38:
          case 39:
          case 48:
          case 49:
          case 50:
            {
              localctx = new NonSignedUnaryExpressionContext(this, localctx);
              this._ctx = localctx;
              _prevctx = localctx;
              this.state = 468;
              this.unaryExpressionNotPlusMinus();
            }
            break;
          case 43:
            {
              localctx = new SignedUnaryExpressionPlusContext(this, localctx);
              this._ctx = localctx;
              _prevctx = localctx;
              this.state = 469;
              this.match(FEEL_1_1Parser.ADD);
              this.state = 470;
              this.unaryExpressionNotPlusMinus();
            }
            break;
          default:
            throw new NoViableAltException(this);
        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 477;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 36, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            if (this._parseListeners != null) {
              this.triggerExitRuleEvent();
            }
            _prevctx = localctx;
            {
              {
                localctx = new FnInvocationContext(this, new UnaryExpressionContext(this, _parentctx, _parentState));
                this.pushNewRecursionContext(localctx, _startState, FEEL_1_1Parser.RULE_unaryExpression);
                this.state = 473;
                if (!this.precpred(this._ctx, 4)) {
                  throw this.createFailedPredicateException("this.precpred(this._ctx, 4)");
                }
                this.state = 474;
                this.parameters();
              }
            }
          }
          this.state = 479;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 36, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.unrollRecursionContexts(_parentctx);
    }
    return localctx;
  }
  // @RuleVersion(0)
  public unaryExpressionNotPlusMinus(): UnaryExpressionNotPlusMinusContext {
    let localctx: UnaryExpressionNotPlusMinusContext = new UnaryExpressionNotPlusMinusContext(
      this,
      this._ctx,
      this.state
    );
    this.enterRule(localctx, 74, FEEL_1_1Parser.RULE_unaryExpressionNotPlusMinus);
    try {
      localctx = new UenpmPrimaryContext(this, localctx);
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 480;
        this.primary();
        this.state = 489;
        this._errHandler.sync(this);
        switch (this._interp.adaptivePredict(this._input, 38, this._ctx)) {
          case 1:
            {
              this.state = 481;
              this.match(FEEL_1_1Parser.DOT);
              this.helper.recoverScope();
              this.helper.enableDynamicResolution();
              this.state = 483;
              this.qualifiedName();
              this.state = 485;
              this._errHandler.sync(this);
              switch (this._interp.adaptivePredict(this._input, 37, this._ctx)) {
                case 1:
                  {
                    this.state = 484;
                    this.parameters();
                  }
                  break;
              }
              this.helper.disableDynamicResolution();
              this.helper.dismissScope();
            }
            break;
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public primary(): PrimaryContext {
    let localctx: PrimaryContext = new PrimaryContext(this, this._ctx, this.state);
    this.enterRule(localctx, 76, FEEL_1_1Parser.RULE_primary);
    try {
      this.state = 504;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 39, this._ctx)) {
        case 1:
          localctx = new PrimaryLiteralContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 491;
            this.literal();
          }
          break;
        case 2:
          localctx = new PrimaryForExpressionContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 492;
            this.forExpression();
          }
          break;
        case 3:
          localctx = new PrimaryQuantifiedExpressionContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 493;
            this.quantifiedExpression();
          }
          break;
        case 4:
          localctx = new PrimaryIfExpressionContext(this, localctx);
          this.enterOuterAlt(localctx, 4);
          {
            this.state = 494;
            this.ifExpression();
          }
          break;
        case 5:
          localctx = new PrimaryIntervalContext(this, localctx);
          this.enterOuterAlt(localctx, 5);
          {
            this.state = 495;
            this.interval();
          }
          break;
        case 6:
          localctx = new PrimaryListContext(this, localctx);
          this.enterOuterAlt(localctx, 6);
          {
            this.state = 496;
            this.list();
          }
          break;
        case 7:
          localctx = new PrimaryContextContext(this, localctx);
          this.enterOuterAlt(localctx, 7);
          {
            this.state = 497;
            this.context();
          }
          break;
        case 8:
          localctx = new PrimaryParensContext(this, localctx);
          this.enterOuterAlt(localctx, 8);
          {
            this.state = 498;
            this.match(FEEL_1_1Parser.LPAREN);
            this.state = 499;
            this.expression();
            this.state = 500;
            this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 9:
          localctx = new PrimaryUnaryTestContext(this, localctx);
          this.enterOuterAlt(localctx, 9);
          {
            this.state = 502;
            this.simplePositiveUnaryTest();
          }
          break;
        case 10:
          localctx = new PrimaryNameContext(this, localctx);
          this.enterOuterAlt(localctx, 10);
          {
            this.state = 503;
            this.qualifiedName();
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public literal(): LiteralContext {
    let localctx: LiteralContext = new LiteralContext(this, this._ctx, this.state);
    this.enterRule(localctx, 78, FEEL_1_1Parser.RULE_literal);
    try {
      this.state = 512;
      this._errHandler.sync(this);
      switch (this._input.LA(1)) {
        case 22:
          localctx = new NumberLiteralContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 506;
            this.match(FEEL_1_1Parser.IntegerLiteral);
          }
          break;
        case 23:
          localctx = new NumberLiteralContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 507;
            this.match(FEEL_1_1Parser.FloatingPointLiteral);
          }
          break;
        case 1:
          localctx = new BoolLiteralContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 508;
            this.match(FEEL_1_1Parser.BooleanLiteral);
          }
          break;
        case 49:
          localctx = new AtLiteralLabelContext(this, localctx);
          this.enterOuterAlt(localctx, 4);
          {
            this.state = 509;
            this.atLiteral();
          }
          break;
        case 24:
          localctx = new StringLiteralContext(this, localctx);
          this.enterOuterAlt(localctx, 5);
          {
            this.state = 510;
            this.match(FEEL_1_1Parser.StringLiteral);
          }
          break;
        case 18:
          localctx = new NullLiteralContext(this, localctx);
          this.enterOuterAlt(localctx, 6);
          {
            this.state = 511;
            this.match(FEEL_1_1Parser.NULL);
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public atLiteral(): AtLiteralContext {
    const localctx: AtLiteralContext = new AtLiteralContext(this, this._ctx, this.state);
    this.enterRule(localctx, 80, FEEL_1_1Parser.RULE_atLiteral);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 514;
        this.match(FEEL_1_1Parser.AT);
        this.state = 515;
        this.atLiteralValue();
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public atLiteralValue(): AtLiteralValueContext {
    const localctx: AtLiteralValueContext = new AtLiteralValueContext(this, this._ctx, this.state);
    this.enterRule(localctx, 82, FEEL_1_1Parser.RULE_atLiteralValue);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 517;
        this.match(FEEL_1_1Parser.StringLiteral);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public simplePositiveUnaryTest(): SimplePositiveUnaryTestContext {
    let localctx: SimplePositiveUnaryTestContext = new SimplePositiveUnaryTestContext(this, this._ctx, this.state);
    this.enterRule(localctx, 84, FEEL_1_1Parser.RULE_simplePositiveUnaryTest);
    try {
      this.state = 550;
      this._errHandler.sync(this);
      switch (this._input.LA(1)) {
        case 36:
          localctx = new PositiveUnaryTestIneqIntervalContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 519;
            (localctx as PositiveUnaryTestIneqIntervalContext)._op = this.match(FEEL_1_1Parser.LT);
            this.helper.enableDynamicResolution();
            this.state = 521;
            this.endpoint();
            this.helper.disableDynamicResolution();
          }
          break;
        case 35:
          localctx = new PositiveUnaryTestIneqIntervalContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 524;
            (localctx as PositiveUnaryTestIneqIntervalContext)._op = this.match(FEEL_1_1Parser.GT);
            this.helper.enableDynamicResolution();
            this.state = 526;
            this.endpoint();
            this.helper.disableDynamicResolution();
          }
          break;
        case 37:
          localctx = new PositiveUnaryTestIneqIntervalContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 529;
            (localctx as PositiveUnaryTestIneqIntervalContext)._op = this.match(FEEL_1_1Parser.LE);
            this.helper.enableDynamicResolution();
            this.state = 531;
            this.endpoint();
            this.helper.disableDynamicResolution();
          }
          break;
        case 38:
          localctx = new PositiveUnaryTestIneqIntervalContext(this, localctx);
          this.enterOuterAlt(localctx, 4);
          {
            this.state = 534;
            (localctx as PositiveUnaryTestIneqIntervalContext)._op = this.match(FEEL_1_1Parser.GE);
            this.helper.enableDynamicResolution();
            this.state = 536;
            this.endpoint();
            this.helper.disableDynamicResolution();
          }
          break;
        case 34:
          localctx = new PositiveUnaryTestIneqContext(this, localctx);
          this.enterOuterAlt(localctx, 5);
          {
            this.state = 539;
            (localctx as PositiveUnaryTestIneqContext)._op = this.match(FEEL_1_1Parser.EQUAL);
            this.helper.enableDynamicResolution();
            this.state = 541;
            this.endpoint();
            this.helper.disableDynamicResolution();
          }
          break;
        case 39:
          localctx = new PositiveUnaryTestIneqContext(this, localctx);
          this.enterOuterAlt(localctx, 6);
          {
            this.state = 544;
            (localctx as PositiveUnaryTestIneqContext)._op = this.match(FEEL_1_1Parser.NOTEQUAL);
            this.helper.enableDynamicResolution();
            this.state = 546;
            this.endpoint();
            this.helper.disableDynamicResolution();
          }
          break;
        case 25:
        case 29:
        case 30:
          localctx = new PositiveUnaryTestIntervalContext(this, localctx);
          this.enterOuterAlt(localctx, 7);
          {
            this.state = 549;
            this.interval();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public simplePositiveUnaryTests(): SimplePositiveUnaryTestsContext {
    const localctx: SimplePositiveUnaryTestsContext = new SimplePositiveUnaryTestsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 86, FEEL_1_1Parser.RULE_simplePositiveUnaryTests);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 552;
        this.simplePositiveUnaryTest();
        this.state = 557;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 553;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 554;
              this.simplePositiveUnaryTest();
            }
          }
          this.state = 559;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public simpleUnaryTests(): SimpleUnaryTestsContext {
    let localctx: SimpleUnaryTestsContext = new SimpleUnaryTestsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 88, FEEL_1_1Parser.RULE_simpleUnaryTests);
    try {
      this.state = 567;
      this._errHandler.sync(this);
      switch (this._input.LA(1)) {
        case 25:
        case 29:
        case 30:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
          localctx = new PositiveSimplePositiveUnaryTestsContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 560;
            this.simplePositiveUnaryTests();
          }
          break;
        case 48:
          localctx = new NegatedSimplePositiveUnaryTestsContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 561;
            this.match(FEEL_1_1Parser.NOT);
            this.state = 562;
            this.match(FEEL_1_1Parser.LPAREN);
            this.state = 563;
            this.simplePositiveUnaryTests();
            this.state = 564;
            this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 44:
          localctx = new PositiveUnaryTestDashContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 566;
            this.match(FEEL_1_1Parser.SUB);
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public positiveUnaryTest(): PositiveUnaryTestContext {
    const localctx: PositiveUnaryTestContext = new PositiveUnaryTestContext(this, this._ctx, this.state);
    this.enterRule(localctx, 90, FEEL_1_1Parser.RULE_positiveUnaryTest);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 569;
        this.expression();
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public positiveUnaryTests(): PositiveUnaryTestsContext {
    const localctx: PositiveUnaryTestsContext = new PositiveUnaryTestsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 92, FEEL_1_1Parser.RULE_positiveUnaryTests);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 571;
        this.positiveUnaryTest();
        this.state = 576;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while (_la === 31) {
          {
            {
              this.state = 572;
              this.match(FEEL_1_1Parser.COMMA);
              this.state = 573;
              this.positiveUnaryTest();
            }
          }
          this.state = 578;
          this._errHandler.sync(this);
          _la = this._input.LA(1);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public unaryTestsRoot(): UnaryTestsRootContext {
    const localctx: UnaryTestsRootContext = new UnaryTestsRootContext(this, this._ctx, this.state);
    this.enterRule(localctx, 94, FEEL_1_1Parser.RULE_unaryTestsRoot);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 579;
        this.unaryTests();
        this.state = 580;
        this.match(FEEL_1_1Parser.EOF);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public unaryTests(): UnaryTestsContext {
    let localctx: UnaryTestsContext = new UnaryTestsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 96, FEEL_1_1Parser.RULE_unaryTests);
    try {
      this.state = 589;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 45, this._ctx)) {
        case 1:
          localctx = new UnaryTests_negatedContext(this, localctx);
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 582;
            this.match(FEEL_1_1Parser.NOT);
            this.state = 583;
            this.match(FEEL_1_1Parser.LPAREN);
            this.state = 584;
            this.positiveUnaryTests();
            this.state = 585;
            this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 2:
          localctx = new UnaryTests_positiveContext(this, localctx);
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 587;
            this.positiveUnaryTests();
          }
          break;
        case 3:
          localctx = new UnaryTests_emptyContext(this, localctx);
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 588;
            this.match(FEEL_1_1Parser.SUB);
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public endpoint(): EndpointContext {
    const localctx: EndpointContext = new EndpointContext(this, this._ctx, this.state);
    this.enterRule(localctx, 98, FEEL_1_1Parser.RULE_endpoint);
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 591;
        this.additiveExpression(0);
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public interval(): IntervalContext {
    const localctx: IntervalContext = new IntervalContext(this, this._ctx, this.state);
    this.enterRule(localctx, 100, FEEL_1_1Parser.RULE_interval);
    try {
      this.state = 647;
      this._errHandler.sync(this);
      switch (this._interp.adaptivePredict(this._input, 46, this._ctx)) {
        case 1:
          this.enterOuterAlt(localctx, 1);
          {
            this.state = 593;
            localctx._low = this.match(FEEL_1_1Parser.LPAREN);
            this.state = 594;
            localctx._start = this.endpoint();
            this.state = 595;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 596;
            localctx._end = this.endpoint();
            this.state = 597;
            localctx._up = this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 2:
          this.enterOuterAlt(localctx, 2);
          {
            this.state = 599;
            localctx._low = this.match(FEEL_1_1Parser.LPAREN);
            this.state = 600;
            localctx._start = this.endpoint();
            this.state = 601;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 602;
            localctx._end = this.endpoint();
            this.state = 603;
            localctx._up = this.match(FEEL_1_1Parser.LBRACK);
          }
          break;
        case 3:
          this.enterOuterAlt(localctx, 3);
          {
            this.state = 605;
            localctx._low = this.match(FEEL_1_1Parser.LPAREN);
            this.state = 606;
            localctx._start = this.endpoint();
            this.state = 607;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 608;
            localctx._end = this.endpoint();
            this.state = 609;
            localctx._up = this.match(FEEL_1_1Parser.RBRACK);
          }
          break;
        case 4:
          this.enterOuterAlt(localctx, 4);
          {
            this.state = 611;
            localctx._low = this.match(FEEL_1_1Parser.RBRACK);
            this.state = 612;
            localctx._start = this.endpoint();
            this.state = 613;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 614;
            localctx._end = this.endpoint();
            this.state = 615;
            localctx._up = this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 5:
          this.enterOuterAlt(localctx, 5);
          {
            this.state = 617;
            localctx._low = this.match(FEEL_1_1Parser.RBRACK);
            this.state = 618;
            localctx._start = this.endpoint();
            this.state = 619;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 620;
            localctx._end = this.endpoint();
            this.state = 621;
            localctx._up = this.match(FEEL_1_1Parser.LBRACK);
          }
          break;
        case 6:
          this.enterOuterAlt(localctx, 6);
          {
            this.state = 623;
            localctx._low = this.match(FEEL_1_1Parser.RBRACK);
            this.state = 624;
            localctx._start = this.endpoint();
            this.state = 625;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 626;
            localctx._end = this.endpoint();
            this.state = 627;
            localctx._up = this.match(FEEL_1_1Parser.RBRACK);
          }
          break;
        case 7:
          this.enterOuterAlt(localctx, 7);
          {
            this.state = 629;
            localctx._low = this.match(FEEL_1_1Parser.LBRACK);
            this.state = 630;
            localctx._start = this.endpoint();
            this.state = 631;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 632;
            localctx._end = this.endpoint();
            this.state = 633;
            localctx._up = this.match(FEEL_1_1Parser.RPAREN);
          }
          break;
        case 8:
          this.enterOuterAlt(localctx, 8);
          {
            this.state = 635;
            localctx._low = this.match(FEEL_1_1Parser.LBRACK);
            this.state = 636;
            localctx._start = this.endpoint();
            this.state = 637;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 638;
            localctx._end = this.endpoint();
            this.state = 639;
            localctx._up = this.match(FEEL_1_1Parser.LBRACK);
          }
          break;
        case 9:
          this.enterOuterAlt(localctx, 9);
          {
            this.state = 641;
            localctx._low = this.match(FEEL_1_1Parser.LBRACK);
            this.state = 642;
            localctx._start = this.endpoint();
            this.state = 643;
            this.match(FEEL_1_1Parser.ELIPSIS);
            this.state = 644;
            localctx._end = this.endpoint();
            this.state = 645;
            localctx._up = this.match(FEEL_1_1Parser.RBRACK);
          }
          break;
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public qualifiedName(): QualifiedNameContext {
    const localctx: QualifiedNameContext = new QualifiedNameContext(this, this._ctx, this.state);
    this.enterRule(localctx, 102, FEEL_1_1Parser.RULE_qualifiedName);

    let name = ""; // null?
    let count = 0;
    const qn = new Array<string>();

    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 649;
        localctx._n1 = this.nameRef();
        name = this.helper.getOriginalText(localctx._n1);
        qn.push(name);
        this.helper.validateVariable(localctx._n1, qn, name);
        this.state = 658;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 47, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            {
              {
                this.state = 651;
                this.match(FEEL_1_1Parser.DOT);
                this.helper.recoverScope(name);
                count++;
                this.state = 653;
                localctx._n2 = this.nameRef();
                name = this.helper.getOriginalText(localctx._n2);
                qn.push(name);
                this.helper.validateVariable(localctx._n2, qn, name);
              }
            }
          }
          this.state = 660;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 47, this._ctx);
        }
      }
      this._ctx.stop = this._input.LT(-1);

      localctx.qns = qn;
      for (let i = 0; i < count; i++) {
        this.helper.dismissScope();
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public nameRef(): NameRefContext {
    const localctx: NameRefContext = new NameRefContext(this, this._ctx, this.state);
    this.enterRule(localctx, 104, FEEL_1_1Parser.RULE_nameRef);
    try {
      let _alt: number;
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 665;
        this._errHandler.sync(this);
        switch (this._input.LA(1)) {
          case 50:
            {
              this.state = 661;
              localctx._st = this.match(FEEL_1_1Parser.Identifier);
              this.helper.startVariable(localctx._st);
            }
            break;
          case 48:
            {
              this.state = 663;
              localctx._not_st = this.match(FEEL_1_1Parser.NOT);
              this.helper.startVariable(localctx._not_st);
            }
            break;
          default:
            throw new NoViableAltException(this);
        }
        this.state = 670;
        this._errHandler.sync(this);
        _alt = this._interp.adaptivePredict(this._input, 49, this._ctx);
        while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
          if (_alt === 1) {
            {
              {
                this.state = 667;
                this.nameRefOtherToken();
              }
            }
          }
          this.state = 672;
          this._errHandler.sync(this);
          _alt = this._interp.adaptivePredict(this._input, 49, this._ctx);
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public nameRefOtherToken(): NameRefOtherTokenContext {
    const localctx: NameRefOtherTokenContext = new NameRefOtherTokenContext(this, this._ctx, this.state);
    this.enterRule(localctx, 106, FEEL_1_1Parser.RULE_nameRefOtherToken);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 673;
        if (!this.helper.followUp(this._input.LT(1), localctx == null)) {
          throw this.createFailedPredicateException(" this.helper.followUp( this._input.LT(1), localctx==null ) ");
        }
        this.state = 674;
        _la = this._input.LA(1);
        if (_la <= 0 || (((_la - 25) & ~0x1f) === 0 && ((1 << (_la - 25)) & 4198015) !== 0)) {
          this._errHandler.recoverInline(this);
        } else {
          this._errHandler.reportMatch(this);
          this.consume();
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }
  // @RuleVersion(0)
  public reusableKeywords(): ReusableKeywordsContext {
    const localctx: ReusableKeywordsContext = new ReusableKeywordsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 108, FEEL_1_1Parser.RULE_reusableKeywords);
    let _la: number;
    try {
      this.enterOuterAlt(localctx, 1);
      {
        this.state = 676;
        _la = this._input.LA(1);
        if (!(((_la & ~0x1f) === 0 && ((1 << _la) & 2097132) !== 0) || _la === 48)) {
          this._errHandler.recoverInline(this);
        } else {
          this._errHandler.reportMatch(this);
          this.consume();
        }
      }
    } catch (re) {
      if (re instanceof RecognitionException) {
        localctx.exception = re;
        this._errHandler.reportError(this, re);
        this._errHandler.recover(this, re);
      } else {
        throw re;
      }
    } finally {
      this.exitRule();
    }
    return localctx;
  }

  public sempred(localctx: RuleContext, ruleIndex: number, predIndex: number): boolean {
    switch (ruleIndex) {
      case 9:
        return this.iterationContext_sempred(localctx as IterationContextContext, predIndex);
      case 12:
        return this.type_sempred(localctx as TypeContext, predIndex);
      case 27:
        return this.conditionalOrExpression_sempred(localctx as ConditionalOrExpressionContext, predIndex);
      case 28:
        return this.conditionalAndExpression_sempred(localctx as ConditionalAndExpressionContext, predIndex);
      case 29:
        return this.comparisonExpression_sempred(localctx as ComparisonExpressionContext, predIndex);
      case 30:
        return this.relationalExpression_sempred(localctx as RelationalExpressionContext, predIndex);
      case 32:
        return this.additiveExpression_sempred(localctx as AdditiveExpressionContext, predIndex);
      case 33:
        return this.multiplicativeExpression_sempred(localctx as MultiplicativeExpressionContext, predIndex);
      case 34:
        return this.powerExpression_sempred(localctx as PowerExpressionContext, predIndex);
      case 35:
        return this.filterPathExpression_sempred(localctx as FilterPathExpressionContext, predIndex);
      case 36:
        return this.unaryExpression_sempred(localctx as UnaryExpressionContext, predIndex);
      case 53:
        return this.nameRefOtherToken_sempred(localctx as NameRefOtherTokenContext, predIndex);
    }
    return true;
  }
  private iterationContext_sempred(localctx: IterationContextContext, predIndex: number): boolean {
    switch (predIndex) {
      case 0:
        return this.helper.isFeatDMN12EnhancedForLoopEnabled();
    }
    return true;
  }
  private type_sempred(localctx: TypeContext, predIndex: number): boolean {
    switch (predIndex) {
      case 1:
        return this._input.LT(1).text === "list";
      case 2:
        return this._input.LT(1).text === "context";
    }
    return true;
  }
  private conditionalOrExpression_sempred(localctx: ConditionalOrExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 3:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private conditionalAndExpression_sempred(localctx: ConditionalAndExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 4:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private comparisonExpression_sempred(localctx: ComparisonExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 5:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private relationalExpression_sempred(localctx: RelationalExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 6:
        return this.precpred(this._ctx, 4);
      case 7:
        return this.precpred(this._ctx, 3);
      case 8:
        return this.precpred(this._ctx, 2);
      case 9:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private additiveExpression_sempred(localctx: AdditiveExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 10:
        return this.precpred(this._ctx, 2);
      case 11:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private multiplicativeExpression_sempred(localctx: MultiplicativeExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 12:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private powerExpression_sempred(localctx: PowerExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 13:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private filterPathExpression_sempred(localctx: FilterPathExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 14:
        return this.precpred(this._ctx, 2);
      case 15:
        return this.precpred(this._ctx, 1);
    }
    return true;
  }
  private unaryExpression_sempred(localctx: UnaryExpressionContext, predIndex: number): boolean {
    switch (predIndex) {
      case 16:
        return this.precpred(this._ctx, 4);
    }
    return true;
  }
  private nameRefOtherToken_sempred(localctx: NameRefOtherTokenContext, predIndex: number): boolean {
    switch (predIndex) {
      case 17:
        return this.helper.followUp(this._input.LT(1), localctx == null);
    }
    return true;
  }

  public static readonly _serializedATN: number[] = [
    4, 1, 54, 679, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 2, 4, 7, 4, 2, 5, 7, 5, 2, 6, 7, 6, 2, 7, 7, 7, 2, 8,
    7, 8, 2, 9, 7, 9, 2, 10, 7, 10, 2, 11, 7, 11, 2, 12, 7, 12, 2, 13, 7, 13, 2, 14, 7, 14, 2, 15, 7, 15, 2, 16, 7, 16,
    2, 17, 7, 17, 2, 18, 7, 18, 2, 19, 7, 19, 2, 20, 7, 20, 2, 21, 7, 21, 2, 22, 7, 22, 2, 23, 7, 23, 2, 24, 7, 24, 2,
    25, 7, 25, 2, 26, 7, 26, 2, 27, 7, 27, 2, 28, 7, 28, 2, 29, 7, 29, 2, 30, 7, 30, 2, 31, 7, 31, 2, 32, 7, 32, 2, 33,
    7, 33, 2, 34, 7, 34, 2, 35, 7, 35, 2, 36, 7, 36, 2, 37, 7, 37, 2, 38, 7, 38, 2, 39, 7, 39, 2, 40, 7, 40, 2, 41, 7,
    41, 2, 42, 7, 42, 2, 43, 7, 43, 2, 44, 7, 44, 2, 45, 7, 45, 2, 46, 7, 46, 2, 47, 7, 47, 2, 48, 7, 48, 2, 49, 7, 49,
    2, 50, 7, 50, 2, 51, 7, 51, 2, 52, 7, 52, 2, 53, 7, 53, 2, 54, 7, 54, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 2, 1, 2, 3,
    2, 118, 8, 2, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 3, 3, 130, 8, 3, 1, 4, 1, 4, 1, 4, 5, 4,
    135, 8, 4, 10, 4, 12, 4, 138, 9, 4, 1, 5, 1, 5, 1, 5, 1, 5, 1, 6, 1, 6, 1, 6, 5, 6, 147, 8, 6, 10, 6, 12, 6, 150, 9,
    6, 1, 7, 1, 7, 1, 7, 1, 7, 1, 7, 1, 7, 1, 7, 1, 8, 1, 8, 1, 8, 5, 8, 162, 8, 8, 10, 8, 12, 8, 165, 9, 8, 1, 9, 1, 9,
    1, 9, 1, 9, 1, 9, 1, 9, 1, 9, 1, 9, 1, 9, 1, 9, 1, 9, 3, 9, 178, 8, 9, 1, 10, 1, 10, 1, 10, 1, 10, 1, 10, 1, 10, 1,
    10, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 1, 11, 3, 11, 201, 8,
    11, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12,
    5, 12, 219, 8, 12, 10, 12, 12, 12, 222, 9, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 1, 12, 5, 12, 232,
    8, 12, 10, 12, 12, 12, 235, 9, 12, 3, 12, 237, 8, 12, 1, 12, 1, 12, 1, 12, 1, 12, 3, 12, 243, 8, 12, 1, 13, 1, 13,
    1, 13, 1, 13, 1, 13, 1, 13, 3, 13, 251, 8, 13, 1, 14, 1, 14, 1, 14, 3, 14, 256, 8, 14, 1, 14, 1, 14, 3, 14, 260, 8,
    14, 1, 14, 1, 14, 1, 14, 1, 14, 1, 15, 1, 15, 1, 15, 5, 15, 269, 8, 15, 10, 15, 12, 15, 272, 9, 15, 1, 16, 1, 16, 1,
    16, 1, 16, 1, 16, 3, 16, 279, 8, 16, 1, 17, 1, 17, 1, 17, 1, 17, 1, 17, 1, 17, 3, 17, 287, 8, 17, 1, 18, 1, 18, 1,
    18, 5, 18, 292, 8, 18, 10, 18, 12, 18, 295, 9, 18, 1, 19, 1, 19, 1, 19, 1, 19, 1, 19, 1, 19, 1, 20, 1, 20, 3, 20,
    305, 8, 20, 1, 21, 1, 21, 1, 21, 1, 22, 1, 22, 1, 22, 1, 23, 1, 23, 1, 23, 1, 23, 1, 23, 1, 23, 1, 23, 5, 23, 320,
    8, 23, 10, 23, 12, 23, 323, 9, 23, 1, 24, 1, 24, 1, 24, 1, 25, 1, 25, 1, 25, 1, 25, 1, 25, 1, 25, 5, 25, 334, 8, 25,
    10, 25, 12, 25, 337, 9, 25, 1, 26, 1, 26, 1, 27, 1, 27, 1, 27, 1, 27, 1, 27, 1, 27, 5, 27, 347, 8, 27, 10, 27, 12,
    27, 350, 9, 27, 1, 28, 1, 28, 1, 28, 1, 28, 1, 28, 1, 28, 5, 28, 358, 8, 28, 10, 28, 12, 28, 361, 9, 28, 1, 29, 1,
    29, 1, 29, 1, 29, 1, 29, 1, 29, 5, 29, 369, 8, 29, 10, 29, 12, 29, 372, 9, 29, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1,
    30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30, 1, 30,
    5, 30, 396, 8, 30, 10, 30, 12, 30, 399, 9, 30, 1, 31, 1, 31, 1, 31, 5, 31, 404, 8, 31, 10, 31, 12, 31, 407, 9, 31,
    1, 32, 1, 32, 1, 32, 1, 32, 1, 32, 1, 32, 1, 32, 1, 32, 1, 32, 5, 32, 418, 8, 32, 10, 32, 12, 32, 421, 9, 32, 1, 33,
    1, 33, 1, 33, 1, 33, 1, 33, 1, 33, 5, 33, 429, 8, 33, 10, 33, 12, 33, 432, 9, 33, 1, 34, 1, 34, 1, 34, 1, 34, 1, 34,
    1, 34, 5, 34, 440, 8, 34, 10, 34, 12, 34, 443, 9, 34, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35,
    1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 1, 35, 5, 35, 461, 8, 35, 10, 35, 12, 35, 464, 9, 35, 1, 36, 1, 36, 1, 36,
    1, 36, 1, 36, 1, 36, 3, 36, 472, 8, 36, 1, 36, 1, 36, 5, 36, 476, 8, 36, 10, 36, 12, 36, 479, 9, 36, 1, 37, 1, 37,
    1, 37, 1, 37, 1, 37, 3, 37, 486, 8, 37, 1, 37, 1, 37, 3, 37, 490, 8, 37, 1, 38, 1, 38, 1, 38, 1, 38, 1, 38, 1, 38,
    1, 38, 1, 38, 1, 38, 1, 38, 1, 38, 1, 38, 1, 38, 3, 38, 505, 8, 38, 1, 39, 1, 39, 1, 39, 1, 39, 1, 39, 1, 39, 3, 39,
    513, 8, 39, 1, 40, 1, 40, 1, 40, 1, 41, 1, 41, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42,
    1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1, 42, 1,
    42, 1, 42, 1, 42, 1, 42, 1, 42, 3, 42, 551, 8, 42, 1, 43, 1, 43, 1, 43, 5, 43, 556, 8, 43, 10, 43, 12, 43, 559, 9,
    43, 1, 44, 1, 44, 1, 44, 1, 44, 1, 44, 1, 44, 1, 44, 3, 44, 568, 8, 44, 1, 45, 1, 45, 1, 46, 1, 46, 1, 46, 5, 46,
    575, 8, 46, 10, 46, 12, 46, 578, 9, 46, 1, 47, 1, 47, 1, 47, 1, 48, 1, 48, 1, 48, 1, 48, 1, 48, 1, 48, 1, 48, 3, 48,
    590, 8, 48, 1, 49, 1, 49, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50,
    1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1,
    50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50,
    1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 1, 50, 3, 50, 648, 8, 50, 1, 51, 1, 51, 1, 51, 1, 51, 1, 51, 1, 51,
    1, 51, 5, 51, 657, 8, 51, 10, 51, 12, 51, 660, 9, 51, 1, 52, 1, 52, 1, 52, 1, 52, 3, 52, 666, 8, 52, 1, 52, 5, 52,
    669, 8, 52, 10, 52, 12, 52, 672, 9, 52, 1, 53, 1, 53, 1, 53, 1, 54, 1, 54, 1, 54, 0, 9, 54, 56, 58, 60, 64, 66, 68,
    70, 72, 55, 0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52,
    54, 56, 58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96, 98, 100, 102, 104, 106, 108,
    0, 5, 3, 0, 21, 21, 33, 33, 43, 46, 1, 0, 34, 39, 1, 0, 45, 46, 3, 0, 25, 31, 34, 36, 47, 47, 3, 0, 2, 3, 5, 20, 48,
    48, 713, 0, 110, 1, 0, 0, 0, 2, 113, 1, 0, 0, 0, 4, 117, 1, 0, 0, 0, 6, 129, 1, 0, 0, 0, 8, 131, 1, 0, 0, 0, 10,
    139, 1, 0, 0, 0, 12, 143, 1, 0, 0, 0, 14, 151, 1, 0, 0, 0, 16, 158, 1, 0, 0, 0, 18, 177, 1, 0, 0, 0, 20, 179, 1, 0,
    0, 0, 22, 200, 1, 0, 0, 0, 24, 242, 1, 0, 0, 0, 26, 250, 1, 0, 0, 0, 28, 252, 1, 0, 0, 0, 30, 265, 1, 0, 0, 0, 32,
    278, 1, 0, 0, 0, 34, 286, 1, 0, 0, 0, 36, 288, 1, 0, 0, 0, 38, 296, 1, 0, 0, 0, 40, 304, 1, 0, 0, 0, 42, 306, 1, 0,
    0, 0, 44, 309, 1, 0, 0, 0, 46, 312, 1, 0, 0, 0, 48, 324, 1, 0, 0, 0, 50, 327, 1, 0, 0, 0, 52, 338, 1, 0, 0, 0, 54,
    340, 1, 0, 0, 0, 56, 351, 1, 0, 0, 0, 58, 362, 1, 0, 0, 0, 60, 373, 1, 0, 0, 0, 62, 400, 1, 0, 0, 0, 64, 408, 1, 0,
    0, 0, 66, 422, 1, 0, 0, 0, 68, 433, 1, 0, 0, 0, 70, 444, 1, 0, 0, 0, 72, 471, 1, 0, 0, 0, 74, 480, 1, 0, 0, 0, 76,
    504, 1, 0, 0, 0, 78, 512, 1, 0, 0, 0, 80, 514, 1, 0, 0, 0, 82, 517, 1, 0, 0, 0, 84, 550, 1, 0, 0, 0, 86, 552, 1, 0,
    0, 0, 88, 567, 1, 0, 0, 0, 90, 569, 1, 0, 0, 0, 92, 571, 1, 0, 0, 0, 94, 579, 1, 0, 0, 0, 96, 589, 1, 0, 0, 0, 98,
    591, 1, 0, 0, 0, 100, 647, 1, 0, 0, 0, 102, 649, 1, 0, 0, 0, 104, 665, 1, 0, 0, 0, 106, 673, 1, 0, 0, 0, 108, 676,
    1, 0, 0, 0, 110, 111, 3, 2, 1, 0, 111, 112, 5, 0, 0, 1, 112, 1, 1, 0, 0, 0, 113, 114, 3, 4, 2, 0, 114, 3, 1, 0, 0,
    0, 115, 118, 3, 28, 14, 0, 116, 118, 3, 54, 27, 0, 117, 115, 1, 0, 0, 0, 117, 116, 1, 0, 0, 0, 118, 5, 1, 0, 0, 0,
    119, 120, 5, 25, 0, 0, 120, 130, 5, 26, 0, 0, 121, 122, 5, 25, 0, 0, 122, 123, 3, 8, 4, 0, 123, 124, 5, 26, 0, 0,
    124, 130, 1, 0, 0, 0, 125, 126, 5, 25, 0, 0, 126, 127, 3, 12, 6, 0, 127, 128, 5, 26, 0, 0, 128, 130, 1, 0, 0, 0,
    129, 119, 1, 0, 0, 0, 129, 121, 1, 0, 0, 0, 129, 125, 1, 0, 0, 0, 130, 7, 1, 0, 0, 0, 131, 136, 3, 10, 5, 0, 132,
    133, 5, 31, 0, 0, 133, 135, 3, 10, 5, 0, 134, 132, 1, 0, 0, 0, 135, 138, 1, 0, 0, 0, 136, 134, 1, 0, 0, 0, 136, 137,
    1, 0, 0, 0, 137, 9, 1, 0, 0, 0, 138, 136, 1, 0, 0, 0, 139, 140, 3, 42, 21, 0, 140, 141, 5, 40, 0, 0, 141, 142, 3, 2,
    1, 0, 142, 11, 1, 0, 0, 0, 143, 148, 3, 2, 1, 0, 144, 145, 5, 31, 0, 0, 145, 147, 3, 2, 1, 0, 146, 144, 1, 0, 0, 0,
    147, 150, 1, 0, 0, 0, 148, 146, 1, 0, 0, 0, 148, 149, 1, 0, 0, 0, 149, 13, 1, 0, 0, 0, 150, 148, 1, 0, 0, 0, 151,
    152, 5, 2, 0, 0, 152, 153, 3, 16, 8, 0, 153, 154, 5, 3, 0, 0, 154, 155, 6, 7, -1, 0, 155, 156, 3, 2, 1, 0, 156, 157,
    6, 7, -1, 0, 157, 15, 1, 0, 0, 0, 158, 163, 3, 18, 9, 0, 159, 160, 5, 31, 0, 0, 160, 162, 3, 18, 9, 0, 161, 159, 1,
    0, 0, 0, 162, 165, 1, 0, 0, 0, 163, 161, 1, 0, 0, 0, 163, 164, 1, 0, 0, 0, 164, 17, 1, 0, 0, 0, 165, 163, 1, 0, 0,
    0, 166, 167, 4, 9, 0, 0, 167, 168, 3, 48, 24, 0, 168, 169, 5, 4, 0, 0, 169, 170, 3, 2, 1, 0, 170, 171, 5, 32, 0, 0,
    171, 172, 3, 2, 1, 0, 172, 178, 1, 0, 0, 0, 173, 174, 3, 48, 24, 0, 174, 175, 5, 4, 0, 0, 175, 176, 3, 2, 1, 0, 176,
    178, 1, 0, 0, 0, 177, 166, 1, 0, 0, 0, 177, 173, 1, 0, 0, 0, 178, 19, 1, 0, 0, 0, 179, 180, 5, 5, 0, 0, 180, 181, 3,
    2, 1, 0, 181, 182, 5, 6, 0, 0, 182, 183, 3, 2, 1, 0, 183, 184, 5, 7, 0, 0, 184, 185, 3, 2, 1, 0, 185, 21, 1, 0, 0,
    0, 186, 187, 5, 8, 0, 0, 187, 188, 3, 16, 8, 0, 188, 189, 5, 10, 0, 0, 189, 190, 6, 11, -1, 0, 190, 191, 3, 2, 1, 0,
    191, 192, 6, 11, -1, 0, 192, 201, 1, 0, 0, 0, 193, 194, 5, 9, 0, 0, 194, 195, 3, 16, 8, 0, 195, 196, 5, 10, 0, 0,
    196, 197, 6, 11, -1, 0, 197, 198, 3, 2, 1, 0, 198, 199, 6, 11, -1, 0, 199, 201, 1, 0, 0, 0, 200, 186, 1, 0, 0, 0,
    200, 193, 1, 0, 0, 0, 201, 23, 1, 0, 0, 0, 202, 203, 4, 12, 1, 0, 203, 204, 5, 50, 0, 0, 204, 205, 5, 36, 0, 0, 205,
    206, 3, 24, 12, 0, 206, 207, 5, 35, 0, 0, 207, 243, 1, 0, 0, 0, 208, 209, 4, 12, 2, 0, 209, 210, 5, 50, 0, 0, 210,
    211, 5, 36, 0, 0, 211, 212, 5, 50, 0, 0, 212, 213, 5, 40, 0, 0, 213, 220, 3, 24, 12, 0, 214, 215, 5, 31, 0, 0, 215,
    216, 5, 50, 0, 0, 216, 217, 5, 40, 0, 0, 217, 219, 3, 24, 12, 0, 218, 214, 1, 0, 0, 0, 219, 222, 1, 0, 0, 0, 220,
    218, 1, 0, 0, 0, 220, 221, 1, 0, 0, 0, 221, 223, 1, 0, 0, 0, 222, 220, 1, 0, 0, 0, 223, 224, 5, 35, 0, 0, 224, 243,
    1, 0, 0, 0, 225, 243, 5, 13, 0, 0, 226, 227, 5, 13, 0, 0, 227, 236, 5, 36, 0, 0, 228, 233, 3, 24, 12, 0, 229, 230,
    5, 31, 0, 0, 230, 232, 3, 24, 12, 0, 231, 229, 1, 0, 0, 0, 232, 235, 1, 0, 0, 0, 233, 231, 1, 0, 0, 0, 233, 234, 1,
    0, 0, 0, 234, 237, 1, 0, 0, 0, 235, 233, 1, 0, 0, 0, 236, 228, 1, 0, 0, 0, 236, 237, 1, 0, 0, 0, 237, 238, 1, 0, 0,
    0, 238, 239, 5, 35, 0, 0, 239, 240, 5, 41, 0, 0, 240, 243, 3, 24, 12, 0, 241, 243, 3, 102, 51, 0, 242, 202, 1, 0, 0,
    0, 242, 208, 1, 0, 0, 0, 242, 225, 1, 0, 0, 0, 242, 226, 1, 0, 0, 0, 242, 241, 1, 0, 0, 0, 243, 25, 1, 0, 0, 0, 244,
    245, 5, 29, 0, 0, 245, 251, 5, 30, 0, 0, 246, 247, 5, 29, 0, 0, 247, 248, 3, 62, 31, 0, 248, 249, 5, 30, 0, 0, 249,
    251, 1, 0, 0, 0, 250, 244, 1, 0, 0, 0, 250, 246, 1, 0, 0, 0, 251, 27, 1, 0, 0, 0, 252, 253, 5, 13, 0, 0, 253, 255,
    5, 25, 0, 0, 254, 256, 3, 30, 15, 0, 255, 254, 1, 0, 0, 0, 255, 256, 1, 0, 0, 0, 256, 257, 1, 0, 0, 0, 257, 259, 5,
    26, 0, 0, 258, 260, 5, 14, 0, 0, 259, 258, 1, 0, 0, 0, 259, 260, 1, 0, 0, 0, 260, 261, 1, 0, 0, 0, 261, 262, 6, 14,
    -1, 0, 262, 263, 3, 2, 1, 0, 263, 264, 6, 14, -1, 0, 264, 29, 1, 0, 0, 0, 265, 270, 3, 32, 16, 0, 266, 267, 5, 31,
    0, 0, 267, 269, 3, 32, 16, 0, 268, 266, 1, 0, 0, 0, 269, 272, 1, 0, 0, 0, 270, 268, 1, 0, 0, 0, 270, 271, 1, 0, 0,
    0, 271, 31, 1, 0, 0, 0, 272, 270, 1, 0, 0, 0, 273, 274, 3, 42, 21, 0, 274, 275, 5, 40, 0, 0, 275, 276, 3, 24, 12, 0,
    276, 279, 1, 0, 0, 0, 277, 279, 3, 42, 21, 0, 278, 273, 1, 0, 0, 0, 278, 277, 1, 0, 0, 0, 279, 33, 1, 0, 0, 0, 280,
    281, 5, 27, 0, 0, 281, 287, 5, 28, 0, 0, 282, 283, 5, 27, 0, 0, 283, 284, 3, 36, 18, 0, 284, 285, 5, 28, 0, 0, 285,
    287, 1, 0, 0, 0, 286, 280, 1, 0, 0, 0, 286, 282, 1, 0, 0, 0, 287, 35, 1, 0, 0, 0, 288, 293, 3, 38, 19, 0, 289, 290,
    5, 31, 0, 0, 290, 292, 3, 38, 19, 0, 291, 289, 1, 0, 0, 0, 292, 295, 1, 0, 0, 0, 293, 291, 1, 0, 0, 0, 293, 294, 1,
    0, 0, 0, 294, 37, 1, 0, 0, 0, 295, 293, 1, 0, 0, 0, 296, 297, 3, 40, 20, 0, 297, 298, 6, 19, -1, 0, 298, 299, 5, 40,
    0, 0, 299, 300, 3, 2, 1, 0, 300, 301, 6, 19, -1, 0, 301, 39, 1, 0, 0, 0, 302, 305, 3, 42, 21, 0, 303, 305, 5, 24, 0,
    0, 304, 302, 1, 0, 0, 0, 304, 303, 1, 0, 0, 0, 305, 41, 1, 0, 0, 0, 306, 307, 3, 46, 23, 0, 307, 308, 6, 21, -1, 0,
    308, 43, 1, 0, 0, 0, 309, 310, 3, 42, 21, 0, 310, 311, 5, 0, 0, 1, 311, 45, 1, 0, 0, 0, 312, 321, 5, 50, 0, 0, 313,
    320, 5, 50, 0, 0, 314, 320, 3, 52, 26, 0, 315, 320, 5, 22, 0, 0, 316, 320, 5, 23, 0, 0, 317, 320, 3, 108, 54, 0,
    318, 320, 5, 4, 0, 0, 319, 313, 1, 0, 0, 0, 319, 314, 1, 0, 0, 0, 319, 315, 1, 0, 0, 0, 319, 316, 1, 0, 0, 0, 319,
    317, 1, 0, 0, 0, 319, 318, 1, 0, 0, 0, 320, 323, 1, 0, 0, 0, 321, 319, 1, 0, 0, 0, 321, 322, 1, 0, 0, 0, 322, 47, 1,
    0, 0, 0, 323, 321, 1, 0, 0, 0, 324, 325, 3, 50, 25, 0, 325, 326, 6, 24, -1, 0, 326, 49, 1, 0, 0, 0, 327, 335, 5, 50,
    0, 0, 328, 334, 5, 50, 0, 0, 329, 334, 3, 52, 26, 0, 330, 334, 5, 22, 0, 0, 331, 334, 5, 23, 0, 0, 332, 334, 3, 108,
    54, 0, 333, 328, 1, 0, 0, 0, 333, 329, 1, 0, 0, 0, 333, 330, 1, 0, 0, 0, 333, 331, 1, 0, 0, 0, 333, 332, 1, 0, 0, 0,
    334, 337, 1, 0, 0, 0, 335, 333, 1, 0, 0, 0, 335, 336, 1, 0, 0, 0, 336, 51, 1, 0, 0, 0, 337, 335, 1, 0, 0, 0, 338,
    339, 7, 0, 0, 0, 339, 53, 1, 0, 0, 0, 340, 341, 6, 27, -1, 0, 341, 342, 3, 56, 28, 0, 342, 348, 1, 0, 0, 0, 343,
    344, 10, 1, 0, 0, 344, 345, 5, 15, 0, 0, 345, 347, 3, 56, 28, 0, 346, 343, 1, 0, 0, 0, 347, 350, 1, 0, 0, 0, 348,
    346, 1, 0, 0, 0, 348, 349, 1, 0, 0, 0, 349, 55, 1, 0, 0, 0, 350, 348, 1, 0, 0, 0, 351, 352, 6, 28, -1, 0, 352, 353,
    3, 58, 29, 0, 353, 359, 1, 0, 0, 0, 354, 355, 10, 1, 0, 0, 355, 356, 5, 16, 0, 0, 356, 358, 3, 58, 29, 0, 357, 354,
    1, 0, 0, 0, 358, 361, 1, 0, 0, 0, 359, 357, 1, 0, 0, 0, 359, 360, 1, 0, 0, 0, 360, 57, 1, 0, 0, 0, 361, 359, 1, 0,
    0, 0, 362, 363, 6, 29, -1, 0, 363, 364, 3, 60, 30, 0, 364, 370, 1, 0, 0, 0, 365, 366, 10, 1, 0, 0, 366, 367, 7, 1,
    0, 0, 367, 369, 3, 60, 30, 0, 368, 365, 1, 0, 0, 0, 369, 372, 1, 0, 0, 0, 370, 368, 1, 0, 0, 0, 370, 371, 1, 0, 0,
    0, 371, 59, 1, 0, 0, 0, 372, 370, 1, 0, 0, 0, 373, 374, 6, 30, -1, 0, 374, 375, 3, 64, 32, 0, 375, 397, 1, 0, 0, 0,
    376, 377, 10, 4, 0, 0, 377, 378, 5, 17, 0, 0, 378, 379, 3, 64, 32, 0, 379, 380, 5, 16, 0, 0, 380, 381, 3, 64, 32, 0,
    381, 396, 1, 0, 0, 0, 382, 383, 10, 3, 0, 0, 383, 384, 5, 4, 0, 0, 384, 385, 5, 25, 0, 0, 385, 386, 3, 92, 46, 0,
    386, 387, 5, 26, 0, 0, 387, 396, 1, 0, 0, 0, 388, 389, 10, 2, 0, 0, 389, 390, 5, 4, 0, 0, 390, 396, 3, 2, 1, 0, 391,
    392, 10, 1, 0, 0, 392, 393, 5, 11, 0, 0, 393, 394, 5, 12, 0, 0, 394, 396, 3, 24, 12, 0, 395, 376, 1, 0, 0, 0, 395,
    382, 1, 0, 0, 0, 395, 388, 1, 0, 0, 0, 395, 391, 1, 0, 0, 0, 396, 399, 1, 0, 0, 0, 397, 395, 1, 0, 0, 0, 397, 398,
    1, 0, 0, 0, 398, 61, 1, 0, 0, 0, 399, 397, 1, 0, 0, 0, 400, 405, 3, 2, 1, 0, 401, 402, 5, 31, 0, 0, 402, 404, 3, 2,
    1, 0, 403, 401, 1, 0, 0, 0, 404, 407, 1, 0, 0, 0, 405, 403, 1, 0, 0, 0, 405, 406, 1, 0, 0, 0, 406, 63, 1, 0, 0, 0,
    407, 405, 1, 0, 0, 0, 408, 409, 6, 32, -1, 0, 409, 410, 3, 66, 33, 0, 410, 419, 1, 0, 0, 0, 411, 412, 10, 2, 0, 0,
    412, 413, 5, 43, 0, 0, 413, 418, 3, 66, 33, 0, 414, 415, 10, 1, 0, 0, 415, 416, 5, 44, 0, 0, 416, 418, 3, 66, 33, 0,
    417, 411, 1, 0, 0, 0, 417, 414, 1, 0, 0, 0, 418, 421, 1, 0, 0, 0, 419, 417, 1, 0, 0, 0, 419, 420, 1, 0, 0, 0, 420,
    65, 1, 0, 0, 0, 421, 419, 1, 0, 0, 0, 422, 423, 6, 33, -1, 0, 423, 424, 3, 68, 34, 0, 424, 430, 1, 0, 0, 0, 425,
    426, 10, 1, 0, 0, 426, 427, 7, 2, 0, 0, 427, 429, 3, 68, 34, 0, 428, 425, 1, 0, 0, 0, 429, 432, 1, 0, 0, 0, 430,
    428, 1, 0, 0, 0, 430, 431, 1, 0, 0, 0, 431, 67, 1, 0, 0, 0, 432, 430, 1, 0, 0, 0, 433, 434, 6, 34, -1, 0, 434, 435,
    3, 70, 35, 0, 435, 441, 1, 0, 0, 0, 436, 437, 10, 1, 0, 0, 437, 438, 5, 42, 0, 0, 438, 440, 3, 70, 35, 0, 439, 436,
    1, 0, 0, 0, 440, 443, 1, 0, 0, 0, 441, 439, 1, 0, 0, 0, 441, 442, 1, 0, 0, 0, 442, 69, 1, 0, 0, 0, 443, 441, 1, 0,
    0, 0, 444, 445, 6, 35, -1, 0, 445, 446, 3, 72, 36, 0, 446, 462, 1, 0, 0, 0, 447, 448, 10, 2, 0, 0, 448, 449, 5, 29,
    0, 0, 449, 450, 6, 35, -1, 0, 450, 451, 3, 2, 1, 0, 451, 452, 6, 35, -1, 0, 452, 453, 5, 30, 0, 0, 453, 461, 1, 0,
    0, 0, 454, 455, 10, 1, 0, 0, 455, 456, 5, 33, 0, 0, 456, 457, 6, 35, -1, 0, 457, 458, 3, 102, 51, 0, 458, 459, 6,
    35, -1, 0, 459, 461, 1, 0, 0, 0, 460, 447, 1, 0, 0, 0, 460, 454, 1, 0, 0, 0, 461, 464, 1, 0, 0, 0, 462, 460, 1, 0,
    0, 0, 462, 463, 1, 0, 0, 0, 463, 71, 1, 0, 0, 0, 464, 462, 1, 0, 0, 0, 465, 466, 6, 36, -1, 0, 466, 467, 5, 44, 0,
    0, 467, 472, 3, 72, 36, 3, 468, 472, 3, 74, 37, 0, 469, 470, 5, 43, 0, 0, 470, 472, 3, 74, 37, 0, 471, 465, 1, 0, 0,
    0, 471, 468, 1, 0, 0, 0, 471, 469, 1, 0, 0, 0, 472, 477, 1, 0, 0, 0, 473, 474, 10, 4, 0, 0, 474, 476, 3, 6, 3, 0,
    475, 473, 1, 0, 0, 0, 476, 479, 1, 0, 0, 0, 477, 475, 1, 0, 0, 0, 477, 478, 1, 0, 0, 0, 478, 73, 1, 0, 0, 0, 479,
    477, 1, 0, 0, 0, 480, 489, 3, 76, 38, 0, 481, 482, 5, 33, 0, 0, 482, 483, 6, 37, -1, 0, 483, 485, 3, 102, 51, 0,
    484, 486, 3, 6, 3, 0, 485, 484, 1, 0, 0, 0, 485, 486, 1, 0, 0, 0, 486, 487, 1, 0, 0, 0, 487, 488, 6, 37, -1, 0, 488,
    490, 1, 0, 0, 0, 489, 481, 1, 0, 0, 0, 489, 490, 1, 0, 0, 0, 490, 75, 1, 0, 0, 0, 491, 505, 3, 78, 39, 0, 492, 505,
    3, 14, 7, 0, 493, 505, 3, 22, 11, 0, 494, 505, 3, 20, 10, 0, 495, 505, 3, 100, 50, 0, 496, 505, 3, 26, 13, 0, 497,
    505, 3, 34, 17, 0, 498, 499, 5, 25, 0, 0, 499, 500, 3, 2, 1, 0, 500, 501, 5, 26, 0, 0, 501, 505, 1, 0, 0, 0, 502,
    505, 3, 84, 42, 0, 503, 505, 3, 102, 51, 0, 504, 491, 1, 0, 0, 0, 504, 492, 1, 0, 0, 0, 504, 493, 1, 0, 0, 0, 504,
    494, 1, 0, 0, 0, 504, 495, 1, 0, 0, 0, 504, 496, 1, 0, 0, 0, 504, 497, 1, 0, 0, 0, 504, 498, 1, 0, 0, 0, 504, 502,
    1, 0, 0, 0, 504, 503, 1, 0, 0, 0, 505, 77, 1, 0, 0, 0, 506, 513, 5, 22, 0, 0, 507, 513, 5, 23, 0, 0, 508, 513, 5, 1,
    0, 0, 509, 513, 3, 80, 40, 0, 510, 513, 5, 24, 0, 0, 511, 513, 5, 18, 0, 0, 512, 506, 1, 0, 0, 0, 512, 507, 1, 0, 0,
    0, 512, 508, 1, 0, 0, 0, 512, 509, 1, 0, 0, 0, 512, 510, 1, 0, 0, 0, 512, 511, 1, 0, 0, 0, 513, 79, 1, 0, 0, 0, 514,
    515, 5, 49, 0, 0, 515, 516, 3, 82, 41, 0, 516, 81, 1, 0, 0, 0, 517, 518, 5, 24, 0, 0, 518, 83, 1, 0, 0, 0, 519, 520,
    5, 36, 0, 0, 520, 521, 6, 42, -1, 0, 521, 522, 3, 98, 49, 0, 522, 523, 6, 42, -1, 0, 523, 551, 1, 0, 0, 0, 524, 525,
    5, 35, 0, 0, 525, 526, 6, 42, -1, 0, 526, 527, 3, 98, 49, 0, 527, 528, 6, 42, -1, 0, 528, 551, 1, 0, 0, 0, 529, 530,
    5, 37, 0, 0, 530, 531, 6, 42, -1, 0, 531, 532, 3, 98, 49, 0, 532, 533, 6, 42, -1, 0, 533, 551, 1, 0, 0, 0, 534, 535,
    5, 38, 0, 0, 535, 536, 6, 42, -1, 0, 536, 537, 3, 98, 49, 0, 537, 538, 6, 42, -1, 0, 538, 551, 1, 0, 0, 0, 539, 540,
    5, 34, 0, 0, 540, 541, 6, 42, -1, 0, 541, 542, 3, 98, 49, 0, 542, 543, 6, 42, -1, 0, 543, 551, 1, 0, 0, 0, 544, 545,
    5, 39, 0, 0, 545, 546, 6, 42, -1, 0, 546, 547, 3, 98, 49, 0, 547, 548, 6, 42, -1, 0, 548, 551, 1, 0, 0, 0, 549, 551,
    3, 100, 50, 0, 550, 519, 1, 0, 0, 0, 550, 524, 1, 0, 0, 0, 550, 529, 1, 0, 0, 0, 550, 534, 1, 0, 0, 0, 550, 539, 1,
    0, 0, 0, 550, 544, 1, 0, 0, 0, 550, 549, 1, 0, 0, 0, 551, 85, 1, 0, 0, 0, 552, 557, 3, 84, 42, 0, 553, 554, 5, 31,
    0, 0, 554, 556, 3, 84, 42, 0, 555, 553, 1, 0, 0, 0, 556, 559, 1, 0, 0, 0, 557, 555, 1, 0, 0, 0, 557, 558, 1, 0, 0,
    0, 558, 87, 1, 0, 0, 0, 559, 557, 1, 0, 0, 0, 560, 568, 3, 86, 43, 0, 561, 562, 5, 48, 0, 0, 562, 563, 5, 25, 0, 0,
    563, 564, 3, 86, 43, 0, 564, 565, 5, 26, 0, 0, 565, 568, 1, 0, 0, 0, 566, 568, 5, 44, 0, 0, 567, 560, 1, 0, 0, 0,
    567, 561, 1, 0, 0, 0, 567, 566, 1, 0, 0, 0, 568, 89, 1, 0, 0, 0, 569, 570, 3, 2, 1, 0, 570, 91, 1, 0, 0, 0, 571,
    576, 3, 90, 45, 0, 572, 573, 5, 31, 0, 0, 573, 575, 3, 90, 45, 0, 574, 572, 1, 0, 0, 0, 575, 578, 1, 0, 0, 0, 576,
    574, 1, 0, 0, 0, 576, 577, 1, 0, 0, 0, 577, 93, 1, 0, 0, 0, 578, 576, 1, 0, 0, 0, 579, 580, 3, 96, 48, 0, 580, 581,
    5, 0, 0, 1, 581, 95, 1, 0, 0, 0, 582, 583, 5, 48, 0, 0, 583, 584, 5, 25, 0, 0, 584, 585, 3, 92, 46, 0, 585, 586, 5,
    26, 0, 0, 586, 590, 1, 0, 0, 0, 587, 590, 3, 92, 46, 0, 588, 590, 5, 44, 0, 0, 589, 582, 1, 0, 0, 0, 589, 587, 1, 0,
    0, 0, 589, 588, 1, 0, 0, 0, 590, 97, 1, 0, 0, 0, 591, 592, 3, 64, 32, 0, 592, 99, 1, 0, 0, 0, 593, 594, 5, 25, 0, 0,
    594, 595, 3, 98, 49, 0, 595, 596, 5, 32, 0, 0, 596, 597, 3, 98, 49, 0, 597, 598, 5, 26, 0, 0, 598, 648, 1, 0, 0, 0,
    599, 600, 5, 25, 0, 0, 600, 601, 3, 98, 49, 0, 601, 602, 5, 32, 0, 0, 602, 603, 3, 98, 49, 0, 603, 604, 5, 29, 0, 0,
    604, 648, 1, 0, 0, 0, 605, 606, 5, 25, 0, 0, 606, 607, 3, 98, 49, 0, 607, 608, 5, 32, 0, 0, 608, 609, 3, 98, 49, 0,
    609, 610, 5, 30, 0, 0, 610, 648, 1, 0, 0, 0, 611, 612, 5, 30, 0, 0, 612, 613, 3, 98, 49, 0, 613, 614, 5, 32, 0, 0,
    614, 615, 3, 98, 49, 0, 615, 616, 5, 26, 0, 0, 616, 648, 1, 0, 0, 0, 617, 618, 5, 30, 0, 0, 618, 619, 3, 98, 49, 0,
    619, 620, 5, 32, 0, 0, 620, 621, 3, 98, 49, 0, 621, 622, 5, 29, 0, 0, 622, 648, 1, 0, 0, 0, 623, 624, 5, 30, 0, 0,
    624, 625, 3, 98, 49, 0, 625, 626, 5, 32, 0, 0, 626, 627, 3, 98, 49, 0, 627, 628, 5, 30, 0, 0, 628, 648, 1, 0, 0, 0,
    629, 630, 5, 29, 0, 0, 630, 631, 3, 98, 49, 0, 631, 632, 5, 32, 0, 0, 632, 633, 3, 98, 49, 0, 633, 634, 5, 26, 0, 0,
    634, 648, 1, 0, 0, 0, 635, 636, 5, 29, 0, 0, 636, 637, 3, 98, 49, 0, 637, 638, 5, 32, 0, 0, 638, 639, 3, 98, 49, 0,
    639, 640, 5, 29, 0, 0, 640, 648, 1, 0, 0, 0, 641, 642, 5, 29, 0, 0, 642, 643, 3, 98, 49, 0, 643, 644, 5, 32, 0, 0,
    644, 645, 3, 98, 49, 0, 645, 646, 5, 30, 0, 0, 646, 648, 1, 0, 0, 0, 647, 593, 1, 0, 0, 0, 647, 599, 1, 0, 0, 0,
    647, 605, 1, 0, 0, 0, 647, 611, 1, 0, 0, 0, 647, 617, 1, 0, 0, 0, 647, 623, 1, 0, 0, 0, 647, 629, 1, 0, 0, 0, 647,
    635, 1, 0, 0, 0, 647, 641, 1, 0, 0, 0, 648, 101, 1, 0, 0, 0, 649, 650, 3, 104, 52, 0, 650, 658, 6, 51, -1, 0, 651,
    652, 5, 33, 0, 0, 652, 653, 6, 51, -1, 0, 653, 654, 3, 104, 52, 0, 654, 655, 6, 51, -1, 0, 655, 657, 1, 0, 0, 0,
    656, 651, 1, 0, 0, 0, 657, 660, 1, 0, 0, 0, 658, 656, 1, 0, 0, 0, 658, 659, 1, 0, 0, 0, 659, 103, 1, 0, 0, 0, 660,
    658, 1, 0, 0, 0, 661, 662, 5, 50, 0, 0, 662, 666, 6, 52, -1, 0, 663, 664, 5, 48, 0, 0, 664, 666, 6, 52, -1, 0, 665,
    661, 1, 0, 0, 0, 665, 663, 1, 0, 0, 0, 666, 670, 1, 0, 0, 0, 667, 669, 3, 106, 53, 0, 668, 667, 1, 0, 0, 0, 669,
    672, 1, 0, 0, 0, 670, 668, 1, 0, 0, 0, 670, 671, 1, 0, 0, 0, 671, 105, 1, 0, 0, 0, 672, 670, 1, 0, 0, 0, 673, 674,
    4, 53, 17, 0, 674, 675, 8, 3, 0, 0, 675, 107, 1, 0, 0, 0, 676, 677, 7, 4, 0, 0, 677, 109, 1, 0, 0, 0, 50, 117, 129,
    136, 148, 163, 177, 200, 220, 233, 236, 242, 250, 255, 259, 270, 278, 286, 293, 304, 319, 321, 333, 335, 348, 359,
    370, 395, 397, 405, 417, 419, 430, 441, 460, 462, 471, 477, 485, 489, 504, 512, 550, 557, 567, 576, 589, 647, 658,
    665, 670,
  ];

  private static __ATN: ATN;
  public static get _ATN(): ATN {
    if (!FEEL_1_1Parser.__ATN) {
      FEEL_1_1Parser.__ATN = new ATNDeserializer().deserialize(FEEL_1_1Parser._serializedATN);
    }

    return FEEL_1_1Parser.__ATN;
  }

  static DecisionsToDFA = FEEL_1_1Parser._ATN.decisionToState.map(
    (ds: DecisionState, index: number) => new DFA(ds, index)
  );
}

export class Compilation_unitContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public EOF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EOF, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_compilation_unit;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCompilation_unit) {
      listener.enterCompilation_unit(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCompilation_unit) {
      listener.exitCompilation_unit(this);
    }
  }
}

export class ExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_expression;
  }
  public copyFrom(ctx: ExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class ExpressionTextualContext extends ExpressionContext {
  public _expr!: TextualExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: ExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public textualExpression(): TextualExpressionContext {
    return this.getTypedRuleContext(TextualExpressionContext, 0) as TextualExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterExpressionTextual) {
      listener.enterExpressionTextual(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitExpressionTextual) {
      listener.exitExpressionTextual(this);
    }
  }
}

export class TextualExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public functionDefinition(): FunctionDefinitionContext {
    return this.getTypedRuleContext(FunctionDefinitionContext, 0) as FunctionDefinitionContext;
  }
  public conditionalOrExpression(): ConditionalOrExpressionContext {
    return this.getTypedRuleContext(ConditionalOrExpressionContext, 0) as ConditionalOrExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_textualExpression;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterTextualExpression) {
      listener.enterTextualExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitTextualExpression) {
      listener.exitTextualExpression(this);
    }
  }
}

export class ParametersContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_parameters;
  }
  public copyFrom(ctx: ParametersContext): void {
    super.copyFrom(ctx);
  }
}
export class ParametersNamedContext extends ParametersContext {
  constructor(parser: FEEL_1_1Parser, ctx: ParametersContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public namedParameters(): NamedParametersContext {
    return this.getTypedRuleContext(NamedParametersContext, 0) as NamedParametersContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterParametersNamed) {
      listener.enterParametersNamed(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitParametersNamed) {
      listener.exitParametersNamed(this);
    }
  }
}
export class ParametersEmptyContext extends ParametersContext {
  constructor(parser: FEEL_1_1Parser, ctx: ParametersContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterParametersEmpty) {
      listener.enterParametersEmpty(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitParametersEmpty) {
      listener.exitParametersEmpty(this);
    }
  }
}
export class ParametersPositionalContext extends ParametersContext {
  constructor(parser: FEEL_1_1Parser, ctx: ParametersContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public positionalParameters(): PositionalParametersContext {
    return this.getTypedRuleContext(PositionalParametersContext, 0) as PositionalParametersContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterParametersPositional) {
      listener.enterParametersPositional(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitParametersPositional) {
      listener.exitParametersPositional(this);
    }
  }
}

export class NamedParametersContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public namedParameter_list(): NamedParameterContext[] {
    return this.getTypedRuleContexts(NamedParameterContext) as NamedParameterContext[];
  }
  public namedParameter(i: number): NamedParameterContext {
    return this.getTypedRuleContext(NamedParameterContext, i) as NamedParameterContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_namedParameters;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNamedParameters) {
      listener.enterNamedParameters(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNamedParameters) {
      listener.exitNamedParameters(this);
    }
  }
}

export class NamedParameterContext extends ParserRuleContext {
  public _name!: NameDefinitionContext;
  public _value!: ExpressionContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public COLON(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COLON, 0);
  }
  public nameDefinition(): NameDefinitionContext {
    return this.getTypedRuleContext(NameDefinitionContext, 0) as NameDefinitionContext;
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_namedParameter;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNamedParameter) {
      listener.enterNamedParameter(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNamedParameter) {
      listener.exitNamedParameter(this);
    }
  }
}

export class PositionalParametersContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public expression_list(): ExpressionContext[] {
    return this.getTypedRuleContexts(ExpressionContext) as ExpressionContext[];
  }
  public expression(i: number): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, i) as ExpressionContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_positionalParameters;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositionalParameters) {
      listener.enterPositionalParameters(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositionalParameters) {
      listener.exitPositionalParameters(this);
    }
  }
}

export class ForExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public FOR(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FOR, 0);
  }
  public iterationContexts(): IterationContextsContext {
    return this.getTypedRuleContext(IterationContextsContext, 0) as IterationContextsContext;
  }
  public RETURN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RETURN, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_forExpression;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterForExpression) {
      listener.enterForExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitForExpression) {
      listener.exitForExpression(this);
    }
  }
}

export class IterationContextsContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public iterationContext_list(): IterationContextContext[] {
    return this.getTypedRuleContexts(IterationContextContext) as IterationContextContext[];
  }
  public iterationContext(i: number): IterationContextContext {
    return this.getTypedRuleContext(IterationContextContext, i) as IterationContextContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_iterationContexts;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterIterationContexts) {
      listener.enterIterationContexts(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitIterationContexts) {
      listener.exitIterationContexts(this);
    }
  }
}

export class IterationContextContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public iterationNameDefinition(): IterationNameDefinitionContext {
    return this.getTypedRuleContext(IterationNameDefinitionContext, 0) as IterationNameDefinitionContext;
  }
  public IN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IN, 0);
  }
  public expression_list(): ExpressionContext[] {
    return this.getTypedRuleContexts(ExpressionContext) as ExpressionContext[];
  }
  public expression(i: number): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, i) as ExpressionContext;
  }
  public ELIPSIS(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ELIPSIS, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_iterationContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterIterationContext) {
      listener.enterIterationContext(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitIterationContext) {
      listener.exitIterationContext(this);
    }
  }
}

export class IfExpressionContext extends ParserRuleContext {
  public _c!: ExpressionContext;
  public _t!: ExpressionContext;
  public _e!: ExpressionContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public IF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IF, 0);
  }
  public THEN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.THEN, 0);
  }
  public ELSE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ELSE, 0);
  }
  public expression_list(): ExpressionContext[] {
    return this.getTypedRuleContexts(ExpressionContext) as ExpressionContext[];
  }
  public expression(i: number): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, i) as ExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_ifExpression;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterIfExpression) {
      listener.enterIfExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitIfExpression) {
      listener.exitIfExpression(this);
    }
  }
}

export class QuantifiedExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_quantifiedExpression;
  }
  public copyFrom(ctx: QuantifiedExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class QuantExprSomeContext extends QuantifiedExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: QuantifiedExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public SOME(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SOME, 0);
  }
  public iterationContexts(): IterationContextsContext {
    return this.getTypedRuleContext(IterationContextsContext, 0) as IterationContextsContext;
  }
  public SATISFIES(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SATISFIES, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterQuantExprSome) {
      listener.enterQuantExprSome(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitQuantExprSome) {
      listener.exitQuantExprSome(this);
    }
  }
}
export class QuantExprEveryContext extends QuantifiedExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: QuantifiedExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public EVERY(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EVERY, 0);
  }
  public iterationContexts(): IterationContextsContext {
    return this.getTypedRuleContext(IterationContextsContext, 0) as IterationContextsContext;
  }
  public SATISFIES(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SATISFIES, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterQuantExprEvery) {
      listener.enterQuantExprEvery(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitQuantExprEvery) {
      listener.exitQuantExprEvery(this);
    }
  }
}

export class TypeContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_type;
  }
  public copyFrom(ctx: TypeContext): void {
    super.copyFrom(ctx);
  }
}
export class ContextTypeContext extends TypeContext {
  public _sk!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: TypeContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public LT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LT, 0);
  }
  public Identifier_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.Identifier);
  }
  public Identifier(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.Identifier, i);
  }
  public COLON_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COLON);
  }
  public COLON(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COLON, i);
  }
  public type__list(): TypeContext[] {
    return this.getTypedRuleContexts(TypeContext) as TypeContext[];
  }
  public type_(i: number): TypeContext {
    return this.getTypedRuleContext(TypeContext, i) as TypeContext;
  }
  public GT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GT, 0);
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterContextType) {
      listener.enterContextType(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitContextType) {
      listener.exitContextType(this);
    }
  }
}
export class FunctionTypeContext extends TypeContext {
  constructor(parser: FEEL_1_1Parser, ctx: TypeContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public FUNCTION(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FUNCTION, 0);
  }
  public LT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LT, 0);
  }
  public GT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GT, 0);
  }
  public RARROW(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RARROW, 0);
  }
  public type__list(): TypeContext[] {
    return this.getTypedRuleContexts(TypeContext) as TypeContext[];
  }
  public type_(i: number): TypeContext {
    return this.getTypedRuleContext(TypeContext, i) as TypeContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterFunctionType) {
      listener.enterFunctionType(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitFunctionType) {
      listener.exitFunctionType(this);
    }
  }
}
export class ListTypeContext extends TypeContext {
  public _sk!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: TypeContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public LT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LT, 0);
  }
  public type_(): TypeContext {
    return this.getTypedRuleContext(TypeContext, 0) as TypeContext;
  }
  public GT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GT, 0);
  }
  public Identifier(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.Identifier, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterListType) {
      listener.enterListType(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitListType) {
      listener.exitListType(this);
    }
  }
}
export class QnTypeContext extends TypeContext {
  constructor(parser: FEEL_1_1Parser, ctx: TypeContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public FUNCTION(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FUNCTION, 0);
  }
  public qualifiedName(): QualifiedNameContext {
    return this.getTypedRuleContext(QualifiedNameContext, 0) as QualifiedNameContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterQnType) {
      listener.enterQnType(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitQnType) {
      listener.exitQnType(this);
    }
  }
}

export class ListContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public LBRACK(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LBRACK, 0);
  }
  public RBRACK(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RBRACK, 0);
  }
  public expressionList(): ExpressionListContext {
    return this.getTypedRuleContext(ExpressionListContext, 0) as ExpressionListContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_list;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterList) {
      listener.enterList(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitList) {
      listener.exitList(this);
    }
  }
}

export class FunctionDefinitionContext extends ParserRuleContext {
  public _external!: Token;
  public _body!: ExpressionContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public FUNCTION(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FUNCTION, 0);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public formalParameters(): FormalParametersContext {
    return this.getTypedRuleContext(FormalParametersContext, 0) as FormalParametersContext;
  }
  public EXTERNAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EXTERNAL, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_functionDefinition;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterFunctionDefinition) {
      listener.enterFunctionDefinition(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitFunctionDefinition) {
      listener.exitFunctionDefinition(this);
    }
  }
}

export class FormalParametersContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public formalParameter_list(): FormalParameterContext[] {
    return this.getTypedRuleContexts(FormalParameterContext) as FormalParameterContext[];
  }
  public formalParameter(i: number): FormalParameterContext {
    return this.getTypedRuleContext(FormalParameterContext, i) as FormalParameterContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_formalParameters;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterFormalParameters) {
      listener.enterFormalParameters(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitFormalParameters) {
      listener.exitFormalParameters(this);
    }
  }
}

export class FormalParameterContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public nameDefinition(): NameDefinitionContext {
    return this.getTypedRuleContext(NameDefinitionContext, 0) as NameDefinitionContext;
  }
  public COLON(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COLON, 0);
  }
  public type_(): TypeContext {
    return this.getTypedRuleContext(TypeContext, 0) as TypeContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_formalParameter;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterFormalParameter) {
      listener.enterFormalParameter(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitFormalParameter) {
      listener.exitFormalParameter(this);
    }
  }
}

export class ContextContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public LBRACE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LBRACE, 0);
  }
  public RBRACE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RBRACE, 0);
  }
  public contextEntries(): ContextEntriesContext {
    return this.getTypedRuleContext(ContextEntriesContext, 0) as ContextEntriesContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_context;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterContext) {
      listener.enterContext(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitContext) {
      listener.exitContext(this);
    }
  }
}

export class ContextEntriesContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public contextEntry_list(): ContextEntryContext[] {
    return this.getTypedRuleContexts(ContextEntryContext) as ContextEntryContext[];
  }
  public contextEntry(i: number): ContextEntryContext {
    return this.getTypedRuleContext(ContextEntryContext, i) as ContextEntryContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_contextEntries;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterContextEntries) {
      listener.enterContextEntries(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitContextEntries) {
      listener.exitContextEntries(this);
    }
  }
}

export class ContextEntryContext extends ParserRuleContext {
  public _key!: KeyContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public key(): KeyContext {
    return this.getTypedRuleContext(KeyContext, 0) as KeyContext;
  }
  public COLON(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COLON, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_contextEntry;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterContextEntry) {
      listener.enterContextEntry(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitContextEntry) {
      listener.exitContextEntry(this);
    }
  }
}

export class KeyContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_key;
  }
  public copyFrom(ctx: KeyContext): void {
    super.copyFrom(ctx);
  }
}
export class KeyNameContext extends KeyContext {
  constructor(parser: FEEL_1_1Parser, ctx: KeyContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public nameDefinition(): NameDefinitionContext {
    return this.getTypedRuleContext(NameDefinitionContext, 0) as NameDefinitionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterKeyName) {
      listener.enterKeyName(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitKeyName) {
      listener.exitKeyName(this);
    }
  }
}
export class KeyStringContext extends KeyContext {
  constructor(parser: FEEL_1_1Parser, ctx: KeyContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public StringLiteral(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.StringLiteral, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterKeyString) {
      listener.enterKeyString(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitKeyString) {
      listener.exitKeyString(this);
    }
  }
}

export class NameDefinitionContext extends ParserRuleContext {
  public _nameDefinitionTokens!: NameDefinitionTokensContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public nameDefinitionTokens(): NameDefinitionTokensContext {
    return this.getTypedRuleContext(NameDefinitionTokensContext, 0) as NameDefinitionTokensContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_nameDefinition;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNameDefinition) {
      listener.enterNameDefinition(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNameDefinition) {
      listener.exitNameDefinition(this);
    }
  }
}

export class NameDefinitionWithEOFContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public nameDefinition(): NameDefinitionContext {
    return this.getTypedRuleContext(NameDefinitionContext, 0) as NameDefinitionContext;
  }
  public EOF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EOF, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_nameDefinitionWithEOF;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNameDefinitionWithEOF) {
      listener.enterNameDefinitionWithEOF(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNameDefinitionWithEOF) {
      listener.exitNameDefinitionWithEOF(this);
    }
  }
}

export class NameDefinitionTokensContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public Identifier_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.Identifier);
  }
  public Identifier(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.Identifier, i);
  }
  public additionalNameSymbol_list(): AdditionalNameSymbolContext[] {
    return this.getTypedRuleContexts(AdditionalNameSymbolContext) as AdditionalNameSymbolContext[];
  }
  public additionalNameSymbol(i: number): AdditionalNameSymbolContext {
    return this.getTypedRuleContext(AdditionalNameSymbolContext, i) as AdditionalNameSymbolContext;
  }
  public IntegerLiteral_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.IntegerLiteral);
  }
  public IntegerLiteral(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IntegerLiteral, i);
  }
  public FloatingPointLiteral_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.FloatingPointLiteral);
  }
  public FloatingPointLiteral(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FloatingPointLiteral, i);
  }
  public reusableKeywords_list(): ReusableKeywordsContext[] {
    return this.getTypedRuleContexts(ReusableKeywordsContext) as ReusableKeywordsContext[];
  }
  public reusableKeywords(i: number): ReusableKeywordsContext {
    return this.getTypedRuleContext(ReusableKeywordsContext, i) as ReusableKeywordsContext;
  }
  public IN_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.IN);
  }
  public IN(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IN, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_nameDefinitionTokens;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNameDefinitionTokens) {
      listener.enterNameDefinitionTokens(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNameDefinitionTokens) {
      listener.exitNameDefinitionTokens(this);
    }
  }
}

export class IterationNameDefinitionContext extends ParserRuleContext {
  public _iterationNameDefinitionTokens!: IterationNameDefinitionTokensContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public iterationNameDefinitionTokens(): IterationNameDefinitionTokensContext {
    return this.getTypedRuleContext(IterationNameDefinitionTokensContext, 0) as IterationNameDefinitionTokensContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_iterationNameDefinition;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterIterationNameDefinition) {
      listener.enterIterationNameDefinition(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitIterationNameDefinition) {
      listener.exitIterationNameDefinition(this);
    }
  }
}

export class IterationNameDefinitionTokensContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public Identifier_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.Identifier);
  }
  public Identifier(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.Identifier, i);
  }
  public additionalNameSymbol_list(): AdditionalNameSymbolContext[] {
    return this.getTypedRuleContexts(AdditionalNameSymbolContext) as AdditionalNameSymbolContext[];
  }
  public additionalNameSymbol(i: number): AdditionalNameSymbolContext {
    return this.getTypedRuleContext(AdditionalNameSymbolContext, i) as AdditionalNameSymbolContext;
  }
  public IntegerLiteral_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.IntegerLiteral);
  }
  public IntegerLiteral(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IntegerLiteral, i);
  }
  public FloatingPointLiteral_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.FloatingPointLiteral);
  }
  public FloatingPointLiteral(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FloatingPointLiteral, i);
  }
  public reusableKeywords_list(): ReusableKeywordsContext[] {
    return this.getTypedRuleContexts(ReusableKeywordsContext) as ReusableKeywordsContext[];
  }
  public reusableKeywords(i: number): ReusableKeywordsContext {
    return this.getTypedRuleContext(ReusableKeywordsContext, i) as ReusableKeywordsContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_iterationNameDefinitionTokens;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterIterationNameDefinitionTokens) {
      listener.enterIterationNameDefinitionTokens(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitIterationNameDefinitionTokens) {
      listener.exitIterationNameDefinitionTokens(this);
    }
  }
}

export class AdditionalNameSymbolContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public DOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.DOT, 0);
  }
  public DIV(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.DIV, 0);
  }
  public SUB(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SUB, 0);
  }
  public ADD(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ADD, 0);
  }
  public MUL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.MUL, 0);
  }
  public QUOTE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.QUOTE, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_additionalNameSymbol;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterAdditionalNameSymbol) {
      listener.enterAdditionalNameSymbol(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitAdditionalNameSymbol) {
      listener.exitAdditionalNameSymbol(this);
    }
  }
}

export class ConditionalOrExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_conditionalOrExpression;
  }
  public copyFrom(ctx: ConditionalOrExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class CondOrContext extends ConditionalOrExpressionContext {
  public _left!: ConditionalOrExpressionContext;
  public _op!: Token;
  public _right!: ConditionalAndExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: ConditionalOrExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public conditionalOrExpression(): ConditionalOrExpressionContext {
    return this.getTypedRuleContext(ConditionalOrExpressionContext, 0) as ConditionalOrExpressionContext;
  }
  public OR(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.OR, 0);
  }
  public conditionalAndExpression(): ConditionalAndExpressionContext {
    return this.getTypedRuleContext(ConditionalAndExpressionContext, 0) as ConditionalAndExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCondOr) {
      listener.enterCondOr(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCondOr) {
      listener.exitCondOr(this);
    }
  }
}
export class CondOrAndContext extends ConditionalOrExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: ConditionalOrExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public conditionalAndExpression(): ConditionalAndExpressionContext {
    return this.getTypedRuleContext(ConditionalAndExpressionContext, 0) as ConditionalAndExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCondOrAnd) {
      listener.enterCondOrAnd(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCondOrAnd) {
      listener.exitCondOrAnd(this);
    }
  }
}

export class ConditionalAndExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_conditionalAndExpression;
  }
  public copyFrom(ctx: ConditionalAndExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class CondAndCompContext extends ConditionalAndExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: ConditionalAndExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public comparisonExpression(): ComparisonExpressionContext {
    return this.getTypedRuleContext(ComparisonExpressionContext, 0) as ComparisonExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCondAndComp) {
      listener.enterCondAndComp(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCondAndComp) {
      listener.exitCondAndComp(this);
    }
  }
}
export class CondAndContext extends ConditionalAndExpressionContext {
  public _left!: ConditionalAndExpressionContext;
  public _op!: Token;
  public _right!: ComparisonExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: ConditionalAndExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public conditionalAndExpression(): ConditionalAndExpressionContext {
    return this.getTypedRuleContext(ConditionalAndExpressionContext, 0) as ConditionalAndExpressionContext;
  }
  public AND(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.AND, 0);
  }
  public comparisonExpression(): ComparisonExpressionContext {
    return this.getTypedRuleContext(ComparisonExpressionContext, 0) as ComparisonExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCondAnd) {
      listener.enterCondAnd(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCondAnd) {
      listener.exitCondAnd(this);
    }
  }
}

export class ComparisonExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_comparisonExpression;
  }
  public copyFrom(ctx: ComparisonExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class CompExpressionContext extends ComparisonExpressionContext {
  public _left!: ComparisonExpressionContext;
  public _op!: Token;
  public _right!: RelationalExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: ComparisonExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public comparisonExpression(): ComparisonExpressionContext {
    return this.getTypedRuleContext(ComparisonExpressionContext, 0) as ComparisonExpressionContext;
  }
  public relationalExpression(): RelationalExpressionContext {
    return this.getTypedRuleContext(RelationalExpressionContext, 0) as RelationalExpressionContext;
  }
  public LT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LT, 0);
  }
  public GT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GT, 0);
  }
  public LE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LE, 0);
  }
  public GE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GE, 0);
  }
  public EQUAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EQUAL, 0);
  }
  public NOTEQUAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NOTEQUAL, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCompExpression) {
      listener.enterCompExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCompExpression) {
      listener.exitCompExpression(this);
    }
  }
}
export class CompExpressionRelContext extends ComparisonExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: ComparisonExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public relationalExpression(): RelationalExpressionContext {
    return this.getTypedRuleContext(RelationalExpressionContext, 0) as RelationalExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterCompExpressionRel) {
      listener.enterCompExpressionRel(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitCompExpressionRel) {
      listener.exitCompExpressionRel(this);
    }
  }
}

export class RelationalExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_relationalExpression;
  }
  public copyFrom(ctx: RelationalExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class RelExpressionBetweenContext extends RelationalExpressionContext {
  public _val!: RelationalExpressionContext;
  public _start!: AdditiveExpressionContext;
  public _end!: AdditiveExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: RelationalExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public BETWEEN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.BETWEEN, 0);
  }
  public AND(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.AND, 0);
  }
  public relationalExpression(): RelationalExpressionContext {
    return this.getTypedRuleContext(RelationalExpressionContext, 0) as RelationalExpressionContext;
  }
  public additiveExpression_list(): AdditiveExpressionContext[] {
    return this.getTypedRuleContexts(AdditiveExpressionContext) as AdditiveExpressionContext[];
  }
  public additiveExpression(i: number): AdditiveExpressionContext {
    return this.getTypedRuleContext(AdditiveExpressionContext, i) as AdditiveExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterRelExpressionBetween) {
      listener.enterRelExpressionBetween(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitRelExpressionBetween) {
      listener.exitRelExpressionBetween(this);
    }
  }
}
export class RelExpressionValueContext extends RelationalExpressionContext {
  public _val!: RelationalExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: RelationalExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public IN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IN, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public relationalExpression(): RelationalExpressionContext {
    return this.getTypedRuleContext(RelationalExpressionContext, 0) as RelationalExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterRelExpressionValue) {
      listener.enterRelExpressionValue(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitRelExpressionValue) {
      listener.exitRelExpressionValue(this);
    }
  }
}
export class RelExpressionTestListContext extends RelationalExpressionContext {
  public _val!: RelationalExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: RelationalExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public IN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IN, 0);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public positiveUnaryTests(): PositiveUnaryTestsContext {
    return this.getTypedRuleContext(PositiveUnaryTestsContext, 0) as PositiveUnaryTestsContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public relationalExpression(): RelationalExpressionContext {
    return this.getTypedRuleContext(RelationalExpressionContext, 0) as RelationalExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterRelExpressionTestList) {
      listener.enterRelExpressionTestList(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitRelExpressionTestList) {
      listener.exitRelExpressionTestList(this);
    }
  }
}
export class RelExpressionAddContext extends RelationalExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: RelationalExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public additiveExpression(): AdditiveExpressionContext {
    return this.getTypedRuleContext(AdditiveExpressionContext, 0) as AdditiveExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterRelExpressionAdd) {
      listener.enterRelExpressionAdd(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitRelExpressionAdd) {
      listener.exitRelExpressionAdd(this);
    }
  }
}
export class RelExpressionInstanceOfContext extends RelationalExpressionContext {
  public _val!: RelationalExpressionContext;
  constructor(parser: FEEL_1_1Parser, ctx: RelationalExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public INSTANCE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.INSTANCE, 0);
  }
  public OF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.OF, 0);
  }
  public type_(): TypeContext {
    return this.getTypedRuleContext(TypeContext, 0) as TypeContext;
  }
  public relationalExpression(): RelationalExpressionContext {
    return this.getTypedRuleContext(RelationalExpressionContext, 0) as RelationalExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterRelExpressionInstanceOf) {
      listener.enterRelExpressionInstanceOf(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitRelExpressionInstanceOf) {
      listener.exitRelExpressionInstanceOf(this);
    }
  }
}

export class ExpressionListContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public expression_list(): ExpressionContext[] {
    return this.getTypedRuleContexts(ExpressionContext) as ExpressionContext[];
  }
  public expression(i: number): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, i) as ExpressionContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_expressionList;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterExpressionList) {
      listener.enterExpressionList(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitExpressionList) {
      listener.exitExpressionList(this);
    }
  }
}

export class AdditiveExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_additiveExpression;
  }
  public copyFrom(ctx: AdditiveExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class AddExpressionMultContext extends AdditiveExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: AdditiveExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public multiplicativeExpression(): MultiplicativeExpressionContext {
    return this.getTypedRuleContext(MultiplicativeExpressionContext, 0) as MultiplicativeExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterAddExpressionMult) {
      listener.enterAddExpressionMult(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitAddExpressionMult) {
      listener.exitAddExpressionMult(this);
    }
  }
}
export class AddExpressionContext extends AdditiveExpressionContext {
  public _op!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: AdditiveExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public additiveExpression(): AdditiveExpressionContext {
    return this.getTypedRuleContext(AdditiveExpressionContext, 0) as AdditiveExpressionContext;
  }
  public multiplicativeExpression(): MultiplicativeExpressionContext {
    return this.getTypedRuleContext(MultiplicativeExpressionContext, 0) as MultiplicativeExpressionContext;
  }
  public ADD(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ADD, 0);
  }
  public SUB(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SUB, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterAddExpression) {
      listener.enterAddExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitAddExpression) {
      listener.exitAddExpression(this);
    }
  }
}

export class MultiplicativeExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_multiplicativeExpression;
  }
  public copyFrom(ctx: MultiplicativeExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class MultExpressionPowContext extends MultiplicativeExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: MultiplicativeExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public powerExpression(): PowerExpressionContext {
    return this.getTypedRuleContext(PowerExpressionContext, 0) as PowerExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterMultExpressionPow) {
      listener.enterMultExpressionPow(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitMultExpressionPow) {
      listener.exitMultExpressionPow(this);
    }
  }
}
export class MultExpressionContext extends MultiplicativeExpressionContext {
  public _op!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: MultiplicativeExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public multiplicativeExpression(): MultiplicativeExpressionContext {
    return this.getTypedRuleContext(MultiplicativeExpressionContext, 0) as MultiplicativeExpressionContext;
  }
  public powerExpression(): PowerExpressionContext {
    return this.getTypedRuleContext(PowerExpressionContext, 0) as PowerExpressionContext;
  }
  public MUL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.MUL, 0);
  }
  public DIV(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.DIV, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterMultExpression) {
      listener.enterMultExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitMultExpression) {
      listener.exitMultExpression(this);
    }
  }
}

export class PowerExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_powerExpression;
  }
  public copyFrom(ctx: PowerExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class PowExpressionUnaryContext extends PowerExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: PowerExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public filterPathExpression(): FilterPathExpressionContext {
    return this.getTypedRuleContext(FilterPathExpressionContext, 0) as FilterPathExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPowExpressionUnary) {
      listener.enterPowExpressionUnary(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPowExpressionUnary) {
      listener.exitPowExpressionUnary(this);
    }
  }
}
export class PowExpressionContext extends PowerExpressionContext {
  public _op!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: PowerExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public powerExpression(): PowerExpressionContext {
    return this.getTypedRuleContext(PowerExpressionContext, 0) as PowerExpressionContext;
  }
  public filterPathExpression(): FilterPathExpressionContext {
    return this.getTypedRuleContext(FilterPathExpressionContext, 0) as FilterPathExpressionContext;
  }
  public POW(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.POW, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPowExpression) {
      listener.enterPowExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPowExpression) {
      listener.exitPowExpression(this);
    }
  }
}

export class FilterPathExpressionContext extends ParserRuleContext {
  public _n0!: FilterPathExpressionContext;
  public _n1!: FilterPathExpressionContext;
  public _filter!: ExpressionContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public unaryExpression(): UnaryExpressionContext {
    return this.getTypedRuleContext(UnaryExpressionContext, 0) as UnaryExpressionContext;
  }
  public LBRACK(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LBRACK, 0);
  }
  public RBRACK(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RBRACK, 0);
  }
  public filterPathExpression(): FilterPathExpressionContext {
    return this.getTypedRuleContext(FilterPathExpressionContext, 0) as FilterPathExpressionContext;
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public DOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.DOT, 0);
  }
  public qualifiedName(): QualifiedNameContext {
    return this.getTypedRuleContext(QualifiedNameContext, 0) as QualifiedNameContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_filterPathExpression;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterFilterPathExpression) {
      listener.enterFilterPathExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitFilterPathExpression) {
      listener.exitFilterPathExpression(this);
    }
  }
}

export class UnaryExpressionContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_unaryExpression;
  }
  public copyFrom(ctx: UnaryExpressionContext): void {
    super.copyFrom(ctx);
  }
}
export class SignedUnaryExpressionPlusContext extends UnaryExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public ADD(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ADD, 0);
  }
  public unaryExpressionNotPlusMinus(): UnaryExpressionNotPlusMinusContext {
    return this.getTypedRuleContext(UnaryExpressionNotPlusMinusContext, 0) as UnaryExpressionNotPlusMinusContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterSignedUnaryExpressionPlus) {
      listener.enterSignedUnaryExpressionPlus(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitSignedUnaryExpressionPlus) {
      listener.exitSignedUnaryExpressionPlus(this);
    }
  }
}
export class SignedUnaryExpressionMinusContext extends UnaryExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public SUB(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SUB, 0);
  }
  public unaryExpression(): UnaryExpressionContext {
    return this.getTypedRuleContext(UnaryExpressionContext, 0) as UnaryExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterSignedUnaryExpressionMinus) {
      listener.enterSignedUnaryExpressionMinus(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitSignedUnaryExpressionMinus) {
      listener.exitSignedUnaryExpressionMinus(this);
    }
  }
}
export class FnInvocationContext extends UnaryExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public unaryExpression(): UnaryExpressionContext {
    return this.getTypedRuleContext(UnaryExpressionContext, 0) as UnaryExpressionContext;
  }
  public parameters(): ParametersContext {
    return this.getTypedRuleContext(ParametersContext, 0) as ParametersContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterFnInvocation) {
      listener.enterFnInvocation(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitFnInvocation) {
      listener.exitFnInvocation(this);
    }
  }
}
export class NonSignedUnaryExpressionContext extends UnaryExpressionContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryExpressionContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public unaryExpressionNotPlusMinus(): UnaryExpressionNotPlusMinusContext {
    return this.getTypedRuleContext(UnaryExpressionNotPlusMinusContext, 0) as UnaryExpressionNotPlusMinusContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNonSignedUnaryExpression) {
      listener.enterNonSignedUnaryExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNonSignedUnaryExpression) {
      listener.exitNonSignedUnaryExpression(this);
    }
  }
}

export class UnaryExpressionNotPlusMinusContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_unaryExpressionNotPlusMinus;
  }
  public copyFrom(ctx: UnaryExpressionNotPlusMinusContext): void {
    super.copyFrom(ctx);
  }
}
export class UenpmPrimaryContext extends UnaryExpressionNotPlusMinusContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryExpressionNotPlusMinusContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public primary(): PrimaryContext {
    return this.getTypedRuleContext(PrimaryContext, 0) as PrimaryContext;
  }
  public DOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.DOT, 0);
  }
  public qualifiedName(): QualifiedNameContext {
    return this.getTypedRuleContext(QualifiedNameContext, 0) as QualifiedNameContext;
  }
  public parameters(): ParametersContext {
    return this.getTypedRuleContext(ParametersContext, 0) as ParametersContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterUenpmPrimary) {
      listener.enterUenpmPrimary(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitUenpmPrimary) {
      listener.exitUenpmPrimary(this);
    }
  }
}

export class PrimaryContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_primary;
  }
  public copyFrom(ctx: PrimaryContext): void {
    super.copyFrom(ctx);
  }
}
export class PrimaryQuantifiedExpressionContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public quantifiedExpression(): QuantifiedExpressionContext {
    return this.getTypedRuleContext(QuantifiedExpressionContext, 0) as QuantifiedExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryQuantifiedExpression) {
      listener.enterPrimaryQuantifiedExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryQuantifiedExpression) {
      listener.exitPrimaryQuantifiedExpression(this);
    }
  }
}
export class PrimaryContextContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public context(): ContextContext {
    return this.getTypedRuleContext(ContextContext, 0) as ContextContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryContext) {
      listener.enterPrimaryContext(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryContext) {
      listener.exitPrimaryContext(this);
    }
  }
}
export class PrimaryIfExpressionContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public ifExpression(): IfExpressionContext {
    return this.getTypedRuleContext(IfExpressionContext, 0) as IfExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryIfExpression) {
      listener.enterPrimaryIfExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryIfExpression) {
      listener.exitPrimaryIfExpression(this);
    }
  }
}
export class PrimaryIntervalContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public interval(): IntervalContext {
    return this.getTypedRuleContext(IntervalContext, 0) as IntervalContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryInterval) {
      listener.enterPrimaryInterval(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryInterval) {
      listener.exitPrimaryInterval(this);
    }
  }
}
export class PrimaryListContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public list(): ListContext {
    return this.getTypedRuleContext(ListContext, 0) as ListContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryList) {
      listener.enterPrimaryList(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryList) {
      listener.exitPrimaryList(this);
    }
  }
}
export class PrimaryUnaryTestContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public simplePositiveUnaryTest(): SimplePositiveUnaryTestContext {
    return this.getTypedRuleContext(SimplePositiveUnaryTestContext, 0) as SimplePositiveUnaryTestContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryUnaryTest) {
      listener.enterPrimaryUnaryTest(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryUnaryTest) {
      listener.exitPrimaryUnaryTest(this);
    }
  }
}
export class PrimaryNameContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public qualifiedName(): QualifiedNameContext {
    return this.getTypedRuleContext(QualifiedNameContext, 0) as QualifiedNameContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryName) {
      listener.enterPrimaryName(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryName) {
      listener.exitPrimaryName(this);
    }
  }
}
export class PrimaryForExpressionContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public forExpression(): ForExpressionContext {
    return this.getTypedRuleContext(ForExpressionContext, 0) as ForExpressionContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryForExpression) {
      listener.enterPrimaryForExpression(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryForExpression) {
      listener.exitPrimaryForExpression(this);
    }
  }
}
export class PrimaryParensContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryParens) {
      listener.enterPrimaryParens(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryParens) {
      listener.exitPrimaryParens(this);
    }
  }
}
export class PrimaryLiteralContext extends PrimaryContext {
  constructor(parser: FEEL_1_1Parser, ctx: PrimaryContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public literal(): LiteralContext {
    return this.getTypedRuleContext(LiteralContext, 0) as LiteralContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPrimaryLiteral) {
      listener.enterPrimaryLiteral(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPrimaryLiteral) {
      listener.exitPrimaryLiteral(this);
    }
  }
}

export class LiteralContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_literal;
  }
  public copyFrom(ctx: LiteralContext): void {
    super.copyFrom(ctx);
  }
}
export class NullLiteralContext extends LiteralContext {
  constructor(parser: FEEL_1_1Parser, ctx: LiteralContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public NULL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NULL, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNullLiteral) {
      listener.enterNullLiteral(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNullLiteral) {
      listener.exitNullLiteral(this);
    }
  }
}
export class StringLiteralContext extends LiteralContext {
  constructor(parser: FEEL_1_1Parser, ctx: LiteralContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public StringLiteral(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.StringLiteral, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterStringLiteral) {
      listener.enterStringLiteral(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitStringLiteral) {
      listener.exitStringLiteral(this);
    }
  }
}
export class AtLiteralLabelContext extends LiteralContext {
  constructor(parser: FEEL_1_1Parser, ctx: LiteralContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public atLiteral(): AtLiteralContext {
    return this.getTypedRuleContext(AtLiteralContext, 0) as AtLiteralContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterAtLiteralLabel) {
      listener.enterAtLiteralLabel(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitAtLiteralLabel) {
      listener.exitAtLiteralLabel(this);
    }
  }
}
export class BoolLiteralContext extends LiteralContext {
  constructor(parser: FEEL_1_1Parser, ctx: LiteralContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public BooleanLiteral(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.BooleanLiteral, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterBoolLiteral) {
      listener.enterBoolLiteral(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitBoolLiteral) {
      listener.exitBoolLiteral(this);
    }
  }
}
export class NumberLiteralContext extends LiteralContext {
  constructor(parser: FEEL_1_1Parser, ctx: LiteralContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public IntegerLiteral(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IntegerLiteral, 0);
  }
  public FloatingPointLiteral(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FloatingPointLiteral, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNumberLiteral) {
      listener.enterNumberLiteral(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNumberLiteral) {
      listener.exitNumberLiteral(this);
    }
  }
}

export class AtLiteralContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public AT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.AT, 0);
  }
  public atLiteralValue(): AtLiteralValueContext {
    return this.getTypedRuleContext(AtLiteralValueContext, 0) as AtLiteralValueContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_atLiteral;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterAtLiteral) {
      listener.enterAtLiteral(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitAtLiteral) {
      listener.exitAtLiteral(this);
    }
  }
}

export class AtLiteralValueContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public StringLiteral(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.StringLiteral, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_atLiteralValue;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterAtLiteralValue) {
      listener.enterAtLiteralValue(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitAtLiteralValue) {
      listener.exitAtLiteralValue(this);
    }
  }
}

export class SimplePositiveUnaryTestContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_simplePositiveUnaryTest;
  }
  public copyFrom(ctx: SimplePositiveUnaryTestContext): void {
    super.copyFrom(ctx);
  }
}
export class PositiveUnaryTestIntervalContext extends SimplePositiveUnaryTestContext {
  constructor(parser: FEEL_1_1Parser, ctx: SimplePositiveUnaryTestContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public interval(): IntervalContext {
    return this.getTypedRuleContext(IntervalContext, 0) as IntervalContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveUnaryTestInterval) {
      listener.enterPositiveUnaryTestInterval(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveUnaryTestInterval) {
      listener.exitPositiveUnaryTestInterval(this);
    }
  }
}
export class PositiveUnaryTestIneqContext extends SimplePositiveUnaryTestContext {
  public _op!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: SimplePositiveUnaryTestContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public endpoint(): EndpointContext {
    return this.getTypedRuleContext(EndpointContext, 0) as EndpointContext;
  }
  public EQUAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EQUAL, 0);
  }
  public NOTEQUAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NOTEQUAL, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveUnaryTestIneq) {
      listener.enterPositiveUnaryTestIneq(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveUnaryTestIneq) {
      listener.exitPositiveUnaryTestIneq(this);
    }
  }
}
export class PositiveUnaryTestIneqIntervalContext extends SimplePositiveUnaryTestContext {
  public _op!: Token;
  constructor(parser: FEEL_1_1Parser, ctx: SimplePositiveUnaryTestContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public endpoint(): EndpointContext {
    return this.getTypedRuleContext(EndpointContext, 0) as EndpointContext;
  }
  public LT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LT, 0);
  }
  public GT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GT, 0);
  }
  public LE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LE, 0);
  }
  public GE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GE, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveUnaryTestIneqInterval) {
      listener.enterPositiveUnaryTestIneqInterval(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveUnaryTestIneqInterval) {
      listener.exitPositiveUnaryTestIneqInterval(this);
    }
  }
}

export class SimplePositiveUnaryTestsContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public simplePositiveUnaryTest_list(): SimplePositiveUnaryTestContext[] {
    return this.getTypedRuleContexts(SimplePositiveUnaryTestContext) as SimplePositiveUnaryTestContext[];
  }
  public simplePositiveUnaryTest(i: number): SimplePositiveUnaryTestContext {
    return this.getTypedRuleContext(SimplePositiveUnaryTestContext, i) as SimplePositiveUnaryTestContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_simplePositiveUnaryTests;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterSimplePositiveUnaryTests) {
      listener.enterSimplePositiveUnaryTests(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitSimplePositiveUnaryTests) {
      listener.exitSimplePositiveUnaryTests(this);
    }
  }
}

export class SimpleUnaryTestsContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_simpleUnaryTests;
  }
  public copyFrom(ctx: SimpleUnaryTestsContext): void {
    super.copyFrom(ctx);
  }
}
export class PositiveUnaryTestDashContext extends SimpleUnaryTestsContext {
  constructor(parser: FEEL_1_1Parser, ctx: SimpleUnaryTestsContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public SUB(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SUB, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveUnaryTestDash) {
      listener.enterPositiveUnaryTestDash(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveUnaryTestDash) {
      listener.exitPositiveUnaryTestDash(this);
    }
  }
}
export class PositiveSimplePositiveUnaryTestsContext extends SimpleUnaryTestsContext {
  constructor(parser: FEEL_1_1Parser, ctx: SimpleUnaryTestsContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public simplePositiveUnaryTests(): SimplePositiveUnaryTestsContext {
    return this.getTypedRuleContext(SimplePositiveUnaryTestsContext, 0) as SimplePositiveUnaryTestsContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveSimplePositiveUnaryTests) {
      listener.enterPositiveSimplePositiveUnaryTests(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveSimplePositiveUnaryTests) {
      listener.exitPositiveSimplePositiveUnaryTests(this);
    }
  }
}
export class NegatedSimplePositiveUnaryTestsContext extends SimpleUnaryTestsContext {
  constructor(parser: FEEL_1_1Parser, ctx: SimpleUnaryTestsContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public NOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NOT, 0);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public simplePositiveUnaryTests(): SimplePositiveUnaryTestsContext {
    return this.getTypedRuleContext(SimplePositiveUnaryTestsContext, 0) as SimplePositiveUnaryTestsContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNegatedSimplePositiveUnaryTests) {
      listener.enterNegatedSimplePositiveUnaryTests(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNegatedSimplePositiveUnaryTests) {
      listener.exitNegatedSimplePositiveUnaryTests(this);
    }
  }
}

export class PositiveUnaryTestContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public expression(): ExpressionContext {
    return this.getTypedRuleContext(ExpressionContext, 0) as ExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_positiveUnaryTest;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveUnaryTest) {
      listener.enterPositiveUnaryTest(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveUnaryTest) {
      listener.exitPositiveUnaryTest(this);
    }
  }
}

export class PositiveUnaryTestsContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public positiveUnaryTest_list(): PositiveUnaryTestContext[] {
    return this.getTypedRuleContexts(PositiveUnaryTestContext) as PositiveUnaryTestContext[];
  }
  public positiveUnaryTest(i: number): PositiveUnaryTestContext {
    return this.getTypedRuleContext(PositiveUnaryTestContext, i) as PositiveUnaryTestContext;
  }
  public COMMA_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.COMMA);
  }
  public COMMA(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_positiveUnaryTests;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterPositiveUnaryTests) {
      listener.enterPositiveUnaryTests(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitPositiveUnaryTests) {
      listener.exitPositiveUnaryTests(this);
    }
  }
}

export class UnaryTestsRootContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public unaryTests(): UnaryTestsContext {
    return this.getTypedRuleContext(UnaryTestsContext, 0) as UnaryTestsContext;
  }
  public EOF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EOF, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_unaryTestsRoot;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterUnaryTestsRoot) {
      listener.enterUnaryTestsRoot(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitUnaryTestsRoot) {
      listener.exitUnaryTestsRoot(this);
    }
  }
}

export class UnaryTestsContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_unaryTests;
  }
  public copyFrom(ctx: UnaryTestsContext): void {
    super.copyFrom(ctx);
  }
}
export class UnaryTests_emptyContext extends UnaryTestsContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryTestsContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public SUB(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SUB, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterUnaryTests_empty) {
      listener.enterUnaryTests_empty(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitUnaryTests_empty) {
      listener.exitUnaryTests_empty(this);
    }
  }
}
export class UnaryTests_positiveContext extends UnaryTestsContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryTestsContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public positiveUnaryTests(): PositiveUnaryTestsContext {
    return this.getTypedRuleContext(PositiveUnaryTestsContext, 0) as PositiveUnaryTestsContext;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterUnaryTests_positive) {
      listener.enterUnaryTests_positive(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitUnaryTests_positive) {
      listener.exitUnaryTests_positive(this);
    }
  }
}
export class UnaryTests_negatedContext extends UnaryTestsContext {
  constructor(parser: FEEL_1_1Parser, ctx: UnaryTestsContext) {
    super(parser, ctx.parentCtx, ctx.invokingState);
    super.copyFrom(ctx);
  }
  public NOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NOT, 0);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public positiveUnaryTests(): PositiveUnaryTestsContext {
    return this.getTypedRuleContext(PositiveUnaryTestsContext, 0) as PositiveUnaryTestsContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterUnaryTests_negated) {
      listener.enterUnaryTests_negated(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitUnaryTests_negated) {
      listener.exitUnaryTests_negated(this);
    }
  }
}

export class EndpointContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public additiveExpression(): AdditiveExpressionContext {
    return this.getTypedRuleContext(AdditiveExpressionContext, 0) as AdditiveExpressionContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_endpoint;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterEndpoint) {
      listener.enterEndpoint(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitEndpoint) {
      listener.exitEndpoint(this);
    }
  }
}

export class IntervalContext extends ParserRuleContext {
  public _low!: Token;
  public _start!: EndpointContext;
  public _end!: EndpointContext;
  public _up!: Token;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public ELIPSIS(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ELIPSIS, 0);
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public endpoint_list(): EndpointContext[] {
    return this.getTypedRuleContexts(EndpointContext) as EndpointContext[];
  }
  public endpoint(i: number): EndpointContext {
    return this.getTypedRuleContext(EndpointContext, i) as EndpointContext;
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public LBRACK_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.LBRACK);
  }
  public LBRACK(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LBRACK, i);
  }
  public RBRACK_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.RBRACK);
  }
  public RBRACK(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RBRACK, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_interval;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterInterval) {
      listener.enterInterval(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitInterval) {
      listener.exitInterval(this);
    }
  }
}

export class QualifiedNameContext extends ParserRuleContext {
  public qns: Array<string>;
  public _n1!: NameRefContext;
  public _n2!: NameRefContext;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public nameRef_list(): NameRefContext[] {
    return this.getTypedRuleContexts(NameRefContext) as NameRefContext[];
  }
  public nameRef(i: number): NameRefContext {
    return this.getTypedRuleContext(NameRefContext, i) as NameRefContext;
  }
  public DOT_list(): TerminalNode[] {
    return this.getTokens(FEEL_1_1Parser.DOT);
  }
  public DOT(i: number): TerminalNode {
    return this.getToken(FEEL_1_1Parser.DOT, i);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_qualifiedName;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterQualifiedName) {
      listener.enterQualifiedName(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitQualifiedName) {
      listener.exitQualifiedName(this);
    }
  }
}

export class NameRefContext extends ParserRuleContext {
  public _st!: Token;
  public _not_st!: Token;
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public Identifier(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.Identifier, 0);
  }
  public NOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NOT, 0);
  }
  public nameRefOtherToken_list(): NameRefOtherTokenContext[] {
    return this.getTypedRuleContexts(NameRefOtherTokenContext) as NameRefOtherTokenContext[];
  }
  public nameRefOtherToken(i: number): NameRefOtherTokenContext {
    return this.getTypedRuleContext(NameRefOtherTokenContext, i) as NameRefOtherTokenContext;
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_nameRef;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNameRef) {
      listener.enterNameRef(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNameRef) {
      listener.exitNameRef(this);
    }
  }
}

export class NameRefOtherTokenContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public LPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LPAREN, 0);
  }
  public RPAREN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RPAREN, 0);
  }
  public LBRACK(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LBRACK, 0);
  }
  public RBRACK(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RBRACK, 0);
  }
  public LBRACE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LBRACE, 0);
  }
  public RBRACE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RBRACE, 0);
  }
  public LT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.LT, 0);
  }
  public GT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.GT, 0);
  }
  public EQUAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EQUAL, 0);
  }
  public BANG(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.BANG, 0);
  }
  public COMMA(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.COMMA, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_nameRefOtherToken;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterNameRefOtherToken) {
      listener.enterNameRefOtherToken(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitNameRefOtherToken) {
      listener.exitNameRefOtherToken(this);
    }
  }
}

export class ReusableKeywordsContext extends ParserRuleContext {
  constructor(parser?: FEEL_1_1Parser, parent?: ParserRuleContext, invokingState?: number) {
    super(parent, invokingState);
    this.parser = parser;
  }
  public FOR(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FOR, 0);
  }
  public RETURN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.RETURN, 0);
  }
  public IF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.IF, 0);
  }
  public THEN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.THEN, 0);
  }
  public ELSE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.ELSE, 0);
  }
  public SOME(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SOME, 0);
  }
  public EVERY(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EVERY, 0);
  }
  public SATISFIES(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.SATISFIES, 0);
  }
  public INSTANCE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.INSTANCE, 0);
  }
  public OF(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.OF, 0);
  }
  public FUNCTION(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FUNCTION, 0);
  }
  public EXTERNAL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.EXTERNAL, 0);
  }
  public OR(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.OR, 0);
  }
  public AND(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.AND, 0);
  }
  public BETWEEN(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.BETWEEN, 0);
  }
  public NOT(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NOT, 0);
  }
  public NULL(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.NULL, 0);
  }
  public TRUE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.TRUE, 0);
  }
  public FALSE(): TerminalNode {
    return this.getToken(FEEL_1_1Parser.FALSE, 0);
  }
  public get ruleIndex(): number {
    return FEEL_1_1Parser.RULE_reusableKeywords;
  }
  public enterRule(listener: FEEL_1_1Listener): void {
    if (listener.enterReusableKeywords) {
      listener.enterReusableKeywords(this);
    }
  }
  public exitRule(listener: FEEL_1_1Listener): void {
    if (listener.exitReusableKeywords) {
      listener.exitReusableKeywords(this);
    }
  }
}
