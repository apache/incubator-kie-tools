/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { resolveCompletionItem } from "@kie-tools/yard-editor/dist/textEditor/CompletionItemResolver";
import { Position } from "monaco-editor";

const text = [
  "specVersion: alpha",
  "kind: ServerlessDecision",
  "name: 'Traffic Violation'",
  "expressionLang: alpha",
  "inputs:",
  " - name: 'Driver'",
  "   type: 'http://myapi.org/jsonSchema.json#Driver'",
  " - name: 'Violation'",
  "   type: 'http://myapi.org/jsonSchema.json#Violation'",
  "elements:",
  " - name: 'Fine'",
  "   type: Decision",
  "   requirements: ['Violation']",
  "   logic:",
  "     type: DecisionTable",
  "     inputs: ['Violation.type', 'Violation.Actual Speed - Violation.Speed Limit']",
  "     outputComponents: ['Amount', 'Points']",
  "     rules:",
  "      - ['=\"speed\"', '[10..30)', 500, 3]",
  "      - ['=\"speed\"', '>= 30', 1000, 7]",
  "      - ['=\"parking\"', '-', 100, 1]",
  "      - ['=\"driving under the influence\"', '-', 1000, 5]",
  " - name: 'Should the driver be suspended?'",
  "   type: Decision",
  "   requirements: ['Driver', 'Fine']",
  "   logic:",
  "     type: LiteralExpression",
  '     expression: \'if Driver.Points + Fine.Points >= 20 then "Yes" else "No"\'',
];
describe("Test completion", () => {
  test("Test no text", () => {
    const completionItem = resolveCompletionItem(new Position(1, 1), []);

    expect(completionItem).toBeNull();
  });
  test("No completion above dtable rows", () => {
    const completionItem = resolveCompletionItem(new Position(1, 1), text);

    expect(completionItem).toBeNull();
  });
  test("No completion below dtable rows", () => {
    const completionItem = resolveCompletionItem(new Position(24, 1), text);

    expect(completionItem).toBeNull();
  });
  test("Get completion", () => {
    const completionItem = resolveCompletionItem(new Position(20, 1), text);

    expect(completionItem?.insertText).toBe(" [ $1,$2,$3,$4 ]");
  });
});
