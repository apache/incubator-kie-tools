// in cypress/support/index.d.ts
// load type definitions that come with Cypress module
/// <reference types="cypress" />

declare namespace Cypress {
    interface Chainable {
       /**
         * Clicks the given 'Try Sample' according to the passed 'assetType'
         * Doesn't wait for the result of the click
         * @param assetType possible values: 'bpmn', 'dmn'
         * @example cy.trySampleModel('dmn')
         */
       trySampleModel(assetType: string): Chainable<Element>

       /**
         * Returns body html element of the given (bpmn, dmn) diagram editor
         * Acessig Diagram Editor via this method is preferred way as the Diagram Editor is inside an iFrame
         */
       getDiagramEditorBody(): Chainable<Element>
    }
}