var cyOnlineEditorUtils = require('../../utils/general')

context('Check sample models successfully opened', () => {

  beforeEach(() => {
    // visit home page of the Online Editor
    cy.visit('/')
  })

  it('Try BPMN sample', () => {
    cyOnlineEditorUtils.trySample('bpmn')

    cyOnlineEditorUtils.getDiagramEditorBody()
      .find('[data-ouia-component-id$="ProjectDiagramExplorerScreen"] button')
      .click();
    cyOnlineEditorUtils.getDiagramEditorBody()
      .find('[data-ouia-component-id="diagram-tree-explorer"]')
      .find('[data-ouia-component-id$="tree-item-Process travelers"]')
  })

  it('Try DMN sample', () => {
    cyOnlineEditorUtils.trySample('dmn')

    cyOnlineEditorUtils.getDiagramEditorBody()
      .find('button[data-ouia-component-id$="dmn.decision.navigator"]')
      .click();
    cyOnlineEditorUtils.getDiagramEditorBody()
      .find('[data-ouia-component-id$="qe-dmn-graph-navigator-node-loan_pre_qualification"]')

  })
})