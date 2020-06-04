var cyOnlineEditorUtils = require('../../utils/general')

context('Check sample models successfully created', () => {

  beforeEach(() => {
    // visit home page of the Online Editor
    cy.visit('/')
  })

  it('Try BPMN sample', () => {
    cyOnlineEditorUtils.trySample('bpmn')

    cyOnlineEditorUtils.getDiagramEditorBody().find('[data-title="Explore Diagram"]').click();
    cyOnlineEditorUtils.getDiagramEditorBody()
      .find('[data-field="explorerPanelBody"]')
      .contains("Process travelers")
  })

  it('Try DMN sample', () => {
    cyOnlineEditorUtils.trySample('dmn')

    cyOnlineEditorUtils.getDiagramEditorBody().find('.fa-chevron-right').click();
    cyOnlineEditorUtils.getDiagramEditorBody()
      .find('li[data-i18n-prefix="DecisionNavigatorTreeView."]')
      .should("have.attr", "title", "loan_pre_qualification");

  })
})