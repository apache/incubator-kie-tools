/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as Monaco from "@kie-tooling-core/monaco-editor";

declare global {
  interface Window {
    __KIE__FEEL__?: FeelService;
  }
}

declare namespace org {
  namespace kie {
    namespace dmn {
      namespace feel {
        namespace client {
          namespace showcase {
            class FeelJS {
              getSuggestions: (feelExpression: string, row: number, col: number) => { toArray: () => string[] };
              evaluate(expression: string): string;
            }
          }
        }
      }
    }
  }
}

export class FeelService {
  private feelGwt?: org.kie.dmn.feel.client.showcase.FeelJS;

  constructor(feelGwt?: org.kie.dmn.feel.client.showcase.FeelJS) {
    this.feelGwt = feelGwt;
  }

  static getInstance() {
    if (window.__KIE__FEEL__ === undefined) {
      try {
        window.__KIE__FEEL__ = new FeelService(new org.kie.dmn.feel.client.showcase.FeelJS());
      } catch (e) {
        return new FeelService();
      }
    }
    return window.__KIE__FEEL__;
  }

  getSuggestions(feelExpression: string, row: number, col: number): Monaco.languages.CompletionItem[] {
    let sortValue = 0;
    try {
      const suggestions = this.feelGwt?.getSuggestions(feelExpression, row, col).toArray() || [];
      return (
        suggestions
          .filter((value, index, self) => {
            return self.indexOf(value) === index;
          })
          .map((s) => {
            return {
              kind: Monaco.languages.CompletionItemKind.Function,
              insertTextRules: Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              label: s,
              insertText: s,
              sortText: String(++sortValue).padStart(5, "0"),
            } as Monaco.languages.CompletionItem;
          }) || []
      );
    } catch (e) {
      return [];
    }
  }

  evaluate(expression: string) {
    const defaultResult = "";
    let result = "";

    try {
      result = this.feelGwt?.evaluate(expression) || defaultResult;
    } catch (e) {
      return defaultResult;
    }

    if (result === "Eval error." || result.startsWith("org.kie.dmn.feel")) {
      result = defaultResult;
    }

    return result;
  }
}
