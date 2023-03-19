/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

describe("Test DMN Documentation tab", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Decision Question and Allowed Answers are present", () => {
    // upload dmn file from fixtures directory by drag and drop
    cy.get("#upload-field").attachFile("testModelDocumentation.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // check editor title name
    cy.get("[aria-label='Edit file name']").should("have.value", "testModelDocumentation");

    // close DMN guided tour dialog
    cy.ouia({ ouiaId: "dmn-guided-tour" }).get("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open Documentaion tab
      cy.ouia({ ouiaId: "Documentation", ouiaType: "editor-nav-tab" }).find("a").click();
      // Question
      cy.get(".drd-component").contains("What is output?").should("exist");
      // Allowed Answers
      cy.get(".drd-component").contains("Always a constant 0.").should("exist");
    });
  });
});
