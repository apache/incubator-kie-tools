/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as buildEnv from "@kogito-tooling/build-env";

describe("Bpmn Editable.", () => {
  before("Visit page", () => {
    cy.visit(`localhost:${buildEnv.standaloneEditors.dev.port}/bpmn-editable`);
    cy.loadEditors(["bpmn-editable"]);
  });

  it("Test Load File And View", () => {
    cy.editor("bpmn-editable").find("[data-field='kie-palette']").should("be.visible");

    cy.uploadFile("process-string.bpmn", "bpmn-editable");
    cy.viewFile("process-string.bpmn", "bpmn-editable");

    cy.editor("bpmn-editable").find("[data-title='Explore Diagram']").should("be.visible").click();

    cy.editor("bpmn-editable")
      .find("[data-field='explorerPanelBody']")
      .wait(1000)
      .scrollIntoView()
      .should("be.visible")
      .find("a.gwt-Anchor")
      .should("have.length", 7)
      .then(($links) => {
        expect($links.eq(0)).to.contain.text("Process string");
        expect($links.eq(1)).to.contain.text("Start");
        expect($links.eq(2)).to.contain.text("Exclusive");
        expect($links.eq(3)).to.contain.text("Process the String");
        expect($links.eq(4)).to.contain.text("Log Error");
        expect($links.eq(5)).to.contain.text("End");
        expect($links.eq(6)).to.contain.text("End Error");
      });
  });
});
