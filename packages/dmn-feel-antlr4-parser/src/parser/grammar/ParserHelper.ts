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

import { Parser, ParserRuleContext, Token } from "antlr4";
import { FilterPathExpressionContext, KeyStringContext, NameRefContext } from "./generated-parser/FEEL_1_1Parser";
import { Scope } from "./Scope";
import { Type } from "./Type";
import { VariableSymbol } from "./VariableSymbol";
import { ScopeImpl } from "./ScopeImpl";
import { NameQueue } from "./NameQueue";
import { FeelVariable } from "../FeelVariable";
import { Scopes } from "./Scopes";
import { ReservedWords } from "../ReservedWords";
import { FeelSyntacticSymbolNature } from "../FeelSyntacticSymbolNature";
import { MapBackedType } from "./MapBackedType";
import { FeelSymbol } from "../FeelSymbol";
import { Variable } from "../Variable";

export class ParserHelper {
  private dynamicResolution = 0;
  private currentScope: Scope | undefined;
  private readonly currentName: NameQueue;
  private readonly _variables: Array<FeelVariable>;
  private readonly scopes = new Scopes();
  private readonly _availableSymbols: Array<FeelSymbol>;

  constructor() {
    this.currentName = new NameQueue();
    this.currentName.push("<local>");
    this.currentScope = this.scopes.getGlobalScope();
    this._variables = new Array<FeelVariable>();
    this._availableSymbols = new Array<FeelSymbol>();
  }

  get availableSymbols(): Array<FeelSymbol> {
    return this._availableSymbols;
  }

  get variables(): Array<FeelVariable> {
    return this._variables;
  }

  public pushScope(type?: Type) {
    this.currentScope = new ScopeImpl(this.currentName.peek(), this.currentScope, type);
  }

  public popScope() {
    this.currentScope = this.currentScope?.getParentScope();
  }

  public enableDynamicResolution() {
    this.dynamicResolution++;
  }

  public disableDynamicResolution() {
    if (this.dynamicResolution > 0) {
      this.dynamicResolution--;
    }
  }

  public isFeatDMN12EnhancedForLoopEnabled(): boolean {
    return true;
  }

  public startVariable(t: Token) {
    this.currentScope?.start(t.text);
  }

  popName() {
    this.currentName.pop();
  }

  pushName(ctx: ParserRuleContext | string) {
    if (ctx instanceof ParserRuleContext) {
      this.currentName.push(this.getName(ctx));
    } else {
      this.currentName.push(ctx);
    }
  }

  private getName(ctx: ParserRuleContext) {
    const key = this.getOriginalText(ctx);
    if (ctx instanceof KeyStringContext) {
      return key; //FIXME: EvalHelper.unescapeString(key);
    }
    return key;
  }

  defineVariable(
    variable: string | ParserRuleContext,
    type?: Type,
    variableType?: FeelSyntacticSymbolNature,
    variableSource?: Variable
  ) {
    const variableSymbol = new VariableSymbol(
      variable instanceof ParserRuleContext ? this.getName(variable) : variable,
      type,
      variableType,
      variableSource
    );

    if (variableSymbol.getId()) {
      this.availableSymbols.push({
        name: variableSymbol.getId() ?? "",
        type: type?.name ?? "undefined-type",
      });
    }

    this.currentScope?.define(variableSymbol);
  }

  dismissScope() {
    if (this.currentScope?.getType() != null && this.currentScope.getType() === undefined) {
      this.disableDynamicResolution();
    }
    this.popScope();
  }

  recoverScope(name?: string) {
    if (!name) {
      return;
    }

    const s = this.currentScope?.getChildScopes().get(name);
    if (s != null) {
      this.currentScope = s;

      //const type = this.currentScope.getType();
      // if (type && type === BuiltInType.UNKNOWN) {
      //   this.enableDynamicResolution();
      // }
    } else {
      const resolved = this.currentScope?.resolve(name);
      const scopeType = resolved?.getType();
      // if (scopeType instanceof GenListType) {
      //   scopeType = ((GenListType) scopeType).getGen();
      // }

      if (resolved != null && scopeType instanceof MapBackedType) {
        this.pushScope(scopeType);
        for (const f of scopeType.properties) {
          this.currentScope?.define(new VariableSymbol(f[0], f[1]));
        }
      } else {
        this.pushScope();
      }
    }
  }

  followUp(token: Token, isPredict: boolean) {
    // FIXME: && FEELParser.isVariableNamePartValid( t.getText(), currentScope ) = this is very slow. Do we really need to do it for this?
    const dynamicResolutionResult = this.isDynamicResolution();
    const follow = dynamicResolutionResult || this.currentScope?.followUp(token.text, isPredict);
    // in case isPredict == false, will need to followUp in the currentScope, so that the TokenTree currentNode is updated as per expectations,
    // this is because the `follow` variable above, in the case of short-circuited on `dynamicResolutionResult`,
    // would skip performing any necessary update in the second part of the || predicate
    if (dynamicResolutionResult && !isPredict) {
      this.currentScope?.followUp(token.text, isPredict);
    }
    return follow ?? false;
  }

  public isDynamicResolution() {
    return this.dynamicResolution > 0;
  }

  validateVariable(_n1: NameRefContext, qn: String[], name: string) {
    const start = _n1.start.start;
    const end = _n1.stop?.stop ?? 0;
    const length = end - start + 1;

    if (this.currentScope?.getChildScopes().has(name)) {
      this.variables.push(new FeelVariable(start, length, FeelSyntacticSymbolNature.GlobalVariable, name));
    } else {
      const symbol = this.currentScope?.resolve(name);
      if (symbol) {
        symbol.getType();
        if (symbol instanceof VariableSymbol) {
          const scopeSymbols = [];
          if ((symbol as VariableSymbol).getType() instanceof MapBackedType) {
            const map = (symbol as VariableSymbol).getType() as MapBackedType;
            for (const [key, value] of map.properties) {
              scopeSymbols.push({
                name: key,
                type: value.typeRef ?? value.name,
              });
            }
          }
          this.variables.push(
            new FeelVariable(
              start,
              length,
              symbol.symbolType ?? FeelSyntacticSymbolNature.GlobalVariable,
              name,
              scopeSymbols
            )
          );
        } else {
          this.variables.push(new FeelVariable(start, length, FeelSyntacticSymbolNature.GlobalVariable, name));
        }
      } else {
        if (!ReservedWords.FeelFunctions.has(name) && !ReservedWords.FeelKeywords.has(name)) {
          this.variables.push(new FeelVariable(start, length, FeelSyntacticSymbolNature.Unknown, name));
        }
      }
    }
  }

  getOriginalText(ctx: ParserRuleContext) {
    const a = ctx.start.start;
    const b = ctx.stop?.stop ?? 0;
    return ctx.start.getInputStream().getText(a, b);
  }

  fphEnd(count: number) {
    return count;
  }

  fphStart(_n1: FilterPathExpressionContext, param2: Parser) {
    return 0;
  }
}
