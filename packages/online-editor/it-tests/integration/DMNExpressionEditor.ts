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
    cy.visit(`https://localhost:${buildEnv.onlineEditor.dev.port}/`);
  });

  it("Test New Expresssion editor - context", () => {
    // click Create new decision model button (new DMN)
    cy.ouiaId("try-dmn-sample-button").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouiaId("docks-item-org.kie.dmn.decision.navigator").children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Back End Ratio'] div span").contains("Context").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouiaId("expression-container").contains("Back End Ratio").should("be.visible");
      cy.get(".expression-title").contains("Back End Ratio").should("be.visible");
      cy.get(".expression-type").contains("Context").should("be.visible");
    });
  });

  it("Test New Expresssion editor - function", () => {
    // click Create new decision model button (new DMN)
    cy.ouiaId("try-dmn-sample-button").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouiaId("docks-item-org.kie.dmn.decision.navigator").children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='PITI'] div span").contains("Function").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouiaId("expression-container").contains("PITI").should("be.visible");
      cy.get(".expression-title").contains("PITI").should("be.visible");
      cy.get(".expression-type").contains("Function").should("be.visible");
    });
  });

  // TODO - unskip once kogito-editors-java bump is available
  it.skip("Test New Expresssion editor - decision table", () => {
    // click Create new decision model button (new DMN)
    cy.ouiaId("try-dmn-sample-button").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator and check new expression editor for expressions
      cy.ouiaId("docks-item-org.kie.dmn.decision.navigator").children("button").click();

      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Credit Score Rating'] div span").contains("Decision Table").click();
      });
      // check using beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();
      cy.ouiaId("expression-container").contains("Credit Score Rating").should("be.visible");
      cy.get(".expression-title").contains("Credit Score Rating").should("be.visible");
      cy.get(".expression-type").contains("Decision Table").should("be.visible");
    });
  });

  // TODO - unskip once kogito-editors-java bump is available
  it.skip("Change Decition Table from Any to Custom Data Type", () => {
    cy.get("#upload-field").attachFile("testModelWithCustomDataType.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // close DMN guided tour dialog
    cy.ouiaId("dmn-guided-tour").children("button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open decision navigator
      cy.ouiaId("docks-item-org.kie.dmn.decision.navigator").children("button").click();

      // open decision table expression
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").within(($navigator) => {
        cy.get("[title='Final Salary'] div span").contains("Decision Table").click();
      });
      // activate editor beta version
      cy.get("[data-field='beta-boxed-expression-toggle'] [data-field='try-it']").click();

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('tSalary')").should("not.exist");

      cy.get(".header-cell-info").contains("Final Salary").click();
      cy.ouiaId("edit-expression-data-type").click();
      cy.ouiaId("expression-popover-menu").within(($menu) => {
        cy.ouiaId("tSalary").click({ force: true });
      });

      cy.get("[data-ouia-component-type='expression-column-header-cell-info']:contains('tSalary')").should(
        "be.visible"
      );
    });
  });
});
