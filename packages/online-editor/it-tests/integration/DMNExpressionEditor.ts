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

import * as buildEnv from "@kogito-tooling/build-env";

describe("DMN Expression Editor Test", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.onlineEditor.dev.port}/`);
  });

  it("Test New Expresssion editor", () => {
    // click Create new decision model button (new DMN)
    cy.ouiaId("try-dmn-sample-button").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // check editor logo
    cy.get("[class='pf-c-brand']").within(($logo) => {
      expect($logo.attr("src")).contain("dmn");
      expect($logo.attr("alt")).contain("dmn");
    });

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouiaId("collapsed-docks-bar-W").children("button").click();

      // check context expression
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Back End Ratio'] div span").contains("Context").click();
      });
      // turn on new editor
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouiaId("expression-container").contains("Back End Ratio").should("be.visible");

      // check function expression
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='PITI'] div span").contains("Function").click();
      });
      cy.ouiaId("expression-container").contains("PITI").should("be.visible");

      // check decision table expression
      // https://issues.redhat.com/browse/KOGITO-6097
      // cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
      //   cy.get("[title='Credit Score Rating'] div span").contains("Decision Table").click();
      // });
      // cy.ouiaId("expression-container").contains("Credit Score Rating").should("be.visible");
    });
  });
});
