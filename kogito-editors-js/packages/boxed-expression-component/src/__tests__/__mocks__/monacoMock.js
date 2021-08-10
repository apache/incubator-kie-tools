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

const languages = [];
let value = "new value";
let onDidBlurEditorText;

module.exports = {
  languages: {
    registeredLanguages: [],
    register: (language) => {
      languages.push(language);
    },
    getLanguages: () => {
      return languages;
    },
    setMonarchTokensProvider: (_name, _tokens) => {},
    registerCompletionItemProvider: (_name, _provider) => {},
    CompletionItemKind: {
      Keyword: "Keyword",
      Function: "Function",
    },
    CompletionItemInsertTextRule: {
      InsertAsSnippet: "InsertAsSnippet",
    },
  },
  editor: {
    defineTheme: (_name, _theme) => {},
    colorize: () => ({
      then: (fn) => fn(),
    }),
    create: (element, _config) => {
      element.innerHTML = "<monaco-editor-mock />";
      return {
        dispose: () => {},
        getValue: () => value,
        setValue: (newValue) => {
          if (newValue.includes("\t")) {
            onDidBlurEditorText(newValue);
          }
        },
        setPosition: (_v) => {},
        focus: () => {},
        onDidChangeModelContent: () => {},
        onDidBlurEditorText: (fn) => (onDidBlurEditorText = fn),
        onKeyDown: () => {},
      };
    },
  },
};
