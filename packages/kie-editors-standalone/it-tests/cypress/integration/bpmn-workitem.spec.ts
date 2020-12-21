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

describe("Bpmn Workitem.", () => {
  before("Visit page", () => {
    cy.visit("localhost:9001/bpmn-workitem");
    cy.loadEditor("bpmn-workitem");
  });

  it("Test Open Editor With Custom Workitem", () => {
    cy.editor("bpmn-workitem")
      .find("[data-field='kie-palette']")
      .should("be.visible")
      .find("[data-field='listGroupItem'] [data-field='categoryIcon']")
      .should("have.length", 9)
      .then($items => {
        expect($items.eq(0)).to.have.attr("title", "Start Events");
        expect($items.eq(1)).to.have.attr("title", "Intermediate Events");
        expect($items.eq(2)).to.have.attr("title", "End Events");
        expect($items.eq(3)).to.have.attr("title", "Activities");
        expect($items.eq(4)).to.have.attr("title", "SubProcesses");
        expect($items.eq(5)).to.have.attr("title", "Gateways");
        expect($items.eq(6)).to.have.attr("title", "Containers");
        expect($items.eq(7)).to.have.attr("title", "Artifacts");
        expect($items.eq(8)).to.have.attr("title", "Custom Tasks");
        cy.wrap($items.eq(8)).click();
      });
    cy.editor("bpmn-workitem")
      .find("[data-field='listGroupItem']")
      .eq(8)
      .find("[data-field='floatingPanel']")
      .find("[data-field='name']")
      .should("exist")
      .should("contain.text", "CreateCustomer");
  });
});
