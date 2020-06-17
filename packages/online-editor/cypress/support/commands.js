// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

/**
 * Clicks the given 'Try Sample' according to the passed 'assetType'
 * Doesn't wait for the result of the click
 * @param assetType possible values: 'bpmn', 'dmn'
 */
Cypress.Commands.add("trySampleModel", (assetType) => {
    cy.get(`[data-ouia-component-id='try-sample-model-${assetType}']`)
        .contains('Try Sample')
        .click();
})

/**
 * Returns body html element of the given (bpmn, dmn) diagram editor
 * Acessig Diagram Editor via this method is preferred way as the Diagram Editor is inside an iFrame
 */
Cypress.Commands.add("getDiagramEditorBody", () => {
    // get the iframe > document > body
    // and retry until the body element is not empty
    return cy
        .get('iframe[data-envelope-channel="ONLINE"]')
        .its('0.contentDocument.body').should('not.be.empty')
        // wraps "body" DOM element to allow
        // chaining more Cypress commands, like ".find(...)"
        // https://on.cypress.io/wrap
        .then(cy.wrap)
})