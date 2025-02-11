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

import { Scope } from "./Scope";
import { Type } from "./Type";
import { Symbol } from "./Symbol";
import { TokenTree } from "./TokenTree";
import { CharStreams, Token } from "antlr4";
import FEEL_1_1Lexer from "./generated-parser/FEEL_1_1Lexer";
import { IdentifierSymbol } from "./IdentifierSymbol";
import { FeelSyntacticSymbolNature } from "../FeelSyntacticSymbolNature";
import { Expression } from "../Expression";

export class ScopeImpl implements Scope {
  private readonly name?: string;
  private readonly parentScope?: Scope;
  private readonly type?: Type;
  private readonly childScopes: Map<string, Scope>;
  private readonly symbols: Map<string, Symbol>;
  private tokenTree?: TokenTree;
  private readonly _allowDynamicVariables?: boolean;

  public constructor(name?: string, parentScope?: Scope, type?: Type, allowDynamicVariables?: boolean) {
    this.childScopes = new Map<string, Scope>();
    this.symbols = new Map<string, Symbol>();
    this.name = name;
    this.parentScope = parentScope;
    this.type = type;
    this._allowDynamicVariables = allowDynamicVariables;
    if (parentScope) {
      parentScope.addChildScope(this);
    }
  }

  get allowDynamicVariables(): boolean | undefined {
    return this._allowDynamicVariables;
  }

  addChildScope(scope: Scope): void {
    this.childScopes.set(scope.getName(), scope);
  }

  start(token: string): void {
    if (!this.tokenTree) {
      this.initializeTokenTree();
    }
    this.tokenTree?.start(token);
    if (this.getParentScope()) {
      this.getParentScope()?.start(token);
    }
  }

  define(symbol: Symbol): boolean {
    if (this.symbols.has(symbol.getId() ?? "")) {
      // duplicate symbol definition
      return false;
    }
    this.symbols.set(symbol.getId() ?? "", symbol);
    if (this.tokenTree) {
      // also load the symbol into the token tree
      this.tokenTree.addName(this.tokenize(symbol.getId() ?? ""));
    }
    return true;
  }

  tokenize(symbol: string) {
    const input = CharStreams.fromString(symbol);
    const lexer = new FEEL_1_1Lexer(input);
    const tokens = new Array<string>();

    for (let token = lexer.nextToken(); token.type != Token.EOF; token = lexer.nextToken()) {
      tokens.push(token.text);
    }
    return tokens;
  }

  followUp(token: string, isPredict: boolean): boolean {
    const parent = (this.getParentScope() && this.getParentScope()?.followUp(token, isPredict)) ?? false;
    return this.tokenTree?.followUp(token, !isPredict) || parent;
  }

  getChildScopes(): Map<string, Scope> {
    return this.childScopes;
  }

  getName(): string {
    return this.name ?? "";
  }

  getParentScope(): Scope | undefined {
    return this.parentScope;
  }

  getSymbols(): Map<string, Symbol> {
    return this.symbols;
  }

  getType(): Type | undefined {
    return this.type;
  }

  resolve(id: string): Symbol | undefined;
  resolve(qualifiedName: string[]): Symbol | undefined;
  resolve(parameter: string | string[]): Symbol | undefined {
    if (typeof parameter === "string") {
      if (this._allowDynamicVariables) {
        return new IdentifierSymbol(
          parameter,
          {
            name: "name",
            source: {
              value: "name",
              feelSyntacticSymbolNature: FeelSyntacticSymbolNature.DynamicVariable,
              expressionsThatUseTheIdentifier: new Map<string, Expression>(),
            },
          },
          FeelSyntacticSymbolNature.DynamicVariable
        );
      }
      return this.resolveId(parameter);
    } else {
      return this.resolveQualifiedName(parameter);
    }
  }

  private resolveId(id: string) {
    const s = this.symbols.get(id); // FIXME: EvalHelper.normalizeVariableName( id )
    if (s == null && this.parentScope) {
      return this.parentScope.resolve(id);
    }
    return s;
  }

  private resolveQualifiedName(qualifiedName: string[]) {
    const root = this.symbols.get(qualifiedName[0]); //fixme:  EvalHelper.normalizeVariableName( qualifiedName[0] )
    if (root == null && this.parentScope) {
      return this.parentScope.resolve(qualifiedName);
    } else if (root != null) {
      let currentSymbol: Symbol | undefined = root;
      for (let i = 1; i < qualifiedName.length && currentSymbol; i++) {
        currentSymbol = currentSymbol.getScope()?.resolve(qualifiedName[i]); //fixme: EvalHelper.normalizeVariableName( qualifiedName[i] )
      }
      return currentSymbol;
    }
    return undefined;
  }

  private initializeTokenTree() {
    this.tokenTree = this.tokenTreeFromSymbols(this.getSymbols());
  }

  private tokenTreeFromSymbols(symbols: Map<string, Symbol>) {
    const tt = new TokenTree();
    for (const symbol of symbols.keys()) {
      const tokens = this.tokenize(symbol);
      tt.addName(tokens);
    }
    return tt;
  }
}
