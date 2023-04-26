/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

describe("Serverless Logic Web Tools - Expression test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  const testParameters: [string, string, number, number][] = [
    ["JSON", "expression/expression.sw.json", 10, 30],
    ["YAML", "expression/expression.sw.yaml", 8, 26],
  ];

  testParameters.forEach(($param) => {
    const [fileType, filePath, rowLocation, columnLocation] = $param;

    it(`should check expression autocompletion in ${fileType} serverless workflow file`, () => {
      // open prepared sw file
      cy.get("#upload-field").attachFile(`${filePath}`, {
        subjectType: "drag-n-drop",
      });
      cy.loadEditor();

      cy.getEditor().within(() => {
        cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
          // move to value of actions -> actionDataFilter -> fromStateData and invoke content assist
          cy.get(".monaco-editor textarea").moveToPosition(rowLocation, columnLocation).type("{ctrl} ");

          // check jq functions are listed in content assist
          cy.get(".monaco-list-row .label-name").should(($jqFunctions) => {
            expect($jqFunctions.eq(0)).text("add");
            expect($jqFunctions.eq(1)).text("all");
            expect($jqFunctions.eq(2)).text("all(condition)");
            expect($jqFunctions.eq(3)).text("all(generator; condition)");
            expect($jqFunctions.eq(4)).text("any");
          });

          // close content assist and invoke new one after '.'
          cy.get(".monaco-editor textarea").type("{esc}").type(".").type("{ctrl} ");

          // check properties from dataInputSchema and callBackFunc are listed in content assist
          cy.get(".monaco-list-row .label-name").should(($properties) => {
            expect($properties.length).eq(5);
            expect($properties.eq(0)).text("numbers");
            expect($properties.eq(1)).text("processInstanceId");
            expect($properties.eq(2)).text("uri");
            expect($properties.eq(3)).text("x");
            expect($properties.eq(4)).text("y");
          });

          // delete '.' and invoke content assist for 'fn:'
          cy.get(".monaco-editor textarea").type("{backspace}").type("fn:").type("{ctrl} ");

          // check function is listed in content assist
          cy.get(".monaco-list-row .label-name").should(($functions) => {
            expect($functions.length).eq(1);
            expect($functions.eq(0)).text("testFunc");
          });
        });
      });
    });
  });
});
