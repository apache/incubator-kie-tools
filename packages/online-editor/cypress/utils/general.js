/**
 * Clicks the given 'Try Sample' according to the passed 'assetType'
 * Doesn't wait for the result of the click
 * @param assetType possible values: 'bpmn', 'dmn'
 */
exports.trySample = function (assetType) {
    cy.get(`[data-ouia-component-id='try-sample-model-${assetType}']`)
        .contains('Try Sample')
        .click();
}

/**
 * Returns body html element of the given (bpmn, dmn) diagram editor
 * Acessig Diagram Editor via this method is preferred way as the Diagram Editor is inside an iFrame
 */
exports.getDiagramEditorBody = () => {
    // get the iframe > document > body
    // and retry until the body element is not empty
    return cy
      .get('iframe[data-envelope-channel="ONLINE"]')
      .its('0.contentDocument.body').should('not.be.empty')
      // wraps "body" DOM element to allow
      // chaining more Cypress commands, like ".find(...)"
      // https://on.cypress.io/wrap
      .then(cy.wrap)
  }