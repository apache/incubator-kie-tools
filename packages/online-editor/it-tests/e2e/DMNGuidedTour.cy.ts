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

describe("DMN Guided Tour Test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("follow first guided tour instruction", () => {
    // click Create new decision model button (new DMN)
    cy.get("[data-ouia-component-id='new-dmn-button']").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // start the guided tour
    cy.get("[data-ouia-component-id='dmn-guided-tour']").contains("Take tour").click();

    // follow first instruction - drag decision node to the canvas
    cy.getEditor().within(() => {
      cy.get("div[data-field='palettePanel'] button[title='DMN Decision']").click();
      cy.get("div[data-field='canvasPanel']").trigger("mousedown", 100, 300);
      cy.get("div[data-field='canvasPanel']").trigger("mouseup");
    });

    // check content of guided tour was updated
    cy.get("[data-ouia-component-id='dmn-guided-tour'] div").contains("Rename our decision node").should("be.visible");
  });
});
