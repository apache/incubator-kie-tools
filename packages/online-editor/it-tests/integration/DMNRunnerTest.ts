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

describe("DMN Runner Test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Test DMN Runner on DMN sample", () => {
    // click Create new decision model button (new DMN)
    cy.get("[data-ouia-component-id='try-dmn-sample-button']").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // start the DMN Runner
    cy.get("[data-ouia-component-id='dmn-guided-tour-skip-runner-start-button']").click();

    // fill in DMN Runner inputs panel
    cy.get("[data-testid='dmn-form']").within(($form) => {
      cy.get("input[name='Credit Score.FICO']").type("650");
      cy.get("input[name='Applicant Data.Age']").type("30");

      cy.get("[x-dmn-type*='Marital_Status'] button").click();
      cy.get("ul[name='Applicant Data.Marital Status'] button").contains("M").click();

      cy.get("input[name='Applicant Data.Existing Customer']").check();

      cy.get("input[name='Applicant Data.Monthly.Income']").type("3000");
      cy.get("input[name='Applicant Data.Monthly.Repayments']").type("120");
      cy.get("input[name='Applicant Data.Monthly.Expenses']").type("0");
      cy.get("input[name='Applicant Data.Monthly.Tax']").type("0");
      cy.get("input[name='Applicant Data.Monthly.Insurance']").type("0");

      cy.get("[x-dmn-type*='Product_Type'] button").click();
      cy.get("ul[name='Requested Product.Type'] button").contains("Standard Loan").click();

      cy.get("input[name='Requested Product.Rate']").type("1.5");
      cy.get("input[name='Requested Product.Term']").type("4");
      cy.get("input[name='Requested Product.Amount']").type("10000");
    });

    // check DMN Runner outputs panel
    cy.get("[data-testid='dmn-form-result']").within(($form) => {
      cy.get("article div:contains('Front End Ratio')").next().contains("Sufficient").should("be.visible");
      cy.get("article div:contains('Back End Ratio')").next().contains("Sufficient").should("be.visible");
      cy.get("article div:contains('Credit Score Rating')").next().contains("Fair").should("be.visible");
      cy.get("article div:contains('Loan Pre-Qualification')").next().should("contain.text", "Qualified");
    });
  });

  it.skip("Test DMN Runner on DMN sample - table view", () => {
    // click Create new decision model button (new DMN)
    cy.get("[data-ouia-component-id='try-dmn-sample-button']").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // skip tour
    cy.get("button").contains("Skip tour").click();

    // switch to tabular view
    cy.get("[data-ouia-component-id='switch-dmn-runner-to-table-view']").click();

    // fill in DMN Runner inputs in table
    cy.get(".dmn-runner-table.id1")
      .get("[data-ouia-component-id='expression-row-0']")
      .within(($table) => {
        cy.get("input[name='Credit Score.FICO']").type("650");
        cy.get("input[name='Applicant Data.Age']").type("30");

        // 'Marital_Status' is set later

        cy.get("input[name='Applicant Data.Existing Customer']").check();

        cy.get("input[name='Applicant Data.Monthly.Income']").type("3000");
        cy.get("input[name='Applicant Data.Monthly.Repayments']").type("120");
        cy.get("input[name='Applicant Data.Monthly.Expenses']").type("0");
        cy.get("input[name='Applicant Data.Monthly.Tax']").type("0");
        cy.get("input[name='Applicant Data.Monthly.Insurance']").type("0");

        // 'Product_Type' is set later

        cy.get("input[name='Requested Product.Rate']").type("1.5");
        cy.get("input[name='Requested Product.Term']").type("4");
        cy.get("input[name='Requested Product.Amount']").type("10000");
      });

    // handle inputs that uses selectboxes outside of expression-row
    cy.get(".dmn-runner-table.id1")
      .get("[data-ouia-component-id='expression-row-0']")
      .within(($table) => {
        cy.get("[x-dmn-type*='Marital_Status'] button").click();
      });
    cy.get("ul[name='Applicant Data.Marital Status'] button").contains("M").click();

    cy.get(".dmn-runner-table.id1")
      .get("[data-ouia-component-id='expression-row-0']")
      .within(($table) => {
        cy.get("[x-dmn-type*='Product_Type'] button").click();
      });
    cy.get("ul[name='Requested Product.Type'] button").contains("Standard Loan").click();

    // check DMN Runner outputs in table
    cy.get(".dmn-runner-table.id2")
      .get("[data-ouia-component-id='expression-row-0']")
      .within(($table) => {
        cy.get("[data-ouia-component-id='expression-column-1']").contains("Sufficient").should("be.visible");
        cy.get("[data-ouia-component-id='expression-column-2']").contains("Sufficient").should("be.visible");
        cy.get("[data-ouia-component-id='expression-column-3']").contains("Fair").should("be.visible");
        cy.get("[data-ouia-component-id='expression-column-4']").should("contain.text", "Qualified");
      });
  });
});
