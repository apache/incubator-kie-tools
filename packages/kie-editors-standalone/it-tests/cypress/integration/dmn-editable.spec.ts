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

describe("Dmn Editable.", () => {
  before("Visit page", () => {
    cy.visit(`localhost:${buildEnv.standaloneEditors.dev.port}/dmn-editable`);
    cy.loadEditors(["dmn-editable"]);
  });

  it("Test Load File And View", () => {
    cy.editor("dmn-editable").find("[data-field='kie-palette']").should("be.visible");

    cy.editor("dmn-editable")
      .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-W", { timeout: 10000 })
      .should("be.visible");

    cy.uploadFile("call centre drd.dmn", "dmn-editable");
    cy.viewFile("call centre drd.dmn", "dmn-editable");

    cy.editor("dmn-editable")
      .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-W")
      .find("button")
      .first()
      .should("be.visible")
      .click(); // open DecisionNavigator

    cy.editor("dmn-editable")
      .ouiaId("expanded-docks-bar", "expanded-docks-bar-W")
      .should("be.visible")
      .within(($navigator) => {
        cy.get("[data-field='item'][title='DRG']")
          .should("be.visible")
          .siblings("[data-field='item']")
          .should("have.length", 4)
          .then(($items) => {
            expect($items.eq(0)).to.have.attr("title", "call centre drd");
            expect($items.eq(0)).not.to.have.class("editable");
            expect($items.eq(1)).to.have.attr("title", "DRDs");
            expect($items.eq(1)).not.to.have.class("editable");
            expect($items.eq(2)).to.have.attr("title", "call centre");
            expect($items.eq(2)).to.have.class("editable");
            expect($items.eq(3)).to.have.attr("title", "preconditions");
            expect($items.eq(3)).to.have.class("editable");
          });
      });
  });
});
