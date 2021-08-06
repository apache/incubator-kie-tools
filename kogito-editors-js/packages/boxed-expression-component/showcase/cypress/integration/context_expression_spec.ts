/// <reference types="Cypress" />

describe("Context Expression Tests", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Define context expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Assert some content
    cy.ouiaId("expression-grid-table").should("contain.text", "ContextEntry-1");
  });
});
